// convert epochtime to JavaScripte Date object
function epochToJsDate(epochTime){
  return new Date(epochTime*1000);
}

// convert time to human-readable format YYYY/MM/DD HH:MM:SS
function epochToDateTime(epochTime){
  var epochDate = new Date(epochToJsDate(epochTime));
  var dateTime = epochDate.getFullYear() + "/" +
    ("00" + (epochDate.getMonth() + 1)).slice(-2) + "/" +
    ("00" + epochDate.getDate()).slice(-2) + " " +
    ("00" + epochDate.getHours()).slice(-2) + ":" +
    ("00" + epochDate.getMinutes()).slice(-2) + ":" +
    ("00" + epochDate.getSeconds()).slice(-2);

  return dateTime;
}

// function to plot values on charts
function plotValues(chart, timestamp, value){
  var x = epochToJsDate(timestamp).getTime();
  var y = Number (value);
  if(chart.series[0].data.length > 40) {
    chart.series[0].addPoint([x, y], true, true, true);
  } else {
    chart.series[0].addPoint([x, y], true, false, true);
  }
}

// DOM elements
const loginElement = document.querySelector('#login-form');
const registerElement = document.querySelector('#register-form');
const contentElement = document.querySelector("#content-sign-in");
const userDetailsElement = document.querySelector('#user-details');
const authBarElement = document.querySelector('#authentication-bar');
const registerButton = document.querySelector('#sign-up-btn');
const signupButton = document.querySelector('#sign-in-btn');
const deleteButtonElement = document.getElementById('delete-button');
const deleteModalElement = document.getElementById('delete-modal');
const deleteDataFormElement = document.querySelector('#delete-data-form');
const viewDataButtonElement = document.getElementById('view-data-button');
const hideDataButtonElement = document.getElementById('hide-data-button');
const tableContainerElement = document.querySelector('#table-container');
const chartsRangeInputElement = document.getElementById('charts-range');
const loadDataButtonElement = document.getElementById('load-data');
const cardsCheckboxElement = document.querySelector('input[name=cards-checkbox]');
const gaugesCheckboxElement = document.querySelector('input[name=gauges-checkbox]');
const chartsCheckboxElement = document.querySelector('input[name=charts-checkbox]');

// DOM elements for sensor readings
const cardsReadingsElement = document.querySelector("#cards-div");
const gaugesReadingsElement = document.querySelector("#gauges-div");
const chartsDivElement = document.querySelector('#charts-div');
const tempElement = document.getElementById("temp");
const humElement = document.getElementById("hum");
const speedElement = document.getElementById("speed");
const updateElement = document.getElementById("lastUpdate")

// MANAGE LOGIN/LOGOUT UI
const setupUI = (user) => {
  if (user) {
    //toggle UI elements
    loginElement.style.display = 'none';
    contentElement.style.display = 'block';
    authBarElement.style.display ='block';
    userDetailsElement.style.display ='block';
    userDetailsElement.innerHTML = user.email;

    // get user UID to get data from database
    var uid = user.uid;
    console.log(uid);

    // Database paths
    var dbPath = '/log';
    var chartPath = '/log';

    // Database references
    var dbRef = firebase.database().ref(dbPath);
    var chartRef = firebase.database().ref(chartPath);

    // CHARTS
    // Number of readings to plot on charts
    var chartRange = 0;
    // Get number of readings to plot saved on database (runs when the page first loads and whenever there's a change in the database)
    chartRef.on('value', snapshot =>{
      chartRange = Number(snapshot.val());
      console.log(chartRange);
      // Delete all data from charts to update with new values when a new range is selected
      chartT.destroy();
      chartH.destroy();
      chartS.destroy();
      // Render new charts to display new range of data
      chartT = createTemperatureChart();
      chartH = createHumidityChart();
      chartS = createSpeedChart();
      // Update the charts with the new range
      // Get the latest readings and plot them on charts (the number of plotted readings corresponds to the chartRange value)
      dbRef.orderByKey().limitToLast(chartRange).on('child_added', snapshot =>{
        var jsonData = snapshot.toJSON(); // example: {temperature: 25.02, humidity: 50.20, speed: 21.48, timestamp:1641317355}
        // Save values on variables
        var temperature = jsonData.Temperature;
        var humidity = jsonData.Humidity;
        var speed = jsonData.speed;
        var timestamp = jsonData.timestamp;
        // Plot the values on the charts
        plotValues(chartT, timestamp, temperature);
        plotValues(chartH, timestamp, humidity);
        plotValues(chartS, timestamp, speed);
      });
    });

    // Update database with new range (input field)
    chartsRangeInputElement.onchange = () =>{
      chartRef.set(chartsRangeInputElement.value);
    };


    // CARDS
    // Get the latest readings and display on cards
    dbRef.orderByKey().limitToLast(1).on('child_added', snapshot =>{
      var jsonData = snapshot.toJSON(); // example: {temperature: 25.02, humidity: 50.20, pressure: 1008.48, timestamp:1641317355}
      var temperature = jsonData.Temperature;
      var humidity = jsonData.Humidity;
      var speed = jsonData.speed;
      var timestamp = jsonData.timestamp;
      // Update DOM elements
      tempElement.innerHTML = temperature;
      humElement.innerHTML = humidity;
      speedElement.innerHTML = speed;
      updateElement.innerHTML = epochToDateTime(timestamp);
    });

    // GAUGES
    // Get the latest readings and display on gauges
    dbRef.limitToLast(1).on('child_added', snapshot =>{
      var jsonData = snapshot.toJSON(); // example: {temperature: 25.02, humidity: 50.20, speed: 18.48, timestamp:1641317355}
      var temperature = jsonData.Temperature;
      var humidity = jsonData.Humidity;
      var speed = jsonData.speed;
      var timestamp = jsonData.timestamp;
      // Update DOM elements
      var gaugeT = createTemperatureGauge();
      var gaugeH = createHumidityGauge();
      var gaugeS = createSpeedGauge();
      gaugeT.draw();
      gaugeH.draw();
      gaugeS.draw();
      gaugeT.value = temperature;
      gaugeH.value = humidity;
      gaugeS.value = speed;
      updateElement.innerHTML = epochToDateTime(timestamp);
    });

    // TABLE
    var lastReadingTimestamp; //saves last timestamp displayed on the table
    // Function that creates the table with the first 100 readings
    function createTable(){
      // append all data to the table
      var firstRun = true;
      dbRef.orderByKey().limitToLast(100).on('child_added', function(snapshot) {
        if (snapshot.exists()) {
          var jsonData = snapshot.toJSON();
          console.log(jsonData);
          var timestamp = jsonData.timestamp;
          var temperature = jsonData.Temperature;
          var humidity = jsonData.Humidity;
          var speed = jsonData.speed;          
          var content = '';
          content += '<tr>';
          content += '<td>' + epochToDateTime(timestamp) + '</td>';
          content += '<td>' + temperature + '</td>';
          content += '<td>' + humidity + '</td>';
          content += '<td>' + speed + '</td>';
          content += '</tr>';
          $('#tbody').prepend(content);
          // Save lastReadingTimestamp --> corresponds to the first timestamp on the returned snapshot data
          if (firstRun){
            lastReadingTimestamp = timestamp;
            firstRun=false;
            console.log(lastReadingTimestamp);
          }
        }
      });
    };

    viewDataButtonElement.addEventListener('click', (e) =>{
      // Toggle DOM elements
      tableContainerElement.style.display = 'block';
      viewDataButtonElement.style.display ='none';
      hideDataButtonElement.style.display ='inline-block';
      loadDataButtonElement.style.display = 'inline-block'
      createTable();
    });

    loadDataButtonElement.addEventListener('click', (e) => {
      appendToTable();
    });

    hideDataButtonElement.addEventListener('click', (e) => {
      tableContainerElement.style.display = 'none';
      viewDataButtonElement.style.display = 'inline-block';
      hideDataButtonElement.style.display = 'none';
    });

  // IF USER IS LOGGED OUT
 
  } else{
    // toggle UI elements
		loginElement.style.display = 'block';
		registerElement.style.display = 'none';
		authBarElement.style.display ='none';
		userDetailsElement.style.display ='none';
		contentElement.style.display = 'none';
	    
	}registerButton.addEventListener('click', (e) => {
		loginElement.style.display = 'none';
		registerElement.style.display = 'block';
		authBarElement.style.display ='none';
		userDetailsElement.style.display ='none';
		contentElement.style.display = 'none';
    });
	
	signupButton.addEventListener('click', (e) => {
		loginElement.style.display = 'block';
		registerElement.style.display = 'none';
		authBarElement.style.display ='none';
		userDetailsElement.style.display ='none';
		contentElement.style.display = 'none';
    });
}
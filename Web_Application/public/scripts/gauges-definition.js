

// Create Humidity Gauge
function createHumidityGauge(){
    var gauge = new RadialGauge({
        renderTo: 'gauge-humidity',
        width: 220,
        height: 220,
        units: "Humidity (%)",
        minValue: 0,
        maxValue: 100,
        colorValueBoxRect: "#049faa",
        colorValueBoxRectEnd: "#049faa",
        colorValueBoxBackground: "#f1fbfc",
        valueInt: 2,
        majorTicks: [
            "0",
            "20",
            "40",
            "60",
            "80",
            "100"
    
        ],
        minorTicks: 4,
        strokeTicks: true,
        highlights: [
            {
                "from": 80,
                "to": 100,
                "color": "#03C0C1"
            }
        ],
        colorPlate: "#fff",
        borderShadowWidth: 0,
        borders: false,
        needleType: "line",
        colorNeedle: "#007F80",
        colorNeedleEnd: "#007F80",
        needleWidth: 2,
        needleCircleSize: 3,
        colorNeedleCircleOuter: "#007F80",
        needleCircleOuter: true,
        needleCircleInner: false,
        animationDuration: 1500,
        animationRule: "linear"
    });
    return gauge;
}

// Create Temperature Gauge
function createTemperatureGauge() {
    var gauge = new LinearGauge({
        renderTo: 'gauge-temperature',
        width: 120,
        height: 300,
        units: "Temperature C",
        minValue: 0,
        startAngle: 90,
        ticksAngle: 180,
        maxValue: 40,
        colorValueBoxRect: "#049faa",
        colorValueBoxRectEnd: "#049faa",
        colorValueBoxBackground: "#f1fbfc",
        valueDec: 2,
        valueInt: 2,
        majorTicks: [
            "0",
            "5",
            "10",
            "15",
            "20",
            "25",
            "30",
            "35",
            "40"
        ],
        minorTicks: 4,
        strokeTicks: true,
        highlights: [
            {
                "from": 30,
                "to": 40,
                "color": "rgba(200, 50, 50, .75)"
            }
        ],
        colorPlate: "#fff",
        colorBarProgress: "#CC2936",
        colorBarProgressEnd: "#049faa",
        borderShadowWidth: 0,
        borders: false,
        needleType: "arrow",
        needleWidth: 2,
        needleCircleSize: 7,
        needleCircleOuter: true,
        needleCircleInner: false,
        animationDuration: 1500,
        animationRule: "linear",
        barWidth: 10,
    });
    return gauge;
}


// Create Speed Gauge
function createSpeedGauge(){
    var gauge = new RadialGauge({
        renderTo: 'gauge-speed',
        width: 220,
        height: 220,
        units: "Speed (kmph)",
        minValue: 0,
        maxValue: 100,
        colorValueBoxRect: "#aa1a04",
        colorValueBoxRectEnd: "#aa1a04",
        colorValueBoxBackground: "#f1fbfc",
        valueInt: 2,
        majorTicks: [
            "0",
            "20",
            "40",
            "60",
            "80",
            "100"
    
        ],
        minorTicks: 4,
        strokeTicks: true,
        highlights: [
            {
                "from": 80,
                "to": 100,
                "color": "#f50505"
            }
        ],
        colorPlate: "#fff",
        borderShadowWidth: 0,
        borders: false,
        needleType: "line",
        colorNeedle: "#f50505",
        colorNeedleEnd: "#f50505",
        needleWidth: 2,
        needleCircleSize: 3,
        colorNeedleCircleOuter: "#007F80",
        needleCircleOuter: true,
        needleCircleInner: false,
        animationDuration: 1500,
        animationRule: "linear"
    });
    return gauge;
}
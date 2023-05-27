#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>                 
#include <FirebaseESP8266.h>      
#include "DHT.h" 
#include "time.h"             
 
// Firebase connections data
#define FIREBASE_HOST "carcontroller-da16c-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "yk70eYcg4rchONc04WGbVnCt5nHaFtcRv3yiGs9o"            
#define WIFI_SSID "anon"
#define WIFI_PASSWORD "bachcha123"          
 
#define DHTPIN  D6                                          // Digital pin connected to DHT11
#define DHTTYPE DHT22                                        // Initialize dht type as DHT 11
DHT dht(DHTPIN, DHTTYPE);                                                    

int Carspeed = 0;

FirebaseData firebaseData;
FirebaseData ledData;
FirebaseJson json;
void printResult(FirebaseData &data);

//----------------------------------------------------------------------------
SoftwareSerial ss(D2, D1); // RX, TX
TinyGPSPlus gps;

String parentPath;

int timestamp;
const char* ntpServer = "pool.ntp.org";

// Function that gets current epoch time
unsigned long getTime() {
  time_t now;
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    //Serial.println("Failed to obtain time");
    return(0);
  }
  time(&now);
  return now;
}

void setup() 
{
  Serial.begin(115200);
  dht.begin();                                                  //reads dht sensor data 
  configTime(0, 0, ntpServer);
               
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
 
  Serial.println();
  Serial.print("Connected");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());                               //prints local IP address
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                 // connect to the firebase

  Serial.begin(9600);
  ss.begin(9600); 
}
 
void loop() 
{
  timestamp = getTime();
  
  float h = dht.readHumidity();                                 // Read Humidity
  float t = dht.readTemperature();                              // Read temperature
  
  if (isnan(h) || isnan(t))                                     // Checking sensor working
  {                                   
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  } 
  Serial.print("Humidity: ");  
  Serial.print(h);
  String fireHumid = String(h);// + String(" %");                   //Humidity integer to string conversion
  
  Serial.print("%  Temperature: ");  
  Serial.print(t);  
  Serial.println("°C ");
  String fireTemp = String(t);// + String(" °C");                  //Temperature integer to string conversion
  //delay(100);
 
  parentPath= "/log1/" + String(timestamp);

  Firebase.setString(firebaseData, "sensor1/Humidity", fireHumid);            //setup path to send Humidity readings
  Firebase.setString(firebaseData, "sensor1/Temperature", fireTemp);  
  
  Firebase.setString(firebaseData, parentPath + "/Humidity", fireHumid);            //setup path to send Humidity readings
  Firebase.setString(firebaseData, parentPath + "/Temperature", fireTemp);         //setup path to send Temperature readings
 delay(100);

 while (ss.available() > 0) {
    gps.encode(ss.read());
    if (gps.location.isUpdated()) {
      Serial.print("Latitude= ");
      Serial.print(gps.location.lat(), 6);
      Firebase.setFloat(firebaseData, "map/lat" , gps.location.lat());
      Serial.print(" | Longitude= ");
      Serial.print(gps.location.lng(), 6);
      Firebase.setFloat(firebaseData, "map/lon" , gps.location.lng());
      Serial.print(" | Speed in km/h = ");
      Serial.print(gps.speed.kmph());
      float Carspeed = gps.speed.kmph();
      if( Carspeed>3){
      //log data
      Firebase.setString(firebaseData, parentPath + "/speed", Carspeed);
      Firebase.setFloat(firebaseData, "map/lat" , gps.location.lat());
      Firebase.setFloat(firebaseData, "map/lon" , gps.location.lng());
      }
      
      Serial.print(" | Number os satellites in use = ");
      Serial.println(gps.satellites.value());
    }
  }
}

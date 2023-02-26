// Library 
#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <TinyGPS++.h>
//------------------------------------------------------------------------
// Firebase connections data
#define FIREBASE_HOST "carcontroller-da16c-default-rtdb.firebaseio.com"      
#define FIREBASE_AUTH "yk70eYcg4rchONc04WGbVnCt5nHaFtcRv3yiGs9o"            
#define WIFI_SSID "ReddragM"                                  
#define WIFI_PASSWORD "Reddrag&1986" 


 
//--------------------------------FirebaseESP8266------------------------------------------
FirebaseData firebaseData;
FirebaseJson json;

//----------------------------------------------------------------------------
//GPS Module RX pin to NodeMCU D1
//GPS Module TX pin to NodeMCU D2
//const int RXPin = 3, TXPin = 1;
SoftwareSerial ss(D1, D2); // RX, TX
TinyGPSPlus gps;
//----------------------------------------------------------------------------

void setup()  {  
  wifiConnect();  

  Serial.begin(9600);
  Serial.println("Connecting Firebase.....");
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  firebaseReconnect();
  Serial.println("Firebase OK."); 

  ss.begin(9600);
}

void loop(){
  
  // This sketch displays information every time a new sentence is correctly encoded.
  while (ss.available() > 0) {
    gps.encode(ss.read());
    if (gps.location.isUpdated()) {
      Serial.print("Latitude= ");
      Serial.print(gps.location.lat(), 6);
      Firebase.pushFloat(firebaseData, "Latitude -" , gps.location.lat());
      Serial.print(" | Longitude= ");
      Serial.print(gps.location.lng(), 6);
      Firebase.pushFloat(firebaseData, "Longitude -" , gps.location.lng());
      Serial.print(" | Speed in km/h = ");
      Serial.print(gps.speed.kmph());
      float Busspeed = gps.speed.kmph();
      if( Busspeed>3){
      Firebase.pushFloat(firebaseData, "Speed - " , Busspeed);
      }
      
      Serial.print(" | Number os satellites in use = ");
      Serial.println(gps.satellites.value());
    }
  }
}

void wifiConnect()
{
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
}
//-------------------------------------------------------------------------------

void firebaseReconnect()
{
  Serial.println("Trying to reconnect");
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 32 // OLED display height, in pixels

// Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
#define OLED_RESET     4 // Reset pin # (or -1 if sharing Arduino reset pin)
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

boolean connected = false;

void setup() {
  // initialize serial:
  Serial.begin(9600);
  // make the pins outputs:
  // initialize digital pin LED_BUILTIN as an output.
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);   // turn the LED on (HIGH is the voltage level)

  initDisplay();


}

void connectDisplay(void) {
  // SSD1306_SWITCHCAPVCC = generate display voltage from 3.3V internally
  if (!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) { // Address 0x3C for 128x32
    Serial.println(F("SSD1306 allocation failed"));
    for (;;); // Don't proceed, loop forever
  }

}

void initDisplayText(void) {
  display.setTextSize(2); // Draw 2X-scale text
  display.setTextColor(WHITE);
  display.setCursor(0, 0);
}

void initDisplay(void) {
  connectDisplay();
  initDisplayText();
  displayStartupText();
}

void displayStartupText(void) {
  display.display();
  display.clearDisplay();
  delay(200);
  display.clearDisplay();
  drawText("AWAITING  CONNECTION");
}



void drawText(String displayText) {
  Serial.println(displayText);
  initDisplayText();
  display.clearDisplay();
  display.println(displayText);
  display.display();
  delay(16);

}

String receiveData() {
  char receivedCharacters[32];
  int receivedCharacterIndex = 0;



  while (Serial.available() > 0) {
    char receivedCharacter = Serial.read();
    if (receivedCharacter == '\n') {
      receivedCharacters[receivedCharacterIndex] = '\0'; // We need to null terminate this String
      return String(receivedCharacters);
    }
    receivedCharacters[receivedCharacterIndex] = receivedCharacter;
    receivedCharacterIndex++;

  }

  return String("-1");
}

String outputTemperatureText(String temperatures) {
  String displayText = "CPU: " + temperatures.substring(0, 2) + "\nGPU: " + temperatures.substring(2);

  return displayText;
}

void loop() {

  // if there's any serial available, read it:
  String displayMessage = receiveData();

  if (!connected) {
    Serial.print("~");

  }

  if (!String("-1").equalsIgnoreCase(displayMessage)) {
    if (!connected) {
      connected = true;
    }

    Serial.println(displayMessage);

    drawText(outputTemperatureText(displayMessage));

    digitalWrite(LED_BUILTIN, HIGH);   // turn the LED on (HIGH is the voltage level)
    delay(200);
  }
  digitalWrite(LED_BUILTIN, LOW);   // turn the LED off (LOW is the voltage level)
  delay(1);

}
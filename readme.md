# Approach

I used Arduino C and Java with the jSerialComm library and the JSensors library. I have a Java application that will look for any USB Serial devices and then attempt to open a connection with them. From there it will send the temperatures of the CPU and the GPU to the Arduino. The Arduino will then read in the received data, parse it out and then have it displayed on the OLED screen. I have the process run at 5hz to ensure that it doesn't cause any hiccups in performence on the PC side when playing high intensity games or running high intensity tasks, such as Houdini. 

# Pitfalls & Challenges

Suprisingly Windows doesn't have a standardized way to get the CPU and GPU temperature so I had to search for a library that would work on my PC. I settled on JSensors as it was very straightforward, efficient and it didn't rely on any other external programs. I also currently only have the process set up to grab the first USB Serial Device that is found connected to the PC and to send data there. Ideally I would set up a handshake process where the Arduino tells the PC "Hey, it's me!" and the PC can then confirm that the device is in fact the Arduino before sending any data. 

I also rely on the CPU and GPU temperatures only being 2 digits. This can cause issues for anyone looking to track exotic cooling such as LN2 where it reached negative temperatures or anyone who is pushing the upper bounds and going to 100+ degrees for whatever crazy reason.


# How to Use

Go ahead and clone my [repository](https://github.com/CodedChai/TrinketM0DisplayTemps). You will have to run the Java code as an administrator otherwise you will not receive access to the CPU temperatures. In the folder named "Arduino Code" you will find the code that will be running on the Arduino. Open this up in Arduino IDE and upload it to the Trinket. 

# What Was Used

* [Adafruit Trinket M0](https://www.adafruit.com/product/3500)
* [SSD1306 OLED Screen](https://www.amazon.com/gp/product/B079BN2J8V/ref=ppx_yo_dt_b_asin_title_o09_s00?ie=UTF8&psc=1)
* [Small Breadboard](https://www.amazon.com/Qunqi-point-Experiment-Breadboard-5-5%C3%978-2%C3%970-85cm/dp/B0135IQ0ZC/ref=sr_1_4?keywords=small+breadboard&qid=1575236114&s=electronics&sr=1-4)
* Some random wires

# Images

## Awaiting Connection

![alt text](https://i.imgur.com/ldf5Kw5.jpg "Awaiting Connection")

## Displaying Temperatures 

![alt text](https://i.imgur.com/modyGoa.jpg "Displaying Temperatures")
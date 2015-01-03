/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 richardhmm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
#define W5100 1
#if W5100
#include <Ethernet.h>
#include <SPI.h>
#include <yl_data_point.h>
#include <yl_device.h>
#include <yl_w5100_client.h>
#include <yl_messenger.h>
#include <yl_sensor.h>
#include <yl_value_data_point.h>
#include <yl_sensor.h>
#endif
#include <DSM501.h>

#define DSM501_PM10 3
#define DSM501_PM25 4
#define THERM_PIN A5

#if W5100
yl_device ardu(16888);  // yeelink device id
yl_sensor therm(29239, &ardu); //  yeelink sensor id  lm35
yl_sensor pm25(29511, &ardu); //  yeelink sensor id  DSM501A

//replace first param value with ur u-apikey
yl_w5100_client client;
yl_messenger messenger(&client, "xxx", "api.yeelink.net");   // API KEY
#endif

DSM501 dsm501(DSM501_PM10, DSM501_PM25);

float lm35_convertor(int analog_num)
{
  return analog_num * (5.0 / 1023.0 * 100);
}

void setup()
{
  Serial.begin(9600);	//for output information

#if W5100
  byte mac[] = {
    0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xAA                                };
  Ethernet.begin(mac);
#endif

  // Initialize DSM501
  dsm501.begin();
}

void loop()
{
//  int v = analogRead(THERM_PIN);
#if w5100
//  yl_value_data_point dp1(lm35_convertor(v));
//  therm.single_post(messenger, dp1);
#endif
//  Serial.println(lm35_convertor(v));

  // call dsm501 to handle updates.
  dsm501.update();

  float pm = dsm501.getPM25();
  pm = pm > 30000.0 ? 30000.0 : pm;
#if W5100
  yl_value_data_point dp6(pm);
  pm25.single_post(messenger, dp6);
#endif
  Serial.println(pm);
}















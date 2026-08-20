// ICustomService.aidl
package com.example.hookgps;

// Declare any non-default types here with import statements
import com.example.hookgps.model.LatLng;

interface ICustomService {
 void setLatng(in LatLng par);
 LatLng getLatng();
}
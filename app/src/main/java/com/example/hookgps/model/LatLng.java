package com.example.hookgps.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LatLng implements Parcelable {
    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected LatLng(Parcel var1) {
        this.latitude = var1.readDouble();
        this.longitude = var1.readDouble();
    }

    public static final Creator<LatLng> CREATOR = new Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel in) {
            return new LatLng(in);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };

    @NonNull
    public String toString() {
        return latitude + "|" + longitude;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeDouble(this.latitude);
        var1.writeDouble(this.longitude);
    }
}

package com.android.pribo.vice.sumsum.Modules;

import android.os.Parcel;
import android.os.Parcelable;

public class Gate  implements Parcelable{

    private double lat;
    private double lang;
    private float distance;
    private String phone;
    private String name;
    private String showName;

    public Gate() {
    }

    public Gate(Parcel source) {
    }


    public Gate(double lat, double lang, int distance, String phone, String name , String showName) {
        this.lat = lat;
        this.lang = lang;
        this.distance = distance;
        this.phone = phone;
        this.name = name;
        this.showName = showName;
    }

    //getters,setters and toString...


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "lat=" + lat +
                ", lang=" + lang +
                ", distance=" + distance +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel in, int i) {
        this.name = in.readString();
        this.phone = in.readString();
        this.showName = in.readString();
        this.distance = in.readInt();
        this.lang = in.readDouble();
        this.lat = in.readDouble();
    }

    public static final Parcelable.Creator<Gate> CREATOR = new Parcelable.Creator<Gate>() {
        @Override
        public Gate createFromParcel(Parcel source) {
            return new Gate(source);
        }

        @Override
        public Gate[] newArray(int size) {
            return new Gate[size];
        }
    };

}

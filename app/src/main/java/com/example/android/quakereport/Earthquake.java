package com.example.android.quakereport;

public class Earthquake {
    private double mGuc;
    private String mLocation;
    private long mData;
    private  String murl;

    public Earthquake(double guc,String location,long data,String url){
        mData = data;
        mGuc =guc;
        mLocation = location;
        murl=url;
    }
    public double getguc (){
        return mGuc;
    }
    public String getLocation(){
        return mLocation;
    }
    public long getDate(){return mData;}
    public  String getUrl(){return murl;}
}

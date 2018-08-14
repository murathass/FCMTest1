package com.biotek.mobil.fcmtest1;

public class Helper {

    public static String encodeToken(String s){
        return s.replace(".","&0001;");
    }


    public static String decodeToken(String s){
        return  s.replace("&0001",".");
    }
}

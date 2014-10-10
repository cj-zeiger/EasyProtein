package com.zygr.easyprotein.app;

import java.io.Serializable;
import java.util.Date;

public class FoodEntry implements Serializable, Comparable<FoodEntry>{
    private final double mCalorie;
    private final double mProtein;
    private Date mTimeCreated;

    public FoodEntry(double calorie, double protein, Date date){
        mCalorie = calorie;
        mProtein = protein;
        mTimeCreated = date;
    }
    public double getCalorie(){
        return mCalorie;
    }
    public double getProtein(){
        return mProtein;
    }
    public Date getTimeCreated(){ return mTimeCreated;}

    @Override
    public int compareTo(FoodEntry another) {
        return another.getTimeCreated().compareTo(mTimeCreated);
    }
}

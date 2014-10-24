package com.zygr.easyprotein.app;

import java.io.Serializable;
import java.util.Date;

public class FoodEntry implements Serializable{
    private final int mCalorie;
    private final int mProtein;
    private Date mTimeCreated;

    public FoodEntry(int calorie, int protein, Date date){
        mCalorie = calorie;
        mProtein = protein;
        mTimeCreated = date;
    }
    public int getCalorie(){
        return mCalorie;
    }
    public int getProtein(){
        return mProtein;
    }
    public Date getTimeCreated(){ return mTimeCreated;}
}

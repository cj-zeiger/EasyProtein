package com.zygr.easyprotein.app;

public class FoodEntry {
    private final double mCalorie;
    private final double mProtein;
    protected FoodEntry(){
        mCalorie = 0.0;
        mProtein = 0.0;
    }
    public FoodEntry(double calorie, double protein){
        mCalorie = calorie;
        mProtein = protein;
    }
    public double getCalorie(){
        return mCalorie;
    }
    public double getProtein(){
        return mProtein;
    }
}

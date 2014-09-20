package com.zygr.easyprotein.app;


public interface ModifyFoodEntry {
    public void addFoodEntry(double calorie, double protein);
    public void resetFoodEntry();
    public void confirmReset();
    public void dataModified();
}

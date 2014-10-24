package com.zygr.easyprotein.app;


public interface ModifyFoodEntry {
    public void addFoodEntry(int calorie, int protein);
    public void resetFoodEntry();
    public void confirmReset();
    public void dataModified();
}

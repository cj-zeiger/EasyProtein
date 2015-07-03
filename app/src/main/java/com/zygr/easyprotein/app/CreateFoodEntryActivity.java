package com.zygr.easyprotein.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zygr.easyprotein.app.R;

public class CreateFoodEntryActivity extends Activity {

    private EditText mInputCal;
    private EditText mInputPro;
    private EditText mInputName;
    private int mCal;
    private int mPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_food_entry);

        Button close_button = (Button) findViewById(R.id.button_close_dialog);
        Button add_button = (Button) findViewById(R.id.button_save_dialog);

        mInputCal = (EditText) findViewById(R.id.editText_calorie);
        mInputPro = (EditText) findViewById(R.id.editText_protein);
        mInputName = (EditText) findViewById(R.id.editText_name);



        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_food_entry, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //PRIVATE METHODS

    /** Validates inputs and also sets member fields mCal and mPro to inputted values.
     * @return true if all inputs have valid values, signifies that a valid FoodEntry can be made
     */
    private boolean validInput(){
        try{
            String sCal = mInputCal.getText().toString();
            String sPro = mInputPro.getText().toString();
            int cal, pro;
            if(sCal.isEmpty()){
                cal = 0;
            } else {
                double tmp = Double.parseDouble(mInputCal.getText().toString());
                cal = (int)Math.round(tmp);
            }
            if(sPro.isEmpty()){
                pro = 0;
            } else {
                double tmp = Double.parseDouble(mInputPro.getText().toString());
                pro = (int)Math.round(tmp);
            }
            if(cal==pro&&cal==0){
                return false;
            }
            mCal = cal;
            mPro = pro;
            return true;
        } catch(NumberFormatException nfe){
            return false;
        }
    }
}

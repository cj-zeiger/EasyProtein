package com.zygr.easyprotein.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.LinkedList;


public class HomeActivity extends ListActivity {

    private final String _saved_state_calories = "calories";
    private final String _save_state_protein = "protein";

    private LinkedList<FoodEntry> mHistoryData;
    private final String filename = "easyprotein.dat";

    //View References for the input part of the layout
    public EditText mInputCal;
    public EditText mInputPro;
    private int mCal;
    private int mPro;
    private TextView mTextCal;
    private TextView mTextPro;

    //View References for the list part of the layout
    View mFooterView;
    HistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);
        mHistoryData = new LinkedList<FoodEntry>();
        try {
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream objectIn = new ObjectInputStream(fis);
            Object object = objectIn.readObject();
            if(!(object instanceof LinkedList)){
                throw new Exception();
            }
            mHistoryData = (LinkedList) object;
            objectIn.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        //Set References to views in the input area of the layout
        mInputCal = (EditText)findViewById(R.id.editText_calorie);
        mInputPro = (EditText)findViewById(R.id.editText_protein);
        mTextCal = (TextView)findViewById(R.id.text_calvar);
        mTextPro = (TextView)findViewById(R.id.text_provar);
        mFooterView =((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listfooter_custom, null, false);

        getListView().addFooterView(mFooterView,null,false);
        mHistoryAdapter = new HistoryAdapter(this,mHistoryData);
        setListAdapter(mHistoryAdapter);

        if(savedInstanceState != null){
            mInputCal.setText(savedInstanceState.getInt(_saved_state_calories)+"");
            mInputPro.setText(savedInstanceState.getInt(_save_state_protein)+"");
        }


    }
    @Override
    protected void onResume(){
        super.onResume();
        refreshCounterDisplay();
    }
    @Override
    protected void onStop(){
        super.onStop();
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objStream = new ObjectOutputStream(fos);
            objStream.writeObject(mHistoryData);
            objStream.close();
        } catch (Exception e){
            Toast.makeText(this, "Issue Saving Data", Toast.LENGTH_SHORT).show();
            //exception
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /**
        if(validInput()){
            outState.putInt(_save_state_protein,mPro);
            outState.putInt(_saved_state_calories,mCal);
        }
         **/

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        } else if(id == R.id.action_reset){
            resetButton();
            return true;
        } else if(id == R.id.action_add_new){
            Intent start_add_dialog = new Intent(this, CreateFoodEntryActivity.class);
            startActivity(start_add_dialog);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_activity, menu);
        return true;
    }

    public static class ResetDataDialog extends DialogFragment {
        HomeActivity act;
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
                act = (HomeActivity) getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to erase your entries?")
                    .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            act.confirmReset();
                        }
                    })
                    .setNegativeButton("No",null);
            return builder.create();
        }
    }
    public class HistoryAdapter extends BaseAdapter {
        Activity mActivity;
        private LinkedList<FoodEntry> mItems;


        public HistoryAdapter(Activity c, LinkedList<FoodEntry> items){
            mActivity = c;
            mItems = items;
        }
        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public void notifyDataSetChanged(){
            super.notifyDataSetChanged();

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView != null){
                v = convertView;
            } else {
                v = mActivity.getLayoutInflater().inflate(R.layout.listitem_historyitem,parent,false);
            }

            TextView calView = (TextView)v.findViewById(R.id.item_calorie);
            TextView proView = (TextView)v.findViewById(R.id.item_protein);
            ImageButton delButton = (ImageButton) v.findViewById(R.id.button_delete);

            final FoodEntry entry = mItems.get(position);
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entry != null){
                        mItems.remove(entry);
                    }
                    refreshCounterDisplay();
                    notifyDataSetChanged();
                }
            });
            calView.setText("" + mItems.get(position).getCalorie());
            proView.setText(mItems.get(position).getProtein()+"g");
            return v;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }
    }

    //Private Aux Methods
    private void addButton(){
        if(validInput()){
            addFoodEntry(mCal, mPro);
        } else {
            Toast.makeText(this,"Invalid amounts",Toast.LENGTH_SHORT).show();
        }
    }
    private void resetButton(){
        ResetDataDialog dialog = new ResetDataDialog();
        dialog.show(getFragmentManager(), null);
    }
    private void addFoodEntry(int calorie, int protein) {
        mHistoryData.push(new FoodEntry(calorie, protein, new Date()));
        refreshCounterDisplay();
        notifyAdapter();
        mInputCal.setText("");
        mInputPro.setText("");
    }
    private void confirmReset() {
        mHistoryData.clear();
        notifyAdapter();
        updateDisplay(0,0);
    }
    private void refreshCounterDisplay() {
        int calorie=0;
        int protein=0;
        for(FoodEntry entry: mHistoryData){
            calorie+=entry.getCalorie();
            protein+=entry.getProtein();
        }
        updateDisplay(calorie,protein);
    }
    public void updateDisplay(int calorie, int protein){
        mTextCal.setText(""+calorie);
        mTextPro.setText(protein+"g");
    }
    /**
     * Sets mCal and mPro to correct value if they are valid inputs
     * @return true if two valid inputs are found
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
    private void notifyAdapter(){
        mHistoryAdapter.notifyDataSetChanged();
    }


}

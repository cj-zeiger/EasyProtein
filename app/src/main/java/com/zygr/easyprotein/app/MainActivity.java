package com.zygr.easyprotein.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ListFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity implements ModifyFoodEntry {

    private HistoryFragment mHistoryFragment;
    private InputFragment mInputFragment;

    private ArrayList<FoodEntry> mHistoryData;

    private final String SAVE_FILE = "input_history";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistoryFragment = (HistoryFragment) getFragmentManager().findFragmentById(R.id.list);
        mInputFragment = (InputFragment) getFragmentManager().findFragmentById(R.id.input);
        mHistoryData = new ArrayList<FoodEntry>();
        mHistoryFragment.setData(mHistoryData);
    }

    @Override
    protected void onPause() {
        super.onPause();

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void resetFoodEntry() {
        ResetDataDialog dialog = new ResetDataDialog();
        dialog.show(getFragmentManager(),null);
    }
    @Override
    public void confirmReset() {
        mHistoryFragment.resetData();
        mHistoryData.clear();
        mInputFragment.updateDisplay(0.0,0.0);
    }
    @Override
    public void addFoodEntry(double newCalorie, double newProtein) {
        mHistoryData.add(new FoodEntry(newCalorie, newProtein));
        dataModified();
        mInputFragment.mInputCal.setText("");
        mInputFragment.mInputPro.setText("");
    }
    @Override
    public void dataModified(){
        double calorie=0;
        double protein=0;
        for(FoodEntry entry:mHistoryData){
            calorie+=entry.getCalorie();
            protein+= entry.getProtein();
        }

        //update calls to fragments
        mInputFragment.updateDisplay(calorie,protein);
        mHistoryFragment.updateData();
    }

    public static class ResetDataDialog extends DialogFragment{
        private ModifyFoodEntry mListener;
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mListener = (ModifyFoodEntry)getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to erase your entries?")
                    .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.confirmReset();
                        }
                    })
                    .setNegativeButton("No",null);
            return builder.create();
        }
    }
    public static class InputFragment extends Fragment {

        private Button mAddButton;
        private Button mResetButton;

        public EditText mInputCal;
        public EditText mInputPro;

        private double mCal;
        private double mPro;

        private TextView mTextCal;
        private TextView mTextPro;

        private final String SAVE_FILE = "saved_history";

        public ModifyFoodEntry mListener;

        public InputFragment() {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_input, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            mAddButton = (Button) getActivity().findViewById(R.id.button_add);
            mResetButton = (Button) getActivity().findViewById(R.id.button_reset);

            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButton();
                }
            });
            mResetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetButton();
                }
            });

            mInputCal = (EditText) getActivity().findViewById(R.id.editText_calorie);
            mInputPro = (EditText) getActivity().findViewById(R.id.editText_protein);

            mTextCal = (TextView) getActivity().findViewById(R.id.text_calvar);
            mTextPro = (TextView) getActivity().findViewById(R.id.text_provar);

            mListener = (ModifyFoodEntry) getActivity();

        }

        private void addButton(){
            if(validInput()){
                mListener.addFoodEntry(mCal,mPro);
            } else {
                Toast.makeText(getActivity(),"Invalid amounts",Toast.LENGTH_SHORT).show();
            }
        }
        private void resetButton(){
            mListener.resetFoodEntry();

        }
        public void updateDisplay(double calorie, double protein){
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
                double cal;
                double pro;
                if(sCal.isEmpty()){
                    cal = 0.0;
                } else {
                    cal = Double.parseDouble(mInputCal.getText().toString());
                }
                if(sPro.isEmpty()){
                    pro = 0.0;
                } else {
                    pro = Double.parseDouble(mInputPro.getText().toString());
                }
                if(cal==pro&&cal==0.0){
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
    public static class HistoryFragment extends ListFragment {
        View mFooterView;
        HistoryAdapter mHistoryAdapter;
        ArrayList<FoodEntry> mHistoryData;
        ModifyFoodEntry mListener;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().addFooterView(mFooterView,"footer",false);
            if(mHistoryData == null){
                throw new NullPointerException();
            }
            mListener = (ModifyFoodEntry)getActivity();
            mHistoryAdapter = new HistoryAdapter(getActivity(),mHistoryData);
            setListAdapter(mHistoryAdapter);
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                                 saved){
            View listView = inflater.inflate(R.layout.fragment_customlist, null);
            mFooterView = inflater.inflate(R.layout.listfooter_custom, null);
            return listView;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //save data
        }

        public void updateData(){
            mHistoryAdapter.notifyDataSetChanged();
        }
        public void resetData(){
            mHistoryAdapter.notifyDataSetChanged();
        }
        public void setData(ArrayList<FoodEntry> data){
            mHistoryData = data;

        }

        public class HistoryAdapter extends BaseAdapter {
            Activity mActivity;
            private ArrayList<FoodEntry> mItems;
            private final String sCalorie = "Calorie: ";
            private final String sProtein = "Protein: ";


            public HistoryAdapter(Activity c, ArrayList<FoodEntry> items){
                mActivity = c;
                mItems = items;
            }
            @Override
            public int getCount() {
                return mItems.size();
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
                Button delButton = (Button) v.findViewById(R.id.button_delete);

                final FoodEntry entry = mItems.get(position);
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entry != null){
                            for(int k =0; k<mItems.size();k++){
                                if(entry == mItems.get(k)) {
                                    mItems.remove(k);
                                }
                            }
                            mListener.dataModified();
                        }
                    }
                });
                calView.setText(sCalorie+mItems.get(position).getCalorie());
                proView.setText(sProtein+mItems.get(position).getProtein()+"g");
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

            public void setData(ArrayList<FoodEntry> data){
                mItems = data;
            }
            public ArrayList<FoodEntry> getData(){
                return mItems;
            }

            public void addEntry(FoodEntry addition){
                mItems.add(addition);
            }
        }

    }
}

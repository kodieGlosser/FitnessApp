package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by Adam on 4/6/2015.
 */
public class historyHeaderFragment extends Fragment {
    //this is the test button that shows up at the top of the list
    //code for our checkboxes or whatever is going to go here.
    private TextView mHeaderText;
    private TextView mAdditionalText;
    private LinearLayout mHeaderLayout;

    private boolean mHasAdditionalText = false;
    private boolean fireOnStart = false;

    private int mAdditionalTextPadding = 18;
    private int mDpasPix;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.h_fragment_header, null);
        mHeaderText = (TextView)v.findViewById(R.id.headerMessage);
        mHeaderLayout = (LinearLayout)v.findViewById(R.id.historyHeaderHandle);

        float scale = getResources().getDisplayMetrics().density;
        mDpasPix = (int) (mAdditionalTextPadding*scale + 0.5f);

        return v;

    }

    @Override
    public void onStart(){
        super.onStart();
        if(fireOnStart) {
            setTotalHistoryText();
            fireOnStart = false;
        }
    }



    public void tryTotalHistoryText(){
        if(isAdded()) {
            setTotalHistoryText();
        } else {
            fireOnStart = true;
        }
    }

    public void setTotalHistoryText(){
        String text = getResources().getString(R.string.TOTAL_HISTORY_STRING);

        mHeaderText.setText(text);

        if (mHasAdditionalText)
            mHeaderLayout.removeView(mAdditionalText);

        mHasAdditionalText = false;
    }

    public void setDayHistoryText(Date date){
        String text = getResources().getString(R.string.DAY_HISTORY_STRING);
        mHeaderText.setText(text);

        if(mAdditionalText == null) {
            mAdditionalText = new TextView(getActivity());
            mAdditionalText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.subHeader));
            mAdditionalText.setPadding(mDpasPix,0,0,0);
        }

        mAdditionalText.setText(date.toString());

        if(!mHasAdditionalText)
            mHeaderLayout.addView(mAdditionalText);

        mHasAdditionalText = true;
    }

    public void setExerciseHistoryText(String name){
        String text = getResources().getString(R.string.EXERCISE_HISTORY_STRING);
        mHeaderText.setText(text);

        if(mAdditionalText == null) {
            mAdditionalText = new TextView(getActivity());
            mAdditionalText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.subHeader));
            mAdditionalText.setPadding(mDpasPix,0,0,0);
        }

        mAdditionalText.setText(name);

        if(!mHasAdditionalText)
            mHeaderLayout.addView(mAdditionalText);

        mHasAdditionalText = true;
    }

}

package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
public class EditExerciseDetailsFragment extends Fragment {

    private int exerciseValue;
    private int circuitValue;
    private Exercise mExercise;
    private Circuit circuit;
    private String title;
    private boolean mBoot;

    private EditText mFirstEditTextHandle;
    private EditText mEditTextHandle;
    private LinearLayout editTextLayout;
    private LinearLayout mLayout;
    private TextView exerciseInfoTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        Log.d("4/9", "InfotextView initialized");
        View v = inflater.inflate(R.layout.e_frag_details, null);
        editTextLayout = (LinearLayout) v.findViewById(R.id.detailLinearLayout);
        exerciseInfoTextView = (TextView) v.findViewById(R.id.exerciseNameView);
        mLayout = (LinearLayout) v.findViewById(R.id.detailView);
        updateUI();
        return v;
    }

    public void focusFirstEdit(){
        Log.d("DETAIL FOCUS TESTS", "FOCUSFIRSTEDITCALLED");
        mFirstEditTextHandle.requestFocus();
        showKeypad();
    }
    public void putExercise(Exercise e){
        mExercise = e;
    }
    public void setFirstFragment(){
        mBoot = true;
    }
    public void setExerciseCompleted(){
         ((DetailActivity)getActivity()).next();
         mExercise.setSaveToHistory(true);
         hideKeypad();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if (mBoot)
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    implementSwipeListener();
                    focusFirstEdit();
                }

            }, 600);
        mBoot = false;
    }

   public void implementSwipeListener(){


        mLayout.setOnTouchListener(new View.OnTouchListener() {
            private int swipeThreshold = 150;
            private float currentY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();

                switch (action){
                    case MotionEvent.ACTION_DOWN: {
                        Log.d("DETAIL SWIPE TESTS", "ACTION_DOWN");
                        final int pointerIndex = MotionEventCompat.getActionIndex(event);
                        final float startY = MotionEventCompat.getY(event, pointerIndex);

                        hideKeypad();

                        v.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if(currentY > startY + swipeThreshold){
                                    ((DetailActivity)getActivity()).previous();
                                    //removeListener();
                                }
                                if(currentY < startY - swipeThreshold){
                                    ((DetailActivity)getActivity()).next();
                                    //removeListener();
                                }
                            }
                        }, 500);

                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        currentY = event.getY();
                        break;
                    }
                }
                return true;
            }
        });

    }

    //private void removeListener(){
        //Log.d("THING I CARE ABOUT", "LISTENER SHOULDBE REMOVED");
       // mLayout.setOnTouchListener(null);
    //}
    private void updateUI(){
        boolean hasPlanMetrics = false;

        title = mExercise.getName();

        exerciseInfoTextView.setText(title);

        editTextLayout.removeAllViewsInLayout();
        final ArrayList<Metric> metrics = mExercise.getMetrics();
        ArrayList<Metric> planMetrics = new ArrayList<>();
        if (mExercise.hasPlanMetrics()) {
            planMetrics = mExercise.getPlanMetrics();
            hasPlanMetrics = true;
        }

        for(int i = 0; i < metrics.size(); i++){
            final int j = i;
            switch(metrics.get(i).getType()){
                case TIME:
                    TextView timeText = new TextView(getActivity());
                    timeText.setText("Time: ");
                    final EditText timeEdit = new EditText(getActivity());
                    timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    timeEdit.setText("" + metrics.get(i).getMetricIntValue());

                    if (i != metrics.size()-1) {
                        timeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }else{
                        timeEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }

                    timeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {


                            } else if(actionId == EditorInfo.IME_ACTION_DONE){
                                setExerciseCompleted();
                                return true;
                            }
                            return false;
                        }
                    });

                    timeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                mEditTextHandle = (EditText) v;
                                ((EditText)v).setSelection(((EditText)v).getText().toString().length());
                                if(timeEdit.getText().toString().equals("0")){
                                    timeEdit.setText("");
                                }
                            } else {
                                if(((EditText) v).getText().toString().equals("")) {
                                    metrics.get(j).setMetricIntValue(0);
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });

                    LinearLayout timeRow = new LinearLayout(getActivity());
                    timeRow.setOrientation(LinearLayout.HORIZONTAL);
                    timeRow.addView(timeText);
                    timeRow.addView(timeEdit);

                    if(hasPlanMetrics){
                        TextView goalText = new TextView(getActivity());
                        String text = "target: " + planMetrics.get(i).getMetricIntValue();
                        goalText.setText(text);
                        timeRow.addView(goalText);
                    }

                    editTextLayout.addView(timeRow);

                    if (i==0)
                        mFirstEditTextHandle = timeEdit;

                    break;
                case REPETITIONS:
                    TextView repText = new TextView(getActivity());
                    repText.setText("Reps: ");
                    final EditText repEdit = new EditText(getActivity());
                    repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repEdit.setText("" + metrics.get(i).getMetricIntValue());
                    if (i != metrics.size()-1) {
                        repEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }else{
                        repEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }

                    repEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {


                            } else if(actionId == EditorInfo.IME_ACTION_DONE){
                                setExerciseCompleted();
                                return true;

                            }
                            return false;
                        }
                    });

                    repEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                ((EditText)v).setSelection(((EditText)v).getText().toString().length());

                                mEditTextHandle = (EditText) v;

                                if(repEdit.getText().toString().equals("0")){
                                    repEdit.setText("");
                                }

                            } else {
                                if (((EditText) v).getText().toString().equals("")) {
                                    metrics.get(j).setMetricIntValue(0);
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });
                    LinearLayout repRow = new LinearLayout(getActivity());
                    repRow.setOrientation(LinearLayout.HORIZONTAL);
                    repRow.addView(repText);
                    repRow.addView(repEdit);
                    //repRow.setLayoutParams(params);

                    if(hasPlanMetrics){
                        TextView goalText = new TextView(getActivity());
                        String text = "target: " + planMetrics.get(i).getMetricIntValue();
                        goalText.setText(text);
                        repRow.addView(goalText);
                    }

                    editTextLayout.addView(repRow);

                    if (i==0)
                        mFirstEditTextHandle = repEdit;

                    break;
                case OTHER:

                    TextView otherText = new TextView(getActivity());
                    otherText.setText(metrics.get(i).getMetricName());

                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                    final EditText editOther = new EditText(getActivity());
                    editOther.setLayoutParams(params);
                    editOther.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    editOther.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                            } else if(actionId == EditorInfo.IME_ACTION_DONE){
                                setExerciseCompleted();
                                return true;

                            }
                            return false;
                        }
                    });

                    editOther.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                mEditTextHandle = (EditText) v;
                            } else {
                                metrics.get(j).setMetricStringValue(((EditText) v).getText().toString());
                            }
                        }
                    });

                    LinearLayout otherRow = new LinearLayout(getActivity());
                    otherRow.setOrientation(LinearLayout.VERTICAL);
                    otherRow.addView(otherText);
                    otherRow.addView(editOther);
                    editTextLayout.addView(otherRow);

                    if (i==0)
                        mFirstEditTextHandle = editOther;

                    break;
                case WEIGHT:
                    TextView wtText = new TextView(getActivity());
                    wtText.setText("Weight: ");

                    final EditText wtEdit = new EditText(getActivity());
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                    if (i != metrics.size()-1) {
                        wtEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }else{
                        wtEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }

                    wtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {


                            } else if(actionId == EditorInfo.IME_ACTION_DONE){
                                setExerciseCompleted();
                                return true;

                            }
                            return false;
                        }
                    });

                    wtEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                mEditTextHandle = (EditText) v;
                                ((EditText)v).setSelection(((EditText)v).getText().toString().length());
                                Log.d("4/2 tests", "Wt edit cursor should be at"
                                        + wtEdit.getText().toString().length());
                                if(wtEdit.getText().toString().equals("0")){
                                    wtEdit.setText("");
                                }
                            } else {
                                if(((EditText) v).getText().toString().equals("")) {
                                    metrics.get(j).setMetricIntValue(0);
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }
                            }
                        }
                    });

                    LinearLayout wtRow = new LinearLayout(getActivity());
                    wtRow.setOrientation(LinearLayout.HORIZONTAL);
                    wtRow.addView(wtText);
                    wtRow.addView(wtEdit);
                    //wtRow.setLayoutParams(params);
                    if(hasPlanMetrics){
                        TextView goalText = new TextView(getActivity());
                        String text = "target: " + planMetrics.get(i).getMetricIntValue();
                        goalText.setText(text);
                        wtRow.addView(goalText);
                    }

                    editTextLayout.addView(wtRow);

                    if (i==0)
                        mFirstEditTextHandle = wtEdit;

                    break;
                default:
                    break;
            }
        }
    }

    public void hideKeypad(){
        if (mEditTextHandle != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditTextHandle.getWindowToken(), 0);
            mEditTextHandle.clearFocus();
        }
    }

    public void showKeypad(){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTextHandle, InputMethodManager.SHOW_IMPLICIT);
    }
}

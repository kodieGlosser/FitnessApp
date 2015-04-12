package com.main.toledo.gymtrackr;

import android.content.Context;
import android.os.Bundle;
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

    private EditText mEditTextHandle;
    private LinearLayout editTextLayout;
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

        implementSwipeListener(v);
        updateUI();
        return v;
    }


    public void putExercise(Exercise e){
        mExercise = e;
    }

    private void implementSwipeListener(View v){
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.detailView);

        layout.setOnTouchListener(new View.OnTouchListener() {
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

                                }
                                if(currentY < startY - swipeThreshold){
                                    ((DetailActivity)getActivity()).next();

                                }
                            }
                        }, 500);

                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        currentY = event.getY();
                        break;
                    }

                    case MotionEvent.ACTION_UP: {

                        break;
                    }

                    case MotionEvent.ACTION_CANCEL: {

                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {

                        break;
                    }
                }
                return true;
            }
        });

    }

    private void updateUI(){
        title = mExercise.getName();

        exerciseInfoTextView.setText(title);

        editTextLayout.removeAllViewsInLayout();
        final ArrayList<Metric> metrics = mExercise.getMetrics();
        for(int i = 0; i < metrics.size(); i++){
            final int j = i;
            switch(metrics.get(i).getType()){
                case TIME:
                    TextView timeText = new TextView(getActivity());
                    timeText.setText("Time: ");
                    final EditText timeEdit = new EditText(getActivity());
                    timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    timeEdit.setText("" + metrics.get(i).getMetricIntValue());
                    timeEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    timeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                                if(j == mExercise.getMetrics().size()-1){
                                    hideKeypad();
                                    return true;
                                }
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

                    editTextLayout.addView(timeRow);
                    break;
                case REPETITIONS:
                    TextView repText = new TextView(getActivity());
                    repText.setText("Reps: ");
                    final EditText repEdit = new EditText(getActivity());
                    repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repEdit.setText("" + metrics.get(i).getMetricIntValue());
                    repEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                    repEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                                if(j == mExercise.getMetrics().size()-1){
                                    hideKeypad();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    //time

                    repEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                //Log.d("WORKSPACELISTFOCUS", "EDIT FOCUSED" + repEdit.getText());
                                ((EditText)v).setSelection(((EditText)v).getText().toString().length());

                                mEditTextHandle = (EditText) v;

                                if(repEdit.getText().toString().equals("0")){
                                    repEdit.setText("");
                                }

                            } else {
                                //Log.d("WORKSPACELISTFOCUS", "EDIT LOST FOCUS" + repEdit.getText());
                                if (((EditText) v).getText().toString().equals("")) {
                                    metrics.get(j).setMetricIntValue(0);
                                } else {
                                    metrics.get(j).setMetricIntValue(Integer.parseInt(((EditText) v).getText().toString()));
                                }

                                /*
                                if (!(frameLayout.getTag() == "checked") || (frameLayout.getTag() == null)) {
                                    Log.d("CHECKED TESTS", "MAKING NEW CHECK");
                                    Workout.get(group).getExercise(child).setSaveToHistory(true);
                                    ImageView mChecked = new ImageView(_context);
                                    mChecked.setImageResource(R.drawable.grn_check);
                                    frameLayout.addView(mChecked);
                                    frameLayout.setTag("checked");
                                }
                                */

                            }
                        }
                    });
                    LinearLayout repRow = new LinearLayout(getActivity());
                    repRow.setOrientation(LinearLayout.HORIZONTAL);
                    repRow.addView(repText);
                    repRow.addView(repEdit);
                    //repRow.setLayoutParams(params);

                    editTextLayout.addView(repRow);
                    break;
                case OTHER:
                    TextView otherText = new TextView(getActivity());
                    otherText.setText(metrics.get(i).getMetricStringValue());

                    LinearLayout otherRow = new LinearLayout(getActivity());
                    otherRow.setOrientation(LinearLayout.HORIZONTAL);
                    otherRow.addView(otherText);

                    break;
                case WEIGHT:
                    TextView wtText = new TextView(getActivity());
                    wtText.setText("Weight: ");

                    final EditText wtEdit = new EditText(getActivity());
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    wtEdit.setText("" + metrics.get(i).getMetricIntValue());
                    wtEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                    wtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                                if(j == mExercise.getMetrics().size()-1){
                                    hideKeypad();
                                    return true;
                                }
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

                    editTextLayout.addView(wtRow);
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
}

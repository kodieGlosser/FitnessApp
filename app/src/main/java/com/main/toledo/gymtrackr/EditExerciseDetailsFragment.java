package com.main.toledo.gymtrackr;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Exercise exercise;
    private Circuit circuit;
    private String title;


    final int NEXT = 1, PREVIOUS = 2;

    private EditText mEditTextHandle;
    private LinearLayout editTextLayout;
    private TextView exerciseInfoTextView;
    private ArrayList<EditText> editList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        exerciseValue = ((EditActivity) getActivity()).getExercise();
        circuitValue = ((EditActivity) getActivity()).getCircuit();

        circuit = WorkoutData.get(getActivity()).getWorkout().get(circuitValue);
        exercise = circuit.getExercise(exerciseValue);

        //sets the view for the fragment
        View v = inflater.inflate(R.layout.e_frag_details, null);

        editTextLayout = (LinearLayout) v.findViewById(R.id.detailLinearLayout);
        exerciseInfoTextView = (TextView) v.findViewById(R.id.exerciseNameView);

        implementSwipeListener(v);
        updateUI();


        return v;
    }

    private void implementSwipeListener(View v){
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.detailView);

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
                                    //swipe down (NEXT)
                                    previous();

                                    Log.d("DETAIL SWIPE TESTS", "NEXT");
                                }
                                if(currentY < startY - swipeThreshold){
                                    next();
                                    Log.d("DETAIL SWIPE TESTS", "PREVIOUS");
                                }
                            }
                        }, 500);

                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {

                        currentY = event.getY();
                        Log.d("DETAIL SWIPE TESTS", "ACTION_MOVE" + currentY);
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

    private void next(){
        boolean updateUI = false;
        int numCircuitsInWorkout =
                WorkoutData.get(getActivity()).getWorkout().size();

        if(circuit.isOpen()){
            int circuitSize = circuit.getSize();

            if(exerciseValue == circuitSize - 2){ //if last exercise in open circuit

                if (circuitValue == numCircuitsInWorkout - 2){ //if last circuit
                    //last item in last circuit handled here
                    updateUI = false;
                }else{  //not last circuit
                    //goto next circuit
                    //goto first exercise
                    circuitValue++;
                    exerciseValue = 0;
                    updateUI = true;
                }

            }else{ //not last exercise in open circuit
                //
                exerciseValue++;
                updateUI = true;
            }

        }else{ //closed circuit
            if(circuitValue == numCircuitsInWorkout - 2){ //last circuit
                updateUI = false;
            } else { //not last circuit
                circuitValue++;
                exerciseValue = 0;
                updateUI = true;
            }
        }
        circuit = WorkoutData.get(getActivity()).getWorkout().get(circuitValue);
        exercise = circuit.getExercise(exerciseValue);
        //if(updateUI)
            //transition(NEXT);
        hideKeypad();
        //Delay a bit for keypad retract animation
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 300);
    }

    private void previous(){
        boolean transition = false;
        if(circuit.isOpen()){ //for open circuit
            if(exerciseValue == 0){ //for first exercise

                if(circuitValue == 0){ //for first circuit
                    //first item, do nothing
                    transition = false;

                } else { //for not first circuit
                    //goto last item in next circuit
                    circuitValue--;
                    Circuit prevCircuit =
                            WorkoutData.get(getActivity()).getWorkout().get(circuitValue);

                    if(prevCircuit.isOpen()){ //dest circuit is open
                        exerciseValue = prevCircuit.getSize() - 2;
                    } else { //dest circuit is closed
                        exerciseValue = 0;
                    }

                    transition = true;
                }

            } else { //for not first exercise
                exerciseValue--;
                transition = true;
            }
        }else{ //for closed circuit

            if(circuitValue == 0){//first circuit
                transition = false;
            } else { //not first circuit
                circuitValue--;
                Circuit prevCircuit =
                        WorkoutData.get(getActivity()).getWorkout().get(circuitValue);

                if(prevCircuit.isOpen()){ //dest circuit is open
                    exerciseValue = prevCircuit.getSize() - 2;
                } else { //dest circuit is closed
                    exerciseValue = 0;
                }

                transition = true;
            }
        }

        circuit = WorkoutData.get(getActivity()).getWorkout().get(circuitValue);
        exercise = circuit.getExercise(exerciseValue);

        //if(transition)
            //transition(PREVIOUS);

        hideKeypad();
        //Delay a bit for keypad retract animation
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 300);

    }

    private void transition(int action){
        final ImageView oldView;
        final ImageView newView;
        final LinearLayout layout;
        int actionBarOffset;
        int layoutYoffset = 0;
        int animationOffset = 0;

       // getActivity().getActionBar();

        View v = getActivity().findViewById(R.id.detailActivityMainView);
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);
        actionBarOffset = xy[1];

        Display d = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        int totalViewSize = size.y - actionBarOffset;
        int totalWidth = size.x;

        //make views
        oldView = ((EditActivity) getActivity()).createTransitionScreen();
        oldView.setMinimumWidth(totalWidth);

        layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = layoutYoffset;

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.format = PixelFormat.OPAQUE;
        layoutParams.windowAnimations = 0;

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(layout, layoutParams);

        //layout.bringToFront();
        layout.addView(oldView);
        layout.setMinimumWidth(totalWidth);
        updateUI();

        newView = ((EditActivity) getActivity()).createTransitionScreen();
        newView.setMinimumWidth(totalWidth);

        switch(action){
            case PREVIOUS:
                //swipe down so
                layout.addView(newView, 0);
                layout.setY(actionBarOffset - totalViewSize);
                //totalViewSize = ac
                break;
            case NEXT:
                //swipe up so
                layout.addView(newView);
                totalViewSize = totalViewSize * -1;
                break;
        }

        Log.d("DETAIL ANIMATION TESTS", "NEW VIEW HEIGHT: " + totalViewSize + " -- TOTAL SCREEN SIZE: " + size.y + " -- ACTION BAR OFFSET: " + actionBarOffset + " -- TOTAL WIDTH: " + totalWidth);

        ObjectAnimator oldSlidInAnimator = android.animation.ObjectAnimator.ofFloat(layout, "translationY", totalViewSize);
        oldSlidInAnimator.setDuration(1500);
        oldSlidInAnimator.start();



        v.postDelayed(new Runnable() {

            @Override
            public void run() {

                layout.setVisibility(View.GONE);
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                wm.removeView(layout);

            }
        }, 1500);
    }

    private void updateUI(){

        ((EditActivity) getActivity()).setHistoryAdapter(exercise.getName());

        if (circuit.isOpen())
            title = exercise.getName() + " in " + circuit.getName() + " -- DEBUG -- CIRCUIT: " + circuitValue + " EXERCISE: " + exerciseValue;
        else
            title = exercise.getName();


        exerciseInfoTextView.setText(title);

        editTextLayout.removeAllViewsInLayout();
        final ArrayList<Metric> metrics = exercise.getMetrics();
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

                                if(j == exercise.getMetrics().size()-1){
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

                                if(j == exercise.getMetrics().size()-1){
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

                                if(j == exercise.getMetrics().size()-1){
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
    /*
    private void set(){
        for(int i = 0; i < exercise.getMetrics().size(); i++){
            if (editList.get(i) != null) {
                int metricValue = Integer.parseInt(editList.get(i).getText().toString());
                exercise.getMetrics().get(i).setMetricIntValue(metricValue);
                Log.d("EDIT TEST", "METRIC: " + exercise.getMetrics().get(i).getType()
                        + " --- VALUE: " + metricValue);
            }
        }
    }
    */

}

package com.main.toledo.gymtrackr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MotionEventCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
    private Exercise exercise;
    private Circuit circuit;
    private String title;

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

                        v.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                if(currentY > startY + swipeThreshold){
                                    //swipe down (NEXT)
                                    next();
                                    Log.d("DETAIL SWIPE TESTS", "NEXT");
                                }
                                if(currentY < startY - swipeThreshold){
                                    previous();
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
        if(updateUI)
            updateUI();
    }

    private void previous(){
        boolean updateUI = false;
        if(circuit.isOpen()){ //for open circuit
            if(exerciseValue == 0){ //for first exercise

                if(circuitValue == 0){ //for first circuit
                    //first item, do nothing
                    updateUI = false;

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

                    updateUI = true;
                }

            } else { //for not first exercise
                exerciseValue--;
                updateUI = true;
            }
        }else{ //for closed circuit

            if(circuitValue == 0){//first circuit
                updateUI = false;
            } else { //not first circuit
                circuitValue--;
                Circuit prevCircuit =
                        WorkoutData.get(getActivity()).getWorkout().get(circuitValue);

                if(prevCircuit.isOpen()){ //dest circuit is open
                    exerciseValue = prevCircuit.getSize() - 2;
                } else { //dest circuit is closed
                    exerciseValue = 0;
                }

                updateUI = true;
            }
        }

        circuit = WorkoutData.get(getActivity()).getWorkout().get(circuitValue);
        exercise = circuit.getExercise(exerciseValue);
        if(updateUI)
            updateUI();
    }

    private void updateUI(){

        if (circuit.isOpen())
            title = exercise.getName() + " in " + circuit.getName() + " -- DEBUG -- CIRCUIT: " + circuitValue + " EXERCISE: " + exerciseValue;
        else
            title = exercise.getName();


        exerciseInfoTextView.setText(title);

        editTextLayout.removeAllViewsInLayout();

        for(int i = 0; i < exercise.getMetrics().size(); i++){
            switch(exercise.getMetrics().get(i).getType()){
                case TIME:
                    TextView timeText = new TextView(getActivity());
                    timeText.setText("Time: ");

                    EditText timeEdit = new EditText(getActivity());
                    timeEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    timeEdit.setText("" + exercise.getMetrics().get(i).getMetricIntValue());
                    timeEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    LinearLayout timeRow = new LinearLayout(getActivity());
                    timeRow.setOrientation(LinearLayout.HORIZONTAL);
                    timeRow.addView(timeText);
                    timeRow.addView(timeEdit);

                    editTextLayout.addView(timeRow);
                    /*
                    LinearLayout timeLayout = (LinearLayout) v.findViewById(R.id.detailTimeViewLayout);
                    timeLayout.setVisibility(View.VISIBLE);
                    TextView timeText = (TextView) v.findViewById(R.id.detailTimeView);
                    timeText.setText("Time: ");

                    time = (EditText)v.findViewById(R.id.time);

                    time.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });

                    editList.add(time);
                    */
                    break;
                case REPETITIONS:
                    TextView repText = new TextView(getActivity());
                    repText.setText("Reps: ");

                    EditText repEdit = new EditText(getActivity());
                    repEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    repEdit.setText("" + exercise.getMetrics().get(i).getMetricIntValue());
                    repEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    LinearLayout repRow = new LinearLayout(getActivity());
                    repRow.setOrientation(LinearLayout.HORIZONTAL);
                    repRow.addView(repText);
                    repRow.addView(repEdit);

                    editTextLayout.addView(repRow);
                    /*
                    LinearLayout repLayout = (LinearLayout) v.findViewById(R.id.detailRepViewLayout);
                    repLayout.setVisibility(View.VISIBLE);
                    TextView repText = (TextView) v.findViewById(R.id.detailRepView);
                    repText.setText("Reps: ");
                    reps = (EditText)v.findViewById(R.id.reps);

                    reps.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                    editList.add(reps);
                    */
                    break;
                case OTHER:
                    TextView otherText = new TextView(getActivity());
                    otherText.setText(exercise.getMetrics().get(i).getMetricStringValue());
                    //EditText otherEdit = new EditText(getActivity());

                    LinearLayout otherRow = new LinearLayout(getActivity());
                    otherRow.setOrientation(LinearLayout.HORIZONTAL);
                    otherRow.addView(otherText);
                    //otherRow.addView(otherEdit);
                    /*
                    LinearLayout otherLayout = (LinearLayout) v.findViewById(R.id.detailOtherViewLayout);
                    otherLayout.setVisibility(View.VISIBLE);
                    TextView otherText = (TextView) v.findViewById(R.id.detailOtherView);
                    otherText.setText(exercise.getMetrics().get(i).getMetricName());
                    */
                    break;
                case WEIGHT:
                    TextView wtText = new TextView(getActivity());
                    wtText.setText("Weight: ");

                    EditText wtEdit = new EditText(getActivity());
                    wtEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    Log.d("METRIC TEST", "TYPE: " + exercise.getMetrics().get(i).getType() + "-- VALUE: " + exercise.getMetrics().get(i).getMetricIntValue());
                    wtEdit.setText("" + exercise.getMetrics().get(i).getMetricIntValue());
                    wtEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                    LinearLayout wtRow = new LinearLayout(getActivity());
                    wtRow.setOrientation(LinearLayout.HORIZONTAL);
                    wtRow.addView(wtText);
                    wtRow.addView(wtEdit);

                    editTextLayout.addView(wtRow);
                    /*
                    LinearLayout weightLayout = (LinearLayout) v.findViewById(R.id.detailWeightViewLayout);
                    weightLayout.setVisibility(View.VISIBLE);
                    TextView weightText = (TextView) v.findViewById(R.id.detailWeightView);
                    weightText.setText("Weight: ");

                    weight = (EditText)v.findViewById(R.id.weight);

                    weight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            boolean handled = false;
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                set();
                                handled = true;
                            }
                            return handled;
                        }
                    });
                    editList.add(weight);
                    */
                    break;
                default:
                    Log.d("ERROR DETAIL FRAGMENT", "DEFAULT REACHED IN DETAIL FRAGMENT");
                    break;
            }
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

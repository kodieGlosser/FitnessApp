package com.main.toledo.gymtrackr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Adam on 2/9/2015.
 */
public class Exercise {

    private int m_id;

    private String m_name;
    private String m_muscleGroup; //leaving out muscle for now
    private String m_equipmentType;
    private String m_targetMuscle;
    private int m_repetitions;
    private int m_weight;
    private int m_lastPerformed;  //this will be an integer value of the last time the exercise was
                                //performed, used to organize browse menu
    private ExerciseStatus status;
    private int CircuitLocation = 0;  //this will be the number of the circuit relative to the
                                  //start of the workout a value of 'one' for exercises in the first
                                  //circuit, etc 0 for not part of a circuit
    private boolean isLast = true;
    private boolean display = true;
    //performed, used to organize browse menu
    private View m_view;
    private String m_viewType;

    public Exercise(String name, String muscleGroup, int lastPerformed, String equipmentType){
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_lastPerformed = lastPerformed;
        this.m_equipmentType = equipmentType;
        isLast = false;
    }

    public Exercise(int id, String name, String muscleGroup, String equipmentType, String targetMuscle, int repetitions, int weight){
        this.m_id = id;
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_equipmentType = equipmentType;
        this.m_targetMuscle = targetMuscle;
        this.m_repetitions = repetitions;
        this.m_weight = weight;
    }

    public Exercise(int id, String name, String muscleGroup, String equipmentType, String targetMuscle){
        this.m_id = id;
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_equipmentType = equipmentType;
        this.m_targetMuscle = targetMuscle;
    }

    public Exercise(){
        m_name = "test";
        m_muscleGroup = "test";
        m_lastPerformed = 0;
        m_equipmentType = "test";
    }

    public void setId(int id) { this.m_id = id; }

    public int getId() { return this.m_id; }

    public void setTargetMuscle(String targetMuscle) { this.m_targetMuscle = targetMuscle; }

    public String getTargetMuscle() { return this.m_targetMuscle; }

    public void setRepetitions(int repetitions) { this.m_repetitions = repetitions; }

    public int getRepetitions() { return this.m_repetitions; }

    public void setWeight(int weight) {this.m_weight = weight; }

    public int getWeight() { return this.m_weight; }

    public void setMuscleGroup(String muscleGroup) {
        this.m_muscleGroup = muscleGroup;
    }

    public String getMuscleGroup() {
        return m_muscleGroup;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setEquipment(String equipment) {
        this.m_equipmentType = equipment;
    }

    public String getEquipment(){ return m_equipmentType; }

    public void setLastPerformed(int lastPerformed) {
        this.m_lastPerformed = lastPerformed;
    }

    public int getLastPerformed() {
        return m_lastPerformed;
    }

    public void setCircuitLocation(int i){ CircuitLocation = i;}

    public int getCircuitLocation(){ return CircuitLocation; }

    public void isPlan(){ status = ExerciseStatus.PLAN; }

    public void isCompleted() { status = ExerciseStatus.COMPLETED; }

    @Override
    public String toString() { return m_name; }



    public enum ExerciseStatus {
        PLAN, COMPLETED
    }

    public View initiateView(final Context c) {
        LayoutInflater inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (m_name) {
            case "test":
                m_view = inflater.inflate(R.layout.w_end_buttons, null);
                m_viewType = "Buttons";
                break;

            default:
                m_view = inflater.inflate(R.layout.w_exercise, null);
                m_viewType = "Text";
                break;
        }

        return m_view;
    }

    public View refreshView(final Context c, final int circuit){
        Button browseButton;
        TextView textView;

        switch (m_name) {
            case "test":
                //Log.d("refreshTest ", "Placeholder called in " + order);
                if (m_view == null) {
//                    Log.d("TEST ", "m_view is null for PlaceHolderCase");
//                    Log.d("TEST ", "in exercise " + CircuitLocation);
//                    Log.d("TEST ", "LIVESAVER GOOOOO");
                    LayoutInflater inflater = (LayoutInflater) c
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    m_view = inflater.inflate(R.layout.w_end_buttons, null);
                    m_viewType = "Buttons";
                }
                Log.d("TEST", "Initializing button for circuit: " + circuit);
                browseButton = (Button)m_view.findViewById(R.id.BrowseButton);
                browseButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent i = new Intent(c, BrowseActivity.class);
//                        Log.d("Test", "Browse called from circuit: " + circuit);
                        i.putExtra("EXTRA_CIRCUIT_NUMBER", circuit);
                        c.startActivity(i);
                    }
                });

                break;

            default:
                if (m_viewType == "Buttons" || m_view == null) {
//                    Log.d("TEST ", "m_view is null for default case");
//                    Log.d("TEST ", "in exercise " + CircuitLocation);
//                    Log.d("TEST ", "LIVESAVER GOOOOO");
                    LayoutInflater inflater = (LayoutInflater) c
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    m_view = inflater.inflate(R.layout.w_exercise, null);
                    m_viewType = "Text";
                }
                Log.d("TEST ", "VIEWTYPE = " + m_viewType);
//                if (m_view == null)
//                    Log.d("TEST ", "M_VIEW IS BREAKING IT");
//                Log.d("TEST ", "VIEW TYPE IS " + viewType);
                textView = (TextView)m_view.findViewById(R.id.workspaceExerciseNameView);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(m_name);
                break;
        }

        return m_view;
    }
}



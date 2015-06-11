package com.main.toledo.gymtrackr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/9/2015.
 */
public class Circuit {
    private int m_circuit_id;

    protected ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    private String name;
    private boolean isOpen;
    private boolean isExpanded = true;
    private int id;

    //test for null pointer in workout data

    public Circuit(){
        m_circuit_id = WorkoutData.STABLE_ID;
        WorkoutData.STABLE_ID++;
        Log.d("CIRCUIT", "Circuit id: " + m_circuit_id);
        name = "Placeholder";
    }

    //called to addToOpenCircuit a special exercise value to the end
    public void add(Exercise e){
        exercises.add(e);
    }

    public void add(int index, Exercise e){ exercises.add(index, e);}

    public void addExerciseAtIndex(int i, Exercise e){
        exercises.add(i, e);
    }

    public Exercise getExercise(int i){
        return exercises.get(i);
    }

    public int getStableID(){return m_circuit_id;}

    public void removeExercise(int i) {exercises.remove(i);}

    public void setName(String s){ name = s; }

    public String getName(){return name;}

    public int getSize(){ return exercises.size(); }

    public ArrayList<Exercise> getExercises(){
        return exercises;
    }

    public void setOpenStatus(boolean b){
        isOpen = b;
    }

    public boolean isOpen(){
        return isOpen;
    }

    public int getId(){return id;}

    public void setId(int i){id = i;}

    public void setExpanded(boolean b){isExpanded = b;}

    public boolean isExpanded(){return isExpanded;}

}
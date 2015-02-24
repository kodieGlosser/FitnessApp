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

    protected ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    private String name;
    boolean isOpen;
    //test for null pointer in workout data

    public Circuit(){
        name = "Placeholder";
    }

    public Circuit( int order, ArrayList<Exercise> e){
        name = "Circuit " + order;
        exercises = e;
    }
    //adds exercise to second to end of list
    public void addToOpenCircuit(Exercise e){
        //if(exercises.isEmpty()) {
        //    exercises.addToOpenCircuit(e);
        //    exercises.addToOpenCircuit(new Exercise());
        //} else {
            exercises.add(exercises.size() - 1, e);
        //}
    }
    //called to addToOpenCircuit a special exercise value to the end
    public void add(Exercise e){
        exercises.add(e);
    }

    public void addExerciseAtIndex(int i, Exercise e){
        exercises.add(i, e);
    }

    public Exercise getExercise(int i){
        return exercises.get(i);
    }

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

}
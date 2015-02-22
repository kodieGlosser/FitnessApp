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
    private int order;
    boolean isLast;
    //test for null pointer in workout data

    public Circuit(){
        order = 0;
        name = "Placeholder";
        isLast = true;
        //exercises.add(new Exercise());
    }

    public Circuit( int order, ArrayList<Exercise> e){
        this.order = order;
        name = "Circuit " + order;
        exercises = e;
    }

    public void add(Exercise e){
        e.setCircuitLocation(order);
        if(exercises.isEmpty()) {
            exercises.add(e);
            exercises.add(new Exercise());
        } else {
            exercises.add(exercises.size() - 1, e);
        }
    }

    public Exercise getExercise(int i){
        return exercises.get(i);
    }

    public void setName(String s){ name = s; }

    public String getName(){return name;}

    public int getOrder() {return order; }

    public void setOrder(int i) { order = i; }

    public int getSize(){ return exercises.size(); }

    public ArrayList<Exercise> getExercises(){
        return exercises;
    }

    public void isNotLast(){
        isLast = false;
    }

    public boolean isLast(){
        return isLast;
    }

}
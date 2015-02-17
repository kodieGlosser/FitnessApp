package com.main.toledo.gymtrackr;

import java.util.ArrayList;

/**
 * Created by Adam on 2/9/2015.
 */
public class Circuit {

    protected ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    private String name;
    private int order;
    //test for null pointer in workout data
    public Circuit(){
        order = 0;
        name = "Circuit 0";
        Exercise e = new Exercise();
        exercises.add(e);
    }
    public Circuit( int order, Exercise e){
        order = order;
        name = "Circuit " + order;
        exercises.add(e);
    }

    public void add(Exercise e){
        exercises.add(e);
    }

    public Exercise get(int i){
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

}

package com.main.toledo.gymtrackr;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Adam on 2/11/2015.
 * This singleton will be used to display data on the workspace.
 */
public class WorkoutData {
    //private HashMap<String, ArrayList<Exercise>> WorkoutMap;
    private ArrayList<Circuit> Workout = new ArrayList<Circuit>();

    private static WorkoutData sWorkspaceData;
    private Context mAppContext;

    private WorkoutData(Context appContext){
        //adds initial values
        mAppContext = appContext;
        initialize();
    }

    public void initialize(){
        Circuit c = new Circuit();
        c.setOpenStatus(false);
        Exercise e = new Exercise();
        c.add(e);
        Workout.add(c);
    }

    public static WorkoutData get(Context c){
        if (sWorkspaceData == null){
            sWorkspaceData = new WorkoutData(c.getApplicationContext());
        }
        return sWorkspaceData;
    }

    public ArrayList<Circuit> getWorkout(){
        return Workout;
    }

    //public ArrayList<String> getCircuitNames()
    //addToOpenCircuit exercise to circuit c
    public void addExerciseToOpenCircuit(Exercise e, int circuitNumber){

        int circuitSize = Workout.get(circuitNumber).getSize();
        //adds exercise to second to last position
        Workout.get(circuitNumber).add( circuitSize - 1 , e );
        //Workout.get(circuitNumber).isNotLast();

        if (Workout.get(circuitNumber).getName() == "Placeholder"){
            Workout.get(circuitNumber).setName("Circuit " + circuitNumber);
            Circuit c = new Circuit();
            Workout.add(c);
        }
    }
    //adds a new open circuit
    public void addCircuit(int circuitNumber){
        Circuit c = new Circuit();
        c.setName("Circuit " + circuitNumber);
        c.add(new Exercise());
        c.setOpenStatus(true);
        Workout.add(circuitNumber, c);
    }
    //adds a closed circuit, e.g. a circuit with only one exercise
    public void addClosedCircuit(Exercise e, int circuitNumber){
        Circuit c = new Circuit();
        c.setOpenStatus(false);
        c.add(e);
        Workout.add(circuitNumber, c);
    }

    //removes exercise at circuit
    public void removeExercise(int exercisePosition, int circuitPosition){
        Workout.get(circuitPosition).removeExercise(exercisePosition);
    }
}
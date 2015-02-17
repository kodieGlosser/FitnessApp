package com.main.toledo.gymtrackr;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

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
        mAppContext = appContext;
        //initiate circuit list
        //Exercise e = new Exercise();
        //Circuit c = new Circuit(0, e);
        //Test, null pointer fix
        Circuit c = new Circuit();
        Workout.add(c);
    }

    public static WorkoutData get(Context c){
        if (sWorkspaceData == null){
            sWorkspaceData = new WorkoutData(c.getApplicationContext());
        }
        return sWorkspaceData;
    }
    //May be necessary
    //public HashMap<String, ArrayList<Exercise>> getWorkoutExercises(){
    //HashMap WorkoutMap = new HashMap<String, ArrayList<Exercise>>();
    //for(Circuit circuit : Workout){
    //    WorkoutMap.put(circuit.getName(), circuit.getExercises());
    //}
    //return WorkoutMap;
    //}

    public ArrayList<Circuit> getWorkout(){
        return Workout;
    }

    public void increment(){
        Circuit c = new Circuit();
        c.setOrder(Workout.size());
        Workout.add(c);

    }
    //public ArrayList<String> getCircuitNames()
    //add exercise to circuit c
    public void addExercise(Exercise e, int circuitNumber){
        Workout.get(circuitNumber).add(e);
        Workout.get(circuitNumber).isNotLast();

        if (Workout.get(circuitNumber).getName() == "Placeholder"){
            Workout.get(circuitNumber).setName("Circuit " + circuitNumber);
        }
    }

}
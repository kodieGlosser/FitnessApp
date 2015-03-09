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

    public void eatPlan(Plan p){

        Workout.clear();
        Circuit_temp[] circuits = p.getCircuits();
        boolean sorted = false;
        //sort in case it's not sorted
        while (!sorted) {
            boolean test = true;
            for (int i = 0; i < circuits.length; i++) {
                if(i != circuits.length - 1){
                    if(circuits[i].getSequence() > circuits[i+1].getSequence()){
                        Circuit_temp c_temp = circuits[i];
                        circuits[i] = circuits[i+1];
                        circuits[i+1] = c_temp;
                        test = false;
                    }
                } else {
                    sorted = test;
                }
            }
        }

        for (Circuit_temp c_old : circuits){
            Circuit c_new = new Circuit();
            c_new.setOpenStatus(c_old.isOpen());
            c_new.setName(c_old.getName());
            c_new.setId(c_old.getCircuitId());
            Exercise[] exercises = c_old.getExercises();
            for(Exercise e : exercises){
                Metric weight = new Metric();
                weight.setType(metricType.WEIGHT);
                Metric reps = new Metric();
                reps.setType(metricType.REPETITIONS);
                e.addMetrics(weight);
                e.addMetrics(reps);
                c_new.add(e);
            }
            if(c_new.isOpen()){
                c_new.add(new Exercise());
            }
            Workout.add(c_new);
        }
        initialize();

    }
}
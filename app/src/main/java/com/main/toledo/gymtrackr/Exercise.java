package com.main.toledo.gymtrackr;

/**
 * Created by Adam on 2/9/2015.
 */
public class Exercise {
    private String name;
    private String muscleGroup; //leaving out muscle for now
    private String equipment;
    private int lastPerformed;  //this will be an integer value of the last time the exercise was
                                //performed, used to organize browse menu
    private ExerciseStatus status;
    private int CircuitLocation = 0;  //this will be the number of the circuit relative to the
                                  //start of the workout a value of 'one' for exercises in the first
                                  //circuit, etc 0 for not part of a circuit
    private boolean display = true;

    public Exercise(String name, String muscleGroup, int lastPerformed, String equipment){
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.lastPerformed = lastPerformed;
        this.equipment = equipment;
    }

    public Exercise(){
        name = "test";
        muscleGroup = "test";
        lastPerformed = 0;
        equipment = "test";
    }

    public void setLastPerformed(int lastPerformed) {
        this.lastPerformed = lastPerformed;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastPerformed() {
        return lastPerformed;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public String getName() {
        return name;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getEquipment(){ return equipment; }

    public void isPlan(){ status = ExerciseStatus.PLAN; }

    public void isCompleted() { status = ExerciseStatus.COMPLETED; }
    @Override
    public String toString() { return name; }

    public int getCircuitLocation(){ return CircuitLocation; }

    public void setCircuitLocation(int i){ CircuitLocation = i;}

    public enum ExerciseStatus {
        PLAN, COMPLETED
    }
}



package com.main.toledo.gymtrackr;

/**
 * Created by Adam on 2/9/2015.
 */
public class Exercise {

    private int m_id;

    private String m_name;
    private String m_muscleGroup; //leaving out muscle for now
    private String m_equipmentType;
    private int m_repetitions;
    private int m_weight;
    private int m_lastPerformed;  //this will be an integer value of the last time the exercise was
                                //performed, used to organize browse menu
    private ExerciseStatus status;
    private int CircuitLocation = 0;  //this will be the number of the circuit relative to the
                                  //start of the workout a value of 'one' for exercises in the first
                                  //circuit, etc 0 for not part of a circuit
    private boolean display = true;

    public Exercise(String name, String muscleGroup, int lastPerformed, String equipmentType){
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_lastPerformed = lastPerformed;
        this.m_equipmentType = equipmentType;
    }

    public Exercise(int id, String name, String muscleGroup, String equipmentType, int repetitions, int weight){
        this.m_id = id;
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_equipmentType = equipmentType;
        this.m_repetitions = repetitions;
        this.m_weight = weight;
    }

    public Exercise(){
        m_name = "test";
        m_muscleGroup = "test";
        m_lastPerformed = 0;
        m_equipmentType = "test";
    }

    public void setId(int id) { this.m_id = id; }

    public int getId() { return this.m_id; }

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
}



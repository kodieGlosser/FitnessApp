package com.main.toledo.gymtrackr;
import android.util.Log;

import java.util.ArrayList;

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
    private int m_sequence;
    private int m_lastPerformed;  //this will be an integer value of the last time the exercise was
    //performed, used to organize browse menu
    private ExerciseStatus status;
    private ArrayList<Metric> m_metrics = new ArrayList<Metric>();
    //performed, used to organize browse menu

    public Exercise(String name, String muscleGroup, int lastPerformed, String equipmentType){
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_lastPerformed = lastPerformed;
        this.m_equipmentType = equipmentType;
    }

    public Exercise(int id, String name, int repetitions, int weight, int sequence){
        this.m_id = id;
        this.m_name = name;
        this.m_repetitions = repetitions;
        this.m_weight = weight;
        this.m_sequence = sequence;

        Log.d("EXERCISE CONSTRUCTOR TEST", "WEIGHT: " + weight + "REPS: " + repetitions);

        //stub for metrics

        Metric weightMetric = new Metric();
        weightMetric.setType(metricType.WEIGHT);
        weightMetric.setMetricIntValue(weight);

        Metric repMetric = new Metric();
        repMetric.setType(metricType.REPETITIONS);
        repMetric.setMetricIntValue(repetitions);

        m_metrics.add(weightMetric);
        m_metrics.add(repMetric);
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

    public void setWeight(int weight) { this.m_weight = weight; }

    public int getWeight() { return this.m_weight; }

    public void setRepetitions(int reps) { this.m_repetitions = reps; }

    public int getRepetitions() { return this.m_repetitions; }

    public void setSequence(int sequence) { this.m_sequence = sequence; }

    public int getSequence() { return this.m_sequence; }

    public void setId(int id) { this.m_id = id; }

    public int getId() { return this.m_id; }

    public void setTargetMuscle(String targetMuscle) { this.m_targetMuscle = targetMuscle; }

    public String getTargetMuscle() { return this.m_targetMuscle; }

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

    public ArrayList<Metric> getMetrics() { return m_metrics; }

    public void addMetrics(Metric m){ m_metrics.add(m); }

    @Override
    public String toString() { return m_name; }

    public enum ExerciseStatus {
        PLAN, COMPLETED
    }

}



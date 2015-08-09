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
    private int m_repetitions = -1;
    private int m_weight = -1;
    private int m_sequence;
    private int m_other = -1;
    private int m_time = -1;
    private int m_oneRepMax;
    private int m_oneRepMaxPercent;
    private boolean m_bWeight;
    private boolean m_bReps;
    private boolean m_bTime;
    private boolean m_bOther;
    private String m_sOther;
    //private int m_exercise_id;

    private int m_lastPerformed;  //this will be an integer value of the last time the exercise was
    //performed, used to organize browse menu
    private ArrayList<Metric> m_metrics = new ArrayList<Metric>();
    private ArrayList<Metric> m_plan_metrics = new ArrayList<>();
    private boolean mSaveToHistory; //tests for whether exercise was used or not, used when saving history
    private boolean mToggled;//used for ui stuffs
    private boolean hasPlanMetrics = false;
    //performed, used to organize browse menu
    private boolean mAnimate = false;

    public Exercise(String name, String muscleGroup, int lastPerformed, String equipmentType){
        //m_exercise_id = WorkoutData.STABLE_ID;
        //WorkoutData.STABLE_ID++;
        //Log.d("EXERCISE CONS 1", "Exercise id: " + m_exercise_id + " Exercise name: " + name);

        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_lastPerformed = lastPerformed;
        this.m_equipmentType = equipmentType;
        //stub for metrics

        //instantiateStubMetrics();

        //tests for whether exercise was used or not, used when saving history
        mSaveToHistory = false;
        mToggled = false;


    }
    //db constructor
    //called when loading a plan
    public Exercise(int id, String name, int repetitions, int weight, int sequence, int oneRepMaxPercent, int time, int other){
        //m_exercise_id = WorkoutData.STABLE_ID;
        //WorkoutData.STABLE_ID++;
        //Log.d("EXERCISE CONS 2", "Exercise id: " + m_exercise_id + " Exercise name: " + name);

        this.m_id = id;
        this.m_name = name;
        this.m_repetitions = repetitions;
        this.m_weight = weight;
        this.m_sequence = sequence;
        this.m_time = time;
        this.m_other = other;
        this.m_oneRepMaxPercent = oneRepMaxPercent;
        //Log.d("EXERCISE CONSTRUCTOR TEST", "WEIGHT: " + weight + "REPS: " + repetitions);

        //stub for metrics
        if(m_weight != -1){
            Metric weightMetric = new Metric();
            weightMetric.setType(metricType.WEIGHT);
            weightMetric.setMetricIntValue(m_weight);
            m_metrics.add(weightMetric);
        }
        if(m_repetitions != -1){
            Metric repMetric = new Metric();
            repMetric.setType(metricType.REPS);
            repMetric.setMetricIntValue(m_repetitions);
            m_metrics.add(repMetric);
        }
        if(m_time != -1){
            Metric timeMetric = new Metric();
            timeMetric.setType(metricType.TIME);
            timeMetric.setMetricIntValue(m_time);
            m_metrics.add(timeMetric);
        }
                /* missing things for other
        if(m_other != -1){
            Metric otherMetric = new Metric();
            otherMetric.setType(metricType.OTHER);
            otherMetric.setMetricName(m_sOther);
            otherMetric.setMetricStringValue("");
            m_metrics.add(otherMetric);
        }


        */


        mSaveToHistory = false;
        mToggled = false;
    }

    //called from browse
    public Exercise(int id, String name, String muscleGroup, String equipmentType, String targetMuscle, int oneRepMax, boolean weight, boolean reps, boolean time, boolean other, String s_other){
        //m_exercise_id = WorkoutData.STABLE_ID;
        //WorkoutData.STABLE_ID++;
        //Log.d("EXERCISE CONS 3", "Exercise id: " + m_exercise_id + " Exercise name: " + name);

        this.m_id = id;
        this.m_name = name;
        this.m_muscleGroup = muscleGroup;
        this.m_equipmentType = equipmentType;
        this.m_targetMuscle = targetMuscle;
        this.m_oneRepMax = oneRepMax;
        this.m_bWeight = weight;
        this.m_bReps = reps;
        this.m_bTime = time;
        this.m_bOther = other;
        this.m_sOther = s_other;
        //if(m_name.equals("1"))// || m_name.equals("2") || m_name.equals("3"))
        //Log.d("4/9", "PULL FROM DB - BROWSE CONSTRUCTOR CALLED NAME: " + m_name + " -- WEIGHT: " + m_bWeight + " -- TIME: " + m_bTime + " -- REPS: " + m_bReps);

        if(m_bWeight){
            Metric weightMetric = new Metric();
            weightMetric.setType(metricType.WEIGHT);
            weightMetric.setMetricIntValue(0);
            m_metrics.add(weightMetric);
        }
        if(m_bReps){
            Metric repMetric = new Metric();
            repMetric.setType(metricType.REPS);
            repMetric.setMetricIntValue(0);
            m_metrics.add(repMetric);
        }
        if(m_bTime){
            Metric timeMetric = new Metric();
            timeMetric.setType(metricType.TIME);
            timeMetric.setMetricIntValue(0);
            m_metrics.add(timeMetric);
        }
        /*
        if(m_bOther){
            Metric otherMetric = new Metric();
            otherMetric.setType(metricType.OTHER);
            otherMetric.setMetricName(m_sOther);
            otherMetric.setMetricStringValue("");
            m_metrics.add(otherMetric);
        }
        */
        if(m_name.equals("1"))// || m_name.equals("2") || m_name.equals("3"))
        for (Metric m : m_metrics){
            Log.d("4/9", "METRIC: " + m.getType() + " -- ADDED TO: " + m_name + " -- TOTAL SIZE: " + m_metrics.size());
        }
        mSaveToHistory = false;
        mToggled = false;
    }

    public Exercise(){
        //m_exercise_id = WorkoutData.STABLE_ID;
        //WorkoutData.STABLE_ID++;
        //Log.d("EXERCISE CONS 4", "Exercise id: " + m_exercise_id + " Exercise name: test");

        m_name = "test";
        m_muscleGroup = "test";
        m_lastPerformed = 0;
        m_equipmentType = "test";

        mSaveToHistory = false;
        mToggled = false;
    }

    public void addSeparatePlanMetrics(){
        for(Metric m : m_metrics){
            Metric plan_metric = new Metric();
            plan_metric.setType(m.getType());
            plan_metric.setMetricIntValue(m.getMetricIntValue());
            m_plan_metrics.add(plan_metric);
        }
        hasPlanMetrics = true;
    }
    /*
    public void instantiateStubMetrics(){
        //stub for metrics
        Metric weightMetric = new Metric();
        weightMetric.setType(metricType.WEIGHT);
        weightMetric.setMetricIntValue(0);
        //weightMetric.setMetricIntValue(weight);

        Metric repMetric = new Metric();
        repMetric.setType(metricType.REPS);
        repMetric.setMetricIntValue(0);
        //repMetric.setMetricIntValue(repetitions);

        m_metrics.add(weightMetric);
        m_metrics.add(repMetric);
    }
    */
    //public void setStableExerciseId(int id){m_exercise_id = id;}
    public void setOneRepMax(int oneRepMax) { this.m_oneRepMax = oneRepMax; }

    public int getOneRepMax() { return this.m_oneRepMax; }

    public void setTime(int time) { this.m_time = time; }

    public int getTime() { return this.m_time; }

    public void setOther(int other) { this.m_other = other; }

    public int getOther() { return this.m_other; }

    public void setOneRepMaxPercent(int oneRepMaxPercent) { this.m_oneRepMaxPercent = oneRepMaxPercent; }

    public int getOneRepMaxPercent() { return this.m_oneRepMaxPercent; }

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

    public ArrayList<Metric> getPlanMetrics()  { return m_plan_metrics; }

    public void addMetrics(Metric m){ m_metrics.add(m); }

    public void setSaveToHistory(boolean b){mSaveToHistory = b;}

    public void setToggled(boolean b){mToggled = b;}

    public boolean isToggled(){return mToggled;}

   //public int getStableID(){return m_exercise_id;}

    public int getMetricValueByType(metricType metricType){
        int i = 0;
        for(Metric m : m_metrics){
            if (m.getType() == metricType){
                i = m.getMetricIntValue();
            }
        }
        return i;
    }

    public boolean isSaveToHistorySet(){return mSaveToHistory;}
    @Override
    public String toString() { return m_name; }

    public boolean hasPlanMetrics(){return hasPlanMetrics;}
    /*
    private boolean m_bWeight;
    private boolean m_bReps;
    private boolean m_bTime;
    private boolean m_bOther;
    private String m_sOther;
     */
    public boolean usesWeight(){return m_bWeight;}
    public boolean usesTime(){return m_bTime;}
    public boolean usesReps(){return m_bReps;}
    public boolean usesOther(){return m_bOther;}
    public String getOtherName(){return m_sOther;}

    public void setUsesWeight(boolean b){m_bWeight = b;}
    public void setUsesTime(boolean b){m_bTime = b;}
    public void setUsesReps(boolean b){m_bReps = b;}
    public void setUsesOthers(boolean b){m_bOther = b;}
    public void setOtherName(String s){m_sOther = s;}

    public void setAnimate(boolean b){mAnimate = b;}
    public boolean doAnimation(){return mAnimate;}
}



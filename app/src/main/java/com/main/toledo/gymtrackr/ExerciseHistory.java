package com.main.toledo.gymtrackr;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kodie Glosser on 2/23/2015.
 */
public class ExerciseHistory {

    private int m_id, m_weight, m_rep, m_exerciseId, m_planId;
    private Date m_date;
    //stubs
    private ArrayList<Metric> m_metrics = new ArrayList<Metric>();

    public ExerciseHistory(Date date, int weight, int rep, int exerciseId, int planId){
        this.m_date = date;
        this.m_weight = weight;
        this.m_rep = rep;
        this.m_exerciseId = exerciseId;
        this.m_planId = planId;

        //Stub for metrics
        Metric weightMetric = new Metric();
        weightMetric.setType(metricType.WEIGHT);
        weightMetric.setMetricIntValue(weight);

        Metric repMetric = new Metric();
        repMetric.setType(metricType.REPETITIONS);
        repMetric.setMetricIntValue(rep);

        m_metrics.add(weightMetric);
        m_metrics.add(repMetric);
    }

    public Date getDate() { return this.m_date; }

    public int getWeight() { return this.m_weight; }

    public int getRep() { return this.m_rep; }

    public int getExerciseId() { return this.m_exerciseId; }

    public int getPlanId() { return this.m_planId; }
    //stub methods
    public void addMetric(Metric m){m_metrics.add(m);}

    public ArrayList<Metric> getMetrics(){return m_metrics;}

}

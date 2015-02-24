package com.main.toledo.gymtrackr;

import java.sql.Date;

/**
 * Created by Kodie Glosser on 2/23/2015.
 */
public class ExerciseHistory {
    private int m_id, m_weight, m_rep, m_exerciseId;
    private Date m_date;

    public ExerciseHistory(Date date, int weight, int rep, int exerciseId){
        this.m_date = date;
        this.m_weight = weight;
        this.m_rep = rep;
        this.m_exerciseId = exerciseId;
    }

    public Date getDate() { return this.m_date; }

    public int getWeight() { return this.m_weight; }

    public int getRep() { return this.m_rep; }

    public int getExerciseId() { return this.m_exerciseId; }
}

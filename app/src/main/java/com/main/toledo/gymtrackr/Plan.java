package com.main.toledo.gymtrackr;

/**
 * Created by Kodie Glosser on 2/23/2015.
 */
public class Plan {

    private String m_name;
    private Circuit[] m_circuits;
    private int m_planId;
    public Plan(String name, Circuit[] circuits, int planId) {
        m_name = name;
        m_circuits = circuits;
        m_planId = planId;
    }

    public void setName(String s){m_name = s;}

    public String getName(){return m_name;}

    public void setCircuits(Circuit[] circuits) {m_circuits = circuits;}

    public Circuit[] getCircuits() {return m_circuits;}

    public void setPlanId(int planId) { m_planId = planId;}

    public int getPlanId() {return m_planId;}

}

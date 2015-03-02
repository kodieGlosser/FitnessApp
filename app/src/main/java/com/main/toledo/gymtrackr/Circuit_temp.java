package com.main.toledo.gymtrackr;

/**
 * Created by Kodie Glosser on 3/1/2015.
 */
public class Circuit_temp {

    private String m_name;
    private Exercise[] m_exercises;
    private int m_circuitId;
    private boolean m_open;
    private int m_sequence;

    public Circuit_temp(String name, Exercise[] exercises, int circuitId, boolean open, int sequence) {
        m_name = name;
        m_exercises = exercises;
        m_circuitId = circuitId;
        m_open = open;
        m_sequence = sequence;
    }

    public void setName(String s){m_name = s;}

    public String getName(){return m_name;}

    public void setExercises(Exercise[] exercises){m_exercises = exercises;}

    public Exercise[] getExercises(){return m_exercises;}

    public void setCircuitId(int id){m_circuitId = id;}

    public int getCircuitId(){return m_circuitId;}

    public void setOpen(boolean open){m_open = open;}

    public boolean isOpen(){return m_open;}

    public void setSequence(int sequence){m_sequence = sequence;}

    public int getSequence(){return m_sequence;}
}

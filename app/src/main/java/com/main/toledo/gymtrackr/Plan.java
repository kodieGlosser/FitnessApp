package com.main.toledo.gymtrackr;

/**
 * Created by Kodie Glosser on 2/23/2015.
 */
public class Plan {

    String m_name;

    public Plan(){
        m_name = "Name not found.";
    }

    public void setName(String s){m_name = s;}

    public String getName(){return m_name;}

}

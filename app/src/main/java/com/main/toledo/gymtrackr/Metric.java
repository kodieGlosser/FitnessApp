package com.main.toledo.gymtrackr;

/**
 * Created by Adam on 2/26/2015.
 */
public class Metric {
    metricType m_type;
    int metricIntValue;
    String metricStringValue;
    String metricName;

    public Metric(){
    }

    public void setType(metricType t){ m_type = t; }
    public metricType getType(){return m_type; }

    public void setMetricIntValue(int i){ metricIntValue = i;}
    public int getMetricIntValue(){ return metricIntValue; }

    public void setMetricStringValue(String s){ metricStringValue = s;}
    public String getMetricStringValue(){return metricStringValue; }

    public void setMetricName(String s){metricName = s;}
    public String getMetricName(){return metricName;}

}






    /*
    Boolean m_weight;
    Boolean m_repetitions;
    Boolean m_time;
    Boolean m_other;

    int m_weightValue;
    int m_repetitionValue;
    int m_timeValue;
    String m_otherValue;
    String m_otherValueName;

    public Metric(){
        m_weight = false;
        m_repetitions = false;
        m_time = false;
        m_other = false;
    }

    public boolean usesWeight(){ return m_weight; }

    public boolean usesRepetitions(){ return m_repetitions; }

    public boolean usesTime(){ return m_time; }

    public boolean usesOther(){ return m_other; }
    //used when loaded from DB at some point
    public void setMetrics(boolean weight, boolean repetitions, boolean time,
                           boolean other, String otherValueName){
        m_weight = weight;
        m_repetitions = repetitions;
        m_time = time;
        m_other = other;
        if(otherValueName != null){
            m_otherValueName = otherValueName;
        }
    }

    public void setWeight(int w){ m_weightValue = w; }

    public void setRepetitions(int r){ m_repetitionValue = r; }

    public void setTime(int t){ m_timeValue = t; }

    public void setOther(String s){ m_otherValue = s; }
    */
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

package com.example.root.project_amio;

import java.util.Date;

/**
 * Created by root on 04/03/18.
 */

public class LumData{
    private Double mote;
    private Double value;
    private Date time;
    private String label;

    public LumData(Double value, Double mote, Date time, String label){
        this.value = value;
        this.mote = mote;
        this.time = time;
        this.label = label;
    }

    public void update(LumData newData){
        this.setValue(newData.getValue());
        this.setTime(newData.getTime());
        this.setLabel(newData.getLabel());
    }

    public Double getMote() {
        return mote;
    }

    public void setMote(Double mote) {
        this.mote = mote;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "LumData{" +
                "mote=" + mote +
                ", value=" + value +
                ", time=" + time +
                ", label='" + label + '\'' +
                '}';
    }
}

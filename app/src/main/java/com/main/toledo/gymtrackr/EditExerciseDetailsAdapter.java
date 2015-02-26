package com.main.toledo.gymtrackr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/26/2015.
 */
    public class EditExerciseDetailsAdapter extends ArrayAdapter<Metric> {

    private Context m_context;
    public EditExerciseDetailsAdapter(Context context, int resource, ArrayList<Metric> metrics){
        super(context, resource, metrics);
        m_context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.m_context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.w_circuit_group, null);

        }

        Metric m = getItem(position);
        //CASE STATEMENT FOR METRIC TYPE VALUES GOES HERE

        return convertView;
    }

}
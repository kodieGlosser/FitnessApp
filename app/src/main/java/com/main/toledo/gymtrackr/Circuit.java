package com.main.toledo.gymtrackr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2/9/2015.
 */
public class Circuit {

    protected ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    private String name;
    private int order;
    private View m_view;
    boolean isLast;
    private String viewType;
    //test for null pointer in workout data

    public Circuit(){
        order = 0;
        name = "Placeholder";
        isLast = true;
        //exercises.add(new Exercise());
    }

    public Circuit( int order, ArrayList<Exercise> e){
        this.order = order;
        name = "Circuit " + order;
        exercises = e;
    }

    public void add(Exercise e){
        e.setCircuitLocation(order);
        if(exercises.isEmpty()) {
            exercises.add(e);
            exercises.add(new Exercise());
        } else {
            exercises.add(exercises.size() - 1, e);
        }
    }

    public Exercise get(int i){
        return exercises.get(i);
    }

    public void setName(String s){ name = s; }

    public String getName(){return name;}

    public int getOrder() {return order; }

    public void setOrder(int i) { order = i; }

    public int getSize(){ return exercises.size(); }

    public ArrayList<Exercise> getExercises(){
        return exercises;
    }

    public void isNotLast(){
        isLast = false;
    }

    public boolean isLast(){
        return isLast;
    }

    public View initiateView(final Context c) {
        LayoutInflater inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (name) {
            case "Placeholder":
                m_view = inflater.inflate(R.layout.w_end_buttons, null);
                viewType = "Buttons";
                break;

            default:
                m_view = inflater.inflate(R.layout.w_circuit_group, null);
                viewType = "Text";
                break;
        }

        return m_view;
    }

    public View refreshView(final Context c){
        Button browseButton;
        TextView textView;

        switch (name) {
            case "Placeholder":
                //Log.d("refreshTest ", "Placeholder called in " + order);
                if (m_view == null) {
                    //                   Log.d("TEST ", "m_view is null for PlaceHolderCase");
                    //                   Log.d("TEST ", "in circuit " + order);
                    //                   Log.d("TEST ", "LIVESAVER GOOOOO");
                    LayoutInflater inflater = (LayoutInflater) c
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    m_view = inflater.inflate(R.layout.w_end_buttons, null);
                    viewType = "Buttons";
                }

                browseButton = (Button)m_view.findViewById(R.id.BrowseButton);
                browseButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent i = new Intent(c, BrowseActivity.class);
                        i.putExtra("EXTRA_CIRCUIT_NUMBER", order);
                        c.startActivity(i);
                    }
                });

                break;

            default:
                if (viewType == "Buttons") {
//                    Log.d("TEST ", "m_view is null for default case");
//                    Log.d("TEST ", "in circuit " + order);
//                    Log.d("TEST ", "LIVESAVER GOOOOO");
                    LayoutInflater inflater = (LayoutInflater) c
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    m_view = inflater.inflate(R.layout.w_circuit_group, null);
                    viewType = "Text";
                }

                //               Log.d("TEST ", "VIEW TYPE IS " + viewType);
                textView = (TextView)m_view.findViewById(R.id.lblListHeader);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setText(name);
                break;
        }

        return m_view;
    }

}
package com.example.calorietracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FdSearchAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> data;

    private TextView lblFoodName;
    private Button btnConsume;

    public FdSearchAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_search_food, parent, false);
        }

        lblFoodName = convertView.findViewById(R.id.lblName);
        lblFoodName.setText(data.get(position));
        lblFoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDescription(position);
            }
        });

        btnConsume = convertView.findViewById(R.id.btnConsume);
        btnConsume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFoodDetails(position);
            }
        });

        return convertView;
    }

    public void getDescription(int index) {
        SearchActivity searchActivity = (SearchActivity)context;
        searchActivity.getFoodDescription(index);
    }

    public void getFoodDetails(int index) {
        SearchActivity searchActivity = (SearchActivity)context;
        searchActivity.getFoodDetails(index);
    }


}

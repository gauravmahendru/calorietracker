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

public class FdCategoryAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> data;

    private TextView FoodName;
    private Button Consume;

    public FdCategoryAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_food_category, parent, false);
        }

        FoodName = convertView.findViewById(R.id.lblName);
        FoodName.setText(data.get(position));
        FoodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDescription(position);
            }
        });

        Consume = convertView.findViewById(R.id.btnConsume);
        Consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushConsumption(position);
            }
        });

        return convertView;
    }

    public void getDescription(int index) {
        DDActivity dailyDiet = (DDActivity) context;
        dailyDiet.getFoodDescription(index);
    }

    public void pushConsumption(int index) {
        DDActivity dailyDiet = (DDActivity) context;
        dailyDiet.addConsumptionItem(index);
    }
}


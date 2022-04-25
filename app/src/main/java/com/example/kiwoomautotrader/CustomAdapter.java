package com.example.kiwoomautotrader;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter {
    private ArrayList<String> strategyName;
    private TextView tvStrategyName;
    private Button btnDelete;
    private Button btnSetting;
    private Activity context;

    public CustomAdapter(Activity context, int resource, ArrayList<String> strategyName) {
        super(context, resource,strategyName);
        this.context = context;
        this.strategyName = strategyName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item, parent, false);
        tvStrategyName = (TextView) row.findViewById(R.id.tvStrategyName);
        btnDelete = (Button) row.findViewById(R.id.btnDelete);
        btnSetting = (Button) row.findViewById(R.id.btnSetting);


        tvStrategyName.setText(strategyName.get(position));

        tvStrategyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Text Working",Toast.LENGTH_SHORT).show();
            }
        });


        //To lazy to implement interface
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Delete Button Working",Toast.LENGTH_SHORT).show();
            }
        });


        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Setting Button Working",Toast.LENGTH_SHORT).show();
            }
        });
        return  row;
    }
}
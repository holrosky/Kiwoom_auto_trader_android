package com.example.kiwoomautotrader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainCustomAdapter extends ArrayAdapter {
    private final String LOG_TAG = MainCustomAdapter.class.getCanonicalName();

    private ArrayList<String> strategyName;
    private TextView tvStrategyName;
    private Button btnDelete;
    private Button btnStartStop;
    private Button btnClear;
    private Button btnSetting;
    private Activity context;
    //private boolean isRunning;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public MainCustomAdapter(Activity context, int resource, ArrayList<String> strategyName) {
        super(context, resource,strategyName);
        this.context = context;
        this.strategyName = strategyName;
        sharedPreferences = context.getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.activity_main_strategy_row_item, parent, false);
        tvStrategyName = (TextView) row.findViewById(R.id.tvStrategyName);
        btnStartStop = (Button) row.findViewById(R.id.btnStartStop);
        //btnDelete = (Button) row.findViewById(R.id.btnDelete);
        //btnClear = (Button) row.findViewById(R.id.btnClear);
        btnSetting = (Button) row.findViewById(R.id.btnSetting);

        tvStrategyName.setText(strategyName.get(position));

        boolean isRunning = false;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            if(jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("running_status").equals("true"))
                isRunning = true;
            else
                isRunning = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isRunning)
        {
            btnStartStop.setText("중지");
            btnStartStop.setBackgroundColor(Color.RED);
            //btnDelete.setEnabled(false);
        }
        else
        {
            btnStartStop.setText("시작");
            btnStartStop.setBackgroundColor(Color.GREEN);
            //btnDelete.setEnabled(true);
        }


        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.strategyStopRun(position);
            }
        });

//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                Toast.makeText(MainActivity.mContext,"삭제중..", Toast.LENGTH_SHORT).show();
//                                JSONObject temp = new JSONObject();
//                                try {
//                                    temp.put("command", "remove_strategy_from_android");
//                                    temp.put("position", position);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                                MainActivity.publishMsg(temp.toString());
//
//                                break;
//
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                //No button clicked
//                                break;
//                        }
//                    }
//                };
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage(strategyName.get(position) + " 전략을 삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
//                        .setNegativeButton("취소", dialogClickListener).show();
//            }
//        });


        boolean finalIsRunning = isRunning;
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.strategySetting(position, finalIsRunning, strategyName.get(position));
            }
        });

        return  row;
    }
}
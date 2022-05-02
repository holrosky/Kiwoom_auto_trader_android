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
    private Button btnSetting;
    private Activity context;

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
        btnDelete = (Button) row.findViewById(R.id.btnDelete);
        btnSetting = (Button) row.findViewById(R.id.btnSetting);

        tvStrategyName.setText(strategyName.get(position));

        boolean isRunning = false;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            if(jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("runnig_status").equals("true"))
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
            btnDelete.setEnabled(false);
            btnSetting.setEnabled(false);
        }
        else
        {
            btnStartStop.setText("시작");
            btnStartStop.setBackgroundColor(Color.GREEN);
            btnDelete.setEnabled(true);
            btnSetting.setEnabled(true);
        }

        tvStrategyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Text Working",Toast.LENGTH_SHORT).show();
            }
        });

        //To lazy to implement interface
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnStartStop = (Button)v;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                try {

                                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));

                                    if(jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("runnig_status").equals("false"))
                                    {
                                        if (jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("strategy_name").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("sCode").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("max_loss").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("max_profit").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("start_hour").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("start_min").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("end_hour").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("end_min").equals("") ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_buy").length() == 0 ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_sell").length() == 0 ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("clear_buy").length() == 0 ||
                                                jsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("clear_sell").length() == 0 )
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:

                                                            break;

                                                    }
                                                }
                                            };
                                            AlertDialog.Builder builder = new AlertDialog.Builder(btnStartStop.getContext());
                                            builder.setMessage("모든 값을 입력후 시작해주세요!").setPositiveButton("확인", dialogClickListener).show();
                                        }
                                        else
                                            jsonObject.getJSONArray("strategy_list").getJSONObject(position).put("runnig_status", "true");

                                    }
                                    else
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(position).put("runnig_status", "false");

                                    editor.putString("strategy_json", jsonObject.toString());
                                    editor.commit();


                                    Log.d(LOG_TAG, sharedPreferences.getString("strategy_json", null));

                                    notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String dialogMessage = "";
                if (btnStartStop.getText().toString().equals("시작"))
                {
                    dialogMessage = " 전략을 시작하시겠습니까?";
                }
                else
                {
                    dialogMessage = " 전략을 중지하시겠습니까?";
                }
                builder.setMessage(strategyName.get(position) + dialogMessage).setPositiveButton("예", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();
            }
        });


        //To lazy to implement interface
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                                    jsonObject.getJSONArray("strategy_list").remove(position);

                                    editor.putString("strategy_json", jsonObject.toString());
                                    editor.commit();

                                    Log.d(LOG_TAG, sharedPreferences.getString("strategy_json", null));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                MainActivity.strategyRemove(position);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(strategyName.get(position) + " 전략을 삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();
            }
        });


        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.strategySetting(position);
            }
        });
        return  row;
    }
}
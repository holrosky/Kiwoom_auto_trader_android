package com.example.kiwoomautotrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeneralSettingActivity extends AppCompatActivity {

    private int strategyPosition;
    private boolean isRunning;
    private String strategyName;
    private EditText etStrategyName;

    private Button btnSave;

    private Spinner spnAccount;
    private CheckBox cbSimulation;

    private EditText etMaxLoss;
    private EditText etMaxProfit;
    private Button btnHideTime;
    private Button btnAddTime;
    private Button btnAddQuant;



    private int typeCounter;

    private TextView tvQuant;
    private boolean isAllFilledUp;

    private ListView listTime;
    private ListView listQuant;

    private ArrayList<TimeItem> timeItemList;
    private ArrayList<String> timeInfo;

    private ArrayList<QuantItem> quantItemList;
    private ArrayList<String> quantInfo;

    private TimeRangeCustomAdapter timeRangeCustomAdapter;
    private QuantCustomAdapter quantCustomAdapter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setting);

        Intent intent = getIntent();

        strategyPosition = intent.getIntExtra("position", 0);
        isRunning = intent.getBooleanExtra("is_running", true);
        strategyName = intent.getStringExtra("strategy_name");

        setTitle(strategyName + " 기본 설정");

        etStrategyName = (EditText) findViewById(R.id.etStrategyName);

        btnSave = (Button) findViewById(R.id.btnSave);
        if (isRunning)
            btnSave.setEnabled(false);

        spnAccount = (Spinner) findViewById(R.id.spnAccount);
        cbSimulation = (CheckBox) findViewById(R.id.cbSimulation);

        etMaxLoss = (EditText) findViewById(R.id.etMaxLoss);
        etMaxProfit = (EditText) findViewById(R.id.etMaxProfit);

        btnHideTime = (Button) findViewById(R.id.btnHideTime);
        btnAddTime = (Button) findViewById(R.id.btnAddTime);
        btnAddQuant = (Button) findViewById(R.id.btnAddQuant);

        typeCounter = 0;

        listTime = (ListView) findViewById(R.id.listTime);
        listQuant = (ListView) findViewById(R.id.listQuant);

        tvQuant = (TextView) findViewById(R.id.tvQuant);



        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllFilledUp = true;

                for (int i = 0; i < timeItemList.size(); i++) {
                    timeInfo = new ArrayList<>();
                    if (timeItemList.get(i).getStartHour().equals("") || timeItemList.get(i).getStartMin().equals("") ||
                            timeItemList.get(i).getEndHour().equals("") || timeItemList.get(i).getEndMin().equals(""))
                        isAllFilledUp = false;

                    if (!isAllFilledUp)
                        break;
                }

                for (int i = 0; i < quantItemList.size(); i++) {
                    quantInfo = new ArrayList<>();
                    if (quantItemList.get(i).getProfitFrom().equals("") || quantItemList.get(i).getProfitTo().equals("") || quantItemList.get(i).getQuant().equals(""))
                        isAllFilledUp = false;

                    if (!isAllFilledUp)
                        break;
                }


                if(etStrategyName.getText().toString().equals("") || etMaxLoss.getText().toString().equals("") || etMaxProfit.getText().toString().equals("") ||
                        !isAllFilledUp)
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(btnSave.getContext());
                    builder.setMessage("모든 값을 입력해주세요!").setPositiveButton("확인", dialogClickListener).show();
                }
                else
                {
                    try {

                        JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                        JSONArray jsonArray = jsonObject.getJSONArray("strategy_list");

                        JSONObject temp_jsonObject = jsonArray.getJSONObject(strategyPosition);

                        temp_jsonObject.put("strategy_name", etStrategyName.getText().toString());

                        if (cbSimulation.isChecked())
                            temp_jsonObject.put("is_simulation", "true");
                        else
                            temp_jsonObject.put("is_simulation", "false");
                        temp_jsonObject.put("trade_account", spnAccount.getSelectedItem().toString());

                        temp_jsonObject.put("max_loss", etMaxLoss.getText().toString());
                        temp_jsonObject.put("max_profit", etMaxProfit.getText().toString());

                        JSONArray jsonArr = new JSONArray();

                        for(int i = 0; i < timeItemList.size(); i++)
                        {
                            String timeFormat = "";
                            String temp = timeItemList.get(i).getStartHour();

                            if (temp.length() == 1 && Integer.parseInt(temp) < 10)
                                temp = '0' + temp;

                            timeFormat += temp + ";";

                            temp = timeItemList.get(i).getStartMin();

                            if (temp.length() == 1 && Integer.parseInt(temp) < 10)
                                temp = '0' + temp;

                            timeFormat += temp + ";";

                            temp = timeItemList.get(i).getEndHour();

                            if (temp.length() == 1 && Integer.parseInt(temp) < 10)
                                temp = '0' + temp;

                            timeFormat += temp + ";";

                            temp = timeItemList.get(i).getEndMin();

                            if (temp.length() == 1 && Integer.parseInt(temp) < 10)
                                temp = '0' + temp;

                            timeFormat += temp;

                            jsonArr.put(timeFormat);

                        }

                        temp_jsonObject.put("trade_time", jsonArr);

                        jsonArr = new JSONArray();

                        for(int i = 0; i < quantItemList.size(); i++)
                        {
                            String temp = quantItemList.get(i).getProfitFrom();
                            temp += ";";

                            temp += quantItemList.get(i).getProfitTo();
                            temp += ";";

                            temp += quantItemList.get(i).getQuant();

                            jsonArr.put(temp);
                        }

                        temp_jsonObject.put("quant", jsonArr);

                        jsonArray.put(strategyPosition, temp_jsonObject);
                        jsonObject.put("strategy_list", jsonArray);

                        jsonObject.put("command", "json_update_require_from_android");
                        Log.e("asdad", jsonObject.toString());
                        MainActivity.publishMsg(jsonObject.toString());

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast myToast = Toast.makeText(btnSave.getContext(),"저장이 완료되었습니다!", Toast.LENGTH_SHORT);

                        finish();
                        myToast.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTime();
            }
        });

        btnAddQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuant();
            }
        });

        btnHideTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQuant.setText("계약수 설정");
                if(btnHideTime.getText().toString().equals("숨기기"))
                {
                    btnHideTime.setText("보기");
                    listTime.setVisibility(View.GONE);
                }
                else
                {
                    btnHideTime.setText("숨기기");
                    listTime.setVisibility(View.VISIBLE);
                }
            }
        });

        timeItemList = new ArrayList<>();
        quantItemList = new ArrayList<>();

        timeRangeCustomAdapter = new TimeRangeCustomAdapter(GeneralSettingActivity.this,R.layout.custom_listview_item_layout, timeItemList);
        quantCustomAdapter = new QuantCustomAdapter(GeneralSettingActivity.this, R.layout.custom_listview_item_layout, quantItemList);
        loadValue();

        listTime.setAdapter(timeRangeCustomAdapter);
        listQuant.setAdapter(quantCustomAdapter);


    }
    private void addTime()
    {
        timeItemList.add(new TimeItem(typeCounter));
        typeCounter ++;
        //enterStrategyLayoutList.add(new LinearLayout(btnAdd.getContext()));
        timeRangeCustomAdapter.notifyDataSetChanged();
    }

    private void addQuant()
    {
        quantItemList.add(new QuantItem(typeCounter));
        typeCounter ++;
        //enterStrategyLayoutList.add(new LinearLayout(btnAdd.getContext()));
        quantCustomAdapter.notifyDataSetChanged();
    }

    private void loadValue()
    {
        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            JSONArray jsonArray = jsonObject.getJSONArray("strategy_list");

            jsonObject = jsonArray.getJSONObject(strategyPosition);

            etStrategyName.setText(jsonObject.getString("strategy_name"));

            if (jsonObject.getString("is_simulation").equals("false"))
                cbSimulation.setChecked(false);
            else
                cbSimulation.setChecked(true);

            JSONArray timeArray = jsonObject.getJSONArray("trade_time");

            for(int i = 0; i < timeArray.length(); i++)
            {
                addTime();
                String[] timeSplit = timeArray.get(i).toString().split(";");
                timeItemList.get(i).setStartHour(timeSplit[0]);
                timeItemList.get(i).setStartMin(timeSplit[1]);
                timeItemList.get(i).setEndHour(timeSplit[2]);
                timeItemList.get(i).setEndMin(timeSplit[3]);
            }

            JSONArray quantArray = jsonObject.getJSONArray("quant");

            for(int i = 0; i < quantArray.length(); i++)
            {
                addQuant();
                String[] quantSplit = quantArray.get(i).toString().split(";");
                quantItemList.get(i).setProfitFrom(quantSplit[0]);
                quantItemList.get(i).setProfitTo(quantSplit[1]);
                quantItemList.get(i).setQuant(quantSplit[2]);
            }

            //loadTime();

            etMaxLoss = (EditText) findViewById(R.id.etMaxLoss);
            etMaxProfit = (EditText) findViewById(R.id.etMaxProfit);

            etMaxLoss.setText(jsonObject.getString("max_loss"));
            etMaxProfit.setText(jsonObject.getString("max_profit"));

            String currentAccNum = jsonObject.getString("trade_account");

            ArrayList<String> accList = MainActivity.getAccountList();
            int index = -1;

            for (int i=0;i<accList.size();i++) {
                try {
                    if (accList.get(i).toString().equals(currentAccNum)) {
                        index = i;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(spnAccount.getContext(), android.R.layout.simple_spinner_item, accList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccount.setAdapter(adapter);

            if (index != -1)
            {
                spnAccount.setSelection(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(spnAccount.getContext());
        builder.setMessage("저장을 안하고 뒤로 가시겠습니까?").setPositiveButton("예", dialogClickListener)
                .setNegativeButton("취소", dialogClickListener).show();
    }

}

package com.example.kiwoomautotrader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private static Context mContext;


    private static final String kiwoomID = "artanis0";

    private static TextView tvMsgServerStatus;
    private static TextView tvAutoTraderStatus;
    private static Button btnTradeServer;
    private static Button btnAccChange;

    private static TextView tvDateInput;
    private static Button btnSelectDate;
    private static Spinner spnAccount;
    private static Button btnGetProfit;

    private static EditText etTradeProfit;
    private static EditText etFee;
    private static EditText etTotalProfit;

    private static TextView tvStrategy;
    private static Button btnAddStrategy;
    private static ListView list;

    private static ArrayList<String> strategyNameList;
    private static MainCustomAdapter adapter;

    private static int offCount;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            tvAutoTraderStatus.setText("연결 끊김");
            setUIForStop();
        }
    };

    private static boolean isUIDisabled;
    private AWSMQTT AWSMQTTClient;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private long backpressedTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("strategy_json", null) == null)
        {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("strategy_list", new JSONArray());
                editor.putString("strategy_json", jsonObject.toString());
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (sharedPreferences.getString("kiwoom_id", null) == null)
        {
            finish();
            Intent intent = new Intent(getApplicationContext(), KiwoomIDSettingActivity.class);
            startActivity(intent);
        }

        String serverType = "";

        tvMsgServerStatus = (TextView) findViewById(R.id.etStrategyName);
        tvAutoTraderStatus = (TextView) findViewById(R.id.tvAutoTraderStatus);
        btnTradeServer = (Button) findViewById(R.id.btnTradeServer);
        btnAccChange = (Button) findViewById(R.id.btnAccChange);

        tvDateInput = (TextView) findViewById(R.id.tvDateInput);
        btnSelectDate = (Button) findViewById(R.id.btnSelectDate);
        spnAccount = (Spinner) findViewById(R.id.spnAccount);
        btnGetProfit = (Button) findViewById(R.id.btnGetProfit);

        etTradeProfit = (EditText) findViewById(R.id.etTradeProfit);
        etFee = (EditText) findViewById(R.id.etFee);
        etTotalProfit = (EditText) findViewById(R.id.etTotalProfit);

        tvStrategy = (TextView) findViewById(R.id.tvStrategy);
        btnAddStrategy = (Button) findViewById(R.id.btnAddStrategy);
        list = (ListView) findViewById(R.id.list);

        if (sharedPreferences.getString("trade_server", "test").equals("test")) {
            serverType = "모의서버";
            btnTradeServer.setText("실전서버");
        }
        else {
            serverType = "실전서버";
            btnTradeServer.setText("모의서버");
        }


        btnAddStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strategyNameList.add("새로운 전략");
                try {

                    JSONObject newStrategy = new JSONObject();

                    newStrategy.put("strategy_name", "새로운 전략");
                    newStrategy.put("runnig_status", "false");
                    newStrategy.put("sCode", "");
                    newStrategy.put("is_simulation", "false");
                    newStrategy.put("trade_account", "");
                    newStrategy.put("max_loss", "");
                    newStrategy.put("max_profit", "");
                    newStrategy.put("start_hour", "");
                    newStrategy.put("start_min", "");
                    newStrategy.put("end_hour", "");
                    newStrategy.put("end_min", "");
                    newStrategy.put("enter_buy", new JSONArray());
                    newStrategy.put("enter_sell", new JSONArray());
                    newStrategy.put("clear_buy", new JSONArray());
                    newStrategy.put("clear_sell", new JSONArray());

                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));

                    jsonObject.getJSONArray("strategy_list").put(newStrategy);

                    editor.putString("strategy_json", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                adapter.notifyDataSetChanged();
            }
        });


        btnTradeServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (sharedPreferences.getString("trade_server", "test").equals("test"))
                        editor.putString("trade_server", "real");
                    else
                        editor.putString("trade_server", "test");

                    editor.commit();
                    finish();
                    startActivity(getIntent());

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Trade Server swap error.", e);
                }
            }
        });

        btnAccChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {

                    Intent intent = new Intent(getApplicationContext(), KiwoomIDSettingActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Account change error", e);
                }
            }
        });

        Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String formatted = format1.format(c.getTime());

        tvDateInput.setText(formatted);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String strMonth = "";
                if ((month + 1) < 10)
                    strMonth = "0" + String.valueOf(month+1);
                else
                    strMonth = String.valueOf(month+1);

                String strDate = "";
                if (dayOfMonth< 10)
                    strDate = "0" + String.valueOf(dayOfMonth);
                else
                    strDate = String.valueOf(dayOfMonth);

                tvDateInput.setText(year + "-" + strMonth + "-" + strDate);
            }
        }, mYear, mMonth, mDay);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnSelectDate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        setTitle("ID : " + sharedPreferences.getString("kiwoom_id", null) + " - " + serverType);

        AWSMQTTClient = new AWSMQTT(getApplicationContext());


    }

    @SuppressLint("LongLogTag")
    public void onResume() {
        MainActivity.super.onResume();

        tvMsgServerStatus.setText("연결 끊김");
        tvAutoTraderStatus.setText("연결 끊김");
        setUIForStop();

        strategyNameList = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            JSONArray jsonArray = jsonObject.getJSONArray("strategy_list");

            for(int i = 0; i < jsonArray.length(); i++)
            {
                strategyNameList.add(jsonArray.getJSONObject(i).getString("strategy_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new MainCustomAdapter(MainActivity.this,R.layout.activity_main_strategy_row_item, strategyNameList);
        list.setAdapter(adapter);

        try {

            AWSMQTTClient.disconnect();
            AWSMQTTClient.connect();
            autoTraderConnectionCheck();


        } catch (Exception e) {
            Log.e("AWSMQTTClient reconnect error", e.toString());
        }


    }

    private void autoTraderConnectionCheck() {
        offCount = 0;

        new Thread() {
            public void run() {
                while (true) {
                    if (offCount >= 5) {
                        //tvAutoTraderStatus.setText("연결 끊김");
                        handler.sendMessage(handler.obtainMessage());
                    }
                    offCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private static void setUIForRunning() {
        isUIDisabled = false;
        btnSelectDate.setEnabled(true);
        spnAccount.setEnabled(true);
        btnGetProfit.setEnabled(true);
    }

    private void setUIForStop() {
        isUIDisabled = true;
        btnSelectDate.setEnabled(false);
        spnAccount.setEnabled(false);
        btnGetProfit.setEnabled(false);
    }

    public static void setMqttServerStatus(String status)
    {
        tvMsgServerStatus.setText(status);
    }
    public static void resetTraderStatusCount()
    {
        offCount = 0;
        tvAutoTraderStatus.setText("연결됨");

        if (isUIDisabled)
        {
            setUIForRunning();
        }
    }


    public static void strategySetting(int position)
    {
        Dialog settingDialog = new Dialog(btnAccChange.getContext());
        settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingDialog.setContentView(R.layout.settting_dialog);

        settingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        settingDialog.show();
        settingDialog.findViewById(R.id.llGeneralSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, GeneralSettingActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llEnterBuySetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, IndicatorSettingActivity.class);
                intent.putExtra("type", "enter_buy");
                intent.putExtra("position", position);
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llEnterSellSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, IndicatorSettingActivity.class);
                intent.putExtra("type", "enter_sell");
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llClearBuySetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, IndicatorSettingActivity.class);
                intent.putExtra("type", "clear_buy");
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llClearSellSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, IndicatorSettingActivity.class);
                intent.putExtra("type", "clear_sell");
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });




//        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
//
//        // 위젯 연결 방식은 각자 취향대로~
//        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
//        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
//        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
//
//        // 아니오 버튼
//        Button noBtn = settingDialog.findViewById(R.id.noBtn);
//        noBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getB
//                // 원하는 기능 구현
//                dilaog01.dismiss(); // 다이얼로그 닫기
//            }
//        });
//        // 네 버튼
//        dilaog01.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 원하는 기능 구현
//                finish();           // 앱 종료
//            }
//        });




    }

    public static void strategyRemove(int position)
    {
        // this line adds the data of your EditText and puts in your array
        strategyNameList.remove(position);
        // next thing you have to do is check if your adapter has changed
        adapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > this.backpressedTime + 2000) {
            this.backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= this.backpressedTime + 2000) {
            finish();
        }
    }

}
package com.example.kiwoomautotrader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class MainActivity extends AppCompatActivity implements LifecycleObserver {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();

    public static Context mContext;

    private static String trade_mode;

    private static TextView tvMsgServerStatus;
    private static TextView tvAutoTraderStatus;
    private static Button btnTradeTypeChange;
    private static Button btnScodeChange;
    private static Button btnAccChange;

    private static Button btnProfitHide;
    private static LinearLayout llProfit;

    private static TextView tvDateInput;
    private static Button btnSelectDate;
    private static Spinner spnAccount;
    private static Button btnGetProfit;

    private static EditText etTradeProfit;
    private static EditText etFee;
    private static EditText etTotalProfit;

    private static Button btnAllClearStopRun;
    private static EditText etAllClearTargetProfitTick;
    private static EditText etAllClearTargetLossTick;

    private static EditText etQueueLen;
    private static EditText etIndicatorLen;


    private static TextView tvStrategy;
    private static Button btnAllClear;
    private static Button btnAddStrategy;
    private static ListView list;

    private static ArrayList<String> strategyNameList;
    private static ArrayList<String> accountList;
    private static MainCustomAdapter adapter;

    private static int autoTraderOffCount;

    private static boolean runningStateFlag;
    private static boolean jsonRequireFlag;

    private final Handler handlerForStop = new Handler() {
        public void handleMessage(Message msg) {
            tvAutoTraderStatus.setText("연결 끊김");
            setUIForStop();
            isClientAndServerConnected = false;
            jsonRequireFlag = true;
        }
    };

    private final Handler handlerForRunning = new Handler() {
        public void handleMessage(Message msg) {

            tvAutoTraderStatus.setText("연결됨");
            if (jsonRequireFlag)
                AWSMQTTClient.requestJsonState();
            setUIForRunning();
            isClientAndServerConnected = true;
            jsonRequireFlag = false;
        }
    };


    private static boolean isClientAndServerConnected;
    private static AWSMQTT AWSMQTTClient;
    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private long backpressedTime = 0;



    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

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
            Intent intent = new Intent(getApplicationContext(), KiwoomIDAndScodeSettingActivity.class);
            intent.putExtra("type", "kiwoom_id");
            startActivity(intent);
        }

        if (sharedPreferences.getString("sCode", null) == null)
        {
            finish();
            Intent intent = new Intent(getApplicationContext(), KiwoomIDAndScodeSettingActivity.class);
            intent.putExtra("type", "sCode");
            startActivity(intent);
        }

        trade_mode = sharedPreferences.getString("trade_mode", "test");

        tvMsgServerStatus = (TextView) findViewById(R.id.etStrategyName);
        tvAutoTraderStatus = (TextView) findViewById(R.id.tvAutoTraderStatus);

        btnProfitHide = (Button) findViewById(R.id.btnProfitHide);
        llProfit = (LinearLayout) findViewById(R.id.llProfit);

        if (sharedPreferences.getBoolean("profit_hidden", false)) {
            btnProfitHide.setText("보이기");
            llProfit.setVisibility(View.GONE);
        }

        else
        {
            btnProfitHide.setText("숨기기");
            llProfit.setVisibility(View.VISIBLE);
        }


        btnTradeTypeChange = (Button) findViewById(R.id.btnTradeTypeChange);
        btnScodeChange = (Button) findViewById(R.id.btnScodeChange);
        btnAccChange = (Button) findViewById(R.id.btnAccChange);

        tvDateInput = (TextView) findViewById(R.id.tvDateInput);
        btnSelectDate = (Button) findViewById(R.id.btnSelectDate);
        spnAccount = (Spinner) findViewById(R.id.spnAccount);
        btnGetProfit = (Button) findViewById(R.id.btnGetProfit);

        etTradeProfit = (EditText) findViewById(R.id.etTradeProfit);
        etFee = (EditText) findViewById(R.id.etFee);
        etTotalProfit = (EditText) findViewById(R.id.etTotalProfit);

        btnAllClearStopRun = (Button) findViewById(R.id.btnAllClearStopRun);
        etAllClearTargetProfitTick = (EditText) findViewById(R.id.etAllClearTargetProfitTick);
        etAllClearTargetLossTick = (EditText) findViewById(R.id.etAllClearTargetLossTick);

        etQueueLen = (EditText) findViewById(R.id.etQueueLen);
        etIndicatorLen = (EditText) findViewById(R.id.etIndicatorLen);

        tvStrategy = (TextView) findViewById(R.id.tvStrategy);
        btnAllClear = (Button) findViewById(R.id.btnAllClear);
        btnAddStrategy = (Button) findViewById(R.id.btnAddStrategy);
        list = (ListView) findViewById(R.id.listTime);


        if (sharedPreferences.getString("trade_mode", "test").equals("test"))
            btnTradeTypeChange.setText("실전투자");
        else
            btnTradeTypeChange.setText("모의투자");


        btnAddStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strategyNameList.add("새로운 전략");
                try {

                    JSONObject newStrategy = new JSONObject();

                    newStrategy.put("strategy_name", "새로운 전략");
                    newStrategy.put("running_status", "false");
                    newStrategy.put("is_simulation", "false");
                    newStrategy.put("trade_account", spnAccount.getSelectedItem().toString());
                    newStrategy.put("max_loss", "");
                    newStrategy.put("max_profit", "");
                    newStrategy.put("trade_time", new JSONArray());
                    newStrategy.put("quant", new JSONArray());
                    newStrategy.put("enter_buy", new JSONArray());
                    newStrategy.put("enter_sell", new JSONArray());
                    newStrategy.put("clear_buy", new JSONArray());
                    newStrategy.put("clear_sell", new JSONArray());
                    newStrategy.put("ai_clear", new JSONArray());

                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));

                    jsonObject.getJSONArray("strategy_list").put(newStrategy);

                    jsonObject.put("command", "json_update_require_from_android");
                    AWSMQTTClient.publish(jsonObject.toString());

                    try {
                        Thread.sleep(1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONObject temp = new JSONObject();
                    temp.put("command", "add_strategy_from_android");

                    AWSMQTTClient.publish(temp.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                adapter.notifyDataSetChanged();
            }
        });

        btnProfitHide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (sharedPreferences.getBoolean("profit_hidden", false)) {
                        btnProfitHide.setText("숨기기");
                        llProfit.setVisibility(View.VISIBLE);
                        editor.putBoolean("profit_hidden", false);
                    }
                    else
                    {
                        btnProfitHide.setText("보이기");
                        llProfit.setVisibility(View.GONE);
                        editor.putBoolean("profit_hidden", true);
                    }

                    editor.commit();

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e(LOG_TAG, "btnProfitHide error", e);
                }
            }
        });


        btnTradeTypeChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    runningStateFlag = false;
                    AWSMQTTClient.disconnect();

                    if (sharedPreferences.getString("trade_mode", "test").equals("test"))
                        editor.putString("trade_mode", "real");

                    else
                        editor.putString("trade_mode", "test");

                    editor.commit();
                    finish();
                    startActivity(getIntent());

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Account change error", e);
                }
            }
        });

        btnScodeChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    runningStateFlag = false;
                    AWSMQTTClient.disconnect();
                    Intent intent = new Intent(getApplicationContext(), KiwoomIDAndScodeSettingActivity.class);
                    intent.putExtra("type", "sCode");
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Account change error", e);
                }
            }
        });

        btnAccChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    runningStateFlag = false;
                    AWSMQTTClient.disconnect();
                    Intent intent = new Intent(getApplicationContext(), KiwoomIDAndScodeSettingActivity.class);
                    intent.putExtra("type", "kiwoom_id");
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Account change error", e);
                }
            }
        });

        btnGetProfit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    String selectedAcc = spnAccount.getSelectedItem().toString();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("command", "get_profit_from_android");
                    jsonObject.put("acc_num", selectedAcc);
                    jsonObject.put("time", tvDateInput.getText().toString().replaceAll("-",""));

                    AWSMQTTClient.publish(jsonObject.toString());
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        });

        btnAllClearStopRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                                    if (jsonObject.getJSONObject("profit_total_clear").getString("is_running").equals("true"))
                                        jsonObject.getJSONObject("profit_total_clear").put("is_running", "false");
                                    else
                                        jsonObject.getJSONObject("profit_total_clear").put("is_running", "true");

                                    jsonObject.getJSONObject("profit_total_clear").put("profit", etAllClearTargetProfitTick.getText());
                                    jsonObject.getJSONObject("profit_total_clear").put("loss", etAllClearTargetLossTick.getText());

                                    jsonObject.put("command", "json_update_require_from_android");
                                    AWSMQTTClient.publish(jsonObject.toString());

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
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String dialogMessage = "";
                if (btnAllClearStopRun.getText().equals("중지"))
                {
                    dialogMessage = "종합 수익 청산을 중지하시겠습니까?";
                }
                else
                {
                    dialogMessage = "종합 수익 청산을 시작하시겠습니까?";
                }
                builder.setMessage(dialogMessage).setPositiveButton("예", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();

            }
        });

        btnAllClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                JSONObject jsonObject = null;
                                try {
                                    Toast.makeText(MainActivity.mContext,"일괄 청산중...", Toast.LENGTH_SHORT).show();

                                    jsonObject = new JSONObject();

                                    jsonObject.put("command", "clear_all_position_from_android");

                                    AWSMQTTClient.publish(jsonObject.toString());

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
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("모든 전략을 청산하시겠습니까?").setPositiveButton("예", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();

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

        if (trade_mode.equals("test"))
            setTitle("ID : " + sharedPreferences.getString("kiwoom_id", null) + "   종목코드 : " + sharedPreferences.getString("sCode", null) + " - 모의");
        else
            setTitle("ID : " + sharedPreferences.getString("kiwoom_id", null) + "   종목코드 : " + sharedPreferences.getString("sCode", null) + " - 실전");

        strategyNameList = new ArrayList<String>();

        adapter = new MainCustomAdapter(MainActivity.this, R.layout.activity_main_strategy_row_item, strategyNameList);
        list.setAdapter(adapter);


        AWSMQTTClient = new AWSMQTT(getApplicationContext());

        autoTraderConnectionCheck();

        tvMsgServerStatus.setText("연결 끊김");
        tvAutoTraderStatus.setText("연결 끊김");
        setUIForStop();


        try {

            AWSMQTTClient.disconnect();
            AWSMQTTClient.connect();

        } catch (Exception e) {
            Log.e("AWSMQTTClient reconnect error", e.toString());
        }


    }

    @SuppressLint("LongLogTag")
    public void onResume() {
        MainActivity.super.onResume();
        if(AWSMQTTClient.isConnected())
            AWSMQTTClient.requestJsonState();
    }



    public static void setStrategyList(JSONObject jsonObject)
    {
        strategyNameList.removeAll(strategyNameList);
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("strategy_list");

            for(int i = 0; i < jsonArray.length(); i++)
            {
                strategyNameList.add(jsonArray.getJSONObject(i).getString("strategy_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

    }

    public static void setAllClear(JSONObject jsonObject)
    {
        try {
            if (jsonObject.getString("is_running").equals("true"))
            {
                btnAllClearStopRun.setText("중지");
                btnAllClearStopRun.setBackgroundColor(Color.RED);
                etAllClearTargetProfitTick.setEnabled(false);
                etAllClearTargetLossTick.setEnabled(false);
            }
            else
            {
                btnAllClearStopRun.setText("시작");
                btnAllClearStopRun.setBackgroundColor(Color.GREEN);
                etAllClearTargetProfitTick.setEnabled(true);
                etAllClearTargetLossTick.setEnabled(true);
            }

            etAllClearTargetProfitTick.setText(jsonObject.getString("profit"));
            etAllClearTargetLossTick.setText(jsonObject.getString("loss"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void autoTraderConnectionCheck() {
        autoTraderOffCount = 10;
        runningStateFlag = true;


        new Thread() {
            public void run() {
                while (true) {
                    if (!runningStateFlag)
                        return;

                    if (autoTraderOffCount >= 4) {
                        handlerForStop.sendMessage(handlerForStop.obtainMessage());
                    }
                    else
                    {
                        handlerForRunning.sendMessage(handlerForRunning.obtainMessage());
                    }
                    autoTraderOffCount++;
                    try {
                        Thread.sleep(1000);
                        if (AWSMQTTClient.isConnected()) {
                            JSONObject jsonObject = new JSONObject();

                            jsonObject.put("command", "length_require_from_android");
                            AWSMQTTClient.publish(jsonObject.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private static void setUIForRunning() {
        btnSelectDate.setEnabled(true);
        btnAddStrategy.setEnabled(true);
        spnAccount.setEnabled(true);
        btnGetProfit.setEnabled(true);
        btnAllClearStopRun.setEnabled(true);
        btnAllClear.setEnabled(true);
        list.setVisibility(View.VISIBLE);
    }

    private static void setUIForStop() {
        btnSelectDate.setEnabled(false);
        btnAddStrategy.setEnabled(false);
        spnAccount.setEnabled(false);
        btnGetProfit.setEnabled(false);
        btnAllClearStopRun.setEnabled(false);
        etAllClearTargetProfitTick.setEnabled(false);
        etAllClearTargetLossTick.setEnabled(false);
        btnAllClear.setEnabled(false);
        list.setVisibility(View.INVISIBLE);
    }

    public static void setAcc(JSONArray jsonArr) {
        accountList = new ArrayList<String>();
        if (jsonArr != null) {
            int len = jsonArr.length();
            for (int i=0;i<len;i++){
                try {
                    accountList.add(jsonArr.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spnAccount.getContext(), android.R.layout.simple_spinner_item, accountList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccount.setAdapter(adapter);

    }


    public static void setProfit(JSONObject jsonObject) {
        try {
            etTradeProfit.setText(jsonObject.getString("trade_profit"));
            etFee.setText(jsonObject.getString("fee"));
            etTotalProfit.setText(jsonObject.getString("total_profit"));

            Toast toast = Toast.makeText(etTradeProfit.getContext(),"조회 완료!", Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setLengthInfo(JSONObject jsonObject) {
        try {
            etQueueLen.setText(jsonObject.getString("queue_length"));
            etIndicatorLen.setText(jsonObject.getString("calc_indicators_length"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static void setMqttServerStatus(String status)
    {
        tvMsgServerStatus.setText(status);
    }
    public static void resetTraderStatusCount()
    {
            autoTraderOffCount = 0;
    }


    public static void strategySetting(int position, boolean isRunning, String strategyName)
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
                intent.putExtra("is_running", isRunning);
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
                intent.putExtra("is_running", isRunning);
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
                intent.putExtra("position", position);
                intent.putExtra("is_running", isRunning);
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
                intent.putExtra("position", position);
                intent.putExtra("is_running", isRunning);
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
                intent.putExtra("position", position);
                intent.putExtra("is_running", isRunning);
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llAIClearSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                Intent intent = new Intent(mContext, IndicatorSettingActivity.class);
                intent.putExtra("type", "ai_clear");
                intent.putExtra("position", position);
                intent.putExtra("is_running", isRunning);
                intent.putExtra("strategy_name", strategyNameList.get(position));
                mContext.startActivity(intent);
            }
        });

        settingDialog.findViewById(R.id.llClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                strategyClear(position);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(strategyName + " 전략을 청산하시겠습니까?").setPositiveButton("청산", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();

            }
        });

        settingDialog.findViewById(R.id.llDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (isRunning)
                                    Toast.makeText(MainActivity.mContext,"전략을 중지 후 삭제해주세요!", Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(MainActivity.mContext, "삭제중..", Toast.LENGTH_SHORT).show();
                                    JSONObject temp = new JSONObject();
                                    try {
                                        temp.put("command", "remove_strategy_from_android");
                                        temp.put("position", position);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    MainActivity.publishMsg(temp.toString());
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(strategyName + " 전략을 삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();
            }
        });

        settingDialog.findViewById(R.id.llCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog.dismiss();

                JSONObject jsonObject;
                JSONObject copyObject;
                try {
                    jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                    copyObject = jsonObject.getJSONArray("strategy_list").getJSONObject(position);
                    copyObject.put("running_status", "false");

                    jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));

                    jsonObject.getJSONArray("strategy_list").put(copyObject);

                    jsonObject.put("command", "json_update_require_from_android");
                    AWSMQTTClient.publish(jsonObject.toString());

                    try {
                        Thread.sleep(1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONObject temp = new JSONObject();
                    temp.put("command", "add_strategy_from_android");

                    AWSMQTTClient.publish(temp.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackground() {
        Log.d(LOG_TAG, "App in background");
        runningStateFlag = false;
        AWSMQTTClient.disconnect();

        finishAndRemoveTask();
    }

    public static void strategyStopRun(int position)
    {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            JSONObject finalJsonObject = jsonObject;
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                if (finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("running_status").equals("false"))
                                {
                                    if (finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("strategy_name").equals("") ||
                                            finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("max_loss").equals("") ||
                                            finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("max_profit").equals("") ||
                                            finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_buy").length() == 0 ||
                                            finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_sell").length() == 0 ||
                                            (finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("ai_clear").length() == 0 &&
                                            (finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("clear_buy").length() == 0 ||
                                             finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("clear_sell").length() == 0)))
                                    {
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:

                                                        break;

                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setMessage("모든 값을 입력후 시작해주세요!").setPositiveButton("확인", dialogClickListener).show();
                                    } else{
                                        int numOfVirtualTrade = 0;

                                        boolean containOthersEnterBuy = false;
                                        for(int i = 0; i < finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_buy").length(); i++)
                                        {
                                            if (!finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_buy").getJSONObject(i).getString("name").contains("가상매매")) {
                                                containOthersEnterBuy = true;

                                            }
                                            else
                                                numOfVirtualTrade ++;

                                        }

                                        boolean containOthersEnterSell = false;
                                        for(int i = 0; i < finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_sell").length(); i++)
                                        {
                                            if (!finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).getJSONArray("enter_sell").getJSONObject(i).getString("name").contains("가상매매")) {
                                                containOthersEnterSell = true;

                                            }
                                            else
                                                numOfVirtualTrade ++;
                                        }

                                        if (numOfVirtualTrade > 1)
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    switch (which) {
                                                        case DialogInterface.BUTTON_POSITIVE:

                                                            break;

                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setMessage("가상매매 지표는 하나만 사용해주세요!").setPositiveButton("확인", dialogClickListener).show();
                                        }

                                        else if (containOthersEnterBuy && containOthersEnterSell)
                                        {
                                            finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).put("running_status", "true");
                                            finalJsonObject.put("command", "json_update_require_from_android");
                                            AWSMQTTClient.publish(finalJsonObject.toString());
                                        }

                                        else
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    switch (which) {
                                                        case DialogInterface.BUTTON_POSITIVE:

                                                            break;

                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setMessage("가상매매 지표외 다른 지표를 추가해주세요!").setPositiveButton("확인", dialogClickListener).show();
                                        }
                                    }



                                } else {
                                    finalJsonObject.getJSONArray("strategy_list").getJSONObject(position).put("running_status", "false");
                                    finalJsonObject.put("command", "json_update_require_from_android");
                                    AWSMQTTClient.publish(finalJsonObject.toString());
                                }


                                Log.d(LOG_TAG, sharedPreferences.getString("strategy_json", null));

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                adapter.notifyDataSetChanged();

                                break;
                            } catch (Exception e)
                            {

                            }

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            String dialogMessage = "";
            if (jsonObject.getJSONArray("strategy_list").getJSONObject(position).getString("running_status").equals("false"))
            {
                dialogMessage = " 전략을 시작하시겠습니까?";
            }
            else
            {
                dialogMessage = " 전략을 중지하시겠습니까?";
            }
            builder.setMessage(strategyNameList.get(position) + dialogMessage).setPositiveButton("예", dialogClickListener)
                    .setNegativeButton("취소", dialogClickListener).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void strategyClear(int position)
    {
        Toast.makeText(MainActivity.mContext,"청산중...", Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("command", "clear_position_require_from_android");
            jsonObject.put("position", String.valueOf(position));

            AWSMQTTClient.publish(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setClearAllResult(JSONObject result)
    {
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(spnAccount.getContext());
            builder.setMessage("청산한 전략 수 : " + String.valueOf(result.get("num_of_clear")) + '개').setPositiveButton("확인", dialogClickListener).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public static void strategyRemove(JSONObject result)
    {
        try {
            if (result.getInt("result") == 0)
            {
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                jsonObject.getJSONArray("strategy_list").remove(result.getInt("position"));

                jsonObject.put("command", "json_update_require_from_android");

                AWSMQTTClient.publish(jsonObject.toString());

                Toast.makeText(MainActivity.mContext,"삭제완료!", Toast.LENGTH_SHORT).show();

                // this line adds the data of your EditText and puts in your array
                strategyNameList.remove(result.getInt("position"));
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();
            }
            else
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
                AlertDialog.Builder builder = new AlertDialog.Builder(spnAccount.getContext());
                builder.setMessage("해당 전략은 포지션을 보유중 입니다.\n청산 후 삭제해주세요.").setPositiveButton("확인", dialogClickListener).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> getAccountList()
    {
        return accountList;
    }

    public static boolean getClientAndServerConnectionStatus()
    {
        return isClientAndServerConnected;
    }

    public static void publishMsg(String msg) {
        AWSMQTTClient.publish(msg);
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
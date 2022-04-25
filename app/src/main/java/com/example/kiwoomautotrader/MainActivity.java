package com.example.kiwoomautotrader;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();

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

    private ArrayList<String> strategyList;
    private CustomAdapter adapter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("kiwoom_id", null) == null)
        {
            Intent intent = new Intent(getApplicationContext(), KiwoomIDSettingActivity.class);
            startActivity(intent);
        }

        String serverType = "";

        tvMsgServerStatus = (TextView) findViewById(R.id.tvMsgServerStatus);
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
        ArrayList<String> strategyList = new ArrayList<String>();
        for(int i=0;i<10;i++) {
            strategyList.add("Test" + i);
        }

        adapter = new CustomAdapter(MainActivity.this,R.layout.row_item, strategyList);
        list.setAdapter(adapter);


//        strategyList = new ArrayList<String>();
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, strategyList);
//
//        // Here, you set the data in your ListView
//        list.setAdapter(adapter);

        btnAddStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this line adds the data of your EditText and puts in your array
                adapter.add("hello world");
                // next thing you have to do is check if your adapter has changed
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
                    finish();
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
}
package com.example.kiwoomautotrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeneralSettingActivity extends AppCompatActivity {

    private int strategyPosition;
    private String strategyName;
    private EditText etStrategyName;
    private EditText etCode;

    private Button btnSave;

    private Spinner spnAccount;
    private CheckBox cbSimulation;

    private EditText etMaxLoss;
    private EditText etMaxProfit;
    private EditText etStartHour;
    private EditText etStartMin;
    private EditText etEndHour;
    private EditText etEndMin;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_setting);

        Intent intent = getIntent();

        strategyPosition = intent.getIntExtra("position", 0);
        strategyName = intent.getStringExtra("strategy_name");

        setTitle(strategyName + " 기본 설정");

        etStrategyName = (EditText) findViewById(R.id.etStrategyName);
        etCode = (EditText) findViewById(R.id.etCode);

        btnSave = (Button) findViewById(R.id.btnSave);

        spnAccount = (Spinner) findViewById(R.id.spnAccount);
        cbSimulation = (CheckBox) findViewById(R.id.cbSimulation);

        etMaxLoss = (EditText) findViewById(R.id.etMaxLoss);
        etMaxProfit = (EditText) findViewById(R.id.etMaxProfit);
        etStartHour = (EditText) findViewById(R.id.etStartHour);
        etStartMin = (EditText) findViewById(R.id.etStartMin);
        etEndHour = (EditText) findViewById(R.id.etEndHour);
        etEndMin = (EditText) findViewById(R.id.etEndMin);

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etStrategyName.getText().toString().equals("") || etCode.getText().toString().equals("") || etMaxLoss.getText().toString().equals("") || etMaxProfit.getText().toString().equals("") ||
                        etStartHour.getText().toString().equals("") || etStartMin.getText().toString().equals("") || etEndHour.getText().toString().equals("") || etEndMin.getText().toString().equals(""))
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

                        temp_jsonObject.put("sCode", etCode.getText().toString());

                        if (cbSimulation.isChecked())
                            temp_jsonObject.put("is_simulation", "true");
                        else
                            temp_jsonObject.put("is_simulation", "false");

                        temp_jsonObject.put("max_loss", etMaxLoss.getText().toString());
                        temp_jsonObject.put("max_profit", etMaxProfit.getText().toString());

                        temp_jsonObject.put("start_hour", etStartHour.getText().toString());
                        temp_jsonObject.put("start_min", etStartMin.getText().toString());

                        temp_jsonObject.put("end_hour", etEndHour.getText().toString());
                        temp_jsonObject.put("end_min", etEndMin.getText().toString());

                        jsonArray.put(strategyPosition, temp_jsonObject);
                        jsonObject.put("strategy_list", jsonArray);

                        editor.putString("strategy_json", jsonObject.toString());
                        editor.commit();

                        Toast myToast = Toast.makeText(btnSave.getContext(),"저장이 완료되었습니다!", Toast.LENGTH_SHORT);
                        myToast.show();

                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadValue();
    }

    private void loadValue()
    {
        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            JSONArray jsonArray = jsonObject.getJSONArray("strategy_list");

            jsonObject = jsonArray.getJSONObject(strategyPosition);

            etStrategyName.setText(jsonObject.getString("strategy_name"));
            etCode.setText(jsonObject.getString("sCode"));

            if (jsonObject.getString("is_simulation").equals("false"))
                cbSimulation.setChecked(false);
            else
                cbSimulation.setChecked(true);

            ArrayList arrayList = new ArrayList<>();
            arrayList.add("1111-1111");
            arrayList.add("2222-2222");
            arrayList.add("3333-3333");
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(spnAccount.getContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);


            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccount.setAdapter(arrayAdapter);

            etMaxLoss = (EditText) findViewById(R.id.etMaxLoss);
            etMaxProfit = (EditText) findViewById(R.id.etMaxProfit);
            etStartHour = (EditText) findViewById(R.id.etStartHour);
            etStartMin = (EditText) findViewById(R.id.etStartMin);
            etEndHour = (EditText) findViewById(R.id.etEndHour);
            etEndMin = (EditText) findViewById(R.id.etEndMin);

            etMaxLoss.setText(jsonObject.getString("max_loss"));
            etMaxProfit.setText(jsonObject.getString("max_profit"));
            etStartHour.setText(jsonObject.getString("start_hour"));
            etStartMin.setText(jsonObject.getString("start_min"));
            etEndHour.setText(jsonObject.getString("end_hour"));
            etEndMin.setText(jsonObject.getString("end_min"));
        } catch (JSONException e) {
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


package com.example.kiwoomautotrader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class IndicatorSettingActivity extends AppCompatActivity {
    static final String LOG_TAG = IndicatorSettingActivity.class.getCanonicalName();

    private long backpressedTime = 0;
    private String type;
    private int strategyPosition;
    private Button btnAdd;
    private Button btnSave;
    private ArrayList<String> IndicatorNameList;
    private int typeCounter;

    private ListView IndicatorList;
    private ArrayList<IndicatorItem> IndicatorItemList;
    private ArrayList<String> indicatorInfo;
    private boolean isAllFilledUp;
    private IndicatorCustomAdapter customAdapter;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator_setting);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        strategyPosition = intent.getIntExtra("position", 0);
        String strategyName = intent.getStringExtra("strategy_name");


        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        if (type.equals("enter_buy"))
            setTitle(strategyName + " 매수진입 설정");
        else if (type.equals("enter_sell"))
            setTitle(strategyName + " 매도진입 설정");
        else if (type.equals("clear_buy"))
            setTitle(strategyName + " 매수청산 설정");
        else if (type.equals("clear_sell"))
            setTitle(strategyName + " 매도청산 설정");

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSave = (Button) findViewById(R.id.btnSave);
        IndicatorList = (ListView) findViewById(R.id.indicatorList);


        typeCounter = 0;



        if (type.equals("clear_buy") || type.equals("clear_sell"))
            IndicatorNameList = new ArrayList<>(Arrays.asList("청산 기본 지표", "기준선-배열/거리", "기준선-크로스", "기준선-직전봉", "기준선-현재가", "파라볼릭", "RSI"
                    , "직전봉-현재가", "직전봉의 상태값", "현재봉의 상태값", "가격지표", "최근 n개봉"));
        else
            IndicatorNameList = new ArrayList<>(Arrays.asList("기준선-배열/거리", "기준선-크로스", "기준선-직전봉", "기준선-현재가", "파라볼릭", "RSI"
                    , "직전봉-현재가", "직전봉의 상태값", "현재봉의 상태값", "가격지표", "최근 n개봉", "가상매매지표 (먼저만족)", "가상매매지표 (모두만족)"
                    , "가상매매지표 (박스권체크)"));

        IndicatorItemList = new ArrayList<>();


        customAdapter = new IndicatorCustomAdapter(IndicatorSettingActivity.this,R.layout.activity_main_strategy_row_item, IndicatorItemList, type, strategyPosition);
        IndicatorList.setAdapter(customAdapter);

        loadIndicator();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog strategyAddDialog = new Dialog(btnAdd.getContext());
                strategyAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                strategyAddDialog.setContentView(R.layout.enter_add_dialog);
                strategyAddDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                ListView enterAddDialoglist = strategyAddDialog.findViewById(R.id.enterAddDialoglist);
                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(btnAdd.getContext(), R.layout.custom_listview_item_layout, IndicatorNameList);
                enterAddDialoglist.setAdapter(itemsAdapter);
                enterAddDialoglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        addStrategy(IndicatorNameList.get(position));
                        strategyAddDialog.dismiss();
                    }
                });

                strategyAddDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, sharedPreferences.getString("strategy_json", null));
                try {
                    isAllFilledUp = true;
                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < IndicatorItemList.size(); i++) {
                        indicatorInfo = new ArrayList<>();
                        isEverythingFilled(IndicatorItemList.get(i).getLinearLayout());

                        if (!isAllFilledUp)
                            break;
                        else
                            saveIndicator(i);
                    }


                    if (isAllFilledUp) {
                        Toast myToast = Toast.makeText(btnSave.getContext(), "저장이 완료되었습니다!", Toast.LENGTH_SHORT);
                        myToast.show();
                        finish();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(btnSave.getContext());
                        builder.setMessage("모든 값을 입력해주세요!").setPositiveButton("확인", dialogClickListener).show();
                    }

                    for (int i = 0; i < IndicatorItemList.size(); i++) {
                        Log.d(LOG_TAG, "name : " + IndicatorItemList.get(i).getName());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void isEverythingFilled(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                isEverythingFilled((ViewGroup) view);
            else if (view instanceof EditText) {
                EditText edittext = (EditText) view;
                if (edittext.getText().toString().equals("")) {
                    isAllFilledUp = false;
                }
                else
                {
                    indicatorInfo.add(edittext.getText().toString());
                }
            }
            else if (view instanceof RadioButton) {
                RadioButton rb = (RadioButton) view;
                if (rb.isChecked())
                    indicatorInfo.add("true");
                else
                    indicatorInfo.add("false");
            }

        }
    }


    private void addStrategy(String name)
    {
        IndicatorItemList.add(new IndicatorItem(name, typeCounter));
        typeCounter ++;
        //enterStrategyLayoutList.add(new LinearLayout(btnAdd.getContext()));
        customAdapter.notifyDataSetChanged();
    }
    private void saveIndicator(int i)
    {
        try {
            JSONObject newIndicator = new JSONObject();
            switch (IndicatorItemList.get(i).getName())
            {
                case "기준선-배열/거리":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());
                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("time_type", "real");
                    else
                        newIndicator.put("time_type", "prev");

                    if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("left_indicator_time_type", "tick");
                    else
                        newIndicator.put("left_indicator_time_type", "min");

                    newIndicator.put("left_indicator_unit", indicatorInfo.get(4));

                    if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("left_indicator_type", "ma");
                    else
                        newIndicator.put("left_indicator_type", "tema");

                    newIndicator.put("left_indicator_period", indicatorInfo.get(7));


                    if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("right_indicator_time_type", "tick");
                    else
                        newIndicator.put("right_indicator_time_type", "min");

                    newIndicator.put("right_indicator_unit", indicatorInfo.get(10));

                    if(indicatorInfo.get(11).equals("true"))
                        newIndicator.put("right_indicator_type", "ma");
                    else
                        newIndicator.put("right_indicator_type", "tema");

                    newIndicator.put("right_indicator_period", indicatorInfo.get(13));
                    newIndicator.put("tick_diff_from", indicatorInfo.get(14));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(15));

                    break;
                case "기준선-크로스":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());
                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("time_type", "real");
                    else
                        newIndicator.put("time_type", "prev");

                    if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("left_indicator_time_type", "tick");
                    else
                        newIndicator.put("left_indicator_time_type", "min");

                    newIndicator.put("left_indicator_unit", indicatorInfo.get(4));

                    if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("left_indicator_type", "ma");
                    else
                        newIndicator.put("left_indicator_type", "tema");

                    newIndicator.put("left_indicator_period", indicatorInfo.get(7));


                    if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("right_indicator_time_type", "tick");
                    else
                        newIndicator.put("right_indicator_time_type", "min");

                    newIndicator.put("right_indicator_unit", indicatorInfo.get(10));

                    if(indicatorInfo.get(11).equals("true"))
                        newIndicator.put("right_indicator_type", "ma");
                    else
                        newIndicator.put("right_indicator_type", "tema");

                    newIndicator.put("right_indicator_period", indicatorInfo.get(13));

                    if(indicatorInfo.get(14).equals("true"))
                        newIndicator.put("cross_type", "golden_cross");
                    else
                        newIndicator.put("cross_type", "dead_cross");

                    break;
                case "기준선-직전봉":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());
                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("time_type", "real");
                    else
                        newIndicator.put("time_type", "prev");

                    if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(4));

                    if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("indicator_type", "ma");
                    else
                        newIndicator.put("indicator_type", "tema");

                    newIndicator.put("indicator_period", indicatorInfo.get(7));


                    if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("ohcl_type", "open");
                    else if(indicatorInfo.get(9).equals("true"))
                        newIndicator.put("ohcl_type", "high");
                    else if(indicatorInfo.get(10).equals("true"))
                        newIndicator.put("ohcl_type", "low");
                    else
                        newIndicator.put("ohcl_type", "close");

                    newIndicator.put("tick_diff_from", indicatorInfo.get(12));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(13));
                    break;
                case "기준선-현재가":

                    newIndicator.put("name", IndicatorItemList.get(i).getName());
                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("time_type", "real");
                    else
                        newIndicator.put("time_type", "prev");

                    if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(4));

                    if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("indicator_type", "ma");
                    else
                        newIndicator.put("indicator_type", "tema");

                    newIndicator.put("indicator_period", indicatorInfo.get(7));

                    newIndicator.put("tick_diff_from", indicatorInfo.get(8));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(9));
                    break;
                case "파라볼릭":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());
                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("time_type", "real");
                    else
                        newIndicator.put("time_type", "prev");

                    if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(4));

                    newIndicator.put("prabolic_value_one", indicatorInfo.get(5));
                    newIndicator.put("prabolic_value_two", indicatorInfo.get(6));


                    if(indicatorInfo.get(7).equals("true"))
                        newIndicator.put("prabolic_type", "buy");
                    else if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("prabolic_type", "sell");
                    else if(indicatorInfo.get(9).equals("true"))
                        newIndicator.put("prabolic_type", "golden_cross");
                    else
                        newIndicator.put("prabolic_type", "dead_cross");
                    break;
                case "RSI":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(2));

                    newIndicator.put("rsi_period", indicatorInfo.get(3));
                    newIndicator.put("rsi_signal", indicatorInfo.get(4));

                    newIndicator.put("rsi_from", indicatorInfo.get(5));
                    newIndicator.put("rsi_to", indicatorInfo.get(6));

                    break;
                case "직전봉-현재가":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(2));

                    if(indicatorInfo.get(3).equals("true"))
                        newIndicator.put("bar_status", "bull");
                    else if(indicatorInfo.get(4).equals("true"))
                        newIndicator.put("bar_status", "bear");
                    else
                        newIndicator.put("bar_status", "all");



                    if(indicatorInfo.get(6).equals("true"))
                        newIndicator.put("ohcl_type", "open");
                    else if(indicatorInfo.get(7).equals("true"))
                        newIndicator.put("ohcl_type", "high");
                    else if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("ohcl_type", "low");
                    else if(indicatorInfo.get(9).equals("true"))
                        newIndicator.put("ohcl_type", "close");
                    else
                        newIndicator.put("ohcl_type", "middle");

                    newIndicator.put("tick_diff_from", indicatorInfo.get(11));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(12));
                    break;
                case "직전봉의 상태값": case "현재봉의 상태값":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(2));


                    if(indicatorInfo.get(3).equals("true"))
                        newIndicator.put("bar_status", "bull");
                    else if(indicatorInfo.get(4).equals("true"))
                        newIndicator.put("bar_status", "bear");

                    newIndicator.put("tick_diff_from", indicatorInfo.get(5));
                    break;
                case "가격지표":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("first_type", "prev_open");
                    else if(indicatorInfo.get(1).equals("true"))
                        newIndicator.put("first_type", "prev_high");
                    else if(indicatorInfo.get(2).equals("true"))
                        newIndicator.put("first_type", "prev_low");
                    else if(indicatorInfo.get(3).equals("true"))
                        newIndicator.put("first_type", "prev_close");
                    else if(indicatorInfo.get(4).equals("true"))
                        newIndicator.put("first_type", "prev_middle");
                    else if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("first_type", "today_open");
                    else if(indicatorInfo.get(6).equals("true"))
                        newIndicator.put("first_type", "today_high");
                    else if(indicatorInfo.get(7).equals("true"))
                        newIndicator.put("first_type", "today_low");
                    else if(indicatorInfo.get(8).equals("true"))
                        newIndicator.put("first_type", "today_middle");
                    else if(indicatorInfo.get(9).equals("true"))
                        newIndicator.put("first_type", "manual");

                    newIndicator.put("manual_price", indicatorInfo.get(10));

                    newIndicator.put("tick_diff_from", indicatorInfo.get(11));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(12));

                    if(indicatorInfo.get(13).equals("true"))
                        newIndicator.put("second_type", "today_low");
                    else if(indicatorInfo.get(14).equals("true"))
                        newIndicator.put("second_type", "today_high");

                    newIndicator.put("second_numerator", indicatorInfo.get(15));

                    if(indicatorInfo.get(16).equals("true"))
                        newIndicator.put("third_type", "prev_low");
                    else if(indicatorInfo.get(17).equals("true"))
                        newIndicator.put("third_type", "prev_high");

                    newIndicator.put("third_numerator", indicatorInfo.get(18));
                    break;
                case "최근 n개봉":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    if(indicatorInfo.get(0).equals("true"))
                        newIndicator.put("indicator_time_type", "tick");
                    else
                        newIndicator.put("indicator_time_type", "min");

                    newIndicator.put("indicator_unit", indicatorInfo.get(2));
                    newIndicator.put("num_of_bar", indicatorInfo.get(3));

                    if(indicatorInfo.get(4).equals("true"))
                        newIndicator.put("type", "high");
                    else if(indicatorInfo.get(5).equals("true"))
                        newIndicator.put("type", "low");

                    newIndicator.put("tick_diff_from", indicatorInfo.get(6));
                    newIndicator.put("tick_diff_to", indicatorInfo.get(7));
                    break;
                case "가상매매지표 (먼저만족)": case "가상매매지표 (모두만족)":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    newIndicator.put("loss_tick_unit", indicatorInfo.get(0));
                    newIndicator.put("loss_tick_times", indicatorInfo.get(1));
                    newIndicator.put("enter_buy_times", indicatorInfo.get(2));
                    newIndicator.put("enter_sell_times", indicatorInfo.get(3));
                    newIndicator.put("profit_preserve_times", indicatorInfo.get(4));

                    break;
                case "가상매매지표 (박스권체크)":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    newIndicator.put("pending_min", indicatorInfo.get(0));
                    newIndicator.put("enter_buy_times", indicatorInfo.get(1));
                    newIndicator.put("enter_sell_times", indicatorInfo.get(2));
                    break;

                case "청산 기본 지표":
                    newIndicator.put("name", IndicatorItemList.get(i).getName());

                    newIndicator.put("profit_tick", indicatorInfo.get(0));
                    newIndicator.put("loss_tick", indicatorInfo.get(1));
                    newIndicator.put("total_profit_tick", indicatorInfo.get(2));
                    newIndicator.put("total_loss_tick", indicatorInfo.get(2));
                    break;

            }

            try {
                JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
                JSONArray jsonArray;

                if (type.equals("enter_buy"))
                    jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_buy");
                else if (type.equals("enter_sell"))
                    jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_sell");
                else if (type.equals("clear_buy"))
                    jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_buy");
                else
                    jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_sell");

                jsonArray.put(i, newIndicator);

                editor.putString("strategy_json", jsonObject.toString());
                editor.commit();

                Log.d(LOG_TAG, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIndicator() {
        try {
            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));
            JSONArray jsonArray;
            if (type.equals("enter_buy"))
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_buy");
            else if (type.equals("enter_sell"))
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_sell");
            else if (type.equals("clear_buy"))
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_buy");
            else
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_sell");

            for(int i = 0; i < jsonArray.length(); i++)
            {
                addStrategy(jsonArray.getJSONObject(i).get("name").toString());
            }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(btnAdd.getContext());
        builder.setMessage("저장을 안하고 뒤로 가시겠습니까?").setPositiveButton("예", dialogClickListener)
                .setNegativeButton("취소", dialogClickListener).show();
    }

//    private void addStrategy(String name)
//    {
//
//        LinearLayout linearLayout = new LinearLayout(btnAdd.getContext());
//        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParam.setMargins(0, 0, 0, 20);
//        layoutParam.gravity = CENTER;
//        linearLayout.setLayoutParams(layoutParam);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//        switch (name)
//        {
//            case "기준선-배열/거리":
//                RadioGroup rg;
//                rg = new RadioGroup(btnAdd.getContext());
//                rg.setLayoutParams(layoutParam);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                RadioButton rb1 = new RadioButton(btnAdd.getContext());
//                rb1.setLayoutParams(layoutParam);
//                rb1.setText("실시간");
//                RadioButton rb2 = new RadioButton(btnAdd.getContext());
//                rb2.setLayoutParams(layoutParam);
//                rb2.setText("직전봉");
//                rg.addView(rb1);
//                rg.addView(rb2);
//                linearLayout.addView(rg);
//
//                LinearLayout tempLinearLayout = new LinearLayout(btnAdd.getContext());
//                tempLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                tempLinearLayout.setLayoutParams(layoutParam);
//
//                LinearLayout tempLinearLayoutLeft = new LinearLayout(btnAdd.getContext());
//                tempLinearLayoutLeft.setOrientation(LinearLayout.VERTICAL);
//                tempLinearLayoutLeft.setLayoutParams(layoutParam);
//
//                LinearLayout tempLinearLayoutRight = new LinearLayout(btnAdd.getContext());
//                tempLinearLayoutRight.setOrientation(LinearLayout.VERTICAL);
//                tempLinearLayoutRight.setLayoutParams(layoutParam);
//
//
//                rg = new RadioGroup(btnAdd.getContext());
//                rg.setLayoutParams(layoutParam);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                rb1 = new RadioButton(btnAdd.getContext());
//                rb1.setLayoutParams(layoutParam);
//                rb1.setText("이평선");
//                rb2 = new RadioButton(btnAdd.getContext());
//                rb2.setLayoutParams(layoutParam);
//                rb2.setText("TEMA");
//                rg.addView(rb1);
//                rg.addView(rb2);
//                tempLinearLayoutLeft.addView(rg);
//
//                rg = new RadioGroup(btnAdd.getContext());
//                rg.setLayoutParams(layoutParam);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                rb1 = new RadioButton(btnAdd.getContext());
//                rb1.setLayoutParams(layoutParam);
//                rb1.setText("틱봉");
//                rb2 = new RadioButton(btnAdd.getContext());
//                rb2.setLayoutParams(layoutParam);
//                rb2.setText("분봉");
//                rg.addView(rb1);
//                rg.addView(rb2);
//                tempLinearLayoutLeft.addView(rg);
//
//                LinearLayout tempLinearLayoutLeftInside = new LinearLayout(btnAdd.getContext());
//                tempLinearLayoutLeftInside.setOrientation(LinearLayout.HORIZONTAL);
//                tempLinearLayoutLeftInside.setLayoutParams(layoutParam);
//
//
//                TextView tv = new TextView(btnAdd.getContext());
//                tv.setText("Period : ");
//                tv.setGravity(CENTER);
//                tv.setLayoutParams(layoutParam);
//                tv.setTextSize(20);
//
//                LinearLayout.LayoutParams tempLayoutParam = new LinearLayout.LayoutParams(150, 150);
//
//                EditText et = new EditText(btnAdd.getContext());
//                et.setTextSize(20);
//                et.setGravity(CENTER);
//                et.setLayoutParams(tempLayoutParam);
//                et.setBackground(ContextCompat.getDrawable(btnAdd.getContext(), R.drawable.edittext_rounded_corner_rectangle));
//                et.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                tempLinearLayoutLeftInside.addView(tv);
//                tempLinearLayoutLeftInside.addView(et);
//                tempLinearLayoutLeft.addView(tempLinearLayoutLeftInside);
//
//
//                tempLinearLayout.addView(tempLinearLayoutLeft);
//
//                tv = new TextView(btnAdd.getContext());
//                tv.setText(">");
//                tv.setTextSize(20);
//
//                tempLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                tempLayoutParam.setMargins(40, 0, 40, 0);
//
//                //tempLayoutParam.gravity = Gravity.CENTER;
//                tv.setLayoutParams(tempLayoutParam);
//                tv.setGravity(CENTER);
//                tempLinearLayout.addView(tv);
//
//
//                rg = new RadioGroup(btnAdd.getContext());
//                rg.setLayoutParams(layoutParam);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                rb1 = new RadioButton(btnAdd.getContext());
//                rb1.setLayoutParams(layoutParam);
//                rb1.setText("이평선");
//                rb2 = new RadioButton(btnAdd.getContext());
//                rb2.setLayoutParams(layoutParam);
//                rb2.setText("TEMA");
//                rg.addView(rb1);
//                rg.addView(rb2);
//                tempLinearLayoutRight.addView(rg);
//
//                rg = new RadioGroup(btnAdd.getContext());
//                rg.setLayoutParams(layoutParam);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                rb1 = new RadioButton(btnAdd.getContext());
//                rb1.setLayoutParams(layoutParam);
//                rb1.setText("틱봉");
//                rb2 = new RadioButton(btnAdd.getContext());
//                rb2.setLayoutParams(layoutParam);
//                rb2.setText("분봉");
//                rg.addView(rb1);
//                rg.addView(rb2);
//                tempLinearLayoutRight.addView(rg);
//
//                LinearLayout tempLinearLayoutRightInside = new LinearLayout(btnAdd.getContext());
//                tempLinearLayoutRightInside.setOrientation(LinearLayout.HORIZONTAL);
//                tempLinearLayoutRightInside.setLayoutParams(layoutParam);
//
//
//                tv = new TextView(btnAdd.getContext());
//                tv.setText("Period : ");
//                tv.setGravity(CENTER);
//                tv.setLayoutParams(layoutParam);
//                tv.setTextSize(20);
//
//                tempLayoutParam = new LinearLayout.LayoutParams(150, 150);
//
//                et = new EditText(btnAdd.getContext());
//                et.setTextSize(20);
//                et.setGravity(CENTER);
//                et.setLayoutParams(tempLayoutParam);
//                et.setBackground(ContextCompat.getDrawable(btnAdd.getContext(), R.drawable.edittext_rounded_corner_rectangle));
//                et.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                tempLinearLayoutRightInside.addView(tv);
//                tempLinearLayoutRightInside.addView(et);
//                tempLinearLayoutRight.addView(tempLinearLayoutRightInside);
//
//
//                tempLinearLayout.addView(tempLinearLayoutRight);
//
//
//                linearLayout.addView(tempLinearLayout);
//
//
//
//
//
//                break;
//            case "기준선-크로스":
//                break;
//            case "기준선-직전봉":
//                break;
//            case "기준선-현재가":
//                break;
//            case "파라볼릭":
//                break;
//            case "RSI":
//                break;
//            case "직전봉-현재가":
//                break;
//            case "직전봉의 상태값":
//                break;
//            case "현재봉의 상태값":
//                break;
//            case "가격지표1":
//                break;
//            case "가격지표2":
//                break;
//            case "가격지표3":
//                break;
//            case "최근 n개봉":
//                break;
//            case "가상매매지표 (먼저만족)":
//                break;
//            case "가상매매지표 (모두만족)":
//                break;
//            case "가상매매지표 (박스권체크)":
//                break;
//
//        }
//        enterStrategyNameList.add(name);
//        enterStrategyLayoutList.add(linearLayout);
//        customAdapter.notifyDataSetChanged();
//
//    }
}
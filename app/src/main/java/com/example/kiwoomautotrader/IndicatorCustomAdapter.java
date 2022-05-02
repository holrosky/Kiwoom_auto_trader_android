package com.example.kiwoomautotrader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IndicatorCustomAdapter extends ArrayAdapter {
    private final String LOG_TAG = IndicatorCustomAdapter.class.getCanonicalName();

    private ArrayList<IndicatorItem> enterIndicatorItemList;
    private String type;
    private int strategyPosition;
    private TextView tvStrategyName;
    private Button btnDelete;
    private Activity context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public IndicatorCustomAdapter(Activity context, int resource, ArrayList<IndicatorItem> enterIndicatorItemList, String type, int strategyPosition) {
        super(context, resource,enterIndicatorItemList);
        this.context = context;
        this.type = type;
        this.strategyPosition = strategyPosition;
        this.enterIndicatorItemList = enterIndicatorItemList;
        sharedPreferences = context.getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();
    }

    @Override
    public int getItemViewType(int position) {
        return enterIndicatorItemList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 500;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "Message received: getview" );

        ViewHolder viewHolder = null;
        LayoutInflater inflater = context.getLayoutInflater();
        IndicatorItem listViewItem = enterIndicatorItemList.get(position);

        if(convertView==null) {
            Log.d(LOG_TAG, "Message received: null");

            switch (listViewItem.getName())
            {
                case "기준선-배열/거리":
                    convertView = inflater.inflate(R.layout.indicator_a, parent, false);
                    break;
                case "기준선-크로스":
                    convertView = inflater.inflate(R.layout.indicator_b, parent, false);
                    break;
                case "기준선-직전봉":
                    convertView = inflater.inflate(R.layout.indicator_c, parent, false);
                    break;
                case "기준선-현재가":
                    convertView = inflater.inflate(R.layout.indicator_d, parent, false);
                    break;
                case "파라볼릭":
                    convertView = inflater.inflate(R.layout.indicator_e, parent, false);
                    break;
                case "RSI":
                    convertView = inflater.inflate(R.layout.indicator_f, parent, false);
                    break;
                case "직전봉-현재가":
                    convertView = inflater.inflate(R.layout.indicator_g, parent, false);
                    break;
                case "직전봉의 상태값": case "현재봉의 상태값":
                    convertView = inflater.inflate(R.layout.indicator_h, parent, false);
                    break;
                case "가격지표":
                    convertView = inflater.inflate(R.layout.indicator_i, parent, false);
                    break;
                case "최근 n개봉":
                    convertView = inflater.inflate(R.layout.indicator_j, parent, false);
                    break;
                case "가상매매지표 (먼저만족)": case "가상매매지표 (모두만족)":
                    convertView = inflater.inflate(R.layout.indicator_k, parent, false);
                    break;
                case "가상매매지표 (박스권체크)":
                    convertView = inflater.inflate(R.layout.indicator_l, parent, false);
                    break;
                case "청산 기본 지표":
                    convertView = inflater.inflate(R.layout.indicator_m, parent, false);
                    break;

            }
            enterIndicatorItemList.get(position).setLinearLayout(convertView.findViewById(R.id.root));
            loadIndicatorValues(position, convertView);

            TextView textView = (TextView) convertView.findViewById(R.id.tvStrategyName);
            viewHolder = new ViewHolder(textView);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.getText().setText(listViewItem.getName());

        tvStrategyName = (TextView) convertView.findViewById(R.id.tvStrategyName);
        btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

        tvStrategyName.setText(enterIndicatorItemList.get(position).getName());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Log.d(LOG_TAG, "DELTE at " + position);

                                try {
                                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("strategy_json", null));

                                    if (type.equals("enter_buy"))
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_buy").remove(position);
                                    else if (type.equals("enter_sell"))
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("enter_sell").remove(position);
                                    else if (type.equals("clear_buy"))
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_buy").remove(position);
                                    else
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_sell").remove(position);


                                    editor.putString("strategy_json", jsonObject.toString());
                                    editor.commit();

                                    Log.d(LOG_TAG, sharedPreferences.getString("strategy_json", null));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //EnterSettingActivity.strategyRemove(position);
                                enterIndicatorItemList.remove(position);
                                notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(enterIndicatorItemList.get(position).getName() + " 지표를 삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();
            }
        });
        return  convertView;
    }

    private void loadIndicatorValues(int position, View convertView){
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


            JSONObject indicators = jsonArray.getJSONObject(position);


            IndicatorItem listViewItem = enterIndicatorItemList.get(position);

            switch (listViewItem.getName())
            {
                case "기준선-배열/거리":

                    RadioGroup rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgLeftIndicatorTimeType);
                    if (indicators.get("left_indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    EditText et = (EditText) convertView.findViewById(R.id.etLeftIndicatorUnit);
                    et.setText(indicators.get("left_indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgLeftIndicatorType);
                    if (indicators.get("left_indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etLeftIndicatorPeriod);
                    et.setText(indicators.get("left_indicator_period").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgRightIndicatorTimeType);
                    if (indicators.get("right_indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etRightIndicatorUnit);
                    et.setText(indicators.get("right_indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgRightIndicatorType);
                    if (indicators.get("right_indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etRightIndicatorPeriod);
                    et.setText(indicators.get("right_indicator_period").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());

                    break;

                case "기준선-크로스":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgLeftIndicatorTimeType);
                    if (indicators.get("left_indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etLeftIndicatorUnit);
                    et.setText(indicators.get("left_indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgLeftIndicatorType);
                    if (indicators.get("left_indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etLeftIndicatorPeriod);
                    et.setText(indicators.get("left_indicator_period").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgRightIndicatorTimeType);
                    if (indicators.get("right_indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etRightIndicatorUnit);
                    et.setText(indicators.get("right_indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgRightIndicatorType);
                    if (indicators.get("right_indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etRightIndicatorPeriod);
                    et.setText(indicators.get("right_indicator_period").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgCrossType);
                    if (indicators.get("cross_type").toString().equals("golden_cross"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());
                    break;
                case "기준선-직전봉":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorType);
                    if (indicators.get("indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorPeriod);
                    et.setText(indicators.get("indicator_period").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectohcl);
                    if (indicators.get("ohcl_type").toString().equals("open"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("ohcl_type").toString().equals("high"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("ohcl_type").toString().equals("low"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("ohcl_type").toString().equals("close"))
                        rg.check(rg.getChildAt(3).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());
                    break;
                case "기준선-현재가":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorType);
                    if (indicators.get("indicator_type").toString().equals("ma"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorPeriod);
                    et.setText(indicators.get("indicator_period").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());
                    break;
                case "파라볼릭":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etParabolicValueOne);
                    et.setText(indicators.get("prabolic_value_one").toString());

                    et = (EditText) convertView.findViewById(R.id.etParabolicValueTwo);
                    et.setText(indicators.get("prabolic_value_two").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectParabolicType);
                    if (indicators.get("prabolic_type").toString().equals("buy"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("prabolic_type").toString().equals("sell"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("prabolic_type").toString().equals("golden_cross"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("prabolic_type").toString().equals("dead_cross"))
                        rg.check(rg.getChildAt(3).getId());
                    break;
                case "RSI":

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etRsiPeriod);
                    et.setText(indicators.get("rsi_period").toString());

                    et = (EditText) convertView.findViewById(R.id.etRsiSignal);
                    et.setText(indicators.get("rsi_signal").toString());

                    et = (EditText) convertView.findViewById(R.id.etRsiFrom);
                    et.setText(indicators.get("rsi_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etRsiTo);
                    et.setText(indicators.get("rsi_to").toString());

                    break;
                case "직전봉-현재가":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectBarStatus);
                    if (indicators.get("bar_status").toString().equals("bull"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("bar_status").toString().equals("bear"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("bar_status").toString().equals("all"))
                        rg.check(rg.getChildAt(2).getId());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectohcl);
                    if (indicators.get("ohcl_type").toString().equals("open"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("ohcl_type").toString().equals("high"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("ohcl_type").toString().equals("low"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("ohcl_type").toString().equals("close"))
                        rg.check(rg.getChildAt(3).getId());
                    else if (indicators.get("ohcl_type").toString().equals("middle"))
                        rg.check(rg.getChildAt(4).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());
                    break;
                case "직전봉의 상태값": case "현재봉의 상태값":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectBarStatus);
                    if (indicators.get("bar_status").toString().equals("bull"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("bar_status").toString().equals("bear"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    break;
                case "가격지표":

                    rg = (RadioGroup) convertView.findViewById(R.id.rgType);
                    RadioButton r1 = (RadioButton) convertView.findViewById(R.id.r1);
                    RadioButton r2 = (RadioButton) convertView.findViewById(R.id.r2);
                    RadioButton r3 = (RadioButton) convertView.findViewById(R.id.r3);
                    RadioButton r4 = (RadioButton) convertView.findViewById(R.id.r4);
                    RadioButton r5 = (RadioButton) convertView.findViewById(R.id.r5);
                    RadioButton r6 = (RadioButton) convertView.findViewById(R.id.r6);
                    RadioButton r7 = (RadioButton) convertView.findViewById(R.id.r7);
                    RadioButton r8 = (RadioButton) convertView.findViewById(R.id.r8);
                    RadioButton r9 = (RadioButton) convertView.findViewById(R.id.r9);
                    RadioButton r10 = (RadioButton) convertView.findViewById(R.id.r10);

                    if (indicators.get("first_type").toString().equals("prev_open"))
                        rg.check(r1.getId());
                    else if (indicators.get("first_type").toString().equals("prev_high"))
                        rg.check(r2.getId());
                    else if (indicators.get("first_type").toString().equals("prev_low"))
                        rg.check(r3.getId());
                    else if (indicators.get("first_type").toString().equals("prev_close"))
                        rg.check(r4.getId());
                    else if (indicators.get("first_type").toString().equals("prev_middle"))
                        rg.check(r5.getId());
                    else if (indicators.get("first_type").toString().equals("today_open"))
                        rg.check(r6.getId());
                    else if (indicators.get("first_type").toString().equals("today_high"))
                        rg.check(r7.getId());
                    else if (indicators.get("first_type").toString().equals("today_low"))
                        rg.check(r8.getId());
                    else if (indicators.get("first_type").toString().equals("today_middle"))
                        rg.check(r9.getId());
                    else if (indicators.get("first_type").toString().equals("manual"))
                        rg.check(r10.getId());


                    et = (EditText) convertView.findViewById(R.id.etManualPrice);
                    et.setText(indicators.get("manual_price").toString());


                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSecondType);

                    if (indicators.get("second_type").toString().equals("today_low"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("second_type").toString().equals("today_high"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etSecondNumerator);
                    et.setText(indicators.get("second_numerator").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgThirdType);

                    if (indicators.get("third_type").toString().equals("prev_low"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("third_type").toString().equals("prev_high"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etThirdNumerator);
                    et.setText(indicators.get("third_numerator").toString());

                    break;
                case "최근 n개봉":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etNumOfBar);
                    et.setText(indicators.get("num_of_bar").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectHL);
                    if (indicators.get("type").toString().equals("high"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());
                    break;
                case "가상매매지표 (먼저만족)": case "가상매매지표 (모두만족)":
                    et = (EditText) convertView.findViewById(R.id.etLossTickUnit);
                    et.setText(indicators.get("loss_tick_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etLossTickTimes);
                    et.setText(indicators.get("loss_tick_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etEnterBuyTimes);
                    et.setText(indicators.get("enter_buy_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etEnterSellTimes);
                    et.setText(indicators.get("enter_sell_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etProfitPreserveTimes);
                    et.setText(indicators.get("profit_preserve_times").toString());
                    break;
                case "가상매매지표 (박스권체크)":
                    et = (EditText) convertView.findViewById(R.id.etPendingMin);
                    et.setText(indicators.get("pending_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etEnterBuyTimes);
                    et.setText(indicators.get("enter_buy_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etEnterSellTimes);
                    et.setText(indicators.get("enter_sell_times").toString());

                    break;

                case "청산 기본 지표":
                    et = (EditText) convertView.findViewById(R.id.etProfitTick);
                    et.setText(indicators.get("profit_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etLossTick);
                    et.setText(indicators.get("loss_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etTotalProfitTick);
                    et.setText(indicators.get("total_profit_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etTotalLossTick);
                    et.setText(indicators.get("total_loss_tick").toString());
                    break;
            }


            Log.d(LOG_TAG, "EROROEOREORERE" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
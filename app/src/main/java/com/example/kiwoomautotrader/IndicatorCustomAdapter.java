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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        //IndicatorItem listViewItem = enterIndicatorItemList.get(position);

        if(convertView==null) {
            Log.d(LOG_TAG, "Message received: null");

            switch (enterIndicatorItemList.get(position).getName())
            {
                case "기준선-배열/거리":
                    convertView = inflater.inflate(R.layout.indicator_a, parent, false);

                    LinearLayout llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    EditText etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    EditText etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "기준선-크로스":
                    convertView = inflater.inflate(R.layout.indicator_b, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "기준선-직전봉":
                    convertView = inflater.inflate(R.layout.indicator_c, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "기준선-현재가":
                    convertView = inflater.inflate(R.layout.indicator_d, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "Pivot-직전봉":
                    convertView = inflater.inflate(R.layout.indicator_u, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "Pivot-현재가":
                    convertView = inflater.inflate(R.layout.indicator_v, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "파라볼릭":
                    convertView = inflater.inflate(R.layout.indicator_e, parent, false);

                    LinearLayout llSameBarEnterTimes = (LinearLayout) convertView.findViewById(R.id.llSameBarEnterTimes);
                    EditText et = (EditText) convertView.findViewById(R.id.etSameBarEnterTimes);

                    if (type.contains("clear")) {
                        et.setText("9999");
                        llSameBarEnterTimes.setVisibility(View.GONE);
                    }

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "파라볼릭 고/저-가격":
                    convertView = inflater.inflate(R.layout.indicator_w, parent, false);


                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "RSI":
                    convertView = inflater.inflate(R.layout.indicator_f, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;
                case "직전봉-현재가":
                    convertView = inflater.inflate(R.layout.indicator_g, parent, false);

                    CheckBox cbStart = (CheckBox)  convertView.findViewById(R.id.cbStart);
                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }
                    else
                        cbStart.setVisibility(View.GONE);

                    break;
                case "직전봉의 상태값": case "현재봉의 상태값":
                    convertView = inflater.inflate(R.layout.indicator_h, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }
                    break;
                case "최근 n개봉":
                    convertView = inflater.inflate(R.layout.indicator_j, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "MACD 크로스":
                    convertView = inflater.inflate(R.layout.indicator_q, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "MACD / Osc 현재값":
                    convertView = inflater.inflate(R.layout.indicator_r, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "MACD Osc 비교":
                    convertView = inflater.inflate(R.layout.indicator_s, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "MACD Osc 연속 증감":
                    convertView = inflater.inflate(R.layout.indicator_t, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    etTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    etTargetClearTickTo = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);

                    if (type.contains("enter")) {
                        etTargetClearTickFrom.setText("9999");
                        etTargetClearTickTo.setText("9999");
                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "가격지표":
                    convertView = inflater.inflate(R.layout.indicator_i, parent, false);

                    llTargetClearTick = (LinearLayout) convertView.findViewById(R.id.llTargetClearTick);
                    EditText etFirstTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etFirstTargetClearTickFrom);
                    EditText etFirstTargetClearTickTo = (EditText) convertView.findViewById(R.id.etFirstTargetClearTickTo);

                    EditText etSecondTargetClearTickFrom = (EditText) convertView.findViewById(R.id.etSecondTargetClearTickFrom);
                    EditText etSecondTargetClearTickTo = (EditText) convertView.findViewById(R.id.etSecondTargetClearTickTo);

                    if (type.contains("enter")) {
                        etFirstTargetClearTickFrom.setText("9999");
                        etFirstTargetClearTickTo.setText("9999");

                        etSecondTargetClearTickFrom.setText("9999");
                        etSecondTargetClearTickTo.setText("9999");

                        llTargetClearTick.setVisibility(View.GONE);
                    }

                    break;

                case "가상매매지표 (먼저만족)": case "가상매매지표 (모두만족)":
                    convertView = inflater.inflate(R.layout.indicator_k, parent, false);
                    break;
                case "가상매매지표 (박스권체크)":
                    convertView = inflater.inflate(R.layout.indicator_l, parent, false);
                    break;
                case "틱 청산":
                    if (type.equals("ai_clear"))
                        convertView = inflater.inflate(R.layout.indicator_o, parent, false);
                    else
                        convertView = inflater.inflate(R.layout.indicator_m, parent, false);
                    break;
                case "스탑트레일링": case "이익보존":
                    if (type.equals("ai_clear"))
                        convertView = inflater.inflate(R.layout.indicator_p, parent, false);
                    else
                        convertView = inflater.inflate(R.layout.indicator_n, parent, false);
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

        viewHolder.getText().setText(enterIndicatorItemList.get(position).getName());

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
                                    else if (type.equals("clear_sell"))
                                        jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_sell").remove(position);
                                    else
                                        jsonObject.getJSONArray("ai_clear").getJSONObject(strategyPosition).getJSONArray("clear_sell").remove(position);

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
            else if (type.equals("clear_sell"))
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("clear_sell");
            else
                jsonArray = jsonObject.getJSONArray("strategy_list").getJSONObject(strategyPosition).getJSONArray("ai_clear");

            JSONObject indicators = jsonArray.getJSONObject(position);


            IndicatorItem listViewItem = enterIndicatorItemList.get(position);

            switch (enterIndicatorItemList.get(position).getName())
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
                    else if (indicators.get("left_indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("left_indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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
                    else if (indicators.get("right_indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("right_indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

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
                    else if (indicators.get("left_indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("left_indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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
                    else if (indicators.get("right_indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("right_indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());
                    break;

                case "기준선-직전봉":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;

                case "기준선-현재가":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "Pivot-직전봉":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectohcl);
                    if (indicators.get("ohcl_type").toString().equals("open"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("ohcl_type").toString().equals("high"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("ohcl_type").toString().equals("low"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("ohcl_type").toString().equals("close"))
                        rg.check(rg.getChildAt(3).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectPivot);
                    if (indicators.get("pivot_type").toString().equals("second_resistance"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("pivot_type").toString().equals("first_resistance"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("pivot_type").toString().equals("pivot_point"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("pivot_type").toString().equals("first_support"))
                        rg.check(rg.getChildAt(3).getId());
                    else if (indicators.get("pivot_type").toString().equals("second_support"))
                        rg.check(rg.getChildAt(4).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;

                case "Pivot-현재가":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectPivot);
                    if (indicators.get("pivot_type").toString().equals("second_resistance"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("pivot_type").toString().equals("first_resistance"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("pivot_type").toString().equals("pivot_point"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("pivot_type").toString().equals("first_support"))
                        rg.check(rg.getChildAt(3).getId());
                    else if (indicators.get("pivot_type").toString().equals("second_support"))
                        rg.check(rg.getChildAt(4).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

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
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());


                    et = (EditText) convertView.findViewById(R.id.etSameBarEnterTimes);
                    et.setText(indicators.get("same_bar_enter_times").toString());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "파라볼릭 고/저-가격":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgUpdateType);
                    if (indicators.get("update_type").toString().equals("real"))
                        rg.check(rg.getChildAt(1).getId());
                    else
                        rg.check(rg.getChildAt(2).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgPriceType);
                    if (indicators.get("price_type").toString().equals("real"))
                        rg.check(rg.getChildAt(1).getId());
                    else
                        rg.check(rg.getChildAt(2).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etParabolicValueOne);
                    et.setText(indicators.get("prabolic_value_one").toString());

                    et = (EditText) convertView.findViewById(R.id.etParabolicValueTwo);
                    et.setText(indicators.get("prabolic_value_two").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgHighLowType);
                    if (indicators.get("high_low_type").toString().equals("0"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("high_low_type").toString().equals("1"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("high_low_type").toString().equals("2"))
                        rg.check(rg.getChildAt(2).getId());
                    else if (indicators.get("high_low_type").toString().equals("3"))
                        rg.check(rg.getChildAt(3).getId());
                    else if (indicators.get("high_low_type").toString().equals("4"))
                        rg.check(rg.getChildAt(4).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffTo);
                    et.setText(indicators.get("tick_diff_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "RSI":

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "직전봉-현재가":
                    CheckBox cbStart = (CheckBox) convertView.findViewById(R.id.cbStart);
                    if (indicators.get("is_start").toString().equals("true"))
                        cbStart.setChecked(true);

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "직전봉의 상태값": case "현재봉의 상태값":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());


                    rg = (RadioGroup) convertView.findViewById(R.id.rgSelectBarStatus);
                    if (indicators.get("bar_status").toString().equals("bull"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("bar_status").toString().equals("bear"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etTickDiffFrom);
                    et.setText(indicators.get("tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;

                case "최근 n개봉":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

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

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;

                case "MACD 크로스":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgTimeType);
                    if (indicators.get("time_type").toString().equals("real"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDShort);
                    et.setText(indicators.get("macd_short").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDLong);
                    et.setText(indicators.get("macd_long").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDSignal);
                    et.setText(indicators.get("macd_signal").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgMACDCross);
                    if (indicators.get("macd_cross_type").toString().equals("golden_cross"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                //MACD 크로스", "MACD / Osc 현재값", "MACD Osc 비교"

                case "MACD / Osc 현재값":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());
                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDShort);
                    et.setText(indicators.get("macd_short").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDLong);
                    et.setText(indicators.get("macd_long").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDSignal);
                    et.setText(indicators.get("macd_signal").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDValueFrom);
                    et.setText(indicators.get("macd_val_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDValueTo);
                    et.setText(indicators.get("macd_val_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDOscValueFrom);
                    et.setText(indicators.get("macd_osc_val_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDOscValueTo);
                    et.setText(indicators.get("macd_osc_val_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;

                case "MACD Osc 비교":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDShort);
                    et.setText(indicators.get("macd_short").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDLong);
                    et.setText(indicators.get("macd_long").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDSignal);
                    et.setText(indicators.get("macd_signal").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgMACDOscValueCompareType);
                    if (indicators.get("macd_compare_type").toString().equals("0"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("macd_compare_type").toString().equals("1"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("macd_compare_type").toString().equals("2"))
                        rg.check(rg.getChildAt(2).getId());
                    else
                        rg.check(rg.getChildAt(3).getId());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "MACD Osc 연속 증감":
                    rg = (RadioGroup) convertView.findViewById(R.id.rgIndicatorTimeType);
                    if (indicators.get("indicator_time_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("min"))
                        rg.check(rg.getChildAt(1).getId());
                    else if (indicators.get("indicator_time_type").toString().equals("day"))
                        rg.check(rg.getChildAt(2).getId());

                    et = (EditText) convertView.findViewById(R.id.etIndicatorUnit);
                    et.setText(indicators.get("indicator_unit").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDShort);
                    et.setText(indicators.get("macd_short").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDLong);
                    et.setText(indicators.get("macd_long").toString());

                    et = (EditText) convertView.findViewById(R.id.etMACDSignal);
                    et.setText(indicators.get("macd_signal").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgMACDChangeType);
                    if (indicators.get("macd_change_type").toString().equals("increase"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("macd_change_type").toString().equals("decrease"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etNumOfBar);
                    et.setText(indicators.get("num_of_bar").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickFrom);
                    et.setText(indicators.get("target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etTargetClearTickTo);
                    et.setText(indicators.get("target_clear_tick_to").toString());

                    break;
                case "가격지표":

                    rg = (RadioGroup) convertView.findViewById(R.id.rgType);

                    switch (indicators.get("first_type").toString())
                    {
                        case "prev_open":
                            ViewGroup temp = (ViewGroup) rg.getChildAt(0);
                            rg.check(temp.getChildAt(0).getId());
                            break;
                        case "prev_high":
                            temp = (ViewGroup) rg.getChildAt(0);
                            rg.check(temp.getChildAt(1).getId());
                            break;
                        case "prev_low":
                            temp = (ViewGroup) rg.getChildAt(0);
                            rg.check(temp.getChildAt(2).getId());
                            break;
                        case "prev_close":
                            temp = (ViewGroup) rg.getChildAt(1);
                            rg.check(temp.getChildAt(0).getId());
                            break;
                        case "prev_middle":
                            temp = (ViewGroup) rg.getChildAt(1);
                            rg.check(temp.getChildAt(1).getId());
                            break;
                        case "today_open":
                            temp = (ViewGroup) rg.getChildAt(1);
                            rg.check(temp.getChildAt(2).getId());
                            break;
                        case "today_high":
                            temp = (ViewGroup) rg.getChildAt(2);
                            rg.check(temp.getChildAt(0).getId());
                            break;
                        case "today_low":
                            temp = (ViewGroup) rg.getChildAt(2);
                            rg.check(temp.getChildAt(1).getId());
                            break;
                        case "today_middle":
                            temp = (ViewGroup) rg.getChildAt(2);
                            rg.check(temp.getChildAt(2).getId());
                            break;
                        case "manual":
                            temp = (ViewGroup) rg.getChildAt(3);
                            rg.check(temp.getChildAt(0).getId());
                            break;
                    }

                    et = (EditText) convertView.findViewById(R.id.etManualPrice);
                    et.setText(indicators.get("manual_price").toString());


                    et = (EditText) convertView.findViewById(R.id.etFirstTickDiffFrom);
                    et.setText(indicators.get("first_tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etFirstTickDiffTo);
                    et.setText(indicators.get("first_tick_diff_to").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgSecondType);

                    if (indicators.get("second_type").toString().equals("today_low"))
                        rg.check(rg.getChildAt(0).getId());
                    else if (indicators.get("second_type").toString().equals("today_high"))
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etSecondNumerator);
                    et.setText(indicators.get("second_numerator").toString());

                    et = (EditText) convertView.findViewById(R.id.etSecondTickDiffFrom);
                    et.setText(indicators.get("second_tick_diff_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etSecondTickDiffTo);
                    et.setText(indicators.get("second_tick_diff_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etFirstTargetClearTickFrom);
                    et.setText(indicators.get("first_target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etFirstTargetClearTickTo);
                    et.setText(indicators.get("first_target_clear_tick_to").toString());

                    et = (EditText) convertView.findViewById(R.id.etSecondTargetClearTickFrom);
                    et.setText(indicators.get("second_target_clear_tick_from").toString());

                    et = (EditText) convertView.findViewById(R.id.etSecondTargetClearTickTo);
                    et.setText(indicators.get("second_target_clear_tick_to").toString());

                    break;

                case "가상매매지표 (먼저만족)":
                    et = (EditText) convertView.findViewById(R.id.etStartHour);
                    et.setText(indicators.get("start_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etStartMin);
                    et.setText(indicators.get("start_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndHour);
                    et.setText(indicators.get("end_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndMin);
                    et.setText(indicators.get("end_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etRerunProfitUnit);
                    et.setText(indicators.get("rerun_profit_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgRerunProfitType);
                    if (indicators.get("rerun_profit_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etLossTotalTick);
                    et.setText(indicators.get("loss_total_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etTotalEnterTimes);
                    et.setText(indicators.get("total_enter_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etConsecutiveLossTimes);
                    et.setText(indicators.get("consecutive_loss_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etConsecutiveProfitPreserveTimes);
                    et.setText(indicators.get("consecutive_profit_preserve_times").toString());

                    et = (EditText) convertView.findViewById(R.id.etConsecutiveLossAndProfitPreserveTimes);
                    et.setText(indicators.get("consecutive_loss_and_profit_preserve_times").toString());

                    break;
                case "가상매매지표 (박스권체크)":
                    et = (EditText) convertView.findViewById(R.id.etStartHour);
                    et.setText(indicators.get("start_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etStartMin);
                    et.setText(indicators.get("start_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndHour);
                    et.setText(indicators.get("end_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndMin);
                    et.setText(indicators.get("end_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etRerunProfitUnit);
                    et.setText(indicators.get("rerun_profit_unit").toString());

                    rg = (RadioGroup) convertView.findViewById(R.id.rgRerunProfitType);
                    if (indicators.get("rerun_profit_type").toString().equals("tick"))
                        rg.check(rg.getChildAt(0).getId());
                    else
                        rg.check(rg.getChildAt(1).getId());

                    et = (EditText) convertView.findViewById(R.id.etPendingMin);
                    et.setText(indicators.get("pending_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etTotalEnterTimes);
                    et.setText(indicators.get("total_enter_times").toString());

                    break;

                case "틱 청산":
                    et = (EditText) convertView.findViewById(R.id.etStartHour);
                    et.setText(indicators.get("start_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etStartMin);
                    et.setText(indicators.get("start_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndHour);
                    et.setText(indicators.get("end_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndMin);
                    et.setText(indicators.get("end_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etProfitTick);
                    et.setText(indicators.get("profit_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etLossTick);
                    et.setText(indicators.get("loss_tick").toString());

                    if (type.equals("ai_clear"))
                    {
                        et = (EditText) convertView.findViewById(R.id.etProfitFrom);
                        et.setText(indicators.get("profit_from").toString());

                        et = (EditText) convertView.findViewById(R.id.etProfitTo);
                        et.setText(indicators.get("profit_to").toString());
                    }

                    break;
                case "스탑트레일링": case "이익보존":
                    et = (EditText) convertView.findViewById(R.id.etStartHour);
                    et.setText(indicators.get("start_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etStartMin);
                    et.setText(indicators.get("start_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndHour);
                    et.setText(indicators.get("end_hour").toString());

                    et = (EditText) convertView.findViewById(R.id.etEndMin);
                    et.setText(indicators.get("end_min").toString());

                    et = (EditText) convertView.findViewById(R.id.etConditionTick);
                    et.setText(indicators.get("condition_tick").toString());

                    et = (EditText) convertView.findViewById(R.id.etReturnTick);
                    et.setText(indicators.get("return_tick").toString());

                    if (type.equals("ai_clear"))
                    {
                        et = (EditText) convertView.findViewById(R.id.etProfitFrom);
                        et.setText(indicators.get("profit_from").toString());

                        et = (EditText) convertView.findViewById(R.id.etProfitTo);
                        et.setText(indicators.get("profit_to").toString());
                    }
                break;
            }

            Log.d(LOG_TAG, "EROROEOREORERE" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
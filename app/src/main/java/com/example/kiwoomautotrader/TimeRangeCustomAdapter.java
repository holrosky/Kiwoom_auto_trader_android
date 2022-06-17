package com.example.kiwoomautotrader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimeRangeCustomAdapter extends ArrayAdapter {
    private final String LOG_TAG = TimeRangeCustomAdapter.class.getCanonicalName();

    private ArrayList<TimeItem> timeList;

    private EditText etStartHour;
    private EditText etStartMin;
    private EditText etEndHour;
    private EditText etEndMin;

    private Button btnDelete;
    private Activity context;

    public TimeRangeCustomAdapter(Activity context, int resource, ArrayList<TimeItem> timeList) {
        super(context, resource,timeList);
        this.context = context;
        this.timeList = timeList;
    }

    @Override
    public int getItemViewType(int position) {
        return timeList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 500;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = context.getLayoutInflater();

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.time_row_item, parent, false);

            etStartHour = (EditText) convertView.findViewById(R.id.etStartHour);
            etStartMin = (EditText) convertView.findViewById(R.id.etStartMin);
            etEndHour = (EditText) convertView.findViewById(R.id.etEndHour);
            etEndMin = (EditText) convertView.findViewById(R.id.etEndMin);

            etStartHour.setText(timeList.get(position).getStartHour());
            etStartMin.setText(timeList.get(position).getStartMin());
            etEndHour.setText(timeList.get(position).getEndHour());
            etEndMin.setText(timeList.get(position).getEndMin());

            etStartHour.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    timeList.get(position).setStartHour(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etStartMin.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    timeList.get(position).setStartMin(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etEndHour.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    timeList.get(position).setEndHour(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etEndMin.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    timeList.get(position).setEndMin(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });




            EditText editText = (EditText) convertView.findViewById(R.id.etStartHour);
            viewHolder = new ViewHolder(editText);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.getText().setText(listViewItem.getStartHour());





        btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                timeList.remove(position);
                                notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("삭제하시겠습니까?").setPositiveButton("삭제", dialogClickListener)
                        .setNegativeButton("취소", dialogClickListener).show();
            }
        });
        return  convertView;
    }
}
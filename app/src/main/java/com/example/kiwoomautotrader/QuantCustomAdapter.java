package com.example.kiwoomautotrader;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class QuantCustomAdapter extends ArrayAdapter {
    private final String LOG_TAG = QuantCustomAdapter.class.getCanonicalName();

    private ArrayList<QuantItem> quantList;

    private EditText etProfitFrom;
    private EditText etProfitTo;
    private EditText etQuant;

    private Button btnDelete;
    private Activity context;

    public QuantCustomAdapter(Activity context, int resource, ArrayList<QuantItem> quantList) {
        super(context, resource,quantList);
        this.context = context;
        this.quantList = quantList;
    }

    @Override
    public int getItemViewType(int position) {
        return quantList.get(position).getType();
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
            convertView = inflater.inflate(R.layout.quant_row_item, parent, false);

            etProfitFrom = (EditText) convertView.findViewById(R.id.etProfitFrom);
            etProfitTo = (EditText) convertView.findViewById(R.id.etProfitTo);
            etQuant = (EditText) convertView.findViewById(R.id.etQuant);


            etProfitFrom.setText(quantList.get(position).getProfitFrom());
            etProfitTo.setText(quantList.get(position).getProfitTo());
            etQuant.setText(quantList.get(position).getQuant());


            etProfitFrom.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    quantList.get(position).setProfitFrom(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etProfitTo.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    quantList.get(position).setProfitTo(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etQuant.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    quantList.get(position).setQuant(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            EditText editText = (EditText) convertView.findViewById(R.id.etProfitFrom);
            viewHolder = new ViewHolder(editText);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                quantList.remove(position);
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
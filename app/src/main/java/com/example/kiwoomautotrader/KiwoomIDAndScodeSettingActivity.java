package com.example.kiwoomautotrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class KiwoomIDAndScodeSettingActivity extends AppCompatActivity {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView tv;
    private EditText etInput;
    private Button btnConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_scode_setting);

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        tv = (TextView) findViewById(R.id.tv);
        etInput = (EditText) findViewById(R.id.etInput);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if (type.equals("kiwoom_id")) {
            setTitle("키움 ID 설정");
            tv.setText("키움아이디 : ");
            etInput.setHint("키움증권 ID 입력");


            btnConfirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {

                        String kiwoomID = etInput.getText().toString();
                        if (kiwoomID.matches("")) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:

                                            break;

                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(btnConfirm.getContext());
                            builder.setMessage("키움 ID를 입력해주세요!").setPositiveButton("확인", dialogClickListener).show();
                        } else {
                            editor.putString("kiwoom_id", kiwoomID.replaceAll(" ", ""));
                            editor.commit();

                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Account Setting error.", e);
                    }
                }
            });
        }
        else
        {
            setTitle("매매 종목 설정");
            tv.setText("종목코드 : ");
            etInput.setHint("종목코드 입력");

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {

                        String sCode = etInput.getText().toString();
                        if (sCode.matches("")) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:

                                            break;

                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(btnConfirm.getContext());
                            builder.setMessage("매매 종목을 입력해주세요!").setPositiveButton("확인", dialogClickListener).show();
                        } else {
                            editor.putString("sCode", sCode.replaceAll(" ", ""));
                            editor.commit();

                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Scode Setting error.", e);
                    }
                }
            });
        }
    }

    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}

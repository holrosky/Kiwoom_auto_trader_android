package com.example.kiwoomautotrader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class KiwoomIDSettingActivity extends AppCompatActivity {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText etAccountInput;
    private Button btnAccConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        etAccountInput = (EditText) findViewById(R.id.etAccountInput);
        btnAccConfirm = (Button) findViewById(R.id.btnAccConfirm);




        btnAccConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {

                    String kiwoomID = etAccountInput.getText().toString();
                    if (kiwoomID.matches("")) {
                        int color = Color.parseColor("#f55c51");
                        Snackbar mSnackBar = Snackbar.make(view, "키움 아이디를 입력해주세요!", Snackbar.LENGTH_SHORT);
                        mSnackBar.getView().setBackgroundColor(color);
                        mSnackBar.show();
                    }

                   else
                   {
                       editor.putString("kiwoom_id", kiwoomID);
                       editor.putString("trade_server", "test");
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
}

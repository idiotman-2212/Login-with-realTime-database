package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private CountryCodePicker countryCodePicker;
    private EditText editTextPhone;
    private ProgressBar progressBar;
    private Button btnSendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        countryCodePicker = findViewById(R.id.countryCodePicker);
        editTextPhone = findViewById(R.id.editTextPhone);
        progressBar = findViewById(R.id.progressBar);
        btnSendCode = findViewById(R.id.btnSendCode);

        countryCodePicker.registerCarrierNumberEditText(editTextPhone);

        setSendCodeClickListener();
    }

    //Bắt sự kiện nhấn nút Send Code
    private void setSendCodeClickListener() {
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendCodeButtonClick();
            }
        });
    }

    //Xử lý sự kiện nhấn nút Send Code
    private void handleSendCodeButtonClick() {
        if (!countryCodePicker.isValidFullNumber()) {
            editTextPhone.setError("Phone number not valid");
            return;
        }
        Intent intent = new Intent(VerifyPhoneNumberActivity.this, EnterOTPActivity.class);
        intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
        startActivity(intent);
    }
}

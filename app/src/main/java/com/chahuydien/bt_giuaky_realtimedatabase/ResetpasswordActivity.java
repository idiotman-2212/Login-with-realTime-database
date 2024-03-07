package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetpasswordActivity extends AppCompatActivity {

    private EditText editTextNewPassword, editTextConfirmPassword;
    private Button btnResetPassword, btnBack;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private String email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack =findViewById(R.id.btnBack);
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("email")) {
            email = extras.getString("email");
        } else {
            finish();
        }

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasswordClickListener();
            }
        });
    }
    private void resetPasswordClickListener() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(ResetpasswordActivity.this, "Please enter both new and confirm passwords", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(ResetpasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            updatePasswordInDatabase(newPassword);
        }
    }

    private void updatePasswordInDatabase(String newPassword) {
        usersReference.child(email.replace(".", "_dot_")).child("password").setValue(newPassword);

        Toast.makeText(ResetpasswordActivity.this, "Password has been reset successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}



package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editUsername, editEmail, editPhone, editPassword;
    private Button btnSave;
    private String usernameUser, emailUser, phoneUser, passwordUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        btnSave = findViewById(R.id.btnSave);

        showData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserNameChanged() || isPasswordChanged() || isEmailChanged() || isPhoneChanged()){
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPhoneChanged() {
        if (!phoneUser.equals(editPhone.getText().toString())){
            reference.child(phoneUser).child("phone").setValue(editEmail.getText().toString());
            phoneUser = editPhone.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmailChanged() {
        if (!emailUser.equals(editEmail.getText().toString())){
            reference.child(emailUser).child("email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isPasswordChanged() {
        if (!passwordUser.equals(editPassword.getText().toString())){
            reference.child(passwordUser).child("password").setValue(editPassword.getText().toString());
            passwordUser = editPassword.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isUserNameChanged() {
        if (!usernameUser.equals(editUsername.getText().toString())){
            reference.child(usernameUser).child("username").setValue(editUsername.getText().toString());
            usernameUser = editUsername.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    public void showData(){
        Intent intent = getIntent();
        usernameUser = intent.getStringExtra("username");
        emailUser = intent.getStringExtra("email");
        phoneUser = intent.getStringExtra("phone");
        passwordUser = intent.getStringExtra("password");
        editUsername.setText(usernameUser);
        editEmail.setText(emailUser);
        editPhone.setText(phoneUser);
        editPassword.setText(passwordUser);
    }
}

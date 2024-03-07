package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.chahuydien.bt_giuaky_realtimedatabase.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputUsername, inputEmail, inputPassword, inputConformPassword, inputPhone;
    private Button btnRegister;
    private TextView alreadyHaveAccount;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       //Ánh xạ các phần trong layout
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConformPassword = findViewById(R.id.inputConformPassword);
        inputPhone = findViewById(R.id.inputPhone);
        btnRegister = findViewById(R.id.btnRegister);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);

        //Bắt sự kiện nhấn nút Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String userName = inputUsername.getText().toString();
                String email = inputEmail.getText().toString();
                String phone = inputPhone.getText().toString();
                String password = inputPassword.getText().toString();
                String re_password = inputConformPassword.getText().toString();

                if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || re_password.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(re_password)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                UserModel userModel = new UserModel(userName,email, phone, password);
                reference.child(userName).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("RegisterActivity", "Đăng ký thành công và lưu dữ liệu vào Realtime Database");
                        } else {
                            Log.e("RegisterActivity", "Lỗi khi lưu dữ liệu vào Realtime Database", task.getException());
                        }
                    }
                });

                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        //Bắt sự kiện nhấn Already Account
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

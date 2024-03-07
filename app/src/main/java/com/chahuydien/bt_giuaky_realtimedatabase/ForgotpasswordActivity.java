package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotpasswordActivity extends AppCompatActivity {

    String email;
    private EditText editTextEmail;
    private Button btnSendEmail, btnBack;
    private DatabaseReference usersReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        editTextEmail = findViewById(R.id.editTextEmail);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnBack = findViewById(R.id.btnBack);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailClickListener();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình hiện tại
            }
        });
    }

    private void sendEmailClickListener() {
        email = editTextEmail.getText().toString().trim();
        if (!email.isEmpty()) {
            checkIfEmailExistsAndSendResetEmail();
        } else {
            editTextEmail.setError("Email field can't be empty");
        }
    }

    private void checkIfEmailExistsAndSendResetEmail() {
        Query checkUser = usersReference.orderByChild("email").equalTo(email);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email exists in the Realtime Database
                    ResetPassword();
                } else {
                    // Email does not exist, show an error message
                    Toast.makeText(ForgotpasswordActivity.this, "Email does not exist in the user database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors when accessing the Realtime Database
                Toast.makeText(ForgotpasswordActivity.this, "Error accessing database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ResetPassword() {
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        handleSuccessfulResetPassword();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleFailedResetPassword(e);
                    }
                });
    }

    private void handleFailedResetPassword(Exception e) {
        Toast.makeText(ForgotpasswordActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    private void handleSuccessfulResetPassword() {
        Toast.makeText(ForgotpasswordActivity.this, "Reset password link has been send to your register email", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ForgotpasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

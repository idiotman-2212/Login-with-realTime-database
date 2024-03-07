package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnGoogle, btnFacebook;
    private TextView forgotPassword, textViewSignUp, textViewPhone;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần trong layout
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnlogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        forgotPassword = findViewById(R.id.forgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewPhone = findViewById(R.id.textViewPhone);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() | !validatePassword()) {
                } else {
                    checkUser();
                }
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotpasswordActivity.class);
                startActivity(intent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInWithGoogle() {
        // Tạo GoogleSignInOptions để cấu hình đăng nhập Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Tạo GoogleSignInClient từ GoogleSignInOptions
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Bắt đầu Intent để đăng nhập Google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập Google thành công, nhận thông tin tài khoản
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Đăng nhập Google thất bại
                Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new  OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            handleSuccessfulLogin();
                        } else {
                            // Đăng nhập thất bại
                            handleFailedLogin(task.getException());
                        }
                    }
                });
    }

    // Xử lý trường hợp đăng nhập sai
    private void handleFailedLogin(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    //Xử lý trường hợp đăng nhập đúng
    private void handleSuccessfulLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void checkUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(email);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (passwordFromDB.equals(password)) {
                            inputPassword.setError(null);

                            String userNameFromDB = userSnapshot.child("username").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);
                            String phoneFromDB = userSnapshot.child("phone").getValue(String.class);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("name", userNameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("phone", phoneFromDB);
                            intent.putExtra("password", passwordFromDB);

                            startActivity(intent);
                        } else {
                            inputPassword.setError("Mật khẩu không hợp lệ");
                            inputPassword.requestFocus();
                        }
                    }
                } else {
                    inputEmail.setError("Email không tồn tại");
                    inputEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi Cơ sở dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean validatePassword () {
            String val = inputPassword.getText().toString();
            if (val.isEmpty()) {
                inputPassword.setError("Password cannot be empty");
                return false;
            } else {
                inputPassword.setError(null);
                return true;
            }
        }

        private boolean validateEmail () {
            String val = inputEmail.getText().toString();
            if (val.isEmpty()) {
                inputEmail.setError("Email cannot be empty");
                return false;
            } else {
                inputEmail.setError(null);
                return true;
            }
        }

    }

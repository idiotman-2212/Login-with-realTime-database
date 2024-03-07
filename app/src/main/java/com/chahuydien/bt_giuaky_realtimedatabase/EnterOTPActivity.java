package com.chahuydien.bt_giuaky_realtimedatabase;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class EnterOTPActivity extends AppCompatActivity {
    private Long timeoutSeconds = 60L;
    private CountDownTimer resendTimer;
    private boolean isResendInProgress = false;
    private EditText editTextCode;
    private TextView textViewSendAgain;
    private ProgressBar progressBar;
    private Button btnSignIn;
    private FirebaseAuth mAuth;
    private String mVerificationId, mPhoneNumber;

    private PhoneAuthProvider.ForceResendingToken mResetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        mPhoneNumber = getIntent().getExtras().getString("phone");

        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        progressBar = findViewById(R.id.progressBar);
        btnSignIn = findViewById(R.id.btnSignIn);
        textViewSendAgain = findViewById(R.id.textViewSendAgain);

        sendOTP(mPhoneNumber, false);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editTextCode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signIn(credential);
            }
        });

        textViewSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isResendInProgress) {
                    sendOTP(mPhoneNumber, true);
                }
            }
        });

    }

    private void sendOTP(String mPhoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(EnterOTPActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                mVerificationId = s;
                                mResetToken = forceResendingToken;
                                Toast.makeText(EnterOTPActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }
                        });
        if (isResend && mResetToken != null) {
            builder.setForceResendingToken(mResetToken);
            isResendInProgress = true;
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(mResetToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    public void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    public void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(EnterOTPActivity.this, MainActivity.class);
                    intent.putExtra("phone", mPhoneNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(EnterOTPActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        public void startResendTimer() {
            textViewSendAgain.setEnabled(false);
            isResendInProgress = true;
            resendTimer = new CountDownTimer(timeoutSeconds * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeoutSeconds = millisUntilFinished / 1000;
                    textViewSendAgain.setText("Resend OTP in " + timeoutSeconds + " seconds");
                }

                @Override
                public void onFinish() {
                    timeoutSeconds = 60L;
                    textViewSendAgain.setText("Resend OTP");
                    textViewSendAgain.setEnabled(true);
                    isResendInProgress = false;
                }
            }.start();
        }

    }


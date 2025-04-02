package com.example.myapplica;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private TextView resendBtn;
    private boolean resendEnabled = false;
    private int resendTime = 60;
    private int selectedETPosition = 0;

    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);

        mAuth = FirebaseAuth.getInstance();

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        resendBtn = findViewById(R.id.resendBtn);
        Button verifyBtn = findViewById(R.id.verifyBtn);
        TextView otpEmail = findViewById(R.id.otpEmail);
        TextView otpmobile = findViewById(R.id.otpmobile);

        String getEmail = getIntent().getStringExtra("email");
        String getMobile = getIntent().getStringExtra("mobile");

        otpEmail.setText(getEmail);
        otpmobile.setText(getMobile);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);

        showKeyboard(otp1);
        startCountDownTimer();

        resendBtn.setOnClickListener(v -> {
            if (resendEnabled) {
                startCountDownTimer();
                sendOtpToPhone(getMobile); // Resend OTP
            }
        });

        verifyBtn.setOnClickListener(v -> {
            String generatedOtp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString();
            if (generatedOtp.length() == 4) {
                verifyOtp(generatedOtp);
            }
        });

        sendOtpToPhone(getMobile); // Initial OTP send
    }

    private void showKeyboard(EditText otp) {
        otp.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otp, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer() {
        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#91000000"));
        new CountDownTimer(resendTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend Code (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(getResources().getColor(R.color.primary));
            }
        }.start();
    }

    private void sendOtpToPhone(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Auto-detected OTP
                        String otp = phoneAuthCredential.getSmsCode();
                        if (otp != null) {
                            otp1.setText(String.valueOf(otp.charAt(0)));
                            otp2.setText(String.valueOf(otp.charAt(1)));
                            otp3.setText(String.valueOf(otp.charAt(2)));
                            otp4.setText(String.valueOf(otp.charAt(3)));
                            verifyOtp(otp);
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Handle verification failure
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        OTPVerification.this.verificationId = verificationId;
                    }
                });
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // OTP verification successful
                        // Proceed to the next activity or action
                    } else {
                        // OTP verification failed
                    }
                });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (selectedETPosition == 0) {
                    selectedETPosition = 1;
                    showKeyboard(otp2);
                } else if (selectedETPosition == 1) {
                    selectedETPosition = 2;
                    showKeyboard(otp3);
                } else if (selectedETPosition == 2) {
                    selectedETPosition = 3;
                    showKeyboard(otp4);
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (selectedETPosition == 3) {
                selectedETPosition = 2;
                showKeyboard(otp3);
            } else if (selectedETPosition == 2) {
                selectedETPosition = 1;
                showKeyboard(otp2);
            } else if (selectedETPosition == 1) {
                selectedETPosition = 0;
                showKeyboard(otp1);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}

package com.example.myapplica;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private boolean passwordShowimg = false;
    private boolean confirmpasswordShowimg = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        final EditText email = findViewById(R.id.EmailEd);
        final EditText mobile = findViewById(R.id.mobileEd);
        final EditText password = findViewById(R.id.passwordEd);
        final EditText confirmPasswordEd = findViewById(R.id.ConfirmpasswordEd);
        final ImageView passwordIcon = findViewById(R.id.passwordIcon);
        final ImageView confirmPasswordIcon = findViewById(R.id.ConfirmpasswordIcon);
        final AppCompatButton signUpBtn = findViewById(R.id.signUpBtn);
        final TextView signInBtn = findViewById(R.id.signInbtn);

        mAuth = FirebaseAuth.getInstance();

        // Gestion de la visibilité du mot de passe
        passwordIcon.setOnClickListener(v -> {
            passwordShowimg = !passwordShowimg;
            password.setInputType(passwordShowimg
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordIcon.setImageResource(passwordShowimg
                    ? R.drawable.password_hide
                    : R.drawable.password_show);
            password.setSelection(password.length());
        });

        confirmPasswordIcon.setOnClickListener(v -> {
            confirmpasswordShowimg = !confirmpasswordShowimg;
            confirmPasswordEd.setInputType(confirmpasswordShowimg
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPasswordIcon.setImageResource(confirmpasswordShowimg
                    ? R.drawable.password_hide
                    : R.drawable.password_show);
            confirmPasswordEd.setSelection(confirmPasswordEd.length());
        });

        signUpBtn.setOnClickListener(v -> {
            String emailTxt = email.getText().toString().trim();
            String passwordTxt = password.getText().toString().trim();
            String confirmPasswordTxt = confirmPasswordEd.getText().toString().trim();
            String mobileTxt = mobile.getText().toString().trim();

            if (emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPasswordTxt.isEmpty() || mobileTxt.isEmpty()) {
                Toast.makeText(Register.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordTxt.equals(confirmPasswordTxt)) {
                Toast.makeText(Register.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Register.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();

                                // Ouvrir OTPVerification
                                Intent intent = new Intent(Register.this, OTPVerification.class);
                                intent.putExtra("mobile", mobileTxt);
                                intent.putExtra("email", emailTxt);
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Échec de l'inscription : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        signInBtn.setOnClickListener(v -> finish());

        // Gestion des insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

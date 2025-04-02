package com.example.myapplica;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private boolean passwordShowimg = false;
    private FirebaseAuth mAuth; // Instance Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); // Initialisation de Firebase Auth

        // Vérifier si un utilisateur est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        final EditText usernameEd = findViewById(R.id.usernameEd);
        final EditText passwordEd = findViewById(R.id.passwordEd);
        final ImageView passwordIcon = findViewById(R.id.passwordIcon);
        final TextView signUpbtn = findViewById(R.id.signUpbtn);
        final TextView signInBtn = findViewById(R.id.signInBtn);

        // Afficher/Masquer le mot de passe
        passwordIcon.setOnClickListener(v -> {
            if (passwordShowimg) {
                passwordShowimg = false;
                passwordEd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordIcon.setImageResource(R.drawable.password_show);
            } else {
                passwordShowimg = true;
                passwordEd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordIcon.setImageResource(R.drawable.password_hide);
            }
            passwordEd.setSelection(passwordEd.length()); // Placer le curseur à la fin
        });

        // Gestion du bouton "Sign Up"
        signUpbtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, Register.class)));

        // Gestion du bouton "Sign In"
        signInBtn.setOnClickListener(v -> {
            String email = usernameEd.getText().toString().trim();
            String password = passwordEd.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        // Gestion des insets système (bordures, clavier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fonction pour se connecter avec Firebase
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

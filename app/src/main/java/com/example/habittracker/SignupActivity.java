package com.example.habittracker;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnSignup;
    TextView textLoginRedirect;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editEmail = findViewById(R.id.Email);
        editPassword = findViewById(R.id.Password);
        btnSignup = findViewById(R.id.Signup);
        textLoginRedirect = findViewById(R.id.textLoginRedirect);

        mAuth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                editEmail.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Password is required");
                return;
            }

            if (password.length() < 6) {
                editPassword.setError("Password must be >= 6 characters");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        textLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}
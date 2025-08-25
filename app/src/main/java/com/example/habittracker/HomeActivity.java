package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnAddHabit, btnViewHabits, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        tvWelcome = findViewById(R.id.tvWelcome);
        btnAddHabit = findViewById(R.id.btnAddHabit);
        btnViewHabits = findViewById(R.id.btnViewHabits);
        btnLogout = findViewById(R.id.btnLogout);

        // Show user email
        if (mAuth.getCurrentUser() != null) {
            tvWelcome.setText("Welcome, " + mAuth.getCurrentUser().getEmail());
        }

        // ➕ Add Habit Button → opens AddHabitActivity
        btnAddHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddHabitActivity.class);
                startActivity(intent);
            }
        });

//         📋 View Habits Button → will open HabitListActivity (we will build this next)
        btnViewHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ViewHabitsActivity.class);
                startActivity(intent);
            }
        });

        // 🚪 Logout Button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}

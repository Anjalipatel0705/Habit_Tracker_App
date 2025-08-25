package com.example.habittracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddHabitActivity extends AppCompatActivity {

    private EditText etHabitName;
    private RadioGroup radioGroupFrequency;
    private Button btnSaveHabit;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private static final String TAG = "AddHabitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit); // ✅ make sure file name matches

        etHabitName = findViewById(R.id.etHabitName);
        radioGroupFrequency = findViewById(R.id.radioGroupFrequency);
        btnSaveHabit = findViewById(R.id.btnSaveHabit);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSaveHabit.setOnClickListener(v -> saveHabit());
    }

    private void saveHabit() {
        String habitName = etHabitName.getText().toString().trim();
        int selectedId = radioGroupFrequency.getCheckedRadioButtonId();

        if (habitName.isEmpty() || selectedId == -1) {
            Toast.makeText(this, "⚠ Please enter a habit name and select frequency", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User is null. Cannot save habit.");
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        RadioButton selectedRadio = findViewById(selectedId);
        String frequency = selectedRadio.getText().toString();

        Map<String, Object> habit = new HashMap<>();
        habit.put("name", habitName);
        habit.put("frequency", frequency);
        habit.put("completed", false);
        habit.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("habits")
                .add(habit)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Habit added with ID: " + documentReference.getId());
                    Toast.makeText(this, "✅ Habit Added Successfully", Toast.LENGTH_SHORT).show();
                    finish(); // 🔥 Go back after saving
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding habit", e);
                    Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

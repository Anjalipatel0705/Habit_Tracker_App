package com.example.habittracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewHabitsActivity extends AppCompatActivity {

    private ListView listView;
    private HabitAdapter adapter;
    private ArrayList<Habit> habitList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private static final String TAG = "ViewHabitsActivity";

    private ProgressBar progressBar;
    private TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habits);

        listView = findViewById(R.id.listViewHabits);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        habitList = new ArrayList<>();
        adapter = new HabitAdapter(this, habitList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadHabitsFromFirestore();
        scheduleDailyNotification();
    }

    private void loadHabitsFromFirestore() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("habits")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error fetching habits", error);
                        return;
                    }

                    if (querySnapshot != null) {
                        habitList.clear();
                        for (DocumentSnapshot doc : querySnapshot) {
                            String id = doc.getId();
                            String name = doc.getString("name");
                            String frequency = doc.getString("frequency");
                            boolean completed = doc.getBoolean("completed") != null && doc.getBoolean("completed");

                            habitList.add(new Habit(id, name, frequency, completed));
                        }
                        adapter.notifyDataSetChanged();
                        updateProgress();
                    }
                });
    }

    private void updateProgress() {
        if (habitList.isEmpty()) {
            progressBar.setProgress(0);
            progressText.setText("Progress: 0%");
            return;
        }

        int completedCount = 0;
        for (Habit habit : habitList) {
            if (habit.isCompleted()) completedCount++;
        }

        int progress = (int) ((completedCount / (float) habitList.size()) * 100);
        progressBar.setProgress(progress);
        progressText.setText("Progress: " + progress + "%");
    }

    private void scheduleDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 9 AM reminder
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }
}

package com.example.habittracker;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HabitAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Habit> habitList;
    private FirebaseFirestore db;
    private String userId;

    public HabitAdapter(Context context, ArrayList<Habit> habitList) {
        this.context = context;
        this.habitList = habitList;
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getCount() { return habitList.size(); }

    @Override
    public Object getItem(int position) { return habitList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Habit habit = habitList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.habitName);
        TextView frequencyText = convertView.findViewById(R.id.habitFrequency);
        CheckBox completedCheck = convertView.findViewById(R.id.habitCompleted);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        nameText.setText(habit.getName());
        frequencyText.setText("Frequency: " + habit.getFrequency());
        completedCheck.setChecked(habit.isCompleted());

        // Strike-through if completed
        if (habit.isCompleted()) {
            nameText.setPaintFlags(nameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            nameText.setPaintFlags(nameText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // ✅ Update checkbox in Firestore
        completedCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.collection("users").document(userId)
                    .collection("habits").document(habit.getId())
                    .update("completed", isChecked);
        });

        // ✅ Delete
        btnDelete.setOnClickListener(v -> {
            db.collection("users").document(userId)
                    .collection("habits").document(habit.getId())
                    .delete();
            habitList.remove(position);
            notifyDataSetChanged();
        });

        // ✅ Edit (dialog with name + frequency radio buttons)
        btnEdit.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_habit, null);
            EditText editName = dialogView.findViewById(R.id.editHabitName);
            RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupFrequency);

            editName.setText(habit.getName());

            if (habit.getFrequency().equals("Daily"))
                radioGroup.check(R.id.radioDaily);
            else if (habit.getFrequency().equals("Weekly"))
                radioGroup.check(R.id.radioWeekly);
            else
                radioGroup.check(R.id.radioMonthly);

            new AlertDialog.Builder(context)
                    .setTitle("Edit Habit")
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = editName.getText().toString();
                        String newFrequency = "Daily";

                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        if (checkedId == R.id.radioWeekly) newFrequency = "Weekly";
                        if (checkedId == R.id.radioMonthly) newFrequency = "Monthly";

                        db.collection("users").document(userId)
                                .collection("habits").document(habit.getId())
                                .update("name", newName, "frequency", newFrequency);

                        habit.setName(newName);
                        habit.setFrequency(newFrequency);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }
}

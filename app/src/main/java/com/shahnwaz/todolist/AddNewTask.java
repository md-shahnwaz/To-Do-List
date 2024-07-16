package com.shahnwaz.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shahnwaz.todolist.Model.ToDoModel;
import com.shahnwaz.todolist.Utils.DatabaseHandler;

import java.util.Calendar;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText newTaskText;
    private EditText newTaskDescription;
    private EditText newTaskCompletionDate;
    private DatabaseHandler db;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskDescription = view.findViewById(R.id.newTaskDescription);
        newTaskCompletionDate = view.findViewById(R.id.newTaskCompletionDate);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskCompletionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            String description = bundle.getString("description");
            String completionDate = bundle.getString("completionDate");
            newTaskText.setText(task);
            newTaskDescription.setText(description);
            newTaskCompletionDate.setText(completionDate);
        }

        final boolean finalIsUpdate = isUpdate;
        view.findViewById(R.id.newTaskButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = newTaskText.getText().toString();
                String description = newTaskDescription.getText().toString();
                String completionDate = newTaskCompletionDate.getText().toString();

                if (TextUtils.isEmpty(text) || TextUtils.isEmpty(description) || TextUtils.isEmpty(completionDate)) {
                    Log.d(TAG, "One or more fields are empty");
                    return;
                }

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text, description, completionDate);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setDescription(description);
                    task.setCompletionDate(completionDate);
                    task.setStatus(0);
                    db.insertTask(task);
                    Log.d(TAG, "Inserted new task: " + task.getTask() + ", Description: " + task.getDescription() + ", Completion Date: " + task.getCompletionDate());
                }
                dismiss();
            }
        });

    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        newTaskCompletionDate.setText(date);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}

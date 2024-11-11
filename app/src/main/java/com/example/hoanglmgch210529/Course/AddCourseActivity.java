package com.example.hoanglmgch210529.Course;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hoanglmgch210529.Model.Course;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class AddCourseActivity extends AppCompatActivity {

    private TextInputEditText etCapacity, etPrice, etDescription, etDuration;
    private TimePicker timePicker;
    private Spinner spinnerDayOfWeek, spinnerClassType;
    private Button btnSubmit;
    private CourseDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Initialize DatabaseHelper
        dbHelper = new CourseDatabaseHelper(this);

        // Initialize views
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        etCapacity = findViewById(R.id.etCapacity);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etDuration = findViewById(R.id.etDuration); // Thêm ô nhập liệu duration
        timePicker = findViewById(R.id.timePicker);
        spinnerClassType = findViewById(R.id.spinnerClassType);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Setup Spinner for Day of Week
        ArrayAdapter<CharSequence> adapterDayOfWeek = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapterDayOfWeek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(adapterDayOfWeek);

        // Setup TimePicker
        timePicker.setIs24HourView(true);

        // Setup Spinner for Class Type
        ArrayAdapter<CharSequence> adapterClassType = ArrayAdapter.createFromResource(this,
                R.array.class_types, android.R.layout.simple_spinner_item);
        adapterClassType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(adapterClassType);

        // Submit Button
        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                // Lấy thông tin đã nhập
                String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String time = String.format("%02d:%02d", hour, minute);
                int minutesInput = Integer.parseInt(etDuration.getText().toString().trim());
                int hours = minutesInput / 60;
                int minutes = minutesInput % 60;
                String duration = String.format("%dh%02d", hours, minutes);
                String classType = spinnerClassType.getSelectedItem().toString();
                int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
                double price = Double.parseDouble(etPrice.getText().toString().trim());
                String priceFormatted = "£" + String.format("%.0f", price);
                String description = etDescription.getText().toString().trim();

                // Tạo chuỗi hiển thị thông tin người dùng đã nhập
                String message = "Day of Week: " + dayOfWeek + "\n" +
                        "Time: " + time + "\n" +
                        "Duration: " + duration + "\n" +
                        "Class Type: " + classType + "\n" +
                        "Capacity: " + capacity + "\n" +
                        "Price: " + priceFormatted + "\n" +
                        "Description: " + description;

                // Tạo AlertDialog để xác nhận thông tin
                new androidx.appcompat.app.AlertDialog.Builder(AddCourseActivity.this)
                        .setTitle("Confirm Course Details")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> {
                            saveCourse();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

    }

    private boolean validateInput() {
        // Kiểm tra nếu bất kỳ trường bắt buộc nào bị bỏ trống
        if (etCapacity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Capacity is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDuration.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            int duration = Integer.parseInt(etDuration.getText().toString().trim());
            if (capacity <= 0 || price <= 0 || duration <= 0) {
                Toast.makeText(this, "Capacity, Price, and Duration must be positive", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;  // Tất cả các trường đã được điền đầy đủ
    }

    private void saveCourse() {
        try {
            // Retrieve input data
            String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);
            int minutesInput = Integer.parseInt(etDuration.getText().toString().trim());  // Lấy số phút nhập vào
            int hours = minutesInput / 60;  // Tính giờ
            int minutes = minutesInput % 60;  // Lấy phần dư (phút)
            String duration = String.format("%dh%02d", hours, minutes);
            String classType = spinnerClassType.getSelectedItem().toString();
            int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
            double price =  Double.parseDouble(etPrice.getText().toString().trim());

            String description = etDescription.getText().toString().trim();

            // Create Course object
            Course course = new Course(dayOfWeek,time,capacity,duration,price,classType,description);

            // Save course to database
            dbHelper.addCourse(course);

            // Return result to previous activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newCourse", course);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
            //co internet
            //them du lieu qua api
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


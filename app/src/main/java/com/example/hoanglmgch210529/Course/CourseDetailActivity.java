package com.example.hoanglmgch210529.Course;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;
import com.example.hoanglmgch210529.Model.Course;

public class CourseDetailActivity extends AppCompatActivity {
    private Spinner etDayOfWeek, etClassType; // Spinner for Day of Week and Class Type
    private TimePicker etTime; // TimePicker for Time
    private EditText etCapacity, etDuration, etPrice, etDescription; // EditText for other fields
    private Button btnUpdate;
    private CourseDatabaseHelper dbHelper;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Ánh xạ các view
        etDayOfWeek = findViewById(R.id.etDayOfWeek); // Spinner for Day of the Week
        etTime = findViewById(R.id.etTime); // TimePicker for Time
        etCapacity = findViewById(R.id.etCapacity); // EditText for Capacity
        etDuration = findViewById(R.id.etDuration); // EditText for Duration
        etPrice = findViewById(R.id.etPrice); // EditText for Price
        etClassType = findViewById(R.id.etClassType); // Spinner for Class Type
        etDescription = findViewById(R.id.etDescription); // EditText for Description
        btnUpdate = findViewById(R.id.btnUpdate); // Button for Update

        dbHelper = new CourseDatabaseHelper(this);

        // Khởi tạo Spinner với các giá trị từ resources
        ArrayAdapter<CharSequence> adapterDayOfWeek = ArrayAdapter.createFromResource(this,
                R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapterDayOfWeek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etDayOfWeek.setAdapter(adapterDayOfWeek);

        ArrayAdapter<CharSequence> adapterClassType = ArrayAdapter.createFromResource(this,
                R.array.class_types, android.R.layout.simple_spinner_item);
        adapterClassType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etClassType.setAdapter(adapterClassType);

        // Lấy ID khóa học từ Intent
        Intent intent = getIntent();
        courseId = intent.getIntExtra("courseId", -1);

        if (courseId != -1) {
            loadCourseDetails(courseId);
        }

        // Xử lý sự kiện khi nhấn nút Update
        btnUpdate.setOnClickListener(v -> updateCourse());
    }

    // Hàm để tải chi tiết khóa học từ database
    private void loadCourseDetails(int id) {
        Cursor cursor = dbHelper.getCourseById(id);
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy giá trị và đặt vào Spinner
            etDayOfWeek.setSelection(getDayOfWeekPosition(cursor.getString(cursor.getColumnIndexOrThrow("day_of_week"))));

            // Lấy giá trị thời gian và chuyển vào TimePicker
            etTime.setHour(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("time")).split(":")[0]));
            etTime.setMinute(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("time")).split(":")[1]));

            // Hiển thị Capacity và Price
            etCapacity.setText(cursor.getString(cursor.getColumnIndexOrThrow("capacity")));
            etPrice.setText(cursor.getString(cursor.getColumnIndexOrThrow("price")));

            // Lấy và chuyển đổi Duration từ dạng hh:mm thành phút
            String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
            int minutes = convertDurationToMinutes(duration);
            etDuration.setText(String.valueOf(minutes)); // Hiển thị số phút

            // Đặt giá trị cho Spinner Class Type
            etClassType.setSelection(getClassTypePosition(cursor.getString(cursor.getColumnIndexOrThrow("class_type"))));

            // Đặt Description
            etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));

            cursor.close();
        }
    }

    // Hàm chuyển đổi Duration từ dạng hh:mm sang phút
    private int convertDurationToMinutes(String duration) {
        String[] parts = duration.split("h");
        int hours = Integer.parseInt(parts[0].trim());
        int minutes = 0;
        if (parts.length > 1) {
            minutes = Integer.parseInt(parts[1].replaceAll("[^0-9]", "").trim());
        }
        return hours * 60 + minutes; // Chuyển sang phút
    }

    // Hàm để lấy vị trí của ngày trong Spinner (Day of Week)
    private int getDayOfWeekPosition(String day) {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i].equals(day)) {
                return i;
            }
        }
        return 0; // Mặc định trả về Monday
    }

    // Hàm để lấy vị trí của Class Type trong Spinner
    private int getClassTypePosition(String classType) {
        String[] classTypes = {"Online", "Offline"};
        for (int i = 0; i < classTypes.length; i++) {
            if (classTypes[i].equals(classType)) {
                return i;
            }
        }
        return 0; // Mặc định trả về "Online"
    }

    // Hàm cập nhật khóa học
    private void updateCourse() {
        String dayOfWeek = etDayOfWeek.getSelectedItem().toString();
        int hour = etTime.getHour();
        int minute = etTime.getMinute();
        String time = String.format("%02d:%02d", hour, minute);

        // Lấy số phút người dùng nhập và chuyển thành giờ và phút
        int minutesInput = Integer.parseInt(etDuration.getText().toString().trim());
        int hours = minutesInput / 60;  // Tính số giờ
        int minutes = minutesInput % 60;  // Lấy số phút còn lại
        String duration = String.format("%dh%02d", hours, minutes);

        // Lấy giá trị còn lại
        int capacity = Integer.parseInt(etCapacity.getText().toString());
        double price = Double.parseDouble(etPrice.getText().toString());
        String classType = etClassType.getSelectedItem().toString();
        String description = etDescription.getText().toString();

        // Tạo đối tượng Course mới và cập nhật vào database
        Course course = new Course(dayOfWeek, time, capacity, duration, price, classType, description);
        course.setId(courseId);

        dbHelper.updateCourse(course);

        Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}

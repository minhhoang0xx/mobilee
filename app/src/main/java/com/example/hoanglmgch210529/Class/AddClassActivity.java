package com.example.hoanglmgch210529.Class;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hoanglmgch210529.Model.ClassInstance;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {

    private EditText etDate, etTeacher, etComments;
    private Button btnSubmit;
    private CourseDatabaseHelper dbHelper;
    private int courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        etDate = findViewById(R.id.etDate);
        etTeacher = findViewById(R.id.etTeacher);
        etComments = findViewById(R.id.etComments);
        btnSubmit = findViewById(R.id.btnSubmit);

        dbHelper = new CourseDatabaseHelper(this);
        courseId = getIntent().getIntExtra("courseId", -1);

        // Thiết lập DatePicker cho etDate
        etDate.setOnClickListener(v -> showDatePicker());

        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                String date = etDate.getText().toString().trim();
                String teacher = etTeacher.getText().toString().trim();
                String comments = etComments.getText().toString().trim();

                // Chuyển đổi định dạng ngày thành chuẩn phù hợp với cơ sở dữ liệu
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = null;
                try {
                    formattedDate = sdf.format(sdf.parse(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tạo đối tượng ClassInstance
                ClassInstance classInstance = new ClassInstance(courseId, formattedDate, teacher, comments);
                dbHelper.addClassInstance(classInstance); // Thêm vào cơ sở dữ liệu

                // Gửi thông tin kết quả về ClassListActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newClassInstance", classInstance); // Truyền đối tượng classInstance mới
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "Class instance added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Đóng AddClassActivity
            }
        });
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            // Định dạng ngày đã chọn
            String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);

            // Kiểm tra ngày đã chọn có trùng với dayOfWeek của khóa học hay không
            if (isDateValid(selectedDate)) {
                etDate.setText(formattedDate);
            } else {
                Toast.makeText(this, "Selected date does not match the course schedule!", Toast.LENGTH_SHORT).show();
            }
        }, year, month, day);

        // Không cho phép chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private boolean isDateValid(Calendar selectedDate) {
        // Lấy dayOfWeek từ cơ sở dữ liệu
        String courseDayOfWeek = getCourseDayOfWeekFromDatabase(courseId);

        // Chuyển đổi dayOfWeek từ String sang int
        int courseDayOfWeekInt = convertDayOfWeekToInt(courseDayOfWeek);

        // Lấy ngày trong tuần từ ngày đã chọn
        int selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

        // Kiểm tra nếu ngày đã chọn trùng với lịch học
        return selectedDayOfWeek == courseDayOfWeekInt;
    }

    // Hàm chuyển đổi dayOfWeek từ String sang int (1 = Chủ nhật, 2 = Thứ Hai, ..., 7 = Thứ Bảy)
    private int convertDayOfWeekToInt(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Sunday":
                return 1;
            case "Monday":
                return 2;
            case "Tuesday":
                return 3;
            case "Wednesday":
                return 4;
            case "Thursday":
                return 5;
            case "Friday":
                return 6;
            case "Saturday":
                return 7;
            default:
                // Trả về giá trị mặc định hoặc throw Exception nếu không hợp lệ
                return -1;
        }
    }

    // Hàm lấy dayOfWeek từ cơ sở dữ liệu
    private String getCourseDayOfWeekFromDatabase(int courseId) {
        // Giả sử bạn đã lấy dayOfWeek từ database, trả về giá trị String
        // Ví dụ: nếu ngày học là Thứ Ba, bạn sẽ trả về "Tuesday"
        return dbHelper.getCourseDayOfWeek(courseId);
    }

    private boolean validateInput() {
        // Kiểm tra nếu trường "Date" không được điền
        if (etDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Date is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Kiểm tra nếu trường "Teacher" không được điền
        if (etTeacher.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Teacher is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

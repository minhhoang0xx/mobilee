package com.example.hoanglmgch210529.Class;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoanglmgch210529.Model.ClassInstance;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.adapter.ClassAdapter;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {

    private TextView tvInstance;
    private Button btnAddInstance;
    private RecyclerView rvInstance;
    private EditText etSearch;
    private ImageButton btnBack;
    private CourseDatabaseHelper dbHelper;
    private List<ClassInstance> classInstances = new ArrayList<>();
    private int courseId;
    private ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        // Ánh xạ các thành phần giao diện
        tvInstance = findViewById(R.id.tvInstance);
        btnAddInstance = findViewById(R.id.btnAddInstance);
        rvInstance = findViewById(R.id.rvInstance);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo database helper
        dbHelper = new CourseDatabaseHelper(this);

        // Lấy courseId và courseName từ Intent
        courseId = getIntent().getIntExtra("courseId", -1);
        String courseName = getIntent().getStringExtra("courseName");
        tvInstance.setText("Class Instances for " + courseName);

        // Thiết lập RecyclerView
        rvInstance.setLayoutManager(new LinearLayoutManager(this));

        // Tải dữ liệu class instances ban đầu
        loadClassInstances();
        adapter = new ClassAdapter(this, classInstances);
        rvInstance.setAdapter(adapter);
        // Xử lý tìm kiếm khi người dùng nhập vào ô tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClasses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        btnBack.setOnClickListener(v -> onBackPressed());
        // Xử lý sự kiện thêm instance mới
        btnAddInstance.setOnClickListener(v -> addClassInstance());
    }

    // Phương thức tải dữ liệu từ cơ sở dữ liệu
    private void loadClassInstances() {
        classInstances.clear(); // Đảm bảo danh sách trống trước khi thêm mới
        classInstances.addAll(dbHelper.getClassInstancesForCourse(courseId));
    }

    // Phương thức lọc các lớp học dựa trên từ khóa
    private void filterClasses(String query) {
        List<ClassInstance> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(dbHelper.getClassInstancesForCourse(courseId));
        } else {
            filteredList.addAll(dbHelper.searchClassInstances(query));
        }

        classInstances.clear();
        classInstances.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    // Phương thức xử lý khi thêm class instance mới
    private void addClassInstance() {
        Intent intent = new Intent(this, AddClassActivity.class);
        intent.putExtra("courseId", courseId);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    // Phương thức làm mới dữ liệu trong RecyclerView
    public void refreshData() {
        loadClassInstances();
        adapter.notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Lấy class instance mới từ Intent
            ClassInstance newInstance = (ClassInstance) data.getSerializableExtra("newClassInstance");
            if (newInstance != null) {
                classInstances.add(newInstance);
                adapter.notifyItemInserted(classInstances.size() - 1);
                rvInstance.scrollToPosition(classInstances.size() - 1);
            }
        }
    }
}

package com.example.hoanglmgch210529.Course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoanglmgch210529.Model.Course;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.adapter.CourseAdapter;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private CourseDatabaseHelper dbHelper;
    private RecyclerView rvCourses;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        dbHelper = new CourseDatabaseHelper(this);
        rvCourses = findViewById(R.id.rvCourses);
        Button btnAddCourse = findViewById(R.id.btnAddCourse);

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(this, courseList);

        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(courseAdapter);

        loadCourses();

        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, AddCourseActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void loadCourses() {
        courseList.clear();
        courseList.addAll(dbHelper.getAllCourses());
        courseAdapter.notifyDataSetChanged();

        //goi api de lay ds khoa hoc yoga moi nhat

        //update khoa hoc yoga => dbHepler.updateAllCourses()

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadCourses(); // Tải lại danh sách khóa học sau khi thêm mới
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses(); // Tải lại danh sách mỗi khi activity được resume
    }
}
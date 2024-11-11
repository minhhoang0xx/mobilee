package com.example.hoanglmgch210529.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoanglmgch210529.Class.ClassListActivity;
import com.example.hoanglmgch210529.Course.CourseDetailActivity;
import com.example.hoanglmgch210529.Model.Course;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;
    private CourseDatabaseHelper dbHelper;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
        this.dbHelper = new CourseDatabaseHelper(context); // Khởi tạo database helper
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        String priceFormatted = String.format("%.0f", course.getPrice());
        holder.tvCourseName.setText(course.getClassType() + " - " + course.getDayOfWeek() + " - " + course.getTime());
        holder.tvCourseDetails.setText("Capacity: " + course.getCapacity() + " | Duration: " + course.getDuration() +" | Price: £" + priceFormatted);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getId());
            context.startActivity(intent);
        });
        // Set listener cho nút xóa
        holder.btnDelete.setOnClickListener(v-> {
            showDeleteDialog(position);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassListActivity.class);
            intent.putExtra("courseId", course.getId());
            context.startActivity(intent);
        });
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Class");
        builder.setMessage("Are you sure you want to delete this class?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Course courseDelete = courseList.get(position);
                dbHelper.deleteCourse(courseDelete.getId()); // Giả sử có phương thức deleteCourse trong CourseDatabaseHelper
                courseList.remove(position);  // Xóa khóa học khỏi danh sách
                notifyItemRemoved(position);  // Cập nhật lại RecyclerView
                Toast.makeText(context, "Class deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
    public void updateCourses(List<Course> newCourseList) {
        this.courseList = newCourseList;
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        TextView tvCourseDetails;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseDetails = itemView.findViewById(R.id.tvCourseDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}

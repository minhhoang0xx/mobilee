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
import com.example.hoanglmgch210529.Class.ClassDetailActivity;
import com.example.hoanglmgch210529.Model.ClassInstance;
import com.example.hoanglmgch210529.R;
import com.example.hoanglmgch210529.db.CourseDatabaseHelper;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private Context context;
    private List<ClassInstance> instanceList;
    private CourseDatabaseHelper dbHelper;

    public ClassAdapter(Context context, List<ClassInstance> instanceList) {
        this.context = context;
        this.instanceList = instanceList;
        this.dbHelper = new CourseDatabaseHelper(context); // Khởi tạo database helper
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassInstance classInstance = instanceList.get(position);

        // Hiển thị thông tin của từng `ClassInstance`
        holder.tvClassInfo.setText(  "Teacher: " + classInstance.getTeacher());
        holder.tvClassDetails.setText(classInstance.getDate());

        // Listener cho nút "Edit"
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassDetailActivity.class);
            intent.putExtra("instanceId", classInstance.getId());
            intent.putExtra("courseId", classInstance.getCourseId());
            context.startActivity(intent);
        });

        // Listener cho nút "Delete"
        holder.btnDelete.setOnClickListener(v -> showDeleteDialog(position));
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Class");
        builder.setMessage("Are you sure you want to delete this class?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClassInstance instanceToDelete = instanceList.get(position);
                dbHelper.deleteClassInstance(instanceToDelete.getId()); // Giả sử có phương thức deleteClassInstance trong CourseDatabaseHelper
                instanceList.remove(position);  // Xóa khỏi danh sách
                notifyItemRemoved(position);  // Cập nhật lại RecyclerView
                Toast.makeText(context, "Class deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return instanceList.size();
    }


    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassInfo;
        TextView tvClassDetails;
        ImageButton btnDelete;
        ImageButton btnEdit;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClassInfo = itemView.findViewById(R.id.tvClassInfo);
            tvClassDetails = itemView.findViewById(R.id.tvClassDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}

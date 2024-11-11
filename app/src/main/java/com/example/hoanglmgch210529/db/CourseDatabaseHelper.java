package com.example.hoanglmgch210529.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hoanglmgch210529.Model.ClassInstance;
import com.example.hoanglmgch210529.Model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yoga_courses.db";
    private static final int DATABASE_VERSION = 2;
    // table and colum of Course
    public static final String TABLE_COURSES = "courses";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CLASS_TYPE = "class_type";
    public static final String COLUMN_DESCRIPTION = "description";


    // Table and column names for Class Instances
    public static final String TABLE_CLASS_INSTANCES = "class_instances";
    public static final String COLUMN_INSTANCE_ID = "id";
    public static final String COLUMN_INSTANCE_COURSE_ID = "course_id";
    public static final String COLUMN_INSTANCE_DATE = "date";
    public static final String COLUMN_INSTANCE_TEACHER = "teacher";
    public static final String COLUMN_INSTANCE_COMMENTS = "comments";

    public CourseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DAY_OF_WEEK + " TEXT NOT NULL,"
                + COLUMN_TIME + " TEXT NOT NULL,"
                + COLUMN_CAPACITY + " INTEGER NOT NULL,"
                + COLUMN_DURATION + " TEXT NOT NULL,"
                + COLUMN_PRICE + " REAL NOT NULL,"
                + COLUMN_CLASS_TYPE + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_COURSES_TABLE);
// Create Class Instances table
        String CREATE_CLASS_INSTANCES_TABLE = "CREATE TABLE " + TABLE_CLASS_INSTANCES + "("
                + COLUMN_INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_INSTANCE_COURSE_ID + " INTEGER NOT NULL,"
                + COLUMN_INSTANCE_DATE + " TEXT NOT NULL,"
                + COLUMN_INSTANCE_TEACHER + " TEXT NOT NULL,"
                + COLUMN_INSTANCE_COMMENTS + " TEXT,"
                + "FOREIGN KEY (" + COLUMN_INSTANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_CLASS_INSTANCES_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_INSTANCES);
            onCreate(db);
        }
    }

    // Thêm khóa học vào cơ sở dữ liệu
    public void addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_CLASS_TYPE, course.getClassType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());
        db.insert(TABLE_COURSES, null, values);
        db.close();
    }

    // Lấy danh sách tất cả các khóa học
    public List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Kiểm tra chỉ số cột trước khi lấy giá trị
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int timeIndex = cursor.getColumnIndex(COLUMN_TIME);
                int capacityIndex = cursor.getColumnIndex(COLUMN_CAPACITY);
                int durationIndex = cursor.getColumnIndex(COLUMN_DURATION);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int classTypeIndex = cursor.getColumnIndex(COLUMN_CLASS_TYPE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                // Kiểm tra xem chỉ số cột có hợp lệ không
                if (idIndex != -1 && dayOfWeekIndex != -1 && timeIndex != -1 && capacityIndex != -1
                        && durationIndex != -1 && priceIndex != -1 && classTypeIndex != -1) {

                    // Lấy giá trị của các cột nếu chỉ số hợp lệ
                    int id = cursor.getInt(idIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String time = cursor.getString(timeIndex);
                    int capacity = cursor.getInt(capacityIndex);
                    String duration = cursor.getString(durationIndex);
                    double price = cursor.getDouble(priceIndex);
                    String classType = cursor.getString(classTypeIndex);
                    String description = cursor.getString(descriptionIndex); // Có thể là null

                    // Tạo đối tượng Course với dữ liệu từ database
                    Course course = new Course(dayOfWeek, time, capacity, duration, price, classType, description);
                    course.setId(id);

                    // Thêm khóa học vào danh sách
                    courseList.add(course);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courseList;
    }


    public void deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Kiểm tra nếu ID hợp lệ
        if (courseId > 0) {
            db.delete(TABLE_COURSES, COLUMN_ID + " = ?", new String[]{String.valueOf(courseId)});
        }
        db.close();
    }

    // Phương thức cập nhật khóa học
    public void updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_CLASS_TYPE, course.getClassType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());

        // Cập nhật theo ID
        db.update(TABLE_COURSES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(course.getId())});
        db.close();
    }

    // Phương thức lấy khóa học theo ID

    public Cursor getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSES + " WHERE " + COLUMN_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(courseId)});
    }

    // Method to add a class instance
    public void addClassInstance(ClassInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INSTANCE_COURSE_ID, instance.getCourseId());
        values.put(COLUMN_INSTANCE_DATE, instance.getDate());
        values.put(COLUMN_INSTANCE_TEACHER, instance.getTeacher());
        values.put(COLUMN_INSTANCE_COMMENTS, instance.getComments());
        db.insert(TABLE_CLASS_INSTANCES, null, values);
        db.close();
    }

    public List<ClassInstance> getAllClassInstances() {
        List<ClassInstance> instanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn lấy tất cả dữ liệu từ bảng ClassInstance
        Cursor cursor = db.query(TABLE_CLASS_INSTANCES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int courseIdIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COURSE_ID);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_DATE);
                int teacherIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_TEACHER);
                int commentsIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COMMENTS);
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID);

                do {
                    ClassInstance instance = new ClassInstance(
                            cursor.getInt(courseIdIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(teacherIndex),
                            cursor.getString(commentsIndex)
                    );
                    instance.setId(cursor.getInt(idIndex));
                    instanceList.add(instance);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        db.close();
        return instanceList;
    }


    // Method to retrieve all instances for a specific course
    public List<ClassInstance> getClassInstancesForCourse(int courseId) {
        List<ClassInstance> instanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn lấy dữ liệu với điều kiện courseId
        Cursor cursor = db.query(TABLE_CLASS_INSTANCES, null, COLUMN_INSTANCE_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int courseIdIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COURSE_ID);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_DATE);
                int teacherIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_TEACHER);
                int commentsIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COMMENTS);
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID);

                do {
                    ClassInstance instance = new ClassInstance(
                            cursor.getInt(courseIdIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(teacherIndex),
                            cursor.getString(commentsIndex)
                    );
                    instance.setId(cursor.getInt(idIndex));
                    instanceList.add(instance);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        db.close();
        return instanceList;
    }


     //Additional methods to update and delete class instances can be added // here as needed
     public void updateClassInstance(int instanceId, String date, String teacher, String comment) {
         SQLiteDatabase db = this.getWritableDatabase();

         ContentValues values = new ContentValues();
         values.put(COLUMN_INSTANCE_DATE, date);
         values.put(COLUMN_INSTANCE_TEACHER, teacher);
         values.put(COLUMN_INSTANCE_COMMENTS, comment);

         // Chỉ cập nhật nếu giá trị là hợp lệ (không phải null hoặc rỗng)
//         if (instance.getCourseId() > 0) {
//             db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instance.getId())});
//         } else {
//             Log.e("UpdateClassInstance", "Invalid Course ID");
//         }
         int rowsUpdated = db.update(TABLE_CLASS_INSTANCES, values, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
         db.close();
     }
    public Cursor getClassById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLASS_INSTANCES + " WHERE " + COLUMN_INSTANCE_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public void deleteClassInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_INSTANCES, COLUMN_INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
        db.close();
    }

    public List<ClassInstance> searchClassInstances(String keyword) {
        List<ClassInstance> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CLASS_INSTANCES + " WHERE "
                + COLUMN_INSTANCE_TEACHER + " LIKE ? OR "
                + "SUBSTR(" + COLUMN_INSTANCE_DATE + ", 1, 2) LIKE ?";

        String searchKeyword = "%" + keyword + "%";

        Cursor cursor = db.rawQuery(query, new String[]{searchKeyword, searchKeyword});

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_ID);
                int courseIdIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COURSE_ID);
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_DATE);
                int teacherIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_TEACHER);
                int commentsIndex = cursor.getColumnIndexOrThrow(COLUMN_INSTANCE_COMMENTS);

                do {
                    ClassInstance instance = new ClassInstance(
                            cursor.getInt(courseIdIndex),
                            cursor.getString(dateIndex),
                            cursor.getString(teacherIndex),
                            cursor.getString(commentsIndex)
                    );
                    instance.setId(cursor.getInt(idIndex));
                    results.add(instance);
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }
        db.close();
        return results;
    }


    public String getCourseDayOfWeek(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Truy vấn lấy day_of_week cho courseId tương ứng
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DAY_OF_WEEK + " FROM " + TABLE_COURSES + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(courseId)});

        String dayOfWeek = null; // Khởi tạo biến dayOfWeek là null
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy index của cột COLUMN_DAY_OF_WEEK
            int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);

            if (dayOfWeekIndex >= 0) {
                dayOfWeek = cursor.getString(dayOfWeekIndex);
            }
        }
        cursor.close();
        db.close();

        return dayOfWeek;
    }
}







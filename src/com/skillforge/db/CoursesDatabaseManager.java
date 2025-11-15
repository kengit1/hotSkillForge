package com.skillforge.db;
import com.skillforge.model.Course ;
public class CoursesDatabaseManager extends jsonDatabaseManager<Course>{
    CoursesDatabaseManager(String filePath, Class<Course> entityClass) {
        super(filePath, Course.class);
    }
    public static final String COURSES_FILE_PATH = "data/courses.json";
    // no need to use our custom serializer, it can work on the old constructor
    public CoursesDatabaseManager() {
        super(COURSES_FILE_PATH, Course.class);
    }
}

package com.skillforge.db;
import com.skillforge.model.Course ;
public class CoursesDatabaseManager extends jsonDatabaseManager<Course>{

    public static final String COURSES_FILE_PATH = "courses.json";
    // no need to use our custom deserializer, it can work on the old constructor
    public CoursesDatabaseManager() {
        super(COURSES_FILE_PATH, Course.class);
    }
    /*public static void main(String[] args)
    {
        CoursesDatabaseManager db = new CoursesDatabaseManager() ;
        Course c = new Course("C03","hell","repeant to god","5678") ;
        db.add(c) ;
        System.out.println(db.getDataList());
    }*/
}

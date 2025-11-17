package com.skillforge.model;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class Student extends User {
    private List<String> enrolledCourses; // List of courseIds
    public Student(String userID, String userName, String email, String passwordHash) {
        setUserID(userID);
        setUserName(userName);
        setRole("Student");
        setEmail(email);
        setPasswordHash(passwordHash);
        this.enrolledCourses=new ArrayList<>();


    }
    public Student(String userId, String role, String username, String email, String passwordHash,
                   List<String> enrolledCourses) {
        setUserID(userId);
        setUserName(username);
        setRole(role);
        setEmail(email);
        setPasswordHash(passwordHash);
        this.enrolledCourses = (enrolledCourses != null) ? enrolledCourses : new ArrayList<>();
    }

    public void enrollCourse(Course course) {
        this.enrolledCourses.add(course.getID());
        UserDatabaseManager db = null;
        db.update(this);
        CoursesDatabaseManager cd=null;
        cd.update(course);
        db.saveData();
        cd.saveData();
    }
    public List<String> getEnrolledCourses() { return enrolledCourses; }
    public List readfromfile()
    {
        List<User> data=new ArrayList<>();
        UserDatabaseManager ud=new UserDatabaseManager("users.json");

        if(ud==null)
        {
            System.out.println("didnt read shit");
        }
        data=ud.getDataList();

        return  data;
    }

    @Override
    public String getID() {
        return getUserID();
    }

    @Override
    public String toString() {
        return this.getID()+" "+this.getUserName()+" "+
                this.getRole()+" "+this.getEmail()+" ";
    }
}

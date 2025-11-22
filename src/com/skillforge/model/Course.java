package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;
import java.util.ArrayList;
import java.util.List;

public class Course implements DatabaseEntity {

    private String courseId;
    private String title;
    private String description;
    private String instructorId;

    // 🔥 NEW: course approval status (Lab 8 requirement)
    private String status;  // PENDING, APPROVED, REJECTED

    private List<Lesson> lessons;
    private List<String> students; // student IDs

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;

        this.status = "APPROVED"; // default for now

        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    @Override
    public String getID() {
        return this.courseId;
    }

    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Lesson> getLessons() { return lessons; }
    public List<String> getStudents() { return students; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    public void addLesson(Lesson lesson) {
        for (Lesson l : lessons) {
            if (l.getID().equals(lesson.getID())) {
                System.out.println("Lesson with ID " + lesson.getID() + " already exists.");
                return;
            }
        }
        lessons.add(lesson);
    }

    public Lesson getLesson(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getID().equals(lessonId)) {
                return lesson;
            }
        }
        System.out.println("Lesson not found: " + lessonId);
        return null;
    }

    public boolean deleteLesson(String lessonId) {
        Lesson lessonToRemove = getLesson(lessonId);
        if (lessonToRemove != null) {
            return lessons.remove(lessonToRemove);
        }
        return false;
    }

    public void addStudent(String studentId) {
        if (!students.contains(studentId)) {
            students.add(studentId);
        }
    }

    public boolean removeStudentId(String studentId) {
        return students.remove(studentId);
    }

    @Override
    public String toString() {
        return this.getID() + " " + this.getTitle() + " " + this.getDescription();
    }
}

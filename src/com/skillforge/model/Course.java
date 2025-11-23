package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;
import java.util.ArrayList;
import java.util.List;

public class Course implements DatabaseEntity {
    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<Lesson> lessons;
    private List<String> students;
    private String approvedstatus;
    private List<String> issuedCertificates;

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.issuedCertificates = new ArrayList<>();
    }

    @Override
    public String getID() {
        return this.courseId;
    }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getInstructorId() { return instructorId; }
    public List<Lesson> getLessons() { return lessons; }
    public List<String> getStudents() { return students; }
    public String getApprovalStatus() { return approvedstatus; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setApprovalStatus(String status){
        this.approvedstatus = status;
    }

    public void addLesson(Lesson lesson) {
        for (Lesson l : this.lessons) {
            if (l.getID().equals(lesson.getID())) {
                System.out.println("Lesson with ID " + lesson.getID() + " already exists in this course.");
                return;
            }
        }
        this.lessons.add(lesson);
    }


    public Lesson getLesson(String lessonId) {
        for (Lesson lesson : this.lessons) {
            if (lesson.getID().equals(lessonId)) {
                return lesson;
            }
        }
        System.out.println("Lesson not found!!");
        return null;
    }


    public boolean deleteLesson(String lessonId) {
        Lesson lessonToRemove = getLesson(lessonId);
        if (lessonToRemove != null) {
            return this.lessons.remove(lessonToRemove);
        }
        return false;
    }

    public void addStudent(String studentId) {
        if (!this.students.contains(studentId)) {
            this.students.add(studentId);
        }
    }

    public boolean removeStudentId(String studentId) {
        return this.students.remove(studentId);
    }

    public void addIssuedCertificate(String certId) {
        if (this.issuedCertificates == null) {
            this.issuedCertificates = new ArrayList<>();
        }
        if (!issuedCertificates.contains(certId))
            issuedCertificates.add(certId);
    }

    @Override
    public String toString() {
        return this.getID()+" "+this.getTitle()+" "+this.getDescription();
    }
}//
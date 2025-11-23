package com.skillforge.model;

import com.skillforge.db.DatabaseEntity;

public class Certificate implements DatabaseEntity {

    private String certificateId;   // unique ID
    private String studentId;
    private String courseId;
    private String issueDate;       // as string: "2025-11-23"
    private String filePath;        // certificate PDF or JSON path

    public Certificate(String certificateId, String studentId, String courseId, String issueDate, String filePath) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.issueDate = issueDate;
        this.filePath = filePath;
    }

    @Override
    public String getID() {
        return certificateId;
    }

    public String getCertificateId() { return certificateId; }
    public String getStudentId() { return studentId; }
    public String getCourseId() { return courseId; }
    public String getIssueDate() { return issueDate; }
    public String getFilePath() { return filePath; }

    public void setFilePath(String path) { this.filePath = path; }
}


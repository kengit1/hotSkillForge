package com.skillforge.model;

import com.skillforge.db.*;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class CertificateService {

    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;
    private final CertificateDatabaseManager certDB;

    public CertificateService(CoursesDatabaseManager courseDB,
                              UserDatabaseManager userDB,
                              CertificateDatabaseManager certDB) {
        this.courseDB = courseDB;
        this.userDB = userDB;
        this.certDB = certDB;
    }

    public Optional<Certificate> generateCertificate(String studentId, String courseId) {

        Student student = (Student) userDB.findById(studentId);
        Course course = courseDB.findById(courseId);

        if (student == null || course == null) return Optional.empty();

        // verify completion
        List<String> completed = student.getProgress().get(courseId);
        if (completed == null || completed.size() != course.getLessons().size())
            return Optional.empty();   // still not completed all lessons

        String certId = "CERT-" + System.currentTimeMillis();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String filePath = "certificates/" + certId + ".json";

        Certificate cert = new Certificate(certId, studentId, courseId, date, filePath);

        java.io.File certDir = new java.io.File("certificates");
        if (!certDir.exists()) {
            certDir.mkdirs(); // Creates the directory and any necessary parent directories
        }

        // Save certificate JSON file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(
                    "{\n" +
                            "  \"certificateId\": \"" + certId + "\",\n" +
                            "  \"studentId\": \"" + studentId + "\",\n" +
                            "  \"courseId\": \"" + courseId + "\",\n" +
                            "  \"issueDate\": \"" + date + "\"\n" +
                            "}"
            );
        } catch (Exception e) { e.printStackTrace(); }

        // Save cert in database
        certDB.add(cert);
        certDB.saveData();

        // Add reference to student
        student.addCertificate(certId);
        userDB.update(student);
        userDB.saveData();
        course.addIssuedCertificate(certId);
        courseDB.update(course);
        courseDB.saveData();

        return Optional.of(cert);
    }

    public List<Certificate> getStudentCertificates(String studentId) {
        return certDB.getCertificatesForStudent(studentId);
    }
}

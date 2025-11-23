package com.skillforge.db;

import com.skillforge.model.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CertificateDatabaseManager extends jsonDatabaseManager<Certificate> {

    public static final String FILE_PATH = "certificates.json";

    public CertificateDatabaseManager() {
        super(FILE_PATH, Certificate.class);
    }

    // Get all certificates of one student
    public List<Certificate> getCertificatesForStudent(String studentId) {
        return getDataList().stream()
                .filter(c -> c.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

}

package com.skillforge.model;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseService {

    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;

    public CourseService(CoursesDatabaseManager courseDB, UserDatabaseManager userDB) {
        this.courseDB = courseDB;
        this.userDB = userDB;
    }

    public Optional<Course> createCourse(String title, String description, String instructorId) {
        String newCourseId = "C-" + System.currentTimeMillis();

        Course newCourse = new Course(newCourseId, title, description, instructorId);
        newCourse.setApprovalStatus("pending");

        if (!courseDB.add(newCourse)) {
            return Optional.empty();
        }

        courseDB.saveData();
        return Optional.of(newCourse);
    }

    public boolean editCourseDetails(String courseId, String newTitle, String newDescription) {
        Course course = courseDB.findById(courseId);
        if (course == null) return false;

        course.setTitle(newTitle);
        course.setDescription(newDescription);

        boolean success = courseDB.update(course);
        if (success) courseDB.saveData();
        return success;
    }

    public boolean addLessonToCourse(String courseId, String title, String content) {
        Course course = courseDB.findById(courseId);
        if (course == null) return false;

        int lessonCount = course.getLessons().size();
        String newLessonId = courseId + "-L" + (lessonCount + 1);

        Lesson newLesson = new Lesson(newLessonId, title, content);
        course.addLesson(newLesson);

        boolean success = courseDB.update(course);
        if (success) courseDB.saveData();
        return success;
    }

    public boolean deleteLessonFromCourse(String courseId, String lessonId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return false;

        boolean success = course.deleteLesson(lessonId);

        if (success) {
            courseDB.update(course);
            courseDB.saveData();
        }
        return success;
    }

    public List<Course> getPendingCourses() {
        return courseDB.getAll().stream()
                .filter(c -> "pending".equalsIgnoreCase(c.getApprovalStatus()))
                .collect(Collectors.toList());
    }

    public boolean approveCourse(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return false;

        course.setApprovalStatus("approved");

        boolean success = courseDB.update(course);
        if (success) courseDB.saveData();
        return success;
    }

    public boolean rejectCourse(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return false;

        course.setApprovalStatus("rejected");

        boolean success = courseDB.update(course);
        if (success) courseDB.saveData();
        return success;
    }
}

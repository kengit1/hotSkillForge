package com.skillforge.main;

import com.skillforge.db.CoursesDatabaseManager;
import com.skillforge.db.UserDatabaseManager;
import com.skillforge.model.Course;
import com.skillforge.model.Lesson;
import com.skillforge.model.Student;
import com.skillforge.model.Lesson.QuizResult;

import java.util.*;
import java.util.stream.Collectors;

public class InstructorAnalyticsService {

    private final CoursesDatabaseManager courseDB;
    private final UserDatabaseManager userDB;

    public InstructorAnalyticsService(CoursesDatabaseManager courseDB, UserDatabaseManager userDB) {
        this.courseDB = courseDB;
        this.userDB = userDB;
    }

    public Map<String, Double> getAverageScorePerLesson(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return Collections.emptyMap();
        Map<String, Double> map = new LinkedHashMap<>();
        for (Lesson lesson : course.getLessons()) {
            List<QuizResult> results = lesson.getQuizResults();
            double avg = results.isEmpty() ? 0.0 :
                    results.stream().mapToDouble(QuizResult::getScore).average().orElse(0.0);
            map.put(lesson.getLessonId(), avg);
        }
        return map;
    }

    public Map<String, Double> getCompletionPercentPerLesson(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return Collections.emptyMap();
        int totalStudents = course.getStudents() == null ? 0 : course.getStudents().size();
        Map<String, Double> map = new LinkedHashMap<>();
        for (Lesson lesson : course.getLessons()) {
            int completed = lesson.getCompletedStudents() == null ? 0 : lesson.getCompletedStudents().size();
            double pct = totalStudents == 0 ? 0.0 : ((double)completed / totalStudents) * 100.0;
            map.put(lesson.getLessonId(), pct);
        }
        return map;
    }

    public List<Map.Entry<String, Double>> getStudentRankingForCourse(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return List.of();
        // aggregate student average scores across lessons in the course
        Map<String, List<Double>> perStudentScores = new HashMap<>();
        for (Lesson lesson : course.getLessons()) {
            for (QuizResult r : lesson.getQuizResults()) {
                perStudentScores.computeIfAbsent(r.getStudentId(), k -> new ArrayList<>()).add(r.getScore());
            }
        }
        Map<String, Double> avgMap = new HashMap<>();
        for (Map.Entry<String, List<Double>> e : perStudentScores.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(d -> d).average().orElse(0.0);
            avgMap.put(e.getKey(), avg);
        }
        return avgMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getCompletionCounts(String courseId) {
        Course course = courseDB.findById(courseId);
        if (course == null) return Collections.emptyMap();
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Lesson lesson : course.getLessons()) {
            int completed = lesson.getCompletedStudents() == null ? 0 : lesson.getCompletedStudents().size();
            map.put(lesson.getLessonId(), completed);
        }
        return map;
    }
}

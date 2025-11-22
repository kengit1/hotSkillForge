package com.skillforge.main;

import com.skillforge.model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AnalyticsTabbedFrame extends JFrame {

    private final Course course;
    private final InstructorAnalyticsService analyticsService;

    public AnalyticsTabbedFrame(Frame owner, Course course, InstructorAnalyticsService service) {
        super("Analytics - " + course.getTitle());
        this.course = course;
        this.analyticsService = service;

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // 1) Summary table
        JPanel summaryPanel = new JPanel(new BorderLayout());
        String[] cols = {"Lesson ID", "Title", "Avg Score", "Completed", "Total Students", "Completion %"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        Map<String, Double> avgs = analyticsService.getAverageScorePerLesson(course.getCourseId());
        Map<String, Double> pct = analyticsService.getCompletionPercentPerLesson(course.getCourseId());
        Map<String, Integer> counts = analyticsService.getCompletionCounts(course.getCourseId());

        int totalStudents = course.getStudents() == null ? 0 : course.getStudents().size();
        course.getLessons().forEach(l -> {
            String lid = l.getLessonId();
            double a = avgs.getOrDefault(lid, 0.0);
            int c = counts.getOrDefault(lid, 0);
            double p = pct.getOrDefault(lid, 0.0);
            model.addRow(new Object[]{lid, l.getTitle(), String.format("%.2f", a), c, totalStudents, String.format("%.2f%%", p)});
        });

        JTable table = new JTable(model);
        summaryPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tabs.addTab("Summary", summaryPanel);

        // 2) Score Chart (bar)
        JPanel scoreChartPanel = new JPanel(new BorderLayout());
        scoreChartPanel.add(new JLabel("Average Score per Lesson", SwingConstants.CENTER), BorderLayout.NORTH);
        scoreChartPanel.add(new BarChartPanel(avgs, "Average Score (%)"), BorderLayout.CENTER);
        tabs.addTab("Score Chart", scoreChartPanel);

        // 3) Completion Chart (bar)
        Map<String, Double> completionPct = pct;
        JPanel completionChartPanel = new JPanel(new BorderLayout());
        completionChartPanel.add(new JLabel("Completion Percentage per Lesson", SwingConstants.CENTER), BorderLayout.NORTH);
        completionChartPanel.add(new BarChartPanelDouble(completionPct, "Completion (%)"), BorderLayout.CENTER);
        tabs.addTab("Completion Chart", completionChartPanel);

        // 4) Student Ranking
        JPanel rankingPanel = new JPanel(new BorderLayout());
        rankingPanel.add(new JLabel("Student Ranking (by average score)", SwingConstants.CENTER), BorderLayout.NORTH);
        DefaultListModel<String> rankingModel = new DefaultListModel<>();
        List<Map.Entry<String, Double>> ranking = analyticsService.getStudentRankingForCourse(course.getCourseId());
        for (Map.Entry<String, Double> e : ranking) {
            String studentName = e.getKey();
            // try lookup name via userDB is not available here; show ID and score
            rankingModel.addElement(studentName + " → Avg: " + String.format("%.2f", e.getValue()));
        }
        JList<String> rankingList = new JList<>(rankingModel);
        rankingPanel.add(new JScrollPane(rankingList), BorderLayout.CENTER);
        tabs.addTab("Student Ranking", rankingPanel);

        add(tabs, BorderLayout.CENTER);
    }

    // Simple bar chart panel for double values (0-100)
    private static class BarChartPanelDouble extends JPanel {
        private final Map<String, Double> data;
        private final String label;

        BarChartPanelDouble(Map<String, Double> data, String label) {
            this.data = data;
            this.label = label;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.drawString("No data available", 20, 20);
                return;
            }
            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int barWidth = Math.max(20, (w - padding * 2) / Math.max(1, data.size()) - 10);
            int x = padding;
            int max = 100; // percent scale
            int idx = 0;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                double val = e.getValue();
                int barHeight = (int) ((h - padding * 2) * (val / max));
                int y = h - padding - barHeight;
                g.setColor(new Color(80, 130, 200));
                g.fillRect(x, y, barWidth, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, barWidth, barHeight);
                // label
                g.drawString(e.getKey(), x, h - padding + 15);
                g.drawString(String.format("%.1f", val), x, y - 5);
                x += barWidth + 10;
                idx++;
            }
            g.drawString(label, getWidth()/2 - 30, 15);
        }
    }

    // Simple bar chart for double values but using Double map already
    private static class BarChartPanel extends JPanel {
        private final Map<String, Double> data;
        private final String label;

        BarChartPanel(Map<String, Double> data, String label) {
            this.data = data;
            this.label = label;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.drawString("No data available", 20, 20);
                return;
            }
            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int barWidth = Math.max(20, (w - padding * 2) / Math.max(1, data.size()) - 10);
            int x = padding;
            double maxVal = data.values().stream().mapToDouble(d -> d).max().orElse(100.0);
            if (maxVal < 1) maxVal = 1;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                double val = e.getValue();
                int barHeight = (int) ((h - padding * 2) * (val / maxVal));
                int y = h - padding - barHeight;
                g.setColor(new Color(80, 180, 120));
                g.fillRect(x, y, barWidth, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, barWidth, barHeight);
                g.drawString(e.getKey(), x, h - padding + 15);
                g.drawString(String.format("%.1f", val), x, y - 5);
                x += barWidth + 10;
            }
            g.drawString(label, getWidth()/2 - 30, 15);
        }
    }
}

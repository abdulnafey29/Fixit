package com.fixit.dto.response;

import java.time.LocalDateTime;

public class FeedbackStatusResponse {

    private boolean submitted;
    private Integer rating;
    private String comment;
    private LocalDateTime submittedAt;
    private String studentName;

    public FeedbackStatusResponse() {
        this.submitted = false;
    }

    public static FeedbackStatusResponse notSubmitted() {
        FeedbackStatusResponse res = new FeedbackStatusResponse();
        res.setSubmitted(false);
        return res;
    }

    public static FeedbackStatusResponse submitted(Integer rating, String comment, LocalDateTime submittedAt, String studentName) {
        FeedbackStatusResponse res = new FeedbackStatusResponse();
        res.setSubmitted(true);
        res.setRating(rating);
        res.setComment(comment);
        res.setSubmittedAt(submittedAt);
        res.setStudentName(studentName);
        return res;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}

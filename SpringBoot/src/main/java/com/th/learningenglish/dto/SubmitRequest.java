package com.th.learningenglish.dto;

import java.util.Map;

public class SubmitRequest {
    private Long lessonId;
    private Map<String, String> answers;

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
}

package gg.v.yeikovych.interview_bespoke.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Token {

    private String content;
    private LocalDateTime timeIssued;
    private LocalDateTime timeExpires;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeIssued() {
        return timeIssued;
    }

    public void setTimeIssued(LocalDateTime timeIssued) {
        this.timeIssued = timeIssued;
    }

    public LocalDateTime getTimeExpires() {
        return timeExpires;
    }

    public void setTimeExpires(LocalDateTime timeExpires) {
        this.timeExpires = timeExpires;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(timeExpires);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token)) return false;
        return Objects.equals(content, token.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "Token{" +
                "content='" + content + '\'' +
                ", timeGiven=" + timeIssued +
                ", timeExpires=" + timeExpires +
                '}';
    }
}

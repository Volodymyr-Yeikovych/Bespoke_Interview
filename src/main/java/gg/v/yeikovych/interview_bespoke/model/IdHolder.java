package gg.v.yeikovych.interview_bespoke.model;

import java.util.Objects;

public class IdHolder {

    private String userId;


    public String getUserId() {
        return userId;
    }

    public IdHolder(String userId) {
        this.userId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "IdHolder{" +
                "userId='" + userId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdHolder idHolder)) return false;
        return Objects.equals(userId, idHolder.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

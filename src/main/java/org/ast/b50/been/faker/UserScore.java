package org.ast.b50.been.faker;

public class UserScore {
    private long userId;
    private UserRating userRating;

    // Getters and Setters
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "userId=" + userId +
                ", userRating=" + userRating +
                '}';
    }

    public UserRating getUserRating() {
        return userRating;
    }

    public void setUserRating(UserRating userRating) {
        this.userRating = userRating;
    }
}


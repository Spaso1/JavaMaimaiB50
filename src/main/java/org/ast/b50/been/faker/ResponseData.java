package org.ast.b50.been.faker;

public class ResponseData {
    private int returnCode;
    private String lastLoginDate;
    private Integer loginCount;
    private Integer consecutiveLoginCount;
    private String loginId;

    // Getters and Setters
    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Integer getConsecutiveLoginCount() {
        return consecutiveLoginCount;
    }

    public void setConsecutiveLoginCount(Integer consecutiveLoginCount) {
        this.consecutiveLoginCount = consecutiveLoginCount;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}

package com.xxxx.crm.model;

/**
 * 返回模型  用户名，密码，真实姓名
 */
public class UserModel {
    //private Integer userId;
    private String userIdStr;
    private String userName;
    private String trueName;
/*
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
*/
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }
}

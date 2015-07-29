package com.contest.mobathon.dao;

import java.util.Map;

/**
 * Created by baratheraja on 29/7/15.
 */
public class UserLoginDao {
    public Boolean error = false;
    public String uid;
    public String error_msg;
    public Map<String ,String> user;

    public UserLoginDao() {
    }

    public UserLoginDao(Boolean error,String error_msg) {
        this.error = error;
        this.error_msg = error_msg;
    }
    public UserLoginDao(Boolean error, String uid, Map<String, String> user) {
        this.error = error;
        this.uid = uid;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, String> getUser() {
        return user;
    }

    public void setUser(Map<String, String> user) {
        this.user = user;
    }
}

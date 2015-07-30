package com.contest.mobathon.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by baratheraja on 30/7/15.
 */
public class ReferalDAO {
    public String id;
    public String name;
    public String total_purchase;
    public String points;
    public List<Map<String,String>> referals;

    public ReferalDAO() {
    }

    public ReferalDAO(String id, String name, String total_purchase, String points, List<Map<String, String>> referals) {
        this.id = id;
        this.name = name;
        this.total_purchase = total_purchase;
        this.points = points;
        this.referals = referals;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal_purchase() {
        return total_purchase;
    }

    public void setTotal_purchase(String total_purchase) {
        this.total_purchase = total_purchase;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public List<Map<String, String>> getReferals() {
        return referals;
    }

    public void setReferals(List<Map<String, String>> referals) {
        this.referals = referals;
    }
}

package org.example.mirai.plugin.pojo;

import java.util.List;

/**
 * @author Mu Yuchen
 * @date 2021/7/9 16:04
 */
public class User {
    public long id;
    public List<String> fundList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getFundList() {
        return fundList;
    }

    public void setFundList(List<String> fundList) {
        this.fundList = fundList;
    }
}

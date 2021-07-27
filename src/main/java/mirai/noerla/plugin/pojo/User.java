package mirai.noerla.plugin.pojo;

import java.util.List;

/**
 * @author Mu Yuchen
 * @date 2021/7/9 16:04
 */
public class User {
    public String id;
    public List<String> fundList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFundList() {
        return fundList;
    }

    public void setFundList(List<String> fundList) {
        this.fundList = fundList;
    }
}

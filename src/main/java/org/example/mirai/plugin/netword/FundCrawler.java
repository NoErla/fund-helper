package org.example.mirai.plugin.netword;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import org.example.mirai.plugin.JavaPluginMain;

public class FundCrawler {

    private static FundCrawler instance = new FundCrawler();

    private FundCrawler(){}

    public static FundCrawler getInstance(){
        return instance;
    }

    public JSONObject getIndustry () {
        try {
            String url = "https://api.doctorxiong.club/v1/stock/industry/rank";
            String result = HttpUtil.get(url);
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject;
        } catch (Exception e) {
            JavaPluginMain.INSTANCE.getLogger().error("第三方接口调用异常");
            throw new RuntimeException();
        }
    }

    public JSONObject getFund (String code) {
        try {
            String url = "https://api.doctorxiong.club/v1/fund?code="+code;
            String result = HttpUtil.get(url);
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject;
        } catch (Exception e) {
            JavaPluginMain.INSTANCE.getLogger().error("第三方接口调用异常");
            throw new RuntimeException();
        }
    }
}

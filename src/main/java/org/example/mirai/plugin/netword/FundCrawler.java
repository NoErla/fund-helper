package org.example.mirai.plugin.netword;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import org.example.mirai.plugin.JavaPluginMain;

public class FundCrawler {

    private static final FundCrawler instance = new FundCrawler();

    private FundCrawler(){}

    public static FundCrawler getInstance(){
        return instance;
    }

    public JSONObject getIndustry () {
        try {
            String url = "https://api.doctorxiong.club/v1/stock/industry/rank";
            String result = HttpUtil.get(url);
            return new JSONObject(result);
        } catch (Exception e) {
            JavaPluginMain.INSTANCE.getLogger().error("第三方接口调用异常");
            throw new RuntimeException();
        }
    }

    public JSONObject getFund (String code) {
        try {
            String url = "https://api.doctorxiong.club/v1/fund?code="+code;
            String result = HttpUtil.get(url);
            return new JSONObject(result);
        } catch (Exception e) {
            JavaPluginMain.INSTANCE.getLogger().error("第三方接口调用异常");
            throw new RuntimeException();
        }
    }

    public JSONObject getFunds (String[] codes) {
        try {
            String code = ArrayUtil.join(codes, ",");
            String url = "https://api.doctorxiong.club/v1/fund?code=" + code;
            String result = HttpUtil.get(url);
            return new JSONObject(result);
        } catch (Exception e) {
            JavaPluginMain.INSTANCE.getLogger().error("第三方接口调用异常");
            throw new RuntimeException();
        }
    }
}

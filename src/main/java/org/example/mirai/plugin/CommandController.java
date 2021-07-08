package org.example.mirai.plugin;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.example.mirai.plugin.annotation.UserCommand;
import org.example.mirai.plugin.netword.FundCrawler;

import java.util.Comparator;
import java.util.List;

/**
 * @author Mu Yuchen
 * @date 2021/7/8 15:21
 */
public class CommandController {

    private FundCrawler fundCrawler = FundCrawler.getInstance();

    @UserCommand(".今日板块")
    public String today(){
        JSONObject industry = fundCrawler.getIndustry();
        JSONArray data = industry.getJSONArray("data");
        List<JSONObject> arrayLists = data.toList(JSONObject.class);
        //排序
        arrayLists.sort(Comparator.comparingDouble(x -> x.getDouble("changePercent")));
        StringBuilder sb = new StringBuilder();
        sb.append("领涨行业：").append("\n");
        sb.append(arrayLists.get(arrayLists.size() - 1).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 1).getStr("changePercent")).append("\n");
        sb.append(arrayLists.get(arrayLists.size() - 2).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 2).getStr("changePercent")).append("\n");
        sb.append(arrayLists.get(arrayLists.size() - 3).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 3).getStr("changePercent")).append("\n");

        sb.append("领跌行业：").append("\n");

        sb.append(arrayLists.get(0).getStr("name")).append(":").append(arrayLists.get(0).getStr("changePercent")).append("\n");
        sb.append(arrayLists.get(1).getStr("name")).append(":").append(arrayLists.get(1).getStr("changePercent")).append("\n");
        sb.append(arrayLists.get(2).getStr("name")).append(":").append(arrayLists.get(2).getStr("changePercent"));
        return sb.toString();
    }

    @UserCommand(".基金")
    public String fund(String code){
        JSONObject fund = fundCrawler.getFund(code).getJSONArray("data").toList(JSONObject.class).get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("基金编号: ").append(code).append("\n");
        sb.append("基金名称: ").append(fund.getStr("name")).append("\n");
        sb.append("今日涨跌: ").append(fund.getStr("dayGrowth")).append("\n");
        sb.append("半年涨跌: ").append(fund.getStr("lastSixMonthsGrowth")).append("\n");
        sb.append("一年涨跌: ").append(fund.getStr("lastYearGrowth"));
        return sb.toString();
    }
}

package org.example.mirai.plugin;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.example.mirai.plugin.annotation.MiraiCommand;
import org.example.mirai.plugin.dao.FundDao;
import org.example.mirai.plugin.netword.FundCrawler;
import org.example.mirai.plugin.pojo.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mu Yuchen
 * @date 2021/7/8 15:21
 */
public class CommandController {

    private final FundCrawler fundCrawler = FundCrawler.getInstance();

    private final FundDao fundDao = FundDao.getInstance();

    private static final CommandController instance = new CommandController();

    private CommandController(){}

    public static CommandController getInstance(){
        return instance;
    }

    @MiraiCommand(value = ".今日板块", description = "查询今日板块情况（维护中），格式: .今日板块")
    public String dailyIndustry(){
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

    @MiraiCommand(value = ".基金", description = "查询指定基金详情，格式: .基金 <code>")
    public String fund(String code){
        JSONObject fund = fundCrawler.getFund(code).getJSONArray("data").toList(JSONObject.class).get(0);
        StringBuilder sb = new StringBuilder();
        sb.append("基金编号: ").append(code).append("\n");
        sb.append("基金名称: ").append(fund.getStr("name")).append("\n");
        sb.append("今日预估: ").append(fund.getStr("expectGrowth")).append("\n");
        sb.append("本周涨跌: ").append(fund.getStr("lastWeekGrowth")).append("\n");
        sb.append("本月涨跌: ").append(fund.getStr("lastMonthGrowth")).append("\n");
        sb.append("三月涨跌: ").append(fund.getStr("lastThreeMonthsGrowth")).append("\n");
        sb.append("半年涨跌: ").append(fund.getStr("lastSixMonthsGrowth")).append("\n");
        sb.append("一年涨跌: ").append(fund.getStr("lastYearGrowth"));
        return sb.toString();
    }

    @MiraiCommand(value = ".我的基金", description = "查询登记的基金情况，格式: .我的基金")
    public String myFund(String id){
        Optional<User> query = fundDao.query(id);
        //如果用户为空则抛出异常
        User user = query.orElseThrow(RuntimeException::new);
        StringBuilder sb = new StringBuilder();
        List<JSONObject> funds = fundCrawler.getFunds(user.getFundList().toArray(new String[0])).getJSONArray("data").toList(JSONObject.class);
        for (JSONObject fund : funds){
            sb.append(fund.getStr("name")).append(": ").append(fund.getStr("expectGrowth")).append("\n");
        }
        return sb.toString();
    }

    @MiraiCommand(value = ".添加自选", description = "登记基金，格式: .添加自选 <code1>,<code2>")
    public String saveFund(String code, String id){
        List<String> fundList = Arrays.stream(code.split(",")).collect(Collectors.toList());
        Optional<User> query = fundDao.query(id);
        //如果存在用户则修改
        if (query.isPresent()){
            User user = query.get();
            user.setFundList(fundList);
            fundDao.update(user);
        } else {
            //如果用户为空则添加记录
            User user = new User();
            user.setId(id);
            user.setFundList(fundList);
            fundDao.add(user);
        }
        return "添加成功";
    }

    @MiraiCommand(value = ".删除自选", description = "删除自己登记的所有记录，格式: .删除自选")
    public String deleteFund(String id){
        fundDao.delete(id);
        return "删除成功";
    }

    @MiraiCommand(value = ".help", description = "查询所有命令详情，格式: .help")
    public String help(String id){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : JavaPluginMain.commandDescription.entrySet()){
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("/n");
        }
        return sb.toString();
    }
}

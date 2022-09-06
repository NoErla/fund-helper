package mirai.noerla.plugin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import mirai.noerla.plugin.annotation.MiraiCommand;
import mirai.noerla.plugin.dao.FundDao;
import mirai.noerla.plugin.netword.FundCrawler;
import mirai.noerla.plugin.pojo.User;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;

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
    public String myFund(String id, MessageEvent messageEvent){
        Optional<User> query = fundDao.query(id);
        //如果用户为空则抛出异常
        User user = query.orElseThrow(RuntimeException::new);
        StringBuilder sb = new StringBuilder();
        List<JSONObject> funds = fundCrawler.getFunds(user.getFundList().toArray(new String[0])).getJSONArray("data").toList(JSONObject.class);
        Double total = 0.0;
        for (JSONObject fund : funds){
            sb.append(fund.getStr("name")).append("(").append(fund.getStr("code")).append(")").append(": ").append(fund.getStr("expectGrowth")).append("\n");
            Double single;
            try {
                single = Double.valueOf(fund.getStr("expectGrowth"));
            } catch (Exception e) {
                single = 0.0;
            }
            total += single;
        }
        final int i = RandomUtil.randomInt(1, 4);
        if (total <= 0) {
            messageEvent.getSubject().sendMessage(Contact.uploadImage(messageEvent.getSubject(), FileUtil.file(PluginConsts.BAD + i + ".jpg")));
        } else {
            messageEvent.getSubject().sendMessage(Contact.uploadImage(messageEvent.getSubject(), FileUtil.file(PluginConsts.GOOD + i + ".jpg")));
        }
        return sb.toString();
    }

    //TODO 多命令优化
    @MiraiCommand(value = ".jj", description = "查询登记的基金情况，格式: .jj")
    public String myFundAnother(String id, MessageEvent messageEvent){
        return myFund(id, messageEvent);
    }

    @MiraiCommand(value = ".添加自选", description = "登记基金，格式: .添加自选 <code1>,<code2>")
    public String saveFund(String code, String id){
        List<String> fundList = Arrays.stream(code.split(",")).collect(Collectors.toList());
        Optional<User> query = fundDao.query(id);
        //如果存在用户则修改
        if (query.isPresent()){
            User user = query.get();
            user.getFundList().removeAll(fundList);
            user.getFundList().addAll(fundList);
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

    @MiraiCommand(value = ".删除自选", description = "删除基金记录，格式: .删除自选 <code1>")
    public String deleteFund(String code, String id){
        List<String> fundList = Arrays.stream(code.split(",")).collect(Collectors.toList());
        Optional<User> query = fundDao.query(id);
        fundDao.delete(query.get(), code);
        return "删除成功";
    }

    @MiraiCommand(value = ".help", description = "查询所有命令详情，格式: .help")
    public String help(String id){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : JavaPluginMain.commandDescription.entrySet()){
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    @MiraiCommand(value = ".基金排名", description = "查看一周内基金排名，格式: .基金排名")
    public String test(){
        StringBuilder sb = new StringBuilder();
        JSONArray fundsRank = fundCrawler.getFundsRank().getJSONObject("data").getJSONArray("rank");
        List<JSONObject> arrayLists = fundsRank.toList(JSONObject.class);
        for (JSONObject fund : arrayLists){
            sb.append(fund.getStr("name")).append("(").append(fund.getStr("code")).append(")").append(": ").append(fund.getStr("lastWeekGrowth")).append("\n");
        }
        return sb.toString();
    }
}

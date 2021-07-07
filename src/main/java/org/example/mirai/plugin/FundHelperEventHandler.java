package org.example.mirai.plugin;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.example.mirai.plugin.netword.FundCrawler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FundHelperEventHandler extends SimpleListenerHost {
    /**
     * 监听群临时会话消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus OnGroupTempMessageEvent(GroupTempMessageEvent event) {
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听好友
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus OnFriendMessageEvent(FriendMessageEvent event) {
        replyFundSeach(event.getMessage().contentToString(), event);
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听群
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus OnGroupMessageEvent(GroupMessageEvent event) {
        replyFundSeach(event.getMessage().contentToString(), event);
        return ListeningStatus.LISTENING;
    }

    private void replyFundSeach(String input, MessageEvent messageEvent) {
        input = input.toLowerCase().replaceAll("。", ".");
        if(!input.startsWith(".今日板块") && !input.startsWith(".基金 "))
            return;
        try{
            FundCrawler fundCrawler = FundCrawler.getInstance();
            if (input.startsWith(".今日板块")) {
                JSONObject industry = fundCrawler.getIndustry();
                JSONArray data = industry.getJSONArray("data");
                System.out.println(data);
                List<JSONObject> arrayLists = data.toList(JSONObject.class);
                //排序
                StringBuilder sb = new StringBuilder();
                sb.append("领涨行业：").append("\n");

                sb.append(arrayLists.get(0).getStr("name")).append(":").append(arrayLists.get(0).getStr("changePercent")).append("\n");
                sb.append(arrayLists.get(1).getStr("name")).append(":").append(arrayLists.get(1).getStr("changePercent")).append("\n");
                sb.append(arrayLists.get(2).getStr("name")).append(":").append(arrayLists.get(2).getStr("changePercent")).append("\n");

                sb.append("领跌行业：").append("\n");

                sb.append(arrayLists.get(arrayLists.size() - 1).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 1).getStr("changePercent")).append("\n");
                sb.append(arrayLists.get(arrayLists.size() - 2).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 2).getStr("changePercent")).append("\n");
                sb.append(arrayLists.get(arrayLists.size() - 3).getStr("name")).append(":").append(arrayLists.get(arrayLists.size() - 3).getStr("changePercent"));
                messageEvent.getSubject().sendMessage(sb.toString());
            } else {
                String code = input.replace(".基金 ", "");
                JSONObject fund = fundCrawler.getFund(code).getJSONArray("data").toList(JSONObject.class).get(0);
                StringBuilder sb = new StringBuilder();
                sb.append("基金编号: "+code).append("\n");
                sb.append("基金名称："+fund.getStr("name")).append("\n");
                sb.append("今日涨跌："+fund.getStr("dayGrowth")).append("\n");
                sb.append("半年涨跌："+fund.getStr("lastSixMonthsGrowth")).append("\n");
                messageEvent.getSubject().sendMessage(sb.toString());
            }

        } catch (Exception e){
            e.printStackTrace();
            messageEvent.getSubject().sendMessage("错误");
        }
    }


}

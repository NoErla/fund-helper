package org.example.mirai.plugin;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.Method;

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
        input = input.toLowerCase().replace("。", ".");
        if(!input.startsWith(".今日板块") && !input.startsWith(".基金 "))
            return;
        try{
            String[] inputs = input.split(" ");
            Method m = JavaPluginMain.mapUrlMethod.get(inputs[0]);  //通过注解得到对应的方法
            if (null == m){
                JavaPluginMain.INSTANCE.getLogger().error("找不到对应的command");
                throw new RuntimeException();
            }
            String result;
            if (1 == inputs.length)
                result = m.invoke(m.getDeclaringClass().newInstance()).toString();
            else if (2 == inputs.length)
                result = m.invoke(m.getDeclaringClass().newInstance(), inputs[1]).toString();
            else
                result = "指令有误";
            messageEvent.getSubject().sendMessage(result);
        } catch (Exception e){
            JavaPluginMain.INSTANCE.getLogger().error(e.getMessage());
            messageEvent.getSubject().sendMessage("错误");
        }
    }


}

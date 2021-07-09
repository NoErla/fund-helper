package org.example.mirai.plugin;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.Method;
import java.util.Set;

public class FundHelperEventHandler extends SimpleListenerHost {

    private final Set<String> commands;

    public FundHelperEventHandler() {
        this.commands = JavaPluginMain.mapUrlMethod.keySet();
    }

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
        if (!input.startsWith(".") && !input.startsWith("。"))
            return;
        input = input.replace("。", ".");
        if(!isCommand(input))
            return;
        try{
            String[] inputs = input.split(" ");
            Method method = JavaPluginMain.mapUrlMethod.get(inputs[0]);  //通过注解得到对应的方法
            if (null == method){
                JavaPluginMain.INSTANCE.getLogger().error("找不到对应的command");
                throw new RuntimeException();
            }
            String result;
            int parameterLength = method.getParameterTypes().length;
            if (0 == parameterLength)
                result = method.invoke(method.getDeclaringClass().newInstance()).toString();
            else if (1 == parameterLength)
                result = method.invoke(method.getDeclaringClass().newInstance(), inputs[1]).toString();
            else if (2 == parameterLength)
                result = method.invoke(method.getDeclaringClass().newInstance(), inputs[1], messageEvent.getSender().getId()).toString();
            else
                result = "指令有误";
            messageEvent.getSubject().sendMessage(result);
        } catch (Exception e){
            JavaPluginMain.INSTANCE.getLogger().error(e.getMessage());
            messageEvent.getSubject().sendMessage("错误");
        }
    }

    private boolean isCommand(String str){
        return commands
                .stream()
                .anyMatch(str::startsWith);
    }


}

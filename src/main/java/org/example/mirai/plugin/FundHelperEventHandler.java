package org.example.mirai.plugin;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;

import java.lang.reflect.Method;
import java.util.Set;

public class FundHelperEventHandler extends SimpleListenerHost {

    private final Set<String> commands;

    private MiraiLogger logger = JavaPluginMain.INSTANCE.getLogger();


    public FundHelperEventHandler() {
        this.commands = JavaPluginMain.commandMethod.keySet();
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
        input = normalize(input);
        if(!isCommand(input))
            return;
        try{
            String[] inputs = input.split(" ");
            Method method = JavaPluginMain.commandMethod.get(inputs[0]);  //通过注解得到对应的方法
            if (null == method)
                return;
            Class<?> clazz = method.getDeclaringClass();
            Method getInstance = clazz.getMethod("getInstance");
            //获得方法参数
            //TODO 目前只有String类型的入参，之后可以改为支持其他类型
            String[] parameters = dataBinder(method, String.valueOf(messageEvent.getSender().getId()), inputs.length>=2 ? inputs[1] : null);
            //反射执行方法
            String result = method.invoke(getInstance.invoke(clazz), (Object[]) parameters).toString();
            messageEvent.getSubject().sendMessage(result);
        } catch (Exception e){
            logger.error(e.getMessage());
            messageEvent.getSubject().sendMessage("错误");
        }
    }

    /**
     * 标准化输入
     * @param input
     * @return
     */
    private String normalize(String input) {
        input = input
                .replace("。", ".")
                .replace("，", ",");
        return input;
    }

    private boolean isCommand(String str){
        return commands
                .stream()
                .anyMatch(str::startsWith);
    }

    private String[] dataBinder(Method method, String id, String code){
        Paranamer info = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));
        String[] parameterNames = info.lookupParameterNames(method);
        String[] parameters = new String[method.getParameterCount()];
        for (int i=0,len=method.getParameterCount();i<len;i++){
            if (parameterNames[i].equals("id"))
                parameters[i] = id;
            else if (parameterNames[i].equals("code"))
                parameters[i] = code;
            else
                parameters[i] = null;
        }
        return parameters;
    }


}

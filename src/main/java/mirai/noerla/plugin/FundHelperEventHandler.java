package mirai.noerla.plugin;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;

public class FundHelperEventHandler extends SimpleListenerHost {

    private final Set<String> commands;

    private MiraiLogger logger = JavaPluginMain.INSTANCE.getLogger();


    public FundHelperEventHandler() {
        this.commands = JavaPluginMain.commandMethod.keySet();
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
            String[] results = result.split(";;");
            if (results.length > 1){
//                createTableImage(results, messageEvent);
                messageEvent.getSubject().sendMessage(results[0]);
                messageEvent.getSubject().sendMessage(results[1]);
            } else {
                messageEvent.getSubject().sendMessage(result);
            }
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

    private void createTableImage(String cellsValue[], MessageEvent messageEvent) throws IOException {
        // 横线的行数
        int totalrow = cellsValue.length + 1;
        // 竖线的行数
        //TODO 列数固定改为不固定
        int totalcol = 2;
        // 图片宽度
        int imageWidth = 1024;
        // 行高
        int rowheight = 40;
        // 图片高度
        int imageHeight = totalrow * rowheight + 50;
        // 起始高度
        int startHeight = 10;
        // 起始宽度
        int startWidth = 10;
        // 单元格宽度
        int colwidth = (int) ((imageWidth - 20) / totalcol);
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setColor(new Color(220, 240, 240));
        //画横线
        for (int j = 0; j < totalrow; j++) {
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth, startHeight + (j + 1) * rowheight, startWidth + colwidth * totalcol, startHeight + (j + 1) * rowheight);
        }
        //画竖线
        for (int k = 0; k < totalcol + 1; k++) {
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth + k * colwidth, startHeight + rowheight, startWidth + k * colwidth, startHeight + rowheight * totalrow);
        }
        graphics.drawString("", startWidth, startHeight + rowheight - 10);
        //写入内容
        for (int n = 0; n < cellsValue.length; n++) {
            String[] data = cellsValue[n].split(":");
            for (int l = 0; l < 2; l++) {
                graphics.setColor(Color.BLACK);
                graphics.drawString(data[l], startWidth + colwidth * l + 5, startHeight + rowheight * (n + 2) - 10);
            }
        }
        // 保存图片
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        ExternalResource.sendAsImage(is, messageEvent.getSubject());
    }


}

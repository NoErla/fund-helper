package mirai.noerla.plugin.timer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import mirai.noerla.plugin.CommandController;
import mirai.noerla.plugin.JavaPluginMain;
import mirai.noerla.plugin.PluginConsts;
import mirai.noerla.plugin.pojo.User;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.utils.ExternalResource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FundJob implements Job {

    CommandController commandController = CommandController.getInstance();
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JavaPluginMain.INSTANCE.getLogger().info("执行定时任务");
        Bot instance = Bot.getInstances().get(0);
        CsvReader reader = CsvUtil.getReader();
        List<User> users =
                reader.read(ResourceUtil.getUtf8Reader(FileUtil.file(PluginConsts.CSV_PATH).getAbsolutePath()), User.class);
        for (User user : users){
            if (instance.getFriend(Long.parseLong(user.getId())) != null){
                JavaPluginMain.INSTANCE.getLogger().info("定时任务对象：" + user.getId());
                //fixme null→messageEvent
                String[] result = commandController.myFund(user.getId(), null).split("\n");
                try {
                    createTableImage(result, instance.getFriend(Long.parseLong(user.getId())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //TODO 非好友支持
        }
    }

    private void createTableImage(String cellsValue[], Friend friend) throws IOException {
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
        ExternalResource.sendAsImage(is, friend);
    }
}

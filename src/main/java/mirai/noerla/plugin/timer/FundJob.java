package mirai.noerla.plugin.timer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import mirai.noerla.plugin.CommandController;
import mirai.noerla.plugin.JavaPluginMain;
import mirai.noerla.plugin.PluginConsts;
import net.mamoe.mirai.Bot;
import mirai.noerla.plugin.pojo.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
//                JavaPluginMain.INSTANCE.getLogger().info("定时任务对象：" + user.getId());
//                instance.getFriend(Long.parseLong(user.getId())).sendMessage(commandController.myFund(user.getId()));
            }
            //TODO 非好友支持
        }
    }
}

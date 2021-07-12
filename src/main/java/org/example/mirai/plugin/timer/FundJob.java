package org.example.mirai.plugin.timer;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import net.mamoe.mirai.Bot;
import org.example.mirai.plugin.CommandController;
import org.example.mirai.plugin.pojo.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

public class FundJob implements Job {
    CommandController commandController = CommandController.getInstance();
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Bot instance = Bot.getInstance(123L);
        CsvReader reader = CsvUtil.getReader();
        List<User> users = reader.read(ResourceUtil.getUtf8Reader("resources/data.csv"), User.class);
        for (User user : users){
            if (instance.getFriend(Long.parseLong(user.getId())) != null)
                instance.getFriend(Long.parseLong(user.getId())).sendMessage(commandController.myFund(user.getId()));
            else if (instance.getGroup(123L).getMembers().get(Long.parseLong(user.getId())) != null)
                instance.getGroup(123L).getMembers().get(Long.parseLong(user.getId())).sendMessage(commandController.myFund(user.getId()));
        }
    }
}

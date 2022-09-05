package mirai.noerla.plugin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import mirai.noerla.plugin.annotation.MiraiCommand;
import mirai.noerla.plugin.pojo.User;
import mirai.noerla.plugin.timer.FundJob;
import mirai.noerla.steam_checker.config.Config;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class JavaPluginMain extends JavaPlugin {

    public static final JavaPluginMain INSTANCE = new JavaPluginMain();

    //key:方法路径，value:方法
    public static Map<String, Method> commandMethod = new HashMap<>();
    //key:方法路径，value:方法描述，用于help命令
    public static Map<String, String> commandDescription = new HashMap<>();

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("mirai.noerla.plugin", "0.1.3")
                .info("EG")
                .build());
    }

    @Override
    public void onEnable() {
        //getLogger().info("当前目录:" + FileUtil.getAbsolutePath("resources/data.csv"));
        //注册handler
        GlobalEventChannel.INSTANCE.registerListenerHost(new FundHelperEventHandler());
        //初始化csv文件
        initCsvFile();
        //初始化MapUrlMethod
        initMapUrlMethod();
        //初始化定时器
        initScheduler();
    }

    private void initScheduler() {
        try {
            if (Config.INSTANCE.isPublish()) {
                getLogger().info("准备开启定时任务");
                // 1、创建调度器Scheduler
                SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                Scheduler scheduler = schedulerFactory.getScheduler();
                // 2、创建JobDetail实例
                JobDetail jobDetail = JobBuilder.newJob(FundJob.class).build();
                // 3、构建Trigger实例,每周一到周五14:40执行一次
                SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
                simpleScheduleBuilder.withIntervalInSeconds(3);
                simpleScheduleBuilder.withRepeatCount(5);
                Trigger trigger = TriggerBuilder.newTrigger()
                        .startNow()//立即生效
                        //.withSchedule(simpleScheduleBuilder)
                        .withSchedule(CronScheduleBuilder.cronSchedule(Config.INSTANCE.getTimer()))
                        .build();//一直执行
                //4、执行
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            }
        } catch (Exception e) {
            getLogger().error("定时任务开启失败");
        }
    }

    private void initCsvFile() {
        try {
            //判断是否存在csv文件
            if (!FileUtil.exist(PluginConsts.CSV_PATH)) {
                //创建data.csv文件
                CsvWriter writer = CsvUtil.getWriter(PluginConsts.CSV_PATH, CharsetUtil.CHARSET_UTF_8);
                //写出表头
                Map<String, Object> properties = BeanUtil.beanToMap(new User());
                writer.writeLine(properties.keySet().toArray(new String[0]));
                //初始用户
                User initUser = new User();
                initUser.setId("123");
                initUser.setFundList(new ArrayList<>());
                writer.writeLine(initUser.getId(), initUser.getFundList().toString());
                writer.flush();
            }
        } catch (Exception e) {
            getLogger().error("csv文件初始化失败");
        }

    }

    private void initMapUrlMethod() {
        //获取对应的类路径
        //TODO 取消硬编码
        String classurl = "mirai.noerla.plugin.CommandController";
        //获取类对象
        try {
            Class<?> c = Class.forName(classurl);
            //获得该类的所有方法对象
            Method[] methods = c.getDeclaredMethods();
            //forEach 遍历方法对象
            for (Method method : methods) {
                //判断是不是UserCommand的注解
                if (method.isAnnotationPresent(MiraiCommand.class)) {
                    //如果有存在就把该标签取出来
                    MiraiCommand xReqMap = method.getAnnotation(MiraiCommand.class);
                    //把方法和该方法对应的标签进行绑定
                    commandMethod.put(xReqMap.value(), method);
                    commandDescription.put(xReqMap.value(), xReqMap.description());
                }
            }
        } catch (Exception e) {
            getLogger().error("方法表初始化失败");
        }
    }
}

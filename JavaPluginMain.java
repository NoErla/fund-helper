package org.example.mirai.plugin;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.example.mirai.plugin.annotation.UserCommand;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * 使用 Java 请把
 * {@code /src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin}
 * 文件内容改成 {@code org.example.mirai.plugin.JavaPluginMain} <br/>
 * 也就是当前主类全类名
 *
 * 使用 Java 可以把 kotlin 源集删除且不会对项目有影响
 *
 * 在 {@code settings.gradle.kts} 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 {@link JvmPluginDescription} 修改插件名称，id 和版本等
 *
 * 可以使用 {@code src/test/kotlin/RunMirai.kt} 在 IDE 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

public final class JavaPluginMain extends JavaPlugin {
    public static final JavaPluginMain INSTANCE = new JavaPluginMain();

    public static Map<String, Method> mapUrlMethod=new HashMap<>(); //输入方法路劲时，得到对应方法

    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("org.example.mirai-example", "0.1.0")
                .info("EG")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("日志");
        //注册handler
        GlobalEventChannel.INSTANCE.registerListenerHost(new FundHelperEventHandler());

        //获取对应的类路径
        String classurl = "org.example.mirai.plugin.CommandController";
        //获取类对象
        Class c = null;
        try {
            c = Class.forName(classurl);
            //获得该类的所有方法对象
            Method [] methods = c.getDeclaredMethods();
            //forEach 遍历方法对象
            for(Method m:methods){
                //判断是不是UserCommand的注解
                if (m.isAnnotationPresent(UserCommand.class)){
                    //如果有存在就把该标签取出来
                    UserCommand xReqMap = m.getAnnotation(UserCommand.class);
                    //把方法和该方法对应的标签进行绑定
                    mapUrlMethod.put(xReqMap.value(), m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error("初始化失败");
        }
    }
}

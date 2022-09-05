package mirai.noerla.steam_checker.config
import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : ReadOnlyPluginConfig("Settings") {
    @ValueDescription(
        """
        是否开启定时推送
        """)
    val isPublish by value(false)

    @ValueDescription(
        """
        定时规则(cron表达式)
        """)
    val timer by value("0 40 14 ? * 2-6 *")
}
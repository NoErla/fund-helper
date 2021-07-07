plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.6"
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}

dependencies{
    implementation("junit:junit:4.13.1")
    implementation("cn.hutool:hutool-all:5.7.3")
    implementation("org.quartz-scheduler:quartz:2.3.2")
}

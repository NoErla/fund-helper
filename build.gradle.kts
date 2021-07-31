plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.7-M2"
}

group = "org.example"
version = "0.1.1"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/jcenter")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
}

dependencies{
    implementation("junit:junit:4.13.1")
    implementation("cn.hutool:hutool-all:5.7.4")
    implementation("com.thoughtworks.paranamer:paranamer:2.8")
    implementation("org.quartz-scheduler:quartz:2.3.2")
}
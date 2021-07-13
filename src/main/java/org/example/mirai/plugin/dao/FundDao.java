package org.example.mirai.plugin.dao;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import org.example.mirai.plugin.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundDao {

    private static final String path = "resources/data.csv";

    private static final FundDao instance = new FundDao();

    private FundDao(){}

    public static FundDao getInstance(){
        return instance;
    }

    public void add(User user){
        List<User> users = new ArrayList<>();
        users.add(user);
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8, true);
        writer.writeLine(user.getId(), user.getFundList().toString());
        writer.flush();
    }

    public Optional<User> query(String id){
        //ResourceUtil.getUtf8Reader在centos下无法获得classpath，因为使用了classloader.getResource
        CsvReader reader = CsvUtil.getReader();
        List<User> users = reader.read(ResourceUtil.getUtf8Reader(FileUtil.file(path).getAbsolutePath()), User.class);
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public void update(User user){
        CsvReader reader = CsvUtil.getReader();
        List<User> users = reader.read(ResourceUtil.getUtf8Reader(FileUtil.file(path).getAbsolutePath()), User.class);
        Optional<User> first = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
        first.ifPresent(f-> f.setFundList(user.getFundList()));
        CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8,false);
        writer.writeBeans(users);
    }

    public void delete(String id){
        CsvReader reader = CsvUtil.getReader();
        List<User> users = reader.read(ResourceUtil.getUtf8Reader(FileUtil.file(path).getAbsolutePath()), User.class);
        Optional<User> first = users.stream().filter(user -> id.equals(user.getId())).findFirst();
        first.ifPresent(user -> {
            users.remove(user);
            CsvWriter writer = CsvUtil.getWriter(path, CharsetUtil.CHARSET_UTF_8,false);
            writer.writeBeans(users);
        });
    }
}

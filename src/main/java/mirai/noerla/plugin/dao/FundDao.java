package mirai.noerla.plugin.dao;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import mirai.noerla.plugin.PluginConsts;
import mirai.noerla.plugin.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FundDao {

    private static final FundDao instance = new FundDao();

    private FundDao(){}

    public static FundDao getInstance(){
        return instance;
    }

    public void add(User user){
        CsvWriter writer = CsvUtil.getWriter(PluginConsts.CSV_PATH, CharsetUtil.CHARSET_UTF_8, true);
        writer.writeLine(user.getId(), user.getFundList().toString());
        writer.flush();
    }

    public Optional<User> query(String id){
        //ResourceUtil.getUtf8Reader在centos下无法获得classpath，因为使用了classloader.getResource
        List<User> users = getAllUsers();
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    public void update(User user){
        List<User> users = getAllUsers();
        Optional<User> first = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
        first.ifPresent(f-> f.setFundList(user.getFundList()));
        CsvWriter writer = CsvUtil.getWriter(PluginConsts.CSV_PATH, CharsetUtil.CHARSET_UTF_8,false);
        writer.writeBeans(users);
    }

    public void delete(String id){
        List<User> users = getAllUsers();
        Optional<User> first = users.stream().filter(user -> id.equals(user.getId())).findFirst();
        first.ifPresent(user -> {
            users.remove(user);
            //防止删除最后一个用户的时候连表头一起删除
            //TODO 固定用户信息
            if (users.isEmpty()){
                users.add(new User());
            }
            CsvWriter writer = CsvUtil.getWriter(PluginConsts.CSV_PATH, CharsetUtil.CHARSET_UTF_8,false);
            writer.writeBeans(users);
        });
    }

    private List<User> getAllUsers(){
        CsvReader reader = CsvUtil.getReader();
        return reader.read(ResourceUtil.getUtf8Reader(FileUtil.file(PluginConsts.CSV_PATH).getAbsolutePath()), User.class);
    }
}

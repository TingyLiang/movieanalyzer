package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    String ID;
    List<User> users;

    public Group() {
        // 分组号
        this.ID = UUID.randomUUID().toString().replace("-", "");
        //用户ID,IMEI号或者手机号,生成第一个当前手机的默认用户
        User user = new User("test");
        user.setmId("1");
        this.users = new ArrayList<>(1);
        this.users.add(user);

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}

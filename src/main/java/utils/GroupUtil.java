package utils;


import com.alibaba.fastjson.JSONObject;
import model.Group;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupUtil {
    public static final String JSON_SUFFIX = ".json";
    public static final String FILE_SUFFIX = ".group";
    public static final String COUNT_SUFFIX = ".count";
    public static final String GROUP_ROOT = "data/groups/";


    /**
     * 新建分组
     *
     * @throws IOException
     */
    public static void createGroup() throws IOException {
        Group group = new Group();
        StringBuilder builder = new StringBuilder(String.valueOf(group.getUsers().size()))
                .append("\n")
                .append(JSONObject.toJSONString(group.getUsers()));
        String filePreffix = GROUP_ROOT + group.getID();
        System.out.println(builder);
        try {
            FileUtil.writeFile(builder.toString(), filePreffix + FILE_SUFFIX);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    public static boolean addUser(String groupId, User user) {
        List<User> users = getUsers(groupId);
        if (null!= users) {
            for (User u : users) {
                //用户名或ID重复
                if (u.getmId().equals(user.getmId()) || u.getmName().equals(user.getmName())) {
                    return false;
                }

            }
        }
        else
            users = new ArrayList<>(1);

        users.add(user);
        try {
            FileUtil.writeFile(String.valueOf(users.size()) + "\n" + JSONObject.toJSONString(users), GROUP_ROOT + groupId + FILE_SUFFIX);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean removeUser(String groupId, User user) {
        List<User> users = getUsers(groupId);
        for (User u : users) {
            if (u.getmId().equals(user.getmId())) {
                users.remove(u);
                break;
            }
        }
        //TODO 删除用户时需要删除用户的历史记账数据
        try {
            FileUtil.writeFile(String.valueOf(users.size()) + "\n" + JSONObject.toJSONString(users), GROUP_ROOT + groupId + FILE_SUFFIX);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean editUser(String groupId, User user) {
        List<User> users = getUsers(groupId);
        for (User u : users) {
            //用户名或ID重复
            if (u.getmId().equals(user.getmId())) {
                u.setmIconBg(user.getmIconBg());
                u.setmName(user.getmName());
                break;
            }
        }
        try {
            FileUtil.writeFile(users.size() + "\n" + JSONObject.toJSONString(users), GROUP_ROOT + groupId + FILE_SUFFIX);
        } catch (IOException e) {
            return false;
        }
        return true;

    }


    public static List<User> getUsers(String groupId) {
        String fileName = GROUP_ROOT + groupId + FILE_SUFFIX;
        List<User> users = null;
        try {
            users = JSONObject.parseArray(FileUtil.readFile(fileName, 2), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return users;
        }
    }

    public static String getUserNameByID(String groupId, String userID) {
        List<User> users = getUsers(groupId);
        for (User u : users) {
            if (u.getmId().equals(userID))
                return u.getmName();
        }
        return null;
    }

    public static int getUserNumber(String groupId) {
        String fileName = GROUP_ROOT + groupId + FILE_SUFFIX;
        try {
            return Integer.valueOf(JSONObject.parse(FileUtil.readFile(fileName, 1)).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void main(String[] args) throws IOException {
        User user = new User("sy");
        user.setmId("2");
        user.setmIconBg(5);
        String gId = "508971a09b5647ee9fab22e750979f97";
//        GroupUtil.createGroup();
        GroupUtil.addUser(gId,user);
//        GroupUtil.editUser(gId, user);
//        GroupUtil.removeUser(gId, user);
    }

}

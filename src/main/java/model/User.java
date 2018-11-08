package model;
public class User {

    public String mId;

    public String mName;

    public int mIconBg;

    public User(String mName) {
        this.mName = mName;
    }
    public User(String mId,String mName,int mIconBg)
    {
        this.mId = mId;
        this.mName =mName;
        this.mIconBg = mIconBg;
    }


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmIconBg() {
        return mIconBg;
    }

    public void setmIconBg(int mIconBg) {
        this.mIconBg = mIconBg;
    }

    @Override
    public String toString() {
        return "User{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mIconBg=" + mIconBg +
                '}';
    }
}

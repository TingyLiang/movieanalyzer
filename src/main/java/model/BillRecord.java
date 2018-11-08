package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BillRecord implements Comparable {

    public String mId;
    public double mAmount;
    public String mType;
    public long mTimestamp;
    public String mUserId;

    public BillRecord(String mId, double mAmount, String mType, long mTimestamp, String mUserId) {
        this.mId = mId;
        this.mAmount = mAmount;
        this.mType = mType;
        this.mTimestamp = mTimestamp;
        this.mUserId = mUserId;
    }

    public BillRecord(String mId) {
        this.mId = mId;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public double getmAmount() {
        return mAmount;
    }

    public void setmAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public long getmTimestamp() {
        return mTimestamp;
    }

    public String getSimpleTime()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(mTimestamp));
    }

    public void setmTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    /**
     * 支持排序，按时间戳排序升序排序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof BillRecord) {
            if (((BillRecord) o).getmTimestamp() > this.mTimestamp) {
                return 1;
            }
            if (((BillRecord) o).getmTimestamp() < this.getmTimestamp()) {
                return -1;
            }
            return 0;
        } else {
            try {
                throw new Exception("Target compare object is a type of" + o.getClass() + ", not a type of [BillRecord]");
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}

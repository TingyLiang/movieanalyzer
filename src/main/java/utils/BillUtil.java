package utils;

import com.alibaba.fastjson.JSONObject;
import model.BillRecord;
import model.StatisDetail;
import model.User;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BillUtil {
    private static final String RECORD_PATH_PREFFIX = "data/records/";
    private static final String TEMP_ROOT = "data/tmp/";
    private static final String SLASH = "/";
    private static final String YEAR_FORMAT = "yyyy";
    private static final String YEAR_MON_FORMAT = "yyyy-MM";
    private static final String JSON_SUFFIX = ".json";
    private static final String STATI_SUFFIX = ".sta";
    private static final String YEAR_STATISTIC_SUFFIX = ".year";
    private static final String MONTH_STATISTIC_SUFFIX = ".mon";


    /**
     * 新增记录
     *
     * @param record  记录数据
     * @param groupId 分组ID
     * @return
     */
    public static boolean addRecord(BillRecord record, String groupId) throws IOException {
        long time = record.getmTimestamp();
        String fileName = new StringBuilder(RECORD_PATH_PREFFIX)
                .append(groupId)
                .append(SLASH)
                .append(stampToDate(time, YEAR_FORMAT))
                .append(SLASH)
                .append(getPartionName(time))
                .append(JSON_SUFFIX).toString();
        //当月所有历史记录
        List<BillRecord> records = getRecords(fileName);
        if (BillUtil.contains(records, record)) {
            return false;
        } else {
            records.add(record);
            Collections.sort(records);
            FileUtil.writeFile(JSONObject.toJSONString(records), fileName);
            //更新统计数据
            updateStatistics(groupId, records);

        }
        return true;
    }

    public static void removeRecord(BillRecord record, String groupId) throws IOException {
        String fileName = new StringBuilder(RECORD_PATH_PREFFIX)
                .append(groupId)
                .append(SLASH)
                .append(stampToDate(record.getmTimestamp(), YEAR_FORMAT))
                .append(SLASH)
                .append(getPartionName(record.getmTimestamp()))
                .append(JSON_SUFFIX).toString();
        List<BillRecord> records = getRecords(fileName);
        records = removeBillRecord(records, record.getmId());
        FileUtil.writeFile(JSONObject.toJSONString(records), fileName);
        updateStatistics(groupId, records);
    }

    private static List<BillRecord> removeBillRecord(List<BillRecord> records, String rId) {
        if (records != null && !records.isEmpty()) {
            Iterator<BillRecord> iterator = records.iterator();
            BillRecord record;
            while (iterator.hasNext()) {
                record = iterator.next();
                if (record.getmId().equals(rId))
                    iterator.remove();
            }
        }
        return records;
    }

    /**
     * 修改记录，以ID作为对比标准
     *
     * @param record
     * @return
     * @throws IOException
     */
    public static boolean editRecord(BillRecord record, String groupId) throws IOException {
        String fileName = new StringBuilder(RECORD_PATH_PREFFIX)
                .append(groupId)
                .append(SLASH)
                .append(stampToDate(record.getmTimestamp(), YEAR_FORMAT))
                .append(SLASH)
                .append(getPartionName(record.getmTimestamp()))
                .append(JSON_SUFFIX).toString();
        List<BillRecord> records = getRecords(fileName);
        if (!records.isEmpty()) {
            Iterator<BillRecord> iterator = records.iterator();
            BillRecord r;
            while (iterator.hasNext()) {
                r = iterator.next();
                if (r.getmId().equals(record.getmId())) {
                    //删除再新增
                    iterator.remove();
                    records.add(record);
                    Collections.sort(records);
                    FileUtil.writeFile(JSONObject.toJSONString(records), fileName);
                    updateStatistics(groupId, records);
                    return true;
                }
            }
        }
        return false;
    }


    public static List<BillRecord> getRecords(String fileName) throws IOException {
        File file = new File(fileName);
        List<BillRecord> records;
        if (file.exists()) {
            return JSONObject.parseArray(FileUtil.readFile(fileName), BillRecord.class);
        } else
            return new ArrayList<>(1);
    }

    /**
     * 合并账单数据
     *
     * @return
     */
    public static boolean merge(String groupId) {
        File source = new File(TEMP_ROOT + groupId);
        File target = new File(RECORD_PATH_PREFFIX + groupId);
        if (source.exists()) {
            if (source.isDirectory()) {
                String[] sourcePa = source.list();
            }
            File[] tmpfiles = source.listFiles();


        }
        return true;
    }

    /**
     * 将时间转换为时间戳
     */
    private static String dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            return null;
        }
        return String.valueOf(date.getTime());
    }

    /**
     * 将时间戳按指定格式转换为时间
     */
    private static String stampToDate(long stamp, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(stamp);
        return simpleDateFormat.format(date);
    }

    private static String getPartionName(long timeStamp) {
        StringBuilder date = new StringBuilder(stampToDate(timeStamp, "yyyy-MM"));
        //以每月第一天第一秒时间为分区文件名
        date.append("-01 00:00:01");
        return dateToStamp(date.toString());
    }

    /**
     * @param records 历史账单数据列表
     * @param record  待判断对象
     * @return
     */
    private static boolean contains(List<BillRecord> records, BillRecord record) {
        if (null != records && records.size() > 0) {
            for (BillRecord r : records) {
                if (record.getmId().equals(r.getmId())
                        && record.getmAmount() == r.getmAmount()
                        && record.getmType().equals(r.getmType())
                        && record.getmTimestamp() == r.getmTimestamp())
                    return true;
            }
        }

        return false;
    }

    private static String getAndUpdateYearStatistics(String groupId, List<StatisDetail> monRecords, String year) {
        List<User> users = GroupUtil.getUsers(groupId);
        StatisDetail yearDetail = new StatisDetail();
        if (null != monRecords && !monRecords.isEmpty()) {
            float costAll = 0;
            Map<String, Float> costMap = new HashMap<>(users.size());
            users.forEach(u -> costMap.put(u.getmName(), 0F));
            for (StatisDetail detail : monRecords) {
                costAll += detail.getCostAll();
                //更新每人的年消费总和
                detail.getCostDetails().forEach((key, value) -> costMap.put(key, costMap.get(key) + value));
            }
            yearDetail.setCostAll(costAll);
            yearDetail.setLabel(year);
            yearDetail.setId(year);
            yearDetail.setAvgCost(costAll / users.size());
            yearDetail.setCostDetails(costMap);
        }
        return JSONObject.toJSONString(yearDetail);
    }

    private static void updateStatistics(String groupId, List<BillRecord> records) {
        if (records != null && !records.isEmpty()) {
            long time = records.get(0).getmTimestamp();
            //本月统计
            StatisDetail monDetail = getMonthDetail(groupId, records);
            String fileName = getStatisFileName(groupId, stampToDate(time, YEAR_FORMAT));
            try {
                //历史每月统计详情
                List<StatisDetail> monDetails = JSONObject.parseArray(FileUtil.readFile(fileName, 2), StatisDetail.class);
                if (monDetails == null) {
                    //null时创建
                    monDetails = new ArrayList<StatisDetail>(1);
                }
                if (!monDetails.isEmpty()) {
                    if (containsMonthDetail(monDetails, monDetail)) {
                        //删除本月统计
                        removeDetail(monDetails, monDetail);
                    }
                }
                monDetails.add(monDetail);
                Collections.sort(monDetails);
                StringBuilder details = new StringBuilder(getAndUpdateYearStatistics(groupId, monDetails, stampToDate(time, YEAR_FORMAT)))
                        .append("\n")
                        .append(JSONObject.toJSONString(monDetails));
                FileUtil.writeFile(details.toString(), fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean containsMonthDetail(List<StatisDetail> monDetails, StatisDetail target) {
        for (StatisDetail detail : monDetails) {
            if (detail.getId().equals(target.getId())) {
                return true;
            }
        }
        return false;
    }

    private static void removeDetail(List<StatisDetail> monDetails, StatisDetail target) {
        if (null != monDetails) {
            Iterator it = monDetails.iterator();
            StatisDetail detail;
            while (it.hasNext()) {
                detail = (StatisDetail) it.next();
                if (detail.getId().equals(target.getId()))
                    it.remove();
            }
        }
    }

    /**
     * 返回月统计详情列表
     *
     * @param groupId
     * @param year
     * @return
     */
    public static List<StatisDetail> getMonthDetails(String groupId, String year) {
        String fileName = getStatisFileName(groupId, year);
        List<StatisDetail> monDetails = null;
        try {
            monDetails = JSONObject.parseArray(FileUtil.readFile(fileName, 2), StatisDetail.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monDetails;
    }

    /**
     * 自定义时间段统计数据获取
     *
     * @param start yyyy-MM-dd
     * @param end   yyyy-MM-dd
     * @return
     */
    public static List<StatisDetail> getMonthDetailsOfTimeRange(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(start);
            date2 = format.parse(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long gap = date2.getTime() - date1.getTime();
        int year = (int) (gap / (1000 * 60 * 60 * 24)) / 365;
        int day = (int) (gap / (1000 * 60 * 60 * 24));

        // TODO 未完成
        //跨年
        if (year > 0) {
        }
        //跨月，暂时没考虑31天的月份
        if (day > 30) {

        }

        return null;

    }

    public static StatisDetail getYearDetail(String groupId, String year) {
        StatisDetail detail = null;
        String file = getStatisFileName(groupId, year);
        try {
            detail = (StatisDetail) JSONObject.parseObject(FileUtil.readFile(file, 1), StatisDetail.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return detail;
    }

    private static String getStatisFileName(String groupId, String year) {
        return new StringBuilder(RECORD_PATH_PREFFIX)
                .append(groupId)
                .append(SLASH)
                .append(year)
                .append(SLASH)
                .append(year)
                .append(STATI_SUFFIX).toString();
    }

    /**
     * 返回当月月详细统计数据
     *
     * @param groupId
     * @param records 当月所有消费记录
     * @return
     */
    private static StatisDetail getMonthDetail(String groupId, List<BillRecord> records) {
        StatisDetail monDetail = null;
        if (records != null || !records.isEmpty()) {
            monDetail = new StatisDetail();
            float costAll = 0;
            List<User> users = GroupUtil.getUsers(groupId);
            int userNum = users.size();
            Map<String, Float> costMap = new HashMap<>(userNum);
            users.forEach(u -> costMap.put(u.getmName(), 0F));
            float oldCost = 0;
            String userName;
            for (BillRecord r : records) {
                costAll += r.getmAmount();
                userName = GroupUtil.getUserNameByID(groupId, r.getmUserId());
                oldCost = costMap.get(userName);
                costMap.put(userName, oldCost + (float) r.getmAmount());
            }
            float avgMonCost = costAll / userNum;
            //本月消费统计明细
            monDetail.setAvgCost(avgMonCost);
            monDetail.setCostAll(costAll);
            monDetail.setCostDetails(costMap);
            long time = records.get(0).getmTimestamp();
            monDetail.setLabel(stampToDate(time, YEAR_MON_FORMAT));
            monDetail.setId(getPartionName(time));
//            System.out.println(stampToDate(time, YEAR_MON_FORMAT) + JSONObject.toJSONString(monDetail));
        }
        return monDetail;
    }

    public static void main(String[] args) throws ParseException, IOException {
       /* String t1 = dateToStamp("2018-10-02 10:23:56");//1538323201000
        String t2 = dateToStamp("2018-09-10 10:23:56");//1535731201000
        String t3 = dateToStamp("2018-08-02 10:03:56");//1533052801000
        String date = getPartionName(Long.parseLong(t1));
        System.out.println(date);
        date = getPartionName(Long.parseLong(t2));
        System.out.println(date);
        date = getPartionName(Long.parseLong(t3));
        System.out.println(date);
        System.out.println(t2.compareTo(t1));*/

        List<BillRecord> list = new ArrayList<>();
        BillRecord record1 = new BillRecord("1", 2.5, "粮油", 1538323201000L, "1");
        BillRecord record2 = new BillRecord("2", 66.6, "粮油", 1535731201000L, "2");
        BillRecord record3 = new BillRecord("3", 3.33, "粮油", 1538323201000L, "1");
        BillRecord record4 = new BillRecord("4", 15.2, "百货", 1535731201000L, "2");
        String groupId = "508971a09b5647ee9fab22e750979f97";
        BillRecord record5 = record3;
        record5.setmType("水果");
        record5.setmAmount(6.99);
//        BillUtil.addRecord(record1, groupId);
//        BillUtil.addRecord(record2, groupId);
//        BillUtil.addRecord(record3, groupId);
//        BillUtil.addRecord(record4, groupId);
//        BillUtil.removeRecord(record1, groupId);
//        BillUtil.editRecord(record5, groupId);
        List<StatisDetail> ds = BillUtil.getMonthDetails(groupId, "2018");
        StatisDetail d = BillUtil.getYearDetail(groupId, "2018");
        int i = 0;
    }

}

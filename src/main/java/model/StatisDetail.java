package model;

import java.util.Map;

public class StatisDetail implements Comparable {
    //月明细ID 为时间戳，年明细为年份
    private String id;
    private String label;
    private float avgCost;
    //总消费
    private float costAll;
    /**
     * key 用户名，value 消费值
     */
    private Map<String, Float> costDetails;

    public StatisDetail() {

    }

    public StatisDetail(String id, String label, float avgCost, float costAll, Map<String, Float> costDetails) {
        this.id = id;
        this.label = label;
        this.avgCost = avgCost;
        this.costAll = costAll;
        this.costDetails = costDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(float avgCost) {
        this.avgCost = avgCost;
    }

    public float getCostAll() {
        return costAll;
    }

    public void setCostAll(float costAll) {
        this.costAll = costAll;
    }

    public Map<String, Float> getCostDetails() {
        return costDetails;
    }

    public void setCostDetails(Map<String, Float> costDetails) {
        this.costDetails = costDetails;
    }

    @Override
    public int compareTo(Object o) {
        if (null != o) {
            if (o instanceof StatisDetail) {
                return id.compareTo(((StatisDetail) o).getId());
            }
        }
        return 0;
    }
}
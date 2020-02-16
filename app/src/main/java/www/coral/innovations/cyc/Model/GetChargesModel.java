package www.coral.innovations.cyc.Model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class GetChargesModel {

    @SerializedName("units")
    private String units;

    @SerializedName("category")
    private String category;

    @SerializedName("subCategory")
    private String subCategory;

    @SerializedName("phase")
    private String phase;

    @SerializedName("load")
    private String load;

    @SerializedName("multiplicationFactor")
    private String multiplicationFactor;

    @SerializedName("prevReadingDate")
    private String prevReadingDate;

    public GetChargesModel(String units, String category, String subCategory, String phase, String load, String multiplicationFactor, String prevReadingDate) {
        this.units = units;
        this.category = category;
        this.subCategory = subCategory;
        this.phase = phase;
        this.load = load;
        this.multiplicationFactor = multiplicationFactor;
        this.prevReadingDate = prevReadingDate;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put("units", units);
        valueMap.put("category", category);
        valueMap.put("subCategory", subCategory);
        valueMap.put("phase", phase);
        valueMap.put("load", load);
        valueMap.put("prevReadingDate", prevReadingDate);
        if (multiplicationFactor != null)
            valueMap.put("multiplicationFactor", multiplicationFactor);
        return valueMap;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getMultiplicationFactor() {
        return multiplicationFactor;
    }

    public void setMultiplicationFactor(String multiplicationFactor) {
        this.multiplicationFactor = multiplicationFactor;
    }

    public String getPrevReadingDate() {
        return prevReadingDate;
    }

    public void setPrevReadingDate(String prevReadingDate) {
        this.prevReadingDate = prevReadingDate;
    }
}

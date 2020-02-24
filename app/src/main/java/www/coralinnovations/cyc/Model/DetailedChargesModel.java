package www.coralinnovations.cyc.Model;

import com.google.gson.annotations.SerializedName;

public class DetailedChargesModel {
    @SerializedName("energyCharges")
    private Double energyCharges;

    @SerializedName("custCharges")
    private Double custCharges;

    @SerializedName("fixedCharges")
    private Double fixedCharges;

    @SerializedName("eDutyCharges")
    private Double eDutyCharges;

    @SerializedName("dueDate")
    private String dueDate;

    @SerializedName("period")
    private Double period;

    @SerializedName("consumptionDays")
    private Double consumptionDays;

    @SerializedName("timestamp")
    private String timestamp;

    public Double getEnergyCharges() {
        return energyCharges;
    }

    public void setEnergyCharges(Double energyCharges) {
        this.energyCharges = energyCharges;
    }

    public Double getCustCharges() {
        return custCharges;
    }

    public void setCustCharges(Double custCharges) {
        this.custCharges = custCharges;
    }

    public Double getFixedCharges() {
        return fixedCharges;
    }

    public void setFixedCharges(Double fixedCharges) {
        this.fixedCharges = fixedCharges;
    }

    public Double geteDutyCharges() {
        return eDutyCharges;
    }

    public void seteDutyCharges(Double eDutyCharges) {
        this.eDutyCharges = eDutyCharges;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public Double getConsumptionDays() {
        return consumptionDays;
    }

    public void setConsumptionDays(Double consumptionDays) {
        this.consumptionDays = consumptionDays;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

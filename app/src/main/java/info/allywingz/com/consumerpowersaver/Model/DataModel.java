package info.allywingz.com.consumerpowersaver.Model;

/**
 * Created by srinu on 5/13/2018.
 */

public class DataModel {
    String item_name ;
    String item_power ;

    String item_maker ;
    String item_qty ;
    String watts ;
    String usage_hrs ;
    String usage_days ;
    String total_units ;


    public String getItem_maker() {
        return item_maker;
    }

    public void setItem_maker(String item_maker) {
        this.item_maker = item_maker;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_power() {
        return item_power;
    }

    public void setItem_power(String item_power) {
        this.item_power = item_power;
    }

    public String getItem_qty() {
        return item_qty;
    }

    public void setItem_qty(String item_qty) {
        this.item_qty = item_qty;
    }

    public String getWatts() {
        return watts;
    }

    public void setWatts(String watts) {
        this.watts = watts;
    }

    public String getUsage_hrs() {
        return usage_hrs;
    }

    public void setUsage_hrs(String usage_hrs) {
        this.usage_hrs = usage_hrs;
    }

    public String getUsage_days() {
        return usage_days;
    }

    public void setUsage_days(String usage_days) {
        this.usage_days = usage_days;
    }

    public String getTotal_units() {
        return total_units;
    }

    public void setTotal_units(String total_units) {
        this.total_units = total_units;
    }
}

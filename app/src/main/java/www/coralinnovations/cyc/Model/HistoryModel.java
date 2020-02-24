package www.coralinnovations.cyc.Model;

public class HistoryModel {
    String date ;
    String Avg_unit_per_day ;
    String expected_amount ;

    public HistoryModel(String date, String avg_unit_per_day, String expected_amount) {
        this.date = date;
        Avg_unit_per_day = avg_unit_per_day;
        this.expected_amount = expected_amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvg_unit_per_day() {
        return Avg_unit_per_day;
    }

    public void setAvg_unit_per_day(String avg_unit_per_day) {
        Avg_unit_per_day = avg_unit_per_day;
    }

    public String getExpected_amount() {
        return expected_amount;
    }

    public void setExpected_amount(String expected_amount) {
        this.expected_amount = expected_amount;
    }
}

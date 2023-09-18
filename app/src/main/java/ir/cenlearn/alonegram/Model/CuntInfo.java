package ir.cenlearn.alonegram.Model;

public class CuntInfo {

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    String clock, days, month, years;

    public CuntInfo(String clock, String days, String month, String years){
        this.clock = clock;
        this.days = days;
        this.month = month;
        this.years = years;
    }

}

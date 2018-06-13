package app.model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Wojciech Jaronski
 */
public class MyValue implements Comparable{
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private String string;
    private Integer number;
    private Date date;
    private Date date2;

    MyValue(String string, Date date) {
        this.string = string;
        this.date = date;
        this.date2 = date;
    }

    MyValue(String s, Integer number, Date date) {
        string = s;
        this.number = number;
        this.date = date;
        this.date2 = date;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    @Override
    public String toString() {
        return string;
    }

    public boolean isBefore(LocalDate value) {
        try {
            Date compareDate = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return date.before(compareDate);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isAfter(LocalDate value) {
        try {
            Date compareDate = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
            return date.after(compareDate);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public int compareTo(Object o) {
        return date.compareTo(((MyValue) o).date);
    }

    public String getDateAsString() {
        try{
            return format.format(date2);
        }catch (Exception e){
            return "";
        }
    }
}

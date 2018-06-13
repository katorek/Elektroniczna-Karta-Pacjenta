package app.model;

/**
 * Created by Wojciech Jaronski
 */

public class MyPoint {
    int col;
    int row;
    String label;
    Class type;

//    static MyPoint of(String label, int col, int row,) {
//        return new MyPoint(col, row, label);
//    }

    public MyPoint(String label, int col, int row, Class type) {
        this.col = col;
        this.row = row;
        this.label = label;
        this.type = type;
    }

    public MyPoint(String label, int col, int row) {
        this(label, col, row, String.class);
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public String getLabel() {
        return label;
    }
}

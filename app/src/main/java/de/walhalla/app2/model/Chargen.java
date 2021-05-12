package de.walhalla.app2.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chargen implements Cloneable {
    int semester, x, vx, fm, xx, xxx;

    public Chargen() {
        this.semester = 0;
        this.x = 0;
        this.vx = 0;
        this.fm = 0;
        this.xx = 0;
        this.xxx = 0;
    }

    public Chargen(int semester, int x, int vx, int fm, int xx, int xxx) {
        this.semester = semester;
        this.x = x;
        this.vx = vx;
        this.fm = fm;
        this.xx = xx;
        this.xxx = xxx;
    }

    public int getSemester() {
        return semester;
    }

    public int getX() {
        return x;
    }

    public int getVX() {
        return vx;
    }

    public int getFM() {
        return fm;
    }

    public int getXX() {
        return xx;
    }

    public int getXXX() {
        return xxx;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setVX(int vx) {
        this.vx = vx;
    }

    public void setFM(int fm) {
        this.fm = fm;
    }

    public void setXX(int xx) {
        this.xx = xx;
    }

    public void setXXX(int xxx) {
        this.xxx = xxx;
    }

    @NotNull
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("x", getX());
        data.put("xx", getXX());
        data.put("xxx", getXXX());
        data.put("vx", getVX());
        data.put("fm", getFM());

        return data;
    }
}

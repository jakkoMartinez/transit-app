package com.zuk0.gaijinsmash.riderz.model.bart;

public class Estimate {

    private String minutes; // can be 'leaving'
    private int platform;
    private String direction;
    private int length;
    private String color;
    private String hexcolor;
    private int bikeflag;
    private int delay;

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHexcolor() {
        return hexcolor;
    }

    public void setHexcolor(String hexcolor) {
        this.hexcolor = hexcolor;
    }

    public int getBikeflag() {
        return bikeflag;
    }

    public void setBikeflag(int bikeflag) {
        this.bikeflag = bikeflag;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }


}

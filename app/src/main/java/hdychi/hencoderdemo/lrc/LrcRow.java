package hdychi.hencoderdemo.lrc;

import android.support.annotation.NonNull;

public class LrcRow implements Comparable<LrcRow> {
    private int time;//开始时间,millisecond
    private String timeStr;
    private String content;//歌词内容
    private int totalTime;//歌曲经过的时间

    public LrcRow() {
        super();
    }

    public LrcRow(String timeStr,int totalTime,String content) {
        super();
        this.timeStr = timeStr;
        this.content = content;
        this.time = totalTime;
    }

    @Override
    public int compareTo(@NonNull LrcRow lrcRow) {
        return this.getTime() - lrcRow.getTime();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
    @Override
    public String toString() {
        return "LrcRow [timeStr=" + timeStr + ", time=" + time + ", content="
                + content + "]";
    }
}

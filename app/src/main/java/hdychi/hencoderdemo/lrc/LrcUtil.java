package hdychi.hencoderdemo.lrc;

import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LrcUtil {
    public static List<LrcRow> createRows(String lrcLine){
        if(!lrcLine.startsWith("[")){
            return null;
        }
        int lastRightBarcketIndex = lrcLine.lastIndexOf("]");
        String content = lrcLine.substring(lastRightBarcketIndex + 1,lrcLine.length());
        String[] tems = lrcLine.substring(0,lastRightBarcketIndex)
                .replace('[','-').replace(']','-').split("-");
        List<LrcRow> res = new ArrayList<>();
        for(String tem : tems){
            if(tem.trim().length()==0 || content.length() == 0){
                continue;
            }
            LrcRow lrcRow = new LrcRow(tem,formatTime(tem),content);
            res.add(lrcRow);
        }

        return res;
    }
    public static int formatTime(String timeStr){
        timeStr = timeStr.replace('.',':');
        String[] times = timeStr.split(":");
        return Integer.parseInt(times[0]) * 60 * 1000
                + Integer.parseInt(times[1]) * 1000
                + Integer.parseInt(times[2]);
    }
    public static List<LrcRow> getLrcRows(String str) {
        List<LrcRow> lrcRows = new ArrayList<LrcRow>();
        BufferedReader br = new BufferedReader(new StringReader(str));
        String lrcLine;
        try {
            while ((lrcLine = br.readLine()) != null) {
                List<LrcRow> rows = createRows(lrcLine);
                if (rows != null && rows.size() > 0) {
                    lrcRows.addAll(rows);
                }
            }
            Collections.sort(lrcRows);
            int len = lrcRows.size();
            for (int i = 0; i < len - 1; i++) {
                lrcRows.get(i).setTotalTime(lrcRows.get(i + 1).getTime() - lrcRows.get(i).getTime());
            }
            lrcRows.get(len - 1).setTotalTime(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return lrcRows;
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

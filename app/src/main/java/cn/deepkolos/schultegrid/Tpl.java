package cn.deepkolos.schultegrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by deepkolos on 2018/3/25.
 */

public class Tpl {
    public static View instance (Context ctx, int layoutId ,int resId) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(layoutId, null);
        ViewGroup root = (ViewGroup) view.getRootView();
        View result = root.findViewById(resId);
        root.removeAllViews();
        return result;
    }
}

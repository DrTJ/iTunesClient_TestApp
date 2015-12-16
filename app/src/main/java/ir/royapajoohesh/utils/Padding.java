package ir.royapajoohesh.utils;

import android.view.View;

/**
 * Created by DrTJ on 11/21/2015.
 */
public class Padding {
    public int PaddingTop;
    public int PaddingRight;
    public int PaddingBottom;
    public int PaddingLeft;

    public static Padding getPadding(View v) {
        Padding res = new Padding();

        res.PaddingTop = v.getPaddingTop();
        res.PaddingRight = v.getPaddingRight();
        res.PaddingBottom = v.getPaddingBottom();
        res.PaddingLeft = v.getPaddingLeft();

        return res;
    }

    public static void setPadding(Padding paddingData, View v) {
        v.setPadding(paddingData.PaddingLeft, paddingData.PaddingTop, paddingData.PaddingRight, paddingData.PaddingBottom);
    }

    public void applyTo(View v) {
        v.setPadding(this.PaddingLeft, this.PaddingTop, this.PaddingRight, this.PaddingBottom);
    }


}

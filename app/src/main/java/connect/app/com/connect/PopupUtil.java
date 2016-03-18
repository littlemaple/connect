package connect.app.com.connect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 44260 on 2016/3/16.
 */
public class PopupUtil {

    public static void show(Context context, View parent, List<PopupItem> list) {
        list = createNative();
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.VERTICAL);
        final PopupWindow popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        for (int i = 0; i < list.size(); i++) {
            PopupItem item = list.get(i);
            TextView textView = new TextView(context);
            textView.setText(item.getName());
            textView.setPadding(0, 20, 0, 20);
            if (i != list.size() - 1)
                textView.setBackgroundResource(R.drawable.shape);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
            container.addView(textView);
        }
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.message_ic_more));
        popupWindow.update();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        container.measure(widthMeasureSpec, heightMeasureSpec);
        int popupWidth = container.getMeasuredWidth();
        int popupHeight = container.getMeasuredHeight();
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, ((int) parent.getX() + parent.getWidth() / 2) - popupWidth / 2, (int) parent.getY() - popupHeight);
    }

    private static List<PopupItem> createNative() {
        List<PopupItem> list = new ArrayList<>();
        list.add(new PopupItem().setDisable(false).setName("ret"));
        list.add(new PopupItem().setDisable(false).setName("re234567t"));
        list.add(new PopupItem().setDisable(false).setName("r1234567654321et"));
        list.add(new PopupItem().setDisable(false).setName("ret"));
        list.add(new PopupItem().setDisable(false).setName("ret"));
        return list;
    }

}

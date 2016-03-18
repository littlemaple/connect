package connect.app.com.connect;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import java.lang.ref.SoftReference;


public class CountdownButton extends TextView {
    private int timeSeconds = 0;
    private boolean isValid = false;
    private static final int MSG_COUNTDOWN = 0x10;
    private static final int DEFAULT_PERIOD = 1;
    private int period = DEFAULT_PERIOD;

    public CountdownButton(Context context) {
        this(context, null);
    }

    public CountdownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setTimeSeconds(10);
    }

    public CountdownButton setPeriod(int period) {
        this.period = period;
        return this;
    }

    private SoftReference<Handler> softReference = new SoftReference<Handler>(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != MSG_COUNTDOWN)
                return;
            if (!isValid) {
                CountdownButton.this.finish();
                return;
            }
            timeSeconds--;
            setText(formatSeconds(timeSeconds));
            if (timeSeconds == 0) {
                removeMessages(MSG_COUNTDOWN);
                CountdownButton.this.finish();
            } else {
                sendEmptyMessageDelayed(MSG_COUNTDOWN, period * 1000);
            }
        }
    });


    public CountdownButton setTextColor(String color) {
        setTextColor(Color.parseColor(color));
        return this;
    }

    public void setTimeSeconds(int timeSeconds) {
        this.timeSeconds = timeSeconds;
        isValid = true;
        setText(formatSeconds(timeSeconds));
        Handler handler = softReference.get();
        if (handler == null) {
            finish();
            return;
        }
        handler.sendEmptyMessageDelayed(MSG_COUNTDOWN, period * 1000);
    }

    private void finish() {
        isValid = false;
        if (countdownListener != null)
            countdownListener.complete();
    }

    OnCountdownListener countdownListener;

    public CountdownButton setCountdownListener(OnCountdownListener countdownListener) {
        this.countdownListener = countdownListener;
        return this;
    }

    public interface OnCountdownListener {
        void complete();
    }

    public static String formatSeconds(int time) {
        String timeStr;
        int hour;
        int minute;
        int second;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}

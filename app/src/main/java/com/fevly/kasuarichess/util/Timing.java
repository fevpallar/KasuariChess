package com.fevly.kasuarichess.util;

import android.os.Handler;
import android.widget.TextView;

public class Timing {

    public void processGameTime(TextView textView) {
        final long totalMillis = 3 * 60 * 1000; // 3 minutes in milliseconds
        Handler handler = new Handler();

        //============== default 3 menit dulu ,
        handler.post(new Runnable() {
            long remainingMillis = totalMillis;

            @Override
            public void run() {
                long minutes = remainingMillis / (60 * 1000);
                long seconds = (remainingMillis % (60 * 1000)) / 1000;

                String timeText = String.format("%02d:%02d", minutes, seconds);
                textView.setText(timeText);

                remainingMillis -= 1000;

                if (remainingMillis >= 0) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

}

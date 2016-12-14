package com.things;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class BlinkActivity extends Activity {
    private static final String TAG = "BlinkActivity";
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private static final String GPIO_PIN_NAME = "pin_one";

    private Handler mHandler = new Handler();

    private Gpio mLedGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Step 1. Create GPIO connection.
        PeripheralManagerService service = new PeripheralManagerService();
        try {
            mLedGpio = service.openGpio(GPIO_PIN_NAME);
        // Step 2. Configure as an output.
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        // Step 3. Repeat using a handler.
            mHandler.post(mBlinkRunnable);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
    }
 }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Step 4. Remove handler events on close.
        mHandler.removeCallbacks(mBlinkRunnable);
        // Step 5. Close the resource.
        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            // Exit if the GPIO is already closed
            if (mLedGpio == null) {
                return;
            }

            try {
                // Step 3. Toggle the LED state
                mLedGpio.setValue(!mLedGpio.getValue());

                // Step 4. Schedule another event after delay.
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
}
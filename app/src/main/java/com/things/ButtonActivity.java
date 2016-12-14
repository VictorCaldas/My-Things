package com.things;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class ButtonActivity extends Activity {
    private static final String TAG = "ButtonActivity";
    private static final String GPIO_PIN_NAME = "pin_one";

    private ButtonInputDriver mButtonInputDriver;

    private Gpio mButtonGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PeripheralManagerService service = new PeripheralManagerService();
        try {
            // Step 1. Create GPIO connection.
            mButtonGpio = service.openGpio(GPIO_PIN_NAME);
            // Step 2. Configure as an input.
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            // Step 3. Enable edge trigger events.
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);

            // Step 4. Register an event callback.
            mButtonGpio.registerGpioCallback(mCallback);
        } catch (IOException e) {
            Log.e(TAG, "Erro no PeripheralIO API", e);
        }


        try {
            // Step 3. Initialize button driver with selected GPIO pin
            mButtonInputDriver = new ButtonInputDriver(
                    GPIO_PIN_NAME,
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_SPACE);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pin", e);
        }
    }

    // Step 4. Register an event callback.
    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO mudou, botao pressionado");

            // Step 5. Return true to keep callback active.
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Step 5. Close the driver and unregister
        if (mButtonInputDriver != null) {
            try {
                mButtonInputDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            }
        }

        // Step 6. Close the resource
        if (mButtonGpio != null) {
            mButtonGpio.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Erro no PeripheralIO API", e);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mButtonInputDriver.register();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mButtonInputDriver.unregister();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            // Handle button pressed event
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            // Handle button released event
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}

package net.kibotu.android.error.tracking;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load secrets
        JSONObject appConfig = JSONUtils.loadJsonFromAssets(this, "app.json");
        final String applicationId = appConfig.optString("applicationId");
        final String clientKey = appConfig.optString("clientKey");

        // start error tracking
        ErrorTracking.startSession(this, applicationId, clientKey);

        // add api level
        JSONObject metaData = new JSONObject();
        JSONUtils.safePut(metaData, "SDK", "" + Build.VERSION.SDK_INT);
        ErrorTracking.addMetaData(metaData);

//        testLowMemory();
//        stub1();
    }

    @Override
    public void onStop() {
        super.onStop();
        ErrorTracking.endSession();
    }

    // region crashes

    private void stub1(){
        testNullPointer();
    }

    private void stub2() {
        testNullPointer();
    }

    private void stub3() {
        testNullPointer();
    }

    private void testNullPointer() {
        String s = null;
        s.toString();
    }

    private void testLowMemory() {
        long[] l = new long[Integer.MAX_VALUE];
    }

    // endregion
}
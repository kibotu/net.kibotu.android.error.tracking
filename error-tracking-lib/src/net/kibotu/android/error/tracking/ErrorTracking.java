package net.kibotu.android.error.tracking;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

public class ErrorTracking {

    private static Context context;
    private static JSONObject metaData;

    public static void startSession(final Context context, final String parseAppKey, final String parseClientKey) {
        ErrorTracking.context = context;
        Logger.init(new LogcatLogger(context), ErrorTracking.class.getSimpleName(), Logger.Level.VERBOSE);

        metaData = new JSONObject();

        // Parse.initialize(context, parseAppKey, parseClientKey);
        ReflectionHelper.tryInvokePublicStatic("com.parse.Parse", "initialize",
                new ReflectionHelper.ReflectionHelperParamater(
                        new Class<?>[]{Context.class, String.class, String.class},
                        new Object[]{context, parseAppKey, parseClientKey}));


        Logger.v("Parse.initialize( " + parseAppKey + ", " + parseClientKey + " )");

        UncaughtExceptionHandlerDecorator.setHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable e) {
                notifyError(e);
            }
        });
    }

    public static void notifyError(final Throwable t) {
        if (context == null) {
            Logger.e("Error tracking not initialized and therefore disabled. Please call first ErrorTrackingHelper.startErrorTracking()");
            return;
        }

        // ParseObject throwable = new ParseObject("Throwable");
        final Class throwableCls = ReflectionHelper.tryClassForName("com.parse.ParseObject");

        // throwable.put("metaData", metaData);
        final Object throwable = ReflectionHelper.contruct(throwableCls, new ReflectionHelper.ReflectionHelperParamater(new Class[]{String.class}, new Object[]{"Throwable"}));
        ReflectionHelper.tryInvoke(throwableCls, throwable, "put", new ReflectionHelper.ReflectionHelperParamater(
                new Class<?>[]{String.class, Object.class},
                new Object[]{"metaData", metaData}));

        // create exception json
        JSONArray exceptions = new JSONArray();
        addException(exceptions, t);

        // throwable.put("exceptionJson", "" + exceptions);
        ReflectionHelper.tryInvoke(throwableCls, throwable, "put", new ReflectionHelper.ReflectionHelperParamater(
                new Class[]{String.class, Object.class},
                new Object[]{"exceptionJson", "" + exceptions}));

        // throwable.saveEventually();
        ReflectionHelper.tryInvoke(throwableCls, throwable, "saveInBackground"); // saveEventually
    }

    private static void addException(final JSONArray exceptions, final Throwable throwable) {
        // Unwrap exceptions
        if (throwable != null) {
            Throwable currentEx = throwable;
            while (currentEx != null) {
                final JSONObject exception = new JSONObject(); // current exception
                JSONUtils.safePut(exception, "errorType", currentEx.getClass().getName());
                JSONUtils.safePut(exception, "message", currentEx.getMessage());
                addStacktraceElements(exception, currentEx.getStackTrace(), 0); // add current stacktrace to the current exception
                exceptions.put(exception); // add the current exception to all exception array
                currentEx = currentEx.getCause();
            }
        } else {
            Logger.w("Exception is null.");
        }
    }

    private static void addStacktraceElements(final JSONObject json, StackTraceElement[] stackTrace, final int startIndex) {
        if (stackTrace.length < startIndex) {
            Logger.w("stackTrace.length < startIndex");
            return;
        }

        final JSONArray stacktrace = new JSONArray();
        final String packageName = context.getPackageName();

        for (int i = startIndex; i < stackTrace.length; ++i) {
            try {
                final StackTraceElement el = stackTrace[i];
                final JSONObject line = new JSONObject();
                JSONUtils.safePut(line, "class", el.getClassName());
                JSONUtils.safePut(line, "method", el.getMethodName());
                JSONUtils.safePut(line, "file", el.getLineNumber() == -2 ? "<Native Method>" : el.getFileName() == null ? "Unknown" : el.getFileName()); // line number -2 = native method
                JSONUtils.safePut(line, "lineNumber", el.getLineNumber());

                // Check if line is inProject
                if (el.getClassName().startsWith(packageName)) {
                    line.put("inProject", true);
                    // break;
                }
                stacktrace.put(line);
            } catch (final Exception e) {
                Logger.e(e.getMessage(), e);
            }
        }
        JSONUtils.safeAppendToJsonArray(json, "stacktrace", stacktrace);
    }

    public static void addMetaData(final JSONObject metaData) {
        if (ErrorTracking.metaData == null) ErrorTracking.metaData = metaData;
        else
            JSONUtils.merge(ErrorTracking.metaData, metaData);
    }

    public static void endSession() {
        UncaughtExceptionHandlerDecorator.release();
    }
}
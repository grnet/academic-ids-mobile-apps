package gr.grnet.academicid.inspector;

import android.app.Application;
import android.content.Context;
import gr.grnet.academicid.inspector.domain.Inspector;
import gr.grnet.academicid.inspector.utilities.SharedPrefs;

public class InspectorApplication extends Application {

    private static Context context;
    private static Inspector inspector;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public InspectorApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InspectorApplication.context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SharedPrefs.ClearInspectorFromPrefs();
    }

    public static Context getAppContext() {
        return InspectorApplication.context;
    }

    public static Inspector getInspector() {
        // Check if object has been deleted from Android garbage collector and if it has, recall it from Shared Preferences
        if (inspector == null) {
            inspector = SharedPrefs.getInspectorFromPrefs();
        }
        return inspector;
    }

    public static void setInspector(Inspector inspector) {
        InspectorApplication.inspector = inspector;
        //Save object to Shared Preferences to prevent NPE when app resuming and android garbage collector has cleared this
        SharedPrefs.setInspectorToPrefs(inspector);
    }
}
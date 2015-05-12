package gr.grnet.academicid.merchant;

import android.app.Application;
import android.content.Context;
import gr.grnet.academicid.merchant.domain.Inspector;
import gr.grnet.academicid.merchant.domain.Offer;
import gr.grnet.academicid.merchant.utilities.SharedPrefs;

public class MerchantApplication extends Application {

    private static Context context;
    private static Inspector inspector;
    private static Offer selectedOffer;

//\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/

    public MerchantApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MerchantApplication.context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SharedPrefs.ClearInspectorFromPrefs();
    }

    public static Context getAppContext() {
        return MerchantApplication.context;
    }

    public static Inspector getInspector() {
        // Check if object has been deleted from Android garbage collector and if it has, recall it from Shared Preferences
        if (inspector == null) {
            inspector = SharedPrefs.getInspectorFromPrefs();
        }
        return inspector;
    }

    public static void setInspector(Inspector inspector) {
        MerchantApplication.inspector = inspector;
        //Save object to Shared Preferences to prevent NPE when app resuming and android garbage collector has cleared this
        SharedPrefs.setInspectorToPrefs(inspector);
    }

    public static Offer getSelectedOffer() {
        return selectedOffer;
    }

    public static void setSelectedOffer(Offer selectedOffer) {
        MerchantApplication.selectedOffer = selectedOffer;
    }
}

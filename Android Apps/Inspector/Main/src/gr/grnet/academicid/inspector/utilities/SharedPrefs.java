package gr.grnet.academicid.inspector.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import gr.grnet.academicid.inspector.InspectorApplication;
import gr.grnet.academicid.inspector.domain.Inspector;

/**
 * Helper class for handling Preferences.
 */
public class SharedPrefs {

    /**
     * Prevents this class from being instantiated.
     */
    private SharedPrefs() {
    }

    /**
     * This method retrieves an Inspector object from Shared Preferences.
     *
     * @return An Inspector object.
     */
    public static Inspector getInspectorFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InspectorApplication.getAppContext());

        Inspector inspector = new Inspector();
        inspector.setFirstname(preferences.getString(Inspector.FIRSTNAME, ""));
        inspector.setLastname(preferences.getString(Inspector.LASTNAME, ""));
        inspector.setUsername(preferences.getString(Inspector.USERNAME, ""));
        inspector.setPasswordHash(preferences.getString(Inspector.PASSWORD_HASH, ""));
        inspector.setChangePswAtLogin(preferences.getBoolean(Inspector.CHANGE_PSW_AT_LOGIN, false));
        inspector.setOrgId(preferences.getLong(Inspector.ORG_ID, 0));
        inspector.setOrgName(preferences.getString(Inspector.ORG_NAME, ""));
        inspector.setOrgDescription(preferences.getString(Inspector.ORG_DESCRIPTION, ""));

        return inspector;
    }

    /**
     * This method stores Inspector object in Shared Preferences.
     *
     * @param inspector The Inspector object that will be saved in Shared Preferences.
     */
    public static void setInspectorToPrefs(Inspector inspector) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InspectorApplication.getAppContext());
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.putString(Inspector.FIRSTNAME, inspector.getFirstname());
        preferencesEditor.putString(Inspector.LASTNAME, inspector.getLastname());
        preferencesEditor.putString(Inspector.USERNAME, inspector.getUsername());
        preferencesEditor.putString(Inspector.PASSWORD_HASH, inspector.getPasswordHash());
        preferencesEditor.putBoolean(Inspector.CHANGE_PSW_AT_LOGIN, inspector.shouldChangePswAtLogin());
        preferencesEditor.putLong(Inspector.ORG_ID, inspector.getOrgId());
        preferencesEditor.putString(Inspector.ORG_NAME, inspector.getOrgName());
        preferencesEditor.putString(Inspector.ORG_DESCRIPTION, inspector.getOrgDescription());

        preferencesEditor.commit();
    }

    /**
     * This method clear Inspector values from Shared Preferences.
     */
    public static void ClearInspectorFromPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InspectorApplication.getAppContext());
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.remove(Inspector.FIRSTNAME);
        preferencesEditor.remove(Inspector.LASTNAME);
        preferencesEditor.remove(Inspector.USERNAME);
        preferencesEditor.remove(Inspector.PASSWORD_HASH);
        preferencesEditor.remove(Inspector.CHANGE_PSW_AT_LOGIN);
        preferencesEditor.remove(Inspector.ORG_ID);
        preferencesEditor.remove(Inspector.ORG_NAME);
        preferencesEditor.remove(Inspector.ORG_DESCRIPTION);

        preferencesEditor.commit();
    }
}
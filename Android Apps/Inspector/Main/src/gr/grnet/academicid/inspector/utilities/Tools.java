package gr.grnet.academicid.inspector.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import gr.grnet.academicid.inspector.InspectorApplication;
import gr.grnet.academicid.inspector.domain.AcademicId;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class with utility functionality.
 */
public class Tools {

    /**
     * Prevents this class from being instantiated.
     */
    private Tools() {
    }

    /**
     * This method verifies that the device has network connectivity.
     *
     * @return True if the device has network connectivity.
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) InspectorApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * This method verifies that a camera is available.
     *
     * @return True if the camera of the device is available.
     */
    public static boolean isCameraAvailable() {
        PackageManager packageManager = InspectorApplication.getAppContext().getPackageManager();
        return (packageManager != null && packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    /**
     * This method verifies that the passed parameter is numeric.
     *
     * @param numStr Contains the questioned value.
     * @return True if numStr is numeric.
     */
    public static boolean isNumericValue(String numStr) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseLong(numStr);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * This method calculates the hash value of the parameter passed based on the DIGEST_ALGORITHM that is used.
     *
     * @param message The plain message that will be hashed
     * @return The hash equivalent of message according to digest algorithm that was used.
     */
    public static String getHash(String message) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(Constants.DIGEST_ALGORITHM);
            byte[] hashedArray = messageDigest.digest(message.getBytes());
            BigInteger hashedNumber = new BigInteger(1, hashedArray);

            return hashedNumber.toString(16);
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.LOGTAG, "Tools.getHash(): The designated algorithm: " + Constants.DIGEST_ALGORITHM + " can't be found.", e);
            return null;
        }
    }

    /**
     * This method concatenates the two parameters passed and encodes them using Base64 scheme.
     *
     * @param str1 This first parameter that needs to be encoded.
     * @param str2 This second parameter  that needs to be encoded.
     * @return a encoded with Base64 scheme String.
     */
    public static String encodeBase64(String str1, String str2) {
        byte[] authEncBytes = Base64.encode((str1 + ":" + str2).getBytes(), Base64.NO_WRAP);
        return new String(authEncBytes);
    }

    /**
     * This method constructs a 2+2 letter concatenated string from firstname and lastname.
     * Method checks if greek name exists and if not proceeds with latin name.
     *
     * @param academicId the object containing the first and lastname that should be concatenated to 2+2 letters.
     * @return a 2+2 concatenated string containing the first two letters from firstname and lastname.
     */
    public static String concatenateName(AcademicId academicId) {
        if (academicId == null) {
            return null;

        } else if ((academicId.getGreekFirstname() != null) && (academicId.getGreekFirstname().length() != 0) &&
                (academicId.getGreekLastname() != null) && (academicId.getGreekLastname().length() != 0)) {
            return (academicId.getGreekFirstname().substring(0, 2) + " " + academicId.getGreekLastname().substring(0, 2));

        } else if ((academicId.getLatinFirstname() != null) && (academicId.getLatinFirstname().length() != 0) &&
                (academicId.getLatinLastname() != null) && (academicId.getLatinLastname().length() != 0))
            return (academicId.getLatinFirstname().substring(0, 2) + " " + academicId.getLatinLastname().substring(0, 2));

        else
            return null;
    }
}
package gr.grnet.academicid.merchant.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import gr.grnet.academicid.merchant.MerchantApplication;
import gr.grnet.academicid.merchant.domain.Offer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ConnectivityManager connectivityManager = (ConnectivityManager) MerchantApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * This method verifies that a camera is available.
     *
     * @return True if the camera of the device is available.
     */
    public static boolean isCameraAvailable() {
        PackageManager packageManager = MerchantApplication.getAppContext().getPackageManager();
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
     * This method splits the offers  provided with offerList param to two lists based on whether the offer is active or not.
     *
     * @param offerList         the original list containing both active and inactive offers.
     * @param activeOfferList   the list containing only active offers.
     * @param inactiveOfferList the list containing only inactive offers.
     */
    public static void splitOffers(List<Offer> offerList, List<Offer> activeOfferList, List<Offer> inactiveOfferList) {
        for (Offer offer : offerList) {
            if (offer.getStatus() == 3) //Status code 3 designates Active Offers
                activeOfferList.add(offer);
            else
                inactiveOfferList.add(offer);
        }
    }

    /**
     * This method gets a list with offers and prepares it to be fed in a list adapter.
     *
     * @param offerList the original list with offers.
     * @return a List < Map < String,String>> for the ListAdapter.
     */
    public static List<Map<String, String>> prepareListForAdapter(List<Offer> offerList) {
        List<Map<String, String>> adapterList = new ArrayList<Map<String, String>>();

        for (Offer offer : offerList) {
            Map<String, String> adapterOffer = new HashMap<String, String>();
            adapterOffer.put(Offer.TITLE, offer.getTitle());
            adapterOffer.put(Offer.DESCRIPTION, offer.getDescription());

            adapterList.add(adapterOffer);
        }
        return adapterList;
    }
}

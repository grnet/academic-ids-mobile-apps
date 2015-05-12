package gr.grnet.academicid.inspector.utilities;

import java.util.Arrays;
import java.util.List;

/**
 * Constants globally available in Inspector application.
 */
public class Constants {

    public static final String LOGTAG = "Inspector";

    // The message digest algorithm that is used to hash user password.
    public static final String DIGEST_ALGORITHM = "MD5";

    // An academicId is invalid if:
    // (response=success && pasovalidity==false) OR (response=success && webservicesuccess==false && validationerror ΙΝ ValidationErrorList
    public static List<String> ValidationErrorList = Arrays.asList(
            "Δεν βρέθηκε αίτηση με το 12ψήφιο κωδικό που εισάγατε",
            "Ο 12ψήφιος που εισάγατε δεν αντιστοιχεί σε αίτηση φοιτητή",
            "Η Ακαδημαϊκή Ταυτότητα δεν έχει υποβληθεί οριστικά από το φοιτητή",
            "Η Ακαδημαϊκή Ταυτότητα έχει απορριφθεί από τη γραμματεία"
    );
}
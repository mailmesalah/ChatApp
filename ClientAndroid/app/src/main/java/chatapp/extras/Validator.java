package chatapp.extras;

/**
 * Created by Sely on 07-Sep-16.
 */
public class Validator {
    public static final int MAX_USER_ID=10;
    public static final int MIN_USER_ID=6;
    public static final int MAX_PASSW_LENGTH=15;
    public static final int MIN_PASSW_LENGTH=6;
    public static final int MAX_PROFILE_NAME_LENGTH=15;
    public static final int MIN_PROFILE_NAME_LENGTH=6;

    public static boolean validatePhoneNumber(String s) {
        String Regex = "[^\\d]";
        String PhoneDigits = s.replaceAll(Regex, "");
        return !(PhoneDigits.length() < 6 || PhoneDigits.length() > 13);
    }

    public static boolean validateUserID(String s) {
        return !(s.length() < MIN_USER_ID || s.length() > MAX_USER_ID);
    }

    public static boolean validatePassword(String s) {
        return !(s.length() < MIN_PASSW_LENGTH || s.length() > MAX_PASSW_LENGTH);
    }

    public static boolean validateProfileName(String s) {
        return !(s.length() < MIN_PROFILE_NAME_LENGTH || s.length() > MAX_PROFILE_NAME_LENGTH);
    }

}

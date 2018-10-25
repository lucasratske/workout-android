package br.com.ratske.workout.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

    /**
     * Verify if the email is valid
     * @param email
     * @return boolean
     */
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Verify if the passwork is valid
     * @param password
     * @return boolean
     */
    public static boolean isPasswordValid(String password) {
        return (password.length() >= 6);
    }

    /**
     * Change de point separator to comma separator in the float value
     * @param value
     * @return String
     */
    public static String numberToBrLocale(Double value) {
        Locale locale = new Locale("pt", "BR");
        return NumberFormat.getNumberInstance(locale).format(value);
    }

    /**
     * Change de comma separator to point separator in the float value
     * @param value
     * @return String
     */
    public static Double commaToPointSeparator(String value) {
        return Double.parseDouble( value.replace(",", "."));
    }

}

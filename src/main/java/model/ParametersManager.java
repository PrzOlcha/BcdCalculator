package model;

import java.util.List;

/**
 * A class that contains methods used to handling parameters
 *
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 2.0
 */
public class ParametersManager {

    /**
     * This method check if parameters are valid numbers written as decimal numbers
     *
     * @param parameters list of numbers in BCD notation
     * @return boolean information if given parameters are valid numbers in BCD notation, true if numbers are valid, false if numbers are't valid
     */
    public boolean checkParameters(List<String> parameters) {
        if ((parameters == null) || (parameters.isEmpty())) {
            return false;
        }
        boolean validParameters = true;
        for (String element : parameters) {
            if ((element == null) || ((element.length() % 4) != 0) || ((element.length()) == 0)) {
                validParameters = false;
            } else {
                int noQuartets = (element.length() / 4); //numbers of quartets that a number in BCD notation contains
                for (int i = 0; i < element.length(); i++) {
                    if ((element.charAt(i) != '1') && (element.charAt(i) != '0')) {
                        validParameters = false;
                    }
                }
                for (int i = 0; i < noQuartets; i++) {
                    String quartet = element.substring(4 * i, 4 * i + 4); //one digit of a number in decimal notation represented as four bits in BCD notation
                    if ((noQuartets > 1) && (i == 0)) {
                        if (quartet.equals("0000")) {
                            validParameters = false;
                        }
                    } else if (quartet.charAt(0) == '1') {
                        if ((quartet.charAt(1) == '1') || (quartet.charAt(2) == '1')) {
                            validParameters = false;
                        }
                    }
                }
            }
        }
        return validParameters;
    }
}

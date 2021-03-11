package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for methods used to perform calculations and convert numbers
 *
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 4.0
 */
public class Calculator {

    /**
     * List that stores results and numbers used to calculate them
     */
    public List<String> resultsHistory = new ArrayList<>();
    
    /**
     * This method starts the calculations of summation and subtraction results
     *
     * @param numbersBcd array containing numbers which are used to perform calculations
     * @return array containing results of summation and subtraction
     * @throws CalculatorException when array numbersBcd contains incorrect quantity of numbers (other than 2)
     */
    public List<String> runCalculations (List<String> numbersBcd) throws CalculatorException
    {
        if ((numbersBcd == null)||(numbersBcd.size()!=2)) {
            throw new CalculatorException();
        } else {
            List<Integer> numbersDecimal = this.convertToDecimal(numbersBcd.get(0),numbersBcd.get(1)); //list containing numbers changed to decimal notation
            List<String> resultsBcd = new ArrayList<>(); //list containing results of summation and subtraction
            resultsBcd.add(this.sumCalculate(numbersDecimal));
            resultsBcd.add(this.subCalculate(numbersDecimal));
            this.addToHistory(numbersBcd, resultsBcd);
            return resultsBcd;
        }
    }
    
    /**
     * This method calculate the sum of arguments
     *
     * @param numbersDecimal array containing the numbers to add
     * @return sum of numbers given as method parameters
     */
    private String sumCalculate(List<Integer> numbersDecimal) {
        int sum = 0;
        for (Integer element : numbersDecimal) {
            sum = sum + element;
        }
        return this.convertToBcd(sum);
    }

    /**
     * This method calculate the subtraction of arguments
     *
     * @param numbersDecimal list containing the numbers used for subtraction
     * @return subtraction result of numbers given as method parameters
     */
    private String subCalculate(List<Integer> numbersDecimal) {
        if ((numbersDecimal == null) || (numbersDecimal.isEmpty())) {
            return this.convertToBcd(0);
        } else {
            int sub = numbersDecimal.get(0);
            for (int i = 1; i < numbersDecimal.size(); i++) {
                sub = sub - numbersDecimal.get(i);
            }
            if (sub<0)
            {
                return "negative number";
            }
            else
            {
                return this.convertToBcd(sub);
            }
        }
    }
    
    /**
     * This method changes number notation from BCD to decimal
     *
     * @param receivedNumbers numbers in BCD notation stored as Strings
     * @return list containing the numbers converted to decimal (decimalNumbers)
     */
    private List<Integer> convertToDecimal(String... receivedNumbers) {
        List<Integer> decimalNumbers = new ArrayList<>(); //list of numbers changed to decimal notation
        boolean conversionFailure = false; //information whether conversion of any number has failed, true if yes, false if not
        if (receivedNumbers == null) {
            return decimalNumbers;
        }
        for (String element : receivedNumbers) {
            if (!conversionFailure) {
                if ((element.length() % 4 != 0) || (element.length() == 0)) {
                    conversionFailure = true;
                } else {
                    for (int i = 0; i < element.length(); i++) {
                        if ((element.charAt(i) != '1') && (element.charAt(i) != '0')) {
                            conversionFailure = true;
                        }
                    }
                    if (!conversionFailure) {
                        int noDecimalDigits = (element.length() / 4); //the number of digits that a number contains when in decimal notification
                        String decimalNumberStr = ""; //set of successive transformed digits from a number in BCD notation
                        for (int j = noDecimalDigits; j > 0; j--) {
                            String quartet = element.substring((4 * j) - 4, (4 * j)); //four bits of an number written in BCD notation that corresponds to one digit in decimal notation, stored as String
                            int quartetInt = Integer.parseInt(quartet); //four bits of an number written in BCD notation that corresponds to one digit in decimal notation, stored as Integer
                            char decimalDigit = '0'; //decimal digit received after changing a quartet from a number in BCD notation
                            switch (quartetInt) {
                                case 0:
                                    decimalDigit = '0';
                                    break;
                                case 1:
                                    decimalDigit = '1';
                                    break;
                                case 10:
                                    decimalDigit = '2';
                                    break;
                                case 11:
                                    decimalDigit = '3';
                                    break;
                                case 100:
                                    decimalDigit = '4';
                                    break;
                                case 101:
                                    decimalDigit = '5';
                                    break;
                                case 110:
                                    decimalDigit = '6';
                                    break;
                                case 111:
                                    decimalDigit = '7';
                                    break;
                                case 1000:
                                    decimalDigit = '8';
                                    break;
                                case 1001:
                                    decimalDigit = '9';
                                    break;
                                default:
                                    conversionFailure = true;
                                    break;
                            }
                            decimalNumberStr = decimalDigit + decimalNumberStr;
                        }
                        decimalNumbers.add(Integer.parseInt(decimalNumberStr));
                    }
                }
            }
        }
        if (conversionFailure == true) {
            decimalNumbers.clear();
        }
        return decimalNumbers;
    }

    /**
     * This method changes number notation from decimal to BCD
     *
     * @param receivedNumber number in decimal notation
     * @return number changed from decimal to BCD notation (bcdNumber)
     */
    private String convertToBcd(Integer receivedNumber) {
        String receivedNumberStr = Integer.toString(receivedNumber); //number in decimal notation stored as String
        int receivedNumberDigits = receivedNumberStr.length(); //number of digits the number contains
        String bcdNumber = ""; //set of successive transformed digits from a number in decimal notation
        for (int i = (receivedNumberDigits - 1); i >= 0; i--) {
            char digit = receivedNumberStr.charAt(i); //one digit from a number in decimal notation
            switch (digit) {
                case '0':
                    bcdNumber = "0000" + bcdNumber;
                    break;
                case '1':
                    bcdNumber = "0001" + bcdNumber;
                    break;
                case '2':
                    bcdNumber = "0010" + bcdNumber;
                    break;
                case '3':
                    bcdNumber = "0011" + bcdNumber;
                    break;
                case '4':
                    bcdNumber = "0100" + bcdNumber;
                    break;
                case '5':
                    bcdNumber = "0101" + bcdNumber;
                    break;
                case '6':
                    bcdNumber = "0110" + bcdNumber;
                    break;
                case '7':
                    bcdNumber = "0111" + bcdNumber;
                    break;
                case '8':
                    bcdNumber = "1000" + bcdNumber;
                    break;
                case '9':
                    bcdNumber = "1001" + bcdNumber;
                    break;
            }
        }
        return bcdNumber;
    }
    
    /**
     * This method saves results and numbers used to calculate them in list named resultsHistory
     *
     * @param numbersBcd list containing numbers used to calculate results
     * @param resultsBcd list containing calculated results
     */
    private void addToHistory(List<String> numbersBcd,List<String> resultsBcd)
    {
        for (int i=0;i<numbersBcd.size();i++) {
            resultsHistory.add(numbersBcd.get(i));
        }
        for (int i=0;i<resultsBcd.size();i++) {
            resultsHistory.add(resultsBcd.get(i));
        }
    }
    
}

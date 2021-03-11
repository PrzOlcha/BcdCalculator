package modelTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import model.Calculator;
import model.CalculatorException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

/**
 * A class containg tests which are used to verify runCalculations() method from Calculator class located in model package.
 *
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 4.0
 */
public class CalculatorTest {

    /**
     * This test is used to verify runCalculations() method located in Calculator class. 
     * It uses varied parameters and expected results definied in MethodSource "resultsStringListAndParametersStringListProvider". 
     * Test runs runCalculations method with these parameters and checks if the returned values equals to expected results (of summation and subtraction). 
     * All tests uses two parameters in a list, becuase it is the only situation when CalculatorException is not thrown.
     */
    @ParameterizedTest
    @MethodSource("resultsStringListAndParametersStringListProvider")
    void runCalculationsTest(List<String> resultsTestList, List<String> parametersTestList) {
        Calculator calculatorTest = new Calculator();
        try {
            assertEquals(resultsTestList, calculatorTest.runCalculations(parametersTestList), "Method runCalculations returned wrong values for parameters: " + parametersTestList.get(0) + " and " + parametersTestList.get(1)
                    + ". They are not the same as the expected ones.");
        } catch (CalculatorException e) {
            fail("This exception should be thrown if the number of parameters is different than 2 - then calculations cannot be performed. This exception should not be thrown, because there is proper number of parameters.");
        }
    }

    /**
     * This test is used to verify runCalculations() method located in Calculator class. 
     * It uses a list which contains incorrect quantity of numbers (other than 2) to call that method and check if exception is thrown as it should be.
     */
    @ParameterizedTest
    @NullSource
    @EmptySource
    @MethodSource("resultsStringListAndParametersStringListProviderException")
    void runCalculationsExceptionTest(List<String> parametersTestList) {
        Calculator calculatorTest = new Calculator();
        try {
            calculatorTest.runCalculations(parametersTestList);
            fail("This exception should be thrown if the number of parameters is different than 2 - then calculations cannot be performed. This exception should be thrown, because number of parameters is different than 2.");
        } catch (CalculatorException e) {
        }
    }

    /**
     * Arguments source for runCalculationsTest. 
     * It contains a list of expected results (of summation and subtraction) and an array of parameters that will be used to perform calculations.
     */
    static Stream<Arguments> resultsStringListAndParametersStringListProvider() {
        return Stream.of(
                arguments(Arrays.asList("0000", "0000"), Arrays.asList("0000", "0000")),
                arguments(Arrays.asList("0001", "0001"), Arrays.asList("0001", "0000")),
                arguments(Arrays.asList("0001", "negative number"), Arrays.asList("0000", "0001")),
                arguments(Arrays.asList("00010010", "00010000"), Arrays.asList("00010001", "0001")),
                arguments(Arrays.asList("00010010", "negative number"), Arrays.asList("0001", "00010001")),
                arguments(Arrays.asList("00010000", "1000"), Arrays.asList("1001", "0001"))
        );
    }

    /**
     * Arguments source for runCalculationsExceptionTest. 
     * It contains a list of parameters that will be used to perform calculations. 
     * Number of parameters in every list is different than 2.
     */
    static Stream<Arguments> resultsStringListAndParametersStringListProviderException() {
        return Stream.of(
                arguments(Arrays.asList()),
                arguments(Arrays.asList("0001")),
                arguments(Arrays.asList("0001", "0001", "0001")),
                arguments(Arrays.asList("0001", "0001", null)),
                arguments(Arrays.asList("0001", "0001", "0001", "0001", "0001", "0001"))
        );
    }
}

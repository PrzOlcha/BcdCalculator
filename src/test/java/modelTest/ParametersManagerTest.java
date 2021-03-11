package modelTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import model.ParametersManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.NullSource;

/**
 * A class containg tests which are used to verify method from ParameterManager class located in model package
 *
 * @author Przemyslaw Olcha, NSI, Informatyka, sem. V, BDIS, sekcja 2 (przeolc221@stundet.polsl.pl)
 * @version 2.0
 */
public class ParametersManagerTest {

    /**
     * This test is used to verify checkParameter() method located in ParametersManager class. 
     * It uses varied parameters definied in MethodSource "correctStringListProvider". 
     * Test runs checkParameter method with these parameters and checks if the returned value is the same as expected. 
     * The parameters are so selected that the returned value should be 'true' in every case.
     */
    @ParameterizedTest
    @MethodSource("correctStringListProvider")
    void checkParametersTestTrue(List<String> parametersStrings) {
        ParametersManager checkParameterTest = new ParametersManager();
        assertTrue(checkParameterTest.checkParameters(parametersStrings), "Method checkParameter returned 'false', but it should return 'true' for given parameters.");
    }

    /**
     * This test is used to verify checkParameter() method located in ParametersManager class. 
     * It uses varied parameters definied in MethodSource "correctStringListProvider". 
     * Test runs checkParameter method with these parameters and checks if the returned value is the same as expected. 
     * The parameters are so selected that the returned value should be 'false' in every case.
     */
    @ParameterizedTest
    @NullSource
    @MethodSource("incorrectStringListProvider")
    void checkParametersTestFalse(List<String> parametersStrings) {
        ParametersManager checkParameterTest = new ParametersManager();
        assertFalse(checkParameterTest.checkParameters(parametersStrings), "Method checkParameter returned 'true', but it should return 'false' for given parameters.");
    }

    /**
     * Arguments source for checkParametersTestTrue. 
     * It contains only Strings which are correctly representing numbers in BCD notation.
     */
    static Stream<Arguments> correctStringListProvider() {
        return Stream.of(
                arguments(Arrays.asList("0000")),
                arguments(Arrays.asList("0001")),
                arguments(Arrays.asList("0010")),
                arguments(Arrays.asList("0011")),
                arguments(Arrays.asList("0100")),
                arguments(Arrays.asList("0101")),
                arguments(Arrays.asList("0110")),
                arguments(Arrays.asList("0111")),
                arguments(Arrays.asList("1000")),
                arguments(Arrays.asList("1001")),
                arguments(Arrays.asList("00010000")),
                arguments(Arrays.asList("10011001")),
                arguments(Arrays.asList("000100000000")),
                arguments(Arrays.asList("000100010000")),
                arguments(Arrays.asList("100110011001")),
                arguments(Arrays.asList("0000", "0001")),
                arguments(Arrays.asList("0000", "1001", "0010", "1000", "0010", "0111"))
        );
    }

    /**
     * Arguments source for checkParametersTestTrue. 
     * It contains elements which are not representing numbers in BCD notation.
     */
    static Stream<Arguments> incorrectStringListProvider() {
        return Stream.of(
                arguments(Arrays.asList()),
                arguments(Arrays.asList("")),
                arguments(Arrays.asList("1010")),
                arguments(Arrays.asList("1011")),
                arguments(Arrays.asList("1100")),
                arguments(Arrays.asList("1101")),
                arguments(Arrays.asList("1110")),
                arguments(Arrays.asList("1111")),
                arguments(Arrays.asList("abcd")),
                arguments(Arrays.asList("0002")),
                arguments(Arrays.asList("000")),
                arguments(Arrays.asList("a")),
                arguments(Arrays.asList("0")),
                arguments(Arrays.asList("1")),
                arguments(Arrays.asList("01")),
                arguments(Arrays.asList("001")),
                arguments(Arrays.asList("00001")),
                arguments(Arrays.asList("0001 0000")),
                arguments(Arrays.asList("0 11")),
                arguments(Arrays.asList("00000001")),
                arguments(Arrays.asList("000000000001")),
                arguments(Arrays.asList(null, "0000")),
                arguments(Arrays.asList("0000", null)),
                arguments(Arrays.asList(null, null)),
                arguments(Arrays.asList("0001", "abcd")),
                arguments(Arrays.asList("abcd", "0001")),
                arguments(Arrays.asList("00011010", "00011010"))
        );
    }

}

package com.kimpflerze;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class ParserUtilitiesTests {

    // ****************
    // * Fuzzing Test *
    // ****************

    public static String[] variableNameFuzzer() {
        String[] dataTypes = {"int", "double", "String", "boolean", "List<>"};
        String[] letters = {"a", "b", "c", "d", "e", "f", "g"};

        Random random = new Random();

        int isArray = random.nextInt(2);
        int randomValueOne = random.nextInt(dataTypes.length);
        int randomValueTwo = random.nextInt(letters.length);

        String generatedString = dataTypes[randomValueOne];
        if(isArray == 0) {
            generatedString = generatedString + "[] ";
        }

        generatedString = generatedString + letters[randomValueTwo];

        String[] generatedStringAndExpectedResult = {generatedString, letters[randomValueTwo]};

        return generatedStringAndExpectedResult;
    }

    @Test
    public void fuzzedDetermineNameTest() {
        String[] fuzzedInformation = variableNameFuzzer();

        String input = fuzzedInformation[0];
        String expected = fuzzedInformation[1];

        String actual = ParserUtilities.determineName(input);

        System.out.println("FuzzedDetermineNameTest - Warning! May fail!");
        System.out.println("Expected: " + expected + ", Actual: " + actual);

        assertTrue(actual.equals(expected));
    }

    // ********************
    // * End Fuzzing Test *
    // ********************


    //At this point in the program, the strings that are passed to this function SHOULD
    //be only the left side of the '=' in a variable declaration.
    //i.e. int a = 0;
    //So only int a would be passed to this function.
    @DataPoints("determineNameDataPoints")
    public static String[][] determineNameDataPoints() {
        return new String[][] {
                new String[]{"int a", "a"},
                new String[]{"double b", "b"},
                new String[]{"String c", "c"},
                new String[]{"String[] d", "d"},
                new String[]{"e", "e"}
        };
    }

    @Theory
    public void determineNamesTest(@FromDataPoints("determineNameDataPoints") String[] data) {
        String result = ParserUtilities.determineName(data[0]);

        assertTrue(result.equals(data[1]));
    }

    //Contains function searches a string character by character to determine
    //if the string contains the desired search strings.
    @DataPoints("containsDataPoints")
    public static String[][] containsDataPoints() {
        return new String[][] {
                new String[]{"Zach", "a,c", "true"},
                new String[]{"Struggle", "b", "false"},
                new String[]{"Eeyore", "e", "true"},
                new String[]{"Luna", "d", "false"},
                new String[]{"Marida", "d,a", "true"}
        };
    }

    @Theory
    public void containsTest(@FromDataPoints("containsDataPoints") String[] data) {
        String[] searchStrings = data[1].split(",");

        boolean actualResult = ParserUtilities.contains(data[0], searchStrings);

        boolean expectedResult = false;
        if(data[2].equals("true")) {
            expectedResult = true;
        }

        assertTrue(actualResult == expectedResult);
    }

    @DataPoints("splitStringAtIndiciesDataPoints")
    public static String[][] splitStringAtIndiciesDataPoints() {
        return new String[][] {
                new String[]{"test", "0", "1"},
                new String[]{"test test test", "1,3", "3"}
        };
    }

    @Theory
    public void splitStringAtIndiciesTest(@FromDataPoints("splitStringAtIndiciesDataPoints") String[] data) {
        String sampleString = data[0];

        String indexString = data[1];
        String[] indexStringSplit = indexString.split(",");

        int[] indicies = new int[indexStringSplit.length];

        int counter = 0;
        for(String index : indexStringSplit) {
            indicies[counter] = Integer.parseInt(index);
            counter++;
        }

        String[] splitString = ParserUtilities.splitStringAtIndicies(indicies, sampleString);

        int expectedLength = Integer.parseInt(data[2]);

        assertTrue(splitString.length == expectedLength);
    }

}

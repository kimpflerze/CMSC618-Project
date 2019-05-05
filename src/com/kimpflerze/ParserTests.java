package com.kimpflerze;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(Theories.class)
public class ParserTests {

    @Test
    public void loadFileTest() {
        String fileName = "ExampleProgram.txt";
        int expectedLength = 39; // I counted prior to execution to determine valid lines.
        String[] results = Parser.loadFile(fileName);

        assertTrue(results.length == expectedLength);
    }

    @DataPoints("commentedLinesDataPoints")
    public static String[][] commentedLinesDataPoints() {
        String[][] trash = {
                new String[]{"// Comment", "SINGLE"},
                new String[]{"/* Start block", "BLOCK"},
                new String[]{"*/ End block", "BLOCKEND"},
                new String[]{"no comment", "NONE"},
                new String[]{"", "NONE"},
                new String[]{"/ trash trash trash trash", "NONE"},
        };

        return trash;
    }

    @Theory
    public void isLineCommentedTest(@FromDataPoints("commentedLinesDataPoints") String[] data) {

        System.out.println(" - isLineCommentedTest Assertion: Input=\"" + data[0] + "\", Expected=\"" + data[1] + "\"");

        Parser.COMMENT_TYPE result = Parser.isLineCommented(data[0]);
        String stringResult = "";

        switch(result) {
            case SINGLE:
                stringResult = "SINGLE";
                break;
            case BLOCK:
                stringResult = "BLOCK";
                break;
            case BLOCKEND:
                stringResult = "BLOCKEND";
                break;
            case NONE:
                stringResult = "NONE";
                break;
        }

        assertTrue(stringResult.equals(data[1]));
    }


    @DataPoints("classNameDataPoints")
    public static String[][] classNameDataPoints() {
        return new String[][] {
                new String[]{"class TestClass {", "1"},
                new String[]{"class TestClass{", "1"},
                new String[]{"classTestClass{", "0"},
                new String[]{"abstract class TestClass", "1"},
                new String[]{"private class TestClass", "1"},
                new String[]{"public class TestClass", "1"}
        };
    }

    @Theory
    public void findClassNamesTest(@FromDataPoints("classNameDataPoints") String[] data) {
        String[] dataToArray = {data[0]};
        String[] result = Parser.findClassAndSubClassNames(dataToArray);

        assertTrue(Integer.parseInt(data[1]) == result.length);
    }

    @DataPoints("findAssignmentsDataPoints")
    public static String[][] variableAssignmentsDataPoints() {
        return new String[][] {
                new String[]{"int ugh = 0;", "1"},
                new String[]{"boolean struggle = true;", "1"},
                new String[]{"ugh += 100000000000", "1"},
                new String[]{"chicken -= ugh", "1"},
                new String[]{"goingBaldOverStress *= 100000000000", "1"},
                new String[]{"progress /= 4", "1"},
                new String[]{"ugh %= 3", "1"},
                new String[]{"ugh ^= 100000000000", "1"},
                new String[]{"ugh == 100000000000", "0"},
                new String[]{"progress != true", "0"},
                new String[]{"for(int struggle = 1; struggle < progress; struggle++)", "0"},
                new String[]{"nada", "0"}
        };
    }

    @Theory
    public void findAssignmentsTest(@FromDataPoints("findAssignmentsDataPoints") String[] data) {

        System.out.println(" - variableAssignmentsTest Assertion: InputLength=\"1\", Expected=\"" + data[1] + "\"");

        String[] dataToArray = {data[0]};
        String[] result = Parser.findAssignments(dataToArray);

        assertTrue(Integer.parseInt(data[1]) == result.length);
    }

    //This is a really weak point within my program that was not thorough enough.
    //It does not cover all of the standard data types that come with Java.
    //It also incapable of detecting custom objects.
    //
    //Because of this, this test is EXPECTED TO FAIL!
    @DataPoints("determineTypeDataPoints")
    public static String[][] determineTypeDataPoints() {
        return new String[][] {
                new String[]{"boolean ugh = 0;", "boolean"},
                new String[]{"byte struggle = true;", "byte"},
                new String[]{"char struggle = 100000000000;", "char"},
                new String[]{"short struggle = 0;", "short"},
                new String[]{"int struggle = 100000000000;", "int"},
                new String[]{"long struggle = 4.0;", "long"},
                new String[]{"float struggle = 3.0;", "float"},
                new String[]{"double struggle = 100000000000;", "double"},
                new String[]{"String struggle = 100000000000;", "String"},
                new String[]{"Integer struggle = new Integer();", "Integer"},
                new String[]{"Double struggle = new Double();", "Double"},
                new String[]{"Boolean struggle = true;", "Boolean"},
                new String[]{"Foo struggle = new Foo();", "Foo"},
                new String[]{"nada", "No type found!"}
        };
    }

    @Theory
    public void determineTypeTest(@FromDataPoints("determineTypeDataPoints") String[] data) {
        String result = Parser.determineType(data[0]);

        assertTrue(result.equals(data[1]));
    }


    @DataPoints("determineOperatorIndiciesDataPoints")
    public static String[][] determineOperatorIndiciesDataPoints() {
        return new String[][] {
                new String[]{"a + b", "2"},
                new String[]{"c - d", "2"},
                new String[]{"e * f", "2"},
                new String[]{"g / h", "2"},
                new String[]{"i % j", "2"},
                new String[]{"a + b - c * d / e % f", "2,6,10,14,18"}
        };
    }

    @Theory
    public void determineOperatorIndiciesTest(@FromDataPoints("determineOperatorIndiciesDataPoints") String[] data) {
        String sampleString = data[0];

        String[] expectedIndiciesString = data[1].split(",");
        int[] expectedIndicies = new int[expectedIndiciesString.length];

        int counter = 0;
        for(String string : expectedIndiciesString) {
            expectedIndicies[counter] = Integer.parseInt(string);
            counter++;
        }

        int[] result = Parser.determineOperatorIndicies(sampleString);

        boolean allMatch = true;
        for(int i = 0; i < expectedIndicies.length; i++) {
            if(expectedIndicies[i] != result[i]) {
                allMatch = false;
            }
        }

        assertTrue(allMatch);
    }

    @DataPoints("determineAttributesDataPoints")
    public static String[][] determineAttributesDataPoints() {
        return new String[][] {
                new String[]{"[a, b]", "2"},
                new String[]{"[a, b", "2"},
                new String[]{"a, b]", "2"},
                new String[]{"a, b", "2"},
        };
    }

    @Theory
    public void determineAttributesTest(@FromDataPoints("determineAttributesDataPoints") String[] data) {
        String[] result = Parser.determineAttributes(data[0]);

        assertTrue(Integer.parseInt(data[1]) == result.length);
    }


    //I had to read the file before hand and manually count out the number of
    //unique variables. Hence, the hard-coded expected value.
    @Test
    public void extractVariablesTest() {
        String fileName = "ExampleProgram.txt";

        String[] originalLines = Parser.loadFile(fileName);

        String[] assignmentLines = Parser.findAssignments(originalLines);

        Variable[] extractedVariables = Parser.extractVariables(assignmentLines);

        int numExpectedVariables = 15;

        assertTrue(numExpectedVariables == extractedVariables.length);
    }

}

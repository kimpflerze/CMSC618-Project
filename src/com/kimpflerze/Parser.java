package com.kimpflerze;

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

public class Parser {

    private static Scanner scanner = new Scanner(System.in);

    private static String[] stringListToArray(List<String> lines) {
        String[] array = new String[lines.size()];
        for(int i = 0; i < lines.size(); i ++) {
            array[i] = lines.get(i);
        }
        return array;
    }

    private static int[] intListToArray(List<Integer> ints) {
        int[] array = new int[ints.size()];
        for(int i = 0; i < ints.size(); i ++) {
            array[i] = ints.get(i);
        }
        return array;
    }

    private static Variable[] variableListToArray(List<Variable> variables) {
        Variable[] array = new Variable[variables.size()];
        for(int i = 0; i < variables.size(); i ++) {
            array[i] = variables.get(i);
        }
        return array;
    }

    public static String[] loadFile(String path) {
        List<String> lines = null;
        try {
            File file = new File(path);
            Scanner fileReader = new Scanner(file);

            lines = new ArrayList<String>();

            int lineCounter = 0;
            while (fileReader.hasNextLine()) {
                ++lineCounter;
                String tempLine = fileReader.nextLine();
                if(!tempLine.isEmpty()) {
                    lines.add(tempLine);
                }
            }

            System.out.println("Total Lines In File: " + lineCounter);

            return stringListToArray(lines);
        } catch (Exception e) {
            System.out.println("File not found at: " + path);
        }
        return stringListToArray(lines);
    }

    private static boolean contains(String string, String[] searchStrings) {
        boolean doesContain = false;
        for(String searchString : searchStrings) {
            if(string.contains(searchString)) {
                doesContain = true;
            }
        }
        return doesContain;
    }


    public static String[] findAssignments(String[] lines) {
        List<String> assignmentLines = new ArrayList<String>();
        for(int i = 0; i < lines.length; i++) {
            String[] assignmentIndicators = {"=", "+=", "-=", "*=", "/=", "%=", "^="};

            if (contains(lines[i], assignmentIndicators)) {
                String tempLine = lines[i];
                String[] falseAssignmentIndicators = {"==", "!=", "for("};
                if (contains(tempLine, falseAssignmentIndicators)) {
                    continue;
                }
                assignmentLines.add(tempLine.trim());
            }
        }
        return stringListToArray(assignmentLines);
    }

    private static String[] trimArray(String[] array) {
        String[] trimmedArray = array;
        for(int i = 0; i < trimmedArray.length; i++) {
            trimmedArray[i] = trimmedArray[i].trim();
        }
        return trimmedArray;
    }

    private static String determineType(String definition) {
        //This list of datatypes should still be expanded!
        String[] dataTypes = {"boolean", "byte", "char", "short", "int", "long", "float", "double",
        "String", "Integer", "List", "ArrayList"};

        for(String type : dataTypes) {
            if(definition.contains(type)) {
                return type;
            }
        }

        return "No type found!";
    }

    private static String determineName(String definition) {
        String[] whiteSpaceSplit = definition.split(" ");

        String name = whiteSpaceSplit[whiteSpaceSplit.length-1];

        if(name.contains("[")) {
            int openBracketIndex = -1;
            openBracketIndex = name.indexOf("[");
            name = name.substring(0, openBracketIndex);
        }

        return name;
    }

    private static String[] splitStringAtIndicies(int[] indexList, String value) {
        List<String> operatorSplit = new ArrayList<String>();

        String valueSubstring = "";
        int startIndex = 0;
        for(int i = 0; i < indexList.length; i++) {
            valueSubstring = value.substring(startIndex, indexList[i]).trim();
            if(!valueSubstring.isEmpty()) {
                operatorSplit.add(valueSubstring);
                startIndex = indexList[i] + 1;
            }

        }
        valueSubstring = value.substring(startIndex, value.length()-1);
        operatorSplit.add(valueSubstring);

        return stringListToArray(operatorSplit);
    }

    private static int[] determineOperatorIndicies(String[] array) {
        String[] operators = {"+", "-", "*", "/", "%", "."};

        List<Integer> indexList = new ArrayList<Integer>();

        for(int i = 0; i < array.length; i++) {
            for (String operator : operators) {
                if(array[i].contains(operator)) {
                    indexList.add(array[i].indexOf(operator));
                }
            }
        }

        //Convert list to array
        return intListToArray(indexList);
    }

    private static int[] determineOperatorIndicies(String string) {
        String[] operators = {"+", "-", "*", "/", "%", "."};

        List<Integer> indexList = new ArrayList<Integer>();

        for (String operator : operators) {
            if(string.contains(operator)) {
                indexList.add(string.indexOf(operator));
            }
        }

        //Convert list to array
        return intListToArray(indexList);
    }

    private static String[] determineRelationships(String value) {
        //First, split the value on white space
        String[] whiteSpaceSplit = value.split(" ");

        //Then, determine if there are any operators within this assignment's value.
        //It may have only one element, then there are two cases here,
        //  1. There is only one variable in this value.
        //  2. There are no spaces between the elements, therefore we still need to find where the operators are.
        int[] operatorIndicies = {};
        if(whiteSpaceSplit.length > 1) {
            //There is clearly more than one relationship to extract
            //Filter through the list and check of any are operators
            operatorIndicies = determineOperatorIndicies(whiteSpaceSplit);
        }
        else if(whiteSpaceSplit.length == 1){
            operatorIndicies = determineOperatorIndicies(whiteSpaceSplit[0]);
        }
        else {
            System.out.println("Cant determine assignment's value/relationship!");
        }

        return splitStringAtIndicies(operatorIndicies, value);
    }

    private static String[] trySplitOn(String string, String[] splitStrings) {
        List<String> splitStringList = new ArrayList<String>();
        boolean succeeded = false;
        for(String splitString : splitStrings) {
            int possibleIndex = string.indexOf(splitString);
            if(possibleIndex > -1) {
                splitStringList.add(string.substring(0, possibleIndex));
                splitStringList.add(string.substring(possibleIndex + 1, string.length() - 1));
                succeeded = true;
            }
        }

        if(!succeeded) {
            splitStringList.add(string);
        }

        return stringListToArray(splitStringList);

    }

    private static boolean doesVariableExist(Variable variable, List<Variable> existingVariables) {
        for(Variable existingVariable : existingVariables) {
            if(existingVariable.name.equals(variable.name)) {
                return true;
            }
        }
        return false;
    }

    public static Variable[] extractVariables(String[] assignmentLines) {
        List<Variable> extractedVariables = new ArrayList<Variable>();

        for(int lineIndex = 0; lineIndex < assignmentLines.length; lineIndex++) {
            //Split on equals sign!
            String[] assignmentIndicators = {"+=", "-=", "*=", "/=", "%=", "^=", "="};
            String[] equalsSignSplitArray = trySplitOn(assignmentLines[lineIndex], assignmentIndicators);
            String definition = equalsSignSplitArray[0];
            try {
                String value = equalsSignSplitArray[1].trim();

                //Split on spaces!
                //Handle the definition side
                String type = determineType(definition);
                String name = determineName(definition);

                Variable newVariable = new Variable(type, name, value);

                if(doesVariableExist(newVariable, extractedVariables) == false) {
                    extractedVariables.add(newVariable);
                }

                /*
                Main.println("New Variable:");
                Main.println("  Definition: " + definition);
                Main.println("  Value: " + value);
                Main.println("      Type: " + type);
                Main.println("      Name: " + name);
                */

                //Handle the value side
//            Main.println("      Relationships:");
//            String[] relationships = determineRelationships(value);

//            Main.printStringArray(relationships);

                Main.println("");
            } catch(IndexOutOfBoundsException e) {
                extractedVariables.add(new Variable("No Type Found!", definition));
            }


        }

        return variableListToArray(extractedVariables);
    }

    public static Variable[] resolveRelationships(Variable[] extractedVariables) {
        //For each variable, we need to evaluate its "value" string for mentioned variables.
        //  Essentially what we need to do is look at each "value" string.
        //  Check if any of the variable names exist within those strings!
        //      If there are whitespace characters in between, we can find the variables easily!
        //      If there is no whitespace, we will need to essentially run extractVariables() again on this string
        //          If I do extractVariables() from this line, it will return Variable objects for comparison which is good.
        List<Variable> resolvedVariables = new ArrayList<Variable>();
        for(Variable variable : extractedVariables) {
            String[] value = {variable.value};
            Variable[] extractedRelations = extractVariables(value);

            Variable tempVariable = variable;
            List<Variable> tempRelationships = new ArrayList<Variable>();

            for(Variable relation : extractedRelations) {
                if(!tempRelationships.contains(relation)) {
                    tempRelationships.add(relation);
                }
            }

            tempVariable.relationships = variableListToArray(tempRelationships);
        }
        return variableListToArray(resolvedVariables);
    }

}

package com.kimpflerze;

import java.util.*;
import java.io.*;
import java.util.regex.PatternSyntaxException;

public class Parser {

    private static Scanner scanner = new Scanner(System.in);
    private static List<String> variableNames = new ArrayList<String>();

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
        String[] whiteSpaceSplit = definition.split(" ");

        //This list of datatypes should still be expanded!
        String[] dataTypes = {"boolean", "byte", "char", "short", "int", "long", "float", "double",
        "String", "Integer", "List", "ArrayList"};

        for(String type : dataTypes) {
            for(String string : whiteSpaceSplit) {
                if (string.contains(type)) {
                    return type;
                }
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
        Arrays.sort(indexList);

        if(indexList.length > 0) {
            if(indexList[0] == 0) {
                String[] array = {value};
                return array;
            }
        }

        String valueSubstring = "";
        int startIndex = 0;
        for(int i = 0; i < indexList.length; i++) {
            valueSubstring = value.substring(startIndex, indexList[i]).trim();
            Main.println("ValueSubstring: " + valueSubstring);
            if(!valueSubstring.isEmpty()) {
                operatorSplit.add(valueSubstring);
                startIndex = indexList[i] + 1;
            }

        }
        valueSubstring = value.substring(startIndex, value.length()).trim();
        operatorSplit.add(valueSubstring);

        return stringListToArray(operatorSplit);
    }

    private static int[] determineOperatorIndicies(String string) {
        char[] operators = {'+', '-', '*', '/', '%'};

        List<Integer> indexList = new ArrayList<Integer>();
        Main.println("Current String: " + string);
        Main.println("indexList Size at Start: " + indexList.size());
        for (char oc : operators) {
            for(int i = 0; i < string.length(); i++) {
                if(string.charAt(i) == oc) {
                    indexList.add(i);
                }
            }
            return intListToArray(indexList);
        }

        Main.println("indexList Size at End: " + indexList.size() + "\n");

        //Convert list to array
        return intListToArray(indexList);
    }

    private static String[] determineRelationships(String value) {
        //First, split the value on white space
        //String[] whiteSpaceSplit = value.split(" ");

        String tempValue = value;
        //String tempValue = value.replace(" ", "");

        //Then, determine if there are any operators within this assignment's value.
        //It may have only one element, then there are two cases here,
        //  1. There is only one variable in this value.
        //  2. There are no spaces between the elements, therefore we still need to find where the operators are.
        int[] operatorIndicies = {};
        //OPERATOR INDICIES ISNT RIGHT HERE! THERE ARE MORE THAN ONE STRING BEING SEARCHED, THEREFORE I NEED A 2D MATRIX OF OPERATOR INDICIES!
        /*
        if(whiteSpaceSplit.length > 1) {
            //There is clearly more than one relationship to extract
            //Filter through the list and check of any are operators
            operatorIndicies = determineOperatorIndicies(whiteSpaceSplit);
            Main.println("WhiteSpaceSplit.length > 1");
            Main.printStringArray(whiteSpaceSplit);
            Main.println("OperatorIndicies:");
            Main.printIntArray(operatorIndicies);
        }
        else if(whiteSpaceSplit.length == 1){
        */
            operatorIndicies = determineOperatorIndicies(tempValue);
            Main.println("TempValue: " + tempValue);
            Main.println("OperatorIndicies: s=" + operatorIndicies.length);
            Main.printIntArray(operatorIndicies);
            /*
        }
        else {
            System.out.println("Cant determine assignment's value/relationship!");
        }
        */

        return splitStringAtIndicies(operatorIndicies, value);
    }

    private static String [] determineAttributes (String value) {
        String[] commasplit = value.split(",");
        return commasplit;
    }

    private static String findbrackets (String value) {
        return (value.substring(s.indexOf("(") + 1, s.indexOf(")")));
    }

    private static String[] determineRelationships1(String value) {
        //First, split the value on white space
        String[] whiteSpaceSplit = value.split(" ");
        String tempValue = value;
        int indexOfnew = ArrayUtils.indexOf(whiteSpaceSplit, "new");
        if (indexOfnew > -1 ) {
            Main.println("its a constructor");
            String[] withoutnew = Arrays.copyOfRange(whiteSpaceSplit, 1, whiteSpaceSplit.length);
            return(determineAttributes(findbrackets(Arrays.toString(withoutnew))));
        } else if (value.indexOf('(') != -1 && value.indexOf(')') != -1) {
            Main.println("its a function");
            return(determineAttributes(findbrackets(value)));
        } else {
        //String tempValue = value.replace(" ", "");

        //Then, determine if there are any operators within this assignment's value.
        //It may have only one element, then there are two cases here,
        //  1. There is only one variable in this value.
        //  2. There are no spaces between the elements, therefore we still need to find where the operators are.
            int[] operatorIndicies = {};
        //OPERATOR INDICIES ISNT RIGHT HERE! THERE ARE MORE THAN ONE STRING BEING SEARCHED, THEREFORE I NEED A 2D MATRIX OF OPERATOR INDICIES!
        /*
        if(whiteSpaceSplit.length > 1) {
            //There is clearly more than one relationship to extract
            //Filter through the list and check of any are operators
            operatorIndicies = determineOperatorIndicies(whiteSpaceSplit);
            Main.println("WhiteSpaceSplit.length > 1");
            Main.printStringArray(whiteSpaceSplit);
            Main.println("OperatorIndicies:");
            Main.printIntArray(operatorIndicies);
        }
        else if(whiteSpaceSplit.length == 1){
        */
            operatorIndicies = determineOperatorIndicies(tempValue);
            Main.println("TempValue: " + tempValue);
            Main.println("OperatorIndicies: s=" + operatorIndicies.length);
            Main.printIntArray(operatorIndicies);
            /*
        }
        else {
            System.out.println("Cant determine assignment's value/relationship!");
        }
        */

            return splitStringAtIndicies(operatorIndicies, value);
        }
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


    public static Variable[] extractRelationships(Variable variable) {
        List<Variable> extractedVariables = new ArrayList<Variable>();

        String[] determinedRelationships = determineRelationships(variable.value);
        Main.println("resolveRelationships - determinedRelationships: ");
        Main.printStringArray(determinedRelationships);

        for(String relation : determinedRelationships) {
            for(String existingVariableName : variableNames) {
                if (relation.trim().equals(existingVariableName)) {
                    Variable tempVariable = new Variable(variable.type, relation);
                    extractedVariables.add(tempVariable);
                }
            }
        }

        return variableListToArray(extractedVariables);
    }


    /*
     * FINALLY FOUND MY ISSUE! TO make this way easier, assume assignments are properly spaced ie "a = b + 1;" and not "a=b+1;
     * This makes it easier so our program can comprehend negatives instead of assuming they are variables or something else.
     *
     * So, if I assume values are spaced properly, I can easily just split on white space, make sure each string doesnt contain any operators,
     * and if not, just look for variable names that match!
     */

    public static Variable[] resolveRelationships(Variable[] extractedVariables) {
        //For each variable, we need to evaluate its "value" string for mentioned variables.
        //  Essentially what we need to do is look at each "value" string.
        //  Check if any of the variable names exist within those strings!
        //      If there are whitespace characters in between, we can find the variables easily!
        //      If there is no whitespace, we will need to essentially run extractVariables() again on this string
        //          If I do extractVariables() from this line, it will return Variable objects for comparison which is good.
        List<Variable> resolvedVariables = new ArrayList<Variable>();

        //Store all variable names in the global list definited above...
        for(Variable variable : extractedVariables) {
            variableNames.add(variable.name);
        }

        //OLD STUFF

        //For every variable that I have extracted...
        for(Variable variable : extractedVariables) {
            //Create a String array to hold a single variable's value String, just for the sake of reusing a function...
            String value = variable.value;
            //Extract the variables from that String
            //Variable[] extractedRelations = extractRelationships(value);
            /*
            String[] determinedRelationships = determineRelationships(value);
            Main.println("resolveRelationships - determinedRelationships: ");
            Main.printStringArray(determinedRelationships);
            */

            Variable[] extractedRelations = extractRelationships(variable);
            Main.println("resolveRelationships - extractedRelations: ");
            for(Variable relation : extractedRelations) {
                Main.println("relation Name: " + relation.name);
            }

            Main.println("");

            //Make a copy of the variable being inspected...
            Variable tempVariable = variable;
            //Make a list to hold the created relationships
            List<Variable> tempRelationships = new ArrayList<Variable>();

            //Loop through the extracted relations  and add them to the tempRelationships array
            for(Variable relation : extractedRelations) {
                if(tempRelationships.contains(relation) == false) {
                    tempRelationships.add(relation);
                }
            }

            //Assign the tempRelationshipsArray to the copied original variable tempVariable.
            tempVariable.relationships = variableListToArray(tempRelationships);

            //Add the new resolved variable, with relationships defined as Variable objects, to the resolvedVariables list!
            resolvedVariables.add(tempVariable);
        }
        //Return the list once all of the looping is done.
        //This means
        return variableListToArray(resolvedVariables);
    }

}

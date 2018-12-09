package com.kimpflerze;

import java.util.*;
import java.util.Arrays;
import java.io.*;
import java.util.regex.PatternSyntaxException;

public class Parser {

    private static Scanner scanner = new Scanner(System.in);
    private static List<String> variableNames = new ArrayList<String>();

    private static String[] stringListToArray(List<String> lines) {
        String[] array = new String[lines.size()];
        for(int i = 0; i < lines.size(); i++) {
            array[i] = lines.get(i);
        }
        return array;
    }

    private static int[] intListToArray(List<Integer> ints) {
        int[] array = new int[ints.size()];
        for(int i = 0; i < ints.size(); i++) {
            array[i] = ints.get(i);
        }
        return array;
    }

    public static Variable[] variableListToArray(List<Variable> variables) {
        Variable[] array = new Variable[variables.size()];
        for(int i = 0; i < variables.size(); i++) {
            array[i] = variables.get(i);
        }
        return array;
    }

    private enum COMMENT_TYPE {
        SINGLE,
        BLOCK,
        BLOCKEND,
        NONE
    }

    private static COMMENT_TYPE isLineCommented(String line) {
        //Check to see if the line is commented out
        String trimmedLine = line.trim();

        if(trimmedLine.length() >= 2) {
            String trimmedLineSubstring = trimmedLine.substring(0, 2);
            Main.println("TrimmedLine: " + trimmedLine);
            Main.println("TrimmedLineSubstring: " + trimmedLineSubstring);
            if (trimmedLineSubstring.equals("//")) {
                Main.println("Found a // line!: " + trimmedLine);
                return COMMENT_TYPE.SINGLE;
            } else if (trimmedLineSubstring.contains("/*")) {
                Main.println("Found a /* line!: " + trimmedLine);
                return COMMENT_TYPE.BLOCK;
            } else if (trimmedLineSubstring.contains("*/")) {
                Main.println("Found a */ line!: " + trimmedLine);
                return COMMENT_TYPE.BLOCKEND;
            } else {
                //Do nothing!
                Main.println("Found a normal line!: " + trimmedLine);
                return COMMENT_TYPE.NONE;
            }
        } else {
            Main.println(" string is huge " + trimmedLine);
        }
        return COMMENT_TYPE.NONE;
    }

    public static String[] loadFile(String path) {
        List<String> lines = null;
        try {
            Main.println("File Name: " + path);

            File file = new File(path);
            Scanner fileReader = new Scanner(file);

            lines = new ArrayList<String>();

            int lineCounter = 0;
            int flag = 0;
            while (fileReader.hasNextLine()) {
                ++lineCounter;
                String tempLine = fileReader.nextLine().trim();

                if(tempLine.isEmpty()) {
                    Main.println("Empty line!" + tempLine);
                    continue;
                }

                COMMENT_TYPE commentType = isLineCommented(tempLine);
                if(commentType == COMMENT_TYPE.SINGLE) {
                    Main.println("Single Comment Line: " + tempLine);
                    continue;
                }
                if(commentType == COMMENT_TYPE.BLOCK) {
                    Main.println("Start Block Comment Line: " + tempLine);
                    flag = 1;
                    continue;
                }
                if(commentType == COMMENT_TYPE.BLOCKEND) {
                    Main.println("End Block Comment Line: " + tempLine);
                    flag = 0;
                    continue;
                }

                if(commentType == COMMENT_TYPE.NONE && flag == 0) {
                    Main.println("Regular Line: " + tempLine);
                    lines.add(tempLine);
                }
            }

            System.out.println("Total Lines In File: " + lineCounter);

            return stringListToArray(lines);
        } catch (Exception e) {
            Main.println("Error: " + e.getMessage());
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

    public static String[] findClassAndSubClassNames(String[] lines) {
        List<String> classNamesList = new ArrayList<String>();

        for(String line : lines) {
            if(line.contains("class")) {
                String[] whiteSpaceSplit = line.split(" ");
                for (int i = 0; i < whiteSpaceSplit.length; i++) {
                    String tempString = whiteSpaceSplit[i].trim();
                    if (tempString.equals("class")) {
                        try {
                            String className = whiteSpaceSplit[i + 1];
                            if(className.contains("{")) {
                                int openBracketIndex = className.indexOf("{");
                                className = className.substring(0, openBracketIndex).trim();
                            }
                            Main.println("Found class named " + className);
                            classNamesList.add(className);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds in function findClassAndSubClassNames!");
                        }
                    }
                }
            }
        }

        return stringListToArray(classNamesList);
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

        String name = whiteSpaceSplit[whiteSpaceSplit.length - 1];

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
        String tempValue = value;
        //String tempValue = value.replace(" ", "");

        int[] operatorIndicies = {};

        operatorIndicies = determineOperatorIndicies(tempValue);
        Main.println("TempValue: " + tempValue);
        Main.println("OperatorIndicies: s=" + operatorIndicies.length);
        Main.printIntArray(operatorIndicies);

        return splitStringAtIndicies(operatorIndicies, value);
    }

    private static String [] determineAttributes (String value) {
        String bracklessValue = value;

        if(value.contains("[")) {
            int openBracketIndex = -1;
            openBracketIndex = value.indexOf("[");
            bracklessValue = value.substring(0, openBracketIndex);
        }
        Main.println("Test3");
        Main.println("Test 3 Value: " + value);
        String[] commasplit = bracklessValue.split(",");

        for(int i = 0; i < commasplit.length; i++) {
            commasplit[i] = commasplit[i].trim();
        }

        return commasplit;
    }

    private static String findbrackets (String value) {
        Main.println("Test1");
        Main.println("Test 1 Value: " + value);
        return (value.substring(value.indexOf("(") + 1, value.indexOf(")")));
    }

    private static String[] determineRelationships1(String value) {
        //First, split the value on white space
        Main.println("Searching relationships for    " + value);
        String[] whiteSpaceSplit = value.split(" ");
        String tempValue = value;


        int indexOfnew = Arrays.asList(whiteSpaceSplit).indexOf("new");

        Main.println("Test2");
        // will resume the work on this
        if (indexOfnew > -1 && value.indexOf('(') != -1 && value.indexOf(')') != -1) {
            Main.println("its a constructor");
            String[] withoutnew = Arrays.copyOfRange(whiteSpaceSplit, 1, whiteSpaceSplit.length);
            String [] temp = determineAttributes(findbrackets(Arrays.toString(withoutnew)));
            List<String> toreturn = new ArrayList<String>();
            for (String s : temp) {
                int [] tempint = determineOperatorIndicies(s);
                for(String p : splitStringAtIndicies(tempint, s)) {
                    toreturn.add(p.trim());
                }
            }
            return stringListToArray(toreturn);
//            return(determineAttributes(findbrackets(Arrays.toString(withoutnew))));
        } else if (value.indexOf('(') != -1 && value.indexOf(')') != -1 && value.indexOf(',') != -1) {
            Main.println("its a function");
            Main.println(value);
            String [] temp = determineAttributes(findbrackets(value));
            List<String> toreturn = new ArrayList<String>();
            for (String s : temp) {
                int [] tempint = determineOperatorIndicies(s);
                for(String p : splitStringAtIndicies(tempint, s)) {
                    toreturn.add(p.trim());
                }
            }
            return stringListToArray(toreturn);

        } else {

            int[] operatorIndicies = {};

            operatorIndicies = determineOperatorIndicies(tempValue);
            Main.println("TempValue: " + tempValue);
            Main.println("OperatorIndicies: s=" + operatorIndicies.length);
            Main.printIntArray(operatorIndicies);


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
    // function to find the taint a variable;

    public static void taintVariable(Variable [] allVariables, String vname) {
        for(int i = 0; i < allVariables.length; i++) {
            if (allVariables[i].name.equals(vname)) {
                allVariables[i].setTainted(true);
                Main.println("tainting variable: " + vname);
            }
        }
    }

    // function to find taint spread
    public static void taintSpread(Variable [] allVariables, String vname, int hop) {
        if(hop == 0) {
            taintVariable(allVariables,vname);
            return;
        } else if(hop > 0) {
            taintVariable(allVariables,vname);
            Main.println("hop no: " + Integer.toString(hop));
            for(int i = 0; i < allVariables.length; i++) {
                for(int j = 0; j < allVariables[i].relationships.length; j++) {
                    if(allVariables[i].relationships[j].name.equals(vname)) {
                        taintSpread(allVariables,allVariables[i].getName(), hop-1);
                    }
                }

            }
        } else {
            return;
        }

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
                String type = determineType(definition).trim();
                String name = determineName(definition).trim();

                //Slimy patch of some hard to remove bugs... Forgive me!
                if(name.contains("{") || name.indexOf('"') > -1) {
                    Main.println(name.trim());
                    continue;
                }
                //End of slimy patch

                Variable newVariable = new Variable(type, name, value);

                if(doesVariableExist(newVariable, extractedVariables) == false) {
                    extractedVariables.add(newVariable);
                } else {
                    for(int i = 0; i < extractedVariables.size(); i++) {
                        //for (Variable var : extractedVariables ) {
                        if (extractedVariables.get(i).name.equals(newVariable.name)) {
                            Variable temp = extractedVariables.get(i);
                            temp.addValue(value);
                            extractedVariables.set(i, temp);
                            Main.println("extracted variable " + temp.name + ", new value "+ temp.value.toString());
                        }
                    }
                }
                Main.println("");
            } catch(IndexOutOfBoundsException e) {
                extractedVariables.add(new Variable("No Type Found!", definition));
            }

        }

        return variableListToArray(extractedVariables);
    }


    public static Variable[] extractRelationships(Variable variable) {
        List<Variable> extractedVariables = new ArrayList<Variable>();
        Main.println("in extract relationships with " + variable.name + " looking for  " + variable.value);
        Main.println("Length of variable's Value list: " + variable.value.size());
        for(int i = 0; i < variable.value.size(); i++) {
            String[] determinedRelationships = determineRelationships1(variable.value.get(i));

            Main.println("resolveRelationships - determinedRelationships: ");
            Main.printStringArray(determinedRelationships);

            for (String relation : determinedRelationships) {
                for (String existingVariableName : variableNames) {
                    if (relation.trim().equals(existingVariableName)) {
                        Main.println("");
                        Variable tempVariable = new Variable(variable.type, relation);
                        extractedVariables.add(tempVariable);
                    }
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

        List<Variable> resolvedVariables = new ArrayList<Variable>();

        //Store all variable names in the global list definited above...
        for(Variable variable : extractedVariables) {
            variableNames.add(variable.name);
        }



        //For every variable that I have extracted...
        for(Variable variable : extractedVariables) {

            //Create a String array to hold a single variable's value String, just for the sake of reusing a function...



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

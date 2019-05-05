package com.kimpflerze;

import java.util.*;
import java.io.*;

public class Parser {

    private static Scanner scanner = new Scanner(System.in);
    private static List<String> variableNames = new ArrayList<String>();

    //Simple, dont test.
    public static void reverseTaint(Variable[] variables){
        for (Variable v : variables){
            v.setTainted(false);
        }
    }

    //Not important, no functionality. Don't test.
    //
    //This is just a function to print out results from the other functions
    //for viewing purposes.
    public static int[] analysis(Variable[] extractedVariables) {
        int total_indegree = 0;
        int total_outdegree = 0;
        int total_tainted = 0;
        int tempcount = 0;
        Main.println("\n\nIn/Out Degree of Individual Variables: ");
        for (Variable v : extractedVariables) {
            total_indegree = total_indegree + v.relationships.length;
            v.setInDegree(total_indegree);
            if(v.tainted == true) {
                total_tainted = total_tainted + 1;
            }
            for (Variable s: extractedVariables) {
                for (Variable t: s.getRelationships()) {
                    if(t.name.equals(v.name)) {
                        tempcount = tempcount+1;
                    }
                }
            }
            v.setOutDegree(tempcount);
            total_outdegree = total_outdegree + tempcount;

            Main.println("\tVariable " + v.getName() + " indegree is " + String.valueOf(v.relationships.length));
            Main.println("\tVariable " + v.getName() + " outdegree is " + String.valueOf(tempcount));
            tempcount = 0;
        }
        int [] toreturn = new int[3];
        toreturn[0] = total_indegree;
        toreturn[1] = total_outdegree;
        toreturn[2] = total_tainted;
        return toreturn;

    }

    public enum COMMENT_TYPE {
        SINGLE,
        BLOCK,
        BLOCKEND,
        NONE
    }

    //Should test - Tested
    public static COMMENT_TYPE isLineCommented(String line) {
        //Check to see if the line is commented out
        String trimmedLine = line.trim();

        if(trimmedLine.length() >= 2) {
            String trimmedLineSubstring = trimmedLine.substring(0, 2);
            //Main.println("TrimmedLine: " + trimmedLine);
            //Main.println("TrimmedLineSubstring: " + trimmedLineSubstring);
            if (trimmedLineSubstring.equals("//")) {
                Main.println("Comment: " + trimmedLine);
                return COMMENT_TYPE.SINGLE;
            } else if (trimmedLineSubstring.contains("/*")) {
                Main.println("Start Block: " + trimmedLine);
                return COMMENT_TYPE.BLOCK;
            } else if (trimmedLineSubstring.contains("*/")) {
                Main.println("End Block: " + trimmedLine);
                return COMMENT_TYPE.BLOCKEND;
            } else {
                //Do nothing!
                //Main.println(trimmedLine);
                return COMMENT_TYPE.NONE;
            }
        } else {
            //Main.println(" string is huge " + trimmedLine);
        }
        return COMMENT_TYPE.NONE;
    }

    //Test this - Tested
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
                    //Main.println("Empty line!" + tempLine);
                    continue;
                }

                COMMENT_TYPE commentType = isLineCommented(tempLine);
                if(commentType == COMMENT_TYPE.SINGLE) {
                    //Main.println("Single Comment Line: " + tempLine);
                    continue;
                }
                if(commentType == COMMENT_TYPE.BLOCK) {
                    //Main.println("Start Block Comment Line: " + tempLine);
                    flag = 1;
                    continue;
                }
                if(commentType == COMMENT_TYPE.BLOCKEND) {
                    //Main.println("End Block Comment Line: " + tempLine);
                    flag = 0;
                    continue;
                }

                if(commentType == COMMENT_TYPE.NONE && flag == 0) {
                    //Main.println("Regular Line: " + tempLine);
                    lines.add(tempLine);
                }
            }

            System.out.println("Total Lines In File: " + lineCounter);

            return ParserUtilities.stringListToArray(lines);
        } catch (Exception e) {
            Main.println("Error: " + e.getMessage());
            System.out.println("File not found at: " + path);
        }
        return ParserUtilities.stringListToArray(lines);
    }

    //Should test - Tested
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
                            //Main.println("Found class named " + className);
                            classNamesList.add(className);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Index out of bounds in function findClassAndSubClassNames!");
                        }
                    }
                }
            }
        }

        return ParserUtilities.stringListToArray(classNamesList);
    }

    //Should test - Tested
    public static String[] findAssignments(String[] lines) {
        List<String> assignmentLines = new ArrayList<String>();
        for(int i = 0; i < lines.length; i++) {
            String[] assignmentIndicators = {"=", "+=", "-=", "*=", "/=", "%=", "^="};

            if (ParserUtilities.contains(lines[i], assignmentIndicators)) {
                String tempLine = lines[i];
                String[] falseAssignmentIndicators = {"==", "!=", "for("};

                if (ParserUtilities.contains(tempLine, falseAssignmentIndicators)) {
                    continue;
                }

                assignmentLines.add(tempLine.trim());
            }
        }
        return ParserUtilities.stringListToArray(assignmentLines);
    }

    //Should test - Tested
    public static String determineType(String definition) {
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

    //Should test - Tested
    public static int[] determineOperatorIndicies(String string) {
        char[] operators = {'+', '-', '*', '/', '%'};

        List<Integer> indexList = new ArrayList<Integer>();
        //Main.println("Current String: " + string);
        //Main.println("indexList Size at Start: " + indexList.size());
        for (char oc : operators) {
            for(int i = 0; i < string.length(); i++) {
                if(string.charAt(i) == oc) {
                    indexList.add(i);
                }
            }
        }

        //Main.println("indexList Size at End: " + indexList.size() + "\n");

        //Convert list to array
        return ParserUtilities.intListToArray(indexList);
    }

    //Not used, don't test.
    private static String[] determineRelationships(String value) {
        String tempValue = value;
        //String tempValue = value.replace(" ", "");

        int[] operatorIndicies = {};

        operatorIndicies = determineOperatorIndicies(tempValue);
        //Main.println("TempValue: " + tempValue);
        //Main.println("OperatorIndicies: s=" + operatorIndicies.length);
        //Main.printIntArray(operatorIndicies);

        return ParserUtilities.splitStringAtIndicies(operatorIndicies, value);
    }

    //Should test - Tested
    public static String [] determineAttributes (String value) {
        String bracklessValue = value;

        if(value.contains("[")) {
            int openBracketIndex = -1;
            openBracketIndex = value.indexOf("[");
            bracklessValue = value.substring(openBracketIndex + 1, value.length());
        }
        if(bracklessValue.contains("]")) {
            int closeBracketIndex = -1;
            closeBracketIndex = value.indexOf("]");
            bracklessValue = value.substring(0, closeBracketIndex - 1);
        }
        //Main.println("Test3");
        //Main.println("Test 3 Value: " + value);
        String[] commasplit = bracklessValue.split(",");

        for(int i = 0; i < commasplit.length; i++) {
            commasplit[i] = commasplit[i].trim();
        }

        return commasplit;
    }

    //Should test - Really hard to do. Skipped.
    private static String[] determineRelationships1(String value) {
        //First, split the value on white space
        //Main.println("Searching relationships for    " + value);
        String[] whiteSpaceSplit = value.split(" ");
        String tempValue = value;


        int indexOfnew = Arrays.asList(whiteSpaceSplit).indexOf("new");

        //Main.println("Test2");
        // will resume the work on this
        if (indexOfnew > -1 && value.indexOf('(') != -1 && value.indexOf(')') != -1) {
            //Main.println("its a constructor");
            String[] withoutnew = Arrays.copyOfRange(whiteSpaceSplit, 1, whiteSpaceSplit.length);
            String [] temp = determineAttributes(ParserUtilities.findbrackets(Arrays.toString(withoutnew)));
            List<String> toreturn = new ArrayList<String>();
            for (String s : temp) {
                int [] tempint = determineOperatorIndicies(s);
                for(String p : ParserUtilities.splitStringAtIndicies(tempint, s)) {
                    toreturn.add(p.trim());
                }
            }
            return ParserUtilities.removeDuplicate(ParserUtilities.stringListToArray(toreturn));
//            return(determineAttributes(findbrackets(Arrays.toString(withoutnew))));
        } else if (value.indexOf('(') != -1 && value.indexOf(')') != -1 && value.indexOf(',') != -1) {
            //Main.println("its a function");
            //Main.println(value);
            String [] temp = determineAttributes(ParserUtilities.findbrackets(value));
            List<String> toreturn = new ArrayList<String>();
            for (String s : temp) {
                int [] tempint = determineOperatorIndicies(s);
                for(String p : ParserUtilities.splitStringAtIndicies(tempint, s)) {
                    toreturn.add(p.trim());
                }
            }
            return ParserUtilities.removeDuplicate(ParserUtilities.stringListToArray(toreturn));

        } else {

            int[] operatorIndicies = {};

            operatorIndicies = determineOperatorIndicies(tempValue);
            //Main.println("TempValue: " + tempValue);
            //Main.println("OperatorIndicies: s=" + operatorIndicies.length);
            //Main.printIntArray(operatorIndicies);


            return ParserUtilities.splitStringAtIndicies(operatorIndicies, value);
        }
    }

    //Should test - Tested
    public static Variable[] extractVariables(String[] assignmentLines) {
        List<Variable> extractedVariables = new ArrayList<Variable>();

        for(int lineIndex = 0; lineIndex < assignmentLines.length; lineIndex++) {
            //Split on equals sign!
            String[] assignmentIndicators = {"+=", "-=", "*=", "/=", "%=", "^=", "="};

            String[] equalsSignSplitArray = ParserUtilities.trySplitOn(assignmentLines[lineIndex], assignmentIndicators);
            String definition = equalsSignSplitArray[0];
            try {
                String value = equalsSignSplitArray[1].trim();

                //Split on spaces!
                //Handle the definition side
                String type = determineType(definition).trim();
                String name = ParserUtilities.determineName(definition).trim();

                //Slimy patch of some hard to remove bugs... Forgive me!
                if(name.contains("{") || name.indexOf('"') > -1) {
                    //Main.println(name.trim());
                    continue;
                }
                //End of slimy patch

                Variable newVariable = new Variable(type, name, value);

                if(ParserUtilities.doesVariableExist(newVariable, extractedVariables) == false) {
                    extractedVariables.add(newVariable);
                } else {
                    for(int i = 0; i < extractedVariables.size(); i++) {
                        //for (Variable var : extractedVariables ) {
                        if (extractedVariables.get(i).name.equals(newVariable.name)) {
                            Variable temp = extractedVariables.get(i);
                            temp.addValue(value);
                            extractedVariables.set(i, temp);
                            //Main.println("extracted variable " + temp.name + ", new value "+ temp.value.toString());
                        }
                    }
                }
                //Main.println("");
            } catch(IndexOutOfBoundsException e) {
                extractedVariables.add(new Variable("No Type Found!", definition));
            }

        }

        System.out.println("********** " + extractedVariables.size());

        return ParserUtilities.variableListToArray(extractedVariables);
    }

    //Should test - Really hard to do. Skipped.
    public static Variable[] extractRelationships(Variable variable) {
        List<Variable> extractedVariables = new ArrayList<Variable>();
        //Main.println("in extract relationships with " + variable.name + " looking for  " + variable.value);
        //Main.println("Length of variable's Value list: " + variable.value.size());
        for(int i = 0; i < variable.value.size(); i++) {
            String[] determinedRelationships = determineRelationships1(variable.value.get(i));

            //Main.println("resolveRelationships - determinedRelationships: ");
            //Main.printStringArray(determinedRelationships);

            for (String relation : determinedRelationships) {
                if(relation.trim().equals(variable.name)) {
                    continue;
                }
                for (String existingVariableName : variableNames) {
                    if (relation.trim().equals(existingVariableName)) {
                        //Main.println("");
                        Variable tempVariable = new Variable(variable.type, relation);
                        extractedVariables.add(tempVariable);
                    }
                }
            }
        }

        return ParserUtilities.variableListToArray(extractedVariables);
    }

    //Should test - Really hard to do. Skipped.
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
            tempVariable.relationships = ParserUtilities.variableListToArray(tempRelationships);

            //Add the new resolved variable, with relationships defined as Variable objects, to the resolvedVariables list!
            resolvedVariables.add(tempVariable);
        }
        //Return the list once all of the looping is done.
        return ParserUtilities.variableListToArray(resolvedVariables);
    }

}
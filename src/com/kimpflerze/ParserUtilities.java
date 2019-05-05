package com.kimpflerze;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class ParserUtilities {

    //Simple, dont test.
    public static boolean doesVariableExist(Variable variable, List<Variable> existingVariables) {
        for(Variable existingVariable : existingVariables) {
            if(existingVariable.name.equals(variable.name)) {
                return true;
            }
        }
        return false;
    }

    //Simple, dont test.
    public static String[] trySplitOn(String string, String[] splitStrings) {
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

    //Simple, dont test.
    public static String[] removeDuplicate(String [] arr) {
        int end = arr.length;
        Set<String> set = new HashSet<String>();

        for(int i = 0; i < end; i++){
            set.add(arr[i]);
        }
        Iterator<String> it = set.iterator();
        String [] toreturn = new String[set.size()];
        int temp = 0;
        while(it.hasNext()) {
            toreturn[temp] = (String) it.next();
            temp = temp+1;
        }
        return toreturn;
    }

    //Simple, dont test.
    public static String findbrackets (String value) {
        //Main.println("Test1");
        //Main.println("Test 1 Value: " + value);
        return (value.substring(value.indexOf("(") + 1, value.indexOf(")")));
    }

    //Should test - Tested
    public static String[] splitStringAtIndicies(int[] indexList, String value) {
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
            //Main.println("ValueSubstring: " + valueSubstring);
            if(!valueSubstring.isEmpty()) {
                operatorSplit.add(valueSubstring);
                startIndex = indexList[i] + 1;
            }

        }
        valueSubstring = value.substring(startIndex, value.length()).trim();
        operatorSplit.add(valueSubstring);

        return stringListToArray(operatorSplit);
    }

    //Should test - Tested
    public static String determineName(String definition) {
        String[] whiteSpaceSplit = definition.split(" ");

        String name = whiteSpaceSplit[whiteSpaceSplit.length - 1];

        if(name.contains("[")) {
            int openBracketIndex = -1;
            openBracketIndex = name.indexOf("[");
            name = name.substring(0, openBracketIndex);
        }

        return name;
    }

    //Not used, dont test.
    public static String[] trimArray(String[] array) {
        String[] trimmedArray = array;
        for(int i = 0; i < trimmedArray.length; i++) {
            trimmedArray[i] = trimmedArray[i].trim();
        }
        return trimmedArray;
    }

    //Should test - Tested
    public static boolean contains(String string, String[] searchStrings) {
        boolean doesContain = false;
        for(String searchString : searchStrings) {
            if(string.contains(searchString)) {
                doesContain = true;
            }
        }
        return doesContain;
    }

    //Simple, dont test.
    public static Variable[] variableListToArray(List<Variable> variables) {
        Variable[] array = new Variable[variables.size()];
        for(int i = 0; i < variables.size(); i++) {
            array[i] = variables.get(i);
        }
        return array;
    }

    //Simple, dont test.
    public static String[] stringListToArray(List<String> lines) {
        String[] array = new String[lines.size()];
        for(int i = 0; i < lines.size(); i++) {
            array[i] = lines.get(i);
        }
        return array;
    }

    //Simple, dont test.
    public static int[] intListToArray(List<Integer> ints) {
        int[] array = new int[ints.size()];
        for(int i = 0; i < ints.size(); i++) {
            array[i] = ints.get(i);
        }
        return array;
    }

    //Technically this function isn't used.
    //The function that calls this function is never used.
    //I cant really test this function. Im using standard libraries and
    //Im not able to be sure of what the object's ByteArray's would be.
    public static Variable deepCopy(Variable object){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
            return (Variable) objectInputStream.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Not used, dont test
    public static Variable[] deepCopyArray(Variable[] variables) {
        List<Variable> tempList = new ArrayList<Variable>();
        for(Variable v : variables) {
            tempList.add(ParserUtilities.deepCopy(v));
        }
        return ParserUtilities.variableListToArray(tempList);
    }

}

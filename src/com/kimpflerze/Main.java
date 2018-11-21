package com.kimpflerze;

public class Main {

    public static void println(String string) {
        System.out.println(string);
    }

    public static void print(String string) {
        System.out.print(string);
    }

    public static void printStringArray(String[] array) {
        for(int i = 0; i < array.length; i++) {
            System.out.println(i + ": " + array[i]);
        }
    }

    public static void main(String[] args) {
        String[] filePaths = {"ExampleProgram.txt"};
        for(String path : filePaths) {
            String[] originalLines = Parser.loadFile(path);

            //Print out the original file's unaltered lines, just for sanity check.
            System.out.println("\nOriginal File:");
            printStringArray(originalLines);
            System.out.println(":End of Original File\n");

            String[] assignmentLines = Parser.findAssignments(originalLines);

            //Print out the lines with assignments.
            System.out.println("\nAssignment Lines:");
            printStringArray(assignmentLines);
            System.out.println(":End of Assignment Lines\n");

            Variable[] extractedVariables = Parser.extractVariables(assignmentLines);
            println("Extracted Variables' Names:");
            for(Variable var : extractedVariables) {
                println("   " + var.name);
            }


            //Unfinished, left off working on this!
            //Variable[] resolvedVariables = Parser.resolveRelationships(extractedVariables);

        }


    }
}

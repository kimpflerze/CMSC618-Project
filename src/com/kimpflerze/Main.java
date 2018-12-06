package com.kimpflerze;

import java.util.*;

public class Main {

    public static void println(String string) {
        System.out.println(string);
    }

    public static void print(String string) {
        System.out.print(string);
    }

    public static void printIntArray(int[] array) {
        if(array.length != 0) {
            for (int i = 0; i < array.length; i++) {
                System.out.println(i + ": " + Integer.toString(array[i]));
            }
        }
    }

    public static void printStringArray(String[] array) {
        for(int i = 0; i < array.length; i++) {
            System.out.println(i + ": " + array[i]);
        }
    }

    private static List<Variable> combineClasses(List<Variable[]> classResolvedRelationsList) {
        List<Variable> combinedRelationsList = new ArrayList<Variable>();

        for(Variable[] relationsArray : classResolvedRelationsList) {
            for(Variable variable : relationsArray) {
                combinedRelationsList.add(variable);
            }
        }

        return combinedRelationsList;
    }

    public static void main(String[] args) {
        String[] filePaths = {"ExampleProgram.txt", "Parser.txt"};

        List<Variable[]> classResolvedRelationsList = new ArrayList<Variable[]>();

        for(String path : filePaths) {
            String[] originalLines = Parser.loadFile(path);

            //Print out the original file's unaltered lines, just for sanity check.
            System.out.println("\nOriginal File:");
            printStringArray(originalLines);
            System.out.println(":End of Original File\n");

            String[] classNames = Parser.findClassAndSubClassNames(originalLines);
            String currentClassName = classNames[0];
            System.out.println("\nClass Names:");
            printStringArray(classNames);
            System.out.println(":End of Class Names\n");

            String[] assignmentLines = Parser.findAssignments(originalLines);

            //Print out the lines with assignments.
            System.out.println("\nAssignment Lines:");
            printStringArray(assignmentLines);
            System.out.println(":End of Assignment Lines\n");

            Variable[] extractedVariables = Parser.extractVariables(assignmentLines);
            println("Extracted Variables' Names:");
            int variableCounter = 0;
            for(Variable var : extractedVariables) {
                println("   " + var.name);
                println("extracted variable value " + var.value.toString());

                //Add class name to every variable extracted
                extractedVariables[variableCounter].className = currentClassName;
                variableCounter++;
                println("extracted variable class name " + var.className);
            }


            //Unfinished, left off working on this!
            Variable[] resolvedVariables = Parser.resolveRelationships(extractedVariables);
            println("Resolved Variables' Names & Relationships:");
            for(Variable var : resolvedVariables) {
                println("extracted variable name " + var.name);
            }

            int varCounter = 0;
            int relationCounter = 0;
            for(Variable var : resolvedVariables) {
                for(Variable relation : var.relationships) {
                    println("#" + varCounter + ", " + relationCounter + ": VarName = " +var.name + ", RelationName = "+ relation.name);
                    relationCounter++;
                }
                varCounter ++;
                relationCounter = 0;
            }

            classResolvedRelationsList.add(resolvedVariables);
            println("going to taint variables ");
            Parser.taintSpread(resolvedVariables,"c" , 1);
            println("checking taint");
            for(int m = 0; m < resolvedVariables.length; m++) {
            	if(resolvedVariables[m].tainted == true) {
            		println(resolvedVariables[m].name + " is tainted.");
            	}
            }
            println("end checking");

        }

        Variable[] combinedClassRelations = Parser.variableListToArray(combineClasses(classResolvedRelationsList));

        GraphDraw graphDraw = new GraphDraw();
        graphDraw.DrawVariables(combinedClassRelations);
        graphDraw.DrawTaintSpreadGraph(combinedClassRelations);

    }
}

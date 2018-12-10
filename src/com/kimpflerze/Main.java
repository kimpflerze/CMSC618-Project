package com.kimpflerze;

import com.sun.corba.se.impl.orbutil.graph.Graph;
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
        Scanner scanner = new Scanner(System.in);
        println("Please type out file names you wish to scan, separated by a comma...");
        println("====================================================================");
        String inputFilesNames = scanner.nextLine();

        String[] filePaths = inputFilesNames.split(",");
        for(int i = 0; i < filePaths.length; i++) {
            filePaths[i] = filePaths[i].trim();
            println("File: " + filePaths[i]);
        }
        //String[] filePaths = {"ExampleProgram.txt"};

        List<Variable[]> classResolvedRelationsList = new ArrayList<Variable[]>();

        for(String path : filePaths) {
            String[] originalLines = Parser.loadFile(path);

            //Print out the original file's unaltered lines, just for sanity check.
            System.out.println("\nOriginal File:");
            printStringArray(originalLines);
            System.out.println(":End of Original File\n");

            int dotIndex = -1;
            for(String tempString : filePaths) {
                for(int i = 0; i < tempString.length(); i++) {
                    if(tempString.charAt(i) == '.') {
                        dotIndex = i;
                    }
                }
            }
            String classNameFromFileName = path.substring(0, dotIndex);

            String[] classNames = Parser.findClassAndSubClassNames(originalLines);
            //String currentClassName = classNames[0];
            String currentClassName = classNameFromFileName;
            println("CurrentClassName: " + currentClassName + ", " + dotIndex);
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
                //println("extracted variable value " + var.value.toString());

                //Add class name to every variable extracted
                extractedVariables[variableCounter].className = currentClassName;
                variableCounter++;
                //println("extracted variable class name " + var.className);
            }


            //Unfinished, left off working on this!
            Variable[] resolvedVariables = Parser.resolveRelationships(extractedVariables);
            println("Resolved Variables' Names & Relationships:");
            for(Variable var : resolvedVariables) {
                println(" " + var.name);
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

            //Display names of variables and prompt for which one is tainted...
            println("\n\n");
            println("Extracted Variables: ");
            for(Variable variable : resolvedVariables) {
                println("\t" + variable.name);
            }
            println("\nWhich Variable is Sensitive? (' ' for none!): ");
            String sensitiveVariableName = scanner.nextLine().trim();


            Parser.taintSpread(resolvedVariables, sensitiveVariableName , 1);
            for(int m = 0; m < resolvedVariables.length; m++) {
                if(resolvedVariables[m].tainted == true) {
                    println(resolvedVariables[m].name + " is tainted.");
                }
            }
        }

        Variable[] combinedClassRelations = Parser.variableListToArray(combineClasses(classResolvedRelationsList));

        println("\n\nDone with parsing, please select output format: ");
        println("(1) Graph");
        println("(2) Gephi GEXF File");
        println("(0) Exit, no output");
        Integer selection = scanner.nextInt();
        if(selection == 1) {
            GraphDraw graphDraw = new GraphDraw();
            graphDraw.DrawVariables(combinedClassRelations);
        }
        else if(selection == 2) {
            GenerateGephiFile gephiGenerator = new GenerateGephiFile();
            gephiGenerator.generateGexfFile(combinedClassRelations);
        }
        else {
            println("No output!");
            System.exit(0);
        }

        println("\n\nAnalysis and output complete!\n");
    }
}

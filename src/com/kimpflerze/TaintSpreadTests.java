package com.kimpflerze;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TaintSpreadTests {


    //I manually count the number of variables effected by the variable "new1" within the
    //sample program provided.
    //I found there are 2 nodes that are tainted, hence the hard-coded value.
    //There is no other means of accurately checking this.
    @Test
    public void taintSpreadTest() {
        String fileName = "ExampleProgram.txt";

        String[] originalLines = Parser.loadFile(fileName);

        String[] classNames = Parser.findClassAndSubClassNames(originalLines);

        String[] variableAssignmentLines = Parser.findAssignments(originalLines);

        Variable[] extractedVariables = Parser.extractVariables(variableAssignmentLines);

        String currentClassName = "ExampleProgram";

        int variableCounter = 0;
        for(Variable var : extractedVariables) {
            extractedVariables[variableCounter].className = currentClassName;
            variableCounter++;
        }

        Variable[] resolvedVariables = Parser.resolveRelationships(extractedVariables);

        for (Variable v : resolvedVariables) {
            TaintSpread.taintSpread(resolvedVariables, "new1", 1);
        }

        //Now determine number of nodes that are tainted.
        //I pre-calculated the number of tainted nodes in the file.
        int expectedNumberOfTaintedNodes = 2;
        int actualNumberOfTaintedNodes = 0;

        for (Variable v : extractedVariables) {
            if (v.tainted == true) {
                actualNumberOfTaintedNodes = actualNumberOfTaintedNodes + 1;
            }
        }


        System.out.println("Expected:" + expectedNumberOfTaintedNodes + ", Actual: " + actualNumberOfTaintedNodes);

        assertTrue(expectedNumberOfTaintedNodes == actualNumberOfTaintedNodes);
    }

}

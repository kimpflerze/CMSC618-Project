/* Simple graph drawing class
Bert Huang
COMS 3137 Data Structures and Algorithms, Spring 2009

This class is really elementary, but lets you draw 
reasonably nice graphs/trees/diagrams. Feel free to 
improve upon it!
 */
package com.kimpflerze;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GenerateGephiFile{

    public static GenerateGephiFile shared = new GenerateGephiFile();

    private ArrayList<Node> nodes = new ArrayList<Node>();
    private ArrayList<edge> edges = new ArrayList<edge>();

    public GenerateGephiFile getShared() {
        return shared;
    }

    class Node {
        int x, y;
        String name;
        Color color;
        boolean tainted;

        public Node(String myName, int myX, int myY, boolean t) {
            x = myX;
            y = myY;
            name = myName;
            tainted = t;
        }
    }

    class edge {
        int i, j;

        public edge(int ii, int jj) {
            i = ii;
            j = jj;
        }
    }

    public void addNode(String name, int x, int y, boolean tainted) {
        //add a node at pixel (x,y)
        nodes.add(new Node(name, x, y, tainted));
    }

    public void addEdge(int i, int j) {
        //add an edge between nodes i and j
        edges.add(new edge(i, j));
    }

    public void generateGexfFile(Variable[] resolvedVariables, String outputFileName) { // draw the nodes and edges
        //<node id="0.0" label="Myriel"/>
        //<edge id="0" source="1.0" target="0.0"/>

        HashMap<String, Integer> hmap = new HashMap<String, Integer>();
        int count = 0;
        for (Variable v : resolvedVariables) {
            hmap.put(v.getName(), count);
            count = count + 1;
        }

        count = 0;
        for(Variable v : resolvedVariables) {
            //addNode(v.getClassName() + "." + v.getName(), 0, 0, v.tainted);
            nodes.add(new Node(v.className + "." + v.name, 0, 0, v.tainted));
            //Main.println("Node: " + v.getClassName() + "." + v.getName() + ", " + v.tainted + "\n");
            count = count + 1;
        }

        //drwaing edges between related variables
        for(Variable v : resolvedVariables) {
            int a = hmap.get(v.getName());
            //Main.println("i am graphing " + v.getName() + " and I found it an hmap index " + a);
            Variable [] rv = v.getRelationships();

            for (Variable v2 : rv) {
                String temp = v2.getName();

                int b = hmap.get(temp);
                //Main.println("parent var " + v.getName() + " relation with " + v2.getName() + " and I found it an hmap index " + b);
                addEdge(a, b);
            }
        }

        HashMap<String, Double> nodesHashMap = new HashMap<String, Double>();
        for(double i = 0.0; i < nodes.size(); i++) {
            nodesHashMap.put(nodes.get((int)(i)).name, i);
        }

        ArrayList<String> nodesList = new ArrayList<String>();
        ArrayList<String> edgesRelationsList = new ArrayList<String>();

        String nodeDefinitionString = "";
        int idCounter = 0;
        for(Node n : nodes) {
            nodeDefinitionString = "<node id=\"";
            nodeDefinitionString += nodesHashMap.get(n.name);
            nodeDefinitionString += "\" label=\"";
            nodeDefinitionString += n.name;
            nodeDefinitionString += "\"/>";

            //Main.println(nodeDefinitionString);
            nodesList.add(nodeDefinitionString);
        }

        idCounter = 0;
        String edgeRelationString = "";
        List<Pair<Double, Double>> usedVariationsList = new ArrayList<Pair<Double, Double>>();
        for(edge e : edges) {
            Double eiIndex = nodesHashMap.get(nodes.get(e.i).name);
            Double ejIndex = nodesHashMap.get(nodes.get(e.j).name);
            edgeRelationString = "<edge id=\"";
            edgeRelationString += idCounter;
            edgeRelationString += "\" source=\"";
            edgeRelationString += ejIndex;
            edgeRelationString += "\" target=\"";
            edgeRelationString += eiIndex;
            edgeRelationString += "\"/>";

            //Main.println(edgeRelationString + "\t" + edgesRelationsList.size());
            edgesRelationsList.add(edgeRelationString);

            idCounter++;
        }

        //Now write it all to a file in correct order...
        try {
            FileWriter fileWriter = new FileWriter(outputFileName);

            //Print file header...
            List<String> headerArray = new ArrayList<String>();
            headerArray.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            headerArray.add("<gexf xmlns:viz=\"http:///www.gexf.net/1.1draft/viz\" version=\"1.1\" xmlns=\"http://www.gexf.net/1.1draft\">\n");
            headerArray.add("<meta lastmodifieddate=\"2010-03-03+23:44\">\n");
            headerArray.add("<creator>GenerateGephiFile</creator>\n");
            headerArray.add("<description>An influence visualization</description>");
            headerArray.add("</meta>\n");
            headerArray.add("<graph defaultedgetype=\"directed\" idtype=\"string\" type=\"static\">\n");
            for(String headerLine : headerArray) {
                fileWriter.write(headerLine);
            }

            //Print node information...
            //<nodes count="77">
            fileWriter.write("<nodes count=\"" + nodesList.size() + "\">\n");
            for(String nodeDefinitionLine : nodesList) {
                fileWriter.write(nodeDefinitionLine + "\n");
            }
            fileWriter.write("</nodes>\n");

            //Print edge information
            //<edges count="254">
            fileWriter.write("<edges count=\"" + edgesRelationsList.size() + "\">\n");
            for(String edgeDefinitionLine : edgesRelationsList) {
                fileWriter.write(edgeDefinitionLine + "\n");
            }
            fileWriter.write("</edges>\n");

            //Close file tags...
            fileWriter.write("</graph>\n");
            fileWriter.write("</gexf>\n");

            fileWriter.close();
        } catch(IOException e) {
            Main.println("Error when creating Gephi output file!");
        }


    }
}
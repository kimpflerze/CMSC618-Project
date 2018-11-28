/* Simple graph drawing class
Bert Huang
COMS 3137 Data Structures and Algorithms, Spring 2009

This class is really elementary, but lets you draw 
reasonably nice graphs/trees/diagrams. Feel free to 
improve upon it!
 */
package com.kimpflerze;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Random;

public class GraphDraw extends JFrame {
    int width;
    int height;

    ArrayList<Node> nodes;
    ArrayList<edge> edges;

    public GraphDraw() { //Constructor
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	nodes = new ArrayList<Node>();
	edges = new ArrayList<edge>();
	width = 50;
	height = 20;
    }

    public GraphDraw(String name) { //Construct with label
	this.setTitle(name);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	nodes = new ArrayList<Node>();
	edges = new ArrayList<edge>();
	width = 100;
	height = 100;
    }

    class Node {
	int x, y;
	String name;
	
	public Node(String myName, int myX, int myY) {
	    x = myX;
	    y = myY;
	    name = myName;
	}
    }
    
    class edge {
	int i,j;
	
	public edge(int ii, int jj) {
	    i = ii;
	    j = jj;	    
	}
    }
    
    public void addNode(String name, int x, int y) { 
	//add a node at pixel (x,y)
	nodes.add(new Node(name,x,y));
	this.repaint();
    }
    public void addEdge(int i, int j) {
	//add an edge between nodes i and j
	edges.add(new edge(i,j));
	this.repaint();
    }
    
    public void paint(Graphics g) { // draw the nodes and edges
	FontMetrics f = g.getFontMetrics();
	int nodeHeight = Math.max(height, f.getHeight());
	Random rand = new Random();
	float r = rand.nextFloat();
	float gg = rand.nextFloat();
	float b = rand.nextFloat();
	Color randomColor = new Color(r, gg, b);

	Graphics2D g2 = (Graphics2D) g;
	for (edge e : edges) {
		r = rand.nextFloat();
		gg = rand.nextFloat();
		b = rand.nextFloat();
		randomColor = new Color(r, gg, b);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(randomColor);
	    g2.drawLine(nodes.get(e.i).x, nodes.get(e.i).y,
		     nodes.get(e.j).x, nodes.get(e.j).y);
	}
	
	for (Node n : nodes) {
		r = rand.nextFloat();
		gg = rand.nextFloat();
		b = rand.nextFloat();
		randomColor = new Color(r, gg, b);
	    int nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);
	    g.setColor(randomColor);
	    g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);
	    g.setColor(Color.black);
	    g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);

		g.setColor(Color.white);
	    g.drawString(n.name, n.x-f.stringWidth(n.name)/2,
			 n.y+f.getHeight()/2);
	}
    }
// }

    public void DrawVarialbes(Variable[] resolvedVariables) {
   	GraphDraw frame = new GraphDraw("Relationship Graph");

	 frame.setSize(1000,1000);
	
	 frame.setVisible(true);
    	// int x = 0;
    	// int y = 0;
    	Random ran = new Random();
		
    	HashMap<String, Integer> hmap = new HashMap<String, Integer>();
    	int count = 0;
    	for (Variable v : resolvedVariables) {
    		hmap.put(v.getName(), count);
    		count = count + 1;
    	}

    	//drawing nodes for all variables
    	ArrayList<Integer> list = new ArrayList<Integer>();
    	for (int i = 0; i <360 ; i=i+30) {
    		list.add(i);
    	}
    	int radius = ran.nextInt(200)+ 200;
    	Main.print("Radius: " + radius);
    	int x_cen = 500;
    	int y_cen = 500;
    	count = 0;
    	for(Variable v : resolvedVariables) {
    		if (count > 35) {
    			radius = ran.nextInt(400)+100;
    		}
    		int x = (int)(radius * Math.cos(Math.toRadians(list.get(count))));
    		int y = (int)(radius * Math.sin(Math.toRadians(list.get(count))));

    		Main.println("Node " + v.getName() + ": " + x + ", " + y);

    		frame.addNode(v.getName(), x + x_cen, y + y_cen);
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

    			if (!v.getName().trim().equals(v2.getName().trim())) {
					frame.addEdge(a, b);
				}
			}
    	}
     }

// class testGraphDraw {
    //Here is some example syntax for the GraphDraw class
    public static void main(String[] args) {
	GraphDraw frame = new GraphDraw("Test Window");

	frame.setSize(1000,1000);
	
	frame.setVisible(true);

	int x = 0;
    int y = 0;
    Random ran = new Random();
    x = ran.nextInt(800) + 100;
    y = ran.nextInt(800) + 100;

	frame.addNode("a", x,y);
	x = ran.nextInt(800) + 100;
    y = ran.nextInt(800) + 100;
	frame.addNode("b", x,y);
	x = ran.nextInt(800) + 100;
    y = ran.nextInt(800) + 100;
	frame.addNode("longNode", x,y);
	frame.addEdge(0,1);
	frame.addEdge(0,2);
    }
}
// }
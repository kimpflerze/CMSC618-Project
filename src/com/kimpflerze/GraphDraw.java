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
    Random randC = new Random();

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
		width = 50;
		height = 30;
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
			color  = new Color(randC.nextFloat(), randC.nextFloat(), randC.nextFloat());
			tainted  = t;
		}
    }

    class edge {
		int i,j;

		public edge(int ii, int jj) {
			i = ii;
			j = jj;
		}
    }

    public void addNode(String name, int x, int y, boolean tainted) {
	//add a node at pixel (x,y)
	nodes.add(new Node(name,x,y,tainted));
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
		g2.setStroke(new BasicStroke(3));
		g2.setColor(nodes.get(e.j).color);
	    g2.drawLine(nodes.get(e.i).x, nodes.get(e.i).y,
		     nodes.get(e.j).x, nodes.get(e.j).y);

		//Add in tainted edge marker
		int markerDimension = 10;
		int centerX = (int)((nodes.get(e.i).x + nodes.get(e.j).x) / 2) - (markerDimension / 2);
		int centerY = (int)((nodes.get(e.i).y + nodes.get(e.j).y) / 2) - (markerDimension / 2);
		if(nodes.get(e.i).tainted && nodes.get(e.j).tainted) {
			Color originalStrokeColor = g2.getColor();
			g2.setColor(Color.RED);
			g2.drawOval(centerX, centerY, markerDimension, markerDimension);
			g2.setColor(originalStrokeColor);
		}
	}

	for (Node n : nodes) {
	    int nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);
	    g.setColor(n.color);
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

    public void DrawVariables(Variable[] resolvedVariables) {
		GraphDraw frame = new GraphDraw("Relationship Graph");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int FRAME_WIDTH = (int) screenSize.getWidth();
		final int FRAME_HEIGHT = (int) screenSize.getHeight();

		final int NUMBER_NODES_PER_RING = resolvedVariables.length / 2;
		frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);

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
    	for (int i = 0; i < 360 ; i=i+(360/NUMBER_NODES_PER_RING)) {
    		Main.println("" + i);
    		list.add(i);
    	}
    	//int radius = ran.nextInt(200)+ 200;
		double radiusHeight = (FRAME_HEIGHT * 0.25);
		double radius = FRAME_HEIGHT - (2.3 * radiusHeight);
    	Main.print("Radius: " + radius);
    	int x_cen = FRAME_WIDTH / 2;
    	int y_cen = FRAME_HEIGHT / 2;
    	Main.println("Centers: " + x_cen + ", " + y_cen);
    	count = 0;
    	for(Variable v : resolvedVariables) {
    		if (count > NUMBER_NODES_PER_RING) {
				//radius = rand.nextInt(400) + 100;
    			radius = radiusHeight - (radiusHeight * 0.2);
    			count = 0;
    		}
    		int x = (int)(radius * Math.cos(Math.toRadians(list.get(count))));
    		int y = (int)(radius * Math.sin(Math.toRadians(list.get(count))));

    		frame.addNode(v.getClassName() + "." + v.getName(), x + x_cen, y + y_cen, v.tainted);
    		Main.println("Node: " + v.getClassName() + "." + v.getName() + ", " + x + x_cen + ", " + y + y_cen + ", " + v.tainted + "\n");
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

     /*
     public void DrawTaintSpreadGraph(Variable[] resolvedVariables) {
		 GraphDraw frame = new GraphDraw("Relationship Graph");

		 Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 final int FRAME_WIDTH = (int) screenSize.getWidth();
		 final int FRAME_HEIGHT = (int) screenSize.getHeight();

		 final int NUMBER_NODES_PER_RING = resolvedVariables.length / 2;
		 frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);

		 frame.setVisible(true);
		 Random ran = new Random();

		 HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		 int count = 0;
		 for (Variable v : resolvedVariables) {
			 hmap.put(v.getName(), count);
			 count = count + 1;
		 }

		 //drawing nodes for all variables
		 ArrayList<Integer> list = new ArrayList<Integer>();
		 for (int i = 0; i <360 ; i=i+(360/NUMBER_NODES_PER_RING)) {
			 list.add(i);
		 }

		 int radiusHeight = (int)(FRAME_HEIGHT * 0.25);
		 double radius = FRAME_HEIGHT - (int)(2.3 * radiusHeight);
		 Main.print("Radius: " + radius);
		 int x_cen = FRAME_WIDTH / 2;
		 int y_cen = FRAME_HEIGHT / 2;
		 Main.println("Centers: " + x_cen + ", " + y_cen);
		 count = 0;
		 for(Variable v : resolvedVariables) {
		 	if(v.tainted) {
				if (count > NUMBER_NODES_PER_RING) {
					radius = radiusHeight - (radiusHeight * 0.1);
					count = 0;
				}
				int x = (int) (radius * Math.cos(Math.toRadians(list.get(count))));
				int y = (int) (radius * Math.sin(Math.toRadians(list.get(count))));

				frame.addNode(v.className + "." + v.getName(), x + x_cen, y + y_cen, v.tainted);
				Main.println("Node: " + v.getClassName() + "." + v.getName() + ", " + x + x_cen + ", " + y + y_cen + ", " + v.tainted + "\n");
				count = count + 1;
			}
		 }

		 //Drawing edges between related variables
		 for(Variable v : resolvedVariables) {
			 int a = hmap.get(v.getName());
			 //Main.println("i am graphing " + v.getName() + " and I found it an hmap index " + a);
			 Variable [] rv = v.getRelationships();

			 for (Variable v2 : rv) {
				 String temp = v2.getName();

				 int b = hmap.get(temp);
				 //Main.println("parent var " + v.getName() + " relation with " + v2.getName() + " and I found it an hmap index " + b);

				 if (!v.getName().trim().equals(v2.getName().trim()) && (v.tainted && v2.tainted)) {
					 frame.addEdge(a, b);
				 }
			 }
		 }
	 }
	 */

     //THIS FUNCTION IS FOR TESTING ONLY! IT DOESNT REALLY EXECUTE IN NORMAL RUNS!
    public static void main(String[] args) {
		GraphDraw frame = new GraphDraw("Test Window");

		frame.setSize(3000,3000);

		frame.setVisible(true);

		int x = 0;
		int y = 0;
		Random ran = new Random();
		x = ran.nextInt(800) + 100;
		y = ran.nextInt(800) + 100;

		frame.addNode("a", x,y,false);
		x = ran.nextInt(800) + 100;
		y = ran.nextInt(800) + 100;
		frame.addNode("b", x,y,false);
		x = ran.nextInt(800) + 100;
		y = ran.nextInt(800) + 100;
		frame.addNode("longNode", x,y,false);
		frame.addEdge(0,1);
		frame.addEdge(0,2);
    }
}
// }
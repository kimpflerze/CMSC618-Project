package com.kimpflerze;

import java.util.*;
import java.io.*;

public class Variable {

    String className;
    String type;
    String name;
    List<String> value = new ArrayList<String>();
    Variable[] relationships;
    boolean tainted;

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
        this.tainted = false;
    }

    public Variable(String type, String name, List<String> value) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.tainted = false;
    }
    public Variable(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value.add(value);
        this.tainted = false;
    }


    public Variable(String type, String name, Variable[] relationships) {

    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
    
    public void setTainted(boolean value) {
        this.tainted = value;
    }

    public void setClassName(String className) { this.className = className; }

    public void addValue(String value) {
        this.value.add(value);
    }

    public void setRelationships(Variable[] relationships) {
        this.relationships = relationships;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValue() {
        return this.value;
    }

    public String getClassName() { return this.className; }

    public Variable[] getRelationships() {
        return this.relationships;
    }

    public boolean compare(Variable a) {
        return this.name.equals(a.name);
    }

}

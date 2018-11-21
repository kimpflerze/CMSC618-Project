package com.kimpflerze;

public class Variable {

    String type;
    String name;
    String value;
    Variable[] relationships;

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public Variable(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Variable(String type, String name, Variable[] relationships) {

    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getValue() {
        return this.value;
    }

    public Variable[] getRelationships() {
        return this.relationships;
    }

    public boolean compare(Variable a) {
        return this.name.equals(a.name);
    }

}

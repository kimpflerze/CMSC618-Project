package com.kimpflerze;

public class TaintSpread {
    //Simple,dont test.
    public static void taintVariable(Variable [] allVariables, String vname) {
        for(int i = 0; i < allVariables.length; i++) {
            if (allVariables[i].name.equals(vname)) {
                allVariables[i].setTainted(true);
            }
        }
    }

    //Should test
    // function to find taint spread
    public static void taintSpread(Variable [] allVariables, String vname, int hop) {
        if(hop == 0) {
            taintVariable(allVariables,vname);
            return;
        } else if(hop > 0) {
            taintVariable(allVariables,vname);
            //Main.println("hop no: " + Integer.toString(hop));
            for(int i = 0; i < allVariables.length; i++) {
                for(int j = 0; j < allVariables[i].relationships.length; j++) {
                    if(allVariables[i].relationships[j].name.equals(vname)) {
                        taintSpread(allVariables,allVariables[i].getName(), hop-1);
                    }
                }

            }
        } else {
            return;
        }

    }
}

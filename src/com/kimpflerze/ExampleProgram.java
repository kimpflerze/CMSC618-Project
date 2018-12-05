package com.kimpflerze;

public class ExampleProgram {
    class dummy{
        int x;
        public dummy(int a) {
            x = a;
        }
    }

    public static double dummyfunc(int a, double b) {
        return (a+b);
    }

    public static void main(String[] args) {
        int a = 0;

        double b = 1.0;
        // dummy test = new dummy (a);
        double func = dummyfunc(a,b);

        String c = "";

        int a_copy = a;

        double taintedVariable  = -1;

        double[] mixedBagArray = new double[10];

        double sum = 0.0;

        for(int i = 0; i < mixedBagArray.length; i++) {
            mixedBagArray[i] = Math.random();
            sum += add(sum, mixedBagArray[i]);
        }

        c += "Cats are cool, dogs are spiffy, aight.";

        String d = c + "Agreed, conversations with myself in test files is nuts.";

        String e = c + d;

        e = "Is there an echo in here?";
    }
    

    private static double add(double a, double b) {
        return a + b;
        //This is a very hard test case!
        // The (unclear (lack of obvious flag)) mixing of generic variables that
        // are already used makes this very difficult!
        //
        //Solution:
        //
        //  Just consider any variable that passes into a function like this as tainted!
        //  Does pose the question, should we only consider a method as a "tainted method"
        //  or method that creates tainted output by default? Or should we only consider it a "tainted method"/taint producing method
        //  if the tainted variable is ever passed to that function?
        //  This would add a lot of complexity to our parser. We would need to find assignments and be able to handle parsing
        //  method calls/functions called on objects (which would be even MORE difficult...) *Grumble* Difficult...
    }
}

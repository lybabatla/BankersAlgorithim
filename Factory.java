import java.io.*;
import java.util.*;

public class Factory {
    public static void main(String[] args) {
        String filename = "/Users/williammccarthy/IdeaProjects/BankersAlgorithmProject_ch7/src/infile.txt";
        int nResources = args.length;
        int[] resources = new int[nResources];
        for (int i = 0; i < nResources; i++) { resources[i] = Integer.parseInt(args[i].trim()); }

        Bank theBank = new BankImpl(resources);
        int[] maxDemand = new int[nResources];
        int[] allocated = new int[nResources];
        Thread[] workers = new Thread[Customer.COUNT];      // the customers

		// read in values and initialize the matrices
		// to do
		// ...
                workers[threadNum] = new Thread(new Customer(threadNum, maxDemand, theBank));
                theBank.addCustomer(threadNum, allocated, maxDemand);

                ++threadNum;        //theBank.getCustomer(threadNum);
                resourceNum = 0;
            }
        } catch (FileNotFoundException fnfe) { throw new Error("Unable to find file \"" + filename + "\"");
        } catch (IOException ioe) { throw new Error("Error processing \"" + filename + "\""); }

        System.out.println("FACTORY: created threads");     // start the customers

        for (int i = 0; i < Customer.COUNT; i++) { workers[i].start(); }
        System.out.println("FACTORY: started threads");

    }
}

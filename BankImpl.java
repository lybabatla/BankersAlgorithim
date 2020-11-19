import java.io.*;
import java.util.*;

public class BankImpl implements Bank {
    private int n;			// the number of threads in the system
    private int m;			// the number of resources

    private int[] available; 	// the amount available of each resource
    private int[][] maximum; 	// the maximum demand of each thread
    private int[][] allocation;	// the amount currently allocated to each thread
    private int[][] need;		// the remaining needs of each thread
    private boolean[] finished;
    
    private void showAllMatrices(int[][] alloc, int[][] max, int[][] need, String msg) { 
		System.out.print(msg + "\n");
		System.out.print("\nALLOCATED     MAXIMUM     NEED");
    	for (int i = 0; i < n; i++) {
			System.out.println("\t");
			showVector(allocation[i], "");
			System.out.println("            ");
			showVector(maximum[i], "");
			System.out.println("            ");
			showVector(need[i], "");
		}
		System.out.print("\n");
	}
 
   // private void showMatrix(int[][] matrix, String title, String rowTitle) {
		// todo
   // }

    private void showVector(int[] vect, String msg) {
		System.out.print(msg + "[" + vect[0]);
		for(int i = 1; i < vect.length; i++) {
			System.out.print(" " + vect[i]);
		}
		System.out.print("]");
    }

    public BankImpl(int[] resources) {      // create a new bank (with resources)
		available = resources;
		n = 0;
    }
                               // invoked by a thread when it enters the system;  also records max demand
    public void addCustomer(int threadNum, int[] allocated, int[] maxDemand) {
		for(int i = 0; i < allocation.length; i++) {
			allocation[i][allocation[i].length-1] = allocated[i];
		}
		for(int j = 0; j < maximum.length; j++) {
			maximum[j][maximum[j].length-1] = maxDemand[j];
		}
		
		int necessity[];
		necessity = new int[allocated.length];
		for(int k = 0;k < allocated.length; k++ ) {
			necessity[k] = maxDemand[k] - allocated[k];		
		}
		for(int l = 0; l < necessity.length; l++) {
			need[l][need.length-1] = necessity[l];
		}
    	finished[finished.length-1] = false;
    	n++;
   }

    public void getState() {        // output state for each thread
		System.out.print("\n");
		showVector(available, "available: ");
		System.out.print("\n");
		showAllMatrices(allocation, maximum, need, "");
		System.out.print("\n");
    }
    
    private boolean isAvailable(int[] resource) {
    	for(int i = 0; i < resource.length; i++) {
    		if(available[i] < resource[i]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    private void allocation(int num, int[] resource) {
    	for(int i = 0; i < resource.length; i++) {
    		available[i] -= resource[i];
    		allocation[num][i] += resource[i];
    		need[num][i] -= resource[i];
    	}
    }
    
    private void deallocate(int num, int[] resource) {
    	for(int j = 0; j < resource.length; j++) {
    		available[j] += resource[j];
    		allocation[num][j] -= resource[j];
    		need[num][j] += resource[j];
    	}
    }
    
    private boolean hasMax(int num) {
    	for(int k = 0; k < allocation[num][k]; k++) {
    		if(allocation[num][k] < maximum[num][k]) {
    			return false;
    		}
    	}
    	return true;
    }
    
    private boolean processFinished() {
    	for(int i = 0; i < n; i++) {
    		if(!finished[i]) {
    			return false;
    		}
    	}
    	return true;
    }

    private boolean isSafeState (int threadNum, int[] request) {
		// todo -- actual banker's algorithm
    	if(!isAvailable(request)) {
    		return false;
    	}
    	
    	int[] original = allocation[threadNum];
    	boolean condition = false;
    	allocation(threadNum, request);
    	if(hasMax(threadNum)) {
    		deallocate(threadNum, allocation[threadNum]);
    		finished[threadNum] = true;
    	}
    	if(processFinished()) {
    		condition = true;
    	}
    	else {
    		for(int k = 0; k < n; k++) {
    			if(!finished[k]) {
    				condition = isSafeState(k, need[k]);
    				if(condition == true) {
    					break;
    				}
    			}
    		}
    	}
    	
    	if(finished[threadNum]) {
    		allocation(threadNum, original);
    	} else {
    		deallocate(threadNum, request);
    	}
    	
    	finished[threadNum] = false;
    	return condition;
    }
                                // make request for resources. will block until request is satisfied safely
    public synchronized boolean requestResources(int threadNum, int[] request)  {
 		for(int i = 0; i < request.length; i++) {
 			if(request[i] > available[i] || request[i] > need[threadNum][i]) {
 				return false;
 			}
 		}
 		
 		System.out.print("#P" + threadNum + " ");
 		showVector(request, "RQ:");
 		showVector(need[threadNum], ", needs:");
 		showVector(available, ",available=");
 		
 		if(isSafeState(threadNum, request)) {
 			allocation(threadNum, request);
 			System.out.print("--APPROVED--, #P" + threadNum);
 			showVector(allocation[threadNum], " now at:");
 			System.out.print("/n");
 			getState();
 			return true;
 		} else {
 			System.out.print(" DENIED\n");
 			return false;
 		}
     }

    public synchronized void releaseResources(int threadNum, int[] release)  {
		System.out.print("--------->#P" + threadNum);
		System.out.print(" has all its resources! RELEASING ALL and SHUTTING DOWN.\n\n");
		System.out.print("======== customer #");
		System.out.print(threadNum);
		showVector(allocation[threadNum], "releasing: ");
		deallocate(threadNum, allocation[threadNum]);
		finished[threadNum] = true;
    }
}
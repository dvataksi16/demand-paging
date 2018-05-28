
/*
 * Demand Paging
 * Operating System
 * Denisa Vataksi -- dv758
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DemandPaging {
	
	//RR quantum
	public static int QUANTUM = 3;

	public static Scanner input;


	// stores all processes
	public static ArrayList<Process> processes = new ArrayList<>();
	
	
	static NumberFormat nf = new DecimalFormat("##.0##############");
	public static void main(String[] args) throws FileNotFoundException {

		input = new Scanner(new File("src/RandomNumber.txt"));

		if (args.length < 6) {
			System.err.println("Wrong input. Must include at least 6 arguments (7 max).");
			System.exit(0);
		}

		// command line args
		Process.M = Integer.parseInt(args[0]);
		Process.P = Integer.parseInt(args[1]);
		Process.S = Integer.parseInt(args[2]);
		Process.J = Integer.parseInt(args[3]);
		Process.N = Integer.parseInt(args[4]);
		Process.R = args[5];

		// print prelim data
		printPreliminaryData();

		// set up processes array list based on job mix
		initProcesses(Process.J, Process.S, Process.N);

		// runs the demand paging algorithm and also returns the total number of faults
		// that happens
		int totalFaults = runAlgo();

		// prints results of the algorithm to the user
		printProcessSummary(processes,totalFaults);

	}
	
	public static int runAlgo() throws FileNotFoundException {
		
		int currentPage = 0;
		int currentPageAdjusted = 0;
		int totalFaults = 0;
		int terminatedProcesses = 0;

		int framesOccupied = 0;

		int totalFrames = Process.M / Process.P;

		int pagesPerProcess = Process.S / Process.P;
		int hitFrame = 0;

		// our data storage for the algorithm
		int frameTable[] = new int[totalFrames];
		int residency[] = new int[totalFrames];
		int pageArray[] = new int[totalFrames];


		for(int i = 0; i < frameTable.length; i++){
			frameTable[i] = -1;
		}

		boolean hit = false;

		// loop until all processes are finished
		while (terminatedProcesses <= processes.size()-1) {
			int i =0;
			for (Process process: processes) {

				// 3 (QUANTUM) references for each process
				for (int k = 0; k < QUANTUM; k++) {

					// go to next process if references are out
					if (process.getN() <= 0) {
						terminatedProcesses++;
						break;
					}
					currentPage = process.getCurrentWord() / Process.P;
					currentPageAdjusted = currentPage + (i * pagesPerProcess);

					hit = false;

					// if page exists in table, set hit to true and break
					for (int j = 0; j < frameTable.length; j++) {
						if (frameTable[j] == currentPageAdjusted) {
							hitFrame = j;
							hit = true;
							break;
						}
					}

					if (hit == true) {
						pageArray[hitFrame] = 0;
					}
					// no hit, fault
					else {
						// initially fill page table
						if (framesOccupied++ < totalFrames) {
							int framesLeft = totalFrames - framesOccupied;
							frameTable[framesLeft] = currentPageAdjusted;
							pageArray[framesLeft] = 0;
							residency[framesLeft] = 0;

						}
						// all frames occupied
						else {
							// replacement algorithms here
							int toBeReplaced = 0;
							int arrayIndexToReplace = 0;
							if(Process.R.equals("lru")){

								arrayIndexToReplace = max(pageArray);
								toBeReplaced = frameTable[arrayIndexToReplace];

							}
							else if(Process.R.equals("fifo")){
								arrayIndexToReplace = max(residency);
								toBeReplaced = frameTable[max(residency)];

							}
							else if(Process.R.equals("random")){
								//get next random number from text file
								arrayIndexToReplace = input.nextInt() % frameTable.length;
								toBeReplaced = frameTable[arrayIndexToReplace];

							}
							int process2Index = toBeReplaced / pagesPerProcess;
							Process process2 = processes.get(process2Index);
							process2.incNumEvictions();
							process2.setRunningTotal(residency[arrayIndexToReplace]);
							frameTable[arrayIndexToReplace] = currentPageAdjusted;
							pageArray[arrayIndexToReplace] = 0;
							residency[arrayIndexToReplace] = pageArray[arrayIndexToReplace]; //=0

						}
						// we faulted so increment fault count in process obj
						totalFaults += 1;
						process.incFaults();
					}

					// set next address for the process
					process.setCurrentWord(getNextAddress(process));

					// increment counts in page array
					for (int q = 0; q < pageArray.length; q++) {
						pageArray[q]+=1;
						residency[q]+=1;
					}

					// reference to current process made - decrement remaining references
					process.decN();
				}
				i++;
			}
		}
		return totalFaults;
	}

	/**
	 * initializes our processes arraylist based on job mix
	 * @param job mix 
	 * @param size of a process
	 * @param number of references per process
	 * @return Arraylist of process objects
	 */
	public static void initProcesses(int jobMix, int sizeOfProcess, int numReferencesPerProcess) {
	
		if(jobMix == 1){
			processes.add(new Process(1.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 1));
		}

		else if( jobMix == 2){
			processes.add(new Process(1.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 1));
			processes.add(new Process(1.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 2));
			processes.add(new Process(1.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 3));
			processes.add(new Process(1.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 4));
		}

		else if(jobMix == 3){
			processes.add(new Process(0.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 1));
			processes.add(new Process(0.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 2));
			processes.add(new Process(0.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 3));
			processes.add(new Process(0.0, 0.0, 0.0, sizeOfProcess, numReferencesPerProcess, 4));
		}

		else if(jobMix == 4){
			processes.add(new Process(0.75, 0.25, 0.0, sizeOfProcess, numReferencesPerProcess, 1));
			processes.add(new Process(0.75, 0.0, 0.25, sizeOfProcess, numReferencesPerProcess, 2));
			processes.add(new Process(0.75, 0.125, 0.125, sizeOfProcess, numReferencesPerProcess, 3));
			processes.add(new Process(0.5, 0.125, 0.125, sizeOfProcess, numReferencesPerProcess, 4));
		}
		else{
			System.err.println("Invalid j. Must be between 1-4.");
			System.exit(1);
		}
		
	}

	/**
	 * 
	 * @param process
	 * @param processNum
	 * @return next address for given process
	 * @throws FileNotFoundException
	 */
	public static int getNextAddress(Process process) throws FileNotFoundException {

		int address = 0;

		//get random number in probability form, see instructions for formula given
		double randomNumber = input.nextInt() / (Integer.MAX_VALUE + 1d);
		
		double aTotal = process.getA();
		double aBTotal = aTotal + process.getB();
		double aBCTotal = aBTotal + process.getC();
		
		if (randomNumber < aTotal) {
			address = (process.getCurrentWord() + 1);
			address %= process.getS();
		} else if (randomNumber < aBTotal) {
			address = (process.getCurrentWord() - 5 + process.getS());
			address %= process.getS();
		} else if (randomNumber < aBCTotal) {
			address = (process.getCurrentWord() + 4);
			address = address % process.getS();
		} else {
			//get next random number in file
			int nextRandomNum = input.nextInt();
			address = nextRandomNum % process.getS();
		}

		return address;
	}
	
	/**
	 * Prints summary data for each process
	 * Function also calls printTotalSummary function to thereafter print final line
	 * @param processes
	 * @param totalFaults
	 */
	
	public static void printProcessSummary(ArrayList<Process> processes, int totalFaults) {

		double avgResidency = 1.0;
		int totalRunning = 0;
		int totalEvictions = 0;

		System.out.println();
		
		for (int i = 0; i < processes.size(); i ++) {
			int numEvictions = processes.get(i).getNumEvictions();
			int numFaults = processes.get(i).getNumFaults();
			if(numEvictions == 0){
				System.out.println("Process " + processes.get(i).getId() + " had "+ numFaults +" faults. \n\tWith no evictions, the average residence is undefined.");
				System.out.println();
			}
			else{
				avgResidency = processes.get(i).getRunningTotal() / (1.0 * numEvictions);
				System.out.println("Process "+ processes.get(i).getId() + " had "+ numFaults +" faults and "+ nf.format(avgResidency) +" average residency.");
				System.out.println();
				totalEvictions = totalEvictions + numEvictions;
				totalRunning = totalRunning + processes.get(i).getRunningTotal();
			}
		}
		
		printTotalSummary(totalRunning,totalEvictions,totalFaults); 
		
	}
	
	/**
	 * Prints summary (overall avg)
	 * @param totalRunning
	 * @param totalEvictions
	 * @param totalFaults
	 */
	public static void printTotalSummary(double totalRunning, int totalEvictions, int totalFaults) {
		if(totalEvictions == 0){
			System.out.println();
			System.out.println(
					"The total number of faults is " + totalFaults + ". \n\tWith no evictions, the average residency is undefined.");
		}
		else{
			double overallAverageResidency = totalRunning / (double) totalEvictions;
			System.out.println();
			System.out.println("The total number of faults is " + totalFaults + " and the overall average residency is "+nf.format(overallAverageResidency)+"\n\n");
		}
		
	}

	/**
	 * back to user the data that was entered from command line to
	 *  let them know what was entered prior to returning results
	 */
	public static void printPreliminaryData() {

		System.out.println("The machine size is " +  Process.M + ".");
		System.out.println("The page size is "+ Process.P +".");
		System.out.println("The process size is "+ Process.S +".");
		System.out.println("The job mix number is "+ Process.J+".");
		System.out.println("The number of references per process is "+ Process.N+".");
		System.out.println("The replacement algorithm is "+ Process.R+".");
		System.out.println("The level of debugging output is "+ 0 +".");
	}

	
	/**
	 * Gets the max of a given array
	 * @param arr
	 * @return
	 */
	public static int max(int[] arr) {
		int max = 0;
		int index = 0;
		for (int a: arr) {
			if (a > arr[max]) {
				max = index;
			}
			index++;
		}

		return max;

	}
}

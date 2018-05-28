public class Process{
	
	private double A;
	private double B;
	private double C;
	
	private int n;
	private int s;
	
	private int numEvictions;
	private int numFaults;
	private int runningTotal;
	private int currentWord;
	
	private int id; 
	
	//Class variables applicable to all Process objects
	static int M;
	static int P;
	static int S;
	static int J;
	static int N;
	static String R;
	
	private String replacementAlgorithm;
	
	public Process(){}
	
	public Process(double A, double B, double C, int s, int n, int id){
		//defaults
		this.numFaults = 0;
		this.runningTotal = 0;
		this.numEvictions = 0;
		
		this.A = A;
		this.B = B;
		this.C = C;
		
		this.s = s;
		this.n = n;
		
		this.currentWord = (111 * id) % s;
		this.id = id;
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getA(){
		return A;
	}
	
	public double getB(){
		return B;
	}
	public void decN(){
		n--;
	}
	
	public int getN(){
		return n;
	}
		
	public double getC(){
		return C;
	}
	
	public int getS(){
		return s;
	}
	
	public int getNumFaults(){
		return numFaults;
	}
	
	public int getCurrentWord(){
		return currentWord;
	}
	
	public void setCurrentWord(int curWord){
		this.currentWord = curWord;
	}
	
	public void incFaults(){
		numFaults++;
	}
	public int getRunningTotal(){
		return runningTotal;
	}
	
	public void setRunningTotal(int add){
		runningTotal += add;
	}
	public int getNumEvictions(){
		return numEvictions;
	}
	
	public void incNumEvictions(){
		this.numEvictions = numEvictions + 1;
	}
	
	
}
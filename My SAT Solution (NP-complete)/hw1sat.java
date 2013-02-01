
/**
* SAT problem, Algorithm: WalkSAT
*  Author: 
*  Yang Song (28243494) 
*  UC IRVINE, Bren School of ICS
*  
*  Example:(a | b ) & (!a | !b)
*  
*  Input format:              Output format:                         Output format:      
*  c This is a comment        c Solution for previous problem        c Solution for previous problem
*	p cnf 2 2                  s SATISFIABLE                          s UNSATISFIABLE
*	1 2 0                      v 1 -2 0
*	-1 -2 0
*/


import java.util.*;
import java.io.*;

public class hw1sat {
	// input file name, probability of RandomWork, maxTry time
	static String filename = "CNF.txt";
	static double probabilityOfRandomWalk = 0.5;  // probabilityOfRandomWalk: stochastic noise, avoid "flip" in local optimum
	static int maxTryTime = 10000;
	// number of variables and clauses in Integer & String form
	static String numberOfClausesInString = null;
	static int numberOfClausesInInt = 0;
	static String numberOfVariablesInString = null;
	static int numberOfVariablesInInt = 0;
	//
	static int[]variables = null;    // store each variables
	static String[]clauses = null;   // store each clauses

	public static void main (String[] args){  // main method, algorithm begin
		// process input file
		BufferedReader rd = null;
		StringTokenizer tokenizer = null;
		String secondLine = null; // second line include number of variables & number of clauses
		String eachLine = null;   // read each line in file one by one

		Model myModel = null;     // Model's object

		try{
			rd = new BufferedReader(new FileReader(filename));
			for (int i=0; i<2; i++){
				secondLine = rd.readLine();    // variable "secondLine" contains number of variables & number of clauses
				tokenizer = new StringTokenizer(secondLine); // StringTokenizer begin from second line
			}

			for (int i =0; i<3; i++){      //  iterate second line No.1 --> No.3 
				numberOfVariablesInString = tokenizer.nextToken();  // third token in secondLine, number of variables(String format)
			}

			numberOfVariablesInInt = Integer.parseInt(numberOfVariablesInString); // convert to integer
			variables = new int[numberOfVariablesInInt*2+1];   // array "variables" have [number of variables * 2 + 1] space

			for (int i =1; i<=numberOfVariablesInInt; i++){    // iterate from 1 --> number of variables
				variables[i]=i;       // store variables from 1 --> number of variables space 
				variables[i+numberOfVariablesInInt]= -i;  // store negations of corresponding variables from number of variables+1 --> number of variables * 2
			}

			myModel = new Model();

			for (int i =1;i<=numberOfVariablesInInt;i++){
				int passInPlus = variables[i];
				int passInMinus = variables[i+numberOfVariablesInInt];
				Boolean b = randomBoolean();    // create a random value for each variable
				myModel = myModel.extend(passInPlus, b); // input each variable with random boolean value, store them in Model
				myModel = myModel.extend(passInMinus, !b); // input each negations of corresponding variable with corresponding !boolean value, store them in Model
			}   // HERE, each variable with their negation has been given random boolean and stored in Model(HashMap)

			numberOfClausesInString = tokenizer.nextToken();   // number of clauses, forth token in "secondLine"
			numberOfClausesInInt = Integer.parseInt(numberOfClausesInString);  // convert to integer
			clauses = new String[numberOfClausesInInt+1];  // array "clauses" have [number of clauses + 1] space

			for (int i =1; i<=numberOfClausesInInt; i++){
				eachLine = rd.readLine(); // iterate "eachLine" from third --> final lines in input file
				clauses[i] = eachLine;    // store each clause in array "clauses"
			} // HERE, each clause has been stored in array "clauses"
		}
		catch (IOException ex){
			System.out.println("no file!");
		}

		arrayOfIndex index = new arrayOfIndex(); // create an object of class: arrayOfIndex	
		int maxClausesSatisfied = index.storeSatis(clauses, myModel).get((index.storeSatis(clauses, myModel)).size()-1); // last element in storeSatis ArrayList


		/** JUDGE: if first time random assignment is satisfiable */
		if (maxClausesSatisfied == numberOfClausesInInt){
			System.out.println("c Solution for previous problem");  // output result
			System.out.println("s SATISFIABLE");
			System.out.print("v");
			for (int i =1; i<=numberOfVariablesInInt; i++){
				boolean b = myModel.getValue(i, myModel);
				if (b == true){
					System.out.print(" "+i);
				}
				else {
					System.out.print(" -"+i);
				}
			}
			System.out.print(" 0");	
		} // HERE, condition 1 is finished


		/** JUDGE: first time random assignment is NOT satisfiable */
		else {
			Flag:
				for (int k=0; k<maxTryTime; k++){
					Random r = new Random();

					if (r.nextDouble()<=probabilityOfRandomWalk ){  // if <probabilityOfRandomWalk, PROCEED randomWork

						int oneNumOfUnsatisClauses = r.nextInt(index.storeUnsatis(clauses, myModel).get((index.storeUnsatis(clauses, myModel)).size()-1)); // choose a random clause's index from the Unsatis clauses
						int indexOfRandomWork = index.storeUnsatis(clauses, myModel).get(oneNumOfUnsatisClauses); // the unsatisfiable clause's index in array
						String randomClause = clauses[indexOfRandomWork];  // get a random clause (remember in mind: +1)
						StringTokenizer str = new StringTokenizer(randomClause); // divide random clause into token
						String s = str.nextToken(); // s = first token
						ArrayList<Integer> list = new ArrayList<Integer>(); // store each variables from random clause into ArrayList
						int counter = 0; // count how many variables the random clause have
						while ((Integer.parseInt(s))!=0){ // put all the variables except 0 into ArrayList: list
							list.add(Integer.parseInt(s));
							counter +=1;
							s=str.nextToken();
						}
						int randomVariable = list.get(r.nextInt(counter)); // return a random variable from the random clause
						myModel = myModel.flip(randomVariable, myModel);
						maxClausesSatisfied = index.storeSatis(clauses, myModel).get((index.storeSatis(clauses, myModel)).size()-1); // update maxClausesSatisfied

						// TEST: maxClausesSatisfied after random work
						System.out.println("Random--Work change MAX: " + maxClausesSatisfied + " / "+ numberOfClausesInInt);
						System.out.println();

						if (maxClausesSatisfied == numberOfClausesInInt){	
							System.out.println("c Solution for previous problem");
							System.out.println("s SATISFIABLE");
							System.out.print("v");
							for (int j =1; j<=numberOfVariablesInInt; j++){
								boolean b = myModel.getValue(j, myModel);
								if (b == true){
									System.out.print(" "+j);
								}
								else {
									System.out.print(" -"+j);
								}
							}
							System.out.println(" 0");
							break Flag;
						}
					}


					/**PROCEED maxClausesSatis, probability of maxClausesSatis = 1 - probabilityOfRandomWalk*/
					else {

						Model m = new Model();
						for (int i=1; i<=numberOfVariablesInInt; i++){ // iterate each variable
							m = myModel.flip(i, myModel); // try to flip each variable this time 
							if(index.storeSatis(clauses, m).get((index.storeSatis(clauses, m)).size()-1) > maxClausesSatisfied){
								myModel = m;
								maxClausesSatisfied = index.storeSatis(clauses, myModel).get((index.storeSatis(clauses, myModel)).size()-1);
    
								// TEST
								System.out.println("Flip Raise MAX: " + maxClausesSatisfied + " / "+ numberOfClausesInInt);
								System.out.println();

								if (maxClausesSatisfied == numberOfClausesInInt){	
									System.out.println("c Solution for previous problem");
									System.out.println("s SATISFIABLE");
									System.out.print("v");
									for (int j =1; j<=numberOfVariablesInInt; j++){
										boolean b = myModel.getValue(j, myModel);
										if (b == true){
											System.out.print(" "+j);
										}
										else {
											System.out.print(" -"+j);
										}
									}
									System.out.println(" 0");
									break Flag;
								}
							}
						} // for (flip)
					}
				} // for circle ended (maxTryTime)
		if (maxClausesSatisfied < numberOfClausesInInt){
			System.out.println("c Solution for previous problem");
			System.out.println("s UNSATISFIABLE");
		}
		} // First time random assignment is NOT successful (else)
	}  
	/** main method end here*/


	// private method, return a random boolean value
	private static boolean randomBoolean() {
		Random _r = new Random();
		int trueOrFalse = _r.nextInt(2);
		return (!(trueOrFalse == 0));
	}


	/** private static class "arrayOfIndex"
	 *  store satis and unsatis clauses' index into ArrayList
	 */
	private static class arrayOfIndex {

		// private method
		private ArrayList<Integer> storeSatis(String[]string, Model model) {	
			ArrayList<Integer> satis = new ArrayList<Integer>(); // store every satisfiable clauses' index
			int numOfSatisClauses = 0;
			for (int i=1; i<=numberOfClausesInInt; i++){ // iterate every clauses
				StringTokenizer token = new StringTokenizer(string[i]);
				String s = token.nextToken(); // here s is the first token
				while ((Integer.parseInt(s))!=0){
					if (model.getValue((Integer.parseInt(s)), model) == true){
						satis.add(i); // clauses[i] is true, add the clause's index in Array to ArrayList "satis" (ArrayList begin from 0)
						numOfSatisClauses +=1; // number of satisfiable clauses + 1
						break;
					}
					else {
						s = token.nextToken();  // try next token
					}
				}//while
			} //for
			satis.add(numOfSatisClauses);  // number of satisfiable clauses store in the last element in ArrayList
			return satis;
		}

		// private method
		private ArrayList<Integer> storeUnsatis(String[]string, Model model){	
			ArrayList<Integer> unSatis = new ArrayList<Integer>(); // store every unsatisfiable clauses' index
			int numOfUnsatisClauses = 0;
			for (int i=1; i<=numberOfClausesInInt; i++){ // iterate every clauses
				StringTokenizer token = new StringTokenizer(string[i]);
				String s = token.nextToken(); // here s is the first token
				while ((Integer.parseInt(s))!=0){
					if (model.getValue((Integer.parseInt(s)), model) == true){
						break;
					}
					else {
						s = token.nextToken();  // try next token
					}
				}//while
				if ((Integer.parseInt(s)==0)){
					unSatis.add(i); // cannot find one variable in clauses[i] is true, clauses[i] is unSatis, add it to "unSatis" ArrayList
					numOfUnsatisClauses +=1;
				}
			} //for
			unSatis.add(numOfUnsatisClauses);  // number of unsatisfiable clauses store in the last element in ArrayList
			return unSatis; 
		}
	}// class: arrayOfIndex


	/** private static class "Model"
	 *  implement a model contains HashMap with method which could update key's value without duplication 
	 */
	private static class Model {
		// build HashMap, when initialized, Model's object has a HashMap
		private HashMap<Integer, Boolean> h = new HashMap<Integer, Boolean>();
		// method: add new key&value to Model to replace the old one
		private Model extend (int s, boolean b) {
			Model m = new Model();
			m.h.putAll(this.h);
			m.h.put(s, b);
			return m;
		}
		// method: return model's key's value 
		private Boolean getValue (int s, Model model) {
			Boolean b = model.h.get(s);
			return b;
		}
		// method: flip
		private Model flip (int s, Model model){
			boolean b = model.getValue(s, model);
			boolean k = model.getValue(-s, model);
			model = model.extend(s, !b);
			model = model.extend(-s, !k);
			return model;
		}
	}
}// hw1sat class end










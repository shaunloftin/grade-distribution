import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.math3.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;;

/**
 * 
 * This program takes an inputed excel sheet of grade distributions 
 * over multiple semesters and outputs the pass rate of each class.
 * 
 * Heavily relies on use of Apache POI library in order to read
 * Excel spreadsheet data.
 * 
 * @author Shaun Loftin
 * @author Landen Master
 *
 */

public class GradeDistribution {
	
	// Constants for relevant data references for easy access
	public static final String MASTER_SHEET = "data/OSU_Grade_Distributions_SU18_AU19.xlsx";
	public static final String EASY_GE_EXPORT = "data/easy-ge-export.txt";
	public static final String CLASS_TRENDS_EXPORT = "data/class-trends-export.txt";
	
	public static Double MAX_COURSE_LEVEL = 2999.0;
	public static Double MIN_ENROLLMENT = 30.0;
	public static boolean IS_SUMMER = false;
	public static boolean TECH_COURSE = false;
	
	/**
     * Explanation of what each column in the excel sheet represents:
     * 
     * COLUMN 0 - Term Semester (ex. AU 2018)
     * COLUMN 1 - Term Code (ex. 1188)
     * COLUMN 2 - Subject Code (ex. CSE)
     * COLUMN 3 - Catalog Number (ex. 2221)
     * COLUMN 4 - # of students with A
     * COLUMN 5 - # of students with A-
     * COLUMN 6 - # of students with B+
     * COLUMN 7 - # of students with B
     * COLUMN 8 - # of students with B-
     * COLUMN 9 - # of students with C+
     * COLUMN 10 - # of students with C
     * COLUMN 11 - # of students with C-
     * COLUMN 12 - # of students with D+
     * COLUMN 13 - # of students with D
     * COLUMN 14 - # of students with E
     * COLUMN 15 - # of students with PA
     * COLUMN 16 - # of students with NP
     * COLUMN 17 - # of students with S
     * COLUMN 18 - # of students with U
     * COLUMN 19 - Total # of students enrolled in the class 
     */

	/**
	 * Main method to prompt user input and switch to relevant user option.
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		XSSFWorkbook gradeBook = new XSSFWorkbook(new FileInputStream(MASTER_SHEET)); // Copying the excel workbook into a local variable
        XSSFSheet mainSheet = gradeBook.getSheet("Sheet1"); // Copying the first excel sheet into a local variable
        Scanner keyboard = new Scanner(System.in); // Initializes scanner for user input
        String option = "";
        
        // Continuously prompts user until null input is given
        while (option != null) {
        	
            // Prompts user to select option, provides option list
            System.out.println("1 - Search by entire department\n"
            		+ "2 - Search by specific course\n"
            		+ "3 - Print lowest pass rate classes\n"
            		+ "4 - Export list of easiest GEs\n"
            		+ "8 - Clear console\n"
            		+ "9 - Modify data filters");
            System.out.print("Select your option (blank to quit): ");
            option = keyboard.nextLine();
            
        	// Calls specific user option method based off of input
	        switch (option) {
		        case "1":
		        	searchByDept(mainSheet, keyboard);
		        	break;
		        case "2":
		        	searchByCourse(mainSheet, keyboard);
		        	break;
		        case "3":
		        	printLowestPassRate(mainSheet, keyboard);
		        	break;
		        case "4":
		        	// TODO - i'm doing this tmrw 
		        	// exportGEList(mainSheet);
		        	System.out.println("Oops! I still need to do this!");
		        	break;
		        case "8":
		        	for (int i = 0; i < 30; i++) {
		        		System.out.println("\n");
		        	}
		        	break;
		        case "9":
		        	// TODO - calculate downward trends
		        	modifyDataFilters(keyboard);
		        	break;
		        case "":
		        	option = null; //sets option to null to exit loop
		        	break;
		        default:
		        	// Prints error message when user doesn't enter 1-5.
		        	System.err.println("ERROR: Invalid input entered.");
	        	}
        }
	    
        // Prints a gentle goodbye
        System.out.println("\nGoodbye!");
        
        // Closes input excel stream and Scanner stream
        keyboard.close();
		gradeBook.close();
    }

	/////////////////////////
    // USER OPTION METHODS //
    /////////////////////////
	
    /**
     * Prints out all data related to a specific course, sorted by lowest pass rate.
     * 
     * @param mainSheet
     * 		instance of Excel sheet passed by main method
     * @param keyboard
     * 		Scanner for user input
     * @throws IOException
     * 		throws exception if error is reached reading excel sheet
     */
    private static void searchByDept(XSSFSheet mainSheet, Scanner keyboard) throws IOException {
    	XSSFRow currentRow; // Tracks the current row being read
    	HashMap<String, Double> data = new HashMap<String, Double>(); // Map used for data entry
    	
    	// Prompts user to enter department for filtering
    	System.out.print("What is the abbreviation of the department you are wanting to search? ");
    	String dept = keyboard.nextLine();
    	
    	// Iterates through entire sheet
        for (int i = 0; i < mainSheet.getLastRowNum(); i++) {
        	currentRow = mainSheet.getRow(i);
        	// Checks to see if current row matches entered dept and testing conditions
        	if(currentRow.getCell(2).toString().equals(dept)
        			&& meetsTestingConditions(currentRow)){
        		// If so, the course info is entered into map
        		data.put(concatCourseInfo(currentRow), findPassRate(currentRow));
            }
        }
        
        printSearch(data, keyboard, "passRates-" + dept, 0);
        
    }
    
    /**
     * Prints out all data related to a specific course, sorted by lowest pass rate.
     * 
     * @param mainSheet
     * 		instance of Excel sheet passed by main method
     * @param keyboard
     * 		Scanner for user input
     * @throws IOException
     * 		throws exception if error is reached reading excel sheet
     */
    private static void searchByCourse(XSSFSheet mainSheet, Scanner keyboard) throws IOException {
    	XSSFRow currentRow; //Tracks the current row being read
    	HashMap<String, Double> data = new HashMap<String, Double>(); // Map used for data entry
    	
    	// Prompts user to enter dept and course # for filtering
    	System.out.print("What is the abbreviation of the department you are wanting to search? ");
    	String dept = keyboard.nextLine();
    	System.out.print("What is the course number you are wanting to search? ");
    	String courseNum = keyboard.nextLine();
    	
    	// Iterates through entire sheet
        for (int i = 0; i < mainSheet.getLastRowNum(); i++) {
        	currentRow = mainSheet.getRow(i);
        	// Checks to see if current row matches entered dept, course #, and testing conditions
        	if(currentRow.getCell(2).toString().equals(dept) 
        			&& currentRow.getCell(3).toString().equals(courseNum)
        			&& meetsTestingConditions(currentRow)){
        		// If so, the course info is entered into map
                data.put(concatCourseInfo(currentRow), findPassRate(currentRow));
            }
        }
        
        printSearch(data, keyboard, "passRates-" + dept + courseNum, 0);
    	
    }
    
    /**
     * Prints out {@code numEntered} courses with the lowest pass rates.
     * 
     * @param mainSheet
     * 		instance of Excel sheet passed by main method
     * @param keyboard
     * 		Scanner for user input
     * @throws IOException
     * 		throws exception if error is reached reading excel sheet
     */
    private static void printLowestPassRate(XSSFSheet mainSheet, Scanner keyboard) throws IOException {
    	XSSFRow currentRow; //Tracks the current row being read
    	HashMap<String, Double> data = new HashMap<String, Double>(); // Map that stores course information and pass rate
    	int numOfCourses = mainSheet.getLastRowNum(); // Stores max amount of rows to be printed
    	
    	// Ask user how many most failed courses they would like to see
    	System.out.print("There are " + numOfCourses + " rows of course data. How many rows would you like to see? ");
    	
    	// Validates user input through try/catch
    	int numEntered;
    	try {
    		numEntered = Integer.parseInt(keyboard.nextLine());
    		while (numEntered < 1) {
    			System.err.println("ERROR: Enter a number greater than 1.");
    			System.out.print("There are " + numOfCourses + " rows of course data. How many rows would you like to see? ");
    	    	numEntered = Integer.parseInt(keyboard.nextLine());
    		}
    		if (numEntered > numOfCourses) {
    			numEntered = numOfCourses;
    		}
    	} catch (NumberFormatException e) {
    		// If user enters invalid number, prints default of top 50 most failed courses
    		numEntered = 50;
    		System.err.println("ERROR: Can't read inputed number. Showing top 50 courses with lowest pass rates.");
    	}
    	
    	// Iterates through entire sheet
    	for (int i = 1; i < mainSheet.getLastRowNum(); i++) {
        	currentRow = mainSheet.getRow(i);
        	// Checks to see if current row meets testing conditions
        	if (meetsTestingConditions(currentRow)) { 
        		// If so, data is inserted into map
        		data.put(concatCourseInfo(currentRow), findPassRate(currentRow)); 
        	}
    	}
    	
    	printSearch(data, keyboard, "lowestPassRates-top" + numEntered, numEntered);
    	
    }
    
    /**
     *  TODO - i'm doing this..eventually
     * @param mainSheet
     */
    private static void exportGEList(XSSFSheet mainSheet) {
    	// XSSFRow currentRow; //Tracks the current row being read
    }
    
    /**
     * TODO - write javadoc
     * TODO - fix bug with technical courses
     * @param keyboard
     */
    private static void modifyDataFilters(Scanner keyboard) {
    	System.out.println("The current data filters are as follows: ");
    	System.out.println("The maximum level course is: " + MAX_COURSE_LEVEL);
    	System.out.println("The minimum enrollment amount is: " + MIN_ENROLLMENT);
    	System.out.println("Summer courses are considered: " + IS_SUMMER);
    	System.out.println("Technical courses are considered: " + TECH_COURSE + "\n");
    	
    	String input;
    	// TODO - remove this
    	System.err.println("WARNING: Technical course filter doesn't work rn");
    	System.out.print("Please enter the new maxmium level course: ");
    	MAX_COURSE_LEVEL = Double.parseDouble(keyboard.nextLine());
    	System.out.print("Please enter the new minimum enrollment: ");
    	MIN_ENROLLMENT = Double.parseDouble(keyboard.nextLine());
    	System.out.print("Please enter whether to consider summer courses (T/F): ");
    	input = keyboard.nextLine();
    	if (input.equals("T") || input.equals("t")) {
    		IS_SUMMER = true;
    	} else {
    		IS_SUMMER = false;
    	}
    	System.out.print("Please enter whether to consider technical courses (T/F): ");
    	input = keyboard.nextLine();
    	if (input.equals("T") || input.equals("t")) {
    		TECH_COURSE = true;
    	} else {
    		TECH_COURSE = false;
    	}
    	
    	System.out.println("Successfully modified data filters. \n");
    	
    }
   
  
    
    ////////////////////////////////////////
    // VARIOUS HELPER/CALCULATION METHODS //
    ////////////////////////////////////////
    
    /**
     * TODO - write this
     * @param data
     * @param keyboard
     * @param contxt
     * @param numToPrint
     */
    private static void printSearch(HashMap<String, Double> data, Scanner keyboard, String contxt, int numToPrint) {
    	List<Entry<String, Double>> sortedData = entriesSortedByValues(data);
    	System.out.println("Would you like to print to console or file?");
    	System.out.print("Enter 1 for console, 2 for file: ");
    	String input = keyboard.nextLine();
    	
    	while (!input.equals("1") && !input.equals("2")) {
    		System.err.println("ERROR: Expected input of 1 or 2.");
    		System.out.print("Enter 1 for console, 2 for file: ");
        	input = keyboard.nextLine();
    	}
    	
    	if (input.equals("1")) {
    		if (numToPrint == 0) {
				for (Map.Entry<String, Double> x : sortedData) {
		        	System.out.println(x.getKey() + " Pass Rate: " + x.getValue() + " %");
		        }
			} else {
				for (int i = 0; i < numToPrint; i++) {
		    		System.out.println(sortedData.get(i).getKey() + " Pass Rate: " + sortedData.get(i).getValue() + " %");
		    	}
			}
    	} else {
    		String fileName = "data/exports/" + contxt + ".txt";
        	try {
    			BufferedWriter fileOut =  new BufferedWriter(new FileWriter(fileName));
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
    			Date date = new Date(System.currentTimeMillis());
    			fileOut.write("##### OUTPUT FILE " + contxt + ".txt - REQUESTED ON: " + date.toString() + " #####\n\n");
    			fileOut.write("##### DATA FILTERS #####\n");
    			fileOut.write("MAXIUMUM COURSE LEVEL: " + MAX_COURSE_LEVEL + "\n");
    			fileOut.write("MINIMUM ENROLLMENT AMOUNT: " + MIN_ENROLLMENT + "\n");
    			fileOut.write("SUMMER COURSES CONSIDERED: " + IS_SUMMER + "\n");
    			fileOut.write("TECHNICAL COURSES CONSIERED: " + TECH_COURSE + "\n");
    			fileOut.write("##### END DATA FILTERS #####\n\n");
    			if (numToPrint == 0) {
    				for (Map.Entry<String, Double> x : sortedData) {
    		        	fileOut.write(x.getKey() + " Pass Rate: " + x.getValue() + " %\n");
    		        }
    			} else {
    				for (int i = 0; i < numToPrint; i++) {
    					fileOut.write(sortedData.get(i).getKey() + " Pass Rate: " + sortedData.get(i).getValue() + " %\n");
    		    	}
    			}
    			fileOut.close();
    			System.out.println("Successfully printed to " + fileName + "\n");
    		} catch (IOException e) {
    			System.err.println("Error printing to file: " + fileName);
    			System.err.println(e);
    		}
    	}
    	
    }
    
    /**
     * Filters for specific course information by returning true/false 
     * based on whether specific testing conditions or met.
     * 
     * @param currentRow
     * 		the current course information being read
     * @return boolean value
     * 		either true or false based off
     */		
    private static boolean meetsTestingConditions(XSSFRow currentRow) {
    	
    	// Checks to see if the course number is less than constant (only undergraduate courses)
    	boolean maxLevelCheck = true;
    	final Double maxLevelCourse = 2999.0;
    	if (Double.parseDouble(currentRow.getCell(3).toString().substring(0,3)) > maxLevelCourse) {
    		maxLevelCheck = false;
    	}
    		
    	// Checks to see if minimum enrollment number is met (at least 30 students)
    	boolean minEnrollmentCheck = true;
    	final Double minNumEnrollment = 30.0;
    	if (Double.parseDouble(currentRow.getCell(19).toString()) < minNumEnrollment) {
    		minEnrollmentCheck = false;
    	}
    	
    	// Checks to see if it is a summer course
    	boolean isSummerCheck = false;
    	if (currentRow.getCell(0).toString().substring(0,2).equals("SU")) {
    		isSummerCheck = true;
    	}
    	
    	if (IS_SUMMER == true && isSummerCheck == true) {
    		isSummerCheck = false;
    	}
    	
    	// Checks to see if it isn't a technical course (ex. 4232T)
    	boolean techCourseCheck = false;
    	if (currentRow.getCell(3).toString().indexOf('T') != -1) {
    		techCourseCheck = true;
    	}
    	
    	if (TECH_COURSE == true && techCourseCheck == true) {
    		isSummerCheck = false;
    	}
    	
    	return maxLevelCheck && minEnrollmentCheck && !isSummerCheck && !techCourseCheck;
    	
    }
    
    /**
     * Concatenates the semester, department, and course number
     * for brevity in other methods.
     * 
     * @param currentRow
     * 			the current row in iteration
     * @return String
     * 			concatenated semester/dept/course number
     */
    private static String concatCourseInfo(XSSFRow currentRow) {
    	return currentRow.getCell(0).toString() + " " + currentRow.getCell(2).toString() + " " 
    			+ currentRow.getCell(3).toString();
    }
    
    /**
     * Calculates the pass rate of a given class.
     * 
     * @param currentCourse
     * 			current class being evaluates
     * @return
     * 			The class passing rate, in percent
     */
    private static double findPassRate(XSSFRow currentCourse) throws IOException {
    	// finds the total amount of students enrolled in the current course
    	double totalEnrolled = parseDoubleNull(currentCourse.getCell(19).toString());
    	
    	/* finds the total amount of students that failed the course 
    	in order to find the number of students who passed the course */
    	double failedStudents = parseDoubleNull(currentCourse.getCell(12).toString()) + 
    			parseDoubleNull(currentCourse.getCell(13).toString()) + 
    			parseDoubleNull(currentCourse.getCell(14).toString());
    	
    	// calculates the number of students that passed in the course
    	double passRate = (totalEnrolled - failedStudents)/totalEnrolled; 
    	
		return 100*passRate; //returns the percentage of students that passed the class
    }
    
    /**
     * Parsing function for doubles that returns a 0 if a null value is found
     * 
     * @param inputtedNumber
     * 			the String to Parse
     * @return
     * 			[x as a double] || 0
     */
    private static Double parseDoubleNull(String inputedNumber) {
    	// if the inputed number is an empty string or Null, returns a 0
    	if (inputedNumber.equals(null) || inputedNumber.equals("") 
    			|| inputedNumber.equals(" ")) {
    		return 0.0;
    	// if the inputed number is not null, returns the parsed value
    	} else {
    		return Double.parseDouble(inputedNumber);
    	}
    }
    
    /**
     * Helper function to sort sets based on their values
     * 
     * @param map
     * 			inputed map whose values must be ordered 
     * 			from greatest to least
     * @return
     * 			list of Entries sorted from greatest 
     * 			values to least values
     */
    static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
    	// Creates new list for sorted values to go into
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		// Entries are sorted in ascending order
		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		        	// Swap e1 and e2 in line below to switch ascending/descending
		            return e1.getValue().compareTo(e2.getValue());
		        }
		    }
		);
		return sortedEntries;
	}
    
    //////////////////////
    // DEPRECIATED CODE //
    //////////////////////
    
   /*
   private static void exportDownwardTrends(XSSFSheet mainSheet) throws IOException {
   	long startTime = System.currentTimeMillis();
   	
   	XSSFRow currentRow; // Tracks the current row being read
   	HashMap<String, Double> data = new HashMap<String, Double>(); // Map that stores course information and pass rate
   	HashMap<String, Double> trends = new HashMap<String, Double>();
   	
   	// Iterates through entire sheet
   	for (int i = 1; i < mainSheet.getLastRowNum(); i++) {
       	currentRow = mainSheet.getRow(i);
       	// Checks to see if current row meets testing conditions
       	if (meetsTestingConditions(currentRow)) { 
       		// If so, data is inserted into map
       		data.put(concatCourseInfo(currentRow), findPassRate(currentRow)); 
       	}
   	}
   	
   	BufferedWriter fileOut =  new BufferedWriter(new FileWriter(CLASS_TRENDS_EXPORT));
   	
   	
   	// Iterates through data to get a list of unique course names
   	for (Map.Entry<String, Double> current : data.entrySet()) {
   		String courseName = current.getKey().substring(8); // removes semester info
   		if (!trends.containsKey(courseName)) {
   			List<Double> passRates = new ArrayList<Double>();
   			for (Map.Entry<String, Double> dataSearch : data.entrySet()) {
   				if (dataSearch.getKey().contains(courseName)) {
   					passRates.add(dataSearch.getValue());
   				}
   			}
   			System.out.println("this ran");
   			trends.put(courseName, approxTrend(passRates));
   		}
   	}
   	
   	// Map is then sorted ascending pass rates, then printed with easy readability
       List<Entry<String, Double>> sortedData = entriesSortedByValues(trends);
       for (Map.Entry<String, Double> x : sortedData) {
       	fileOut.write(x.getKey() + " - Pass Rate Trend: " + x.getValue() + "\n");
       }
   
   	fileOut.close();
   	
   	long endTime = System.currentTimeMillis();
   	System.out.println("\nDone! Exported to: " + CLASS_TRENDS_EXPORT + " in " + (endTime - startTime) + " milliseconds.\n");
	}
   
   //////////////////////////////////
   // STATISTICAL ANALYSIS METHODS //
   //////////////////////////////////
   
   public static double approxTrend(List<Double> passRates) { 
   	int n = passRates.size();
 
   	if (n > 1) {
	    	double[] x = new double[n];
	    	for (int i = 0; i < n; i++) {
	    		x[i] = Double.valueOf(i+1);
	    	}
	    	
	    	double[] y = new double[n];
	    	for (int i = 0; i < n; i++) {
	    		y[i] = passRates.get(0);
	    	}
	    	
	    	double[][] data = new double[2][n];
	    	for (int i = 0; i < n; i++) {
	    		data[0][i] = i;
	    		data[1][i] = passRates.get(i);
	    	}
	    	
	    	System.out.println("this ran");
	    	
	    	SimpleRegression regression = new SimpleRegression();
	    	regression.addData(data);
	    	return regression.getSlope();
   	} else {
   		return 1.0;
   	}
   	*/
   	        
   } 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	public static final String EASY_GE_EXPORT = "data/easy-ge-list.txt";
	
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
        
        // Prompts user to select option, provides option list
        System.out.println("1 - Search by entire department\n"
        		+ "2 - Search by specific course\n"
        		+ "3 - Print lowest pass rate classes\n"
        		+ "4 - Export list of easiest GEs");
        System.out.print("Select your option (blank to quit): ");
        String option = keyboard.nextLine();
        
        // Continuously prompts user until null input is given
        while (option != null) {
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
		        	exportGEList(mainSheet);
		        	break;
		        case "":
		        	//TODO: actually break out of this loop
		        	break;
		        // next, i want to calculate downward trends of pass rate for courses
		        default:
		        	// Prints error message when user doesn't enter 1-4.
		        	System.err.println("ERROR: Invalid input entered.");
	        }
	        System.out.println();
	        System.out.println("1 - Search by entire department\n"
	        		+ "2 - Search by specific course\n"
	        		+ "3 - Print lowest pass rate classes\n"
	        		+ "4 - Export list of easiest GEs");
	        System.out.print("Select your option (blank to quit): ");
	        option = keyboard.nextLine();
        }
	    
        // Prints a gentle goodbye
        System.out.println("\nDone!");
        
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
        
        // Map is then sorted ascending pass rates, then printed with easy readability
        List<Entry<String, Double>> sortedData = entriesSortedByValues(data);
        for (Map.Entry<String, Double> x : sortedData) {
        	System.out.println(x.getKey() + " Pass Rate: " + x.getValue() + " %");
        }
        
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
        
        // Map is then sorted ascending pass rates, then printed with easy readability
        List<Entry<String, Double>> sortedData = entriesSortedByValues(data);
        for (Map.Entry<String, Double> x : sortedData) {
        	System.out.println(x.getKey() + " Pass Rate: " + x.getValue() + " %");
        }
    	
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
    	System.out.println("There are " + numOfCourses + " rows of course data. How many rows would you like to see? ");
    	String numEnteredStr = keyboard.nextLine();
    	
    	// Validates user input through try/catch
    	int numEntered;
    	try {
    		numEntered = Integer.parseInt(numEnteredStr);
    		if (numEntered > numOfCourses) {
    			numEntered = numOfCourses;
    		}
    		System.out.println("Showing top " + numEntered + " courses with lowest pass rates.");
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
    	
    	// Map is then sorted ascending pass rates, then printed with easy readability
    	List<Entry<String, Double>> sortedData = entriesSortedByValues(data);
    	for (int i = 0; i < numEntered; i++) {
    		System.out.println(sortedData.get(i).getKey() + " Pass Rate: " + sortedData.get(i).getValue() + " %");
    	}
    	
    }
    
    // TODO - i'm doing this tmrw
    private static void exportGEList(XSSFSheet mainSheet) {
    	XSSFRow currentRow; //Tracks the current row being read
    	// This is a tomorrow project for me, this will take a while to compile EvErY Ge OpTiOn
    }
    
    ////////////////////////////////////////
    // VARIOUS HELPER/CALCULATION METHODS //
    ////////////////////////////////////////
    
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
    	/**
    	 * TODO
    	 * THIS METHOD CURRENTLY DOESN"T WORK.
    	 */
    	
    	// Checks to see if the course number is less than constant (only undergraduate courses)
    	boolean maxLevelCheck = true;
    	final int maxLevelCourse = 5999;
    	if (Double.parseDouble(currentRow.getCell(19).toString().substring(0,3)) < maxLevelCourse) {
    		maxLevelCheck = false;
    	}
    		
    	// Checks to see if minimum enrollment number is met (at least 30 students)
    	boolean minEnrollmentCheck = true;
    	final int minNumEnrollment = 30;
    	if (Double.parseDouble(currentRow.getCell(19).toString()) < minNumEnrollment) {
    		minEnrollmentCheck = false;
    	}
    	
    	// Checks to see if it isn't a technical course (ex. 4232T)
    	boolean techCourse = false;
    	if (currentRow.getCell(19).toString().length() > 4 && currentRow.getCell(19).toString().charAt(4) == 'T') {
    		techCourse = true;
    	}
    	
    	// return maxLevelCheck && minEnrollmentCheck && !techCourse;
    	return true;
    	
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

}
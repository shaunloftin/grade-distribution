import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GradeDistribution {

	// HashMap<String, Double> avgGpa = new HashMap<String, Double>();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
        readFromExcel("data/data.xlsx");
    }
    
    public static void readFromExcel(String file) throws IOException{
        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet myExcelSheet = myExcelBook.getSheet("Sheet1");
        XSSFRow currentRow;
        HashMap<String, Double> passMap = new HashMap<String, Double>();
     
        /**
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
         * COLUMN 19 - Total
         */
        
        /*for (int i = 0; i < myExcelSheet.getLastRowNum(); i++) {
        	currentRow = myExcelSheet.getRow(i);
        	if(currentRow.getCell(2).toString().equals("CHEM") 
        			&& currentRow.getCell(3).toString().equals("1250")){
                System.out.println(currentRow.getCell(0) + " Pass Rate: " + calcPassRate(currentRow) + " %");
            }
        }*/
        
        for (int i = 1; i < myExcelSheet.getLastRowNum(); i++) {
        	currentRow = myExcelSheet.getRow(i);
        	if (currentRow.getCell(17) != null && currentRow.getCell(18) != null) {
        		String courseName = currentRow.getCell(0).toString() + " " + currentRow.getCell(2).toString() + " " 
            			+ currentRow.getCell(3).toString();
            	passMap.put(courseName, calcPassRate(currentRow));
        	}
        	System.out.println("row " + i);
        }
        
        List<Entry<String, Double>> topPassed = entriesSortedByValues(passMap);
        int k = 0;
        for (Map.Entry<String, Double> x : topPassed) {
        	if (k < 50) {
        		System.out.println(x.getKey() + " || Pass Rate: " + x.getValue());
        	} else {
        		break;
        	}
        }
        
        
        myExcelBook.close();
        
    }
    
    
    private static Double calcPassRate(XSSFRow currentRow) throws IOException {
    	Double passRate = (parseDoubleNull(currentRow.getCell(19).toString())-parseDoubleNull(currentRow.getCell(12).toString())
				-parseDoubleNull(currentRow.getCell(13).toString())
				-parseDoubleNull(currentRow.getCell(14).toString()))/(parseDoubleNull(currentRow.getCell(19).toString()));
		return 100*passRate;
    }
    
    private static Double parseDoubleNull(String x) {
    	if (x.equals(null) || x.equals("") || x.equals(" ")) {
    		return 0.0;
    	} else {
    		return Double.parseDouble(x);
    	}
    }
    
    static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {
		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());
		Collections.sort(sortedEntries, 
		    new Comparator<Entry<K,V>>() {
		        @Override
		        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
		            return e2.getValue().compareTo(e1.getValue());
		        }
		    }
		);
		
		return sortedEntries;
	}

}
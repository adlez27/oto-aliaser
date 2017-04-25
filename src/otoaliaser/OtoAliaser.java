package otoaliaser;

import java.util.*;
import java.io.*;

import javax.swing.*;

public class OtoAliaser {
	
	/* This is all the text in the GUI.
	 * To translate this program, simply edit these strings.
	 */
	static String windowTitle = "OTO Aliaser", 
				  errorMessage = " Error";
	static String otoReadError = "Could not read OTO.",
				  dictionaryReadError = "Could not read dictionary.",
				  otoWriteError = "Could not write OTO.";
	static String operatingSystemQuestion = "Which operating system?";
	static String aliasActionQuestion = "Add or replace aliases?",
				  addAliasOption = "Add aliases",
				  replaceAliasOption = "Replace aliases";
	static String dictionaryQuestion = "Which aliases?",
				  roma2hiraOption = "Romaji to Hiragana",
				  hira2romaOption = "Hiragana to Romaji",
				  customDictOption = "Custom";
	static String doneMessage = "Done.";
	
	public static void main(String[]args){
		
		// Instantiating a bunch of variables
		
		ArrayList<String> otoFile = new ArrayList<String>();
		ArrayList<String[]> otoMatrix = new ArrayList<String[]>();
		boolean windows, adding, error = false;
	    String macHeader = "";
	    ImageIcon utau = new ImageIcon("utaulogo.png");
	    HashMap<String,String> replaceDict = new HashMap<String,String>();
	    File replaceFile;
	     
	    // Selecting operating system
	    
	    Object[] possibleOS = { "Windows", "Mac"};
        Object os = JOptionPane.showInputDialog(null,operatingSystemQuestion, windowTitle,JOptionPane.INFORMATION_MESSAGE, utau, possibleOS, possibleOS[0]);
        
        if (os.equals("Windows")) {
        	windows = true;
        } else {
        	windows = false;
        }
	      
	    String inFile, outFile;
	    if (windows) {
	    	inFile = "oto/oto.ini";
	    	outFile = "oto/edited-oto.ini";
		} else {
	    	inFile = "oto/oto_ini.txt";
	    	outFile = "oto/edited-oto_ini.txt";
		}
	    
	    // Reading the OTO
	    
	    try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
			
			String line;
			while ((line = br.readLine()) != null) {
	    		otoFile.add(line);
	    	}
	         
	    	if (!windows) {
	        	macHeader = otoFile.remove(0);
	    	}
	    } catch (Exception ex) {
	    	error = true;
	    	ex.printStackTrace();
	    	JOptionPane.showMessageDialog(null, otoReadError, windowTitle + errorMessage, JOptionPane.ERROR_MESSAGE);
	    }
	      
	    if (!error) {
	    	
	    	// Turning the OTO into an ArrayList of string arrays
	    	
	    	for (int i = 0; i < otoFile.size(); i++) {
	    		otoFile.set(i, otoFile.get(i) + ",");
	    		String line = otoFile.get(i);
	            String[] arr = new String[7];
	            int start = 0;
	            int end = line.indexOf("=");
	            for (int j = 0; j < 7; j++) {
	            	arr[j] = line.substring(start,end);
	            	start = end + 1;
	            	end = line.indexOf(",",start);
	        	}
	            arr[0] = arr[0].substring(0,arr[0].length()-4);
	            otoMatrix.add(arr);
	        }
	    	
	    	/* Selecting the method of editing aliases
	    	 * If adding, it will read the filenames in the OTO
	    	 * If replacing, it will read the existing aliases in the OTO
	    	 */
	    	
	    	Object[] possibleAction = { addAliasOption, replaceAliasOption};
	        Object action = JOptionPane.showInputDialog(null,aliasActionQuestion, windowTitle,JOptionPane.INFORMATION_MESSAGE, utau, possibleAction, possibleAction[0]);
	        
	        if (action.equals(addAliasOption)){
	        	adding = true;
	        } else {
	        	adding = false;
	        }
	        
	        /* Selecting the replacement dictionary for editing aliases
	         * Romaji to Hiragana and Hiragana to Romaji are pre-included
	         * Users can also create their own custom dictionary
	         */
	        
	        Object[] possibleDict = {roma2hiraOption, hira2romaOption, customDictOption};
	        Object chosenDict = JOptionPane.showInputDialog(null, dictionaryQuestion, windowTitle, JOptionPane.INFORMATION_MESSAGE, utau, possibleDict, possibleDict[0]);
	        
	        if (chosenDict.equals(roma2hiraOption)) {
	        	replaceFile = new File("csv/roma2hira.csv");
	        } else if (chosenDict.equals(hira2romaOption)) {
	        	replaceFile = new File("csv/hira2roma.csv");
	        } else {
	        	replaceFile = new File("csv/custom.csv");
	        }
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(replaceFile))){
				String line;
				while ((line = br.readLine()) != null) {
					String[] lineArr = line.split(",");
					replaceDict.put(lineArr[0],lineArr[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, dictionaryReadError, windowTitle + errorMessage, JOptionPane.ERROR_MESSAGE);
			}
	        
	        // Editing the aliases
	        
	        if (adding){
	        	for (String[] line : otoMatrix){
	    			if (replaceDict.containsKey(line[0]))
	    				line[1] = replaceDict.get(line[0]);
	    		}
	        } else {
	        	for (String[] line : otoMatrix){
	    			if (replaceDict.containsKey(line[1]))
	    				line[1] = replaceDict.get(line[1]);
	    		}
	        }
	        
	        // Exporting the edited OTO
	        
	        try {
	            PrintWriter writer = new PrintWriter(new File(outFile));
	            
	            if (!windows)
	            	writer.println(macHeader);
	            for (String[] line : otoMatrix) {
	            	writer.println(line[0] + ".wav=" + line[1] + "," + line[2] + "," + line[3] + "," + line[4] + "," + line[5] + "," + line[6]);
	            }
	            writer.close();
	         } catch (Exception ex) {
	        	 ex.printStackTrace();
	        	 JOptionPane.showMessageDialog(null, otoWriteError, windowTitle + errorMessage, JOptionPane.ERROR_MESSAGE);
	         }
	        
	        JOptionPane.showMessageDialog(null, doneMessage, windowTitle, JOptionPane.INFORMATION_MESSAGE,utau);
		}
	}
}

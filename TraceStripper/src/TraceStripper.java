import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This program is to take a Soar trace from running John's parser
 * and strip out the sentences with their semantic expectations.
 * 
 */

/**
 * @author plindes
 *
 */
public class TraceStripper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    final Charset ENCODING = StandardCharsets.UTF_8;
	    
	    //	Set up file names
		String inputFile = args[0];
		String outputFile = convertToOutput(inputFile);
		//	Read the lines in the file and write the output
		int lineNo = 0;
    	;
		try (BufferedWriter writer =Files.newBufferedWriter(Paths.get(outputFile), ENCODING)) {
	        try (BufferedReader reader =
    				Files.newBufferedReader(Paths.get(inputFile),
    										StandardCharsets.ISO_8859_1)) {
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	//	Look for the start or end of a sentence
		        	if (line.startsWith("====")) {
		        		if (lineNo > 0) {
		        			writer.newLine();
		        			System.out.println();
		        		}
		        		lineNo = (lineNo > 0)? 0 : 1;
		        	} else {
		        		if (lineNo > 0) {
		        			String text = line;
		        			if (lineNo++ == 1) {
		        				//	Show the sentence as a comment
		        				text = String.format("#   %s\r\n%s",
		        										line, line);
		        			} else if (line.startsWith("FAILED!")) {
		        				//	Show FAILED! as a comment
		        				text = "#   " + line;
		        			}
		        			writer.write(text);
		        			writer.newLine();
		        			System.out.println(text);
		        		}
		        	}
		        }
		    } catch(Exception e) {
		    	System.out.println(e.getMessage());
		    }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
	}

	private static String convertToOutput(String inputFile) {
		int dotPos = inputFile.lastIndexOf('.');
		return String.format("%s.exp.txt", inputFile.substring(0, dotPos));
	}

}

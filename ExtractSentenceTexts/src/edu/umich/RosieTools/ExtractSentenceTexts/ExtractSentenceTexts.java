/**
 * 
 */
package edu.umich.RosieTools.ExtractSentenceTexts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author plindes
 *
 */
public class ExtractSentenceTexts {
	//	Private Static Members
	private static String dataDirectory;
	
    //  The main program
    public static void main(String[] args) throws Exception {
    	//	If no args, print usage
    	if (args.length == 0) {
    		System.out.println("Tool to extract sentence texts from Soar code files.");
    		System.out.println("Usage:");
//    		System.out.println("java -jar SentencesToSoar.jar <input-file-name>.txt [-o <output-file-name>]");
//    		System.out.println("If no -o option is given, the output will be written to <input-file-name>.soar.");
    		System.out.println("");
    		System.exit(100);
    	}
    	//	Set default data directory
    	dataDirectory = "..\\..\\rosie\\agent\\language-comprehension";
    	//	Get directory name
    	if (args.length > 0) {
    		dataDirectory = args[0];
    	}
    	//	Report what we're doing
    	System.out.format("Extracting texts from *.soar files in %s directory.\r\n",
    							dataDirectory);
    	
    	//	Get the list of file names
    	File directory = new File(dataDirectory);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
        	//	Get the input file name
        	String inputFile = file.getName();
        	//	Skip files not to be processed
            if (!inputFile.endsWith(".soar") || inputFile.endsWith("source.soar")) {
            	continue;
            }
            System.out.println(inputFile);
        	//	Compute the output path
        	String outputFile = inputFile.replace(".soar", "-new.script");
        	String outputAbsolute = String.format("%s\\%s", directory.toString(), outputFile);
        	Path outputPath = Paths.get(outputAbsolute);
            Path inputPath = Paths.get(file.toString());
            String inputText = getTextFromFile(inputPath);
            String outputText = extractTexts(inputText);
            writeOutput(outputText, outputPath);
        }
   	
//    	//	Get or use defaults for file names
//    	for (int i = 0; i < args.length; i++) {
//    		if (args[i].equals("-o"))
//    			outputFile = args[++i];
//    		inputFile = args[i];
//    	}
//    	//	Set default output file
//    	if (outputFile == null) {
//    		int extension = inputFile.lastIndexOf('.');
//    		if (extension < 1) {
//        		System.out.println(String.format("'%s' is not a valid input file name."));
//        		System.exit(101);
//    		}
//    		outputFile = inputFile.substring(0, extension) + ".soar";
//    	}
//    	//	Report what we're doing
//		System.out.println(String.format("Translating '%s' to '%s'.", inputFile, outputFile));
        
    }

	private static String extractTexts(String inputText) {
		//	Look for sentence texts
		StringBuilder builder = new StringBuilder();
		int start = 0;
		int end = 0;
		while (start < inputText.length()) {
			final String pattern = "^complete-sentence |";
			int next = inputText.indexOf(pattern, start) + pattern.length();
			if (next < start)
				break;
			start = next;
			end = inputText.indexOf("|", start);
			String text = inputText.substring(start, end);
			builder.append(text);
			builder.append("\r\n");
			start = end + 1;
		}
		return builder.toString();
	}

	private static void writeOutput(String inputText, Path outputPath) {
		//	Now write it all out
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(outputPath, charset)) {
		    writer.write(inputText, 0, inputText.length());
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}

	private static String getTextFromFile(Path file) {
		StringBuilder contents = new StringBuilder();
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        contents.append(line);
		        contents.append("\r\n");
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		return contents.toString();
	}

}

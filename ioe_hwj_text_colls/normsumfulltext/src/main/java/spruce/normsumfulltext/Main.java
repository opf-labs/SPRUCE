package spruce.normsumfulltext;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;

import cue.lang.unicode.Normalizer;

public class Main {

	private final static String FULLTEXTFN = "textextract.txt";
	private final static String NORMFTFN = "textnorm.txt";
	private final static String NORMFTCHKFN = "textnormsha256.txt";
	
	private int count;
	
	public static void main(String[] args) {
		Main man = new Main();
		if (args.length != 1) {
			doErrorAndExit("No parameters");
		}
		File croot = new File(args[0]);
		if (croot.exists() && croot.isDirectory()) {
			man.go(croot);
		}
	}

	private void go(File croot) {
		// We look for the fulltext files.
		// Normalise them.
		// Checksum them.
		// Save the output.
		walkCollection(croot);
	}

	private void walkCollection(File cfile) {
		if (cfile.isDirectory()) {
			File[] files = cfile.listFiles();
			for (File file : files) {
				walkCollection(file);
			}
		} else {
			// A file then... :-)
			if (cfile.getName().equals(FULLTEXTFN)) {
				// We found some fulltext - hooray! :-)
				try {
					String fullText = FileUtils.readFileToString(cfile);
					String norman = Normalizer.getInstance().normalize(fullText);
					norman = stripWhitespace(norman);
					norman = norman.toLowerCase();
			        MessageDigest md = MessageDigest.getInstance("SHA-256");
			        md.update(norman.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			        byte[] digest = md.digest();
			        
			        StringBuffer sb = new StringBuffer();
			        for (int i = 0; i < digest.length; i++) {
			          sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
			        }
			    	
			    	File normTextFile = new File(cfile.getParent() + "/" + NORMFTFN);
			    	File normSumFile = new File(cfile.getParent() + "/" + NORMFTCHKFN);

			    	FileUtils.writeStringToFile(normTextFile, norman);
			    	FileUtils.writeStringToFile(normSumFile, sb.toString());
					System.out.println( ++count );
			    	
				} catch ( IOException e ) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private String stripWhitespace(String norman) {
		norman = norman.replace(" ", "");
		norman = norman.replace("\n", "");
		norman = norman.replace("\b", "");
		norman = norman.replace("\r", "");
		norman = norman.replace("\f", "");
		return norman;
	}

	private static void doErrorAndExit(String msg) {
		System.out.println("\n\n" + msg + "\n\n");
		System.exit(0);
	}
}

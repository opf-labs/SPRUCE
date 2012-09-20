package spruce.rain;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud;

public class Main {

	private final static String FULLTEXTFN = "textextract.txt";
	private final static String UNICLOUDFN = "unicloud.html";
	private final static String BICLOUDFN = "bicloud.html";
	private final static String TRICLOUDFN = "tricloud.html";
	
	private final static String UNICOLCLOUDFN = "unicolcloud.html";
	private final static String BICOLCLOUDFN = "bicolcloud.html";
	private final static String TRICOLCLOUDFN = "tricolcloud.html";
	
	private final static String MYMASSIVEFILE = System.getProperty("java.io.tmpdir")+"/massivefile.tmp";
	
	private int count = 0;

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
		// See if they have data in.
		// If they do, create a cloud output file for it.
		// uni- bi- and tri- gram? As HTML and as text.

		walkCollection(croot);
		
		// So, that should've put everything in massive file too...
		File massive = new File(MYMASSIVEFILE);
		String bigstring = null;
		try {
			bigstring = FileUtils.readFileToString(massive);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cloud mcloud = new Cloud();
		mcloud.absorb(bigstring, 1);			
		try {
			FileUtils.writeStringToFile(new File(croot+"/"+UNICOLCLOUDFN), mcloud.toHTMLem());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mcloud.absorb(bigstring, 2);			
		try {
			FileUtils.writeStringToFile(new File(croot+"/"+BICOLCLOUDFN), mcloud.toHTMLem());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mcloud.absorb(bigstring, 3);			
		try {
			FileUtils.writeStringToFile(new File(croot+"/"+TRICOLCLOUDFN), mcloud.toHTMLem());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void walkCollection(File cfile) {
		if (cfile.isDirectory()) {
			File[] files = cfile.listFiles();
			for (File file : files) {
				walkCollection(file);
			}
		} else {
			System.out.println( ++count );
			// A file then... :-)
			if (cfile.getName().equals(FULLTEXTFN)) {
				// We found some fulltext - hooray! :-)
				try {
					String fullText = FileUtils.readFileToString(cfile);
					
					// Append fulltext to collection fulltext... This is gonna be huge! :-)
					File bigFile = new File(MYMASSIVEFILE);	
					FileUtils.writeStringToFile(bigFile, fullText, true);
					
					File unicloud = new File( cfile.getParent() + "/" + UNICLOUDFN);
					File bicloud = new File( cfile.getParent() + "/" + BICLOUDFN);
					File tricloud = new File( cfile.getParent() + "/" + TRICLOUDFN);

					if (fullText != null && fullText.length() > 0) {
						Cloud c = new Cloud();
						c.absorb(fullText, 1);
						FileUtils.writeStringToFile(unicloud, c.toHTMLem());
						c.absorb(fullText, 2);
						FileUtils.writeStringToFile(bicloud, c.toHTMLem());
						c.absorb(fullText, 3);
						FileUtils.writeStringToFile(tricloud, c.toHTMLem());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void doErrorAndExit(String msg) {
		System.out.println("\n\n" + msg + "\n\n");
		System.exit(0);
	}
}

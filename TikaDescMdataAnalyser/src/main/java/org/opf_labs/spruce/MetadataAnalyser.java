/**
 * 
 */
package org.opf_labs.spruce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.cli.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.opf_labs.spruce.filesystems.FileSystem.DamagedEntryException;
import org.opf_labs.spruce.filesystems.FileSystem.EntryNotFoundException;
import org.opf_labs.spruce.filesystems.FileSystemEntry;
import org.opf_labs.spruce.filesystems.FileSystems;
import org.opf_labs.spruce.filesystems.JavaIOFileSystem;

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud;

import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.Head;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.Style;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Title;
import com.hp.gagawa.java.elements.Tr;

/**
 * @author Peter May
 */
public class MetadataAnalyser {
	// Command line options
	private static Options options = null;
	private static final String		OPTION_CONFIG_FILE = "configfile";
	
	// Default configuration file name
	private static final String		CONFIG = "resources/config.properties";
	
	// Default lists - can be overwritten with a config.properties file
//	private static final Set<String> IMAGE_DESC_MDATA_KEYS = new HashSet<String>(Arrays.asList("Artist", "Author", "Copyright", "creator", "dc:creator", "dc:description", "dc:subject", "dc:title", "description", "Image Description", "Keywords", "meta:author", "meta:keyword", "subject", "title"));
//	private static final Set<String> TEXT_DESC_MDATA_KEYS = new HashSet<String>(Arrays.asList("Author", "creator", "dc:creator", "dc:title", "meta:author", "meta:last-author", "title"));
	
	// Once configuration loaded, this will be the actual set of descriptive metadata keys
	private HashMap<String, Set<String>> KEYLIST = new HashMap<String, Set<String>>();
	
	// FileSystem and TikaWrapper object variables
	private JavaIOFileSystem 	fileSys = null;
	private TikaWrapper 		TIKA 	= null;
	
	static {
		@SuppressWarnings("static-access")
		Option configfile   = OptionBuilder.withArgName( "file" )
								.hasArg()
								.withDescription(  "use given config file for descriptive metadata lists" )
								.create( OPTION_CONFIG_FILE );
		
		options = new Options();
		options.addOption( configfile );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		// process command line arguments - need at least 2 (directory to parse & output file)
		CommandLine cmd = null;
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        cmd = parser.parse( options, args );
	    }
	    catch( ParseException exp ) {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	    
	    // get the configuration file to use
	    String config_file = cmd.getOptionValue(OPTION_CONFIG_FILE, CONFIG);

	    // check remaining arguments and analyse
	    if(cmd.getArgs().length==2){
	    	MetadataAnalyser analyser = new MetadataAnalyser(config_file);
	    	analyser.analyse(cmd.getArgs()[0], cmd.getArgs()[1], null);
	    } else {
	    	HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java MetadataAnalyser [options] <input directory> <output file>", options);
			System.exit(1);
	    }
	}
	

	
	/**
	 * Public constructor to create a new Metadata Analyser
	 * @param configFile	the configuration file listing descriptive metadata files
	 * 
	 */
	public MetadataAnalyser(String configFile){
		// get a reference to the TikaWrapper
		TIKA = TikaWrapper.getTika();

		// load descriptive metadata keys
		loadConfiguration(configFile);
	}
	
	
	private boolean stop = false;
	/**
	 * Terminates the program execution
	 */
	public void terminate(){
		stop = true;
	}
	
	/**
	 * Use this MetadataAnalyser to analyse the specified input directory, writing results to
	 * the specified output HTML file. 
	 * @param inputDir		the input directory to analyse
	 * @param outputFile	HTML output file to write results to
	 */
	public void analyse(String inputDir, String outputFile, ThreadListener listener){
		stop = false;
		String rootPath = inputDir;
		File rootDir = new File(rootPath);
		fileSys = FileSystems.fromDirectory(rootDir);
		
		TreeMap<String, FileMetaInformation> metaTable = new TreeMap<String, FileMetaInformation>();
		
		Set<FileSystemEntry> fileEntries = fileSys.getEntries();
		int fileCount = fileEntries.size();
		
		int procCount = 0;
		int pcComplete= 0;
		for (FileSystemEntry entry : fileEntries) {
			try {
				// Checksumming
//				InputStream stream = fileSys.getEntryStream(entry.getName());
//				ByteStreamId bsi = ByteStreams.fromInputStream(stream);
//				stream.close();
				
				// File Format Identification
				InputStream stream = fileSys.getEntryStream(entry.getName());
				MediaType mediaType = TIKA.getMediaType(stream);
				stream.close();
				
//				System.out.println("Mediatype: "+mediaType.toString());
				
				// File Format Parsing
				stream = fileSys.getEntryStream(entry.getName());
				Metadata md = new Metadata();
				Reader reader = TIKA.parse(stream, md);				
				stream.close();
				
//				System.out.println("Metadata: "+md.toString());
				
				String wordcloud="";
				if (mediaType.getType().equalsIgnoreCase(MediaType.application(null).getType()) || 
					mediaType.getType().equalsIgnoreCase(MediaType.text(null).getType())){
					Cloud cl = new Cloud();
					
					String str = readFile(reader);
					if(str!=null && str.length()>0){
						cl.absorb(str, 1);	// 1=unigram; 2=bigrams
						wordcloud=cl.toHTMLem();
					}
				}
				
				// Add the metadata information into the hashtable
				metaTable.put(entry.getName(), new FileMetaInformation(mediaType, md, wordcloud));
			} catch (EntryNotFoundException e) {
				e.printStackTrace();
			} catch (DamagedEntryException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TikaException e) {
				e.printStackTrace();
			}
			
			procCount++;
			double done = 100*((double)procCount)/fileCount;
			if(done>=(pcComplete+10)){
				pcComplete+=10;
				System.out.println("Processed "+pcComplete+"%");
				// update the UI if there is one
				if(listener!=null){
					listener.notifyUpdate();
				}
			}
			
			// if asked to stop, stop immediately
			if (stop){
				return;
			}
		}
		
//		System.out.println("metaTable: "+metaTable.size());
		
		generateHTML(outputFile, metaTable);
	}
	
	
	
	/**
	 * Loads the configuration file to build up the necessary list of descriptive metadata
	 * keys.
	 * @param propFile	File name of the configuration properties file
	 */
	private void loadConfiguration(String propFile){
		// load default lists first, then override if necessary
//		KEYLIST.put(MediaType.image(null).getType(), IMAGE_DESC_MDATA_KEYS);
//		KEYLIST.put(MediaType.application(null).getType(), TEXT_DESC_MDATA_KEYS);
//		KEYLIST.put(MediaType.text(null).getType(), TEXT_DESC_MDATA_KEYS);
		
		// now override any specific mimetypes
		Properties configFile = new Properties();
		try {
			InputStream cfStream = MetadataAnalyser.class.getClassLoader().getResourceAsStream(propFile);
			if (cfStream==null){
				// try loading from file
				cfStream = new FileInputStream(propFile);
			}
				
			configFile.load(cfStream);
			
			
			for(String key: configFile.stringPropertyNames()){
				loadDescriptiveMetadataFile(key, configFile.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the specified Descriptive metadata file.
	 * If filename is null, then use the default based on the mimetype
	 * @param filename
	 */
	private void loadDescriptiveMetadataFile(String mimetype, String filename){
		if(filename!=null){
			// read each line of the relevant
			System.out.print("Loading Descriptive Metadata Tags for: "+mimetype);
			System.out.flush();
			
			Set<String> mdata_keys = new HashSet<String>();
			try {
				InputStream dmStream = MetadataAnalyser.class.getClassLoader().getResourceAsStream(filename);
				if (dmStream==null){
					// can't find, so try assuming a full file path
					dmStream = new FileInputStream(filename);
				}
				
				BufferedReader brin = new BufferedReader(
						new InputStreamReader(dmStream));
			
				String br = "";
				while (brin.ready()) {
					br = brin.readLine();
					System.out.print(".");
					System.out.flush();
					mdata_keys.add(br);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			KEYLIST.put(mimetype, mdata_keys);
			System.out.println("Done");
		}
	}
	
	
	/**
	 * Returns an ArrayList of Descriptive Metadata keys for the file if they exist
	 * @param mtype
	 * @param md
	 * @return
	 */
	private ArrayList<String> getDescriptiveMetadataKeys(MediaType mtype, Metadata md){
		ArrayList<String> mdata = new ArrayList<String>();
		// get the full descriptive metadata key list for this MediaType
		Set<String> keys = KEYLIST.get(mtype.getType());
		
		// check which of the descriptive metadata keys have values in this file's metadata
		if(keys!=null){
			for (String s: keys){
//				System.out.println("Key: "+s+"\tvalue: "+md.get(s));
				if(md.get(s)!=null){
					mdata.add(s);
				}
			}
		}
		
		return mdata;
	}
	
	private static String CSS = 
			"#container {width:100%;}" +
			"#left {float:left; width:50%;}" +
			"#right {float:right; width:50%;}" +
			"#clear {clear:both;}" +
			"body {font-family: Verdana, Tahoma, Helvetica, Arial;}" +
			"h1 {font-size: 18px; padding-top: 6px; width: 100%; border-bottom: 1px solid black;}" +
			"table {border-collapse:collapse; font-family: Verdana, Tahoma, Helvetica, Arial;}" +
			"table, th, td {border:1px solid black;}" +
			"table.borderless {border:none;}" +
			"th {background-color:white;}" +
			".red {background-color:#ff2626;} " +		// red
			".green {background-color:#6fff44;} " +	// green
			".orange {background-color:#ffac62;}";	// orange
	
	
	public void generateHTML(String outFile, TreeMap<String, FileMetaInformation> mdT){
		Html html = new Html();
		Head head = new Head();
		
		// Header stuff
		html.appendChild(head);
		
		Title title = new Title();
		title.appendText("File Analysis");
		head.appendChild(title);
		
		Style style = new Style("text/css");
		style.appendText(CSS);
		head.appendChild(style);
		
		// Body stuff
		Body body = new Body();
		
		html.appendChild(body);
		
		// stats overview
		
		
		Div statsdiv = new Div();
		H1 h1 = new H1();
		h1.appendText("Overview:");
		statsdiv.appendChild(h1);
		
		statsdiv.setId("container");
		Div ffcountdiv = new Div();
		ffcountdiv.setId("left");
		Div mcountdiv = new Div();
		mcountdiv.setId("right");
		statsdiv.appendChild(ffcountdiv);
		statsdiv.appendChild(mcountdiv);
		
		body.appendChild(statsdiv);
		Table stattab = new Table();
		stattab.setCSSClass("borderless");
		Tr goodrow = new Tr();
		Tr okrow = new Tr();
		Tr badrow = new Tr();
		stattab.appendChild(goodrow);
		stattab.appendChild(okrow);
		stattab.appendChild(badrow);
		Td goodcell = new Td();
		goodcell.appendText("Files with full descriptive metadata:");
		goodrow.appendChild(goodcell);
		Td goodcellval = new Td();
		goodcellval.setCSSClass("green");
		goodrow.appendChild(goodcellval);
		
		Td okcell = new Td();
		okcell.appendText("Files with some descriptive metadata:");
		okrow.appendChild(okcell);
		Td okcellval = new Td();
		okcellval.setCSSClass("orange");
		okrow.appendChild(okcellval);
		
		Td badcell = new Td();
		badcell.appendText("Files with no descriptive metadata:");
		badrow.appendChild(badcell);
		Td badcellval = new Td();
		badcellval.setCSSClass("red");
		badrow.appendChild(badcellval);
		
		mcountdiv.appendChild(stattab);
		
		// mimetype format count
		Table formattab = new Table();
		ffcountdiv.appendChild(formattab);
		String[] ffheadings = {"File Format", "Count"};
		Tr fftr = new Tr();
		formattab.appendChild(fftr);
		for (String h: ffheadings){
			Th th = new Th();
			th.appendText(h);
			fftr.appendChild(th);
		}
		
		// file details table
		Div ffdetails = new Div();
		ffdetails.setId("clear");
		body.appendChild(ffdetails);
		h1 = new H1();
		h1.appendText("File details:");
		ffdetails.appendChild(h1);
		Table table = new Table();
		
		String[] headings = {"Filename", "Mimetype", "Descriptive Metadata", "WordCloud/Thumbnail"};
		Tr tr = new Tr();
		table.appendChild(tr);
		for (String h: headings){
			Th th = new Th();
			th.appendText(h);
			tr.appendChild(th);
		}
		
		
		// counts of files with full, some and no metadata
		int goodCount = 0;
		int okCount = 0;
		int badCount = 0;

//		Set<String> imgdescset = new HashSet<String>();
//		for(String s: IMAGE_DESC_MDATA_KEYS){
//			imgdescset.add(s);
//		}
		
		TreeMap<String, Integer> filemapCount = new TreeMap<String, Integer>();
		
		for(String filename: mdT.keySet()){
			// add a new row for each file 
			
			// get the metainformation
			MediaType mtype = mdT.get(filename).getMediaType();
			Metadata mdata = mdT.get(filename).getMetadata();
			
//			System.out.println("mtype: "+mtype.toString());
//			System.out.println("mdata: "+mdata.toString());
			
			// add to file mimetype count
			if(filemapCount.containsKey(mtype.toString())){
				filemapCount.put(mtype.toString(), filemapCount.get(mtype.toString())+1);
			} else {
				filemapCount.put(mtype.toString(), 1);
			}
			
			tr = new Tr();
			table.appendChild(tr);
			
			// filename cell
			Td fname = new Td();
			fname.appendText(filename);
			tr.appendChild(fname);
			
			// Mimetype cell
			Td mimetype = new Td();
			mimetype.appendText(mtype.toString());
			tr.appendChild(mimetype);
			
			// Descriptive Metadata cell
			Td descmdatacell = new Td();
			tr.appendChild(descmdatacell);
			descmdatacell.appendText("");
			ArrayList<String> descmdata = getDescriptiveMetadataKeys(mtype, mdata);
//			System.out.println("DescMdata size: "+descmdata.size());
			if (descmdata.size()>0){
				StringBuffer buf = new StringBuffer("");
				for (String key: descmdata){
					buf.append("<b>"+key+"</b>: "+mdata.get(key)+"<br/>");
				}
				descmdatacell.appendText(buf.toString());
				
				// set green/orange row colour
				if (descmdata.size()==KEYLIST.get(mtype.getType()).size()){
					// green
					tr.setCSSClass("green");
					goodCount++;
				} else {
					tr.setCSSClass("orange");
					okCount++;
					
					// calculate missing keys
					Set<String> tkeys = new HashSet<String>(KEYLIST.get(mtype.getType()));//new HashSet(imgdescset);
					Set<String> keys = new HashSet<String>(descmdata);
					tkeys.removeAll(keys);
					StringBuffer buf2 = new StringBuffer("Missing metadata: &#013;");
					if(tkeys!=null){
						for (String k: tkeys){
							buf2.append(k+" &#013;");
						}
					}
					descmdatacell.setTitle(buf2.toString());
				}
			} else {
				tr.setCSSClass("red");
				badCount++;
				
				StringBuffer buf2 = new StringBuffer("Missing metadata: &#013;");
				Set<String> keys = KEYLIST.get(mtype.getType());
				if (keys!=null){
					for (String k: keys){ //IMAGE_DESC_MDATA_KEYS){
						buf2.append(k+" &#013;");
					}
				}
				descmdatacell.setTitle(buf2.toString());
			}
			
			// Thumbnail/Word Cloud
			Td thumbcloud = new Td();
			tr.appendChild(thumbcloud);
			if (mtype.getType().equalsIgnoreCase(MediaType.application(null).getType()) || 
				mtype.getType().equalsIgnoreCase(MediaType.text(null).getType())){
				// add wordcloud to output
				thumbcloud.appendText(mdT.get(filename).getWordCloud());
			} else if (mtype.getType().equalsIgnoreCase(MediaType.image(null).getType())){
//				IImageMetadata metadata = Sanselan.getMetadata(fileSys.getEntryStream(filename), );
			}

			
			
		}
		
		body.appendChild(table);
		
		goodcellval.appendText(""+goodCount);
		okcellval.appendText(""+okCount);
		badcellval.appendText(""+badCount);
		
		// handle file mime type counts
		for(String mimetype: filemapCount.keySet()){
			Tr mimeRow = new Tr();
			
			Td mime = new Td();
			mime.appendText(mimetype);
			mimeRow.appendChild(mime);
			
			Td count = new Td();
			count.appendText(""+filemapCount.get(mimetype));
			mimeRow.appendChild(count);
			
			formattab.appendChild(mimeRow);
		}
		
		// Write HTML file
		try{
			File output = new File(outFile);
			PrintWriter out = new PrintWriter(new FileOutputStream(output));
			out.println(html.write());
			out.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}

	
	private static String readFile(Reader reader){
		StringBuffer sb = new StringBuffer();

		try {
			BufferedReader brin = new BufferedReader(reader);
			String line = "";
			while ((line=brin.readLine())!=null) {
				sb.append(line + "\n");
			}
			brin.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return null;
	}

}

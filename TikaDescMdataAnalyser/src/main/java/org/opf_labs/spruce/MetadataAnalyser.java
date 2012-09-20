/**
 * 
 */
package org.opf_labs.spruce;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.opf_labs.spruce.bytestreams.ByteStreamId;
import org.opf_labs.spruce.bytestreams.ByteStreams;
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
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Title;
import com.hp.gagawa.java.elements.Tr;

/**
 * @author carl
 *
 */
public class MetadataAnalyser {
	
	private static JavaIOFileSystem fileSys;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TikaWrapper TIKA = TikaWrapper.getTika();
		// TODO Auto-generated method stub
		String rootPath = args[0];
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
				
				// File Format Parsing
				stream = fileSys.getEntryStream(entry.getName());
				Metadata md = new Metadata();
				Reader reader = TIKA.parse(stream, md);				
				stream.close();
				
				// Add the metadata information into the hashtable
				metaTable.put(entry.getName(), new FileMetaInformation(mediaType, md));
			} catch (EntryNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DamagedEntryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			procCount++;
			double done = 100*((double)procCount)/fileCount;
			if(done>=(pcComplete+10)){
				pcComplete+=10;
				System.out.println("Processed "+pcComplete+"%");
			}
		}
		
		generateHTML(args[1], metaTable);
	}
	
	
	private static String[] IMAGE_DESC_MDATA_KEYS = {"Artist", "Author", "Copyright", "creator", "dc:creator", "dc:description", "dc:subject", "dc:title", "description", "Image Description", "Keywords", "meta:author", "meta:keyword", "subject", "title"};
	private static String[] TEXT_DESC_MDATA_KEYS = {"Author", "creator", "dc:creator", "dc:title", "meta:author", "meta:last-author", "title"};
	
	/**
	 * Returns an ArrayList of Descriptive Metadata keys for the file if they exist
	 * @param mtype
	 * @param md
	 * @return
	 */
	public static ArrayList<String> getDescriptiveMetadataKeys(MediaType mtype, Metadata md){
		ArrayList<String> mdata = new ArrayList<String>();
		
		String[] keys = null;
		if(mtype.getType().equalsIgnoreCase(MediaType.image(null).getType())){
			keys = IMAGE_DESC_MDATA_KEYS;
		} else if(mtype.getType().equalsIgnoreCase(MediaType.application(null).getType()) || 
				  mtype.getType().equalsIgnoreCase(MediaType.text(null).getType())){
			keys = TEXT_DESC_MDATA_KEYS;
		}
		
		if(keys!=null){
			for (String s: keys){
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
	
	
	public static void generateHTML(String outFile, TreeMap<String, FileMetaInformation> mdT){
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

		Set<String> imgdescset = new HashSet<String>();
		for(String s: IMAGE_DESC_MDATA_KEYS){
			imgdescset.add(s);
		}
		
		TreeMap<String, Integer> filemapCount = new TreeMap<String, Integer>();
		
		for(String filename: mdT.keySet()){
			// add a new row for each file 
			
			// get the metainformation
			MediaType mtype = mdT.get(filename).getMediaType();
			Metadata mdata = mdT.get(filename).getMetadata();
			
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
			if (descmdata.size()>0){
				StringBuffer buf = new StringBuffer("");
				for (String key: descmdata){
					buf.append("<b>"+key+"</b>: "+mdata.get(key)+"<br/>");
				}
				descmdatacell.appendText(buf.toString());
				
				// set green/orange row colour
				if (descmdata.size()==IMAGE_DESC_MDATA_KEYS.length){
					// green
					tr.setCSSClass("green");
					goodCount++;
				} else {
					tr.setCSSClass("orange");
					okCount++;
					
					// calculate missing keys
					Set<String> tkeys = new HashSet(imgdescset);
					Set<String> keys = new HashSet<String>(descmdata);
					tkeys.removeAll(keys);
					StringBuffer buf2 = new StringBuffer("Missing metadata: &#013;");
					for (String k: tkeys){
						buf2.append(k+" &#013;");
					}
					descmdatacell.setTitle(buf2.toString());
				}
			} else {
				tr.setCSSClass("red");
				badCount++;
				
				StringBuffer buf2 = new StringBuffer("Missing metadata: &#013;");
				for (String k: IMAGE_DESC_MDATA_KEYS){
					buf2.append(k+" &#013;");
				}
				descmdatacell.setTitle(buf2.toString());
			}
			
			// Thumbnail/Word Cloud
			Td thumbcloud = new Td();
			tr.appendChild(thumbcloud);
			if (mtype.getType().equalsIgnoreCase(MediaType.application(null).getType()) || 
				mtype.getType().equalsIgnoreCase(MediaType.text(null).getType())){
				Cloud cl = new Cloud();
				String wordcloud="";
				
				try {
					String str = readFile(fileSys.getEntryStream(filename));
					cl.absorb(str, 2);	// 1=unigram; 2=bigrams
					wordcloud=cl.toHTMLem();
				} catch (EntryNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DamagedEntryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// add wordcloud to output
				thumbcloud.appendText(wordcloud);
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
	
	public static String readFile(InputStream inputStream){
		StringBuffer sb = new StringBuffer();

		try {
			BufferedReader brin = new BufferedReader(
					new InputStreamReader(inputStream));
			while (brin.ready()) {
				sb.append(brin.readLine() + "\n");
			}
			brin.close();
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String readFile(String fn) {
		try {
			return readFile(new FileInputStream(new File(fn)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

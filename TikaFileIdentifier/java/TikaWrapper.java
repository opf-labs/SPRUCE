/**
 * @author Peter May (The British Library) 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TikaWrapper {

	/* Main Application */
	public static void main(String[] args) throws Exception{
	
		TikaWrapper tika = TikaWrapper.getTika();
		
		MediaType mime = tika.getMimeType(new File(args[0]));

		System.out.println(""+mime.toString());
	}

	/* Singleton Tika instance */
	private static TikaWrapper tika;
	
	/* Reference to parser */
	private Parser parser;
	
	/* Private constructor to enable singleton creation */
	private TikaWrapper(){
		parser = new AutoDetectParser();
	}
	
	/**
	 * Returns a singleton instance of the TikaWrapper class
	 * @return
	 */
	public static synchronized TikaWrapper getTika() {
		if (tika==null){
			tika = new TikaWrapper(); 
		}
		return tika;
	}

	/**
	 * Returns a MediaType object representing the mime-type of the specified file.
	 * @param	file		the file to find the mime-type information of
	 * @return	MediaType	an object representing the mime-type information of the specified file
	 * @throws FileNotFoundException
	 */
	public MediaType getMimeType(File file) throws FileNotFoundException {
		MediaType mediaType = null;

		try{
			Metadata metadata = new Metadata();
			TikaInputStream stream = TikaInputStream.get(file, metadata);
			
			try {
				mediaType = ((AutoDetectParser) parser).getDetector().detect(stream, metadata);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				stream.close();
			}
		} catch (FileNotFoundException fnfe){
			throw fnfe;
		} catch (IOException ioe){
			System.err.println("IO Exception: "+ioe);
			ioe.printStackTrace();
		}
        
        return mediaType;
	}

	/**
	 * Parses the specified file using Tika and returns a String containing metadata information
	 * @param	file	the file to parse
	 * @return	String	a String containing metadata information
	 * @throws FileNotFoundException
	 */
	public String parse(File file) throws FileNotFoundException {
		StringBuilder metadataBuffer = new StringBuilder();
		Metadata metadata = new Metadata();

		try{
			TikaInputStream stream = TikaInputStream.get(file, metadata);
			ContentHandler handler = new TeeContentHandler();
			
			try {
				((AutoDetectParser) parser).parse(stream, handler, metadata);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				stream.close();
			}
		} catch (FileNotFoundException fnfe){
			throw fnfe;
		} catch (IOException ioe){
			System.err.println("IO Exception: "+ioe);
			ioe.printStackTrace();
		}
		
		String[] names = metadata.names();
        Arrays.sort(names);
        for (String name : names) {
            metadataBuffer.append(name);
            metadataBuffer.append(": ");
            metadataBuffer.append(metadata.get(name));
            metadataBuffer.append("\n");
        }
        
        return metadata.toString();
	}	
}
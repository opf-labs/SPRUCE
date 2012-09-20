/**
 * 
 */
package org.opf_labs.spruce;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * @author pmay
 *
 */
public class FileMetaInformation {

	private MediaType 	mtype 	  = null;
	private Metadata  	mdata 	  = null;
	private String		wordcloud = "";
	
	public FileMetaInformation(MediaType mtype, Metadata mdata, String wordcloud){
		this.mtype 		= mtype;
		this.mdata		= mdata;
		this.wordcloud  = wordcloud;
	}
	
	public String getWordCloud(){
		return wordcloud;
	}
	
	public MediaType getMediaType(){
		return mtype;
	}
	
	public Metadata getMetadata(){
		return mdata;
	}

}

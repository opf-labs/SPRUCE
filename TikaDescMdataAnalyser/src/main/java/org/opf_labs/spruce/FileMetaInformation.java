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

	private MediaType mtype = null;
	private Metadata  mdata = null;
	
	public FileMetaInformation(MediaType mtype, Metadata mdata){
		this.mtype = mtype;
		this.mdata = mdata;
	}
	
	public MediaType getMediaType(){
		return mtype;
	}
	
	public Metadata getMetadata(){
		return mdata;
	}

}

/**
 * 
 */
package org.opf_labs.spruce;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
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

/**
 * @author carl
 * 
 */
public class DirectoryCharacteriser {

	private final static String METAXMLFN = "tikaextract.xml";
	private final static String METAJSONFN = "tikaextract.json";
	private final static String FULLTEXTFN = "textextract.txt";
	private final static String CHKSUMFN = "chksum.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TikaWrapper TIKA = TikaWrapper.getTika();
		Tika tk = new Tika();
		// TODO Auto-generated method stub
		String rootPath = args[0];
		String outputPath = args[1];

		File rootDir = new File(rootPath);

		int count = 0;

		JavaIOFileSystem fileSys = FileSystems.fromDirectory(rootDir);
		for (FileSystemEntry entry : fileSys.getEntries()) {
			try {

				System.out.println("[" + ++count + "] Processing "
						+ entry.getName());

				InputStream stream = fileSys.getEntryStream(entry.getName());
				ByteStreamId bsi = ByteStreams.fromInputStream(stream);
				stream.close();
				stream = fileSys.getEntryStream(entry.getName());
				MediaType mediaType = TIKA.getMediaType(stream);
				stream.close();
				stream = fileSys.getEntryStream(entry.getName());
				Metadata md = new Metadata();
				Reader reader = TIKA.parse(stream, md);
				stream.close();

				File entryFile = fileSys.getEntryFile(entry.getName());

				String fullText = tk.parseToString(entryFile);

				// Create output directory and save metadata file.
				File outputDir = new File(outputPath + "/" + entry.getName());
				outputDir.mkdirs();

				File xmlOut = new File(outputDir.getAbsolutePath() + "/"
						+ METAXMLFN);
				File jsonOut = new File(outputDir.getAbsolutePath() + "/"
						+ METAJSONFN);
				File ftOut = new File(outputDir.getAbsolutePath() + "/"
						+ FULLTEXTFN);
				File chkOut = new File(outputDir.getAbsolutePath() + "/"
						+ CHKSUMFN);

				md.add("SHA256", bsi.getHexSHA256());
				md.add("Original-File-Path", entry.getName());
				md.add("Original-File-Size", "" + entryFile.length());

				FileUtils.writeStringToFile(xmlOut,
						MetadataFormatter.toXML(md), false);
				FileUtils.writeStringToFile(jsonOut,
						MetadataFormatter.toJSON(md), false);
				FileUtils.writeStringToFile(ftOut, fullText, false);
				FileUtils.writeStringToFile(chkOut, bsi.getHexSHA256(), false);

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
		}
	}

}

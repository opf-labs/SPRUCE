package uk.bl.spruce

import static uk.bl.spruce.Names.*

import org.apache.commons.io.FileUtils
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.opf_labs.spruce.MetadataFormatter
import org.opf_labs.spruce.TikaWrapper
import org.opf_labs.spruce.bytestreams.ByteStreamId
import org.opf_labs.spruce.bytestreams.ByteStreams

class TikaExtractService {
    def process(File f, String outputPath, String colPath) {
		app.event("StatusUpdate", ["TikaExtract: ${f.name}", false])
		
		def TIKA = new TikaWrapper();

		// Create output directory and save metadata file.
		String fname = f.getAbsolutePath();
		File colPathFile = new File(colPath);
		fname = fname.substring(colPath.length()-colPathFile.getName().length(),fname.length());
		
		File outputDir = new File(outputPath + "/" + fname + "_meta" );
		
		outputDir.mkdirs();
		
		// Checksum		
		InputStream stream = new FileInputStream(f);
		ByteStreamId bsi = ByteStreams.fromInputStream(stream);
		stream.close();
		stream = new FileInputStream(f);

		File chkOut = new File(outputDir.getAbsolutePath() + "/"
			+ Names.CHKSUMFN);
		
		FileUtils.writeStringToFile(chkOut, bsi.getHexSHA256(), false);
		
		// Tika full parse (don't matter if it falls over! :-))
		Metadata md = new Metadata();
		Reader reader = TIKA.parse(stream, md);
		stream.close();

		File xmlOut = new File(outputDir.getAbsolutePath() + "/"
				+ Names.METAXMLFN);
		File jsonOut = new File(outputDir.getAbsolutePath() + "/"
				+ Names.METAJSONFN);
			
		md.add("SHA256", bsi.getHexSHA256());
		md.add("Original-File-Path", f.absolutePath);
		md.add("Original-File-Size", "" + f.length());

		FileUtils.writeStringToFile(xmlOut,
				MetadataFormatter.toXML(md), false);
		FileUtils.writeStringToFile(jsonOut,
				MetadataFormatter.toJSON(md), false);
	
		// Tika Full text extract
		Tika tk = new Tika();
		String fullText = tk.parseToString(f);
		File ftOut = new File(outputDir.getAbsolutePath() + "/"	+ Names.FULLTEXTFN);
		FileUtils.writeStringToFile(ftOut, fullText, false);
	}
}
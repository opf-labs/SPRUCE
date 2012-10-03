package uk.bl.spruce

import org.apache.commons.io.FileUtils

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud

class RainCollectionService {
	def process(File f) {
		app.event("StatusUpdate", ["Collection WordCloud: ${f.name}", false])
		
		String fullText = FileUtils.readFileToString(f);
				
		File uniout = new File(f.parent + "/" + Names.UNICOLCLOUDFN);
		File biout = new File(f.parent + "/" + Names.BICOLCLOUDFN);
		
		if (fullText != null && fullText.length() > 0) {
			Cloud cloud = new Cloud();
			cloud.absorb(fullText, 1);
			FileUtils.writeStringToFile(uniout, cloud.toHTMLem());
			cloud = new Cloud();
			cloud.absorb(fullText, 2);
			FileUtils.writeStringToFile(biout, cloud.toHTMLem());
		}
	}
}
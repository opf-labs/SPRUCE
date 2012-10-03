package uk.bl.spruce

import static uk.bl.spruce.Names.*

import org.apache.commons.io.FileUtils

import uk.ac.ox.bodleian.beam.drr.wclouds.Cloud

class RainService {
    def process(File f, File colWordsOut) {
		app.event("StatusUpdate", ["WordCloud: ${f.name}", false])
		
		String fullText = FileUtils.readFileToString(f);
				
		File uniout = new File(f.parent + "/" + Names.UNICLOUDFN);
		File biout = new File(f.parent + "/" + Names.BICLOUDFN);
		
		StringBuilder sb = new StringBuilder();
		
		if (fullText != null && fullText.length() > 0) {
			Cloud cloud = new Cloud();
			cloud.absorb(fullText, 1);
			sb.append(cloud.toHTMLem().replaceAll("\\<.*?>",""));
			FileUtils.writeStringToFile(uniout, cloud.toHTMLem());
			cloud = new Cloud();
			cloud.absorb(fullText, 2);
			sb.append(cloud.toHTMLem().replaceAll("\\<.*?>",""));
			FileUtils.writeStringToFile(biout, cloud.toHTMLem());
			FileUtils.writeStringToFile(colWordsOut, sb.toString(), true);
		}
    }
}
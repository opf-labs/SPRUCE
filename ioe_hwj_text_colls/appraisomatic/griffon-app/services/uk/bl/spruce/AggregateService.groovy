package uk.bl.spruce

import org.apache.commons.io.FileUtils

import argo.jdom.JdomParser
import argo.jdom.JsonNode
import argo.jdom.JsonRootNode
import argo.jdom.JsonStringNode
import argo.saj.InvalidSyntaxException

import static uk.bl.spruce.Names.*

class AggregateService {
	private static final JdomParser JDP = new JdomParser();
	private Entities ents;

    def process(File root) {
		app.event("StatusUpdate", ["Generating Report", false])
		go(root);
    }
	
	private void go(File croot) {
		ents = new Entities(croot.getName());
		walkCollection(croot);
		File f1 = new File(croot.getAbsolutePath() + "/" + Names.UNICOLCLOUDFN);
		File f2 = new File(croot.getAbsolutePath() + "/" + Names.BICOLCLOUDFN);
	
		try {
			ents.setUniCloud(FileUtils.readFileToString(f1));
			ents.setBiCloud(FileUtils.readFileToString(f2));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File summaryFile = new File(croot.getAbsolutePath() + "/" + Names.COLLSUMMARY);
		String summaryHtml = createHtmlSummary(ents);
		try {
			FileUtils.writeStringToFile(summaryFile, summaryHtml);
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
			if (cfile.getName().equals(Names.METAJSONFN)) {
				try {
					String metadata = FileUtils.readFileToString(cfile);
					JsonRootNode json = JDP.parse(metadata);
					Map<JsonStringNode, JsonNode> elements = json.getFields();
					Entity e = new Entity();
					for(JsonNode element: elements.keySet()) {
						e.addField(element.getText(), elements.get(element).getText());
					}
					ents.addEntity(e);
				} catch (IOException e) {

				} catch (InvalidSyntaxException e) {
					// TODO Auto-generated catch block
					System.err.println( "Problem parsing JSON for " + cfile.getAbsolutePath());
					System.err.println( e.getMessage() );
				}
			}
		}
	}
	
	private String createHtmlSummary(Entities ents) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>");
		sb.append("Collection Summary for " + ents.getColName());
		sb.append("</title><!--script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js\"></script-->");
		sb.append("</title></head>");
		sb.append("<body>");
		sb.append("<h1>Collection Summary for " + ents.getColName() + "</h1>\n");
		sb.append("<div>\n");
		sb.append(ents.getUniCloud());
		sb.append(ents.getBiCloud());
		sb.append("</div>");
		sb.append(ents.toString());
		sb.append("</body></html>");
		return sb.toString();
	}
}
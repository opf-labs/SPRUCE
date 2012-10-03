package uk.bl.spruce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entities {
	List<Entity> entities;
	String colName;
	String biCloud;
	String uniCloud;
	
	public String getColName() {
		return colName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getNumberOfEntities() + "<br />\n");
		sb.append(getNumPages() + "<br />\n");
		sb.append(getNumWords() + "<br />\n");
		sb.append(getEarliestDate() + "<br />\n");
		sb.append(getLatestDate() + "<br />\n");
		sb.append(getTotalFileSize() + "<br />\n");
		sb.append(getAuthors() + "<br />\n");
		sb.append(getCompanies() + "<br />\n");
		sb.append(getFormatsAndCounts() + "<br />\n");
		return sb.toString();
	}

	public Entities(String colName) {
		this.entities = new ArrayList<Entity>();
		this.colName = colName;
	}

	public void addEntity(Entity ent) {
		entities.add(ent);
	}

	public String getAuthors() {
		return getCollatedField("Authors", "meta:author");
	}

	public String getCompanies() {
		return getCollatedField("Companies", "extended-properties:company");
	}

	public String getNumberOfEntities() {
		return "<h2>Total Processed Items:</h2>&nbsp; " + entities.size();
	}

	public String getFormatsAndCounts() {
		return getCollatedField("Formats", "Content-Type");
	}

	public String getEmptyFullTexts() {
		return "Not implemented";

	}

	public String getNumPages() {
		return getSumOfField("Total Page Count", "meta:page-count");
	}

	public String getNumWords() {
		return getSumOfField("Total Word Count", "meta:word-count");
	}

	public String getTotalFileSize() {
		return getSumOfField("Total File Size (Originals)",
				"original-file-size");

	}

	public String getLatestDate() {
		Date currDate = null;
		for (Entity ent : entities) {
			String dateStr = ent.getValue("meta:creation-date");
			Date d = getDateFromString(dateStr);
			if (d != null) {
				if (currDate == null) {
					currDate = d;
				} else if (currDate.before(d)) {
					currDate = d;
				}
			}
		}
		return "<h2>Latest Creation Date Found:</h2>" + formatDate(currDate);
	}

	public String getEarliestDate() {
		Date currDate = null;
		for (Entity ent : entities) {
			String dateStr = ent.getValue("meta:creation-date");
			Date d = getDateFromString(dateStr);
			if (d != null) {
				if (currDate == null) {
					currDate = d;
				} else if (currDate.after(d)) {
					currDate = d;
				}
			}
		}
		return "<h2>Earliest Creation Date Found:</h2>" + formatDate(currDate);
	}
	
	private String formatDate(Date currDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = formatter.format(currDate);
        return formattedDate;
	}

	public Date getDateFromString(String str) {
		if (str.contains("T")) {
			String ymd = str.substring(0, str.indexOf("T"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = formatter.parse(ymd);
			} catch (ParseException e) {
				System.err.println("Date parse error " + ymd + " "
						+ e.getMessage());
			}
			return date;
		} else {
			return null;
		}
	}

	private String getSumOfField(String fieldName, String jsonName) {
		int total = 0;
		for (Entity ent : entities) {
			int a = 0;
			try {
				a = Integer.parseInt(ent.getValue(jsonName));
			} catch (NumberFormatException e) {
				// We don't really care about this.
				// Stays at 0 and we add 0.
			}
			total = total + a;
		}
		return "<h2>" + fieldName + ":</h2>&nbsp;" + total + "";
	}

	private String getCollatedField(String fieldName, String jsonName) {
		Map<String, Integer> auths = new HashMap<String, Integer>();
		Map<String, List<String>> authDetails = new HashMap<String, List<String>>();

		for (Entity e : entities) {
			String auth = e.getValue(jsonName);
			if (auths.containsKey(auth)) {
				auths.put(auth, auths.get(auth) + 1);
			} else {
				auths.put(auth, 1);
			}
			if ( authDetails.containsKey(auth)) {
				authDetails.get(auth).add(e.getValue("original-file-path"));
			} else {
				List<String> newList = new ArrayList<String>();
				newList.add(e.getValue("original-file-path"));
				authDetails.put(auth, newList);
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<h2>" + fieldName + ": </h2>");
		for (String a : auths.keySet()) {
			sb.append("<div id=\"header\">" + a + " [" + auths.get(a) + "]</div>\n");
			sb.append("<div id=\"detail\"><ul>");
			for ( String path: authDetails.get(a) ) {
				sb.append("<li>" + path + "</li>");
			}
			sb.append("</ul></div>");
		}
		return sb.toString();
	}

	public void setUniCloud(String str) {
		this.uniCloud = "<h2>1-gram word cloud (by frequency)</h2><div style=\"background: rgb(177,177,177); padding: 10px; border-radius: 5px; \" id=\"unicloud\">" + str + "</div>\n";
	}

	public void setBiCloud(String str) {
		this.biCloud = "<h2>2-gram word cloud (by frequency)</h2><div style=\"background: rgb(177,177,177); padding: 10px; border-radius: 5px;\" id=\"bicloud\">" + str + "</div>\n";
	}

	public String getUniCloud() {
		return uniCloud;
	}

	public String getBiCloud() {
		return biCloud;
	}
}

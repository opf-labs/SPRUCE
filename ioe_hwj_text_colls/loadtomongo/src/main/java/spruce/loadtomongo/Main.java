package spruce.loadtomongo;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Main {

	Mongo m;
	DB db;
	DBCollection coll;
	int count = 0;
	
	public static void main(String[] args) {
		Main man = new Main();
		if (args.length != 1) {
			doErrorAndExit("No parameters");
		}
		File croot = new File(args[0]);
		if (croot.exists() && croot.isDirectory()) {
			man.go(croot);
		}
	}

	private void setupMongo(String cname) {
		try {
			m = new Mongo("localhost");
			db = m.getDB("spruce.txtcols");
			coll = db.getCollection(cname);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void go(File croot) {
		setupMongo(croot.getName());
		walkCollection(croot);
		stopMongo();
	}

	private void stopMongo() {
		m.close();
	}

	private void walkCollection(File cfile) {
		if (cfile.isDirectory()) {
			File[] files = cfile.listFiles();
			for (File file : files) {
				walkCollection(file);
			}
		} else {
			DBObject dbobj = new BasicDBObject();
			
			// Simple XML import/parse of metadata.
			// Fulltext?
			
			// Go.
			
			
			coll.insert(dbobj);
		}
	}

	private static void doErrorAndExit(String msg) {
		System.out.println("\n\n" + msg + "\n\n");
		System.exit(0);
	}
}

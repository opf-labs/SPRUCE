/**
 * 
 */
package org.opf_labs.spruce.ui;

import java.util.EventListener;
import java.util.concurrent.Executor;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.opf_labs.spruce.MetadataAnalyser;
import org.opf_labs.spruce.ThreadListener;
import org.xml.sax.ContentHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author pmay
 *
 */
public class FileAnalyserUI extends Application {
	
	private static final String		TXT_SELECT 		= "Select a directory...";
	private static final String		TXT_PROCESSING	= "Processing...";
	private static final String		TXT_ANALYSE		= "Analyse...";
	private static final String		TXT_CANCEL		= "Cancel";
	
	private boolean processing = false;
	
	// Default configuration file name
	private static final String		CONFIG = "resources/config.properties";
	
	private MetadataAnalyser analyser = null;
	
	Text scenetitle = null;
	TextField dirTextField = null;
	TextField ofTextField = null;
	Text outputText = null;
	Button btn = null;
	
	private int complete = 0;
	
	ThreadListener listener;

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// create a new Metadata Analyser
		analyser = new MetadataAnalyser(CONFIG);	
	
		primaryStage.setTitle("File Metadata Analyser");
		
		GridPane grid = new GridPane();
		
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,25,25,25));
		
		scenetitle = new Text(TXT_SELECT);
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0,0,2,1);
		
		Label directory = new Label("Directory:");
		grid.add(directory, 0, 1);
		
		dirTextField = new TextField();
		grid.add(dirTextField, 1, 1);
		
		Label outputFile = new Label("Results Output File:");
		grid.add(outputFile, 0, 2);
		
		ofTextField = new TextField();
		grid.add(ofTextField, 1, 2);
		
		btn = new Button(TXT_ANALYSE);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BASELINE_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		
		outputText = new Text();
		grid.add(outputText, 1, 6);
		
//		final Text actiontarget = new Text();
//		grid.add(actiontarget,  1, 6);
//		
		// initially not processing
		processing = false;
		
		listener = new ThreadListener(){
			public void notifyUpdate(){
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						complete+=10;
						outputText.setText(complete+"% completed");
					}
				});
			}
			
			public void notifyThreadComplete(){
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						setProcessing(false);
					}
				});
				
			}
		};
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (processing){
					// want to cancel
					setProcessing(false);
					analyser.terminate();					
				} else {
					setProcessing(true);

					// this currently runs asynchronously
			    	runAnalyser(dirTextField.getText(), ofTextField.getText());
				}
			}
		});
		
		Scene scene = new Scene(grid, 400, 275);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
	private void setProcessing(boolean proc){
		processing = proc;
		dirTextField.setDisable(proc);
		ofTextField.setDisable(proc);
		if (proc){
			scenetitle.setText(TXT_PROCESSING);
			btn.setText(TXT_CANCEL);
			outputText.setText(complete+"% completed");
		} else {
			complete = 0;
			outputText.setText("");
			scenetitle.setText(TXT_SELECT);
			btn.setText(TXT_ANALYSE);
		}
	}
	
	private String directoryStr;
	private String outputFileStr;
	
	private void runAnalyser(String dir, String outputFile){
		directoryStr  = dir;
		outputFileStr = outputFile;
		
		Executor exec = new Executor() {
            public void execute(Runnable command) {
                String name = "FileAnalyser";
                Thread thread = new Thread(command, name);
                thread.setDaemon(true);
                thread.start();
            }
        };
        
        exec.execute(new AnalysingTask(this.listener));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
     * The background File Analysis task.
     */
    private class AnalysingTask implements Runnable {
    	
    	ThreadListener listener = null;
    	
    	public AnalysingTask(ThreadListener listener){
    		this.listener = listener;
    	}
    	
        public void run() {
        	System.out.println("Running task");
        	analyser.analyse(directoryStr, outputFileStr, this.listener);
        	this.listener.notifyThreadComplete();
        }
    }
}

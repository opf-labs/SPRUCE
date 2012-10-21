/**
 * 
 */
package org.opf_labs.spruce.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
	Button dirButton = null;
	Button htmlButton = null;
	
	private int complete = 0;
	
	String configFile = "";
	
	ThreadListener listener;

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {
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
		
		InputStream is_img = FileAnalyserUI.class.getClassLoader().getResourceAsStream("resources/Folder_Add.png");
		if (is_img==null){
			// try loading from file
			is_img = new FileInputStream("resources/Folder_Add.png");
		}
		Image img_folder_add = new Image(is_img);
		ImageView iv_folder_add = new ImageView(img_folder_add);
		iv_folder_add.setFitHeight(16);
		iv_folder_add.setPreserveRatio(true);

		dirButton = new Button();
		dirButton.setGraphic(iv_folder_add);
		dirButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser directoryChooser = new DirectoryChooser(); 
                directoryChooser.setTitle("Select a folder to analyse...");
				//Show open file dialog
				File file = directoryChooser.showDialog(primaryStage);
				if(file!=null){
					dirTextField.setText(file.getPath());
				}
			}
		});
		
		grid.add(dirButton, 2, 1);
		
		Label outputFile = new Label("Results Output File:");
		grid.add(outputFile, 0, 2);
		
		ofTextField = new TextField();
		grid.add(ofTextField, 1, 2);
		
		InputStream is_img_file = FileAnalyserUI.class.getClassLoader().getResourceAsStream("resources/File_HTML.png");
		if (is_img_file==null){
			// try loading from file
			is_img_file = new FileInputStream("resources/File_HTML.png");
		}
		Image img_file_html = new Image(is_img_file);
		ImageView iv_file_html = new ImageView(img_file_html);
		iv_file_html.setFitHeight(16);
		iv_file_html.setPreserveRatio(true);

		htmlButton = new Button();
		htmlButton.setGraphic(iv_file_html);
		htmlButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save results to (HTML file) ...");
				
				//Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML pages", "*.htm", "*.html", "*.xhtml");
				fileChooser.getExtensionFilters().add(extFilter);
				
				//Show save file dialog
				File file = fileChooser.showSaveDialog(primaryStage);
				if(file!=null){
					String path = file.getPath();
					if(!file.getName().matches(".*\\.[Xx]?[Hh][Tt][Mm][Ll]?")){
						int index = path.lastIndexOf(".");
						if (index==-1){
							index = path.length();
						}
						path = path.substring(0, index)+".html";
					}
					ofTextField.setText(path);
				}
			}
		});
		
		grid.add(htmlButton, 2, 2);
		
		
		btn = new Button(TXT_ANALYSE);
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BASELINE_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		
		outputText = new Text();
		grid.add(outputText, 1, 6);
		
		final Label configSelect = new Label("Choose new Metadata config file...");
		configSelect.setTextFill(Color.web("#0076a3"));
		
		configSelect.setOnMouseEntered(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent e) {
		    	configSelect.setScaleX(1.2);
		    	configSelect.setScaleY(1.2);
		    }
		});

		configSelect.setOnMouseExited(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent e) {
		    	configSelect.setScaleX(1);
		    	configSelect.setScaleY(1);
		    }
		});
		
		configSelect.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				if(!configFile.equals("")){
					fileChooser.setInitialDirectory(new File(configFile));
				}
				fileChooser.setTitle("Select a new Metadata properties file ...");
				
				//Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Metadata Properties", "*.properties");
				fileChooser.getExtensionFilters().add(extFilter);
				
				//Show save file dialog
				File file = fileChooser.showOpenDialog(primaryStage);
				if(file!=null){
					configFile = file.getPath();
					analyser.loadConfiguration(configFile);
				}
			}
		});
		
		HBox hbCSel = new HBox(10);
		hbCSel.setAlignment(Pos.BASELINE_RIGHT);
		hbCSel.getChildren().add(configSelect);
		grid.add(hbCSel, 1, 8, 2, 1);
		
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
		dirButton.setDisable(proc);
		htmlButton.setDisable(proc);
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

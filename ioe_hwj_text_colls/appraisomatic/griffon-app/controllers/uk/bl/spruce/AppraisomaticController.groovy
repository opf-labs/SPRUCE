package uk.bl.spruce

import griffon.transform.Threading

import javax.swing.JFileChooser
import static uk.bl.spruce.Names.*

class AppraisomaticController {
	// these will all be injected
	def model
	def view
	def tikaExtractService
	def rainService
	def rainCollectionService
	def aggregateService

	int count = 0

	def clear = {
		// clears all properties in the model.
		model.griffonClass.propertyNames.each {  name ->
			model[name] = ''
		}
	}
	
	def submit = {
		javax.swing.JOptionPane.showMessageDialog(
			app.windowManager.windows.find{it.focused},
			model.collectionPath
		)
	}
	
	def setCollectionPath = {
		def fc = view.fileChooserWindow.showOpenDialog()
		if ( fc != JFileChooser.APPROVE_OPTION ) return
		model.collectionPath = view.fileChooserWindow.selectedFile.absolutePath
	}
	
	def setOutputPath = {
		def fc = view.fileChooserWindow.showOpenDialog()
		if ( fc != JFileChooser.APPROVE_OPTION ) return
		model.outputPath = view.fileChooserWindow.selectedFile.absolutePath
	}
	
	def process = {
		// Steps
		// 1. Extract metadata and write checksum, json, fulltext
		// 2. Create cloud for each item and also for collection
		// 3. Create aggregation report
		count = 0
		File oroot = new File(model.outputPath)
		File iroot = new File(model.collectionPath)
		File collated = new File(model.outputPath + "/" + iroot.name + "/" + Names.COLATEFN);
		tikaTreeWalk(iroot)
		rainTreeWalk(oroot, collated)
		rainCollectionService.process(collated)
		aggregateService.process(new File(collated.parent));
		model.statusMsg = "All files processed"
	}
		
	def quit = {
		app.shutdown()
	}
	
	def tikaTreeWalk(fn) {
		if ( fn.isDirectory() ) {
			for(File f: fn.listFiles()) {
				tikaTreeWalk(f)
			}
		} else {
			app.event("StatusUpdate", ["Processing: ${fn.name}", true]);
			try {
				tikaExtractService.process(fn, model.outputPath, model.collectionPath);
			} catch (Exception e) {
				//TODO - save this to a file or sommit. Usually these are when Tika
				// has messed up its ID and is using the wrong parser (eg. Groovy id'd as XML!)
				println("Didn't process ${fn.name} ${e.message}");
			}
		}
	}
	
	def rainTreeWalk(fn, collated) {
		if ( fn.isDirectory() ) {
			for(File f: fn.listFiles()) {
				rainTreeWalk(f, collated)
			}
		} else {
			// Only create cloud on discovery of a fulltext file...
			if ( fn.name.equals(Names.FULLTEXTFN)) {
				try {
					rainService.process(fn, collated);
				} catch (Exception e) {
					//TODO - save this to a file or sommit. Usually these are when Tika
					// has messed up its ID and is using the wrong parser (eg. Groovy id'd as XML!)
					println("Unable to create word cloud for ${fn.name} ${e.message}");
				}
			}
		}
	}
	
	@Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def onStatusUpdate = { message, incr ->
		if ( incr ) {
			count++;
		}
		String text = "${count}: ${message}"
		model.statusMsg = text
	}
}

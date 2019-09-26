package de.unituebingen.medizin.eye.ciloct.ui;

import java.io.File;
import java.util.prefs.Preferences;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * @author strasser
 */
public class ImageFileChooser {
	public static final FileChooser.ExtensionFilter EXT_FILTER_DICOM = new FileChooser.ExtensionFilter("DICOM files", "*.dcm");
	public static final FileChooser.ExtensionFilter EXT_FILTER_IMAGE = new FileChooser.ExtensionFilter("Image files", "*.png", "*.bmp", "*.jpg"); 

	public static final String PREF_LAST_DIRECTORY = "lastDirectory";
	public static final String DEFAULT_DIRECTORY	= ".";

	private FileChooser.ExtensionFilter _selectedExtensionFilter;

	public File showOpenDialog(final Window window) {
		_selectedExtensionFilter = null;

		final FileChooser chooser = new FileChooser();

		chooser.setTitle("Open");
		chooser.getExtensionFilters().addAll(EXT_FILTER_DICOM, EXT_FILTER_IMAGE);
		final File initial = new File(Preferences.userNodeForPackage(getClass()).get(PREF_LAST_DIRECTORY, DEFAULT_DIRECTORY));
		if (initial.isDirectory()) {	
			chooser.setInitialDirectory(initial);
		}

		final File file = chooser.showOpenDialog(window);
		if (file != null) {
			Preferences.userNodeForPackage(getClass()).put(PREF_LAST_DIRECTORY, file.getParent());
			_selectedExtensionFilter = chooser.getSelectedExtensionFilter();

			return file;
		}

		return null;
	}

	public FileChooser.ExtensionFilter getSelectedExtensionFilter() throws IllegalStateException {
		if (_selectedExtensionFilter == null) {
			throw new IllegalStateException("No file selected.");
		}
		return _selectedExtensionFilter;
	}
}
package de.unituebingen.medizin.eye.ciloct;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * FXML Controller class
 *
 * @author strasser
 */
public class SettingsDialogController implements Initializable {
	private final DoubleProperty _pixelProperty = new SimpleDoubleProperty();
	private final DoubleProperty _widthProperty = new SimpleDoubleProperty();
	private final DoubleProperty _resolutionProperty = new SimpleDoubleProperty();

	private final DoubleProperty _refractiveIndexAqueousFluid = new SimpleDoubleProperty();
	private final DoubleProperty _refractiveIndexAir = new SimpleDoubleProperty();
	private final DoubleProperty _refractiveIndexSclera = new SimpleDoubleProperty();
	private final DoubleProperty _refractiveIndexCiliaryMuscle = new SimpleDoubleProperty();

	private final DoubleProperty _cmEvalLength = new SimpleDoubleProperty();

	private final StringProperty _caffePrototxtFile = new SimpleStringProperty();
	private final StringProperty _caffeWeightsFile = new SimpleStringProperty();
	private final BooleanProperty _useGPU = new SimpleBooleanProperty(false);

	private final IntegerProperty _port = new SimpleIntegerProperty();
	private final IntegerProperty _epoch = new SimpleIntegerProperty();
	private final StringProperty _server = new SimpleStringProperty();
	private final StringProperty _user = new SimpleStringProperty();
	private final StringProperty _job = new SimpleStringProperty();
	private final StringProperty _sshUser = new SimpleStringProperty();
	private final StringProperty _sshPassword = new SimpleStringProperty();
	private final BooleanProperty _useSSH = new SimpleBooleanProperty();
	private final BooleanProperty _localOpenCV = new SimpleBooleanProperty();
	private final BooleanProperty _remoteDIGITS = new SimpleBooleanProperty();

	private boolean _cancelled = false;

	@FXML
	private TextField pixelTextField;

	@FXML
	private TextField widthTextField;

	@FXML
	private TextField resolutionTextField;

	@FXML
	private TextField refractiveIndexAqueousFluidTextField;

	@FXML
	private TextField refractiveIndexAirTextField;

	@FXML
	private TextField refractiveIndexScleraTextField;

	@FXML
	private TextField refractiveIndexCiliaryMuscleTextField;

	@FXML
	private TextField cmEvalLengthTextField;

	@FXML
	private TextField caffePrototxtFileTextField;

	@FXML
	private TextField caffeWeightsFileTextField;

	@FXML
	private CheckBox useGPUCheckbox;

	@FXML
	private TextField serverTextField;

	@FXML
	private TextField portTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField jobTextField;

	@FXML
	private TextField epochTextField;

	@FXML
	private CheckBox useSSHTunnelCheckbox;

	@FXML
	private TextField sshUsernameTextField;

	@FXML
	private TextField sshPasswordPasswordField;

	@FXML
	private RadioButton openCVRadioButton;

	@FXML
	private RadioButton remoteDIGITSRadioButton;

	private Stage _stage;

	/**
	 * Initializes the controller class.
	 * @param url
	 * @param rb
	 */
	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		caffePrototxtFileTextField.textProperty().bindBidirectional(_caffePrototxtFile);
		caffeWeightsFileTextField.textProperty().bindBidirectional(_caffeWeightsFile);
		useGPUCheckbox.selectedProperty().bindBidirectional(_useGPU);

		portTextField.setTextFormatter(new TextFormatter<>(new FormatStringConverter<>(DecimalFormat.getIntegerInstance())));
		Bindings.bindBidirectional(portTextField.textProperty(), _port, new NumberStringConverter(DecimalFormat.getIntegerInstance()));
		epochTextField.setTextFormatter(new TextFormatter<>(new FormatStringConverter<>(DecimalFormat.getIntegerInstance())));
		Bindings.bindBidirectional(epochTextField.textProperty(), _epoch, new NumberStringConverter(DecimalFormat.getIntegerInstance()));
		serverTextField.textProperty().bindBidirectional(_server);
		usernameTextField.textProperty().bindBidirectional(_user);
		jobTextField.textProperty().bindBidirectional(_job);
		useSSHTunnelCheckbox.selectedProperty().bindBidirectional(_useSSH);
		sshUsernameTextField.textProperty().bindBidirectional(_sshUser);
		sshPasswordPasswordField.textProperty().bindBidirectional(_sshPassword);

		openCVRadioButton.selectedProperty().bindBidirectional(_localOpenCV);
		remoteDIGITSRadioButton.selectedProperty().bindBidirectional(_remoteDIGITS);

		pixelTextField.setTextFormatter(new TextFormatter<>(new FormatStringConverter<>(DecimalFormat.getIntegerInstance())));
		widthTextField.setTextFormatter(new TextFormatter<>(new FormatStringConverter<>(DecimalFormat.getIntegerInstance())));

		pixelTextField.textProperty().bindBidirectional(_pixelProperty, new NumberStringConverter());
		widthTextField.textProperty().bindBidirectional(_widthProperty, new NumberStringConverter());
		resolutionTextField.textProperty().bind(_resolutionProperty.asString());
		_resolutionProperty.bind(_pixelProperty.divide(_widthProperty));

		refractiveIndexAqueousFluidTextField.textProperty().bindBidirectional(_refractiveIndexAqueousFluid, new NumberStringConverter());
		refractiveIndexAirTextField.textProperty().bindBidirectional(_refractiveIndexAir, new NumberStringConverter());
		refractiveIndexScleraTextField.textProperty().bindBidirectional(_refractiveIndexSclera, new NumberStringConverter());
		refractiveIndexCiliaryMuscleTextField.textProperty().bindBidirectional(_refractiveIndexCiliaryMuscle, new NumberStringConverter());

		cmEvalLengthTextField.textProperty().bindBidirectional(_cmEvalLength, new NumberStringConverter());
	}

	@FXML
	private void cancel(final ActionEvent e) {
		_cancelled = true;
		close();
	}

	@FXML
	private void save(final ActionEvent e) {
		_cancelled = false;
		close();
	}

	@FXML
	private void openCaffePrototxtFile(final ActionEvent e) {
		final FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Caffe deploy prototxt");
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Net deploy.prototxt", "*.prototxt"));
		final File initial = new File(Preferences.userNodeForPackage(getClass()).node("caffe").node("opencv").get("lastPrototxtDirectory", "."));
		if (initial.isDirectory()) {
			chooser.setInitialDirectory(initial);
		}
		final File file = chooser.showOpenDialog(((Node) e.getTarget()).getScene().getWindow());
		if (file != null) {
			Preferences.userNodeForPackage(getClass()).node("caffe").node("opencv").put("lastPrototxtDirectory", file.getParent());
			_caffePrototxtFile.setValue(file.getAbsolutePath());
		}
	}

	@FXML
	private void openCaffeWeightsFile(final ActionEvent e) {
		final FileChooser chooser = new FileChooser();
		chooser.setTitle("Open Caffe model weights");
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Net weights", "*.caffemodel"));
		final File initial = new File(Preferences.userNodeForPackage(getClass()).node("caffe").node("opencv").get("lastWeightsDirectory", "."));
		if (initial.isDirectory()) {
			chooser.setInitialDirectory(initial);
		}
		final File file = chooser.showOpenDialog(((Node) e.getTarget()).getScene().getWindow());
		if (file != null) {
			Preferences.userNodeForPackage(getClass()).node("caffe").node("opencv").put("lastWeightsDirectory", file.getParent());
			_caffeWeightsFile.setValue(file.getAbsolutePath());
		}
	}

	private void close() {
		_stage.close();
	}

	void setStage(final Stage stage) {
		_stage = stage;
	}

	void setWidth(final double width) {
		_widthProperty.set(width);
	}

	void setPixel(final double pixel) {
		_pixelProperty.set(pixel);
	}

	double getWidth() {
		return _widthProperty.get();
	}

	double getPixel() {
		return _pixelProperty.get();
	}

	double getRefractiveIndexAir() {
		return _refractiveIndexAir.get();
	}

	double getRefractiveIndexSclera() {
		return _refractiveIndexSclera.get();
	}

	double getRefractiveIndexCiliaryMuscle() {
		return _refractiveIndexCiliaryMuscle.get();
	}

	double getRefractiveIndexAqueousFluid() {
		return _refractiveIndexAqueousFluid.get();
	}

	double getCiliaryMuscleEvaluationLength() {
		return _cmEvalLength.get();
	}

	void setRefractiveIndexAir(final double refractiveIndexAir) {
		_refractiveIndexAir.set(refractiveIndexAir);
	}

	void setRefractiveIndexSclera(final double refractiveIndexSclera) {
		_refractiveIndexSclera.set(refractiveIndexSclera);
	}

	void setRefractiveIndexAqueousFluid(final double refractiveIndexAqueousFluid) {
		_refractiveIndexAqueousFluid.set(refractiveIndexAqueousFluid);
	}

	void setRefractiveIndexCiliaryMuscle(final double refractiveIndexCiliaryMuscle) {
		_refractiveIndexCiliaryMuscle.set(refractiveIndexCiliaryMuscle);
	}

	void setCiliaryMuscleEvaluationLength(final double cmEvalLength) {
		_cmEvalLength.set(cmEvalLength);
	}

	int getPort() {
		return _port.get();
	}

	void setPort(final int port) {
		_port.set(port);
	}

	int getEpoch() {
		return _epoch.get();
	}

	void setEpoch(final int epoch) {
		_epoch.set(epoch);
	}

	String getServer() {
		return _server.get();
	}

	void setServer(final String server) {
		_server.set(server);
	}

	String getUser() {
		return _user.get();
	}

	void setUser(final String user) {
		_user.set(user);
	}

	String getJob() {
		return _job.get();
	}

	void setJob(final String job) {
		_job.set(job);
	}

	String getSSHUser() {
		return _sshUser.get();
	}

	void setSSHUser(final String sshUser) {
		_sshUser.set(sshUser);
	}

	String getSSHPassword() {
		return _sshPassword.get();
	}

	void setSSHPassword(final String password) {
		_sshPassword.set(password);
	}

	boolean isUseSSH() {
		return _useSSH.get();
	}

	void setUseSSH(final boolean useSSH) {
		_useSSH.set(useSSH);
	}

	String getCaffePrototxtFile() {
		return _caffePrototxtFile.get();
	}

	void setCaffePrototxtFile(final String caffePrototxtFile) {
		_caffePrototxtFile.set(caffePrototxtFile);
	}

	String getCaffeWeightsFile() {
		return _caffeWeightsFile.get();
	}

	void setCaffeWeightsFile(final String caffeWeightsFile) {
		_caffeWeightsFile.set(caffeWeightsFile);
	}

	boolean isUseGPU() {
		return _useGPU.get();
	}

	void setUseGPU(final boolean useGPU) {
		_useGPU.set(useGPU);
	}

	boolean isLocalOpenCV() {
		return _localOpenCV.get();
	}

	void setLocalOpenCV(final boolean localOpenCV) {
		_localOpenCV.set(localOpenCV);
	}

	boolean isRemoteDIGITS() {
		return _remoteDIGITS.get();
	}

	void setRemoteDIGITS(final boolean remoteDIGITS) {
		_remoteDIGITS.set(remoteDIGITS);
	}

	boolean isCancelled() {
		return _cancelled;
	}
}
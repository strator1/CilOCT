package de.unituebingen.medizin.eye.ciloct;

import de.unituebingen.medizin.eye.ciloct.util.Skeletonize;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.pixelduke.control.ribbon.QuickAccessBar;
import com.pixelduke.control.ribbon.RibbonGroup;
import com.pixelduke.control.ribbon.RibbonTab;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.unituebingen.medizin.eye.ciloct.refract.Interface;
import de.unituebingen.medizin.eye.ciloct.refract.Rays;
import de.unituebingen.medizin.eye.ciloct.task.ExceptionCatchingTask;
import de.unituebingen.medizin.eye.ciloct.draw.AngleDrawable;
import de.unituebingen.medizin.eye.ciloct.draw.Caliper;
import de.unituebingen.medizin.eye.ciloct.draw.FunctionDrawable;
import de.unituebingen.medizin.eye.ciloct.draw.IDrawable;
import de.unituebingen.medizin.eye.ciloct.events.RepaintEventHandler;
import de.unituebingen.medizin.eye.ciloct.javafx.OptionalObjectProperty;
import de.unituebingen.medizin.eye.ciloct.javafx.FontAwesomeCursor;
import de.unituebingen.medizin.eye.ciloct.vo.Angle;
import de.unituebingen.medizin.eye.ciloct.ui.ImageFileChooser;
import de.unituebingen.medizin.eye.ciloct.vo.Point;
import de.unituebingen.medizin.eye.ciloct.draw.Landmark;
import de.unituebingen.medizin.eye.ciloct.draw.POI;
import de.unituebingen.medizin.eye.ciloct.draw.PointDrawable;
import de.unituebingen.medizin.eye.ciloct.draw.PointListDrawable;
import de.unituebingen.medizin.eye.ciloct.vo.BoundedPolynomialSplineFunction;
import de.unituebingen.medizin.eye.ciloct.vo.Line;
import de.unituebingen.medizin.eye.ciloct.vo.PointList;
import de.unituebingen.medizin.eye.ciloct.matrix.ImageDataMatrix;
import de.unituebingen.medizin.eye.ciloct.events.RepaintEvent;
import de.unituebingen.medizin.eye.ciloct.events.UpdateManager;
import de.unituebingen.medizin.eye.ciloct.exception.CalculationException;
import de.unituebingen.medizin.eye.ciloct.exception.FitException;
import de.unituebingen.medizin.eye.ciloct.exception.GradientException;
import de.unituebingen.medizin.eye.ciloct.exception.POIException;
import de.unituebingen.medizin.eye.ciloct.javafx.RadiusCellFactory;
import de.unituebingen.medizin.eye.ciloct.math.LinearInterpolator;
import de.unituebingen.medizin.eye.ciloct.util.FloodSelect;
import de.unituebingen.medizin.eye.ciloct.util.MatrixUtil;
import de.unituebingen.medizin.eye.ciloct.util.ImageUtil;
import de.unituebingen.medizin.eye.ciloct.util.MathUtil;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collector;
import static java.util.stream.Collectors.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import de.unituebingen.medizin.eye.ciloct.math.PolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.refract.IInterface;
import de.unituebingen.medizin.eye.ciloct.refract.InterfacePointList;
import de.unituebingen.medizin.eye.ciloct.serialization.LandmarkMapper;
import de.unituebingen.medizin.eye.ciloct.serialization.LandmarksVO;
import de.unituebingen.medizin.eye.ciloct.util.Util;
import de.unituebingen.medizin.eye.ciloct.vo.BoundedPolynomialFunction;
import de.unituebingen.medizin.eye.ciloct.vo.IBoundedFunction;
import de.unituebingen.medizin.eye.ciloct.vo.Profile;
import de.unituebingen.medizin.eye.ciloct.vo.ProfilePoint;
import de.unituebingen.medizin.eye.ciloct.vo.SegmentationPoint;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;
import org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.TagUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class CilOCTController implements Initializable {
	private static final boolean SHOW_CNN_RIBBONGROUP				= true;
	private static final boolean USE_ANGLES							= false;

	private static final Cursor REMOVE_LANDMARK_CURSOR				= FontAwesomeCursor
																		.glyph(MaterialDesignIcon.ERASER)
																		.hotspot(FontAwesomeCursor.POS.BOTTOM_LEFT)
																		.create()
																	;//new ImageCursor(new Image("cursor_circle.png"), 16, 16);
	private static final Cursor BRUSH_LIGHTEN_CURSOR				= FontAwesomeCursor
																		.glyph(FontAwesomeIcon.PAINT_BRUSH)
																		.size(20).hotspot(FontAwesomeCursor.POS.BOTTOM_LEFT)
																		.paint(Color.LIGHTGREY)
																		.create()
																	;//new ImageCursor(new Image("cursor_brush_lighten.png"), 16, 16);
	private static final Cursor BRUSH_DARKEN_CURSOR					= FontAwesomeCursor
																		.glyph(FontAwesomeIcon.PAINT_BRUSH)
																		.size(20).hotspot(FontAwesomeCursor.POS.BOTTOM_LEFT)
																		.paint(Color.DARKGREY)
																		.create()
																	;//new ImageCursor(new Image("cursor_brush_darken.png"), 16, 16);
	private static final Cursor LENS_START_POINT_CURSOR				= FontAwesomeCursor
																		.glyph(MaterialDesignIcon.TARGET)
																		.size(20).hotspot(FontAwesomeCursor.POS.CENTER)
																		.paint(Color.AQUAMARINE)
																		.create()
																	;
	private static final Cursor ANTERIOUR_CHAMBER_POINT_CURSOR		= FontAwesomeCursor
																		.glyph(MaterialDesignIcon.TARGET)
																		.size(20).hotspot(FontAwesomeCursor.POS.CENTER)
																		.paint(Color.ORCHID)
																		.create()
																	;
	private static final Cursor CROSSHAIR_CURSOR					= Cursor.CROSSHAIR;
	private static final Cursor DRAG_HAND_CURSOR					= Cursor.CLOSED_HAND;
	private static final int REMOVE_RADIUS							= 8;
	private static final double ZOOM_FACTOR							= 1.15;
	private static final int BRUSH_VALUE							= 10;

	private static final int INNER_ANGLE_INTERPOLATION_LENGTH		= 40;
	private static final int CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH = 15;
	private static final int RANGE									= 4;

	private static final Color COLOR_CILIARY_MUSCLE_AREA			= new Color(1d, 0d, 0d, 0.1d);
	private static final Color COLOR_OUTER_CILIARY_MUSCLE_BORDER	= Color.RED.desaturate();
	private static final Color COLOR_INNER_CILIARY_MUSCLE_BORDER	= Color.RED.desaturate();
	private static final Color COLOR_VERTICAL_CILIARY_MUSCLE_BORDER = Color.RED.desaturate();
	private static final Color COLOR_OUTER_CONJUNCTIVA_BORDER		= Color.THISTLE;//Color.ORANGE.desaturate();
	private static final Color COLOR_OUTER_CONJUNCTIVA_EXTRAPOLATED_BORDER = Color.ORANGE;
	private static final Color COLOR_INNER_SCLERAL_BORDER			= Color.ORANGE.desaturate();
	private static final Color COLOR_UPPER_IRIS_BORDER				= Color.YELLOW.desaturate();
	private static final Color COLOR_OUTER_CILIARY_MUSCLE_LANDMARK	= Color.LIME;
	private static final Color COLOR_INNER_CILIARY_MUSCLE_LANDMARK	= Color.CYAN;
	private static final Color COLOR_VERTICAL_CILIARY_MUSCLE_LANDMARK = Color.YELLOW;
	private static final Color COLOR_LENS_BORDER					= Color.AQUAMARINE;
	private static final Color COLOR_LENS_AREA						= new Color(0.49803922d, 1.0d, 0.1d, 0.1d);

	private static final double EXPORT_SCALE						= 0.5;

	private final ExecutorService _executor = Executors.newCachedThreadPool();
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> _executor.shutdownNow()));
	}

	private final StandardPBEStringEncryptor ENCRYPTOR = new StandardPBEStringEncryptor();
	{
		ENCRYPTOR.setPassword(getClass().getName());
	}

	final StringProperty _fileProperty = new SimpleStringProperty();

	private final DoubleProperty _probability = new SimpleDoubleProperty(0.85);
	private Map<GroundTruthPalette, List<SegmentationPoint>> _segmentation;

	private final BooleanProperty _useAI = new SimpleBooleanProperty(false);
	private final StringProperty _patientName = new SimpleStringProperty("");
	private final ObjectProperty<Date> _acquisitionDateTime = new SimpleObjectProperty<Date>();
	private final StringProperty _eye = new SimpleStringProperty();
	private final BooleanProperty _ready = new SimpleBooleanProperty(false);
	private final BooleanProperty _segmented = new SimpleBooleanProperty(false);
	private final BooleanProperty _corrected = new SimpleBooleanProperty(false);
	private final IntegerProperty _width = new SimpleIntegerProperty(1280);
	private final IntegerProperty _height = new SimpleIntegerProperty(512);
	private final DoubleProperty _scale = new SimpleDoubleProperty(1);
	private final DoubleProperty _translateX = new SimpleDoubleProperty(0);
	private final DoubleProperty _translateY = new SimpleDoubleProperty(0);
	private final ObjectProperty<Cursor> _cursor = new SimpleObjectProperty<>(Cursor.DEFAULT);
	private final ObjectProperty<Double> _thresholdFactor = new SimpleObjectProperty(1d);
	private final ObjectProperty<Double> _lensThresholdFactor = new SimpleObjectProperty(1.2);

	private final OptionalObjectProperty<Point> _lensStartPoint = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<PointList> _lensPoints = new OptionalObjectProperty();
	private final OptionalObjectProperty<PointList> _upperLensBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<PointList> _lowerLensBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<IBoundedFunction> _upperLensFit = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<IBoundedFunction> _lowerLensFit = new OptionalObjectProperty<>();

	private final OptionalObjectProperty<PointList> _airPoints = new OptionalObjectProperty();

	private final OptionalObjectProperty<Point> _anteriorChamberStartPoint = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<PointList> _anteriorChamberPoints = new OptionalObjectProperty();

	private final ObservableList<Point> _outerCiliaryMuscleBorderLandmarks = FXCollections.observableArrayList();
	private final OptionalObjectProperty<PointList>_outerCiliaryMuscleBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<BoundedPolynomialSplineFunction> _outerCiliaryMuscleBorderFit = new OptionalObjectProperty<>();

	private final ObservableList<Point> _innerCiliaryMuscleBorderLandmarks = FXCollections.observableArrayList();
	private final OptionalObjectProperty<PointList> _innerCiliaryMuscleBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<BoundedPolynomialSplineFunction> _innerCiliaryMuscleBorderFit = new OptionalObjectProperty<>();

	private final OptionalObjectProperty<PointList> _outerConjunctivaBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<BoundedPolynomialSplineFunction> _outerConjunctivaBorderFit = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<IBoundedFunction> _outerConjunctivaBorderExtrapolationFit = new OptionalObjectProperty<>();

	private final OptionalObjectProperty<PointList> _innerSclearalBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<BoundedPolynomialSplineFunction> _innerScleralBorderFit = new OptionalObjectProperty<>();

	private final OptionalObjectProperty<PointList> _upperIrisBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<BoundedPolynomialSplineFunction> _upperIrisBorderFit = new OptionalObjectProperty<>();

	private final ObservableList<Point> _verticalCiliaryMuscleBorderLandmarks = FXCollections.observableArrayList();
	private final OptionalObjectProperty<PointList> _verticalCiliaryMuscleBorderGradients = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<PointList> _verticalCiliaryMuscleBorderFitPoints = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<PointList> _ciliaryMuscleAreaPoints = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Double> _ciliaryMuscleArea = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Double> _ciliaryApexShift = new OptionalObjectProperty<>();

	private final OptionalObjectProperty<Line> _perpendicularAxisCiliaryMuscle = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Point> _ciliaryApex = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Point> _scleralSpur = new OptionalObjectProperty<>();
	
	private final OptionalObjectProperty<Point> _innerAngleRecess = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Angle> _ciliaryApexAngle = new OptionalObjectProperty<>();
	private final OptionalObjectProperty<Angle> _innerAngle = new OptionalObjectProperty<>();

	private final IntegerProperty _brushRadius = new SimpleIntegerProperty(8);

	private final ObservableSet<Result> _results = FXCollections.observableSet(new LinkedHashSet<>());

	private final ObservableList<IDrawable> _debug = FXCollections.observableArrayList();
	private final ObservableList<Point> _debug2 = FXCollections.observableArrayList();

	private final OptionalObjectProperty<Profile> _profile = new OptionalObjectProperty<>();

	private final BooleanProperty _gradientsSet = new SimpleBooleanProperty(false);

	private final BooleanProperty _calculateArea = new SimpleBooleanProperty(true);
	private final BooleanProperty _calculateProfile = new SimpleBooleanProperty(true);

	private final List<Brush> _brushList = new ArrayList<>();

	private final boolean[] _layerVisibilityTemp = { false, false, false, false, false, false };
	private final BiConsumer<ToggleButton, Integer> getAndSet = (c, i) -> { final boolean temp = c.selectedProperty().get(); c.selectedProperty().set(_layerVisibilityTemp[i]); _layerVisibilityTemp[i] = temp; };

	private Cursor _prevCursor = CROSSHAIR_CURSOR;
	private Cursor _prevExitCursor = _prevCursor;

	private double _mouseAnchorX = 0;
	private double _mouseAnchorY = 0;
	private boolean _dragging = false;
	private double _startThreshold = 127;

	private ImageDataMatrix _srcMatrix;
	private ImageDataMatrix _srcMatrixSave;
	private RealMatrix _verticalGradientMatrix;

	private int _limitLeft = -1;
	private int _limitRight = -1;
	private int[] _limitLower;

	private boolean _newThresholdCalculation = true;

	private double _refractiveIndexAir;
	private double _refractiveIndexCiliaryMuscle;
	private double _refractiveIndexSclera;
	private double _refractiveIndexAqueousFluid;
	private double _calWidth;
	private double _calPixel;
	private double _cmEvalLength;
	private double _mmPerPixel;

	private int _port;
	private int _epoch;
	private String _server;
	private String _user;
	private String _job;
	private String _sshUser;
	private String _sshPassword;
	private boolean _useSSH;
	private String _caffePrototxtFile;
	private String _caffeWeightsFile;
	private boolean _useGPU;
	private boolean _localOpenCV;
	private boolean _remoteDIGITS;


	private boolean _maximized = false;
	private Rectangle2D _stageSize;

	@FXML
	RibbonTab cnnSegmentationTab;

	@FXML
	RibbonGroup cnnRibbonGroup;

	@FXML
	RibbonGroup cnnToolsRibbonGroup;

	@FXML
	QuickAccessBar quickAccessBar;

	@FXML
	private Button toggleLayersButton;

	@FXML
	private ComboBox<Integer> brushRadiusComboBox;

	@FXML
	private Label fileLabel;

	@FXML
	private Label zoomLabel;

	@FXML
	private Label thresholdLabel;

	@FXML
	private Button skeletonizeButton;

	@FXML
	private Button clusterButton;

	@FXML
	private Button histogramButton;

	@FXML
	private Button stretchHistogramButton;

	@FXML
	private ToggleButton useAIToggleButton;

	@FXML
	private CheckBox useHeadersCheckBox;

	@FXML
	private Button copyResultsButton;

	@FXML
	private Button clearResultsButton;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private Pane blockingPane;

	@FXML
	private Slider contrastSlider;

	@FXML
	private Slider probabilitySlider;

	@FXML
	private TextField contrastTextField;

	@FXML
	private TextField probabilityTextField;

	@FXML
	private Button copyProfilesButton;

	@FXML
	private Button saveLandmarksButton;

	@FXML
	private Button loadLandmarksButton;

	@FXML
	private Button saveLandmarksButton2;

	@FXML
	private Button loadLandmarksButton2;

	@FXML
	private Canvas gradientCanvas;

	@FXML
	private ToggleButton gradientCanvasToggleButton;

	@FXML
    private Canvas debugCanvas;

	@FXML
    private Canvas imageCanvas;

	@FXML
	private Canvas segmentationCanvas;

	@FXML
	private Canvas poiCanvas;

	@FXML
	private Canvas caliperCanvas;

	@FXML
	private ToggleButton caliperCanvasToggleButton;

	@FXML
	private ToggleButton poiCanvasToggleButton;

	@FXML
	private ToggleButton debugCanvasToggleButton;

	@FXML
	private ToggleButton segmentationCanvasToggleButton;

	@FXML
	private Canvas landmarksCanvas;

	@FXML
	private ToggleButton landmarksCanvasToggleButton;

	@FXML
	private ToggleGroup landmarkToggleGroup;

	@FXML
	private ToggleButton outerCiliaryMuscleBorderToggleButton;

	@FXML
	private ToggleButton innerCiliaryMuscleBorderToggleButton;

	@FXML
	private ToggleButton verticalCiliaryMuscleBorderToggleButton;

	@FXML
	private TextField patientNameTextField;

	@FXML
	private TextField dateTextField;

	@FXML
	private TextField timeTextField;

	@FXML
	private TextField eyeTextField;

	@FXML
	private Button segmentButton;

	@FXML
	private Button gradientsButton;

	@FXML
	private Button correctButton;

	@FXML
	private Button determineButton;

	@FXML
	private StackPane scalePane;

	@FXML
	private Button zoomInButton;

	@FXML
	private Button zoomOutButton;

	@FXML
	private Button resetZoomButton;

	@FXML
	private ToggleButton selectAnteriorChamberStartPointToggleButton;

	@FXML
	private ToggleButton darkenToggleButton;

	@FXML
	private ToggleButton brightenToggleButton;

	@FXML
	private Button clearAllBrushButton;

	@FXML
	private CheckBox anonymizeCheckBox;

	@FXML
	private Label resultNumberLabel;
	
	@FXML
	private Spinner<Double> thresholdFactorSpinner;
	
	@FXML
	private Spinner<Double> lensThresholdFactorSpinner;

	@FXML
	private ToggleButton placeLensStartToggleButton;

	@FXML
	private CheckBox calculateAreaCheckbox;

	@FXML
	private CheckBox calculateProfileCheckbox;

	ObjectProperty<Cursor> cursorProperty() {
		return _cursor;
	}

	@Override
    public void initialize(final URL url, final ResourceBundle rb) {
		initPreferences();

		_acquisitionDateTime.set(new Date());

		cnnSegmentationTab.setDisable(!SHOW_CNN_RIBBONGROUP);
		cnnRibbonGroup.setVisible(SHOW_CNN_RIBBONGROUP);
		cnnToolsRibbonGroup.setVisible(SHOW_CNN_RIBBONGROUP);

		calculateAreaCheckbox.selectedProperty().bindBidirectional(_calculateArea);
		calculateProfileCheckbox.selectedProperty().bindBidirectional(_calculateProfile);

		quickAccessBar.setSkin(new de.unituebingen.medizin.eye.ciloct.javafx.QuickAccessBarSkin(quickAccessBar, "CilOCT"));

		placeLensStartToggleButton.disableProperty().bind(_ready.not().or(landmarksCanvas.visibleProperty().not()));//.or(_segmented.not())
		fileLabel.textProperty().bind(Bindings.format("File: %s", Bindings.when(_fileProperty.isNotNull()).then(_fileProperty).otherwise("<No file selected>")));
		zoomLabel.textProperty().bind(Bindings.format("%.0f %%", _scale.multiply(100)));

		_thresholdFactor.addListener((final ObservableValue<? extends Double> observable, final Double oldValue, final Double newValue) -> thresholdLabel.setText(String.format("%.1f", newValue * _startThreshold)));

		thresholdFactorSpinner.getValueFactory().valueProperty().bindBidirectional(_thresholdFactor);
		thresholdFactorSpinner.disableProperty().bind(_ready.not());

		lensThresholdFactorSpinner.getValueFactory().valueProperty().bindBidirectional(_lensThresholdFactor);
		lensThresholdFactorSpinner.disableProperty().bind(_ready.not());

		resultNumberLabel.textProperty().bind(Bindings.size(_results).asString());
		blockingPane.visibleProperty().bind(progressIndicator.visibleProperty());
		debugCanvas.widthProperty().bind(_width);
		debugCanvas.heightProperty().bind(_height);
		debugCanvas.mouseTransparentProperty().bind(debugCanvas.visibleProperty().not());
		segmentationCanvas.widthProperty().bind(_width);
		segmentationCanvas.heightProperty().bind(_height);
		segmentationCanvas.mouseTransparentProperty().bind(segmentationCanvas.visibleProperty().not());
		landmarksCanvas.widthProperty().bind(_width);
		landmarksCanvas.heightProperty().bind(_height);
		landmarksCanvas.mouseTransparentProperty().bind(landmarksCanvas.visibleProperty().not());
		gradientCanvas.widthProperty().bind(_width);
		gradientCanvas.heightProperty().bind(_height);
		gradientCanvas.mouseTransparentProperty().bind(gradientCanvas.visibleProperty().not());
		caliperCanvas.widthProperty().bind(_width);
		caliperCanvas.heightProperty().bind(_height);
		caliperCanvas.mouseTransparentProperty().bind(caliperCanvas.visibleProperty().not());
		poiCanvas.mouseTransparentProperty().bind(poiCanvas.visibleProperty().not());

		skeletonizeButton.disableProperty().bind(useAIToggleButton.selectedProperty().and(_gradientsSet).not());
		clusterButton.disableProperty().bind(skeletonizeButton.disableProperty());

		histogramButton.disableProperty().bind(_ready.not());
		stretchHistogramButton.disableProperty().bind(_ready.not());
		contrastSlider.disableProperty().bind(_ready.not());
		probabilitySlider.disableProperty().bind(_useAI.not());
		probabilitySlider.prefWidthProperty().bind(gradientsButton.prefWidthProperty());
		contrastTextField.disableProperty().bind(_ready.not());
		probabilityTextField.disableProperty().bind(_useAI.not());
		segmentButton.disableProperty().bind(_ready.not());
		correctButton.disableProperty().bind(_segmented.not().or(_corrected));
		determineButton.disableProperty().bind(_segmented.not());
		outerCiliaryMuscleBorderToggleButton.disableProperty().bind(_ready.not());
		innerCiliaryMuscleBorderToggleButton.disableProperty().bind(_ready.not());
		verticalCiliaryMuscleBorderToggleButton.disableProperty().bind(_ready.not());
		saveLandmarksButton.disableProperty().bind(_ready.not());
		loadLandmarksButton.disableProperty().bind(_ready.not());
		saveLandmarksButton2.disableProperty().bind(_ready.not());
		loadLandmarksButton2.disableProperty().bind(_ready.not());
		zoomInButton.disableProperty().bind(_ready.not());
		zoomOutButton.disableProperty().bind(_ready.not());
		resetZoomButton.disableProperty().bind(_ready.not());
		copyResultsButton.disableProperty().bind(_ready.not().or(Bindings.isEmpty(_results)));

		copyProfilesButton.disableProperty().bind(_ready.not().or(Bindings.isNull(_profile)));
		clearResultsButton.disableProperty().bind(_ready.not().or(Bindings.isNull(_profile)).and(Bindings.isEmpty(_results)));
		selectAnteriorChamberStartPointToggleButton.disableProperty().bind(_ready.and(landmarksCanvas.visibleProperty()).not());

		final RadiusCellFactory cellFactory = new RadiusCellFactory();
		brushRadiusComboBox.setCellFactory(cellFactory);
		brushRadiusComboBox.setButtonCell(cellFactory.call(null));
		brushRadiusComboBox.disableProperty().bind(darkenToggleButton.disableProperty());
		_brushRadius.bind(brushRadiusComboBox.valueProperty());

		darkenToggleButton.disableProperty().bind((_ready.and(gradientCanvas.visibleProperty().or(debugCanvas.visibleProperty()).or(segmentationCanvas.visibleProperty()).or(poiCanvas.visibleProperty()).or(caliperCanvas.visibleProperty()).or(landmarksCanvas.visibleProperty()).not())).not());
		brightenToggleButton.disableProperty().bind(darkenToggleButton.disableProperty());
		clearAllBrushButton.disableProperty().bind(darkenToggleButton.disableProperty());
		darkenToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) -> _prevExitCursor = newValue ? BRUSH_DARKEN_CURSOR : CROSSHAIR_CURSOR);
		brightenToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) -> _prevExitCursor = (newValue) ? BRUSH_LIGHTEN_CURSOR : CROSSHAIR_CURSOR);
	
		Stream
			.of(gradientCanvas, debugCanvas, segmentationCanvas, poiCanvas, caliperCanvas, landmarksCanvas)
			.map(Canvas::visibleProperty)
			.forEach(c -> c.addListener((final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) -> {
				if (newValue) {
					darkenToggleButton.selectedProperty().set(false);
					brightenToggleButton.selectedProperty().set(false);
				}
			}));

		Bindings.bindBidirectional(contrastTextField.textProperty(), contrastSlider.valueProperty(), new DecimalFormat("0"));
		Bindings.bindBidirectional(probabilityTextField.textProperty(), probabilitySlider.valueProperty(), new DecimalFormat("0.00"));
		probabilitySlider.valueProperty().bindBidirectional(_probability);
		_probability.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				setSegmentation();
			}
		});

		useAIToggleButton.selectedProperty().bindBidirectional(_useAI);
		gradientsButton.disableProperty().bind(_useAI.not().or(_ready.not()));
		patientNameTextField.textProperty().bind(Bindings.when(anonymizeCheckBox.selectedProperty().not()).then(_patientName).otherwise(""));
		dateTextField.textProperty().bind(Bindings.when(anonymizeCheckBox.selectedProperty().not()).then(Bindings.createStringBinding(() -> DateFormat.getDateInstance(DateFormat.SHORT).format(_acquisitionDateTime.get()), _acquisitionDateTime)).otherwise(""));
		timeTextField.textProperty().bind(Bindings.when(anonymizeCheckBox.selectedProperty().not()).then(Bindings.createStringBinding(() -> DateFormat.getTimeInstance(DateFormat.SHORT).format(_acquisitionDateTime.get()), _acquisitionDateTime)).otherwise(""));
		eyeTextField.textProperty().bind(_eye);


		debugCanvas.visibleProperty().bind(debugCanvasToggleButton.selectedProperty());
		segmentationCanvas.visibleProperty().bind(segmentationCanvasToggleButton.selectedProperty());
		landmarksCanvas.visibleProperty().bind(landmarksCanvasToggleButton.selectedProperty());
		gradientCanvas.visibleProperty().bind(gradientCanvasToggleButton.selectedProperty());
		caliperCanvas.visibleProperty().bind(caliperCanvasToggleButton.selectedProperty());
		poiCanvas.visibleProperty().bind(poiCanvasToggleButton.selectedProperty());


		debugCanvas.scaleXProperty().bind(_scale);
		debugCanvas.scaleYProperty().bind(_scale);
		debugCanvas.translateXProperty().bind(_translateX);
		debugCanvas.translateYProperty().bind(_translateY);
		debugCanvas.widthProperty().bind(scalePane.widthProperty());
		debugCanvas.heightProperty().bind(scalePane.heightProperty());
		segmentationCanvas.scaleXProperty().bind(_scale);
		segmentationCanvas.scaleYProperty().bind(_scale);
		segmentationCanvas.translateXProperty().bind(_translateX);
		segmentationCanvas.translateYProperty().bind(_translateY);
		segmentationCanvas.widthProperty().bind(scalePane.widthProperty());
		segmentationCanvas.heightProperty().bind(scalePane.heightProperty());
		landmarksCanvas.scaleXProperty().bind(_scale);
		landmarksCanvas.scaleYProperty().bind(_scale);
		landmarksCanvas.translateXProperty().bind(_translateX);
		landmarksCanvas.translateYProperty().bind(_translateY);
		landmarksCanvas.widthProperty().bind(scalePane.widthProperty());
		landmarksCanvas.heightProperty().bind(scalePane.heightProperty());
		gradientCanvas.scaleXProperty().bind(_scale);
		gradientCanvas.scaleYProperty().bind(_scale);
		gradientCanvas.translateXProperty().bind(_translateX);
		gradientCanvas.translateYProperty().bind(_translateY);
		gradientCanvas.widthProperty().bind(scalePane.widthProperty());
		gradientCanvas.heightProperty().bind(scalePane.heightProperty());
		caliperCanvas.scaleXProperty().bind(_scale);
		caliperCanvas.scaleYProperty().bind(_scale);
		caliperCanvas.translateXProperty().bind(_translateX);
		caliperCanvas.translateYProperty().bind(_translateY);
		caliperCanvas.widthProperty().bind(scalePane.widthProperty());
		caliperCanvas.heightProperty().bind(scalePane.heightProperty());
		poiCanvas.scaleXProperty().bind(_scale);
		poiCanvas.scaleYProperty().bind(_scale);
		poiCanvas.translateXProperty().bind(_translateX);
		poiCanvas.translateYProperty().bind(_translateY);
		poiCanvas.widthProperty().bind(scalePane.widthProperty());
		poiCanvas.heightProperty().bind(scalePane.heightProperty());
		imageCanvas.scaleXProperty().bind(_scale);
		imageCanvas.scaleYProperty().bind(_scale);
		imageCanvas.translateXProperty().bind(_translateX);
		imageCanvas.translateYProperty().bind(_translateY);
		imageCanvas.widthProperty().bind(scalePane.widthProperty());
		imageCanvas.heightProperty().bind(scalePane.heightProperty());

		outerCiliaryMuscleBorderToggleButton.setUserData(Segments.OUTER_CILIARY_MUSCLE_BORDER);
		innerCiliaryMuscleBorderToggleButton.setUserData(Segments.INNER_CILIAR_MUSCLE_BORDER);
		verticalCiliaryMuscleBorderToggleButton.setUserData(Segments.VERTICAL_CILIARY_MUSCLE_BORDER);

		toggleLayersButton.disableProperty().bind(_ready.not());

		UpdateManager.register(debugCanvas, 
			_debug,
			_debug2
		);

		UpdateManager.register(landmarksCanvas, 
			_outerCiliaryMuscleBorderLandmarks,
			_innerCiliaryMuscleBorderLandmarks,
			_verticalCiliaryMuscleBorderLandmarks,
			_anteriorChamberStartPoint,
			_lensStartPoint
		);

		UpdateManager.register(segmentationCanvas,
			_outerCiliaryMuscleBorderFit,
			_innerCiliaryMuscleBorderFit,
			_outerConjunctivaBorderFit,
			_outerConjunctivaBorderExtrapolationFit,
			_innerScleralBorderFit,
			_upperIrisBorderFit,
			_verticalCiliaryMuscleBorderFitPoints,
			_lowerLensFit,
			_upperLensFit
		);

		UpdateManager.register(gradientCanvas,
			_outerCiliaryMuscleBorderGradients,
			_innerCiliaryMuscleBorderGradients,
			_outerConjunctivaBorderGradients,
			_innerSclearalBorderGradients,
			_upperIrisBorderGradients,
			_verticalCiliaryMuscleBorderGradients,
			_lensPoints
		);

		UpdateManager.register(poiCanvas,
			_innerAngleRecess,
			_ciliaryApex,
			_scleralSpur,
			_ciliaryApexShift
		);
		if (USE_ANGLES) {
			UpdateManager.register(caliperCanvas,
				_innerAngle,
				_ciliaryApexAngle
			);
		}

		UpdateManager.register(caliperCanvas,
			_perpendicularAxisCiliaryMuscle,
			_ciliaryMuscleAreaPoints,
			_ciliaryMuscleArea
		);

		UpdateManager.register(imageCanvas, contrastSlider.valueProperty());

		debugCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				if (debugCanvas.isVisible()) {
					_debug.forEach(d -> d.draw(gc));
					_debug2.forEach(p -> new PointDrawable(p, Color.AQUA).draw(gc));
				}
			}
		});
		debugCanvas.visibleProperty().addListener((final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) -> {
			if (newValue) {
				debugCanvas.fireEvent(new RepaintEvent(null));
			}
		});

		landmarksCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				_outerCiliaryMuscleBorderLandmarks.forEach(p -> new Landmark(p, COLOR_OUTER_CILIARY_MUSCLE_LANDMARK).draw(gc));
				_innerCiliaryMuscleBorderLandmarks.forEach(p -> new Landmark(p, COLOR_INNER_CILIARY_MUSCLE_LANDMARK).draw(gc));
				_verticalCiliaryMuscleBorderLandmarks.forEach(p -> new Landmark(p, COLOR_VERTICAL_CILIARY_MUSCLE_LANDMARK).draw(gc));
				_anteriorChamberStartPoint.ifPresent(p -> new Landmark(p, Color.ORCHID).draw(gc));
				_lensStartPoint.ifPresent(p -> new Landmark(p, COLOR_LENS_BORDER).draw(gc));
			}
		});

		gradientCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				_outerCiliaryMuscleBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(0, 1.0, 0, 0.2)).draw(gc)));
				_innerCiliaryMuscleBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(0, 0, 1.0, 0.2)).draw(gc)));
				_outerConjunctivaBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(1.0, 0, 0, 0.2)).draw(gc)));
				_innerSclearalBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(1.0, 1.0, 0, 0.2)).draw(gc)));
				_upperIrisBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(1.0, 0.0, 1.0, 0.2)).draw(gc)));
				_verticalCiliaryMuscleBorderGradients.ifPresent(pl -> pl.forEach(p -> new PointDrawable(p, Color.color(1.0, 0.27058825, 0.0, 0.8)).draw(gc)));

				_anteriorChamberPoints.ifPresent(pointList -> new PointListDrawable(pointList, Color.color(0.0, 1.0, 1.0, 0.1)).draw(gc));
				_lensPoints.ifPresent(pointList -> new PointListDrawable(pointList, COLOR_LENS_AREA).draw(gc));
			}
		});

		segmentationCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				_outerCiliaryMuscleBorderFit.ifPresent(f -> new FunctionDrawable(f, COLOR_OUTER_CILIARY_MUSCLE_BORDER).draw(gc));
				_innerCiliaryMuscleBorderFit.ifPresent(f -> new FunctionDrawable(f, COLOR_INNER_CILIARY_MUSCLE_BORDER).draw(gc));
				_verticalCiliaryMuscleBorderFitPoints.ifPresent(pf -> new PointListDrawable(pf, COLOR_VERTICAL_CILIARY_MUSCLE_BORDER).draw(gc));

				_outerConjunctivaBorderExtrapolationFit.ifPresent(f -> new FunctionDrawable(f, COLOR_OUTER_CONJUNCTIVA_EXTRAPOLATED_BORDER).draw(gc));
				_outerConjunctivaBorderFit.ifPresent(f -> new FunctionDrawable(f, COLOR_OUTER_CONJUNCTIVA_BORDER).draw(gc));
				_innerScleralBorderFit.ifPresent(f -> new FunctionDrawable(f, COLOR_INNER_SCLERAL_BORDER).draw(gc));
				_upperIrisBorderFit.ifPresent(f -> new FunctionDrawable(f, COLOR_UPPER_IRIS_BORDER).draw(gc));

				_lowerLensFit.ifPresent(f -> new FunctionDrawable(f, COLOR_LENS_BORDER).draw(gc));
				_upperLensFit.ifPresent(f -> new FunctionDrawable(f, COLOR_LENS_BORDER).draw(gc));
			}
		});

		poiCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				_ciliaryApex.ifPresent(p -> new POI(p, "Ciliary apex", Color.LIME).draw(gc));
				_scleralSpur.ifPresent(p -> new POI(p, "Scleral spur", Color.LIME).draw(gc));
				_innerAngleRecess.ifPresent(p -> new POI(p, "Inner angle recess", Color.CYAN).draw(gc));
//				_ciliaryApexShift.ifPresent(shift -> new POI(new Point(_ciliaryApex.getActualValue().getX()-shift/2d, _ciliaryApex.getActualValue().getY()-10), String.format("%.2f px", shift), Color.BLUE).draw(gc));
			}
		});

		caliperCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				_perpendicularAxisCiliaryMuscle.ifPresent(l -> new Caliper(l, Color.LIME).draw(gc));
				if (USE_ANGLES) {
					_ciliaryApexAngle.ifPresent(apexAngle -> new AngleDrawable(apexAngle, CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH, Color.FUCHSIA).draw(gc));
					_innerAngle.ifPresent(innerAngle -> new AngleDrawable(innerAngle, INNER_ANGLE_INTERPOLATION_LENGTH, Color.CYAN).draw(gc));
				}
				_ciliaryMuscleArea.andPresent(_ciliaryMuscleAreaPoints, (value, points) -> new PointListDrawable(String.format("%.1f px\u00B2", value), points, COLOR_CILIARY_MUSCLE_AREA).draw(gc));
			}
		});
		
		imageCanvas.addEventHandler(RepaintEvent.EVENT_TYPE, new RepaintEventHandler() {
			@Override
			protected void repaint(final GraphicsContext gc) {
				final ImageDataMatrix newMatrix = ImageUtil.changeContrast(_srcMatrix.copy(), contrastSlider.getValue());
				imageCanvas.getGraphicsContext2D().drawImage(new WritableImage(newMatrix, newMatrix.getColumnDimension(), newMatrix.getRowDimension()), 0, 0);
			}
		});

		_lensStartPoint.addListener(this::extrapolateOuterConjunctivaBorder);
	}

	@FXML
	private void placeLensStart(final ActionEvent event) {
		if (((ToggleButton) event.getSource()).isSelected()) {
			_prevExitCursor = LENS_START_POINT_CURSOR;
		} else {
			_prevExitCursor = CROSSHAIR_CURSOR;
		}
	}

	@FXML
	private void maximize(final ActionEvent event) {
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		//stage.setMaximized(!stage.isMaximized());
		//button.setGraphic(new MaterialDesignIconView(stage.isMaximized() ? MaterialDesignIcon.WINDOW_RESTORE : MaterialDesignIcon.WINDOW_MAXIMIZE));

		_maximized = !_maximized;
		if (!_maximized) {
            stage.setX(_stageSize.getMinX());
            stage.setY(_stageSize.getMinY());
            stage.setWidth(_stageSize.getWidth());
            stage.setHeight(_stageSize.getHeight());
		} else {
			_stageSize = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
			final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
		}
	}

	@FXML
	private void minimize(final ActionEvent event) {
		final Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}


	@FXML
	private void clearAllBrush(final ActionEvent event) {
		_brushList.clear();
		_srcMatrix = _srcMatrixSave.copy();
		imageCanvas.fireEvent(new RepaintEvent(null));
	}

	@FXML
	private void toggleLayers(final ActionEvent event) {
		getAndSet.accept(landmarksCanvasToggleButton, 0);
		getAndSet.accept(gradientCanvasToggleButton, 1);
		getAndSet.accept(segmentationCanvasToggleButton, 2);
		getAndSet.accept(poiCanvasToggleButton, 3);
		getAndSet.accept(caliperCanvasToggleButton, 4);
		getAndSet.accept(debugCanvasToggleButton, 5);
	}

	@FXML
	private void genGroundTruthImages(final ActionEvent event) {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select image directory");
		final File f = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastImageDirectory", Preferences.userNodeForPackage(getClass()).get(ImageFileChooser.PREF_LAST_DIRECTORY, ImageFileChooser.DEFAULT_DIRECTORY)));
		if (f.exists()) {
			chooser.setInitialDirectory(f);
		}
		final File imageDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (imageDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("batch").put("lastImageDirectory", imageDir.toString());

		final File f2 = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastLandmarkDirectory", Preferences.userNodeForPackage(getClass()).node("landmarks").get("lastDirectory", ".")));
		if (f2.exists()) {
			chooser.setInitialDirectory(f2);
		}
		chooser.setTitle("Select landmark directory");
		final File landmarkDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (landmarkDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("batch").put("lastLandmarkDirectory", landmarkDir.toString());

		final File f3 = new File(Preferences.userNodeForPackage(getClass()).node("genGroundTruth").get("lastTargetDirectory", "."));
		if (f3.exists()) {
			chooser.setInitialDirectory(f3);
		}
		chooser.setTitle("Select target directory");
		final File targetDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (targetDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("genGroundTruth").put("lastTargetDirectory", targetDir.toString());

		final long fileCount;
		try {
			fileCount = Files.list(imageDir.toPath()).filter(path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm")).count();
		} catch (IOException ex) {
			final Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Could not open directory");
			alert.setContentText(ex.getMessage());
			alert.showAndWait();

			return;
		}

		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/progressDialog.fxml"));
			final Parent parent = loader.load();
			final ProgressDialogController controller = loader.getController();

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Batch processing");

			final Task<TaskResult> task = new Task<TaskResult>() {
				private long _file = 0;

				private void updateFailed() {
					Platform.runLater(() -> controller.failedProperty().set(controller.failedProperty().get()+1));
				}

				private void updateSuccessfull() {
					Platform.runLater(() -> controller.successfullProperty().set(controller.successfullProperty().get()+1));
				}

				private Optional<? extends Throwable> call(final Runnable r) {
					try {
						r.run();
						return Optional.empty();
					} catch (Throwable e) {
						return Optional.of(e);
					}
				}

				private void runAndWait(final Runnable action) {
					if (action == null) {
						throw new NullPointerException("action");
					}

					// run synchronously on JavaFX thread
					if (Platform.isFxApplicationThread()) {
						action.run();
						return;
					}

					// queue on JavaFX thread and wait for completion
					final CountDownLatch doneLatch = new CountDownLatch(1);
					Platform.runLater(() -> {
						try {
							action.run();
						} finally {
							doneLatch.countDown();
						}
					});

					try {
						doneLatch.await();
					} catch (InterruptedException e) {
					}
				}

				@Override
				protected void cancelled() {
					finish();

					final Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setHeaderText("Batch process cancelled");
					alert.showAndWait();
				}

				@Override
				protected void succeeded() {
					finish();

					try {
						final TaskResult taskResult = get();

						try (final PrintWriter writer = new PrintWriter(new FileWriter(Paths.get(targetDir.getAbsolutePath(), "errors.csv").toFile()))) {
							writer.println("Filename\texceptions");
							taskResult
								.getErrors()
								.forEach((file, exceptions) -> writer.printf("%s\t%s\n", file, exceptions.stream().map(e -> e.getMessage()).collect(joining(","))))
							;
						} catch (IOException e) {
							Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, e);
						}

						final Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText("Batch process finished");
						alert.setContentText(String.format("Finished with %d successful and %d failed of %d total", taskResult.getSuccessfull(), taskResult.getFailed(), taskResult.getTotal()));
						alert.showAndWait();
					} catch (InterruptedException | ExecutionException ex) {
						Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, ex);
					}
				}

				private void finish() {
					clear();
					stage.close();
				}

				@Override
				protected TaskResult call() throws Exception {
					final List<Result> results = new ArrayList<>();
					final Map<String, List<Throwable>> errors = new HashMap<>();
					final List<Profile> profiles = new ArrayList<>();

					try (final DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir.toPath(), path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm"))) {
						ds.forEach(path -> {
							if (isCancelled()) {
								return;
							}
							updateMessage(path.toFile().getName());
							updateProgress(_file++, fileCount);

							final File imageFile = path.toFile();
							updateTitle("load image");

							runAndWait(() -> loadImage(imageFile, false));

							runAndWait(() -> _segmented.setValue(false));
							runAndWait(() -> _ready.set(true));
							runAndWait(() -> _scale.set(1));

							final String landmarksFilename = imageFile.getName().split("\\.")[0] + ".landmark.xml";
							//load landmarks
							final Path landmarkFile = Paths.get(landmarkDir.toString(), landmarksFilename);
							if (Files.exists(landmarkFile)) {
								updateTitle("load landmarks");
								runAndWait(() -> loadLandmarks(landmarkFile.toFile()));

								final List<Throwable> exceptions = new ArrayList<>(0);
								
								call(CilOCTController.this::createVerticalGradientMatrix).ifPresent(exceptions::add);

								//segment
								updateTitle("find anterior chamber");
								call(CilOCTController.this::findAnteriorChamber).ifPresent(exceptions::add);
								updateTitle("fit inner scleral border");
								call(CilOCTController.this::fitInnerScleralBorder).ifPresent(exceptions::add);
								updateTitle("fit upper iris border");
								call(CilOCTController.this::fitUpperIrisBorder).ifPresent(exceptions::add);
								updateTitle("fit outer ciliary muscle border");
								call(CilOCTController.this::fitOuterCiliaryMuscleBorder).ifPresent(exceptions::add);
								updateTitle("fit inner ciliary muscle border");		
								call(CilOCTController.this::fitInnerCiliaryMuscleBorder).ifPresent(exceptions::add);
								updateTitle("fit conjunctiva border");
								call(CilOCTController.this::fitConjunctivaBorder).ifPresent(exceptions::add);
								updateTitle("fit vertical ciliary muscle border");
								call(CilOCTController.this::fitVerticalCiliaryMuscleBorder).ifPresent(exceptions::add);
								updateTitle("fit lens borders");
								call(CilOCTController.this::fitLensBorders).ifPresent(exceptions::add);

								runAndWait(() -> _segmented.setValue(true));

								exceptions.forEach(Throwable::printStackTrace);

								updateTitle("add result");
								if (exceptions.isEmpty()) {
									results.add(new Result(
											path.toFile().getName(),
											Optional.empty(),
											Optional.empty(),
											Optional.empty(),
											Optional.empty(),
											Optional.empty(),
											Optional.empty(),
											Optional.empty()
										)
									);

									updateSuccessfull();
								} else {
									errors.put(path.toFile().getName(), exceptions);
									updateFailed();
								}

								updateTitle("save image");

								final IndexColorModel colorModel = new IndexColorModel(8, GroundTruthPalette.values().length, GroundTruthPalette.toArray(), 0, false, 0, DataBuffer.TYPE_BYTE);
								final BufferedImage image = new BufferedImage((int) Math.round(_width.get() * EXPORT_SCALE), (int) Math.round(_height.get() * EXPORT_SCALE), BufferedImage.TYPE_BYTE_INDEXED, colorModel);

								updateTitle("air area");
								_airPoints.ifPresent(pl -> pl.forEach(p ->{
									final int x = (int) Math.round(p.getRoundedX() * EXPORT_SCALE);
									final int y = (int) Math.round(p.getRoundedY() * EXPORT_SCALE);
									image.setRGB(x, y, GroundTruthPalette.AIR_AREA.rgba());
								}));

								updateTitle("anterior chamber area");
								_anteriorChamberPoints.ifPresent(pl -> pl.forEach(p ->{
									final int x = (int) Math.round(p.getRoundedX() * EXPORT_SCALE);
									final int y = (int) Math.round(p.getRoundedY() * EXPORT_SCALE);
									image.setRGB(x, y, GroundTruthPalette.ANTERIOR_CHAMBER_AREA.rgba());
								}));

								updateTitle("ciliary muscle area");
								_outerCiliaryMuscleBorderFit.andPresent(_innerCiliaryMuscleBorderFit, (outer, inner) -> {
									_verticalCiliaryMuscleBorderFitPoints.ifPresent(verticalPoints -> {
										final BoundedPolynomialSplineFunction fvertical =
											verticalPoints
												.stream()
												.map(p -> new Point(p.getY(), p.getX()))
												.collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline))
										;
										
										final double xmin = Math.max(inner.getX1(), outer.getX1());
										final double xmax = Math.min(inner.getX2(), outer.getX2());
										for (double y = 0; y < _height.get(); y++) {
											for (double x = xmin; x < xmax; x++) {
												if (y > outer.value(x) && y < inner.value(x)) {
													if (inner.isValidPoint(x)) {
														if (fvertical.isValidPoint(y)) {
															if (x < fvertical.value(y)) {
																image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.CILIARY_MUSCLE_AREA.rgba());
															}
														} else {
															image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.CILIARY_MUSCLE_AREA.rgba());
														}
													}
												}
											}
										}
										double xmax2 = -1;
										for (double y = fvertical.getX1(); y < fvertical.getX2(); y+=0.1) {
											final double x = fvertical.value(y);
											if (xmax2 < x) {
												xmax2 = x;
											}
										}
										for (double x = xmax; x < xmax2; x++) {
											for (double y = 0; y < _height.get(); y++) {
												if (fvertical.isValidPoint(y)) {
													if (x < fvertical.value(y)) {
														image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.CILIARY_MUSCLE_AREA.rgba());
													}
												} else if (x < outer.getX2() && outer.isValidPoint(x) && y > outer.value(x) && y < fvertical.getX2()) {
													image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.CILIARY_MUSCLE_AREA.rgba());
												} else if (x < inner.getX2() && inner.isValidPoint(x) && y < inner.value(x) && y > fvertical.getX1()) {
													image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.CILIARY_MUSCLE_AREA.rgba());
												}
											}
										}
									});
								});

								updateTitle("inner scleral border");
								_innerScleralBorderFit.ifPresent(innerScleralBorder -> {
									for (int x = (int) Math.round(innerScleralBorder.getX1()); x < innerScleralBorder.getX2(); x++) {
										final double y = innerScleralBorder.value(x);
										if (y < 20) {
											break;
										}
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.INNER_SCLERAL_BORDER.rgba());
									}									
								});

								updateTitle("upper iris border");
								_upperIrisBorderFit.ifPresent(upperIrisBorder -> {
									for (int x = (int) Math.round(upperIrisBorder.getX1()); x < upperIrisBorder.getX2(); x++) {
										final double y = upperIrisBorder.value(x);
										if (y > _height.get() - 20) {
											break;
										}
										try {
											image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.UPPER_IRIS_BORDER.rgba());
										} catch (Exception e) {
										}
									}									
								});
								updateTitle("outer conjunctiva border");
								_outerConjunctivaBorderFit.ifPresent(outerConjunctivaBorder -> {
									for (int x = (int) Math.round(outerConjunctivaBorder.getX1()); x < outerConjunctivaBorder.getX2(); x++) {
										final double y = outerConjunctivaBorder.value(x);
										if (y < 20) {
											break;
										}
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(y * EXPORT_SCALE), GroundTruthPalette.OUTER_CONJUNCTIVA_BORDER.rgba());
									}									
								});

								updateTitle("outer ciliary muslce border");
								_outerCiliaryMuscleBorderFit.ifPresent(outerCiliaryMuscleBorder -> {
									for (int x = (int) Math.round(outerCiliaryMuscleBorder.getX1()); x < outerCiliaryMuscleBorder.getX2(); x++) {
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(outerCiliaryMuscleBorder.value(x) * EXPORT_SCALE), GroundTruthPalette.OUTER_CILIARY_MUSCLE_BORDER.rgba());
									}
								});

								updateTitle("inner ciliary muscle border");
								_innerCiliaryMuscleBorderFit.ifPresent(innerCiliaryMuscleBorder -> {
									for (int x = (int) Math.round(innerCiliaryMuscleBorder.getX1()); x < innerCiliaryMuscleBorder.getX2(); x++) {
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(innerCiliaryMuscleBorder.value(x) * EXPORT_SCALE), GroundTruthPalette.INNER_CILIARY_MUSCLE_BORDER.rgba());
									}
								});

								updateTitle("vertical ciliary muscle border");
								_verticalCiliaryMuscleBorderFitPoints.ifPresent(verticalPoints -> {
									final BoundedPolynomialSplineFunction fvertical =
										verticalPoints
											.stream()
											.map(p -> new Point(p.getY(), p.getX()))
											.collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline))
									;
									for (double x = fvertical.getX1(); x < fvertical.getX2(); x+=0.1) {
										image.setRGB((int) Math.round(fvertical.value(x) * EXPORT_SCALE), (int) Math.round(x * EXPORT_SCALE), GroundTruthPalette.VERTICAL_CILIARY_MUSCLE_BORDER.rgba());
									}
								});

								updateTitle("lens area");
								_lensPoints.ifPresent(pl -> pl.forEach(p ->{
									final int x = (int) Math.round(p.getRoundedX() * EXPORT_SCALE);
									final int y = (int) Math.round(p.getRoundedY() * EXPORT_SCALE);
									image.setRGB(x, y, GroundTruthPalette.LENS_AREA.rgba());
								}));
								updateTitle("lens borders");
								_lowerLensFit.ifPresent(f -> {
									for (int x = (int) Math.round(f.getX1()); x < f.getX2(); x++) {
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(f.value(x) * EXPORT_SCALE), GroundTruthPalette.LOWER_LENS_BORDER.rgba());
									}
								});
								_upperLensFit.ifPresent(f -> {
									for (int x = (int) Math.round(f.getX1()); x < f.getX2(); x++) {
										image.setRGB((int) Math.round(x * EXPORT_SCALE), (int) Math.round(f.value(x) * EXPORT_SCALE), GroundTruthPalette.UPPER_LENS_BORDER.rgba());
									}
								});

								AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
								at.translate(-image.getWidth(), 0);
								BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, colorModel);
								flippedImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(image, flippedImage);

								updateTitle("write image");
								try {
									ImageIO.write(image, "png", Paths.get(targetDir.getAbsolutePath(), path.toFile().getName() + "_l.png").toFile());
									ImageIO.write(flippedImage, "png", Paths.get(targetDir.getAbsolutePath(), path.toFile().getName() + "_r.png").toFile());
								} catch (IOException ex) {
									Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, ex);
								}

								updateTitle("clear values");
								runAndWait(() -> clear());
							}
						});
					} catch (IOException ex) {
						throw ex;
					}
					try(final PrintWriter pw = new PrintWriter(Paths.get(targetDir.getAbsolutePath(), "colors.txt").toFile())) {
						for (String name : GroundTruthPalette.names()) {
							pw.println(name);
						}
					} catch (IOException ex) {
						throw ex;
					}

					return new TaskResult(fileCount, results, profiles, errors);
				}
			};

			controller.setTask(task);
			controller.statusProperty().bind(task.titleProperty());
			controller.fileProperty().bind(task.messageProperty());
			controller.progressProperty().bind(task.progressProperty());

			_executor.execute(task);

			stage.show();
		} catch (IOException e) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, e);
		}
	}

	@FXML
	private void batchConvert(final ActionEvent event) {
	final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select image directory");
		final File f = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastImageDirectory", Preferences.userNodeForPackage(getClass()).get(ImageFileChooser.PREF_LAST_DIRECTORY, ImageFileChooser.DEFAULT_DIRECTORY)));
		if (f.exists()) {
			chooser.setInitialDirectory(f);
		}
		final File imageDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (imageDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("batch").put("lastImageDirectory", imageDir.toString());

		final File f3 = new File(Preferences.userNodeForPackage(getClass()).node("convert").get("lastTargetDirectory", "."));
		if (f3.exists()) {
			chooser.setInitialDirectory(f3);
		}
		chooser.setTitle("Select target directory");
		final File targetDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (targetDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("convert").put("lastTargetDirectory", targetDir.toString());

		final long fileCount;
		try {
			fileCount = Files.list(imageDir.toPath()).filter(path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm")).count();
		} catch (IOException ex) {
			final Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Could not open directory");
			alert.setContentText(ex.getMessage());
			alert.showAndWait();

			return;
		}


		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/progressDialog.fxml"));
			final Parent parent = loader.load();
			final ProgressDialogController controller = loader.getController();

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Batch conversion");

			final Task<Void> task = new Task<Void>() {
				private long _file = 0;

				private void updateFailed() {
					Platform.runLater(() -> controller.failedProperty().set(controller.failedProperty().get()+1));
				}

				private void updateSuccessfull() {
					Platform.runLater(() -> controller.successfullProperty().set(controller.successfullProperty().get()+1));
				}

				private Optional<? extends Throwable> call(final Runnable r) {
					try {
						r.run();
						return Optional.empty();
					} catch (Throwable e) {
						return Optional.of(e);
					}
				}

				private void runAndWait(final Runnable action) {
					if (action == null) {
						throw new NullPointerException("action");
					}

					// run synchronously on JavaFX thread
					if (Platform.isFxApplicationThread()) {
						action.run();
						return;
					}

					// queue on JavaFX thread and wait for completion
					final CountDownLatch doneLatch = new CountDownLatch(1);
					Platform.runLater(() -> {
						try {
							action.run();
						} finally {
							doneLatch.countDown();
						}
					});

					try {
						doneLatch.await();
					} catch (InterruptedException e) {
					}
				}

				@Override
				protected void cancelled() {
					finish();

					final Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setHeaderText("Batch process cancelled");
					alert.showAndWait();
				}

				@Override
				protected void succeeded() {
					finish();

					final Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setHeaderText("Batch conversion finished");
					alert.setContentText(String.format("Finished"));
					alert.showAndWait();
				}

				private void finish() {
					stage.close();
				}

				@Override
				protected Void call() throws Exception {
					try (final DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir.toPath(), path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm"))) {
						ds.forEach(path -> {
							if (isCancelled()) {
								return;
							}
							updateMessage(path.toFile().getName());
							updateProgress(_file++, fileCount);

							final File file = path.toFile();
							updateTitle("load image");

							if (file != null) {
								try {
									final BufferedImage convertedImage = ImageUtil.adjustVisanteDicomImage(ImageIO.read(file));
									BufferedImage scaledImage = new BufferedImage((int) Math.round(convertedImage.getWidth() * EXPORT_SCALE), (int) Math.round(convertedImage.getHeight() * EXPORT_SCALE), convertedImage.getType());
									scaledImage = new AffineTransformOp(AffineTransform.getScaleInstance(EXPORT_SCALE, EXPORT_SCALE), AffineTransformOp.TYPE_BILINEAR).filter(convertedImage, scaledImage);
									updateTitle("write converted image");
									File newfile = new File(targetDir.getAbsolutePath() + "/" + file.getName() + "_l.png");
									ImageIO.write(scaledImage, "PNG", newfile);

									AffineTransform at = AffineTransform.getScaleInstance(-1, 1);
									at.translate(-scaledImage.getWidth(), 0);
									BufferedImage flippedImage = new BufferedImage(scaledImage.getWidth(), scaledImage.getHeight(), scaledImage.getType());
									flippedImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(scaledImage, flippedImage);
									newfile = new File(targetDir.getAbsolutePath() + "/" + file.getName() + "_r.png");
									ImageIO.write(flippedImage, "PNG", newfile);
								} catch (IOException ex) {
									Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						});
					}
					return null;
				}
			};

			controller.setTask(task);
			controller.statusProperty().bind(task.titleProperty());
			controller.fileProperty().bind(task.messageProperty());
			controller.progressProperty().bind(task.progressProperty());

			_executor.execute(task);

			stage.show();
		} catch (IOException e) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, e);
		}
	}

	@FXML
	private void reidentification(final ActionEvent event) {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select image directory");
		final File f = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastImageDirectory", Preferences.userNodeForPackage(getClass()).get(ImageFileChooser.PREF_LAST_DIRECTORY, ImageFileChooser.DEFAULT_DIRECTORY)));
		if (f.exists()) {
			chooser.setInitialDirectory(f);
		}
		final File imageDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (imageDir == null) {
			return;
		}

		final FileChooser fchooser = new FileChooser();
		final File f3 = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastTargetDirectory", "."));
		if (f3.exists()) {
			fchooser.setInitialDirectory(f3);
			fchooser.setInitialFileName("reidentification.csv");
		}
		fchooser.setTitle("Select filename");
		final File targetFile = fchooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());
		if (targetFile == null) {
			return;
		}

		try (final DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir.toPath(), path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm"))) {
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final List<String[]> entries = new ArrayList<>();
			ds.forEach(path -> {
				try (final DicomInputStream dis = new DicomInputStream(path.toFile())) {
					final Attributes attributes = dis.readDataset(-1, -1);
					final String filename = path.toFile().getName();
					final String subject = Arrays.stream(attributes.getString(Tag.PatientName).split("\\^")).filter(StringUtils::isNoneBlank).collect(joining(","));
					final Date date = attributes.getDate(Tag.AcquisitionDateTime);
					final String sex = attributes.getString(Tag.PatientSex);
					entries.add(new String[] { filename, subject, df.format(date), sex });
				} catch (IOException ex) {
					Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
				}
			});

			try (final PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
				writer.println("Filename\tsubject\tdate\tsex");
				entries
					.stream()
					.sorted((a, b) -> a[2].compareTo(b[2]))
					.map(row -> String.join("\t", row))
					.forEach(writer::println);
			}
		} catch (IOException ex) {
			final Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setHeaderText("Failed to generate reidentification list.");
			alert.setContentText(ex.getMessage());
			alert.showAndWait();
		}
	}

	@FXML
	private void batch(final ActionEvent event) {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Select image directory");
		final File f = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastImageDirectory", Preferences.userNodeForPackage(getClass()).get(ImageFileChooser.PREF_LAST_DIRECTORY, ImageFileChooser.DEFAULT_DIRECTORY)));
		if (f.exists()) {
			chooser.setInitialDirectory(f);
		}
		final File imageDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (imageDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("batch").put("lastImageDirectory", imageDir.toString());

		final File landmarkDir;
		if (!_useAI.get()) {
			final File f2 = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastLandmarkDirectory", Preferences.userNodeForPackage(getClass()).node("landmarks").get("lastDirectory", ".")));
			if (f2.exists()) {
				chooser.setInitialDirectory(f2);
			}
			chooser.setTitle("Select landmark directory");
			landmarkDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
			if (landmarkDir == null) {
				return;
			}
			Preferences.userNodeForPackage(getClass()).node("batch").put("lastLandmarkDirectory", landmarkDir.toString());
		} else {
			landmarkDir = null;
		}

		final File f3 = new File(Preferences.userNodeForPackage(getClass()).node("batch").get("lastTargetDirectory", "."));
		if (f3.exists()) {
			chooser.setInitialDirectory(f3);
		}
		chooser.setTitle("Select target directory");
		final File targetDir = chooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
		if (targetDir == null) {
			return;
		}
		Preferences.userNodeForPackage(getClass()).node("batch").put("lastTargetDirectory", targetDir.toString());

		final long fileCount;
		try {
			fileCount = Files.list(imageDir.toPath()).filter(path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm")).count();
		} catch (IOException ex) {
			final Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Could not open directory");
			alert.setContentText(ex.getMessage());
			alert.showAndWait();

			return;
		}

		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/progressDialog.fxml"));
			final Parent parent = loader.load();
			final ProgressDialogController controller = loader.getController();

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Batch processing");

			final Task<TaskResult> task = new Task<TaskResult>() {
				private long _file = 0;

				private void updateFailed() {
					Platform.runLater(() -> controller.failedProperty().set(controller.failedProperty().get()+1));
				}

				private void updateSuccessfull() {
					Platform.runLater(() -> controller.successfullProperty().set(controller.successfullProperty().get()+1));
				}

				private Optional<? extends Throwable> call(final Runnable r) {
					try {
						r.run();
						return Optional.empty();
					} catch (Throwable e) {
						return Optional.of(e);
					}
				}

				private void runAndWait(final Runnable action) {
					if (action == null) {
						throw new NullPointerException("action");
					}

					// run synchronously on JavaFX thread
					if (Platform.isFxApplicationThread()) {
						action.run();
						return;
					}

					// queue on JavaFX thread and wait for completion
					final CountDownLatch doneLatch = new CountDownLatch(1);
					Platform.runLater(() -> {
						try {
							action.run();
						} finally {
							doneLatch.countDown();
						}
					});

					try {
						doneLatch.await();
					} catch (InterruptedException e) {
					}
				}

				@Override
				protected void cancelled() {
					finish();

					final Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setHeaderText("Batch process cancelled");
					alert.showAndWait();
				}

				@Override
				protected void succeeded() {
					finish();

					try {
						final TaskResult taskResult = get();

						try (final PrintWriter writer = new PrintWriter(new FileWriter(Paths.get(targetDir.getAbsolutePath(), "results.csv").toFile()))) {
							writer.println("Filename\tPerpendicular axis\tCiliary muscle area\tCiliary apex x0\tCiliary apex y0\tScleral spur x0\tScleral spur y0\tCA-SP-dist\tCiliary apex shift");
							taskResult
								.getResults()
								.forEach(result -> writer.println(result.toString()));
						} catch (IOException e) {
							Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, e);
						}

						try (final PrintWriter writer = new PrintWriter(new FileWriter(Paths.get(targetDir.getAbsolutePath(), "errors.csv").toFile()))) {
							writer.println("Filename\texceptions");
							taskResult
								.getErrors()
								.forEach((file, exceptions) -> writer.printf("%s\t%s\n", file, exceptions.stream().map(e -> e.getMessage()).collect(joining(","))))
							;
						} catch (IOException e) {
							Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, e);
						}

						if (_calculateProfile.get()) {
							try (final PrintWriter writer = new PrintWriter(new FileWriter(Paths.get(targetDir.getAbsolutePath(), "profiles.csv").toFile()))) {
								writer.println("Filename\tcorrected\ttype\tx0\tx1\ty1\tarclength1\tcurvature1\tx2\ty2\tcurvature2\twidth\tx1-ssx1");
								taskResult
									.getProfiles()
									.stream()
									.forEach(profile -> {
										final Optional<ProfilePoint> sp = profile.stream().filter(p -> "scleral spur".equals(p.getType())).findFirst();
										profile
											.stream()
											.map(point -> point.toString().concat(sp.isPresent() && point.getP1() != null ? String.format("\t%.2f", point.getP1().getX() - sp.get().getP1().getX()) : "\t"))
											.forEach(writer::println);
									})
//									.flatMap(profile -> profile.stream())
//									.map(point -> point.toString())
//									.forEach(writer::println)
								;
							} catch (IOException e) {
								Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, e);
							}
						}

						final Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText("Batch process finished");
						alert.setContentText(String.format("Finished with %d successful and %d failed of %d total", taskResult.getSuccessfull(), taskResult.getFailed(), taskResult.getTotal()));
						alert.showAndWait();
					} catch (InterruptedException | ExecutionException ex) {
						Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, ex);
					}
				}

				private void finish() {
					runAndWait(() -> {
						clear();
						clearDeterminedValues();
						stage.close();
					});
				}

				@Override
				protected TaskResult call() throws Exception {
					final List<Result> results = new ArrayList<>();
					final Map<String, List<Throwable>> errors = new HashMap<>();
					final List<Profile> profiles = new ArrayList<>();

					try (final DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir.toPath(), path -> path.toFile().isFile() && path.toString().toLowerCase().endsWith("dcm"))) {
						ds.forEach(path -> {
							if (isCancelled()) {
								return;
							}
							updateMessage(path.toFile().getName());
							updateProgress(_file++, fileCount);

							final File imageFile = path.toFile();
							updateTitle("load image");

							runAndWait(() -> loadImage(imageFile, false));

							runAndWait(() -> _corrected.setValue(false));
							runAndWait(() -> _segmented.setValue(false));
							runAndWait(() -> _ready.set(true));
							runAndWait(() -> _scale.set(1));

							final Path landmarkFile;
							if (!_useAI.get()) {
								final String landmarksFilename = imageFile.getName().split("\\.")[0] + ".landmark.xml";
								//load landmarks
								landmarkFile = Paths.get(landmarkDir.toString(), landmarksFilename);
							} else {
								landmarkFile = null;
							}

							if (!_useAI.get() && landmarkFile != null && Files.exists(landmarkFile)) {
								updateTitle("load landmarks");
								runAndWait(() -> loadLandmarks(landmarkFile.toFile()));
							}

							final List<Throwable> exceptions = new ArrayList<>(0);

							if (!_useAI.get()) {
								call(CilOCTController.this::createVerticalGradientMatrix).ifPresent(exceptions::add);

								//segment
								updateTitle("find anterior chamber");
								call(CilOCTController.this::findAnteriorChamber).ifPresent(exceptions::add);
							} else {
								updateTitle("Neural Net segmentation");
								call(CilOCTController.this::gradients).ifPresent(exceptions::add);
								call(CilOCTController.this::cluster).ifPresent(exceptions::add);
							}
							updateTitle("fit inner scleral border");
							call(CilOCTController.this::fitInnerScleralBorder).ifPresent(exceptions::add);
							updateTitle("fit upper iris border");
							call(CilOCTController.this::fitUpperIrisBorder).ifPresent(exceptions::add);
							updateTitle("fit outer ciliary muscle border");
							call(CilOCTController.this::fitOuterCiliaryMuscleBorder).ifPresent(exceptions::add);
							updateTitle("fit inner ciliary muscle border");		
							call(CilOCTController.this::fitInnerCiliaryMuscleBorder).ifPresent(exceptions::add);
							updateTitle("fit conjunctiva border");
							call(CilOCTController.this::fitConjunctivaBorder).ifPresent(exceptions::add);
							updateTitle("fit vertical ciliary muscle border");
							call(CilOCTController.this::fitVerticalCiliaryMuscleBorder).ifPresent(exceptions::add);

							call(CilOCTController.this::extrapolateOuterConjunctivaBorder).ifPresent(exceptions::add);

							runAndWait(() -> _segmented.setValue(true));
							runAndWait(() -> segmentationCanvasToggleButton.setSelected(true));

							//correct
							updateTitle("correct refraction");
							call(CilOCTController.this::refract).ifPresent(exceptions::add);
							runAndWait(() -> _corrected.setValue(true));

							//determine
							updateTitle("determine inner angle recess");
							call(CilOCTController.this::determineInnerAngleRecess).ifPresent(exceptions::add);

							if (USE_ANGLES) {
								updateTitle("calculate inner angle");
								call(CilOCTController.this::calcInnerAngle).ifPresent(exceptions::add);
							}
							updateTitle("determine scleral spur");
							call(CilOCTController.this::determineScleralSpur).ifPresent(exceptions::add);
							updateTitle("determine ciliary apex");
							call(CilOCTController.this::determineCiliaryApex).ifPresent(exceptions::add);
							updateTitle("determine perpendicular axis");
							call(CilOCTController.this::determinePerpendicularAxisCiliaryMuscle).ifPresent(exceptions::add);
							if (USE_ANGLES) {
								updateTitle("calculate ciliary apex angle");
								call(CilOCTController.this::calcCiliaryApexAngle).ifPresent(exceptions::add);
							}
							updateTitle("calculate ciliary apex shift");
							call(CilOCTController.this::calcCiliaryApexShift).ifPresent(exceptions::add);

							//calculate CMA
							if (exceptions.isEmpty() && _calculateArea.get()) {
								updateTitle("calculate ciliary muscle area");
								call(CilOCTController.this::calcCiliaryMuscleArea).ifPresent(exceptions::add);
							}

							//calculate profile
							if (exceptions.isEmpty() && _calculateProfile.get()) {
								updateTitle("calculate profile");
								call(CilOCTController.this::calcProfile).ifPresent(exceptions::add);
							}
							exceptions.forEach(Throwable::printStackTrace);

							updateTitle("add result");
							if (exceptions.isEmpty()) {
								results.add(new Result(
										path.toFile().getName(),
										Optional.ofNullable(_perpendicularAxisCiliaryMuscle.orElse(null)),
										Optional.ofNullable(_ciliaryApexAngle.orElse(null)),
										Optional.ofNullable(_innerAngle.orElse(null)),
										Optional.ofNullable(_ciliaryMuscleArea.orElse(null)),
										Optional.ofNullable(_ciliaryApex.orElse(null)),
										Optional.ofNullable(_scleralSpur.orElse(null)),
										Optional.ofNullable(_ciliaryApexShift.orElse(null))
									)
								);

								if (_calculateProfile.get()) {
									updateTitle("add profile");
									runAndWait(() -> _profile.ifPresent(profiles::add));
								}

								updateSuccessfull();
							} else {
								errors.put(path.toFile().getName(), exceptions);
								updateFailed();
							}

							updateTitle("save image");
							final WritableImage image = new WritableImage(_width.get(), _height.get());
							runAndWait(() -> scalePane.snapshot(new SnapshotParameters(), image));
							try {
								ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", Paths.get(targetDir.getAbsolutePath(), path.toFile().getName() + ((exceptions.isEmpty()) ? "_successful" : "_failed") + ".png").toFile());
							} catch (IOException ex) {
								Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, ex);
							}

							updateTitle("clear values");
							runAndWait(() -> clearDeterminedValues());
							runAndWait(() -> clear());
						});
					} catch (IOException ex) {
						throw ex;
					}

					return new TaskResult(fileCount, results, profiles, errors);
				}
			};

			controller.setTask(task);
			controller.statusProperty().bind(task.titleProperty());
			controller.fileProperty().bind(task.messageProperty());
			controller.progressProperty().bind(task.progressProperty());

			_executor.execute(task);

			stage.show();
		} catch (IOException e) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, e);
		}
	}

	@FXML
	private void clearResults(final ActionEvent event) {
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm clear results");
		alert.setHeaderText("Do you really want to clear all results?");
		alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {_results.clear(); _profile.empty();});
	}

	@FXML
	private void copyResults(final ActionEvent event) {
		final ClipboardContent content = new ClipboardContent();
		content.putString(
			_results
				.stream()
				.map(result -> result.toString())
				.collect(
					joining("\n", useHeadersCheckBox.isSelected() ? Result.header(USE_ANGLES) : "", "")
				)
		);
		Clipboard.getSystemClipboard().setContent(content);
	}

	@FXML
	private void copyProfiles(final ActionEvent event) {
		_profile.ifPresent(profile -> {
			final ClipboardContent content = new ClipboardContent();

			final Optional<ProfilePoint> sp = profile.stream().filter(p -> "scleral spur".equals(p.getType())).findFirst();
			content.putString(
				profile
					.stream()
					.map(point -> point.toString().concat(sp.isPresent() && point.getP1() != null ? String.format("\t%.2f", point.getP1().getX() - sp.get().getP1().getX()) : "\t"))
					.collect(joining("\n", useHeadersCheckBox.isSelected() ? "Filename\tcorrected\ttype\tx0\tx1\ty1\tarclength1\tcurvature1\tx2\ty2\tcurvature2\twidth\tx1-ssx1\n" : "", ""))
			);

			Clipboard.getSystemClipboard().setContent(content);
		});
	}

	@FXML
	private void showAbout(final ActionEvent event) {
		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aboutDialog.fxml"));
			final Parent parent = loader.load();
			parent.getStylesheets().add("/styles/CilOCT.css");

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("About");

			stage.showAndWait();
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	private void showSettings(final ActionEvent event) {
		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settingsDialog.fxml"));
			final Parent parent = loader.load();
			parent.getStylesheets().add("/styles/CilOCT.css");
			final SettingsDialogController controller = loader.getController();

			controller.setWidth(_calWidth);
			controller.setPixel(_calPixel);
			controller.setRefractiveIndexAir(_refractiveIndexAir);
			controller.setRefractiveIndexSclera(_refractiveIndexSclera);
			controller.setRefractiveIndexAqueousFluid(_refractiveIndexAqueousFluid);
			controller.setRefractiveIndexCiliaryMuscle(_refractiveIndexCiliaryMuscle);
			controller.setCiliaryMuscleEvaluationLength(_cmEvalLength);

			controller.setPort(_port);
			controller.setEpoch(_epoch);
			controller.setServer(_server);
			controller.setUser(_user);
			controller.setJob(_job);
			controller.setSSHUser(_sshUser);
			controller.setSSHPassword(_sshPassword);
			controller.setCaffePrototxtFile(_caffePrototxtFile);
			controller.setCaffeWeightsFile(_caffeWeightsFile);
			controller.setUseGPU(_useGPU);
			controller.setLocalOpenCV(_localOpenCV);
			controller.setRemoteDIGITS(_remoteDIGITS);
			controller.setUseSSH(_useSSH);

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Settings");
			controller.setStage(stage);

			stage.showAndWait();

			if (!controller.isCancelled()) {
				_calWidth = controller.getWidth();
				_calPixel = controller.getPixel();
				_refractiveIndexAir = controller.getRefractiveIndexAir();
				_refractiveIndexSclera = controller.getRefractiveIndexSclera();
				_refractiveIndexCiliaryMuscle = controller.getRefractiveIndexCiliaryMuscle();
				_refractiveIndexAqueousFluid = controller.getRefractiveIndexAqueousFluid();
				_cmEvalLength = controller.getCiliaryMuscleEvaluationLength();

				_port = controller.getPort();
				_epoch = controller.getEpoch();
				_server = controller.getServer();
				_user = controller.getUser();
				_job = controller.getJob();
				_sshUser = controller.getSSHUser();
				_sshPassword = controller.getSSHPassword();
				_useSSH = controller.isUseSSH();
				_caffePrototxtFile = controller.getCaffePrototxtFile();
				_caffeWeightsFile = controller.getCaffeWeightsFile();
				_useGPU = controller.isUseGPU();
				_localOpenCV = controller.isLocalOpenCV();
				_remoteDIGITS = controller.isRemoteDIGITS();

				savePreferences();
			}
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	private void saveLandmarks(final ActionEvent event) {
		final FileChooser chooser = new FileChooser();
		chooser.setTitle("Save landmarks");
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Landmark XML files", "*.landmark.xml"));
		final File initial = new File(Preferences.userNodeForPackage(getClass()).node("landmarks").get("lastDirectory", "."));
		if (initial.isDirectory()) {
			chooser.setInitialDirectory(initial);
		}
		chooser.setInitialFileName(_fileProperty.get().split("\\.")[0] + ".landmark.xml");
		final File file = chooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());
		if (file != null) {
			saveLandmarks(file);
		}
	}

	private void saveLandmarks(final File file) {
		final LandmarksVO vo = new LandmarksVO();
		_anteriorChamberStartPoint.ifPresent(vo::setAnteriorChamberStartPoint);
		_lensStartPoint.ifPresent(vo::setLensStartPoint);
		vo.setOuterCiliaryMuscleBorderLandmarks(_outerCiliaryMuscleBorderLandmarks);
		vo.setInnerCiliaryMuscleBorderLandmarks(_innerCiliaryMuscleBorderLandmarks);
		vo.setVerticalCiliaryMuscleBorderLandmarks(_verticalCiliaryMuscleBorderLandmarks);
		vo.setNewThresholdCalculation(_newThresholdCalculation);
		vo.setThresholdFactor(_thresholdFactor.get());
		vo.setLensThresholdFactor(_lensThresholdFactor.get());
		vo.setBrushes(_brushList);

		try {
			LandmarkMapper.create().writeValue(file, vo);
			Preferences.userNodeForPackage(getClass()).node("landmarks").put("lastDirectory", file.getParent());
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}


//			try (final XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file))) {
//				Arrays
//					.asList(_outerCiliaryMuscleBorderLandmarks, _innerCiliaryMuscleBorderLandmarks, _verticalCiliaryMuscleBorderLandmarks)
//					.forEach(landmarkList -> encoder.writeObject(landmarkList.stream().map(p -> new double[] {p.getX(), p.getY()}).collect(toList())));
//				_anteriorChamberStartPoint.ifPresent(p -> encoder.writeObject(new double[] {p.getX(), p.getY()}));
//				encoder.writeObject(_newThresholdCalculation);
//				encoder.writeObject(_thresholdFactor.get());
//				encoder.writeObject(_brushList);
//				Preferences.userNodeForPackage(getClass()).node("landmarks").put("lastDirectory", file.getParent());
//			} catch (FileNotFoundException ex) {
//				Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
//			}
	}

	@FXML
	private void loadLandmarks(final ActionEvent event) {
		final FileChooser chooser = new FileChooser();
		chooser.setTitle("Open landmarks");
		chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Landmark XML files", "*.landmark.xml"), new FileChooser.ExtensionFilter("Landmark XML files V1", "*.landmark.v1.xml"));
		final File initial = new File(Preferences.userNodeForPackage(getClass()).node("landmarks").get("lastDirectory", "."));
		if (initial.isDirectory()) {
			chooser.setInitialDirectory(initial);
		}
		chooser.setInitialFileName(_fileProperty.get().split("\\.")[0] + ".landmark.xml");
		final File file = chooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());
		if (file != null) {
			Preferences.userNodeForPackage(getClass()).node("landmarks").put("lastDirectory", file.getParent());
			loadLandmarks(file);
		}
	}

	private void loadLandmarks(final File file) {
		try {
			final LandmarksVO vo = LandmarkMapper.create().readValue(file, LandmarksVO.class);
			_outerCiliaryMuscleBorderLandmarks.setAll(vo.getOuterCiliaryMuscleBorderLandmarks());
			_innerCiliaryMuscleBorderLandmarks.setAll(vo.getInnerCiliaryMuscleBorderLandmarks());
			_verticalCiliaryMuscleBorderLandmarks.setAll(vo.getVerticalCiliaryMuscleBorderLandmarks());
			_anteriorChamberStartPoint.set(Optional.ofNullable(vo.getAnteriorChamberStartPoint()));
			_lensStartPoint.set(Optional.ofNullable(vo.getLensStartPoint()));
			_newThresholdCalculation = vo.isNewThresholdCalculation();
			_thresholdFactor.setValue(vo.getThresholdFactor());
			_lensThresholdFactor.setValue(vo.getLensThresholdFactor());

			_brushList.clear();
			if (vo.getBrushes() != null && !vo.getBrushes().isEmpty()) {
				_brushList.addAll(vo.getBrushes());
			}
		} catch (UnrecognizedPropertyException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, "Landmarks file uses old version. Fall back to load old version.");
			loadLandmarksOld(file);
			try {
				if (!file.getName().contains("v1")) {
					Files.copy(file.toPath(), Paths.get(file.getParent(), file.getName().replaceAll("landmark.xml", "landmark.v1.xml")));
					saveLandmarks(file);
					Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, "Landmarks file converted to new version and saved.");
				}
			} catch (IOException ex1) {
				Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex1);
			}
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}

		calcThreshold();

		_brushList.forEach(this::applyBrush);
		imageCanvas.fireEvent(new RepaintEvent(null));
		landmarksCanvasToggleButton.setSelected(true);
	}

	private void loadLandmarksOld(final File file) {
		try (final XMLDecoder decoder = new XMLDecoder(new FileInputStream(file))) {
			Arrays
				.asList(_outerCiliaryMuscleBorderLandmarks, _innerCiliaryMuscleBorderLandmarks, _verticalCiliaryMuscleBorderLandmarks)
				.forEach(landmarkList -> {
					landmarkList.clear();
					((List<double[]>) decoder.readObject()).forEach(d -> landmarkList.add(new Point(d[0], d[1])));
				});
			try {
				final double[] anteriorChamberStartPoint = (double[]) decoder.readObject();
				_anteriorChamberStartPoint.setActualValue(new Point(anteriorChamberStartPoint[0], anteriorChamberStartPoint[1]));
			} catch(ArrayIndexOutOfBoundsException e) {
			}
			try {
				final Object o = decoder.readObject();
				if (o instanceof Boolean) {
					_newThresholdCalculation = (Boolean) o;
					final Double thresholdFactor = (Double) decoder.readObject();
					_thresholdFactor.set(thresholdFactor);					
				} else {
					_newThresholdCalculation = false;
					final Double thresholdFactor = (Double) o;
					_thresholdFactor.set(thresholdFactor);
				}
			} catch(ArrayIndexOutOfBoundsException e) {
			}

			_brushList.clear();
			try {
				final List<Brush> brushList = (List<Brush>) decoder.readObject();
				if (brushList != null && !brushList.isEmpty()) {
					_brushList.addAll(brushList);
				}
			} catch(ArrayIndexOutOfBoundsException e) {
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	void exit(final ActionEvent event) {
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirm exit");
		alert.setHeaderText("Do you really want to quit the application?");
		alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> Platform.exit());
	}

	@FXML
	private void showHistogram(final ActionEvent e) {
		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/histogramDialog.fxml"));
			final Parent parent = loader.load();
			final HistogramDialogController controller = loader.getController();

			final Stage stage = new Stage();
			stage.setScene(new Scene(parent));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Histogram");

			controller.setMatrix(_srcMatrix);
			controller.setThreshold(_startThreshold * _thresholdFactor.get());

			stage.showAndWait();
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

    @FXML
    private void loadImage(final ActionEvent event) {
		landmarkToggleGroup.selectToggle(landmarkToggleGroup.getToggles().get(0));

		final ImageFileChooser chooser = new ImageFileChooser();
		final File file = chooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());

		loadImage(file, true);

		_corrected.setValue(false);
		_segmented.setValue(false);
		_ready.set(true);
		_scale.set(1);
    }

	@FXML
	private void gradients(final ActionEvent e) {
		if (_gradientsSet.get()) {
			final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setHeaderText("Gradients exist. Overwrite?");
			if (ButtonType.OK != alert.showAndWait().get()) {
				return;
			}
		}

		_executor.submit(new ExceptionCatchingTask(progressIndicator, "DNN Segmentation failed", _fileProperty.get()) {
			@Override
			protected List<Throwable> call() throws Exception {
				final List<Throwable> exceptions = new ArrayList<>(0);

				try {
					gradients();
				} catch (GradientException e) {
					exceptions.add(e);
				}

				return exceptions;
			}
		});
	}

	private void gradients() throws GradientException {
		final ICaffeSegment segmenter;
		if (_localOpenCV) {
			segmenter = new OpenCVCaffeSegment(new File(_caffePrototxtFile), new File(_caffeWeightsFile));
			((OpenCVCaffeSegment) segmenter).setUseGPU(_useGPU);
		} else {
			segmenter = new DigitsCaffeSegment(_server, _port, _user, _job, _epoch);
		}

		_segmentation = segmenter.segment(createImageFromMatrix());

		final IndexColorModel colorModel = new IndexColorModel(8, GroundTruthPalette.values().length, GroundTruthPalette.toArray(), 0, false, 0, DataBuffer.TYPE_BYTE);
		final BufferedImage image = new BufferedImage((int) Math.round(_width.get() * EXPORT_SCALE), (int) Math.round(_height.get() * EXPORT_SCALE), BufferedImage.TYPE_BYTE_INDEXED, colorModel);

		final double prob = _probability.doubleValue();
		_segmentation.forEach((c, points) -> points.stream().filter(p -> p.getProbability() > prob).forEach(p -> image.setRGB((int) Math.round(p.getX()*EXPORT_SCALE), (int) Math.round(p.getY()*EXPORT_SCALE), c.rgba())));

		try {
			ImageIO.write(image, "png", Paths.get("./segmented", _fileProperty.get() + ".png").toFile());
//			ImageIO.write(flippedImage, "png", Paths.get(targetDir.getAbsolutePath(), path.toFile().getName() + "_r.png").toFile());
		} catch (IOException ex) {
			Logger.getLogger(CilOCTController.class.getName()).log(Level.WARNING, null, ex);
		}


		setSegmentation();
		skeletonize();
		cluster();

		Platform.runLater(() -> {
			segmentationCanvasToggleButton.setSelected(false);
			gradientCanvasToggleButton.setSelected(true);
			_gradientsSet.set(true);
		});
	}

	private void setSegmentation() {
		if (_segmentation != null) {
			final double prob = _probability.doubleValue();
			_outerCiliaryMuscleBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.OUTER_CILIARY_MUSCLE_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_innerCiliaryMuscleBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.INNER_CILIARY_MUSCLE_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_outerConjunctivaBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.OUTER_CONJUNCTIVA_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_innerSclearalBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.INNER_SCLERAL_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_upperIrisBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.UPPER_IRIS_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_verticalCiliaryMuscleBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.VERTICAL_CILIARY_MUSCLE_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_lowerLensBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.LOWER_LENS_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_upperLensBorderGradients.setActualValue(_segmentation.get(GroundTruthPalette.UPPER_LENS_BORDER).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_lensPoints.setActualValue(_segmentation.get(GroundTruthPalette.LENS_AREA).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
			_anteriorChamberPoints.setActualValue(_segmentation.get(GroundTruthPalette.ANTERIOR_CHAMBER_AREA).stream().filter(p -> p.getProbability() > prob).collect(toCollection(PointList::new)));
		}
	}

	@FXML
	private void cluster(final ActionEvent e) {
		final List<PointList> segmentation = new ArrayList<>(6);
		_outerCiliaryMuscleBorderGradients.ifPresent(segmentation::add);
		_outerCiliaryMuscleBorderGradients.ifPresent(segmentation::add);
		_innerCiliaryMuscleBorderGradients.ifPresent(segmentation::add);
		_outerConjunctivaBorderGradients.ifPresent(segmentation::add);
		_innerSclearalBorderGradients.ifPresent(segmentation::add);
		_upperIrisBorderGradients.ifPresent(segmentation::add);
		_verticalCiliaryMuscleBorderGradients.ifPresent(segmentation::add);

		final DBSCANClusterer<Point> clusterer = new DBSCANClusterer<>(30, 20);

		segmentation.forEach(pl -> {
			final List<Cluster<Point>> clusters = clusterer.cluster(pl);
			clusters
				.stream()
				.flatMap(c -> c.getPoints().stream())
				.collect(Collectors.collectingAndThen(Collectors.toList(), pl::retainAll))
			;
			if (clusters.size() > 1) {
				clusters
					.stream()
					.map(Cluster::getPoints)
					.sorted(Comparator.comparingInt(List<?>::size).reversed())
					.skip(1)
					.forEach(pl::removeAll)
				;
			}
		});
		gradientCanvas.fireEvent(new RepaintEvent(null));
	}

	private void skeletonize() {
		skeletonize(new ActionEvent());
	}
	
	private void cluster() {
		cluster(new ActionEvent());
	}

	@FXML
	private void skeletonize(final ActionEvent e) {
		Stream
			.of(
				_outerCiliaryMuscleBorderGradients,
				_innerCiliaryMuscleBorderGradients,
				_outerConjunctivaBorderGradients,
				_innerSclearalBorderGradients,
				_upperIrisBorderGradients, 
				_verticalCiliaryMuscleBorderGradients
			)
			.filter(OptionalObjectProperty<PointList>::isPresent)
			.forEach(p ->  p.setActualValue(new Skeletonize(p.getActualValue(), 2).doZhangSuenThinning()));
		;
	}

	@FXML
	private void segment(final ActionEvent e) {
		_executor.execute(new ExceptionCatchingTask(progressIndicator, "Segmentation failed.", _fileProperty.get()) {
				@Override
				protected void succeeded() {
					landmarksCanvasToggleButton.setSelected(false);
					gradientCanvasToggleButton.setSelected(false);

					_segmented.set(true);
					_corrected.set(false);
					segmentationCanvasToggleButton.setSelected(true);

					_executor.execute(
						new ExceptionCatchingTask(progressIndicator, "Profile calculation failed.", _fileProperty.get()) {
							@Override
							protected List<Throwable> call() throws Exception {
								final List<Throwable> exceptions = new ArrayList<>(0);

								call(CilOCTController.this::calcProfile).ifPresent(exceptions::add);

								return exceptions;
							}
						}
					);
				}

				@Override
				protected List<Throwable> call() throws Exception {
					final List<Throwable> exceptions = new ArrayList<>(0);

					if (!_useAI.get()) {
						call(CilOCTController.this::createVerticalGradientMatrix).ifPresent(exceptions::add);
						call(CilOCTController.this::findAnteriorChamber).ifPresent(exceptions::add);
					}

					call(CilOCTController.this::fitInnerScleralBorder).ifPresent(exceptions::add);
					call(CilOCTController.this::fitUpperIrisBorder).ifPresent(exceptions::add);


					call(CilOCTController.this::fitOuterCiliaryMuscleBorder).ifPresent(exceptions::add);
					call(CilOCTController.this::fitInnerCiliaryMuscleBorder).ifPresent(exceptions::add);
					call(CilOCTController.this::fitConjunctivaBorder).ifPresent(exceptions::add);
					call(CilOCTController.this::fitVerticalCiliaryMuscleBorder).ifPresent(exceptions::add);

					call(CilOCTController.this::extrapolateOuterConjunctivaBorder).ifPresent(exceptions::add);

					return exceptions;
				}
			}
		);
	}

	@FXML
	private void correct(final ActionEvent e) {
		clearDeterminedValues();

		_executor.execute(
			new ExceptionCatchingTask(progressIndicator, "Refraction failed.", _fileProperty.get()) {
				@Override
				protected void succeeded() {
					_corrected.set(true);

					_executor.execute(
						new ExceptionCatchingTask(progressIndicator, "Profile calculation failed.", _fileProperty.get()) {
							@Override
							protected List<Throwable> call() throws Exception {
								final List<Throwable> exceptions = new ArrayList<>(0);

								call(CilOCTController.this::calcProfile).ifPresent(exceptions::add);

								return exceptions;
							}
						}
					);
				}
				
				@Override
				protected List<Throwable> call() throws Exception {
					final List<Throwable> exceptions = new ArrayList<>(0);

					call(CilOCTController.this::refract).ifPresent(exceptions::add);

					return exceptions;
				}
			}
		);
	}

	@FXML
	private void determine(final ActionEvent e) {
		_executor.execute(new ExceptionCatchingTask(progressIndicator, "Determining parameters failed.", _fileProperty.get()) {
				@Override
				protected void succeeded() {
					final Result result = new Result(
						_fileProperty.get(),
						Optional.ofNullable(_perpendicularAxisCiliaryMuscle.orElse(null)),
						Optional.ofNullable(_ciliaryApexAngle.orElse(null)),
						Optional.ofNullable(_innerAngle.orElse(null)),
						Optional.ofNullable(_ciliaryMuscleArea.orElse(null)),
						Optional.ofNullable(_ciliaryApex.orElse(null)),
						Optional.ofNullable(_scleralSpur.orElse(null)),
						Optional.ofNullable(_ciliaryApexShift.orElse(null))
					);
					_results.remove(result);
					_results.add(result);
				}

				@Override
				protected void done() {
					try {
						final List<Throwable> prevExceptions = get();
						_executor.execute(
							new ExceptionCatchingTask(progressIndicator, "Profile calculation failed.", _fileProperty.get()) {
								@Override
								protected List<Throwable> call() throws Exception {
									final List<Throwable> exceptions = new ArrayList<>(prevExceptions);
									
									call(CilOCTController.this::calcProfile).ifPresent(exceptions::add);
									
									return exceptions;
								}
							}
						);
					} catch (InterruptedException | ExecutionException ex) {
						Logger.getLogger(CilOCTController.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				@Override
				protected List<Throwable> call() throws Exception {
					final List<Throwable> exceptions = new ArrayList<>(0);

					call(CilOCTController.this::determineInnerAngleRecess).ifPresent(exceptions::add);
					if (USE_ANGLES) {
						call(CilOCTController.this::calcInnerAngle).ifPresent(exceptions::add);
					}
					call(CilOCTController.this::determineScleralSpur).ifPresent(exceptions::add);
					call(CilOCTController.this::determineCiliaryApex).ifPresent(exceptions::add);
					call(CilOCTController.this::determinePerpendicularAxisCiliaryMuscle).ifPresent(exceptions::add);
					if (USE_ANGLES) {
						call(CilOCTController.this::calcCiliaryApexAngle).ifPresent(exceptions::add);
					}
					call(CilOCTController.this::calcCiliaryMuscleArea).ifPresent(exceptions::add);
					call(CilOCTController.this::calcCiliaryApexShift).ifPresent(exceptions::add);

					return exceptions;
				}
			}
		);
	}

	@FXML
	private void stretchHistogram(final ActionEvent e) {
		ImageUtil.clahe(_srcMatrix, 33, 3d, 255);

		imageCanvas.fireEvent(new RepaintEvent(null));
	}

	@FXML
	private void handleMouseEnter() {
		_cursor.set(_prevExitCursor);
	}

	@FXML
	private void handleMouseExit() {
		_prevExitCursor = _cursor.get() == REMOVE_LANDMARK_CURSOR ? CROSSHAIR_CURSOR : _cursor.get();
		_cursor.set(Cursor.DEFAULT);
	}

	@FXML
	private void handleKeyPressLandmarkCanvas(final KeyEvent ke) {
		if (ke.isControlDown()) {
			_prevCursor = _cursor.get();
			_cursor.set(REMOVE_LANDMARK_CURSOR);
		}
	}

	@FXML
	private void handleKeyReleaseLandmarkCanvas(final KeyEvent ke) {
		if (_cursor.get() == REMOVE_LANDMARK_CURSOR) {
			_cursor.set(_prevCursor);
//		_cursor.set(CROSSHAIR_CURSOR);
		}
	}

	@FXML
	private void handleScroll(final ScrollEvent se) {
		if (se.isShiftDown() && se.isControlDown()) {
			final double currentValue = contrastSlider.getValue();
			final double delta = contrastSlider.getBlockIncrement() * 10 * Math.signum(se.getDeltaX());
			contrastSlider.adjustValue(delta + currentValue);
			se.consume();
		} else if (se.isShiftDown()) {
			final ActionEvent e = new ActionEvent(se.getSource(), se.getTarget());
			if (se.getDeltaX() < 0) {
				handleZoomOut(e);
			} else {
				handleZoomIn(e);
			}
			se.consume();
		}
	}

	@FXML
	private void handleResetZoom(final ActionEvent e) {
		_scale.set(1);
		_translateX.set(0);
		_translateY.set(0);
	}

	@FXML
	private void handleZoomIn(final ActionEvent e) {
		if (_ready.get()) {
			_scale.set(_scale.get() * ZOOM_FACTOR);
		}
	}

	@FXML
	private void handleZoomOut(final ActionEvent e) {
		if (_ready.get() && _scale.get() > 1) {
			_scale.set(_scale.get() / ZOOM_FACTOR);
			if (_scale.get() < 1) {
				_scale.set(1);
			}
		}
	}

	@FXML
	private void handleMouseClickQuickAccessBar(final MouseEvent me) {
		if (MouseButton.PRIMARY.equals(me.getButton()) && me.getClickCount() == 2) {
			maximize(new ActionEvent(me.getSource(), me.getTarget()));
		}
	}

	@FXML
	private void handleMouseEnterLandmarkCanvas(final MouseEvent me) {
		handleMouseEnter();
		if (_ready.get()) {
			landmarksCanvas.requestFocus();
		}
	}

	@FXML
	private void handleDragDetect(final MouseEvent me) {
		if (_ready.get()) {
			_prevCursor = _cursor.get();
			_cursor.set(DRAG_HAND_CURSOR);
			_dragging = true;
		}
	}

	@FXML
	private void handleMousePress(final MouseEvent me) {
		if (_ready.get()) {
			_mouseAnchorX = me.getX();
			_mouseAnchorY = me.getY();
		}
	}

	@FXML
	private void handleMouseRelease(final MouseEvent me) {
		if (_ready.get() && _dragging) {
			_cursor.set(_prevCursor);
		}
	}

	@FXML
	private void handleMouseDrag(final MouseEvent me) {
		if (_ready.get()) {
			final double deltaX = _translateX.get() + me.getX() - _mouseAnchorX;
			final double deltaY = _translateY.get() + me.getY() - _mouseAnchorY;
			_translateX.set(deltaX);
			_translateY.set(deltaY);

			_mouseAnchorX = me.getX();
			_mouseAnchorY = me.getY();
		}
	}

	@FXML
	private void handleMouseClickImageCanvas(final MouseEvent me) {
		if (_ready.get()) {
			if (_cursor.get() == BRUSH_DARKEN_CURSOR || _cursor.get() == BRUSH_LIGHTEN_CURSOR) {
				final Brush brush = new Brush((int) Math.round(me.getX()), (int) Math.round(me.getY()), (_cursor.get() == BRUSH_LIGHTEN_CURSOR ? BRUSH_VALUE : -BRUSH_VALUE), _brushRadius.get());
				applyBrush(brush);
				_brushList.add(brush);

				imageCanvas.fireEvent(new RepaintEvent(null));
			}
		}
	}

	@FXML
	private void handleMouseClickLandmarkCanvas(final MouseEvent me) {
		if (_ready.get()) {
			if (!_dragging) {
				if (_cursor.get() == ANTERIOUR_CHAMBER_POINT_CURSOR) {
					_anteriorChamberStartPoint.setActualValue(new Point(me.getX(), me.getY()));
					_cursor.setValue(CROSSHAIR_CURSOR);
					selectAnteriorChamberStartPointToggleButton.setSelected(false);
				} else if (_cursor.get() == LENS_START_POINT_CURSOR) {
//					final double y = _outerConjunctivaBorderFit.getActualValue().value(me.getX());
					_lensStartPoint.setActualValue(new Point(me.getX(), me.getY()));
					_cursor.setValue(CROSSHAIR_CURSOR);
					placeLensStartToggleButton.setSelected(false);
				} else {
					if (MouseButton.PRIMARY.equals(me.getButton())) {
						final List<Point> list;
						switch ((Segments) landmarkToggleGroup.getSelectedToggle().getUserData()) {
							case OUTER_CILIARY_MUSCLE_BORDER:
								list = _outerCiliaryMuscleBorderLandmarks;
								break;
							case INNER_CILIAR_MUSCLE_BORDER:
								list = _innerCiliaryMuscleBorderLandmarks;
								break;
							case VERTICAL_CILIARY_MUSCLE_BORDER:
								list = _verticalCiliaryMuscleBorderLandmarks;
								break;
							default:
								return;
						}

						if (me.isControlDown()) {
							if (_lensStartPoint.isPresent() && Math.abs(_lensStartPoint.getActualValue().getX() - me.getX()) < REMOVE_RADIUS && Math.abs(_lensStartPoint.getActualValue().getY() - me.getY()) < REMOVE_RADIUS) {
								_lensStartPoint.set(Optional.empty());
							} else {
								list.removeIf(p -> me.getX() > p.getX()-REMOVE_RADIUS && me.getX() < p.getX()+REMOVE_RADIUS && me.getY() > p.getY()-REMOVE_RADIUS && me.getY() < p.getY()+REMOVE_RADIUS);
							}
						} else {
							list.add(new Point(me.getX(), me.getY()));
						}
					} else if (MouseButton.SECONDARY.equals(me.getButton())) {			
						final int index = (landmarkToggleGroup.getToggles().indexOf(landmarkToggleGroup.getSelectedToggle()) + 1) % landmarkToggleGroup.getToggles().size();
						landmarkToggleGroup.selectToggle(landmarkToggleGroup.getToggles().get(index));
					}
				}
			} else {
				_dragging = false;
			}
		}
	}

	@FXML
	private void selectAnteriorChamberStartPoint(final ActionEvent event) {
		if (((ToggleButton) event.getSource()).isSelected()) {
			_prevExitCursor = ANTERIOUR_CHAMBER_POINT_CURSOR;
		} else {
			_prevExitCursor = CROSSHAIR_CURSOR;
		}
	}

	private BufferedImage createImageFromMatrix() {
		final BufferedImage image = new BufferedImage(_width.get(), _height.get(), BufferedImage.TYPE_BYTE_GRAY);

		_srcMatrixSave.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
			@Override
			public void visit(final int row, final int column, final double value) {
				image.getRaster().setPixel(column, row, new double[] {value});
			}
		});

		return image;
	}

	void loadImage(final File file, final boolean loadLandmarks) {
		try {
			if (file != null) {
				final String extension = (file.getName().lastIndexOf(".") > 0) ? file.getName().substring(file.getName().lastIndexOf(".")+1) : "";
				//final BufferedImage image = ImageIO.read(file);	

				if (ImageFileChooser.EXT_FILTER_DICOM.getExtensions().contains("*." + extension.toLowerCase())) {
					try (final DicomInputStream dis = new DicomInputStream(file)) {
						final Attributes attributes = dis.readDataset(-1, -1);
//System.out.println(attributes.toString());
						_patientName.set(Arrays.stream(attributes.getString(Tag.PatientName).split("\\^")).filter(StringUtils::isNoneBlank).collect(joining(", ")));
						_acquisitionDateTime.set(attributes.getDate(Tag.AcquisitionDateTime));

						_limitLeft = (int) (attributes.getFloat(TagUtils.toTag(0x0063, 0x1060), -1) * 2.5);
						_limitRight = (int) (attributes.getFloat(TagUtils.toTag(0x0063, 0x1065), -1) * 2.5);

						_eye.set(attributes.getString(Tag.Laterality));

						if ("DERIVED".equals(attributes.getString(Tag.ImageType))) {
							final Alert alert = new Alert(Alert.AlertType.WARNING);
							alert.setHeaderText(null);
							alert.setContentText("Image already de-warped!");
							alert.showAndWait();
						}
					}
				} else {
					final ChoiceDialog<String> choice = new ChoiceDialog<>("OD", "OD", "OS");
					choice.setTitle("Which eye was imaged?");
					choice.setHeaderText(null);
					choice.setContentText("Choose eye:");
					choice.showAndWait().ifPresent(_eye::set);
				}

				_srcMatrix = new ImageDataMatrix(ImageUtil.adjustVisanteDicomImage(ImageUtil.convertToGray(ImageIO.read(file))));
				_srcMatrixSave = _srcMatrix.copy();

				if (StringUtils.isNotEmpty(_eye.get()) && "os".equals(_eye.get().toLowerCase())) {
					_srcMatrix = MatrixUtil.flipVertical(_srcMatrix);
					_eye.set(String.format("%s (flipped)", _eye.get()));
				}

				if (!_useAI.get()) {
					calcThreshold();

					final double offlimit = _srcMatrix.getEntry(_srcMatrix.getRowDimension()-1, _srcMatrix.getColumnDimension()/2);
					_limitLower =
						FloodSelect.select(_srcMatrix, _srcMatrix.getColumnDimension()/2, _srcMatrix.getRowDimension()-1, (x, y) -> Double.compare(offlimit, _srcMatrix.getEntry(y, x)) == 0)
							.stream()
							.collect(groupingBy(Point::getX, minBy(Point::compareY)))
							.values()
							.stream()
							.filter(Optional::isPresent)
							.map(Optional::get)
							.mapToInt(Point::getRoundedY)
							.toArray()
					;
				}
				if (_limitLower == null || _limitLower.length < _srcMatrix.getColumnDimension()-10) {
					_limitLower = new int[_srcMatrix.getColumnDimension()];
					Arrays.fill(_limitLower, _srcMatrix.getRowDimension()-10);
				}
				if (_limitLeft < 0) {
					_limitLeft = 20;
				}
				if (_limitRight < 0) {
					_limitRight = _srcMatrix.getColumnDimension()-20;
				}
				
				clear();

				_anteriorChamberStartPoint.setActualValue(new Point(_srcMatrix.getColumnDimension()-_limitLeft-(_srcMatrix.getColumnDimension()-_limitRight)-2, _srcMatrix.getRowDimension()/3));

				imageCanvas.getGraphicsContext2D().drawImage(new WritableImage(_srcMatrix, _srcMatrix.getColumnDimension(), _srcMatrix.getRowDimension()), 0, 0);

				_fileProperty.set(file.getName());

				_ready.set(true);

				landmarksCanvasToggleButton.setSelected(true);
				segmentationCanvasToggleButton.setSelected(true);
				contrastSlider.setValue(0);
				_gradientsSet.set(false);

				if (loadLandmarks && !_useAI.get()) {
					final String landmarksFilename = _fileProperty.get().split("\\.")[0] + ".landmark.xml";
					final Path path = Paths.get(Preferences.userNodeForPackage(getClass()).node("landmarks").get("lastDirectory", "."), landmarksFilename);
	//				final Path path = Paths.get(file.getParent(), landmarksFilename);
					if (Files.exists(path)) {
						final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
						alert.setTitle("Load landmarks");
						alert.setHeaderText("A landmarks file exists for this image. Do you want to load it?");
						alert
							.showAndWait()
							.filter(response -> response == ButtonType.OK)
							.ifPresent(response -> loadLandmarks(path.toFile()));
					}
				}
			}
		} catch (IOException ex) {
			Platform.runLater(() -> {
				final Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText(String.format("Could not open file %s.", file));
				alert.setContentText(ex.getMessage());
				alert.showAndWait();
			});
		}
	}

	private void extrapolateOuterConjunctivaBorder(final ObservableValue<? extends Optional<Point>> observable, final Optional<Point> oldValue, final Optional<Point> newValue) {
		extrapolateOuterConjunctivaBorder();
	}

	private void extrapolateOuterConjunctivaBorder() {
		_outerConjunctivaBorderFit.andPresent(_lensStartPoint, (final BoundedPolynomialSplineFunction outerConjunctivaBorder, final Point lensStart) -> {
			fitLensBorders();

			if (_lowerLensFit.isPresent() && _upperLensFit.isPresent()) {
				final org.apache.commons.math3.analysis.polynomials.PolynomialFunction f2 = ((BoundedPolynomialFunction) _lowerLensFit.getActualValue()).polynomialDerivative().polynomialDerivative();

				final PointList pl = new PointList();
				for (double x = outerConjunctivaBorder.getX1(); x < _lowerLensFit.getActualValue().getX1()-10d; x++) {
					pl.add(new Point(x, outerConjunctivaBorder.value(x)));
				}
				for (double x = _lowerLensFit.getActualValue().getX1(); x < _lowerLensFit.getActualValue().getX2(); x++) {
					if (f2.value(x) > 0) {
						pl.add(new Point(x, _lowerLensFit.getActualValue().value(x)));
					}
				}
				final PolynomialFunction extrapolationFunction = MathUtil.fitTail(3, pl);
				_outerConjunctivaBorderExtrapolationFit.setActualValue(new BoundedPolynomialFunction(extrapolationFunction.getCoefficients(), outerConjunctivaBorder.getX1(), outerConjunctivaBorder.getX2()));
			}
		});
	}

	private void fitLensBorders() {
		if (_useAI.get()) {
				_lowerLensBorderGradients.andPresent(_upperLensBorderGradients, (lowerLensGradients, upperLensGradients) -> {
					final BoundedPolynomialSplineFunction blowerLens = MathUtil.fitPolynomialSpline(lowerLensGradients);
					final BoundedPolynomialFunction lowerLens = MathUtil.fitTail(3, blowerLens, blowerLens.getX1(), (int) Math.round(blowerLens.getX2()-blowerLens.getX1()));
					_lowerLensFit.setActualValue(lowerLens);

					final BoundedPolynomialSplineFunction upperLens = MathUtil.fitPolynomialSpline(upperLensGradients);
					_upperLensFit.setActualValue(upperLens);
				});
		} else {
			_outerConjunctivaBorderFit.andPresent(_lensStartPoint, (final BoundedPolynomialSplineFunction outerConjunctivaBorder, final Point lensStart) -> {
				final RealMatrix matrix = _srcMatrix.getSubMatrix(0, _srcMatrix.getRowDimension()-5, 0, (int) outerConjunctivaBorder.getX2());

				final double t = _srcMatrix.getSubMatrix(lensStart.getRoundedY()-1, lensStart.getRoundedY()+1, lensStart.getRoundedX()-1, lensStart.getRoundedX()+1).walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
					private double _sum = 0;
					private double _count = 0;

					@Override
					public void visit(final int row, final int column, final double value) {
						_sum += value;
						_count++;
					}

					@Override
					public double end() {
						return _sum / _count;
					}
				});
				final double t1 = t / _lensThresholdFactor.get();
				final double t2 = t * _lensThresholdFactor.get();

				final List<Point> points = FloodSelect
					.select(matrix, lensStart.getRoundedX(), lensStart.getRoundedY(), (x, y) -> matrix.getEntry(y, x) > t1 && matrix.getEntry(y, x) <= t2);
				_lensPoints.setActualValue(new PointList(points));
			});

			if (_lensPoints.isPresent()) {
				final PointList lowerLensGradients = createGradients(_lensPoints.getActualValue(), maxBy(Point::compareY),  p -> _verticalGradientMatrix.getEntry(p.getRoundedY(), p.getRoundedX()) > 2);
				_lowerLensBorderGradients.setActualValue(lowerLensGradients);
				final BoundedPolynomialSplineFunction blowerLens = MathUtil.fitPolynomialSpline(lowerLensGradients);
				final BoundedPolynomialFunction lowerLens = MathUtil.fitTail(3, blowerLens, blowerLens.getX1(), (int) Math.round(blowerLens.getX2()-blowerLens.getX1()));
				_lowerLensFit.setActualValue(lowerLens);

				final PointList upperLensGradients = createGradients(_lensPoints.getActualValue(), minBy(Point::compareY), p -> _verticalGradientMatrix.getEntry(p.getRoundedY(), p.getRoundedX()) < 2);
				_upperLensBorderGradients.setActualValue(upperLensGradients);
				final BoundedPolynomialSplineFunction upperLens = MathUtil.fitPolynomialSpline(upperLensGradients);
				_upperLensFit.setActualValue(upperLens);
			}
		}
	}

	private void calcThreshold() {
		if (_newThresholdCalculation) {
			final double[] histogram = MatrixUtil.histogram(_srcMatrix, true);
			final double maxValue = Arrays.stream(histogram).max().getAsDouble();
			_startThreshold = org.apache.commons.lang3.ArrayUtils.indexOf(histogram, maxValue) * 2d;
		} else {
			_startThreshold = MatrixUtil.getMax(_srcMatrix.getSubMatrix(0, 99, 0, 99));
		}
	}

	private void fitOuterCiliaryMuscleBorder() throws FitException {
		try {
			if (!_useAI.get()) {
				_outerCiliaryMuscleBorderGradients.setActualValue(createGradients(_outerCiliaryMuscleBorderLandmarks));	
			}

			final BoundedPolynomialSplineFunction fit = MathUtil.fitPolynomialSpline(_outerCiliaryMuscleBorderGradients.getActualValue());
			_outerCiliaryMuscleBorderFit.setActualValue(fit);
		} catch (GradientException | FitException e) {
			throw new FitException("Could not fit outer ciliary muscle border.", e);
		}
	}

	private void fitInnerCiliaryMuscleBorder() throws FitException {
		try {
			if (!_useAI.get()) {
				_innerCiliaryMuscleBorderGradients.setActualValue(createGradients(_innerCiliaryMuscleBorderLandmarks));	
			}
			final BoundedPolynomialSplineFunction fit = MathUtil.fitPolynomialSpline(_innerCiliaryMuscleBorderGradients.getActualValue());
			_innerCiliaryMuscleBorderFit.setActualValue(fit);
		} catch (GradientException | FitException e) {
			throw new FitException("Could not fit inner ciliary muscle border.", e);
		}
	}

	private void fitConjunctivaBorder() throws FitException {
		try {
			if (!_useAI.get()) {
				final int border = 5;
				final RealMatrix matrix = _srcMatrix.getSubMatrix(border, _srcMatrix.getRowDimension()-1, _limitLeft, _limitRight);
				final List<Point> points = FloodSelect
					.select(matrix, 1, 1, (x, y) -> matrix.getEntry(y, x) <= _startThreshold * _thresholdFactor.get());
				_airPoints.setActualValue(new PointList(points));
				final PointList pointList =
					createGradients(points, maxBy(Point::compareY), p -> true)
						.stream()
						.map(p -> new Point(p.getX() + _limitLeft, p.getY() + border))
						.collect(toCollection(PointList::new))
				;
				_outerConjunctivaBorderGradients.setActualValue(pointList);
			}

			final BoundedPolynomialSplineFunction fit = MathUtil.fitPolynomialSpline(_outerConjunctivaBorderGradients.getActualValue());
			_outerConjunctivaBorderFit.setActualValue(fit);
		} catch (OutOfRangeException | NumberIsTooSmallException | FitException | GradientException e) {
			throw new FitException("Could not fit conjunctiva border.", e);
		}
	}

	private void fitInnerScleralBorder() throws FitException {
		try {
			if (_anteriorChamberPoints.isPresent() && !_useAI.get()) {
				_innerSclearalBorderGradients.setActualValue(createGradients(_anteriorChamberPoints.getActualValue(), minBy(Point::compareY), p -> _verticalGradientMatrix.getEntry(p.getRoundedY(), p.getRoundedX()) < 2));
			}

			final BoundedPolynomialSplineFunction fit = MathUtil.fitPolynomialSpline(_innerSclearalBorderGradients.getActualValue());
			_innerScleralBorderFit.setActualValue(fit);
		} catch (NoSuchElementException | FitException | GradientException e) {
			throw new FitException("Could not fit inner scleral border.", e);
		}
	}

	private void fitUpperIrisBorder() throws FitException {
		try {
			if (!_useAI.get() && _anteriorChamberPoints.isPresent()) {
				_upperIrisBorderGradients.setActualValue(createGradients(_anteriorChamberPoints.getActualValue(), maxBy(Point::compareY),  p -> _verticalGradientMatrix.getEntry(p.getRoundedY(), p.getRoundedX()) > 2));
			}

			if (!_upperIrisBorderGradients.getActualValue().isEmpty()) {
				final BoundedPolynomialSplineFunction fit = MathUtil.fitPolynomialSpline(_upperIrisBorderGradients.getActualValue());
				_upperIrisBorderFit.setActualValue(fit);
			} else {
				throw new FitException("Gradient for upper iris border not found");
			}
		} catch (NoSuchElementException | FitException | GradientException e) {
			throw new FitException("Could not fit upper iris border.", e);
		}
	}

	private void fitVerticalCiliaryMuscleBorder() throws FitException {
		try {
			if (!_useAI.get()) {
				final BoundedPolynomialSplineFunction roughFunction = _verticalCiliaryMuscleBorderLandmarks.stream().map(p -> new Point(p.getY(), p.getX())).collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline));

				final PointList pointList = new PointList();
				for (double y = Math.min(roughFunction.getX1(), roughFunction.getX2()); y < Math.max(roughFunction.getX1(), roughFunction.getX2()); y++) {
					final int x = (int) Math.round(roughFunction.value(y));
					if (y >= 0 && y < _srcMatrix.getRowDimension()) {
						final double y1 = y;

						final BoundedPolynomialSplineFunction f = MathUtil.fitPolynomialSpline(
							IntStream
								.range(x - RANGE, x + RANGE)
								.filter(x1 -> x1 >= 0 && x1 < _srcMatrix.getColumnDimension())
								.mapToObj(x1 -> new Point(x1, _srcMatrix.getEntry((int) Math.round(y1), x1)))
								.collect(toList())
						);

						final UnivariateFunction f1 = f.derivative();

						DoubleStream
							.iterate(f.getX1(), d -> d + 1d)
							.limit(Math.round(f.getX2() - f.getX1()))
	//						.range((int) f.getX1(), (int) f.getX2())
							.reduce((a, b) -> f1.value(a) > f1.value(b) ? a : b)
							.ifPresent(maxX1 -> pointList.add(new Point(maxX1, y1)));
					}
				}
				_verticalCiliaryMuscleBorderGradients.setActualValue(pointList);
			}

			final Set<Double> set = new HashSet<>();
			final BoundedPolynomialSplineFunction fit =
				_verticalCiliaryMuscleBorderGradients.getActualValue()
					.stream()
					.map(p -> new Point(p.getY(), p.getX()))
					.collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline))
			;

			final double res = 0.5;

			final PointList points =
				DoubleStream
					.iterate(fit.getX1(), d -> d + res)
					.limit((int) Math.ceil((fit.getX2()-fit.getX1()) / res))
					.filter(fit::isValidPoint)
					.mapToObj(x -> new Point(fit.value(x), x))
					.collect(toCollection(PointList::new))
			;

			_verticalCiliaryMuscleBorderFitPoints.setActualValue(points);
		} catch (OutOfRangeException | NonMonotonicSequenceException | DimensionMismatchException | NoDataException | NotFiniteNumberException | NumberIsTooSmallException | FitException e) {
			throw new FitException("Could not fit vertical ciliary muscle border.", e);
		}
	}

	private PointList createGradients(final List<Point> landmarks) throws GradientException {
		try {
			final BoundedPolynomialSplineFunction roughFunction = MathUtil.fitPolynomialSpline(landmarks);

			return
				IntStream
					.range((int) Math.floor(roughFunction.getX1()), (int) Math.ceil(roughFunction.getX2()))
					.filter(x -> roughFunction.isValidPoint(x))
					.mapToObj(x -> new Point(x, roughFunction.value(x)))
					.flatMap(p -> IntStream.range((int) Math.floor(p.getY() - RANGE), (int) Math.ceil(p.getY() + RANGE)).mapToObj(y -> new Point(p.getX(), y)))
					.filter(p -> p.getY() >= 0 && p.getY() < _verticalGradientMatrix.getRowDimension())
					.collect(groupingBy(Point::getX, minBy((p1, p2) -> Double.compare(_verticalGradientMatrix.getEntry(p1.getRoundedY(), p1.getRoundedX()), _verticalGradientMatrix.getEntry(p2.getRoundedY(), p2.getRoundedX())))))
					.values()
					.stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(toCollection(PointList::new))
			;
		} catch (Exception e) {
			throw new GradientException(e);
		}
	}

	private PointList createGradients(final List<Point> points, final Collector<Point, ?, Optional<Point>> collector, final Predicate<Point> predicate) throws GradientException {
		try {
			return points
				.stream()
				.collect(groupingBy(Point::getX, collector))
				.values()
				.stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(predicate)
				.sorted(Point::compareX)
				.collect(toCollection(PointList::new))
			;
		} catch (Exception e) {
			throw new GradientException(e);
		}

	}

	private void findAnteriorChamber() throws GradientException {
		if (!_useAI.get()) {
			final Point startPoint = _anteriorChamberStartPoint.orElseThrow(() -> new GradientException("Could not determine anterior chamber. No start point placed."));
			try {
				//TODO: if using submatrix, the coordinates would have to be corrected!!
				final RealMatrix matrix = _srcMatrix;//.getSubMatrix(0, _srcMatrix.getRowDimension()-1, _limitLeft, _limitRight);

				_anteriorChamberPoints.setActualValue(
					FloodSelect
						.select(matrix, startPoint.getRoundedX(), startPoint.getRoundedY(), (x, y) -> y > 10 && x < _limitRight && y < _limitLower[x] && matrix.getEntry(y, x) < _startThreshold * _thresholdFactor.get())
						.stream()
						//.map(p -> new Point(p.getX() + _limitLeft, p.getY()))
	//					.map(p -> new Point(p.getX(), p.getY()))
						.collect(toCollection(PointList::new))
				);
			} catch (OutOfRangeException | NumberIsTooSmallException e) {
				throw new GradientException("Could not determine anterior chamber.", e);
			}
		}
	}

	private void determineInnerAngleRecess() throws POIException {
		try {
			_upperIrisBorderFit.andPresent(_innerScleralBorderFit, (fUpperIris, fInnerScleral) -> {
				final double minX = Math.min(fUpperIris.getX1(), fInnerScleral.getX1());

				final BoundedPolynomialFunction f1 = MathUtil.fitTail(2, fUpperIris, fUpperIris.getX1(), INNER_ANGLE_INTERPOLATION_LENGTH);
				final BoundedPolynomialFunction f2 = MathUtil.fitTail(2, fInnerScleral, fInnerScleral.getX1(), INNER_ANGLE_INTERPOLATION_LENGTH);

				final double[] zeros = MathUtil.intersect(f1, f2, minX-50, minX+50);
				if (zeros.length > 0) {
					final double intersectionX = zeros[0];
					final double intersectionY = f1.value(intersectionX);

					_innerAngleRecess.setActualValue(new Point(intersectionX, intersectionY));
				}
			});
		} catch (Exception e) {
			throw new POIException("Could not determine inner angle recess.", e);
		}
	}

	private void calcInnerAngle() throws CalculationException {
		try {
			_innerAngleRecess.ifPresent(innerAngleRecess -> {
				_innerScleralBorderFit.andPresent(_upperIrisBorderFit, (innerScleralBorder, upperIrisBorder) -> {
					final BoundedPolynomialFunction f1 = MathUtil.fitTail(2, innerScleralBorder, innerScleralBorder.getX1(), INNER_ANGLE_INTERPOLATION_LENGTH);
					final BoundedPolynomialFunction f2 = MathUtil.fitTail(2, upperIrisBorder, upperIrisBorder.getX1(), INNER_ANGLE_INTERPOLATION_LENGTH);

					final double angle = MathUtil.calcAngle(f1, f2, innerAngleRecess, MathUtil.AngleOpening.LEFT);
					final double startAngle = 180d-MathUtil.calcAngle(f1, innerAngleRecess);

					_innerAngle.setActualValue(new Angle(angle, startAngle, innerAngleRecess));
				});
			});
		} catch (Exception e) {
			throw new CalculationException("Could not calculate the inner angle.", e);
		}
	}

	private void determineScleralSpur() throws POIException {
		if (!_outerCiliaryMuscleBorderFit.isPresent() && !_verticalCiliaryMuscleBorderFitPoints.isPresent()) {
			throw new POIException("Could not determine scleral spur. Outer or vertical border missing. Try reposition landmarks.");
		}
		try {
			_outerCiliaryMuscleBorderFit.andPresent(_verticalCiliaryMuscleBorderFitPoints, (fouter, verticalPoints) -> {
				final Point start = new Point(fouter.getX2(), fouter.value(fouter.getX2()));
				final PolynomialFunction fvertical =
					verticalPoints
						.stream()
						.filter(p -> MathUtil.distance(p, start) < CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH)
						.map(p -> new Point(p.getY(), p.getX()))
						.collect(collectingAndThen(toList(), LinearInterpolator::fit))
						.reciprocal()
				;

				final BoundedPolynomialFunction fouter2 = MathUtil.fitTail(3, fouter, fouter.getX2()-CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH, 2*CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH);

				final Optional<Point> sp = 
					fouter2
						.intersect(fvertical)
						.stream()
						.reduce((p1, p2) -> Double.compare(MathUtil.distance(p1, start), MathUtil.distance(p2, start)) <= 0 ? p1 : p2)
				;
				if (sp.isPresent()) {
					_scleralSpur.setActualValue(sp.get());
				} else {
					throw new POIException("Could not determine scleral spur. Try reposition landmarks.");
				}
			});
		} catch (MathIllegalArgumentException | TooManyEvaluationsException | NoSuchElementException ex) {
			throw new POIException("Could not determine scleral spur. Try reposition landmarks.", ex);
		}
	}

	private void determineCiliaryApex() throws POIException {
		if (!_innerCiliaryMuscleBorderFit.isPresent() && !_verticalCiliaryMuscleBorderFitPoints.isPresent()) {
			throw new POIException("Could not determine ciliary apex. Inner or vertical border missing. Try reposition landmarks.");
		}

		try {
			_innerCiliaryMuscleBorderFit.andPresent(_verticalCiliaryMuscleBorderFitPoints, (finner, verticalPoints) -> {
				//final Point start = new Point(finner.getX2(), finner.value(finner.getX2()));
//				final Point start = verticalPoints.stream().filter(p -> finner.isValidPoint(p.getX())).min(new Comparator<Point>() {
//					@Override
//					public int compare(Point p1, Point p2) {
//						return Double.compare(Math.abs(finner.value(p1.getX()) - p1.getY()), Math.abs(finner.value(p2.getX()) - p2.getY()));
//					}
//				}).get();
				final Point max = verticalPoints.stream().max((p1, p2) -> Double.compare(p1.getY(), p2.getY())).get();
				final PolynomialFunction fvertical =
					verticalPoints
						.stream()
						//.filter(p -> MathUtil.distance(p, start) < 2*CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH)
						.filter(p -> MathUtil.distance(p, max) < CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH)
						.map(p -> new Point(p.getY(), p.getX()))
						.collect(collectingAndThen(toList(), LinearInterpolator::fit))
						.reciprocal()
				;
				final BoundedPolynomialFunction finner2 = MathUtil.fitTail(3, finner, finner.getX2()-2*CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH, 4*CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH);

				final List<Point> list = finner2.intersect(fvertical);
				final Optional<Point> apex = list.stream().findFirst();
//						.reduce((p1, p2) -> Double.compare(MathUtil.distance(p1, max), MathUtil.distance(p2, max)) <= 0 ? p1 : p2)
//				;
				
				if (apex.isPresent()) {
					_ciliaryApex.setActualValue(apex.get());
				} else {
					throw new POIException("Could not determine ciliary apex. Try reposition landmarks.");
				}
			});
		} catch (Exception ex) {
			throw new POIException("Could not determine ciliary apex. Try reposition landmarks.", ex);
		}
	}

	private void calcCiliaryApexAngle() throws CalculationException {
		try {
			_ciliaryApex.ifPresent(apex ->
				_innerCiliaryMuscleBorderFit.andPresent(_verticalCiliaryMuscleBorderFitPoints, (finner, verticalPoints) -> {
					final PolynomialFunction ginner = MathUtil.fitTail(1, finner, apex.getX() - CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH, CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH);

					final PolynomialFunction gvertical =
						verticalPoints
							.stream()
							.filter(p -> MathUtil.distance(apex, p) < CILIARY_APEX_ANGLE_INTERPOLATION_LENGTH)
							.map(p -> new Point(p.getY(), p.getX()))
							.collect(collectingAndThen(toList(), MathUtil::fitTail))
							.reciprocal()
					;
final double a = ginner.derivative().value(apex.getX());
final double b = gvertical.derivative().value(apex.getX());

					final double angle = (a >= 0 && b < 0) ? 180d - MathUtil.calcAngle(ginner, gvertical, apex, MathUtil.AngleOpening.UP) : MathUtil.calcAngle(ginner, gvertical, apex, MathUtil.AngleOpening.UP);
					final double startAngle = MathUtil.calcAngle(ginner, apex);

					_ciliaryApexAngle.setActualValue(new Angle(angle, startAngle, apex));
				})
			);
		} catch (Exception e) {
			throw new CalculationException("Could not calculate the ciliary apex angle.", e);
		}
	}

	private void calcCiliaryApexShift() {
		_outerCiliaryMuscleBorderFit.ifPresent(fouter -> {
			_ciliaryApex.andPresent(_scleralSpur, (apex, scleralSpur) -> {
				if (fouter.isValidPoint(apex.getX())) {
//					final double y1 = fouter.derivative().value(apex.getX());
//					final double b = fouter.value(apex.getX()) + apex.getX() / y1;
//					final double m = -1d / y1;
//					final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });
//					final double yy = perpendicular.value(scleralSpur.getX());
//					final double side = scleralSpur.getY() > yy ? -1 : 1;
//					final double distance = Math.abs(b + m * scleralSpur.getX() - scleralSpur.getY()) / Math.sqrt(1 + m * m);
//					_ciliaryApexShift.setActualValue(distance * side);

					final PolynomialFunction f = MathUtil.fitTail(2, fouter, fouter.getX2()-100, fouter.getX2());

					final double m = -1d / f.derivative().value(scleralSpur.getX());
					final double b = m * (0d - scleralSpur.getX()) + scleralSpur.getY();
					final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });
					
					double distance = Math.abs((m * apex.getX() - apex.getY() + b) / Math.sqrt(1d + m * m));
					
					final double m1 = -1d / m;
					final double b1 = m1 * (0d - apex.getX()) + apex.getY();
					final PolynomialFunction perpendicular2 = new PolynomialFunction(new double[] { b1, m1 });
					final Point lot = perpendicular.intersect(perpendicular2, 0, _width.doubleValue()).get(0);
					
					if (apex.getX() < lot.getX()) {
						distance *= -1d;
					}

					_debug.add(new FunctionDrawable(f, Color.BLUE, fouter.getX2()-100, fouter.getX2()+10));
					_debug.add(new FunctionDrawable(perpendicular, Color.BLUE, 0, _width.doubleValue()));
					_debug.add(new FunctionDrawable(perpendicular2, Color.BLUE, apex.getX(), lot.getX()));
					
					_ciliaryApexShift.setActualValue(distance);
				}
			});
		});
	}

	private void calcCiliaryMuscleArea() {
//		try {
			_scleralSpur.andPresent(_ciliaryApex, (scleralSpur, apex) -> {
				if (scleralSpur.getX() <= apex.getX()) {
					throw new CalculationException("Could not calculate ciliary muscle area. Scleral spur < apex.");
				}
				_innerCiliaryMuscleBorderFit.andPresent(_verticalCiliaryMuscleBorderFitPoints, (finner, verticalPoints) -> {
					_outerCiliaryMuscleBorderFit.ifPresent(fouter -> {
						double startX = Math.min(fouter.getX2(), scleralSpur.getX())-1;
						final double end = Math.min(fouter.getX2()-0.01, scleralSpur.getX());
						for (double value = 0; value <= _cmEvalLength/_mmPerPixel; startX-=0.1) {
							value = fouter.arclength(startX, end);
						}

						final double y1 = fouter.derivative().value(startX);
						final double b = fouter.value(startX) + startX / y1;
						final double m = -1d / y1;
						final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });

						final double startX2 = finner.intersect(perpendicular).stream().findFirst().get().getX();

						final double area9 = finner.integrate(Math.min(startX, startX2), Math.max(startX, startX2));
						final double area10 = (startX2 - startX) * fouter.value(startX);
						final double area11 = ((startX2 - startX) * (finner.value(startX2) - fouter.value(startX))) / 2d;
						final double area12 = area9 - (area10 + area11);

						final BoundedPolynomialSplineFunction fvertical =
							verticalPoints
								.stream()
								.map(p -> new Point(p.getY(), p.getX()))
								.collect(collectingAndThen(toList(), MathUtil::fitPolynomialSpline))
						;

						final double areaOuter = fouter.integrate(startX, apex.getX());
						final double areaInner = finner.integrate(startX, Math.min(apex.getX(), finner.getX2()));

						final double area1 = areaInner - areaOuter;
						
						final double area2 = fouter.integrate(apex.getX(), Math.min(scleralSpur.getX(), fouter.getX2()));
						final double area3 = (scleralSpur.getX() - apex.getX()) * scleralSpur.getY();
						
						final double area4 = area3 - area2;
						
						final double area5 = area1 + area4;
						
						final double area6 = fvertical.integrate();
						final double area7 = (apex.getY() - scleralSpur.getY()) * apex.getX();
						final double area8 = area6 - area7;
						
						final double area = area5 + area8 - area12;

						_ciliaryMuscleArea.setActualValue(area);

						final PointList areaPoints = new PointList();
						final double endX = verticalPoints.stream().mapToDouble(Point::getX).max().getAsDouble();
						for (int x = (int) Math.round(startX); x < endX; x++) {
							for (int y = 0; y < _srcMatrix.getRowDimension(); y++) {
								if (y <= perpendicular.value(x)) {
									if (fouter.isValidPoint(x) && y >= fouter.value(x)) {
										if (finner.isValidPoint(x) && y <= finner.value(x)) {
											areaPoints.add(new Point(x, y));
										}
										if (x > apex.getX() && y < fvertical.getX1()) {
											areaPoints.add(new Point(x, y));
										}
									}
									if (x > apex.getX() && fvertical.isValidPoint(y) && x <= fvertical.value(y)) {
										areaPoints.add(new Point(x, y)); 
									}
								}
							}
						}

						_ciliaryMuscleAreaPoints.setActualValue(areaPoints);
					});
				});
			});
//		} catch (Exception e) {
//			throw new CalculationException("Could not calculate ciliary muscle area.", e);
//		}
	}

	private void determinePerpendicularAxisCiliaryMuscle() throws CalculationException {
		try {
			_ciliaryApex.andPresent(_outerCiliaryMuscleBorderFit, (apex, fouter) -> {
				
				final UnivariateFunction ff;
//				if (apex.getX() > fouter.getX2()) {
//					final double m = (apex.getY() - fouter.value(fouter.getX2())) / (apex.getX() - fouter.getX2());
//					final double t = -m * apex.getX() + apex.getY();
//					ff = new PolynomialFunction(new double[] {t, m});
//				} else {
					ff = fouter.derivative();
//				}
				final double y1 = ff.value(apex.getX());
				final double b = apex.getY() + apex.getX() / y1;
				final double m = -1d / y1;
				final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });

				final List<Line> lines = intersect(fouter, perpendicular, apex);
				//final Line line = lines.stream().reduce((l1, l2) -> l1.compareTo(l2) < 0 ? l1 : l2).orElseThrow(() -> new CalculationException("Could not determine the ciliary muscle perdendicular axis."));
				final Line line = lines.stream().reduce((l1, l2) -> Double.compare(Math.abs(l1.getEnd().getX() - apex.getX()), Math.abs(l2.getEnd().getX() - apex.getX())) < 0 ? l1 : l2 ).orElseThrow(() -> new CalculationException("Could not determine the ciliary muscle perdendicular axis."));

				_perpendicularAxisCiliaryMuscle.setActualValue(line);
			});
		} catch (Exception e) {
			throw new CalculationException("Could not determine the ciliary muscle perdendicular axis.", e);
		}
	}

	private void refract() {
		final Interface startBorder = new Interface("start", new BoundedPolynomialFunction(new double[] {0d}, 0, _width.doubleValue()), _refractiveIndexAir);

		_outerConjunctivaBorderFit.ifPresent(outerConjunctivaBorder -> {
			_outerCiliaryMuscleBorderFit.andPresent(_innerCiliaryMuscleBorderFit, (outerCiliaryMuscleBorder, innerCiliaryMuscleBorder) -> {
				_innerScleralBorderFit.andPresent(_upperIrisBorderFit, (innerScleralBorder, upperIrisBorder) ->
					_verticalCiliaryMuscleBorderFitPoints.ifPresent(verticalCiliaryMuscleBorder -> {
						final Rays rays = new Rays(0.1d);

						rays.addInterface(startBorder);
						rays.addInterface(new Interface("outerConjunctivaBorder", outerConjunctivaBorder, _refractiveIndexSclera));
						rays.addInterface(new Interface("outerCiliaryMuscleBorder", outerCiliaryMuscleBorder, _refractiveIndexCiliaryMuscle));
						rays.addInterface(new Interface("innerCiliaryMuscleBorder", innerCiliaryMuscleBorder, Double.NaN));
						rays.addInterface(new Interface("innerScleralBorder", innerScleralBorder, _refractiveIndexAqueousFluid));
						rays.addInterface(new Interface("upperIrisBorder", upperIrisBorder, Double.NaN));
						rays.addInterface(new InterfacePointList("verticalCiliaryMuscleBorder", verticalCiliaryMuscleBorder, _refractiveIndexSclera));

						rays.createRays(outerConjunctivaBorder.getX1(), outerConjunctivaBorder.getX2());

						rays.refract();

						final List<String> names = rays.getInterfaces().stream().skip(2).map(IInterface::getName).collect(toList());
						rays
							.getInterfaces()
//							.parallelStream()
							.forEach(iface ->
									Util.match(
										iface.getName(),
										names,
										_outerCiliaryMuscleBorderFit,
										_innerCiliaryMuscleBorderFit,
										_innerScleralBorderFit,
										_upperIrisBorderFit,
										_verticalCiliaryMuscleBorderFitPoints
									)
									.ifPresent(p -> {
										if (iface instanceof InterfacePointList) {
											((OptionalObjectProperty<PointList>) p).setActualValue((PointList) iface.getBorder());
										} else {
											((OptionalObjectProperty<BoundedPolynomialSplineFunction>) p).setActualValue((BoundedPolynomialSplineFunction) iface.getBorder());
										}
									})
							);

						rays.debug(_debug, 1, 20);
						rays.debug(_debug, 2, 20);
					})
				);
			});
		});

	}

	private void calcProfile() {
		final OptionalObjectProperty<? extends IBoundedFunction> oop = _outerConjunctivaBorderExtrapolationFit.isPresent() ? _outerConjunctivaBorderExtrapolationFit : _outerConjunctivaBorderFit;
		oop.andPresent(_outerCiliaryMuscleBorderFit, (outerConjunctivaFit, outerCiliaryMuscleFit) -> {
			final List<ProfilePoint> points = new ArrayList<>();

			_innerCiliaryMuscleBorderFit.ifPresent(innerCiliaryMuscleFit -> {
				for (double x = 0; x < _width.doubleValue(); x+=1) {
					if (outerConjunctivaFit.isValidPoint(x)) {
						final double yl = outerConjunctivaFit.derivative().value(x);
						final double b = outerConjunctivaFit.value(x) + x / yl;
						final double m = -1d / yl;
						final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });

						final Point p1 = outerCiliaryMuscleFit.intersect(perpendicular).stream().findFirst().orElse(null);
						if (p1 != null) {
							final Point p2 = innerCiliaryMuscleFit.intersect(perpendicular).stream().findFirst().orElse(null);
							
							final double k1 = outerCiliaryMuscleFit.curvature(p1.getX());
							final double arcLength = outerCiliaryMuscleFit.arclength(outerCiliaryMuscleFit.getX1(), p1.getX());
							if (p2 != null) {
//								if (x % 20 == 0) {
//								_debug.add(new FunctionDrawable(perpendicular, Color.CYAN, 0, 1280));
//								}
//								_debug.add(new PointDrawable(p1, Color.DEEPPINK));
//								_debug.add(new PointDrawable(p2, Color.VIOLET));
	
								
								final double k2 = innerCiliaryMuscleFit.curvature(p2.getX());
								final double width = Math.abs(MathUtil.distance(p1, p2));

								points.add(new ProfilePoint(x, p1, arcLength, k1, p2, k2, width, "inner"));
							} else {
								points.add(new ProfilePoint(x, p1, arcLength, k1, "inner"));
							}				
						} else {
							points.add(new ProfilePoint(x, "none"));
						}
					} else {
						points.add(new ProfilePoint(x, "none"));
					}
				}

				_scleralSpur.ifPresent(p -> {
					if (outerConjunctivaFit.isValidPoint(p.getX())) {
						final double arcLength = outerCiliaryMuscleFit.arclength(outerCiliaryMuscleFit.getX1(), Math.min(p.getX(), outerCiliaryMuscleFit.getX2()));
						final double curvature = outerCiliaryMuscleFit.curvature(Math.min(p.getX(), outerCiliaryMuscleFit.getX2()));
						final double y1 = outerConjunctivaFit.derivative().value(p.getX());
						final double b = p.getY() + p.getX() / y1;
						final double m = -1d / y1;
						final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });
//						_debug.add(new FunctionDrawable(perpendicular, Color.CYAN, 0, 1280));

						final ProfilePoint pp =
							outerCiliaryMuscleFit
								.intersect(perpendicular)
								.stream()
								.findFirst()
								.map(p1 -> new ProfilePoint(p.getX(), p1, arcLength, curvature, null, null, MathUtil.distance(p1, p), "scleral spur"))
								.orElse(
									MathUtil.fitTail(1, outerCiliaryMuscleFit, outerCiliaryMuscleFit.getX2(), 10)
										.intersect(perpendicular)
										.stream()
										.findFirst()
										.map(p1 -> new ProfilePoint(p.getX(), p1, arcLength, curvature, null, null, MathUtil.distance(p1, p), "scleral spur"))
										.orElse(null)
								)
						;
						if (pp != null) {
							points.add(pp);
						} else {
							throw new CalculationException("Could not find intersection between perpendicular and outer border at scleral spur");
						}
					} else {
						throw new CalculationException("Outer border too short.");
					}
				});

				_ciliaryApex.ifPresent(p -> {
					if (outerConjunctivaFit.isValidPoint(p.getX())) {
						final double yl = outerConjunctivaFit.derivative().value(p.getX());
						final double b = p.getY() + p.getX() / yl;
						final double m = -1d / yl;
						final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });
						final Point p1 = outerCiliaryMuscleFit.intersect(perpendicular).get(0);
						final double arcLength = outerCiliaryMuscleFit.arclength(outerCiliaryMuscleFit.getX1(), p1.getX());
						final double curvature = outerCiliaryMuscleFit.curvature(p1.getX());
						final double width = MathUtil.distance(p1, p);
						points.add(new ProfilePoint(p.getX(), p1, arcLength, curvature, p, null, width, "ciliary apex"));
					}
				});
			});

			_verticalCiliaryMuscleBorderFitPoints.ifPresent(verticalCiliaryMusclePoints -> {
				verticalCiliaryMusclePoints
					.stream()
					.forEach(p -> {
						if (outerConjunctivaFit.isValidPoint(p.getX())) {
							final double yl = outerConjunctivaFit.derivative().value(p.getX());
							final double b = outerConjunctivaFit.value(p.getX()) + p.getX() / yl;
							final double m = -1d / yl;
							final PolynomialFunction perpendicular = new PolynomialFunction(new double[] { b, m });
							final Point p1 = outerCiliaryMuscleFit.intersect(perpendicular).stream().findFirst().orElse(null);
							if (p1 != null) {
								final double arcLength = outerCiliaryMuscleFit.arclength(outerCiliaryMuscleFit.getX1(), p1.getX());
								final double curvature = outerCiliaryMuscleFit.curvature(p1.getX());
								final double width = MathUtil.distance(p1, p);

								points.add(new ProfilePoint(p.getX(), p1, arcLength, curvature, p, null, width, "vertical"));
							} else {
								points.add(new ProfilePoint(p.getX(), "vertical"));
							}
						}
					});
			});

			if (!points.isEmpty()) {
				_profile.setActualValue(new Profile(_fileProperty.get(), _corrected.get(), points));
			}
		});
	}

	private List<Line> intersect(final BoundedPolynomialSplineFunction f1, final PolynomialFunction f2, final Point start) throws NoSuchElementException {
		return f1.intersect(f2)
			.stream()
			.filter(p -> Math.round(p.getX()) < f1.getX2())
			.map(p -> new Line(start, p))
			.collect(Collectors.toList())
		;
	}

	private void createVerticalGradientMatrix() {
		_verticalGradientMatrix = MatrixUtil.horizontalGradient(MatrixUtil.medianFilter(_srcMatrix));
	}

	private void applyBrush(final Brush brush) {
		int x1 = brush.getX() - brush.getRadius();
		x1 = (x1 < 0) ? 0 : x1;
		int x2 = brush.getX() + brush.getRadius();
		x2 = (x2 < _srcMatrix.getColumnDimension()) ? x2 : _srcMatrix.getColumnDimension()-1;
		int y1 = brush.getY() - brush.getRadius();
		y1 = (y1 < 0) ? 0 : y1;
		int y2 = brush.getY() + brush.getRadius();
		y2 = (y2 < _srcMatrix.getRowDimension()) ? y2 : _srcMatrix.getRowDimension()-1;
		_srcMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
			@Override
			public double visit(final int y, final int x, final double value) {
				if (Math.pow(x - brush.getX(), 2) + Math.pow(y - brush.getY(), 2) < brush.getRadius() * brush.getRadius()) {
					final double newValue = value + brush.getValue();
					return (newValue <= 255) ? newValue >= 0 ? newValue : 0 : 255;
				} else {
					return value;
				}
			}
		}, y1, y2, x1, x2);
	}

	private void clear() {
		_thresholdFactor.set(1.0);
		_brushList.clear();

		Arrays.asList(
			_outerCiliaryMuscleBorderLandmarks, 
			_innerCiliaryMuscleBorderLandmarks,
			_verticalCiliaryMuscleBorderLandmarks,
			_debug,
			_debug2
		).forEach(l -> l.clear());

		Arrays.asList(
			_outerCiliaryMuscleBorderFit,
			_anteriorChamberPoints,
			_anteriorChamberStartPoint,
			_lensStartPoint,
			_lensPoints,

			_outerCiliaryMuscleBorderGradients,
			_innerCiliaryMuscleBorderGradients,
			_verticalCiliaryMuscleBorderGradients,
			_innerSclearalBorderGradients,
			_upperIrisBorderGradients,
			_outerConjunctivaBorderGradients,

			_innerCiliaryMuscleBorderFit,
			_verticalCiliaryMuscleBorderFitPoints,
			_ciliaryMuscleAreaPoints,
			_outerConjunctivaBorderFit,
			_outerConjunctivaBorderExtrapolationFit,
			_innerScleralBorderFit,
			_upperIrisBorderFit,

			_lowerLensFit,
			_upperLensFit
		).forEach(p -> p.empty());

		clearDeterminedValues();

		Arrays
			.asList(caliperCanvas, segmentationCanvas, landmarksCanvas, imageCanvas, gradientCanvas, poiCanvas)
			.forEach(canvas -> canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight()))
		;
	}

	private void clearDeterminedValues() {
		Arrays.asList(
			_perpendicularAxisCiliaryMuscle,
			_ciliaryApex,
			_scleralSpur,
			_innerAngleRecess,
			_innerAngle,
			_ciliaryApexAngle,
			_ciliaryMuscleArea,
			_ciliaryApexShift,
			_profile
		).forEach(p -> p.empty());

		_debug.clear();
		_debug2.clear();
	}

	private void initPreferences() {
		final Preferences settings = Preferences.userNodeForPackage(getClass()).node("settings");

		final Preferences refractiveIndices = settings.node("refractiveIndices");
		_refractiveIndexAir = refractiveIndices.getDouble("air", 1.0);
		_refractiveIndexCiliaryMuscle = refractiveIndices.getDouble("ciliaryMuscle", 1.38);
		_refractiveIndexSclera = refractiveIndices.getDouble("sclera", 1.41);
		_refractiveIndexAqueousFluid = refractiveIndices.getDouble("aqueousFluid", 1.34);

		final Preferences resolution = settings.node("resolution");
		_calPixel = resolution.getDouble("pixel", 128);
		_calWidth = resolution.getDouble("width", 1);
		_mmPerPixel = _calWidth / _calPixel;
		_cmEvalLength = settings.node("analysis").getDouble("ciliaryMuscleEvaluationLength", 4);

		final Preferences nn = settings.node("nn");
		_localOpenCV = nn.getBoolean("useOpenCV", true);
		_remoteDIGITS = nn.getBoolean("useRemoteDIGITS", false);

		final Preferences remote = nn.node("DIGITS");
		_port = remote.getInt("port", 5000);
		_epoch = remote.getInt("epoch", 0);
		_server = remote.get("server", "");
		_user = remote.get("user", "");
		_job = remote.get("job", "");
		_useSSH = remote.getBoolean("useSSH", false);
		if (_useSSH) {
			final Preferences ssh = remote.node("ssh");
			_sshUser = ssh.get("user", "");
			final String password = ssh.get("password", "");
			if (!password.isEmpty()) {
				try {
					_sshPassword = ENCRYPTOR.decrypt(password);
				} catch (EncryptionOperationNotPossibleException e) {
					_sshPassword = "";
				}
			}
		}

		final Preferences opencv = nn.node("openCV");
		_caffePrototxtFile = opencv.get("prototxt", "");
		_caffeWeightsFile = opencv.get("weights", "");
		_useGPU = opencv.getBoolean("useGPU", false);
	}

	private void savePreferences() {
		final Preferences settings = Preferences.userNodeForPackage(getClass()).node("settings");

		final Preferences refractiveIndices = settings.node("refractiveIndices");
		refractiveIndices.putDouble("air", _refractiveIndexAir);
		refractiveIndices.putDouble("ciliaryMuscle", _refractiveIndexCiliaryMuscle);
		refractiveIndices.putDouble("sclera", _refractiveIndexSclera);
		refractiveIndices.putDouble("aqueousFluid", _refractiveIndexAqueousFluid);

		final Preferences resolution = settings.node("resolution");
		resolution.putDouble("pixel", _calPixel);
		resolution.putDouble("width", _calWidth);

		settings.node("analysis").putDouble("ciliaryMuscleEvaluationLength", _cmEvalLength);

		final Preferences nn = settings.node("nn");
		nn.putBoolean("useOpenCV", _localOpenCV);
		nn.putBoolean("useRemoteDIGITS", _remoteDIGITS);
		if (_localOpenCV) {
			final Preferences opencv = nn.node("openCV");
			opencv.put("prototxt", _caffePrototxtFile);
			opencv.put("weights", _caffeWeightsFile);
			opencv.putBoolean("useGPU", _useGPU);
		}
		if (_remoteDIGITS) {
			final Preferences remote = nn.node("DIGITS");
			remote.putInt("port", _port);
			remote.putInt("epoch", _epoch);
			remote.put("server", _server);
			remote.put("user", _user);
			remote.put("job", _job);
			remote.putBoolean("useSSH", _useSSH);
			if (_useSSH) {
				final Preferences ssh = remote.node("ssh");
				ssh.put("user", _sshUser);
				ssh.put("password", ENCRYPTOR.encrypt(_sshPassword));
			}
		}
	}
}
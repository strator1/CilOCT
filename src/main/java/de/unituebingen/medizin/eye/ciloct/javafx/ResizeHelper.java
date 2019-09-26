package de.unituebingen.medizin.eye.ciloct.javafx;

/**
 * @author strasser
 * created by Alexander Berg
 * https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage
 */
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeHelper {
    public static void addResizeListener(final Stage stage, final ObjectProperty<Cursor> cursorProperty) {
        final ResizeListener resizeListener = new ResizeListener(stage, cursorProperty);

		stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);

		stage
			.getScene()
			.getRoot()
			.getChildrenUnmodifiable()
			.forEach(child -> addListenerDeeply(child, resizeListener));
    }

    private static void addListenerDeeply(final Node node, final EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);

		if (node instanceof Parent) {
            final Parent parent = (Parent) node;
			parent
				.getChildrenUnmodifiable()
				.forEach(child -> addListenerDeeply(child, listener));
        }
    }

    private static class ResizeListener implements EventHandler<MouseEvent> {
        private final static int BORDER = 4;

		private final ObjectProperty<Cursor> _cursorProperty;
		private final Stage _stage;

        private Cursor cursorEvent = Cursor.DEFAULT;
        private double startX = 0;
        private double startY = 0;

        public ResizeListener(final Stage stage, final ObjectProperty<Cursor> cursorProperty) {
            _stage = stage;
			_cursorProperty = cursorProperty;
        }

        @Override
        public void handle(final MouseEvent mouseEvent) {
            final EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            final Scene scene = _stage.getScene();

            final double mouseEventX = mouseEvent.getSceneX();
            final double mouseEventY = mouseEvent.getSceneY();
            final double sceneWidth = scene.getWidth();
            final double sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                if (mouseEventX < BORDER && mouseEventY < BORDER) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < BORDER && mouseEventY > sceneHeight - BORDER) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - BORDER && mouseEventY < BORDER) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - BORDER && mouseEventY > sceneHeight - BORDER) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < BORDER) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - BORDER) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < BORDER) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - BORDER) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }

				_cursorProperty.set(cursorEvent);
            } else if(MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)){
                _cursorProperty.set(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                startX = _stage.getWidth() - mouseEventX;
                startY = _stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                if (!Cursor.DEFAULT.equals(cursorEvent)) {
                    if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                        double minHeight = _stage.getMinHeight() > (BORDER*2) ? _stage.getMinHeight() : (BORDER*2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                            if (_stage.getHeight() > minHeight || mouseEventY < 0) {
                                _stage.setHeight(_stage.getY() - mouseEvent.getScreenY() + _stage.getHeight());
                                _stage.setY(mouseEvent.getScreenY());
                            }
                        } else {
                            if (_stage.getHeight() > minHeight || mouseEventY + startY - _stage.getHeight() > 0) {
                                _stage.setHeight(mouseEventY + startY);
                            }
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                        double minWidth = _stage.getMinWidth() > (BORDER*2) ? _stage.getMinWidth() : (BORDER*2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                            if (_stage.getWidth() > minWidth || mouseEventX < 0) {
                                _stage.setWidth(_stage.getX() - mouseEvent.getScreenX() + _stage.getWidth());
                                _stage.setX(mouseEvent.getScreenX());
                            }
                        } else {
                            if (_stage.getWidth() > minWidth || mouseEventX + startX - _stage.getWidth() > 0) {
                                _stage.setWidth(mouseEventX + startX);
                            }
                        }
                    }
                }
            }
        }
    }
}
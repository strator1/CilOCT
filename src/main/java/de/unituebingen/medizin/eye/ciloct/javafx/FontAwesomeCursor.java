package de.unituebingen.medizin.eye.ciloct.javafx;

import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.geometry.Point2D;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author strasser
 */
public final class FontAwesomeCursor extends ImageCursor {
	private static final SnapshotParameters SNAPSHOT_PARAMETERS = new SnapshotParameters();
	static {
		SNAPSHOT_PARAMETERS.setFill(Color.TRANSPARENT);
	}

	private static interface IPos {
		public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> icon);
	}

	public enum POS implements IPos {
		CENTER {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue() / 2d, glyph.getGlyphSize().doubleValue() / 2d);
			}
		},
		TOP {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue()/2d, 0d);
			}
		},
		LEFT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(0d, glyph.getGlyphSize().doubleValue()/2d);
			}
		},
		RIGHT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue(), glyph.getGlyphSize().doubleValue()/2d);
			}
		},
		TOP_LEFT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(0d, 0d);
			}
		},
		TOP_RIGHT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue(), 0d);
			}
		},
		BOTTOM {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue()/2d, glyph.getGlyphSize().doubleValue());
			}
		},
		BOTTOM_LEFT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(0d, glyph.getGlyphSize().doubleValue());
			}
		},
		BOTTOM_RIGHT {
			@Override
			public Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph) {
				return new Point2D(glyph.getGlyphSize().doubleValue(), glyph.getGlyphSize().doubleValue());
			}
		};

		@Override
		public abstract Point2D getHotspot(final GlyphIcon<? extends GlyphIcons> glyph);
	}

	private FontAwesomeCursor(final Node icon, final double hotspotX, final double hotspotY) {
		super(icon.snapshot(SNAPSHOT_PARAMETERS, null), hotspotX, hotspotY);
	}

	public static FontAwesomeCursorBuilder glyph(final GlyphIcon<? extends GlyphIcons> icon) {
		return new FontAwesomeCursorBuilder(icon);
	}

	public static FontAwesomeCursorBuilder glyph(final FontAwesomeIcon icon) {
		return glyph(new FontAwesomeIconView(icon));
	}

	public static FontAwesomeCursorBuilder glyph(final MaterialDesignIcon icon) {
		return glyph(new MaterialDesignIconView(icon));
	}

	public static FontAwesomeCursorBuilder glyph(final MaterialIcon icon) {
		return glyph(new MaterialIconView(icon));
	}

	public static FontAwesomeCursorBuilder glyph(final OctIcon icon) {
		return glyph(new OctIconView(icon));
	}

	public static final class FontAwesomeCursorBuilder {
		private static final int DEFAULT_SIZE = 32;

		private final GlyphIcon<? extends GlyphIcons> _glyph;

		private double _hotspotX = 0;
		private double _hotspotY = 0;

		private FontAwesomeCursorBuilder(final GlyphIcon<? extends GlyphIcons> icon) {
			new Scene(new Pane(icon));
			_glyph = icon;
			size(DEFAULT_SIZE);
		}

		public FontAwesomeCursor create() {
			return new FontAwesomeCursor(_glyph, _hotspotX, _hotspotY);
		}

		public FontAwesomeCursorBuilder paint(final Paint paint) {
			_glyph.setFill(paint);
			return this;
		}

		public FontAwesomeCursorBuilder size(final int size) {
			_glyph.setGlyphSize(size);
			return this;
		}

		public FontAwesomeCursorBuilder hotspot(final double x, final double y) {
			_hotspotX = x;
			_hotspotY = y;
			return this;
		}

		public FontAwesomeCursorBuilder hotspot(final POS pos) {
			final Point2D p = pos.getHotspot(_glyph);
			_hotspotX = p.getX();
			_hotspotY = p.getY();
			return this;
		}
	}
}
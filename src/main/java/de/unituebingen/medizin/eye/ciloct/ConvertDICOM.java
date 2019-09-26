/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unituebingen.medizin.eye.ciloct;

import com.sun.corba.se.spi.ior.Writeable;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.FileImageInputStream;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;

/**
 *
 * @author strasser
 */
public class ConvertDICOM {
	public static void main(final String[] args) throws Exception {
		File f = new File("C:\\Users\\strasser\\Desktop\\CilOCT\\dicom\\02ATI4C7KT0OBN3SAXE3LF927IOLS4X2ZU.EX.DCM");
		Iterator readers = ImageIO.getImageReadersByFormatName("dicom");
		DicomImageReader reader = (DicomImageReader) readers.next();
		reader.setInput(new FileImageInputStream(f), true);

		final Raster raster = reader.readRaster(0, new ImageReadParam());
		DataBufferUShort db = (DataBufferUShort) raster.getDataBuffer();
		short[] ss = db.getData();

		ByteBuffer bb = ByteBuffer.allocate(ss.length*2);
		for (int i = 0; i < ss.length; i++) {
			bb.putShort(ss[i]);
		}
		bb.rewind();

		for (int j = 0; j < ss.length; j++) {
			ss[j] = (short) ((((bb.get() & 0xFF) << 8) | (bb.get() & 0xFF)) + 32768);
		}

		final ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);
		
		final BufferedImage image = new BufferedImage(colorModel, (WritableRaster) raster, colorModel.isAlphaPremultiplied(), null);
		ImageIO.write(image, "PNG", new File("C:\\Users\\strasser\\Desktop\\CilOCT\\dicom\\test.png"));
	}
}

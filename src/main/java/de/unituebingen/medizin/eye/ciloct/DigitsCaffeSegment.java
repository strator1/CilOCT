package de.unituebingen.medizin.eye.ciloct;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jsoniter.JsonIterator;
import de.unituebingen.medizin.eye.ciloct.exception.GradientException;
import de.unituebingen.medizin.eye.ciloct.vo.SegmentationPoint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author strasser
 */
public class DigitsCaffeSegment implements ICaffeSegment {

	static {
		JSch.setConfig("StrictHostKeyChecking", "no");
	}

	private static final int LOCAL_PORT = 6000;
	private static final int SSH_PORT = 22;
	private static final String QUERY_FORMAT = "http://%s:%d/models/images/generic/infer_one.json?job_id=%s";

	private final String _server;
	private final int _port;
	private final String _user;
	private final String _job;
	private final int _epoch;

	public DigitsCaffeSegment(final String server, final int port, final String user, final String job, final int epoch) {
		_server = server;
		_port = port;
		_user = user;
		_job = job;
		_epoch = epoch;
	}

	@Override
	public Map<GroundTruthPalette, List<SegmentationPoint>> segment(final BufferedImage image) throws GradientException {
		try {
			final Session session = new JSch().getSession("strasser", _server, SSH_PORT);
			session.setPassword("EZ2010biomed!");
			session.connect();
			session.setPortForwardingL(LOCAL_PORT, "localhost", _port);
			final Channel channel = session.openChannel("shell");
			channel.connect();

			try (final CloseableHttpClient client = HttpClientBuilder.create().build(); final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(image, "PNG", baos);
				baos.flush();

				final HttpPost post = new HttpPost(String.format(QUERY_FORMAT, "localhost", LOCAL_PORT, _job));
				post.setEntity(
					MultipartEntityBuilder
						.create()
							.addTextBody("username", _user, ContentType.TEXT_PLAIN)
							.addTextBody("snapshot_epoch", Integer.toString(_epoch), ContentType.TEXT_PLAIN)
							.addBinaryBody("image_file", baos.toByteArray(), ContentType.APPLICATION_OCTET_STREAM, "file")
						.build()
				);

				try (final CloseableHttpResponse response = client.execute(post)) {
					if (response.getStatusLine().getStatusCode() == 200) {
						final HttpEntity entity = response.getEntity();
						if (entity != null) {
							try (final JsonIterator iter = JsonIterator.parse(entity.getContent(), 4096)) {
								iter.readObject();
								iter.readObject();
								iter.readArray();

								final List<Integer> classes = new ArrayList<>(image.getWidth() * image.getHeight());
								final List<Double> values = new ArrayList<>(classes.size());

								int width = 0;
								int height = 0;

								for (int classNumber = 0, index = 0; iter.readArray(); classNumber++, index = 0) {
									for (height = 0; iter.readArray(); height++) {
										for (width = 0; iter.readArray(); width++, index++) {
											final double newValue = iter.readDouble();
											if (classNumber == 0) {
												values.add(newValue);
												classes.add(classNumber);
											} else {
												if (newValue > values.get(index)) {
													values.set(index, newValue);
													classes.set(index, classNumber);
												}
											}
										}
									}
								}

								final int scale = image.getWidth() / width;

								final Map<GroundTruthPalette, List<SegmentationPoint>> segmentation =
									Arrays
										.stream(PALETTE)
										.filter(p -> p != GroundTruthPalette.BACKGROUND)
										.collect(Collectors.toMap(Function.identity(), p -> new ArrayList<SegmentationPoint>()))
								;

								for (int y = 0, i = 0; y < height; y++) {
									for (int x = 0; x < width; x++, i++) {
										final int clazz = classes.get(i);
										if (clazz > 0) {
											segmentation.get(PALETTE[clazz-1]).add(new SegmentationPoint(x * scale, y * scale, PALETTE[clazz-1], values.get(i)));
										}
									}
								}

								return segmentation;
							} finally {
								EntityUtils.consume(entity);
							}
						} else {
							throw new GradientException(new IOException("Empty response"));
						}
					} else {
						throw new GradientException(new IOException(response.getStatusLine().getReasonPhrase()));
					}
				}
			} catch (IOException e) {
				throw new GradientException(e);
			} finally {
				channel.disconnect();
				session.disconnect();
			}
		} catch (JSchException e) {
			throw new GradientException(e);
		}
	}
}
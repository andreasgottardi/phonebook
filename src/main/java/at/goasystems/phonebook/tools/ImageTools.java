package at.goasystems.phonebook.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageTools {

	private static Logger logger = LoggerFactory.getLogger(ImageTools.class);

	public static BufferedImage resizeImage(BufferedImage originalImage) {

		BufferedImage outputImage = new BufferedImage(143, 167, originalImage.getType());
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, 143, 167, null);
		g2d.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(outputImage, "JPEG", baos);
			return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
		} catch (IOException e) {
			logger.error("Error writing image.", e);
		}

		return originalImage;
	}

}

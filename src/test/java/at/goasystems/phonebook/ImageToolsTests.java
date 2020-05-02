package at.goasystems.phonebook;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.goasystems.phonebook.tools.ImageTools;

class ImageToolsTests {

	private static Logger logger = LoggerFactory.getLogger(ImageToolsTests.class);

	@Test
	void test() {

		try {
			BufferedImage bi = ImageTools
					.scaleImageToWidth(ImageIO.read(ImageToolsTests.class.getResourceAsStream("/example1.jpg")), 143);
			assertTrue(bi.getWidth() == 143);
			assertTrue(bi.getHeight() == 143);
		} catch (IOException e) {
			logger.error("Error resizing image.", e);
		}

		try {
			BufferedImage bi = ImageTools
					.scaleImageToWidth(ImageIO.read(ImageToolsTests.class.getResourceAsStream("/example2.jpg")), 143);
			assertTrue(bi.getWidth() == 143);
			assertFalse(bi.getHeight() == 143);
		} catch (IOException e) {
			logger.error("Error resizing image.", e);
		}
	}

	@Test
	void testWriteDefaultUserPicture() {

		File f = new File(System.getProperty("java.io.tmpdir"), "defaultuserpicutre.jpg");

		try (FileOutputStream fos = new FileOutputStream(f)) {
			BufferedImage bi = ImageTools.scaleImageToWidth(
					ImageIO.read(ImageToolsTests.class.getResourceAsStream("/defaultuserpicture.jpg")), 143);
			assertTrue(bi.getWidth() == 143);
			assertTrue(bi.getHeight() == 143);
			ImageIO.write(bi, "jpg", fos);
			fos.flush();
			assertTrue(f.exists());
			assertTrue(f.length() > 0);
		} catch (IOException e) {
			logger.error("Error resizing image.", e);
		}
	}
}

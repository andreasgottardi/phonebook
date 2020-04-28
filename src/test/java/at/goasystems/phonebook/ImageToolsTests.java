package at.goasystems.phonebook;

import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
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
					.resizeImage(ImageIO.read(ImageToolsTests.class.getResourceAsStream("/avatar.jpg")));
			assertTrue(bi.getWidth() == 143);
			assertTrue(bi.getHeight() == 167);
		} catch (IOException e) {
			logger.error("Error resizing image.", e);
		}

	}
}

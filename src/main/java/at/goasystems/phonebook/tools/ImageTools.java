package at.goasystems.phonebook.tools;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageTools {

	private static Logger logger = LoggerFactory.getLogger(ImageTools.class);

	private ImageTools() {

	}

	public static BufferedImage scaleImageToWidth(BufferedImage img, int targetWidth) {

		logger.debug("Scaling image to a width of {}.", targetWidth);

		BufferedImage ret = img;
		int w = img.getWidth();
		int h = img.getHeight();
		int m = 3;

		do {
			if (w > targetWidth) {
				w = (w / m) * (m - 1);
				h = (h / m) * (m - 1);
				if (w < targetWidth) {
					double percent = ((100.0 / w) * targetWidth) / 100.0;
					w = targetWidth;
					/* Recalculate the height. */
					h = (int) (h * percent);
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();
			ret = tmp;
		} while (w != targetWidth);
		return ret;
	}

}

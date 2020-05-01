package at.goasystems.phonebook.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputOutput {

	private InputOutput() {
	}

	private static final Logger logger = LoggerFactory.getLogger(InputOutput.class);

	private static final String DEF_EX_MESSAGE = "Error";
	private static final String FILE_NOT_FOUND = "File {} not found.";

	public static String read(File file) {
		if (file != null && file.exists() && !file.isDirectory()) {
			try {
				return read(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				logger.error(FILE_NOT_FOUND, file.getAbsolutePath(), e);
				return "";
			}
		} else {
			return "";
		}
	}

	public static String read(InputStream is) {

		try (InputStreamReader isr = new InputStreamReader(is);) {
			char[] buffer = new char[1024];
			StringBuilder sb = new StringBuilder();
			int read = isr.read(buffer);
			while (read != -1) {
				sb.append(buffer, 0, read);
				read = isr.read(buffer);
			}
			return sb.toString();
		} catch (IOException e) {
			logger.error(DEF_EX_MESSAGE, e);
			return "";
		}
	}

	public static byte[] readBytes(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int read = is.read(buffer);
			while (read != -1) {
				baos.write(buffer, 0, read);
				read = is.read(buffer);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			logger.error(DEF_EX_MESSAGE, e);
			return baos.toByteArray();
		}
	}

	public static void write(File file, String str) {
		if (file != null && !file.isDirectory()) {
			try {
				logger.debug("Writing '{}' to {}", str, file.getAbsolutePath());
				write(new FileOutputStream(file), str);
			} catch (FileNotFoundException e) {
				logger.error(FILE_NOT_FOUND, file.getAbsolutePath(), e);
			}
		}
	}

	public static void write(FileOutputStream os, String str) {

		try (OutputStreamWriter osr = new OutputStreamWriter(os)) {
			osr.write(str);
			osr.flush();
		} catch (IOException e) {
			logger.error(DEF_EX_MESSAGE, e);
		}
	}

	public static void write(InputStream is, OutputStream os) {
		try {
			byte[] buffer = new byte[1024];
			int read = is.read(buffer);
			while (read != -1) {
				os.write(buffer, 0, read);
				read = is.read(buffer);
			}
		} catch (IOException e) {
			logger.error(DEF_EX_MESSAGE, e);
		}
	}
}

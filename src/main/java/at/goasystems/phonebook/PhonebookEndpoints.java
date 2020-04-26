package at.goasystems.phonebook;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import at.goasystems.phonebook.model.Backend;
import at.goasystems.phonebook.tools.GsonTools;
import at.goasystems.phonebook.tools.InputOutput;

@RestController
public class PhonebookEndpoints {

	private static Logger logger = LoggerFactory.getLogger(PhonebookEndpoints.class);
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String DEFAULT_MESSAGE = "Got request from client {} on port {}.";
	private static final String APP_JSON = "application/json";

	@GetMapping("/")
	public ResponseEntity<String> def(HttpServletRequest request) {
		String startpage = InputOutput.read(PhonebookEndpoints.class.getResourceAsStream("/phonebook.html"));
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(CONTENT_TYPE, "text/html");
		responseHeaders.set(CONTENT_LENGTH, Integer.toString(startpage.getBytes(StandardCharsets.UTF_8).length));
		return ResponseEntity.ok().headers(responseHeaders).body(startpage);
	}

	@GetMapping("/getEntries")
	public ResponseEntity<String> getEntries(HttpServletRequest request) {
		logger.info(DEFAULT_MESSAGE, request.getRemoteAddr(), request.getLocalPort());
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(CONTENT_TYPE, APP_JSON);
		Gson gson = new GsonTools().getDefaultGson();
		String json = gson.toJson(Backend.getInstance().getUsers());
		responseHeaders.set(CONTENT_LENGTH, Integer.toString(json.getBytes(StandardCharsets.UTF_8).length));
		return ResponseEntity.ok().headers(responseHeaders).body(json);
	}
}

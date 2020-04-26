package at.goasystems.phonebook.tools;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonTools {

	private static Logger logger = LoggerFactory.getLogger(GsonTools.class);

	private final String format;

	public GsonTools() {

		// Required format is specified here.
		this.format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	}

	/**
	 * Generates a Gson object with required modifications like GregorianCalender
	 * De/Serializer.
	 * 
	 * @return A Gson object.
	 */
	public Gson getDefaultGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new GregorianCalendarDeserializer());
		gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new GregorianCalendarSerializer());
		return gsonBuilder.create();
	}

	/**
	 * Deserializer class which creates implements JsonDeserializer. Format is
	 * specified by GsonTools.format.
	 * 
	 * @author ago
	 *
	 */
	private class GregorianCalendarDeserializer implements JsonDeserializer<GregorianCalendar> {
		@Override
		public GregorianCalendar deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) {
			String date = element.getAsString();
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(formatter.parse(date));
				return gc;
			} catch (ParseException e) {
				logger.error("Error", e);
				return null;
			}
		}
	}

	/**
	 * Serializer class which creates implements JsonSerializer. Format is specified
	 * by GsonTools.format.
	 * 
	 * @author ago
	 *
	 */
	private class GregorianCalendarSerializer implements JsonSerializer<GregorianCalendar> {
		@Override
		public JsonElement serialize(GregorianCalendar src, Type typeOfSrc, JsonSerializationContext context) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			return new JsonPrimitive(formatter.format(src.getTime()));
		}
	}
}

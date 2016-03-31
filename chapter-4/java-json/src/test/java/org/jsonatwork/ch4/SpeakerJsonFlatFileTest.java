package org.jsonatwork.ch4;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class SpeakerJsonFlatFileTest {

	private static final String SPEAKER_JSON_FILE_NAME = "speaker.json";
	private static final String SPEAKERS_JSON_FILE_NAME = "speakers.json";

	@Test
	public void serializeObject() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String[] tags = {"json", "rest", "api", "oauth"};
			Speaker speaker = new Speaker(39, "Larson Richard", tags, true);

			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			mapper.writeValue(System.out, speaker);
			System.out.println("\n");
			assertTrue(true);
		} catch (JsonGenerationException jge) {
			jge.printStackTrace();
			fail(jge.getMessage());
		} catch (JsonMappingException jme) {
			jme.printStackTrace();
			fail(jme.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}
	}
	
	private File getSpeakerFile(String speakerFileName) throws URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL fileUrl = classLoader.getResource(speakerFileName);
		URI fileUri = new URI(fileUrl.toString());
		File speakerFile = new File(fileUri);

		return speakerFile;
	}
	
	@Test
	public void deSerializeObject() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File speakerFile = getSpeakerFile(SpeakerJsonFlatFileTest.SPEAKER_JSON_FILE_NAME);
			Speaker speaker = mapper.readValue(speakerFile, Speaker.class);
			
			System.out.println("\n" + speaker + "\n");
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail(use.getMessage());
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
			fail(jpe.getMessage());
		} catch (JsonMappingException jme) {
			jme.printStackTrace();
			fail(jme.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}
	}
	
	@Test
	public void deSerializeMultipleObjects() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File speakersFile = getSpeakerFile(SpeakerJsonFlatFileTest.SPEAKERS_JSON_FILE_NAME);
			List<Speaker> speakers = mapper.readValue(speakersFile, 
					TypeFactory.defaultInstance().constructCollectionType(List.class,  
							Speaker.class));

			System.out.println("\n\n\nAll Speakers\n");
			for (Speaker speaker: speakers) {
				System.out.println(speaker);	
			}
			System.out.println("\n");
		} catch (URISyntaxException use) {
			use.printStackTrace();
			fail(use.getMessage());
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
			fail(jpe.getMessage());
		} catch (JsonMappingException jme) {
			jme.printStackTrace();
			fail(jme.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail(ioe.getMessage());
		}
	}
}
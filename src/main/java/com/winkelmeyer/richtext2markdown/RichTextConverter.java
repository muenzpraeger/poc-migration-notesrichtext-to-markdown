/**
 * 
 */

package com.winkelmeyer.richtext2markdown;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.overzealous.remark.Remark;
import com.winkelmeyer.richtext2markdown.model.ViewEntry;

/*
 * Main class for converting NotesRichText (gathered via HTTP from a Notes database) to Markdown. This code serves as a
 * Proof of Concept.
 */
public class RichTextConverter {

	public static Properties	properties;

	public Logger				logger	= LoggerFactory.getLogger(RichTextConverter.class);

	public RichTextConverter() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void runConversion() throws Exception {

		logger.info("Starting conversion process");

		// Validating if property file can be found.
		if (!initProperties()) {
			logger.warn("Properties aren't initialized, exiting conversion");
			return;
		}

		// Build authentication string for basic auth based on configuration in application.properties
		String authString = properties.getProperty("username") + ":" + properties.getProperty("password");
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

		logger.debug("Basic authenticaton for user '{}' has been created.", properties.getProperty("username"));
		logger.trace("Authentication string is '{}'", authStringEnc);

		logger.info("Creating URL string for reading all docs in the view");
		// Getting the first 1000 documents from the given server/database/view combination
		String stringUrlAllEntries = "http://" + properties.getProperty("server") + "/" + properties.getProperty("database") + "/" + properties.getProperty("view") + "?ReadViewEntries=&Outputformat=json&count=1000";

		logger.debug("Target URL for all documents is '{}'", stringUrlAllEntries);

		// Requesting the view entries from the HTTP URL of the given Notes database
		URL urlAllEntries = new URL(stringUrlAllEntries);
		URLConnection urlConnectionAllEntries = urlAllEntries.openConnection();
		urlConnectionAllEntries.setRequestProperty("Authorization", "Basic " + authStringEnc);

		logger.debug("URL connection object created");

		HashMap<String, ViewEntry> mapEntries = Maps.newHashMap();

		try (InputStream is = urlConnectionAllEntries.getInputStream()) {
			logger.info("Data read from URL");
			JsonElement elementRoot = new JsonParser().parse(new InputStreamReader(is));
			JsonObject jsonView = elementRoot.getAsJsonObject();
			JsonArray jsonViewEntries = jsonView.getAsJsonArray("viewentry");
			Gson gson = new GsonBuilder().create();
			logger.debug("Creating Java objects from view data");
			ViewEntry[] entriesTemp = gson.fromJson(jsonViewEntries, ViewEntry[].class);
			logger.info("Java objects from view data have been created");
			for (ViewEntry entry : entriesTemp) {
				if (entry.getUnid() != null) { // check for null so that we only get real documents and no categories
					mapEntries.put(entry.getUnid(), entry);
				}
			}
		}

		logger.info("Reading details from view");

		Iterator it = mapEntries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ViewEntry> mapEntry = (Entry<String, ViewEntry>) it.next();
			ViewEntry viewEntry = mapEntry.getValue();

			logger.trace("Adding metadata for document with UNID '{}'", viewEntry.getUnid());

			ArrayList entryDataArray = (ArrayList) viewEntry.getEntrydata();

			if (entryDataArray.size() > 1) {

				for (Object entryDataDetails : entryDataArray) {
					LinkedTreeMap detail = (LinkedTreeMap) entryDataDetails;

					switch ((String) detail.get("@name")) {
						case "help_number":
							viewEntry.setNumber((String) ((LinkedTreeMap) detail.get("text")).get("0"));
							logger.trace("Entry number is '{}'", viewEntry.getNumber());
							break;
						case "help_title":
							viewEntry.setTitle((String) ((LinkedTreeMap) detail.get("text")).get("0"));
							logger.trace("Entry title is '{}'", viewEntry.getNumber());
							break;
					}
				}
			}
			mapEntries.put(viewEntry.getUnid(), viewEntry);
		}

		logger.info("Reading content from Notes documents");

		it = mapEntries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ViewEntry> mapEntry = (Entry<String, ViewEntry>) it.next();
			ViewEntry viewEntry = mapEntry.getValue();

			String stringUrlSingleEntry = "http://" + properties.getProperty("server") + "/" + properties.getProperty("database") + "/" + properties.getProperty("view") + "/" + viewEntry.getUnid() + "/" + properties.getProperty("fieldBody") + "?OpenField";

			logger.trace("Target URL is '{}'", stringUrlSingleEntry);

			URL urlSingleEntry = new URL(stringUrlSingleEntry);
			URLConnection urlConnectionSingleEntry = urlSingleEntry.openConnection();
			urlConnectionSingleEntry.setRequestProperty("Authorization", "Basic " + authStringEnc);

			try (InputStream is = urlConnectionSingleEntry.getInputStream()) {

				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);

				viewEntry.setBody(writer.toString());

				logger.trace("HTML content added to object");

				IOUtils.closeQuietly(is);

				IOUtils.close(urlConnectionSingleEntry);
			}

			mapEntries.put(viewEntry.getUnid(), viewEntry);
		}

		logger.info("Starting file creation");

		// HOORAY - you got now all data, now write it to disk
		Remark remark = new Remark();
		String fileName = "";
		PrintWriter out;

		it = mapEntries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ViewEntry> mapEntry = (Entry<String, ViewEntry>) it.next();
			ViewEntry viewEntry = mapEntry.getValue();

			fileName = CharMatcher.WHITESPACE.replaceFrom(viewEntry.getTitle(), '_');
			fileName = CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(fileName);
			fileName = CharMatcher.JAVA_DIGIT.retainFrom(viewEntry.getNumber()) + "_" + fileName;

			out = new PrintWriter(properties.getProperty("pathSave") + "/" + fileName + ".md");
			out.write(remark.convertFragment(viewEntry.getBody()));
			IOUtils.closeQuietly(out);
		}

		logger.info("Files successfully created in path '{}'", properties.getProperty("pathSave"));

	}

	private boolean initProperties() {

		boolean areLoaded = false;

		try {
			InputStream is = getClass().getResourceAsStream("/application.properties");
			properties = new Properties();
			properties.load(is);
			areLoaded = true;
		} catch (Exception e) {
			logger.error("Error on initiating properties", e);
		}

		return areLoaded;
	}

}

package org.scaffoldeditor.editormc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.minecraft.client.MinecraftClient;

/**
 * Handles the Scaffold Editor config.
 * @author Igrium
 */
public final class Config {
	public static final String FILENAME = "config/scaffold.xml";
	private static Path gameDir;
	
	private static Document document;
	
	private static DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
	private static XPathFactory xPathFactory = XPathFactory.newInstance();
	public static Transformer transformer;
	public static DocumentBuilder builder;
	public static XPath xPath = xPathFactory.newXPath();
	
	private static List<Runnable> saveListeners = new ArrayList<Runnable>();
	
	public static void init() {
		LogManager.getLogger().info("Initializing Scaffold config.");
		try {
			builder = documentFactory.newDocumentBuilder();
			transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "true");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			load();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load the config file. If the file is non-existant, create it.
	 * @return Loaded config document.
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document load() throws SAXException, IOException {
		MinecraftClient client = MinecraftClient.getInstance();
		gameDir = client.runDirectory.toPath();
		File configFile = getFile();
		
		Document defaultConfig = builder.parse(Config.class.getResourceAsStream("/scaffold.default_config.xml"));
		
		if (!configFile.isFile()) {
			document = defaultConfig;
			save(false);
			return document;
		}
		
		document = builder.parse(configFile);
		
		// Check all values are there.
		Element defElement = defaultConfig.getDocumentElement();
		List<String> items = new ArrayList<>();
		scanElement(defElement, "", items);
		
		for (String item : items) {
			if (getElement(item) == null) {
				try {
					Element def = (Element) xPath.evaluate(item, defaultConfig, XPathConstants.NODE);
					setValue(item, def.getTagName(), def.getAttribute("value"));
				} catch (XPathExpressionException e) {
					throw new AssertionError("Unable to load config", e);
				}
			}
		}
		
		return document;
	}
	
	private static void scanElement(Element element, String baseString, List<String> childStrings) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childElement = (Element) child;
			String id = childElement.getAttribute("id");
			childStrings.add(baseString + id);
			scanElement(childElement, baseString + id + '.', childStrings);
		}
	}
	
	public static void save() throws IOException {
		save(true);
	}
	
	public static void save(boolean callListeners) throws IOException {
		DOMSource source = new DOMSource(document);
		
		FileWriter writer = new FileWriter(getFile());
		StreamResult result = new StreamResult(writer);
		
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		writer.close();
		
		if (callListeners) {
			for (Runnable r : saveListeners) {
				r.run();
			}
		}
	}
	
	public static Document getDocument() {
		return document;
	}
	
	public static Element getConfig() {
		return (Element) document.getElementsByTagName("Config").item(0);
	}
	
	public static File getFile() {
		return gameDir.resolve(FILENAME).toFile();
	}
	
	
	public static Element getElement(String path) {
		String[] splitPath = path.split("\\.");
		return getElement(splitPath);
		
	}
	
	public static Element getElement(String[] path) {
		String expression = generateXPathExpression(path);
		
		try {
			Object obj = xPath.evaluate(expression, document, XPathConstants.NODE);
			if (obj == null) return null;
			return (Element) obj;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String generateXPathExpression(String[] splitPath) {
		String expression = "/Config";
		for (String s : splitPath) {
			expression = expression+"/"+id(s);
		}
		return expression;
	}
	
	private static String id (String id) {
		return "*[@id='"+id+"']";
	}
	
	/**
	 * Get a value from the config.
	 * @param path Path to the value. Format = <code>[page].[group].[value]</code>
	 * @return The value tag of the entry. Null if entry doesn't exist.
	 */
	public static String getValue(String path) {
		Element element = getElement(path);
		if (element != null) {
			return element.getAttribute("value");
		} else {
			LogManager.getLogger().warn("No config value: "+path);
			return null;
		}
	}
	
	/**
	 * Set a config value.
	 * @param path Path to the value.
	 * @param type Class of the value. (KeyBind, Integer, etc)
	 * @param value Value to set.
	 */
	public static void setValue(String path, String type, String value) {
		String[] splitPath = path.split("\\.");
		
		Element element = getElement(splitPath);
		if (element != null) {
			element.setAttribute("value", value);
		} else {
			Element parent = getElement(Arrays.copyOfRange(splitPath, 0, splitPath.length - 1));
			Element newElement = document.createElement(type);
			newElement.setAttribute("value", value);
			newElement.setAttribute("id", splitPath[splitPath.length-1]);
			parent.appendChild(newElement);
		}
		
	}
	
	protected static void createGroup(String page, String id) {
		
	}
	
	/**
	 * Called when the config is saved. Use this to update dependent methods.
	 * @param listener On save listener.
	 */
	public static void onSave(Runnable listener) {
		saveListeners.add(listener);
	}
}

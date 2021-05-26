package org.scaffoldeditor.editormc.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

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

import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.BlockEntity;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.OperationManager;
import org.scaffoldeditor.scaffold.serialization.EntitySerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ScaffoldClipboard {
	final Clipboard clipboard = Clipboard.getSystemClipboard();
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private TransformerFactory tFactory;
	private Transformer transformer;
	
	private static ScaffoldClipboard instance;
	public static ScaffoldClipboard getInstance() {
		if (instance == null) {
			instance = new ScaffoldClipboard();
		}
		return instance;
	}
	
	private ScaffoldClipboard() {
		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.tFactory = TransformerFactory.newInstance();
		try {
			this.dBuilder = dbFactory.newDocumentBuilder();
			this.transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
			throw new AssertionError("Unable to create scaffold clipboard.", e);
		}
	}
	
	public String serializeEntities(Set<Entity> entities) {	
		try {
			Document doc = dBuilder.newDocument();
			Element root = doc.createElement("entities");
			for (Entity entity : entities) {
				Element element = new EntitySerializer(entity).serialize(doc);
				root.appendChild(element);
			}
			doc.appendChild(root);
			
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public void copyEntities(Set<Entity> entities) {
		System.out.println("Copying entities");
		ClipboardContent content = new ClipboardContent();
		content.putString(serializeEntities(entities));
		clipboard.setContent(content);
	}

	public void deserializeEntities(String in, Level level) {
		Set<Entity> entities = new HashSet<>();
		try {
			Document doc = dBuilder.parse(new InputSource(new StringReader(in)));
			NodeList children = doc.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) child;
					entities.add(EntitySerializer.deserialize(element, level));
				}
			}
			level.updateEntityStack();

		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		for (Entity ent : entities) {
			if (ent instanceof BlockEntity) {
				level.dirtySections.addAll(((BlockEntity) ent).getOverlappingSections(level.getBlockWorld()));
			}
		}
	}
	
	public void pasteEntities(OperationManager operationManager, Level level) {
		if (clipboard.hasString()) {
			deserializeEntities(clipboard.getString(), level);
		}
	}
}

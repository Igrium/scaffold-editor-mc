package org.scaffoldeditor.editormc.controls;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.EditorCameraEntity;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class ViewportControls {
	protected ScaffoldEditor editor;
	protected ScaffoldUI ui;
	/**
	 * Key: Keypress, Value: Binding
	 */
	public final Map<String, String> bindings = new HashMap<String, String>();
	public final Map<String, Boolean> bindingsPressed = new HashMap<String, Boolean>();
	private MinecraftClient client = MinecraftClient.getInstance();
	protected CameraController camera;
	
	public ViewportControls() {
		Config.onSave(() -> initControls());
	}
	
	public void init(ScaffoldUI ui) {
		System.out.println("Initializig Scaffold viewport controls.");
		this.ui = ui;
		this.editor = ui.getEditor();
		Scene scene = ui.getStage().getScene();
		
		scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			String name = e.getCode().getName();
			if (bindings.containsKey(name)) {
				bindingsPressed.put(bindings.get(name), true);
				onUpdate();
			}
		});
		
		scene.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			String name = e.getCode().getName();
			if (bindings.containsKey(name)) {
				bindingsPressed.put(bindings.get(name), false);
				onUpdate();
			}
		});
		
		initControls();
	}
	
	public void onUpdate() {
		// TESTING ONLY
		System.out.println("Keyboard Update!");

		// TODO: Make this directly call the client player entity.
//		client.options.keyForward.setPressed(bindingsPressed.get("forward"));
//		client.options.keyBack.setPressed(bindingsPressed.get("backward"));
//		client.options.keyLeft.setPressed(bindingsPressed.get("left"));
//		client.options.keyRight.setPressed(bindingsPressed.get("right"));
//		client.options.keySneak.setPressed(bindingsPressed.get("down"));
//		client.options.keyJump.setPressed(bindingsPressed.get("up"));
		
		double forwardBackward = 0;
		double leftRight = 0;
		double upDown = 0;
		
		if (bindingsPressed.get("forward")) forwardBackward += 1;
		if (bindingsPressed.get("backward")) forwardBackward -= 1;
		if (bindingsPressed.get("left")) leftRight -= 1;
		if (bindingsPressed.get("right")) leftRight += 1;
		if (bindingsPressed.get("up")) upDown += 1;
		if (bindingsPressed.get("down")) upDown -= 1;
		
		if (camera != null) {
			camera.setFrontBack(forwardBackward);
			camera.setLeftRight(leftRight);
			camera.setUpDown(upDown);
		}
	}
	
	protected void initControls() {
		NodeList nodes;
		try {
			nodes = (NodeList) Config.xPath.evaluate("Config/*[@id='viewport']/*[@id='controls']/KeyBinding", Config.getDocument(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return;
		}
		
		bindings.clear();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			bindings.put(element.getAttribute("value"), element.getAttribute("id"));
		}
		
		for (String b : bindings.values()) {
			bindingsPressed.put(b, false);
		}
		
		Entity cameraEntity = client.getCameraEntity();
		if (!(cameraEntity instanceof CameraController)) {
			System.out.println("ERROR: Camera entity is not a camera controller! Viewport controls will not work!");
			return;
		}
		
		camera = (EditorCameraEntity) client.getCameraEntity();
	}
}

package org.scaffoldeditor.editormc.controls;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.engine.EditorCameraEntity;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/**
 * Handles manipulation of the 3D viewport.
 * @author Igrium
 */
public class ViewportControls {
	/**
	 * The amount of time it takes for a right click to activate the viewport controls.
	 */
	public static final Duration HOLD_TIME = new Duration(100);
	
	protected ScaffoldEditor editor;
	protected ScaffoldUI ui;
	/**
	 * Key: Keypress, Value: Binding
	 */
	public final Map<String, String> bindings = new HashMap<String, String>();
	public final Map<String, Boolean> bindingsPressed = new HashMap<String, Boolean>();
	private MinecraftClient client = MinecraftClient.getInstance();
	protected CameraController camera;
	
	protected boolean enableControls = false;
	
	protected boolean captureMouse = false;
	private boolean ignoreMouse = false;
	private double mouseX = 0;
	private double mouseY = 0;
	
	private Robot mouseMover;

	
	public ViewportControls() {
		Config.onSave(() -> initControls());
	}
	
	public void init(ScaffoldUI ui) {
		LogManager.getLogger().info("Initializig Scaffold viewport controls.");
		this.ui = ui;
		this.editor = ui.getEditor();
		Scene scene = ui.getStage().getScene();
		
		try {
			mouseMover = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
		
		scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, new MouseListener());
		scene.addEventHandler(MouseEvent.MOUSE_MOVED, new MouseListener());
		
		
		Entity cameraEntity = client.getCameraEntity();
		if (!(cameraEntity instanceof CameraController)) {
			LogManager.getLogger().error("Camera entity is not a camera controller! Viewport controls will not work!");
			return;
		}
		
		camera = (EditorCameraEntity) client.getCameraEntity();
		
		initControls();
	}
	
	public void setEnableControls(boolean enable) {
		enableControls = enable;
		setCaptureMouse(enable);
		
		if (!enable && camera != null) {
			camera.setFrontBack(0);
			camera.setLeftRight(0);
			camera.setUpDown(0);
		}
	}
	
	public boolean getEnableControls() { return enableControls; }
	
	public boolean isBindingPressed(String name) {
		Boolean value = bindingsPressed.get(name);
		return (value != null && value);
	}
	

	protected void onUpdate() {
//		if (isBindingPressed("grab") && editor.getSelectedEntities().size() > 0) {
//			ui.getViewport().beginTransformation("translate", editor.getSelectedEntities().iterator().next());
//		} else if (isBindingPressed("cancel_transformation")) ui.getViewport().cancelTransformation();
//		else if (isBindingPressed("apply_transformation")) ui.getViewport().applyTransformation();
		
		if (enableControls) {
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
		
	}
	
	protected void handleMoveMouse(double dx, double dy) {
		if (camera != null) {
			camera.addRot(dx, dy);
		}
	}
	
	public void setCaptureMouse(boolean captureMouse) {
		if (captureMouse) { 
			if (this.captureMouse) return;
			ignoreMouse = true;
			ui.getStage().getScene().setCursor(Cursor.NONE);
		} else {
			ui.getStage().getScene().setCursor(Cursor.DEFAULT);
		}
		this.captureMouse = captureMouse;
	}
	
	public boolean getCaptureMouse() {
		return captureMouse;
	}
	
	protected class MouseListener implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (!captureMouse) {
				return;
			}
			ui.getViewport().getParent().requestFocus();
		    if(ignoreMouse) {
		    	ignoreMouse = false;
		    	mouseX = event.getSceneX();
			    mouseY = event.getSceneY();
		        return;
		    }
		    Stage stage = ui.getStage();
		    
		    double centerX = stage.getX() + stage.getWidth() / 2;
		    double centerY = stage.getY() + stage.getHeight() / 2;
		    		    
		    double dx = (event.getSceneX() - mouseX);
		    double dy = (event.getSceneY() - mouseY);
		    mouseX = event.getSceneX();
		    mouseY = event.getSceneY();
		    		    
		    ignoreMouse = true;
		    moveCursor((int) centerX, (int) centerY);
		    handleMoveMouse(dx, dy);
		}
		
//		@Override
//		public void handle(MouseEvent event) {
//			if (ignoreMouse || !captureMouse) {
//				ignoreMouse = false;
//				return;
//			}
//			Stage stage = ui.getStage();
//			
//		    moveCursor((int) (stage.getX() + (stage.getWidth() / 2.0)), (int) (stage.getY() + (stage.getHeight() / 2.0)));
//		
//			ignoreMouse = true;
//			
//			double dx = mouseX - event.getSceneX();
//			double dy = mouseY - event.getSceneY();
//			mouseX = event.getSceneX();
//			mouseY = event.getSceneY();
//		    
//		    
//		    handleMoveMouse(dx, dy);
//		}
		
	}
	
	/**
	 * Move the mouse to the specific screen position
	 * 
	 * @param x
	 * @param y
	 */
	public void moveCursor(int screenX, int screenY) {
		mouseMover.mouseMove(screenX, screenY);
	}
}
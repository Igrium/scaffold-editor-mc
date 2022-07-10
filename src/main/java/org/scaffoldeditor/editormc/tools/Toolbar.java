package org.scaffoldeditor.editormc.tools;

import java.util.HashMap;
import java.util.Map;
import org.scaffoldeditor.editormc.tools.Toolbar.ToolEntry;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Toolbar {
	
	class ToolEntry {
		public final ViewportTool tool;
		public final Button image;
		
		public ToolEntry(ViewportTool tool) {
			this.tool = tool;
			this.image = new Button();
			image.setGraphic(new ImageView(tool.getIcon()));
			image.setOnAction(e -> {
				setTool(tool);
			});
			Tooltip t = new Tooltip(tool.getName());
			Tooltip.install(image, t);
		}
	}
	
	private final Map<String, ToolEntry> entries = new HashMap<>();
	
	/**
	 * The root node of the toolbar.
	 */
	public final HBox root = new HBox();
	
	public Toolbar() {
		root.setSpacing(5);
		HBox.setHgrow(root, Priority.ALWAYS);
		BorderPane.setMargin(root, new Insets(3, 20, 3, 20));

	}
	
	/**
	 * Add a tool to the toolbar.
	 * @param tool Tool to add.
	 * @param registryName Registry name.
	 */
	public void addTool(ViewportTool tool, String registryName) {
		ToolEntry entry = new ToolEntry(tool);
		entries.put(registryName, entry);
		root.getChildren().add(entry.image);
	}
	
	private void setTool(ViewportTool tool) {
		ScaffoldUI.getInstance().getViewport().setActiveTool(tool);
	}
	
	/**
	 * Set the active tool.
	 * @param registryName Registry name of tool.
	 */
	public void setTool(String registryName) {
		setTool(entries.get(registryName).tool);
	}
	
	public void clear() {
		entries.clear();
		root.getChildren().clear();
	}
}

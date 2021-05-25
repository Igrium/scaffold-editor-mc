package org.scaffoldeditor.editormc.tools;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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
	
	/**
	 * The root node of the toolbar.
	 */
	public final HBox root = new HBox();
	
	public Toolbar() {
		root.setSpacing(5);
		BorderPane.setMargin(root, new Insets(3, 20, 3, 20));

	}
	
	public void addTool(ViewportTool tool) {
		ToolEntry entry = new ToolEntry(tool);
		root.getChildren().add(entry.image);
	}
	
	private void setTool(ViewportTool tool) {
		ScaffoldUI.getInstance().getViewport().setActiveTool(tool);
	}
}

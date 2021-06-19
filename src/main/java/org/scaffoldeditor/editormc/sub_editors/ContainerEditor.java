package org.scaffoldeditor.editormc.sub_editors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.scaffoldeditor.editormc.sub_editors.nbt.NBTEditorController;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class ContainerEditor {
	private static class Cell {
		public final StackPane stack;
		public final ImageView image;
//		public final int id;
		
		public Cell(int id) {
			stack = new StackPane();
			image = new ImageView();
			image.getStyleClass().add("inventory_slot");
			stack.getChildren().add(image);
			image.setFitWidth(54);
			image.setFitHeight(54);
//			this.id = id;
		}
	}
	
	@FXML
	private GridPane grid;
	@FXML
	private BorderPane root;
	
	private List<Cell> cells = new ArrayList<>();
	private ListTag<CompoundTag> content;
	private EventDispatcher<ListTag<CompoundTag>> finishedDispatcher = new EventDispatcher<>();
	private Stage stage;
	
	@FXML
	private void initialize() {
		for (Node node : root.getChildren()) {
			if (node.getStyleClass().contains("button-pane")) {
				node.setOnMouseEntered(e -> {
					node.getStyleClass().add("button-selected");
				});
				node.setOnMouseExited(e -> {
					node.getStyleClass().remove("button-selected");
				});
			}
		}
	}
	
	public void init(int numSlotsX, int numSlotsY) {
		int i = 0;
		for (int y = 0; y < numSlotsY; y++) {
			for (int x = 0; x < numSlotsX; x++) {
				Cell cell = new Cell(i);
				grid.add(cell.stack, x, y);
				cells.add(cell);
				i++;
			}
		}
	}
	
	public void setContent(ListTag<CompoundTag> content) {
		this.content = content;
	}
	
	@FXML
	public void browseNBT() {
		if (content == null) return;
		
		NBTEditorController editor = NBTEditorController.openPopup(grid.getScene().getWindow());
		editor.loadNBT(new NamedTag("Items", content));
	}
	
	@FXML
	public void apply() {
		finishedDispatcher.fire(content);
		stage.close();
	}
	
	public void onFinished(EventListener<ListTag<CompoundTag>> listener) {
		finishedDispatcher.addListener(listener);
	}
	
	public static ContainerEditor open(Window parent, int numSlotsX, int numSlotsY) throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/container_editor.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.setTitle("Console");
		stage.setScene(scene);
		ContainerEditor controller = loader.getController();
		controller.stage = stage;
		controller.init(numSlotsX, numSlotsY);
		
		stage.show();
		
		return controller;
	} 
	
}

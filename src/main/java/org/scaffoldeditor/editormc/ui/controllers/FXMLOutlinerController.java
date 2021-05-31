package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;
import org.scaffoldeditor.scaffold.operation.ModifyStackOperation;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class FXMLOutlinerController {
	@FXML
	private ListView<String> entityList;
	private ScaffoldEditor editor;
	
	private Parent root;
	private boolean supressSelectionUpdate = false;
	
	@FXML
	private void initialize() {
		entityList.setCellFactory(param -> new EntityCell());
		entityList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		entityList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> c) {
				if (!supressSelectionUpdate) {
					for (int i = 0; i < entityList.getItems().size(); i++) {
						if (entityList.getItems().get(i) != null) {
							if (entityList.getSelectionModel().isSelected(i)) {
								editor.getSelectedEntities().add(editor.getLevel().getEntity(entityList.getItems().get(i)));
							} else {
								editor.getSelectedEntities().remove(editor.getLevel().getEntity(entityList.getItems().get(i)));
							}
						}
					}
					editor.updateSelection();
				}
			}
		});
	}
	
	public void init(ScaffoldEditor editor) {
		this.editor = editor;
		editor.onUpdateSelection(e -> {
			Platform.runLater(() -> {
				supressSelectionUpdate = true;
				for (int i = 0; i < entityList.getItems().size(); i++) {
					Entity ent = editor.getLevel().getEntity(entityList.getItems().get(i));
					if (e.newSelection.contains(ent)) {
						entityList.getSelectionModel().select(i);
					} else {
						entityList.getSelectionModel().clearSelection(i);
					}
				}
				supressSelectionUpdate = false;
			});
		});
	}
	
	private class EntityCell extends ListCell<String> {
		public EntityCell() {
			EntityCell thisCell = this;
			setOnDragDetected(event -> {
				if (getItem() == null) {
					return;
				}			
				Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem());
				dragboard.setContent(content);
				event.consume();
			});
			
			setOnDragOver(event -> {
				if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.MOVE);
				}
				event.consume();
			});
			
			setOnDragEntered(event -> {
				if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
					setOpacity(0.3);
				}
			});
			
			setOnDragExited(event -> {
				if (event.getGestureSource() != thisCell && event.getDragboard().hasString()) {
					setOpacity(1);
				}
			});
			
			setOnDragDropped(event -> {
				System.out.println("Drag detected "+getItem());
				if (getItem() == null) return;
				
				Dragboard db = event.getDragboard();
				boolean success = false;
				
				if (db.hasString()) {
					ObservableList<String> items = getListView().getItems();
					int draggedIndex = items.indexOf(db.getString());
					int thisIndex = items.indexOf(getItem());
					
					items.set(draggedIndex, getItem());
					items.set(thisIndex, db.getString());
					
					success = true;
				}
				event.setDropCompleted(success);
				event.consume();
			});
			
			setOnDragDone(event -> {
				handleUpdate();
				event.consume();
			});
			
			addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getClickCount() > 1 && getItem() != null) {
					editor.getUI().openEntityEditor(editor.getLevel().getEntity(getItem()));
					event.consume();
				}
			});
		}
		
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty || item == null) {
				setGraphic(null);
			}
			setContextMenu(createContextMenu(item));
			setGraphic(new Label(item));
		}
	}
	
	protected ContextMenu createContextMenu(String entName) {
		MenuItem edit = new MenuItem("Edit");
		edit.setOnAction(e -> {
			editor.getUI().openEntityEditor(editor.getLevel().getEntity(entName));
		});
		
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e -> {
			editor.getLevel().getOperationManager().execute(new DeleteEntityOperation(editor.getLevel(),
					Collections.singleton(editor.getLevel().getEntity(entName))));
		});

		return new ContextMenu(edit, delete);
	}
	
	public Parent getRoot() {
		return root;
	}
	
	public void handleUpdate() {
		editor.getLevel().getOperationManager().execute(new ModifyStackOperation(editor.getLevel(), entityList.getItems()));
	}
	
	public void setEntities(List<String> stack) {
		entityList.getItems().clear();
		entityList.getItems().addAll(stack);
	}
	
	public static FXMLOutlinerController load(ScaffoldUI parent) throws IOException {
		FXMLLoader loader = new FXMLLoader(parent.getClass().getResource("/assets/scaffold/ui/outliner.fxml"));
		Parent root = loader.load();
		FXMLOutlinerController controller = loader.getController();
		controller.root = root;
		controller.init(parent.getEditor());
		return controller;
	}
}

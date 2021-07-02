package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.level.stack.StackItem.ItemType;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class Outliner {
	private TreeView<StackItem> entityList;
	private ScaffoldEditor editor;
	
	public Outliner() {
		entityList = new TreeView<>();
		entityList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		entityList.setCellFactory(tree -> {
			return new EntryCell();
		});
	}
	
	private boolean supressSelectionUpdate = false;
	
	public class EntryCell extends TreeCell<StackItem> {
		@Override
		protected void updateItem(StackItem item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
				setContextMenu(null);
			} else {
				if (item.getType() == ItemType.GROUP) {
					StackGroup group = item.getGroup();
					setText(group.getName()+": "+group.items.size()+" items");
					setContextMenu(null);
				} else {
					setText(item.getEntity().getName());
					setContextMenu(createContextMenu(item.getEntity()));
				}
			}
		}
	}
	

	
	public void init(ScaffoldEditor editor) {
		this.editor = editor;
		editor.onUpdateSelection(e -> {
			Platform.runLater(() -> {
				supressSelectionUpdate = true;
				entityList.getSelectionModel().clearSelection();
				
				iterate(entityList.getRoot(), item -> {
					if (item.getValue().getType() == ItemType.GROUP) {
						boolean selected = true;
						for (Entity ent : item.getValue().getGroup()) {
							if (!editor.getSelectedEntities().contains(ent)) {
								selected = false;
								break;
							}
						}
						if (selected) entityList.getSelectionModel().select(item);
					} else {
						if (editor.getSelectedEntities().contains(item.getValue().getEntity())) {
							entityList.getSelectionModel().select(item);
						}
					}
				});
				
				supressSelectionUpdate = false;
			});
		});
	}
	
	public static TreeItem<StackItem> load(StackItem item) {
		TreeItem<StackItem> root = new TreeItem<>(item);
		if (item.getType() == ItemType.GROUP) {
			for (StackItem child : item.getGroup().items) {
				root.getChildren().add(load(child));
			}
		}
		
		return root;
	}
	
	protected <T> void iterate(TreeItem<T> root, Consumer<TreeItem<T>> run) {
		run.accept(root);
		for (TreeItem<T> item : root.getChildren()) {
			run.accept(item);
			iterate(item, run);
		}
	}
	
	protected ContextMenu createContextMenu(Entity entity) {
		MenuItem edit = new MenuItem("Edit");
		edit.setOnAction(e -> {
			editor.getUI().openEntityEditor(entity);
		});
		
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e -> {
			editor.getLevel().getOperationManager().execute(new DeleteEntityOperation(editor.getLevel(),
					Collections.singleton(entity)));
		});

		return new ContextMenu(edit, delete);
	}
	
	public TreeView<StackItem> getRoot() {
		return entityList;
	}
	
	public void handleUpdate() {
//		editor.getLevel().getOperationManager().execute(new ModifyStackOperation(editor.getLevel(), entityList.getItems()));
	}
	
	public void setEntities(StackGroup entities) {
		StackItem root = new StackItem(entities);
		entityList.setRoot(load(root));
	}
	
	public static Outliner load(ScaffoldUI parent) throws IOException {
		Outliner outliner = new Outliner();
		outliner.init(parent.getEditor());
		return outliner;
	}
}

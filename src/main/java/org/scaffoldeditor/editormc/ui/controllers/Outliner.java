package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.scaffoldeditor.editormc.ScaffoldEditor;
import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.stack.StackGroup;
import org.scaffoldeditor.scaffold.level.stack.StackItem;
import org.scaffoldeditor.scaffold.level.stack.StackItem.ItemType;
import org.scaffoldeditor.scaffold.operation.DeleteEntityOperation;
import org.scaffoldeditor.scaffold.operation.DeleteGroupOperation;
import org.scaffoldeditor.scaffold.operation.ModifyStackOperation;
import org.scaffoldeditor.scaffold.util.ClipboardManager;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class Outliner {
	private TreeView<StackItem> entityList;
	private ScaffoldEditor editor;
	
	public Outliner() {
		entityList = new TreeView<>();
		entityList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		entityList.setCellFactory(tree -> {
			return new EntryCell();
		});
		
		entityList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<StackItem>>() {

			@Override
			public void onChanged(Change<? extends TreeItem<StackItem>> c) {
				updateSelection();
			}
			
		});
	}
	
	private boolean supressSelectionUpdate = false;
	private boolean isUpdatingSelection = false;
	private TreeItem<StackItem> draggedItem;
	private TreeCell<?> dropZone;
	
	public class EntryCell extends TreeCell<StackItem> {
		public EntryCell() {
			this.setOnMouseClicked(event -> {
				if (getItem() != null && event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
					if (getItem().getType() == ItemType.ENTITY) {
						editor.getUI().openEntityEditor(getItem().getEntity());
					}
				}
			});
			
			this.setOnDragDetected(event -> dragDetected(event, this, getTreeView()));
			this.setOnDragOver(event -> dragOver(event, this, getTreeView()));
			this.setOnDragDropped(event -> drop(event, this, getTreeView()));
			this.setOnDragDone(event -> clearDropZone());
		}
		
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
					setContextMenu(createGroupContextMenu(getTreeItem()));
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
			if (isUpdatingSelection) return;
			
			Platform.runLater(() -> {
				supressSelectionUpdate = true;
				entityList.getSelectionModel().clearSelection();
				
				iterate(entityList.getRoot(), item -> {
					
					if (item.getValue().getType() == ItemType.ENTITY
							&& editor.getSelectedEntities().contains(item.getValue().getEntity())) {
						entityList.getSelectionModel().select(item);
					}
				});
				
				supressSelectionUpdate = false;
			});
		});
	}
	
	private static TreeItem<StackItem> load(StackItem item, TreeItem<StackItem> original) {
		// Attempt to re-use tree items when possible.
		TreeItem<StackItem> root;
		if (original != null && item.getType() == original.getValue().getType()) {
			root = original;
			root.setValue(item);
		} else {
			root = new TreeItem<>(item);
		}
		
		if (item.getType() == ItemType.GROUP) {
			Map<StackItem, TreeItem<StackItem>> map = new HashMap<>();
			
			for (StackItem it : item.getGroup().items) {
				if (original != null) {
					map.put(it, load(it, getItem(original.getChildren(), it)));
				} else {
					map.put(it, load(it, null));
				}
			}
			
			root.getChildren().clear();
			for (StackItem it : item.getGroup().items) {
				root.getChildren().add(map.get(it));
			}
		}	
		
		return root;
	}
	
	private static boolean isSimilar(StackItem item1, StackItem item2) {
		return (item1.getType() == item2.getType() && item1.getName() == item2.getName());
	}
	
	private static TreeItem<StackItem> getItem(Collection<TreeItem<StackItem>> collection, StackItem item) {
		for (TreeItem<StackItem> i : collection) {
			if (isSimilar(i.getValue(), item)) return i;
		}
		return null;
	}
	
	private void dragDetected(MouseEvent event, TreeCell<StackItem> treeCell, TreeView<StackItem> treeView) {
		draggedItem = treeCell.getTreeItem();
		// Root cannot be dragged.
		if (draggedItem == null || draggedItem.getParent() == null) return;
		Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
		
		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, treeCell.getText());
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();
	}
	
	private void dragOver(DragEvent event, TreeCell<StackItem> treeCell, TreeView<StackItem> treeView) {
		if (!event.getDragboard().hasContent(DataFormat.PLAIN_TEXT)) return;
		TreeItem<StackItem> thisItem = treeCell.getTreeItem();
		
		if (draggedItem == null || thisItem == null || thisItem == draggedItem) return;	
		if (draggedItem.getParent() == null) {
			clearDropZone();
			return;
		}
		
		event.acceptTransferModes(TransferMode.MOVE);
		if (!treeCell.equals(dropZone)) {
			clearDropZone();
			this.dropZone = treeCell;
			if (treeCell.getItem().getType() == ItemType.GROUP && !treeCell.getTreeItem().isExpanded()) {
				dropZone.getStyleClass().add("drop-hint-group");
			} else {
				dropZone.getStyleClass().add("drop-hint");
			}
		}
	}
	
	private void drop(DragEvent event, TreeCell<StackItem> treeCell, TreeView<StackItem> treeView) {
		Dragboard db = event.getDragboard();
		TreeItem<StackItem> thisItem = treeCell.getTreeItem();
		if (!db.hasContent(DataFormat.PLAIN_TEXT) || draggedItem == null || draggedItem == thisItem) return;
		
		TreeItem<StackItem> droppedItemParent = draggedItem.getParent();
		droppedItemParent.getChildren().remove(draggedItem);
		
		if (thisItem.getValue().getType() == ItemType.GROUP) {
			thisItem.getChildren().add(0, draggedItem);
		} else {
			int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
			thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);
		}
		
		treeView.getSelectionModel().clearSelection();
		treeView.getSelectionModel().select(draggedItem);
		event.setDropCompleted(true);
		handleUpdate();
	}
	
	private void clearDropZone() {
		if (dropZone != null) {
			dropZone.getStyleClass().removeAll("drop-hint", "drop-hint-group");
			dropZone = null;
		}
	}
	
	/**
	 * Get the stack group closest to the active element.
	 */
	public StackGroup getSelectedGroup() {
		TreeItem<StackItem> item = entityList.getSelectionModel().getSelectedItem();
		if (item == null) {
			return entityList.getRoot().getValue().getGroup();
		}
		while (true) {
			if (item.getValue().getType() == ItemType.GROUP) {
				return item.getValue().getGroup();
			}
			item = item.getParent();
			if (item == null) return null;
		}
	}
	
	/**
	 * Get a stack group representing the current tree, after any modifications.
	 */
	public static StackItem parse(TreeItem<StackItem> item) {
		if (item.getValue().getType() == ItemType.ENTITY) {
			return item.getValue();
		} else {
			StackGroup group = new StackGroup(item.getValue().getGroup().getName());
			for (TreeItem<StackItem> child : item.getChildren()) {
				group.items.add(parse(child));
			}
			return new StackItem(group);
		}
	}
	
	public void handleUpdate() {
		StackGroup updated = parse(entityList.getRoot()).getGroup();
		if (updated == null) return;
		
		Level level = ScaffoldEditor.getInstance().getLevel();
		level.getOperationManager().execute(new ModifyStackOperation(level, updated));
	}
	
	protected void updateSelection() {
		if (supressSelectionUpdate) return;
		
		isUpdatingSelection = true;
		editor.getSelectedEntities().clear();
		for (TreeItem<StackItem> selected : entityList.getSelectionModel().getSelectedItems()) {
			StackItem item = selected.getValue();
			if (item.getType() == ItemType.ENTITY) {
				editor.getSelectedEntities().add(item.getEntity());
			}
		}
		editor.updateSelection();
		isUpdatingSelection = false;
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
			getLevel().getOperationManager().execute(new DeleteEntityOperation(getLevel(),
					Collections.singleton(entity)));
		});

		return new ContextMenu(edit, delete);
	}
	
	protected ContextMenu createGroupContextMenu(TreeItem<StackItem> item) {
		StackGroup group = item.getValue().getGroup();
		
		MenuItem selectChildren = new MenuItem("Select Children");
		selectChildren.setOnAction(e -> {
			supressSelectionUpdate = true;
			entityList.getSelectionModel().clearSelection();
			iterate(item, i -> {
				entityList.getSelectionModel().select(i);
			});
			supressSelectionUpdate = false;
			updateSelection();
		});
		
		MenuItem copy = new MenuItem("Copy Group");
		copy.setOnAction(e -> {
			ClipboardManager.getInstance().copyGroup(group);
		});
		
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e -> {
			if (group.size() > 0) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
//				alert.setHeaderText("Delete Group");
				alert.setContentText("This group has entities in it. Are you sure you want to delete it?");
				Optional<ButtonType> response = alert.showAndWait();
				if (response.isEmpty() || response.get() != ButtonType.OK) return;
			}
			
			getLevel().getOperationManager().execute(new DeleteGroupOperation(getLevel(), Collections.singleton(group)));
		});
		
		return new ContextMenu(selectChildren, copy, delete);
	}
	
	public TreeView<StackItem> getRoot() {
		return entityList;
	}
	
	public void setEntities(StackGroup entities) {
		StackItem root = new StackItem(entities);
		entityList.setRoot(load(root, entityList.getRoot()));
		updateSelection();
	}
	
	public static Outliner load(ScaffoldUI parent) throws IOException {
		Outliner outliner = new Outliner();
		outliner.init(parent.getEditor());
		return outliner;
	}
	
	private Level getLevel() {
		return editor.getLevel();
	}
}

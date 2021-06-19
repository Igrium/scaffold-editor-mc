package org.scaffoldeditor.editormc.sub_editors.nbt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.util.Constants;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

public class NBTEditorController {
	
	@FXML
	private BorderPane rootPane;
	@FXML
	private Button editButton;
	@FXML
	private Button editSNBTButton;

	private NBTBrowserController nbtBrowser;
	private Parent root;
	private Stage stage;
	private EventDispatcher<NamedTag> updateNBTDispatcher = new EventDispatcher<>();
	
	public void loadNBT(NamedTag tag) {
		nbtBrowser.loadNBT(tag);
	}
	
	@FXML
	private void initialize() {
		try {
			nbtBrowser = NBTBrowserController.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		rootPane.setCenter(nbtBrowser.getRoot());
		
		nbtBrowser.getNBTTree().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				event.consume();
				editEntry();
			}
		});
		
		nbtBrowser.getNBTTree().getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<NamedTag>>() {
			@Override
			public void onChanged(Change<? extends TreeItem<NamedTag>> c) {
				if (c.getList().size() > 0) {
					NamedTag selection = c.getList().get(0).getValue();
					editButton.setDisable(!isEditable(selection.getTag()));

				} else {
					editButton.setDisable(true);
				}
				editSNBTButton.setDisable(c.getList().size() == 0);;
			}
		});
		
		rootPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
				delete();
				event.consume();
			}
		});
	}
	
	@FXML
	public void delete() {
		for (TreeItem<NamedTag> item : nbtBrowser.getNBTTree().getSelectionModel().getSelectedItems()) {
			deleteEntry(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleteEntry(TreeItem<NamedTag> item) {
		if (item == null || item.getParent() == null) return;
		Tag<?> parent = item.getParent().getValue().getTag();
		
		if (parent instanceof CompoundTag) {
			((CompoundTag) parent).remove(item.getValue().getName());
		} else if (parent instanceof ListTag) {
			ListTag<Tag<?>> parentTag = (ListTag<Tag<?>>) parent;
			parentTag.remove(parentTag.indexOf(item.getValue().getTag()));
		} else {
			return;
		}
		item.getParent().getChildren().remove(item);
	}
	
	@FXML
	public void editEntry() {
		editEntry(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	public void editEntry(TreeItem<NamedTag> item) {
		if (isEditable(item.getValue().getTag())) {
			TreeItem<NamedTag> parent = item.getParent();
			boolean isList = parent.getValue().getTag() instanceof ListTag;
			
			NBTValueEditor editor = NBTValueEditor.open(item.getValue(), stage, isList);
			editor.onFinished(tag -> {
				if (parent.getValue().getTag() instanceof CompoundTag) {
					CompoundTag compound = (CompoundTag) parent.getValue().getTag();
					compound.remove(item.getValue().getName());
					compound.put(tag.getName(), tag.getTag());
				} else if (parent.getValue().getTag() instanceof ListTag) {
					@SuppressWarnings("unchecked")
					ListTag<Tag<?>> list = (ListTag<Tag<?>>) parent.getValue().getTag();
					int index = list.indexOf(item.getValue().getTag());
					list.set(index, tag.getTag());
				}
				
				int index = parent.getChildren().indexOf(item);
				parent.getChildren().set(index, new TreeItem<>(tag));
			});
		}
	}
	
	/**
	 * Force-replace an entry on the tree. Very unstable.
	 */
//	private void replaceEntry(TreeItem<NamedTag> item, Tag<?> newValue) {
//		String name = item.getValue().getName();
//		item.setValue(new NamedTag(name, newValue));
//		
//		if (item.getParent() != null) {
//			Tag<?> parent = item.getParent().getValue().getTag();
//			if (parent instanceof CompoundTag) {
//				((CompoundTag) parent).put(name, newValue);
//			} else if (parent instanceof ListTag) {
//				@SuppressWarnings("unchecked")
//				ListTag<Tag<?>> list = (ListTag<Tag<?>>) parent;
//				list.set(list.indexOf(item.getValue().getTag()), newValue);
//			} else {
//				throw new IllegalStateException("Tag's parent doesn't include it as a child!");
//			}
//		} else {
//			nbtBrowser.loadNBT(new NamedTag(name, newValue));
//		}
//	}
	
	@FXML
	public void newEntry() {
		newEntry(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	public void newEntry(TreeItem<NamedTag> parentItem) {
		if (parentItem == null) return;
		Tag<?> parent = parentItem.getValue().getTag();	
		if (!(parent instanceof CompoundTag || parent instanceof ListTag)) return;
		
		if (parent instanceof ListTag) {
			ListTag<?> list = (ListTag<?>) parent;
			if (ListTag.class.isAssignableFrom(list.getTypeClass()) || CompoundTag.class.isAssignableFrom(list.getTypeClass())) return;
		}
		
		NBTValueEditor editor;
		if (parent instanceof ListTag) {			
			editor = NBTValueEditor.openNew(((ListTag<?>) parent).getTypeClass(), stage, true);
		} else {
			editor = NBTValueEditor.openNew(ByteTag.class, stage, false);
		}
		editor.onFinished(tag -> {
			if (parent instanceof CompoundTag) {
				((CompoundTag) parent).put(tag.getName(), tag.getTag());
			} else if (parent instanceof ListTag) {
				@SuppressWarnings("unchecked")
				ListTag<Tag<?>> list = (ListTag<Tag<?>>) parent;
				
				if (!list.getTypeClass().isAssignableFrom(tag.getTag().getClass())) {
					LogManager.getLogger().error(tag.getTag().getClass().getSimpleName()
							+ " cannnot be added to a list of type " + list.getTypeClass().getSimpleName());
					return;
				}
				list.add(tag.getTag());
			}
			parentItem.getChildren().add(new TreeItem<>(tag));	
		});
	}
	
	@FXML
	public void newCompound() {
		newCompound(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void newCompound(TreeItem<NamedTag> parentItem) {
		if (parentItem == null) return;
		Tag<?> parent = parentItem.getValue().getTag();
		CompoundTag newTag = new CompoundTag();
		
		if (parent instanceof ListTag && !((ListTag) parent).getTypeClass().isAssignableFrom(CompoundTag.class)) return;
		
		String name = "";
		if (parent instanceof CompoundTag) {
			TextInputDialog nameDialog = new TextInputDialog();
			nameDialog.setTitle("New Compound Tag");
			nameDialog.setHeaderText("Tag name");
			Optional<String> nameOpt = nameDialog.showAndWait();
			if (nameOpt.isEmpty() || nameOpt.get().length() == 0) return;
			name = nameOpt.get();
			
			((CompoundTag) parent).put(name, newTag);
		} else if (parent instanceof ListTag) {
			ListTag parentTag = (ListTag) parent;
			if (!parentTag.getTypeClass().isAssignableFrom(CompoundTag.class)) {
				LogManager.getLogger().error(
						"CompoundTag cannnot be added to a list of type " + parentTag.getTypeClass().getSimpleName());
				return;
			}

			parentTag.add(newTag);
		} else {
			return;
		}
		parentItem.getChildren().add(new TreeItem<>(new NamedTag(name, newTag)));
	}
	
	@FXML
	public void newList() {
		newList(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void newList(TreeItem<NamedTag> parentItem) {
		if (parentItem == null) return;
		Tag<?> parent = parentItem.getValue().getTag();
		if (!(parent instanceof CompoundTag || parent instanceof ListTag)) return;
		
		if (parent instanceof ListTag && !((ListTag) parent).getTypeClass().isAssignableFrom(ListTag.class)) return;
		
		ChoiceDialog<String> typeDialog = new ChoiceDialog<>("CompoundTag", Constants.TAG_NAMES.keySet());
		typeDialog.setTitle("New List Tag");
		typeDialog.setHeaderText("Select list type");
		Optional<String> typeOpt = typeDialog.showAndWait();
		if (typeOpt.isEmpty()) return;
		ListTag<?> newTag = new ListTag<>(Constants.TAG_NAMES.get(typeOpt.get()));
		
		String name = "";
		if (parent instanceof CompoundTag) {
			TextInputDialog nameDialog = new TextInputDialog();
			nameDialog.setTitle("New List Tag");
			nameDialog.setHeaderText("Tag name");
			Optional<String> nameOpt = nameDialog.showAndWait();
			if (nameOpt.isEmpty() || nameOpt.get().length() == 0) return;
			name = nameOpt.get();
			
			((CompoundTag) parent).put(name, newTag);
		} else if (parent instanceof ListTag) {
			ListTag parentTag = (ListTag) parent;
			if (!parentTag.getTypeClass().isAssignableFrom(ListTag.class)) {
				LogManager.getLogger().error(
						"ListTag cannnot be added to a list of type " + parentTag.getTypeClass().getSimpleName());
				return;
			}

			parentTag.add(newTag);
		} else {
			return;
		}
		parentItem.getChildren().add(new TreeItem<>(new NamedTag(name, newTag)));
	}
	
	@FXML
	public void editSNBT() {
		editSNBT(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	public void editSNBT(TreeItem<NamedTag> item) {
		String name = item.getValue().getName();
		Tag<?> oldTag = item.getValue().getTag();
		SNBTEditor editor;
		try {
			editor = SNBTEditor.open(stage, item.getValue().getTag());
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		
		
		editor.onFinished(tag -> {
			TreeItem<NamedTag> newItem = nbtBrowser.loadTag(new NamedTag(name, tag));
			if (item.getParent() == null) {
				nbtBrowser.loadNBT(newItem);
			} else {
				Tag<?> parentTag = item.getParent().getValue().getTag();
				if (parentTag instanceof CompoundTag) {
					((CompoundTag) parentTag).put(name, tag);
				} else if (parentTag instanceof ListTag) {
					@SuppressWarnings("unchecked")
					ListTag<Tag<?>> list = (ListTag<Tag<?>>) parentTag;
					if (!list.getTypeClass().isInstance(tag)) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("NBT Error");
						alert.setHeaderText("Error saving SNBT");
						alert.setContentText("Tag type "+tag.getClass().getSimpleName()+" cannot be added to a list of type "+list.getTypeClass().getSimpleName());
						alert.show();
						return;
					}
					
					list.set(list.indexOf(oldTag), tag);
				}
				
				List<TreeItem<NamedTag>> children = item.getParent().getChildren();
				children.set(children.indexOf(item), new TreeItem<>(new NamedTag(name, tag)));
			}
		});
	}
	
	@FXML
	public void apply() {
		NamedTag value = nbtBrowser.getNBT();
		updateNBTDispatcher.fire(value);
		
		if (stage != null) {
			stage.close();
		}
	}
	
	protected boolean isEditable(Tag<?> tag) {
		return (tag.getID() != 9 && tag.getID() != 10);
	}
	
	public static NBTEditorController load() throws IOException {
		FXMLLoader loader = new FXMLLoader(NBTBrowserController.class.getResource("/assets/scaffold/ui/nbt/nbt_editor.fxml"));
		Parent root = loader.load();
		NBTEditorController controller = loader.getController();
		controller.root = root;
		return controller;
	}
	
	public static NBTEditorController openPopup(Window parent) {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.initOwner(parent);
		popup.setTitle("Edit NBT");
		
		NBTEditorController controller;
		try {
			controller = load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Parent root = controller.root;
		Scene scene = new Scene(root, root.prefWidth(100), root.prefHeight(100));
		popup.setScene(scene);
		controller.stage = popup;
		popup.show();
		
		return controller;
	}
	
	public void onUpdateNBT(EventListener<NamedTag> listener) {
		updateNBTDispatcher.addListener(listener);
	}

	public Parent getRoot() {
		return root;
	}
}

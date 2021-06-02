package org.scaffoldeditor.editormc.nbt_browser;

import java.io.IOException;

import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

public class NBTEditorController {
	
	@FXML
	private BorderPane rootPane;
	@FXML
	private Button editButton;
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
			}
		});
	}
	
	@FXML
	public void editEntry() {
		editEntry(nbtBrowser.getNBTTree().getSelectionModel().getSelectedItem());
	}
	
	public void editEntry(TreeItem<NamedTag> item) {
		if (isEditable(item.getValue().getTag())) {
			TreeItem<NamedTag> parent = item.getParent();
			boolean allowEmptyName = parent.getValue().getTag() instanceof ListTag;
			
			NBTValueEditor editor = NBTValueEditor.open(item.getValue(), stage, allowEmptyName);
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
	
	public static NBTEditorController openPopup(Stage parent) {
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

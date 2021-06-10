package org.scaffoldeditor.editormc.sub_editors.nbt;

import java.io.IOException;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.Tag;

public final class NBTBrowserController {
	
	public final Image BYTE_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_byte.png"));
	public final Image BYTE_ARRAY_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_byte_array.png"));
	public final Image COMPOUND_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_compound.png"));
	public final Image DOUBLE_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_double.png"));
	public final Image FLOAT_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_float.png"));
	public final Image INT_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_int.png"));
	public final Image INT_ARRAY_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_int_array.png"));
	public final Image LIST_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_list.png"));
	public final Image LONG_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_long.png"));
	public final Image LONG_ARRAY_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_long_array.png"));
	public final Image SHORT_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_short.png"));
	public final Image STRING_ICON = new Image(getClass().getResourceAsStream("/assets/scaffold/ui/images/nbt/tag_string.png"));
	
	@FXML
	private TreeView<NamedTag> nbtTree;
	
	private NamedTag tag;
	private Parent root;
	
	@FXML
	private void initialize() {
		nbtTree.setCellFactory(tree -> {
			TreeCell<NamedTag> cell = new TreeCell<NamedTag>() {
				public void updateItem(NamedTag item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
						setGraphic(null);
					} else {
						Tag<?> tag = item.getTag();
						String textValue = tag.valueToString();
						if (tag instanceof ListTag) {
							textValue = ((ListTag<?>) tag).size()+" entries";
						} else if (tag instanceof CompoundTag) {
							textValue = ((CompoundTag) tag).size()+" entries";
						}
						setText(item.getName().length() > 0 ? item.getName()+": "+textValue : textValue);
						setGraphic(new ImageView(getTagImage(tag.getID())));
					}
				}
			};
			
			
			return cell;
		});
	}
	
	/**
	 * Load an NBT tag into the UI.
	 * @param tag Tag to load.
	 */
	public void loadNBT(NamedTag tag) {
		this.tag = tag;
		TreeItem<NamedTag> root = loadTag(tag);
		nbtTree.setRoot(root);
	}
	
	/**
	 * Get the NBT which is being displayed.
	 */
	public NamedTag getNBT() {
		return tag;
	}
	
	public TreeView<NamedTag> getNBTTree() {
		return nbtTree;
	}
	
	public TreeItem<NamedTag> loadTag(NamedTag tag) {
		TreeItem<NamedTag> item = new TreeItem<NamedTag>(tag);
		if (tag.getTag().getID() == 9) {
			for (Tag<?> subTag : (ListTag<?>) tag.getTag()) {
				item.getChildren().add(loadTag(new NamedTag("", subTag)));
			}
		} else if (tag.getTag().getID() == 10) {
			CompoundTag compound = (CompoundTag) tag.getTag();
			for (String name : compound.keySet()) {
				item.getChildren().add(loadTag(new NamedTag(name, compound.get(name))));
			}
		}
		return item;
	}
	
	
	private Image getTagImage(byte tagID) {
		if (tagID == 1) {
			return BYTE_ICON;
		} else if (tagID == 2) {
			return SHORT_ICON;
		} else if (tagID == 3) {
			return INT_ICON;
		} else if (tagID == 4) {
			return LONG_ICON;
		} else if (tagID == 5) {
			return FLOAT_ICON;
		} else if (tagID == 6) {
			return DOUBLE_ICON;
		} else if (tagID == 7) {
			return BYTE_ARRAY_ICON;
		} else if (tagID == 8) {
			return STRING_ICON;
		} else if (tagID == 9) {
			return LIST_ICON;
		} else if (tagID == 10) {
			return COMPOUND_ICON;
		} else if (tagID == 11) {
			return INT_ARRAY_ICON;
		} else if (tagID == 12) {
			return LONG_ARRAY_ICON;
		} else {
			return null;
		}
 	}
	
	public Parent getRoot() {
		return root;
	}
	
	public static NBTBrowserController load() throws IOException {
		FXMLLoader loader = new FXMLLoader(NBTBrowserController.class.getResource("/assets/scaffold/ui/nbt/nbt_browser.fxml"));
		Parent root = loader.load();
		NBTBrowserController controller = loader.getController();
		controller.root = root;
		return controller;
	}
	
	public static NBTBrowserController openPopup(Stage parent) {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.initOwner(parent);
		popup.setTitle("Browse NBT");
		
		NBTBrowserController controller;
		try {
			controller = load();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Parent root = controller.getRoot();
		Scene scene = new Scene(root, root.prefWidth(100), root.prefHeight(100));
		popup.setScene(scene);
		popup.show();
		
		return controller;
	}
}
package org.scaffoldeditor.editormc.nbt_browser;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class NBTBrowserController {
	
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
	private TreeView<String> nbtTree;
	
	private NamedTag tag;
	private Parent root;
	
	/**
	 * Load an NBT tag into the UI.
	 * @param tag Tag to load.
	 */
	public void loadNBT(NamedTag tag) {
		this.tag = tag;
		TreeItem<String> root = loadTag(tag);
		nbtTree.setRoot(root);
	}
	
	/**
	 * Get the NBT which is being displayed.
	 */
	public NamedTag getNBT() {
		return tag;
	}
	
	protected TreeItem<String> loadTag(NamedTag in) {
		String name = in.getName();
		Tag<?> tag = in.getTag();
		
		if (tag.getID() == 1) {
			return loadByteTag((ByteTag) tag, name);
		} else if (tag.getID() == 2) {
			return loadShortTag((ShortTag) tag, name);
		} else if (tag.getID() == 3) {
			return loadIntTag((IntTag) tag, name);
		} else if (tag.getID() == 4) {
			return loadLongTag((LongTag) tag, name);
		} else if (tag.getID() == 5) {
			return loadFloatTag((FloatTag) tag, name);
		} else if (tag.getID() == 6) {
			return loadDoubleTag((DoubleTag) tag, name);
		} else if (tag.getID() == 7) {
			return loadByteArrayTag((ByteArrayTag) tag, name);
		} else if (tag.getID() == 8) {
			return loadStringTag((StringTag) tag, name);
		} else if (tag.getID() == 9) {
			return loadListTag((ListTag<?>) tag, name);
		} else if (tag.getID() == 10) {
			return loadCompoundTag((CompoundTag) tag, name);
		} else if (tag.getID() == 11) {
			return loadIntArrayTag((IntArrayTag) tag, name);
		} else if (tag.getID() == 12) {
			return loadLongArrayTag((LongArrayTag) tag, name);
		} else {
			throw new AssertionError("Unknown tag ID: "+tag.getID());
		}
	}
	
	protected TreeItem<String> loadByteTag(ByteTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(BYTE_ICON));
	}
	
	protected TreeItem<String> loadShortTag(ShortTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(SHORT_ICON));
	}
	
	protected TreeItem<String> loadIntTag(IntTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(INT_ICON));
	}
	
	protected TreeItem<String> loadLongTag(LongTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(LONG_ICON));
	}
	
	protected TreeItem<String> loadFloatTag(FloatTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(FLOAT_ICON));
	}
	
	protected TreeItem<String> loadDoubleTag(DoubleTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(DOUBLE_ICON));
	}
	
	protected TreeItem<String> loadByteArrayTag(ByteArrayTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(BYTE_ARRAY_ICON));
	}
	
	protected TreeItem<String> loadStringTag(StringTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(STRING_ICON));
	}
	
	protected TreeItem<String> loadListTag(ListTag<?> tag, String name) {
		TreeItem<String> item = new TreeItem<>(name+": "+tag.size()+" entries", new ImageView(LIST_ICON));
		for (Tag<?> subTag : tag) {
			item.getChildren().add(loadTag(new NamedTag("", subTag)));
		}
		return item;
	}
	
	protected TreeItem<String> loadCompoundTag(CompoundTag tag, String name) {
		TreeItem<String> item = new TreeItem<>(name+": "+tag.size()+" entries", new ImageView(COMPOUND_ICON));
		for (String s : tag.keySet()) {
			item.getChildren().add(loadTag(new NamedTag(s, tag.get(s))));
		}
		return item;
	}
	
	protected TreeItem<String> loadIntArrayTag(IntArrayTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(INT_ARRAY_ICON));
	}
	
	protected TreeItem<String> loadLongArrayTag(LongArrayTag tag, String name) {
		return new TreeItem<>(name+": "+tag.valueToString(), new ImageView(LONG_ARRAY_ICON));
	}
	
	public Parent getRoot() {
		return root;
	}
	
	public static NBTBrowserController load() throws IOException {
		FXMLLoader loader = new FXMLLoader(NBTBrowserController.class.getResource("/assets/scaffold/ui/nbt_browser.fxml"));
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
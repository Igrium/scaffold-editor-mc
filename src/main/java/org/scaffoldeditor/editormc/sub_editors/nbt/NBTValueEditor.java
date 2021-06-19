package org.scaffoldeditor.editormc.sub_editors.nbt;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert.AlertType;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

public class NBTValueEditor {
	public static interface TagFieldType<T extends Tag<?>> {
		default String getString(Tag<?> tag) {
			return tag.valueToString();
		}
		public T getTag(String in);
	}
	
	@Deprecated
	public NBTValueEditor() {
		tagTypes.put("Byte", new TagFieldType<ByteTag>() {
			@Override
			public ByteTag getTag(String in) {
				return new ByteTag(Byte.parseByte(in));
			}
		});
		tagNames.put(1, "Byte");
		tagTypes.put("Short", new TagFieldType<ShortTag>() {
			@Override
			public ShortTag getTag(String in) {
				return new ShortTag(Short.parseShort(in));
			}
		});
		tagNames.put(2, "Short");
		tagTypes.put("Integer", new TagFieldType<IntTag>() {
			@Override
			public IntTag getTag(String in) {
				return new IntTag(Integer.parseInt(in));
			}
		});
		tagNames.put(3, "Integer");
		tagTypes.put("Long", new TagFieldType<LongTag>() {
			@Override
			public LongTag getTag(String in) {
				return new LongTag(Long.parseLong(in));
			}
		});
		tagNames.put(4, "Long");
		tagTypes.put("Float", new TagFieldType<FloatTag>() {
			@Override
			public FloatTag getTag(String in) {
				return new FloatTag(Float.parseFloat(in));
			}
		});
		tagNames.put(5, "Float");
		tagTypes.put("Double", new TagFieldType<DoubleTag>() {
			@Override
			public DoubleTag getTag(String in) {
				return new DoubleTag(Double.parseDouble(in));
			}
		});
		tagNames.put(6, "Double");
		tagTypes.put("String", new TagFieldType<StringTag>() {
			@Override
			public String getString(Tag<?> tag) {
				if (tag instanceof StringTag) {
					return ((StringTag) tag).getValue();
				} else {
					return TagFieldType.super.getString(tag);
				}
			}
			
			@Override
			public StringTag getTag(String in) {
				return new StringTag(in);
			}
		});
		tagNames.put(8, "String");
		tagTypes.put("Byte Array", new TagFieldType<ByteArrayTag>() {
			@Override
			public String getString(Tag<?> tag) {
				if (tag instanceof ByteArrayTag) {
					return Arrays.toString(((ByteArrayTag) tag).getValue());
				} else {
					return TagFieldType.super.getString(tag);
				}
			}
			
			@Override
			public ByteArrayTag getTag(String in) {
				String[] strings = in.replace("[", "").replace("]", "").replace(" ", "").split(",");
				byte[] result = new byte[strings.length];
				for (int i = 0; i < result.length; i++) {
					result[i] = Byte.parseByte(strings[i]);
				}
				return new ByteArrayTag(result);
			}
		});
		tagNames.put(7, "Byte Array");
		tagTypes.put("Int Array", new TagFieldType<IntArrayTag>() {
			@Override
			public String getString(Tag<?> tag) {
				if (tag instanceof IntArrayTag) {
					return Arrays.toString(((IntArrayTag) tag).getValue());
				} else {
					return TagFieldType.super.getString(tag);
				}
			}
			
			@Override
			public IntArrayTag getTag(String in) {
				String[] strings = in.replace("[", "").replace("]", "").replace(" ", "").split(",");
				int[] result = new int[strings.length];
				for (int i = 0; i < result.length; i++) {
					result[i] = Integer.parseInt(strings[i]);
				}
				return new IntArrayTag(result);
			}
		});
		tagNames.put(11, "Int Array");
		tagTypes.put("Long Array", new TagFieldType<LongArrayTag>() {
			@Override
			public String getString(Tag<?> tag) {
				if (tag instanceof LongArrayTag) {
					return Arrays.toString(((LongArrayTag) tag).getValue());
				} else {
					return TagFieldType.super.getString(tag);
				}
			}
			
			@Override
			public LongArrayTag getTag(String in) {
				String[] strings = in.replace("[", "").replace("]", "").replace(" ", "").split(",");
				long[] result = new long[strings.length];
				for (int i = 0; i < result.length; i++) {
					result[i] = Long.parseLong(strings[i]);
				}
				return new LongArrayTag(result);
			}
		});
		tagNames.put(12, "Long Array");
	};
	
	private Parent root;
	@FXML
	private TextField nameField;
	@FXML
	private TextField valueField;
	@FXML
	private ChoiceBox<String> choiceBox;
	
	private Map<String, TagFieldType<?>> tagTypes = new HashMap<>();
	private Map<Integer, String> tagNames = new HashMap<>();
	private EventDispatcher<NamedTag> dispatcher = new EventDispatcher<>();
	private Stage stage;
	private boolean allowEmptyName = false;
	private boolean forceType = false;
	
	@FXML
	private void initialize() {
		for (String name : tagTypes.keySet()) {
			choiceBox.getItems().add(name);
		}
	}
	
	public void setNBT(NamedTag tag) {
		nameField.setText(tag.getName());
		Tag<?> nbt = tag.getTag();
		String type = tagNames.get((int) nbt.getID());
		choiceBox.setValue(type);
		valueField.setText(tagTypes.get(type).getString(nbt));
		nameField.setDisable(allowEmptyName);
		choiceBox.setDisable(forceType);
	}
	
	public NamedTag getNBT() {
		String name = nameField.getText();
		if (!allowEmptyName && name.length() == 0) {
			Alert alert = new Alert(AlertType.ERROR, "Name cannot be empty!");
			alert.initOwner(stage);
			alert.showAndWait();
			return null;
		}
		
		try {
			Tag<?> nbt = tagTypes.get(choiceBox.getValue()).getTag(valueField.getText());
			return new NamedTag(name, nbt);
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR, "Improperly formatted value: "+e.getMessage());
			alert.showAndWait();
			return null;
		}
	}
	
	@FXML
	public void apply() {
		NamedTag tag = getNBT();
		if (tag != null) {
			dispatcher.fire(tag);
			stage.close();
		}
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public void onFinished(EventListener<NamedTag> listener) {
		dispatcher.addListener(listener);
	}
	
	/**
	 * Open the NBT value editor with a new tag.
	 * @param tagClass Class of tag to create.
	 * @param parent Parent window.
	 * @param isList Is the parent tag a list?
	 * @return Editor instance.
	 */
	public static NBTValueEditor openNew(Class<?> tagClass, Window parent, boolean isList) {
		Tag<?> tag;
		if (ByteTag.class.isAssignableFrom(tagClass)) {
			tag = new ByteTag();
		} else if (ShortTag.class.isAssignableFrom(tagClass)) {
			tag = new ShortTag();
		} else if (IntTag.class.isAssignableFrom(tagClass)) {
			tag = new IntTag();
		} else if (LongTag.class.isAssignableFrom(tagClass)) {
			tag = new LongTag();
		} else if (FloatTag.class.isAssignableFrom(tagClass)) {
			tag = new FloatTag();
		} else if (DoubleTag.class.isAssignableFrom(tagClass)) {
			tag = new DoubleTag();
		} else if (StringTag.class.isAssignableFrom(tagClass)) {
			tag = new StringTag();
		} else if (ByteArrayTag.class.isAssignableFrom(tagClass)) {
			tag = new ByteArrayTag();
		} else if (LongArrayTag.class.isAssignableFrom(tagClass)) {
			tag = new LongArrayTag();
		} else {
			throw new IllegalArgumentException("Cannot create a tag with class: "+tagClass.getSimpleName()+" using the NBT value editor!");
		}
		
		return open(new NamedTag("", tag), parent, isList);
	}
	
	/**
	 * Open the editor.
	 * @param tag Tag to open with.
	 * @param parent Parent stage.
	 * @param isList Is the parent tag a list?
	 * @return Editor instance.
	 */
	public static NBTValueEditor open(NamedTag tag, Window parent, boolean isList) {
		FXMLLoader loader = new FXMLLoader(NBTValueEditor.class.getResource("/assets/scaffold/ui/nbt/nbt_value_editor.fxml"));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new AssertionError("Unable to load NBT value editor ui!", e);
		}
		
		NBTValueEditor controller = loader.getController();
		controller.root = root;
		
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(parent);
		window.setTitle("Edit Value");
		
		Scene scene = new Scene(root, root.prefWidth(100), root.prefHeight(100));
		window.setScene(scene);
		controller.stage = window;
		window.setResizable(false);
		window.show();
		
		controller.allowEmptyName = isList;
		controller.forceType = isList;
		controller.setNBT(tag);
		return controller;
	}

	public Parent getRoot() {
		return root;
	}
	
	public Stage getStage() {
		return stage;
	}
}

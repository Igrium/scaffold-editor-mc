package org.scaffoldeditor.editormc.sub_editors;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.editormc.sub_editors.nbt.NBTEditorController;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.util.event.EventDispatcher;
import org.scaffoldeditor.scaffold.util.event.EventListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

public class BlockEditor {
	@FXML
	private NBTEditorController nbtBrowserController;
	@FXML
	private TextField nameField;
	
	private Stage stage;
	protected CompoundTag properties;
	
	private EventDispatcher<Block> dispatcher = new EventDispatcher<>();
	
	@FXML
	protected void initialize() {
		nbtBrowserController.setButtonBarEnabled(false);
		nbtBrowserController.onUpdateNBT(tag -> {
			properties = (CompoundTag) tag.getTag();
		});
	}
	
	/**
	 * Set the block this editor is displaying. 
	 * @param block Block to set.
	 */
	public void setBlock(Block block) {
		nameField.setText(block.getName());
		properties = block.getProperties().clone();
		nbtBrowserController.loadNBT(new NamedTag("Properties", properties));
	}
	
	public String getName() {
		return nameField.getText();
	}
	
	public CompoundTag getProperties() {
		return properties;
	}
	
	public Block getBlock() {
		return new Block(getName(), properties);
	}
	
	@FXML
	public void apply() {
		dispatcher.fire(getBlock());
		stage.close();
	}
	
	@FXML
	public void cancel() {
		stage.close();
	}
	
	public void onApply(EventListener<Block> listener) {
		dispatcher.addListener(listener);
	}
	
	public static BlockEditor open(Window parent, Block block) {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/block_editor.fxml"));
		Parent root;
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new AssertionError("Error loading block editor!", e);
		}
		LogManager.getLogger().info("Opening level properties editor.");
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("Edit Block Properties");
		stage.setScene(scene);
		BlockEditor controller = loader.getController();
		
		controller.setBlock(block);
		controller.stage = stage;
		stage.show();
		
		return controller;
	}
}

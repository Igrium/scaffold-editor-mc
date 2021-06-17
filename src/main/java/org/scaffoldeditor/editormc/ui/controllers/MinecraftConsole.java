package org.scaffoldeditor.editormc.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.scaffoldeditor.editormc.engine.mixins.HudAccessor;
import org.scaffoldeditor.editormc.ui.controllers.CompileProgressUI.MessageType;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;

public final class MinecraftConsole {
	@FXML
	private TextFlow outputField;
	
	@FXML
	private TextField inputField;
	
	@FXML
	private Button sendButton;
	
	private MinecraftClient client;
	private Stage stage;
	
	private List<String> history = new ArrayList<String>();
	private int historyIndex = 0;
	
	private ClientChatListener listener = new ClientChatListener() {
		
		@Override
		public void onChatMessage(net.minecraft.network.MessageType messageType, net.minecraft.text.Text message,
				UUID sender) {
			addMessage(message.getString(), MessageType.LOG);
		}
	};
	
	@FXML
	private void initialize() {
		inputField.setOnKeyPressed(event -> handleKeyPressed(event));
	}
	
	/**
	 * Called after the root node has been added to a scene.
	 */
	public void init(MinecraftClient client) {
		this.client = client;
		Map<net.minecraft.network.MessageType, List<ClientChatListener>> listeners = ((HudAccessor) client.inGameHud).getChatListeners();
		
		listeners.get(net.minecraft.network.MessageType.CHAT).add(listener);
		listeners.get(net.minecraft.network.MessageType.SYSTEM).add(listener);
		
//		client.inGameHud.
	}
	
	public void close() {
		Map<net.minecraft.network.MessageType, List<ClientChatListener>> listeners = ((HudAccessor) client.inGameHud).getChatListeners();
		listeners.get(net.minecraft.network.MessageType.CHAT).remove(listener);
		listeners.get(net.minecraft.network.MessageType.SYSTEM).remove(listener);
	}
	
	public void addMessage(String message, MessageType type) {
		Platform.runLater(() -> {
			Text output = new Text(message);
			switch(type) {
			case ERROR:
				output.getStyleClass().add("output-error");
				break;
			default:
				output.getStyleClass().add("output-text");
				break;
			}
			outputField.getChildren().addAll(output, new Text(System.lineSeparator()));
		});
	}
	
	public void sendMessage(String message) {
		client.execute(() -> {
			client.player.sendChatMessage(message);
		});
		history.subList(historyIndex, history.size()).clear();
		history.add(message);
		historyIndex = history.size();
		sendButton.setDisable(true);
	}
	
	public void up() {
		if (historyIndex == 0) return;
		historyIndex--;
		inputField.setText(history.get(historyIndex));
	}
	
	public void down() {
		if (historyIndex >= history.size() - 1) return;
		historyIndex++;
		inputField.setText(history.get(historyIndex));
	}
	
	private void handleKeyPressed(KeyEvent event) {
		if (event.getCode().equals(KeyCode.UP)) up();
		if (event.getCode().equals(KeyCode.DOWN)) down();
		
		sendButton.setDisable(inputField.getText().length() == 0);
	}
	
	@FXML
	private void handleSend() {
		sendMessage(inputField.getText());
		inputField.clear();
	}
	

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}
	
	public static MinecraftConsole open(Window parent) throws IOException {
		FXMLLoader loader = new FXMLLoader(CompileProgressUI.class.getResource("/assets/scaffold/ui/minecraft_console.fxml"));
		Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		Stage stage = new Stage();
		stage.setTitle("Console");
		stage.setScene(scene);
		MinecraftConsole controller = loader.getController();
		controller.stage = stage;
		controller.init(MinecraftClient.getInstance());
		
		stage.setOnCloseRequest(event -> {
			controller.close();
		});
		
		stage.show();
		
		return controller;
	}

}

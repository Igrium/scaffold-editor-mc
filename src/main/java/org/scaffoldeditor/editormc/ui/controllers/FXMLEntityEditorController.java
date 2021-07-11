package org.scaffoldeditor.editormc.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.scaffoldeditor.editormc.ui.ScaffoldUI;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.io.Output;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import com.github.rjeschke.txtmark.Processor;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

public class FXMLEntityEditorController {
	
	private Entity entity;
	
	@FXML
	public Button applyButton;
	
	@FXML
	public Button applyAndCloseButton;
	
	@FXML
	public GridPane attributePane;
	
	@FXML
	public TextField nameField;
	
	@FXML
	public Label entityTypeLabel;
	
	@FXML
	public HBox macroBox;
	
	@FXML
	public TableView<Output> outputTable;
	
	@FXML
	public TableColumn<Output, String> outputColumn;
	
	@FXML
	public TableColumn<Output, String> targetColumn;
	
	@FXML
	public TableColumn<Output, String> inputColumn;
	
	@FXML
	public ComboBox<String> outputBox;
	
	@FXML
	public ComboBox<String> targetBox;
	
	@FXML
	public ComboBox<String> inputBox;
	
	@FXML
	public Button deleteOutputButton;
	
	@FXML
	public Button selectTargetButton;
	
	@FXML
	public WebView docView;
		
	@FXML
	public VBox center;
	
	public boolean hasBeenUpdated = false;
	
	public void loadOutputs(Entity entity) {
		this.entity = entity;
		
		outputColumn.setCellValueFactory(new Callback<CellDataFeatures<Output, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Output, String> param) {
				return new ReadOnlyStringWrapper(param.getValue().getTrigger());
			}

		});
		
		targetColumn.setCellValueFactory(new Callback<CellDataFeatures<Output, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Output, String> param) {
				return new ReadOnlyStringWrapper(param.getValue().getTarget());
			}

		});
		
		inputColumn.setCellValueFactory(new Callback<CellDataFeatures<Output, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Output, String> param) {
				return new ReadOnlyStringWrapper(param.getValue().getInputName());
			}

		});
		
		outputTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		// We clone the outputs so we're not editing them directly
		outputTable.getItems().addAll(entity.getOutputs().stream().map(output -> output.clone()).collect(Collectors.toList()));
		
		outputBox.getItems().addAll(entity.getDeclaredOutputs().stream().map(output -> output.getName()).collect(Collectors.toList()));
		List<String> entNames = new ArrayList<>();
		entNames.add("!this");
		entNames.add("!instigator");
		entNames.addAll(entity.getLevel().listEntityNames());
		targetBox.getItems().addAll(entNames);
	}
	
	private void setDisableOutputControls(boolean disable) {
		deleteOutputButton.setDisable(disable);
		outputBox.setDisable(disable);
		targetBox.setDisable(disable);
		selectTargetButton.setDisable(disable);
		inputBox.setDisable(disable);
	}
	
	private void handleOutputSelected() {
		Output output = outputTable.getSelectionModel().getSelectedItem();
		reloadInputBox();
		if (output == null) {
			setDisableOutputControls(true);
			return;
		}
		setDisableOutputControls(false);
		
		outputBox.setValue(output.getTrigger());
		targetBox.setValue(output.getTarget());
		inputBox.setValue(output.getInputName());
	}
	
	private void reloadInputBox() {
		Output output = outputTable.getSelectionModel().getSelectedItem();
		inputBox.getItems().clear();
		if (output == null) {
			return;
		}
		
		Entity target;
		if ("!this".equals(targetBox.getValue())) {
			target = entity;
		} else {
			target = entity.getLevel().getEntity(targetBox.getValue());
		}
		
		if (target == null) {
			return;
		}
		
		inputBox.getItems().addAll(target.getDeclaredInputs().stream().map(input -> input.getName()).collect(Collectors.toList()));
	}
	
	@FXML
	public void addOutput() {
		outputTable.getItems().add(new Output(entity));
		hasBeenUpdated = true;
	}
	
	@FXML
	public void deleteSelected() {
		outputTable.getItems().remove(outputTable.getSelectionModel().getSelectedIndex());
		hasBeenUpdated = true;
	}
	
	@FXML
	private void initialize() {
		outputTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<>() {

			@Override
			public void onChanged(Change<? extends Integer> c) {
				handleOutputSelected();
			}
			
		});
		
		outputBox.focusedProperty().addListener(e -> {
			if (!outputBox.isFocused()) {
				getSelected().setTrigger(outputBox.getValue());
				onUpdate();
			}
		});
		
		outputBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				getSelected().setTrigger(outputBox.getValue());
				onUpdate();
			}
		});
		
		targetBox.focusedProperty().addListener(e -> {
			if (!targetBox.isFocused()) {
				getSelected().setTarget(targetBox.getValue());
				reloadInputBox();
				onUpdate();
			}
		});
		
		targetBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				getSelected().setTarget(targetBox.getValue());
				reloadInputBox();
				onUpdate();
			}
		});
		
		inputBox.focusedProperty().addListener(e -> {
			if (!inputBox.isFocused()) {
				getSelected().setInputName(inputBox.getValue());
				onUpdate();
			}
		});
		
		inputBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				getSelected().setInputName(inputBox.getValue());
				onUpdate();
			}	
		});

		docView.setContextMenuEnabled(false);
		WebEngine webEngine = docView.getEngine();
		webEngine.setUserStyleSheetLocation(
				getClass().getResource("/assets/scaffold/ui/css/scaffold_web.css").toString());
		
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				// Inject font.
				Document doc = webEngine.getDocument();
				Element styleNode = doc.createElement("style");
				styleNode.setTextContent("body { font-family: '" + Font.getDefault().getFamily() + "';}");
				
				doc.getDocumentElement().getElementsByTagName("head").item(0).appendChild(styleNode);
				
				// Deal with links.
				NodeList links = doc.getElementsByTagName("a");
				for (int i = 0; i < links.getLength(); i++) {
					EventTarget item = (EventTarget) links.item(i);
					item.addEventListener("click", event -> {
						HTMLAnchorElement element = (HTMLAnchorElement) event.getCurrentTarget();
						String href = element.getHref();
						event.preventDefault();
						
						ScaffoldUI.getInstance().getHostServices().showDocument(href);
					}, false);
				}
			}
		});
	}

	public void updateDoc(String str) {
		docView.getEngine().loadContent(Processor.process(str));
	}
	
	private void onUpdate() {
		outputTable.refresh();
		hasBeenUpdated = true;
	}
	
	private Output getSelected() {
		return outputTable.getSelectionModel().getSelectedItem();
	}
}

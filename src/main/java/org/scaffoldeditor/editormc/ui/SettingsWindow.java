package org.scaffoldeditor.editormc.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scaffoldeditor.editormc.Config;
import org.scaffoldeditor.editormc.ui.setting_types.ChangeSettingEvent;
import org.scaffoldeditor.editormc.ui.setting_types.ISettingType;
import org.scaffoldeditor.editormc.ui.setting_types.KeyBinding;
import org.scaffoldeditor.editormc.ui.setting_types.StringSetting;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {

	public final Map<String, ISettingType> settingTypes = new HashMap<>();
	protected TabPane tabPane;
	protected Stage stage;
	protected Scene scene;
	protected final Map<String, String[]> cache = new HashMap<>();
	
	public SettingsWindow(Stage parent) {
		stage = new Stage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/scaffold/ui/settings_window.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		scene = new Scene(root, 1280, 800);
		stage.setTitle("Scaffold Settings");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		
		tabPane = (TabPane) scene.lookup("#tabPane");
		
		scene.addEventHandler(ChangeSettingEvent.SETTING_CHANGED, (e) -> {
			cache.put(e.getPath(), new String[] {e.getType(), e.getNewValue()});
		});
		
		stage.setOnCloseRequest(e -> save());
		
		// Init setting types;
		settingTypes.put("KeyBinding", new KeyBinding());
		settingTypes.put("StringSetting", new StringSetting());
	}
	
	public void show() {
		stage.show();
		reload();
	}
	
	public void close() {
		stage.close();
	}
	
	public void reload() {
		generateSettings(Config.getConfig());
	}
	
	public void save() {
		if (cache.size() > 0) {
			for (String path : cache.keySet()) {
				String[] setting = cache.get(path);
				Config.setValue(path, setting[0], setting[1]);
			}
			try {
				Config.save();
			} catch (IOException e) {
				System.err.println("Unable to save Scaffold config!");
				e.printStackTrace();
			}
		}
	}
	
	private void generateSettings(Element config) {
		NodeList children = config.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element page = (Element) child;
				if (page.getTagName().equals("Page")) {
					addTab(generatePage(page));
				}
			}	
		}
	}
	
	private Tab generatePage(Element page) {
		NodeList children = page.getChildNodes();
		
		Accordion accordion = new Accordion();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				Element group = (Element) node;
				if (group.getTagName().equals("Group")) {
					int index = -1;
					TitledPane oldPane = getPane(accordion, page.getAttribute("id"));
					if (oldPane != null) {
						index = accordion.getPanes().indexOf(oldPane);
						accordion.getPanes().set(index, generateGroup(group, page.getAttribute("id")));
					} else {
						accordion.getPanes().add(generateGroup(group, page.getAttribute("id")));
					}
				}
			}
		}
		return new Tab(page.getAttribute("id"), accordion);
	}
	
	private TitledPane generateGroup(Element group, String pageName) {
		GridPane grid = new GridPane();
		NodeList children = group.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element setting = (Element) child;
				String name = setting.getTagName();
				if (settingTypes.containsKey(name)) {
					String id = setting.getAttribute("id");
					ISettingType settingType = settingTypes.get(name);
					String path = String.join(".", pageName, group.getAttribute("id"), id);
					
					String value = Config.getValue(path);
					if (value == null)
						value = "";
					
					javafx.scene.Node setter = settingType.createSetter(setting, path, value, tabPane.getScene());

					grid.add(new Label(id + ":"), 0, i);
					grid.add(setter, 1, i);
				}
			}
		}

		return new TitledPane(group.getAttribute("id"), grid);
	}
	
	
	private void addTab(Tab tab) {
		String id = tab.getText();
		List<Tab> tabs = tabPane.getTabs();
		
		Tab oldTab = getTab(id);
		int index = -1;
		if (oldTab != null) {
			index = tabs.indexOf(oldTab);
		}
		
		if (index < 0) {
			tabs.add(tab);
		} else {
			tabs.set(index, tab);
		}
	}
	
	private Tab getTab(String name) {
		for (Tab tab : tabPane.getTabs()) {
			if (tab.getText().equals(name)) {
				return tab;
			}
		}
		return null;
	}
	
	private TitledPane getPane(Accordion in, String name) {
		for (TitledPane pane : in.getPanes()) {
			if (name.equals(pane.getText())) {
				return pane;
			}
		}
		return null;
	}
	
}

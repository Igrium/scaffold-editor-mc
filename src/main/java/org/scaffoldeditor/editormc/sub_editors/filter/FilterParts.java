package org.scaffoldeditor.editormc.sub_editors.filter;

import org.scaffoldeditor.editormc.util.CommandVectorSetter;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandRotation;
import org.scaffoldeditor.scaffold.logic.datapack.arguements.CommandVector3f;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.*;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public final class FilterParts {
	private FilterParts() {};
	
	public static abstract class StringLike<T extends SubCommand> extends FilterPart<T> {
		protected TextField setter = new TextField();
		protected abstract T get(String value);
		public abstract String getString(T val);
		
		@Override
		public TextField getSetter() {
			return setter;
		}
		
		@Override
		public T getValue() {
			return get(setter.getText());
		}
		
		@Override
		public void setValue(T value) {
			setter.setText(getString(value));
		}
		
		@Override
		public void onUpdate(Runnable callback) {
			setter.textProperty().addListener((observable, val, old) -> {
				callback.run();
			});
		}
	}
	
	public static abstract class SelectorLike<T extends SubCommand> extends FilterPart<T> {
		protected TextField setter = new TextField();
		protected abstract T get(TargetSelector value);
		public abstract TargetSelector getSelector(T val);
		
		public SelectorLike() {
			setter.setPromptText("Target selector string");
		}
		
		@Override
		public TextField getSetter() {
			return setter;
		}
		
		@Override
		public T getValue() {
			return get(TargetSelector.fromString(setter.getText()));
		}
		
		@Override
		public void setValue(T value) {
			setter.setText(getSelector(value).compile());
		}
		
		@Override
		public void onUpdate(Runnable callback) {
			setter.textProperty().addListener((observable, val, old) -> {
				callback.run();
			});
		}
	}
	
	public static abstract class VectorLike<T extends SubCommand> extends FilterPart<T> {
		protected CommandVectorSetter setter = new CommandVectorSetter();
		protected abstract T get(CommandVector3f value);
		public abstract CommandVector3f getVector(T val);
		
		@Override
		public Node getSetter() {
			return setter;
		}
		
		@Override
		public T getValue() {
			return get(setter.getCommandVector());
		}
		
		@Override
		public void setValue(T value) {
			setter.setVector(getVector(value));
		}
		
		@Override
		public void onUpdate(Runnable callback) {
			setter.onUpdate(callback);
		}
	}
	
	public static abstract class ConditionalFilter<T extends SubCommand> extends FilterPart<T> {
		
		protected Conditional conditional;
		
		protected abstract T get(Conditional value);
		public abstract Conditional getConditional(T val);
		
		@Override
		public Node getSetter() {
			return new Label("Conditionals not implemented yet!");
		}
		
		@Override
		public T getValue() {
			return get(conditional);
		}
		
		@Override
		public void setValue(T value) {
			conditional = getConditional(value);
		}
		
		@Override
		public void onUpdate(Runnable callback) {
		}
		
	}
	
	public static class AlignFilter extends StringLike<Align> {

		@Override
		public String getRegistryName() {
			return "align";
		}

		@Override
		protected Align get(String value) {
			return new Align(value);
		}

		@Override
		public String getString(Align val) {
			return val.value;
		}
		
	}
	
	public static class AnchoredFilter extends StringLike<Anchored> {

		@Override
		public String getRegistryName() {
			return "anchored";
		}

		@Override
		protected Anchored get(String value) {
			return new Anchored(value);
		}

		@Override
		public String getString(Anchored val) {
			return val.value;
		}
		
	}
	
	public static class AsFilter extends SelectorLike<As> {

		@Override
		public String getRegistryName() {
			return "as";
		}

		@Override
		protected As get(TargetSelector value) {
			return new As(value);
		}

		@Override
		public TargetSelector getSelector(As val) {
			return val.value;
		}
		
	}
	
	public static class AtFilter extends SelectorLike<At> {

		@Override
		public String getRegistryName() {
			return "at";
		}

		@Override
		protected At get(TargetSelector value) {
			return new At(value);
		}

		@Override
		public TargetSelector getSelector(At val) {
			return val.value;
		}
		
	}
	
	public static class FacingFilter extends VectorLike<Facing> {

		@Override
		public String getRegistryName() {
			return "facing";
		}

		@Override
		protected Facing get(CommandVector3f value) {
			return new Facing(value);
		}

		@Override
		public CommandVector3f getVector(Facing val) {
			return val.value;
		}

	}
	
	public static class FacingEntFilter extends FilterPart<FacingEnt> {
		
		TextField target = new TextField();
		TextField anchor = new TextField();
		HBox box = new HBox(5);
		
		private final double PREF_WIDTH = 30;
		
		public FacingEntFilter() {
			target.setPromptText("Target entity");
			anchor.setPromptText("Anchor");
			target.setPrefWidth(PREF_WIDTH);
			anchor.setPrefWidth(PREF_WIDTH);
			
			box.getChildren().add(target);
			box.getChildren().add(anchor);
		}
		
		@Override
		public Node getSetter() {
			return box;
		}

		@Override
		public FacingEnt getValue() {
			return new FacingEnt(TargetSelector.fromString(target.getText()), anchor.getText());
		}

		@Override
		public String getRegistryName() {
			return "facing entity";
		}

		@Override
		public void setValue(FacingEnt value) {
			target.setText(value.target.compile());
			anchor.setText(value.anchor);
		}
		
		@Override
		public void onUpdate(Runnable callback) {
			target.textProperty().addListener((prop, val, old) -> {
				callback.run();
			});
			
			anchor.textProperty().addListener((prop, val, old) -> {
				callback.run();
			});
		}

	}
	
	public static class InFilter extends StringLike<In> {

		@Override
		public String getRegistryName() {
			return "in";
		}

		@Override
		protected In get(String value) {
			return new In(value);
		}

		@Override
		public String getString(In val) {
			return val.value;
		}
		
	}
	
	public static class PositionedFilter extends VectorLike<Positioned> {

		@Override
		public String getRegistryName() {
			return "positioned";
		}

		@Override
		protected Positioned get(CommandVector3f value) {
			return new Positioned(value);
		}

		@Override
		public CommandVector3f getVector(Positioned val) {
			return val.value;
		}
		
	}
	
	public static class RotatedFilter extends FilterPart<Rotated> {

		@Override
		public Node getSetter() {
			return new Label("Rotation setter not implemented yet!");
		}

		@Override
		public Rotated getValue() {
			return new Rotated(new CommandRotation(0, 0));
		}

		@Override
		public String getRegistryName() {
			return "rotated";
		}

		@Override
		public void setValue(Rotated value) {
		}
		
		@Override
		public void onUpdate(Runnable callback) {
		}
	}
	
	public static class RotatedAsFilter extends SelectorLike<RotatedAs> {

		@Override
		public String getRegistryName() {
			return "rotated as";
		}

		@Override
		protected RotatedAs get(TargetSelector value) {
			return new RotatedAs(value);
		}

		@Override
		public TargetSelector getSelector(RotatedAs val) {
			return val.value;
		}
		
	}
	
	public static class IfFilter extends ConditionalFilter<If> {

		@Override
		public String getRegistryName() {
			return "if";
		}

		@Override
		protected If get(Conditional value) {
			return new If(value);
		}

		@Override
		public Conditional getConditional(If val) {
			return val.value;
		}
		
	}
	
	public static class UnlessFilter extends ConditionalFilter<Unless> {

		@Override
		public String getRegistryName() {
			return "unless";
		}

		@Override
		protected Unless get(Conditional value) {
			return new Unless(value);
		}

		@Override
		public Conditional getConditional(Unless val) {
			return val.value;
		}
		
	}
}

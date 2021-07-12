package org.scaffoldeditor.editormc.sub_editors.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.scaffoldeditor.editormc.sub_editors.filter.FilterParts.*;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.*;

public class FilterPartRegistry {
	
	public static interface FilterPartFactory {
		FilterPart<?> create(SubCommand intialValue);
		FilterPart<?> create();
	}

	
	public static class BaseFactory implements FilterPartFactory {
		final Supplier<FilterPart<?>> supplier;
		public BaseFactory(Supplier<FilterPart<?>> supplier) {
			this.supplier = supplier;
		}
		@Override
		public FilterPart<?> create(SubCommand intialValue) {
			FilterPart<?> val = supplier.get();
			val.setSubCommand(intialValue);
			
			return val;
		}
		@Override
		public FilterPart<?> create() {
			return supplier.get();
		}
	}
	
	public final Map<String, FilterPartFactory> registry = new HashMap<>();
	
	public FilterPartRegistry() {
		registerDefaults();
	}
	
	public FilterPart<?> create(String registryName, SubCommand initialValue) {
		FilterPartFactory factory = registry.get(registryName);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown filter part: "+registryName);
		}
		return factory.create(initialValue);
	}
	
	public FilterPart<?> create(SubCommand initialValue) {
		return create(getRegistryName(initialValue), initialValue);
	}
	
	public FilterPart<?> create(String registryName) {
		FilterPartFactory factory = registry.get(registryName);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown filter part: "+registryName);
		}
		return factory.create();
	}
	
	public String getRegistryName(SubCommand command) {
		if (command instanceof Align) return "align";
		if (command instanceof Anchored) return "anchored";
		if (command instanceof As) return "as";
		if (command instanceof At) return "at";
		if (command instanceof Facing) return "facing";
		if (command instanceof FacingEnt) return "facing entity";
		if (command instanceof In) return "in";
		if (command instanceof Positioned) return "positioned"; 
		if (command instanceof Rotated) return "rotated";
		if (command instanceof RotatedAs) return "rotated as";
		if (command instanceof If) return "if";
		if (command instanceof Unless) return "unless";
		
		throw new IllegalArgumentException("Unknown filter part type: "+command.getClass().getSimpleName());
	}
	
	public void registerDefaults() {
		registry.put("align", new BaseFactory(AlignFilter::new));
		registry.put("anchored", new BaseFactory(AnchoredFilter::new));
		registry.put("as", new BaseFactory(AsFilter::new));
		registry.put("at", new BaseFactory(AtFilter::new));
		registry.put("facing", new BaseFactory(FacingFilter::new));
		registry.put("facing entity", new BaseFactory(FacingEntFilter::new));
		registry.put("in", new BaseFactory(InFilter::new));
		registry.put("positioned", new BaseFactory(PositionedFilter::new));
		registry.put("rotated", new BaseFactory(RotatedFilter::new));
		registry.put("rotated as", new BaseFactory(RotatedAsFilter::new));
		registry.put("if", new BaseFactory(IfFilter::new));
		registry.put("unless", new BaseFactory(UnlessFilter::new));
	}
}

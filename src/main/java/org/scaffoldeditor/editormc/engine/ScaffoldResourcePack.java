package org.scaffoldeditor.editormc.engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.io.AssetManager;

import com.google.common.base.Charsets;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public class ScaffoldResourcePack implements ResourcePack {
	
	/**
	 * The Scaffold asset manager to use.
	 */
	public final AssetManager assetManager;
	
	public ScaffoldResourcePack(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public InputStream openRoot(String fileName) throws IOException {
		if ("pack.mcmeta".equals(fileName)) {
			String description = "Scaffold resources.";
			String pack = String.format("{\"pack\":{\"pack_format\":"+ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion())+",\"description\":\"%s\"}}", description);
			return IOUtils.toInputStream(pack, Charsets.UTF_8);
		} else if ("pack.png".equals(fileName)) {
			InputStream stream = FabricLoader.getInstance().getModContainer("fabric-resource-loader-v0")
					.flatMap(container -> container.getMetadata().getIconPath(512).map(container::getPath))
					.filter(Files::exists)
					.map(iconPath -> {
						try {
							return Files.newInputStream(iconPath);
						} catch (IOException e) {
							return null;
						}
					}).orElse(null);

			if (stream != null) {
				return stream;
			}
		}

		// ReloadableResourceManagerImpl gets away with FileNotFoundException.
		throw new FileNotFoundException("\"" + fileName + "\" in Scaffold resource pack");
	}
	

	@Override
	public InputStream open(ResourceType type, Identifier id) throws IOException {
		URL url = assetManager.getAsset(getScaffoldPath(id));
		if (url == null) {
			throw new FileNotFoundException(id.getPath());
		}
		return url.openStream();
	}
	
	private String getScaffoldPath(Identifier id) {
		return "assets/"+id.getNamespace()+"/"+id.getPath();
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth,
			Predicate<String> pathFilter) {
		Set<Identifier> set = new HashSet<>();
		if (type == ResourceType.CLIENT_RESOURCES) {
			List<URL> list;
			try {
				list = assetManager.getAssets("assets/");
			} catch (IOException e) {
				LogManager.getLogger().error("Couldn't get a list of Scaffold resources!", e);
				return set;
			}
			
			try {
				for (URL url : list) {
					URI uri = url.toURI();
					if (uri.getScheme().equals("file")) {
					}
				}
			} catch (URISyntaxException e) {
				LogManager.getLogger().error("Couldn't get a list of Scaffold resources!", e);
				return set;
			}

			
		} else {
			return new HashSet<>();
		}
		return set;	
		
	}

	@Override
	public boolean contains(ResourceType type, Identifier id) {
		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType type) {
		return null;
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return "Scaffold Project";
	}

	@Override
	public void close() {
	}

}

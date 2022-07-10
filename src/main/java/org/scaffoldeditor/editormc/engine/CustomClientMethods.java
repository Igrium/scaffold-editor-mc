package org.scaffoldeditor.editormc.engine;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;

public interface CustomClientMethods {
    void startScaffoldServer(LevelStorage.Session session, ResourcePackManager datapackManager, SaveLoader saveLoader);
}

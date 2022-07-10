package org.scaffoldeditor.editormc.engine.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;

@Mixin(World.class)
public interface WorldAccessor {

    @Invoker("getEntityLookup")
    EntityLookup<Entity> getEntityLookup();
}

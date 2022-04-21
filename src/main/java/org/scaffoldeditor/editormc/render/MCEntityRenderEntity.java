package org.scaffoldeditor.editormc.render;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3dc;
import org.scaffoldeditor.editormc.scaffold_interface.NBTConverter;
import org.scaffoldeditor.nbt.util.MCEntity;
import org.scaffoldeditor.scaffold.render.EntityRenderEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MCEntityRenderEntity extends MCRenderEntity implements EntityRenderEntity {

    Entity mcEntity;
    MCEntity scaffoldEntity;

    float pitch;
    float yaw;

    private Vector3dc position;
    private World world;

    public MCEntityRenderEntity(MCRenderEntityManager manager) {
        super(manager);
        world = manager.getWorld();
        enable();
    }

    protected void respawn() {
        if (!world.getServer().isOnThread()) {
            world.getServer().execute(this::respawn);
            return;
        }

        if (mcEntity != null) {
            despawn();
        }

        Optional<EntityType<?>> type = EntityType.get(scaffoldEntity.getID());
        EntityType<?> entType;
        if (type.isPresent()) {
            entType = type.get();
        } else {
            LogManager.getLogger().error("Unknown entity id: {}!", scaffoldEntity.getID());
            entType = EntityType.MARKER;
        }
        
        mcEntity = entType.create(world);
        world.spawnEntity(mcEntity);
    }

    protected void despawn() {
        if (!world.getServer().isOnThread()) {
            world.getServer().execute(this::despawn);
            return;
        }
        if (mcEntity != null) {
            mcEntity.remove(RemovalReason.DISCARDED);
            mcEntity = null;
        }
    }

    protected void update() {
        if (!world.getServer().isOnThread()) {
            world.getServer().execute(this::update);
            return;
        }

        if (scaffoldEntity == null) {
            despawn();
            return;
        }

        if (mcEntity == null || !EntityType.getId(mcEntity.getType()).toString().equals(scaffoldEntity.getID())) {
            respawn();
        }

        NbtCompound newNBT = NBTConverter.scaffoldCompoundToMinecraft(scaffoldEntity.getNBT());
        newNBT.putBoolean("NoAI", true);
        newNBT.putBoolean("NoGravity", true);
        newNBT.putBoolean("Silent", true);
        newNBT.putBoolean("Invulnerable", true);

        mcEntity.readNbt(newNBT);
        mcEntity.updatePositionAndAngles(position.x(), position.y(), position.z(), yaw, pitch);
    }

    @Override
    public void disable() {
        despawn();
    }

    @Override
    public void enable() {
        update();
    }

    @Override
    public Vector3dc getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3dc pos) {
        this.position = pos;
        update();
    }

    @Override
    public MCEntity getMCEntity() {
        return scaffoldEntity;
    }

    @Override
    public void setMCEntity(MCEntity entity) {
        scaffoldEntity = entity;
        update();
    }

    public float getYaw() {
        return yaw;
    }
    
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
}

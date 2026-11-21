package com.ticxo.modelengine.v26_2;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum NMSFields implements ReflectionUtils.ReflectionEnum {
   ENTITY_dimensions(Entity.class, "dimensions", "dimensions"),
   ENTITY_eyeHeight(Entity.class, "eyeHeight", "eyeHeight"),
   ENTITY_bukkitEntity(Entity.class, "bukkitEntity", "bukkitEntity"),
   LIVING_ENTITY_noJumpDelay(LivingEntity.class, "noJumpDelay", "noJumpDelay"),
   LIVING_ENTITY_jumping(LivingEntity.class, "jumping", "jumping"),
   MOB_lookControl(Mob.class, "lookControl", "lookControl"),
   MOB_moveControl(Mob.class, "moveControl", "moveControl"),
   MOB_navigation(Mob.class, "navigation", "navigation"),
   MOB_goalSelector(Mob.class, "goalSelector", "goalSelector"),
   MOB_bodyRotationControl(Mob.class, "bodyRotationControl", "bodyRotationControl"),
   SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating(ServerGamePacketListenerImpl.class, "clientIsFloating", "clientIsFloating"),
   MOVE_CONTROL_operation(MoveControl.class, "operation", "operation"),
   TRACKED_ENTITY_range(TrackedEntity.class, "range", "range"),
   LEVEL_soundSeedGenerator(Level.class, "soundSeedGenerator", "soundSeedGenerator");

   private final Class<?> target;
   private final String obfuscated;
   private final String mapped;
}

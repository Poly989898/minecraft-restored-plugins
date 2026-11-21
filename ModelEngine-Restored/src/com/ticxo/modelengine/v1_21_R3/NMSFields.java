package com.ticxo.modelengine.v1_21_R3;

import com.ticxo.modelengine.api.utils.ReflectionUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum NMSFields implements ReflectionUtils.ReflectionEnum {
   ENTITY_dimensions(Entity.class, "bb", "dimensions"),
   ENTITY_eyeHeight(Entity.class, "bc", "eyeHeight"),
   ENTITY_bukkitEntity(Entity.class, "bukkitEntity", "bukkitEntity"),
   LIVING_ENTITY_noJumpDelay(LivingEntity.class, "ce", "noJumpDelay"),
   LIVING_ENTITY_jumping(LivingEntity.class, "bm", "jumping"),
   MOB_lookControl(Mob.class, "bO", "lookControl"),
   MOB_moveControl(Mob.class, "bP", "moveControl"),
   MOB_navigation(Mob.class, "bR", "navigation"),
   MOB_goalSelector(Mob.class, "bS", "goalSelector"),
   MOB_bodyRotationControl(Mob.class, "ca", "bodyRotationControl"),
   SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating(ServerGamePacketListenerImpl.class, "I", "clientIsFloating"),
   SERVER_COMMON_PACKET_LISTENER_IMPL_connection(ServerCommonPacketListenerImpl.class, "e", "connection"),
   MOVE_CONTROL_operation(MoveControl.class, "k", "operation"),
   TRACKED_ENTITY_serverEntity(TrackedEntity.class, "b", "serverEntity"),
   TRACKED_ENTITY_range(TrackedEntity.class, "d", "range"),
   LEVEL_threadSafeRandom(Level.class, "f", "threadSafeRandom");

   private final Class<?> target;
   private final String obfuscated;
   private final String mapped;
}

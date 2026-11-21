package com.ticxo.modelengine.api.nms.entity;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Version-specific bridge to the server's entity internals (NMS). One implementation exists per supported
 * Minecraft version and is picked at startup; everything that cannot be done through the Bukkit API goes through here.
 */
public interface EntityHandler {
   int ON_MIN_X = 1;
   int ON_MAX_X = 2;
   int ON_MIN_Y = 4;
   int ON_MAX_Y = 8;
   int ON_MIN_Z = 16;
   int ON_MAX_Z = 32;

   /** Collects the box corners that lie on the faces selected by an {@code ON_*} bit mask. */
   static Set<EntityHandler.Point> getPoints(byte face) {
      EnumSet<EntityHandler.Point> set = EnumSet.noneOf(EntityHandler.Point.class);
      if ((face & 1) == 1) {
         set.add(EntityHandler.Point.CORNER_A);
         set.add(EntityHandler.Point.CORNER_E);
         set.add(EntityHandler.Point.CORNER_C);
         set.add(EntityHandler.Point.CORNER_G);
      }

      if ((face & 4) == 4) {
         set.add(EntityHandler.Point.CORNER_A);
         set.add(EntityHandler.Point.CORNER_E);
         set.add(EntityHandler.Point.CORNER_B);
         set.add(EntityHandler.Point.CORNER_F);
      }

      if ((face & 16) == 16) {
         set.add(EntityHandler.Point.CORNER_A);
         set.add(EntityHandler.Point.CORNER_C);
         set.add(EntityHandler.Point.CORNER_B);
         set.add(EntityHandler.Point.CORNER_D);
      }

      if ((face & 2) == 2) {
         set.add(EntityHandler.Point.CORNER_B);
         set.add(EntityHandler.Point.CORNER_F);
         set.add(EntityHandler.Point.CORNER_D);
         set.add(EntityHandler.Point.CORNER_H);
      }

      if ((face & 8) == 8) {
         set.add(EntityHandler.Point.CORNER_C);
         set.add(EntityHandler.Point.CORNER_G);
         set.add(EntityHandler.Point.CORNER_D);
         set.add(EntityHandler.Point.CORNER_H);
      }

      if ((face & 32) == 32) {
         set.add(EntityHandler.Point.CORNER_E);
         set.add(EntityHandler.Point.CORNER_G);
         set.add(EntityHandler.Point.CORNER_F);
         set.add(EntityHandler.Point.CORNER_H);
      }

      return set;
   }

   /**
    * Visits the grid points on the faces of an integer box that face the camera until the visitor returns true.
    *
    * @return true as soon as a visit returned true, false if none did or the box is empty
    */
   static boolean sweepCullPoints(
      int minX,
      int minY,
      int minZ,
      int maxX,
      int maxY,
      int maxZ,
      EntityHandler.BoxRelToCam relX,
      EntityHandler.BoxRelToCam relY,
      EntityHandler.BoxRelToCam relZ,
      EntityHandler.CullPointVisitor visitor
   ) {
      if (minX <= maxX && minY <= maxY && minZ <= maxZ) {
         boolean hasX = relX != EntityHandler.BoxRelToCam.INSIDE;
         boolean hasY = relY != EntityHandler.BoxRelToCam.INSIDE;
         boolean hasZ = relZ != EntityHandler.BoxRelToCam.INSIDE;
         int planeX = relX == EntityHandler.BoxRelToCam.POSITIVE ? minX : maxX + 1;
         int planeY = relY == EntityHandler.BoxRelToCam.POSITIVE ? minY : maxY + 1;
         int planeZ = relZ == EntityHandler.BoxRelToCam.POSITIVE ? minZ : maxZ + 1;
         if (hasX) {
            for (int y = minY; y <= maxY + 1; y++) {
               for (int z = minZ; z <= maxZ + 1; z++) {
                  if (visitor.visit(planeX, y, z)) {
                     return true;
                  }
               }
            }
         }

         if (hasY) {
            for (int x = minX; x <= maxX + 1; x++) {
               if (!hasX || x != planeX) {
                  for (int z = minZ; z <= maxZ + 1; z++) {
                     if (visitor.visit(x, planeY, z)) {
                        return true;
                     }
                  }
               }
            }
         }

         if (hasZ) {
            for (int x = minX; x <= maxX + 1; x++) {
               if (!hasX || x != planeX) {
                  for (int y = minY; y <= maxY + 1; y++) {
                     if ((!hasY || y != planeY) && visitor.visit(x, y, planeZ)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   /** Re-reads the block culling settings (force-render width and height, paper-clip method). */
   void updateConfig();

   /** Reserves a fresh entity id from the server's id counter. */
   int getNextEntityId();

   /** Sets the entity's dimensions (max width and height), eye height and bounding box from the hitbox. */
   void setHitbox(Entity entity, Hitbox hitbox);

   /** Sets the step height attribute of a living entity (no effect on other entities). */
   void setStepHeight(Entity entity, double height);

   /** Current step height of the entity. */
   double getStepHeight(Entity entity);

   /** Teleports the entity to the position without any of Bukkit's teleport handling. */
   void setPosition(Entity entity, double x, double y, double z);

   /** Places a passenger so that its vehicle attachment point lands at the given position. */
   void movePassenger(Entity entity, double x, double y, double z);

   /** Sends the spawn data for the entity to one player; does nothing if the player is null or the base is not a Bukkit entity. */
   void forceSpawn(BaseEntity<?> entity, Player player);

   /** Broadcasts the spawn packets to every player tracking the entity. */
   default void forceSpawn(Entity entity) {
      TrackedEntity tracked = this.wrapTrackedEntity(entity);
      if (tracked != null) {
         tracked.broadcastSpawn();
      }
   }

   /** Sends the removal of the entity to one player. */
   void forceDespawn(BaseEntity<?> entity, Player player);

   /** Broadcasts the removal to every player tracking the entity. */
   default void forceDespawn(Entity entity) {
      TrackedEntity tracked = this.wrapTrackedEntity(entity);
      if (tracked != null) {
         tracked.broadcastRemove();
      }
   }

   /** Marks the entity invisible for clients regardless of its real invisibility state. */
   void setForcedInvisible(Entity entity, boolean flag);

   default boolean isForcedInvisible(Entity entity) {
      return this.isForcedInvisible(entity.getUniqueId());
   }

   /** Whether the entity with this UUID is forced invisible. */
   boolean isForcedInvisible(UUID uuid);

   /**
    * Installs ModelEngine's body rotation controller on a mob and returns it.
    *
    * @param def supplies a standalone controller when the entity is not a mob or the vanilla one cannot be replaced
    */
   BodyRotationController wrapBodyRotationControl(Entity entity, Supplier<BodyRotationController> def);

   /**
    * Wraps a mob's move control so ModelEngine can drive it; returns the existing wrapper if already installed.
    *
    * @param def supplies a standalone controller when the entity is not a mob or the field cannot be replaced
    */
   MoveController wrapMoveController(Entity entity, Supplier<MoveController> def);

   /**
    * Wraps a mob's look control so ModelEngine can drive it; returns the existing wrapper if already installed.
    *
    * @param def supplies a standalone controller when the entity is not a mob or the field cannot be replaced
    */
   LookController wrapLookController(Entity entity, Supplier<LookController> def);

   /**
    * Replaces a mob's path navigation with a ModelEngine wrapper (ground, wall climber, flying, water bound and
    * amphibious navigation are supported; other types only log a warning). Does nothing for non-mobs.
    */
   void wrapNavigation(Entity entity);

   /**
    * Creates the entity that represents a sub-hitbox of a bone and queues it to be added to the world on the
    * entity's own thread (it is registered with the interaction tracker once added).
    *
    * @return the hitbox entity, or null if no world can be determined from the location or the bone's base entity
    */
   HitboxEntity createHitbox(Location location, ModelBone bone, SubHitbox subHitbox);

   /** Returns the entity as a hitbox entity if it is one, otherwise null. */
   @Nullable
   HitboxEntity castHitbox(Entity entity);

   /**
    * Damages the entity.
    *
    * @param source the server's {@code DamageSource}; passed as Object to keep this API version independent
    * @throws RuntimeException if {@code source} is not a damage source or the entity is not in a server level
    */
   boolean hurt(Entity entity, Object source, float amount);

   /** Makes the player interact with a living entity using the given hand (as a right click would); other entities give {@code FAIL}. */
   EntityHandler.InteractionResult interact(Entity entity, HumanEntity player, EquipmentSlot hand);

   /** Sends the packets that spawn a dynamic hitbox (a pivot with a mounted slime) to its player only. */
   void spawnDynamicHitbox(DynamicHitbox hitbox);

   /** Teleports a dynamic hitbox's pivot to its current tracked position, for its player only. */
   void updateDynamicHitbox(DynamicHitbox hitbox);

   /** Removes the entities of a dynamic hitbox from its player's client. */
   void destroyDynamicHitbox(DynamicHitbox hitbox);

   /** Makes the player use the item in the given hand, as if their client had sent a use-item packet. */
   void forceUseItem(Player player, EquipmentSlot hand);

   /** Body yaw in degrees. */
   float getYRot(Entity entity);

   /** Head yaw in degrees. */
   float getYHeadRot(Entity entity);

   /** Head pitch in degrees. */
   float getXHeadRot(Entity entity);

   /** Body yaw in degrees; for non-living entities this is the entity's yaw. */
   float getYBodyRot(Entity entity);

   void setYRot(Entity entity, float angle);

   void setYHeadRot(Entity entity, float angle);

   void setXHeadRot(Entity entity, float angle);

   void setYBodyRot(Entity entity, float angle);

   /** Moves the entity by the given offset with normal collision handling. */
   void move(Entity entity, double x, double y, double z);

   /** Whether the entity moved horizontally since the previous tick (false during its first tick). */
   boolean isWalking(Entity entity);

   /** Whether the entity is moving sideways. */
   boolean isStrafing(Entity entity);

   /** Whether the entity is jumping. */
   boolean isJumping(Entity entity);

   /** Whether the entity is flying. */
   boolean isFlying(Entity entity);

   float getHealth(Entity entity);

   float getMaxHealth(Entity entity);

   default AttributeAccessor createAttributeAccessor(Entity entity) {
      return () -> this.getMaxHealth(entity);
   }

   default Vector3f getPivotOffset(Entity entity) {
      return null;
   }

   default Float getCameraPivotHeight(Entity entity) {
      return null;
   }

   default Float getCameraEyeHeight(Entity entity) {
      return null;
   }

   /** Whether the entity was removed from its world. */
   boolean isRemoved(Entity entity);

   /** Colour of the entity's team, used for its glow outline. */
   int getGlowColor(Entity entity);

   /** Sets how many ticks the entity has been dead; living entities only. */
   void setDeathTick(Entity entity, int tick);

   /** Wraps the server's tracker entry of the entity, which decides which players see it. */
   TrackedEntity wrapTrackedEntity(Entity entity);

   /**
    * Occlusion test: whether the box is completely hidden from the player's eye position by solid blocks.
    * Boxes at least as wide or tall as the configured force-render size are never culled.
    */
   boolean shouldCull(Player player, Location eyePosition, BoundingBox box);

   default int getEntityId(Entity entity) {
      return entity.getEntityId();
   }

   default boolean isGlowing(Entity entity) {
      return entity.isGlowing();
   }

   default boolean isVisualFire(Entity entity) {
      return entity.isVisualFire() || entity.getFireTicks() > 0;
   }

   default int getFireTicks(Entity entity) {
      return entity.getFireTicks();
   }

   default BoundingBox getBoundingBox(Entity entity) {
      return entity.getBoundingBox();
   }

   default void getBoundingBox(Entity entity, BoundingBox dest) {
      BoundingBox box = this.getBoundingBox(entity);
      dest.resize(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
   }

   default Set<UUID> getCollidableExemptions(Entity entity) {
      return entity instanceof LivingEntity living ? living.getCollidableExemptions() : null;
   }

   default Component getCustomName(Entity entity) {
      return entity.customName();
   }

   default void readEntityState(Entity entity, EntityState state) {
      state.yRot = this.getYRot(entity);
      state.yHeadRot = this.getYHeadRot(entity);
      state.xHeadRot = this.getXHeadRot(entity);
      state.yBodyRot = this.getYBodyRot(entity);
      state.walking = this.isWalking(entity);
      state.strafing = this.isStrafing(entity);
      state.jumping = this.isJumping(entity);
      state.flying = this.isFlying(entity);
      state.health = this.getHealth(entity);
      state.removed = this.isRemoved(entity);
      state.glowing = this.isGlowing(entity);
      state.onFire = this.isVisualFire(entity);
      this.getBoundingBox(entity, state.boundingBox);
   }

   default void getLocation(Entity entity, Location location) {
      entity.getLocation(location);
   }

   /** Where a box lies relative to the camera along one axis. */
   enum BoxRelToCam {
      INSIDE,
      POSITIVE,
      NEGATIVE;

      public static EntityHandler.BoxRelToCam from(int min, int max, int pos) {
         if (max > pos && min > pos) {
            return POSITIVE;
         } else {
            return min < pos && max < pos ? NEGATIVE : INSIDE;
         }
      }
   }

   @FunctionalInterface
   interface CullPointVisitor {
      boolean visit(int value, int value2, int value3);
   }

   /** Outcome of {@link #interact}, mirroring the server's interaction results. */
   enum InteractionResult {
      SUCCESS,
      SUCCESS_NO_ITEM_USED,
      CONSUME,
      CONSUME_PARTIAL,
      PASS,
      FAIL;
   }

   /** Sample points on a unit box: its eight corners and the centre of each face. */
   @AllArgsConstructor(access = AccessLevel.PACKAGE)
   enum Point {
      CORNER_A(0.0F, 0.0F, 0.0F),
      CORNER_B(1.0F, 0.0F, 0.0F),
      CORNER_C(0.0F, 1.0F, 0.0F),
      CORNER_D(1.0F, 1.0F, 0.0F),
      CORNER_E(0.0F, 0.0F, 1.0F),
      CORNER_F(1.0F, 0.0F, 1.0F),
      CORNER_G(0.0F, 1.0F, 1.0F),
      CORNER_H(1.0F, 1.0F, 1.0F),
      FACE_NX(0.0F, 0.5F, 0.5F),
      FACE_PX(1.0F, 0.5F, 0.5F),
      FACE_NY(0.5F, 0.0F, 0.5F),
      FACE_PY(0.5F, 1.0F, 0.5F),
      FACE_NZ(0.5F, 0.5F, 0.0F),
      FACE_PZ(0.5F, 0.5F, 1.0F);

      public final float x;
      public final float y;
      public final float z;
   }
}

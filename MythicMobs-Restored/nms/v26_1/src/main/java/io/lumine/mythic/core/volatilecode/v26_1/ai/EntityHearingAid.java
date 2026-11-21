package io.lumine.mythic.core.volatilecode.v26_1.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MobEars;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Data;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Listener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Ticker;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User;
import org.bukkit.craftbukkit.entity.CraftEntity;

public class EntityHearingAid implements MobEars, VibrationSystem {
   private Data vibrationData;
   private User vibrationUser;
   private final DynamicGameEventListener<Listener> eventListener;
   private final ActiveMob activeMob;
   private final Entity nmsEntity;

   public EntityHearingAid(ActiveMob am) {
      this.activeMob = am;
      org.bukkit.entity.Entity bukkitEntity = BukkitAdapter.adapt(am.getEntity());
      this.nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      this.vibrationData = new Data();
      this.vibrationUser = new EntityHearingAid.VibrationUser();
      this.eventListener = new DynamicGameEventListener(new Listener(this));
   }

   @Override
   public void tick() {
      Ticker.tick(this.nmsEntity.level(), this.vibrationData, this.vibrationUser);
      this.eventListener.move((ServerLevel)this.nmsEntity.level());
   }

   public void updateEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> biconsumer) {
      if (this.nmsEntity.level() instanceof ServerLevel serverLevel) {
         biconsumer.accept(this.eventListener, serverLevel);
      }
   }

   @Generated
   public Data getVibrationData() {
      return this.vibrationData;
   }

   @Generated
   public User getVibrationUser() {
      return this.vibrationUser;
   }

   @Generated
   public DynamicGameEventListener<Listener> getEventListener() {
      return this.eventListener;
   }

   @Generated
   public ActiveMob getActiveMob() {
      return this.activeMob;
   }

   @Generated
   public Entity getNmsEntity() {
      return this.nmsEntity;
   }

   @Generated
   public void setVibrationData(Data vibrationData) {
      this.vibrationData = vibrationData;
   }

   @Generated
   public void setVibrationUser(User vibrationUser) {
      this.vibrationUser = vibrationUser;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EntityHearingAid other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$vibrationData = this.getVibrationData();
         Object other$vibrationData = other.getVibrationData();
         if (this$vibrationData == null ? other$vibrationData == null : this$vibrationData.equals(other$vibrationData)) {
            Object this$vibrationUser = this.getVibrationUser();
            Object other$vibrationUser = other.getVibrationUser();
            if (this$vibrationUser == null ? other$vibrationUser == null : this$vibrationUser.equals(other$vibrationUser)) {
               Object this$eventListener = this.getEventListener();
               Object other$eventListener = other.getEventListener();
               if (this$eventListener == null ? other$eventListener == null : this$eventListener.equals(other$eventListener)) {
                  Object this$activeMob = this.getActiveMob();
                  Object other$activeMob = other.getActiveMob();
                  if (this$activeMob == null ? other$activeMob == null : this$activeMob.equals(other$activeMob)) {
                     Object this$nmsEntity = this.getNmsEntity();
                     Object other$nmsEntity = other.getNmsEntity();
                     return this$nmsEntity == null ? other$nmsEntity == null : this$nmsEntity.equals(other$nmsEntity);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof EntityHearingAid;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $vibrationData = this.getVibrationData();
      result = result * 59 + ($vibrationData == null ? 43 : $vibrationData.hashCode());
      Object $vibrationUser = this.getVibrationUser();
      result = result * 59 + ($vibrationUser == null ? 43 : $vibrationUser.hashCode());
      Object $eventListener = this.getEventListener();
      result = result * 59 + ($eventListener == null ? 43 : $eventListener.hashCode());
      Object $activeMob = this.getActiveMob();
      result = result * 59 + ($activeMob == null ? 43 : $activeMob.hashCode());
      Object $nmsEntity = this.getNmsEntity();
      return result * 59 + ($nmsEntity == null ? 43 : $nmsEntity.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "EntityHearingAid(vibrationData="
         + this.getVibrationData()
         + ", vibrationUser="
         + this.getVibrationUser()
         + ", eventListener="
         + this.getEventListener()
         + ", activeMob="
         + this.getActiveMob()
         + ", nmsEntity="
         + this.getNmsEntity()
         + ")";
   }

   private class VibrationUser implements User {
      private final int listenerRadius = 16;
      private final PositionSource positionSource = new EntityPositionSource(EntityHearingAid.this.nmsEntity, EntityHearingAid.this.nmsEntity.getEyeHeight());

      public VibrationUser() {
      }

      public boolean canReceiveVibration(ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, Context context) {
         if (EntityHearingAid.this.nmsEntity instanceof Mob mob) {
            if (!mob.isNoAi() && !mob.isDeadOrDying() && serverLevel.getWorldBorder().isWithinBounds(blockPos)) {
               if (context.sourceEntity() instanceof LivingEntity livingEntity) {
                  return this.canTargetEntity(livingEntity);
               } else {
                  return context.sourceEntity() instanceof Projectile projectile ? projectile.getOwner() != EntityHearingAid.this.nmsEntity : false;
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }

      public void onReceiveVibration(
         ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, @Nullable Entity entity, @Nullable Entity projectile, float volume
      ) {
         AbstractWorld world = BukkitAdapter.adapt(serverLevel.getWorld());
         AbstractVector sourcePosition = new AbstractVector(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         AbstractEntity sourceEntity = entity == null ? null : BukkitAdapter.adapt(entity.getBukkitEntity());
         AbstractEntity triggeringEntity = projectile == null ? null : BukkitAdapter.adapt(projectile.getBukkitEntity());
         String soundType = BuiltInRegistries.GAME_EVENT.getKey(holder.value()).getPath();
         EntityHearingAid.this.handleDetection(EntityHearingAid.this.activeMob, world, sourcePosition, sourceEntity, triggeringEntity, soundType, volume);
      }

      public boolean canTargetEntity(@Nullable Entity entity) {
         return entity instanceof LivingEntity entityliving
            && EntityHearingAid.this.nmsEntity.level() == entity.level()
            && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)
            && !EntityHearingAid.this.nmsEntity.isAlliedTo(entity)
            && entityliving.getType() != EntityType.ARMOR_STAND
            && entityliving.getType() != EntityType.WARDEN
            && !entityliving.isInvulnerable()
            && !entityliving.isDeadOrDying()
            && EntityHearingAid.this.nmsEntity.level().getWorldBorder().isWithinBounds(entityliving.getBoundingBox());
      }

      @Generated
      public int getListenerRadius() {
         return 16;
      }

      @Generated
      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof EntityHearingAid.VibrationUser other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getListenerRadius() != other.getListenerRadius()) {
            return false;
         } else {
            Object this$positionSource = this.getPositionSource();
            Object other$positionSource = other.getPositionSource();
            return this$positionSource == null ? other$positionSource == null : this$positionSource.equals(other$positionSource);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof EntityHearingAid.VibrationUser;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getListenerRadius();
         Object $positionSource = this.getPositionSource();
         return result * 59 + ($positionSource == null ? 43 : $positionSource.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "EntityHearingAid.VibrationUser(listenerRadius=" + this.getListenerRadius() + ", positionSource=" + this.getPositionSource() + ")";
      }
   }
}

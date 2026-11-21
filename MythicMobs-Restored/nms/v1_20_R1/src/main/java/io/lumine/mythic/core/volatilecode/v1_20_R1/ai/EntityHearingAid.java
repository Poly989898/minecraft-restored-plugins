package io.lumine.mythic.core.volatilecode.v1_20_R1.ai;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MobEars;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import lombok.Generated;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.a;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.b;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.c;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem.d;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

public class EntityHearingAid implements MobEars, VibrationSystem {
   private a vibrationData;
   private d vibrationUser;
   private final DynamicGameEventListener<b> eventListener;
   private final ActiveMob activeMob;
   private final Entity nmsEntity;

   public EntityHearingAid(ActiveMob am) {
      this.activeMob = am;
      org.bukkit.entity.Entity bukkitEntity = BukkitAdapter.adapt(am.getEntity());
      this.nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      this.vibrationData = new a();
      this.vibrationUser = new EntityHearingAid.VibrationUser();
      this.eventListener = new DynamicGameEventListener(new b(this));
   }

   @Override
   public void tick() {
      c.a(this.nmsEntity.dI(), this.vibrationData, this.vibrationUser);
      this.eventListener.c((WorldServer)this.nmsEntity.dI());
   }

   public void updateEventListener(BiConsumer<DynamicGameEventListener<?>, WorldServer> biconsumer) {
      if (this.nmsEntity.dI() instanceof WorldServer serverLevel) {
         biconsumer.accept(this.eventListener, serverLevel);
      }
   }

   @Generated
   public a gb() {
      return this.vibrationData;
   }

   @Generated
   public d gc() {
      return this.vibrationUser;
   }

   @Generated
   public DynamicGameEventListener<b> getEventListener() {
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
   public void setVibrationData(a vibrationData) {
      this.vibrationData = vibrationData;
   }

   @Generated
   public void setVibrationUser(d vibrationUser) {
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
         Object this$vibrationData = this.gb();
         Object other$vibrationData = other.gb();
         if (this$vibrationData == null ? other$vibrationData == null : this$vibrationData.equals(other$vibrationData)) {
            Object this$vibrationUser = this.gc();
            Object other$vibrationUser = other.gc();
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
      Object $vibrationData = this.gb();
      result = result * 59 + ($vibrationData == null ? 43 : $vibrationData.hashCode());
      Object $vibrationUser = this.gc();
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
         + this.gb()
         + ", vibrationUser="
         + this.gc()
         + ", eventListener="
         + this.getEventListener()
         + ", activeMob="
         + this.getActiveMob()
         + ", nmsEntity="
         + this.getNmsEntity()
         + ")";
   }

   private class VibrationUser implements d {
      private final int listenerRadius = 16;
      private final PositionSource positionSource = new EntityPositionSource(EntityHearingAid.this.nmsEntity, EntityHearingAid.this.nmsEntity.cF());

      public VibrationUser() {
      }

      public boolean a(WorldServer serverLevel, BlockPosition blockPos, GameEvent gameEvent, net.minecraft.world.level.gameevent.GameEvent.a context) {
         if (EntityHearingAid.this.nmsEntity instanceof EntityInsentient mob) {
            if (!mob.fQ() && !mob.es() && serverLevel.w_().a(blockPos)) {
               if (context.a() instanceof EntityLiving livingEntity) {
                  return this.canTargetEntity(livingEntity);
               } else {
                  return context.a() instanceof IProjectile projectile ? projectile.v() != EntityHearingAid.this.nmsEntity : false;
               }
            } else {
               return false;
            }
         } else {
            return true;
         }
      }

      public void a(WorldServer serverLevel, BlockPosition blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity projectile, float volume) {
         AbstractWorld world = BukkitAdapter.adapt(serverLevel.getWorld());
         AbstractVector sourcePosition = new AbstractVector(blockPos.u(), blockPos.v(), blockPos.w());
         AbstractEntity sourceEntity = entity == null ? null : BukkitAdapter.adapt(entity.getBukkitEntity());
         AbstractEntity triggeringEntity = projectile == null ? null : BukkitAdapter.adapt(projectile.getBukkitEntity());
         String soundType = BuiltInRegistries.b.b(gameEvent).a();
         EntityHearingAid.this.handleDetection(EntityHearingAid.this.activeMob, world, sourcePosition, sourceEntity, triggeringEntity, soundType, volume);
      }

      public boolean canTargetEntity(@Nullable Entity entity) {
         return entity instanceof EntityLiving entityliving
            && EntityHearingAid.this.nmsEntity.dI() == entity.dI()
            && IEntitySelector.e.test(entity)
            && !EntityHearingAid.this.nmsEntity.p(entity)
            && entityliving.ae() != EntityTypes.d
            && entityliving.ae() != EntityTypes.bi
            && !entityliving.co()
            && !entityliving.es()
            && EntityHearingAid.this.nmsEntity.dI().w_().a(entityliving.cE());
      }

      @Generated
      public int a() {
         return 16;
      }

      @Generated
      public PositionSource b() {
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
         } else if (this.a() != other.a()) {
            return false;
         } else {
            Object this$positionSource = this.b();
            Object other$positionSource = other.b();
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
         result = result * 59 + this.a();
         Object $positionSource = this.b();
         return result * 59 + ($positionSource == null ? 43 : $positionSource.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "EntityHearingAid.VibrationUser(listenerRadius=" + this.a() + ", positionSource=" + this.b() + ")";
      }
   }
}

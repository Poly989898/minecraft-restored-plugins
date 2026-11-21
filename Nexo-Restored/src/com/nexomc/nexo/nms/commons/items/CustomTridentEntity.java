package com.nexomc.nexo.nms.commons.items;

import com.nexomc.nexo.mechanics.trident.ICustomTridentEntity;
import com.nexomc.nexo.mechanics.trident.TridentMechanic;
import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.nms.commons.CommonNMSHandler;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import kotlin.Metadata;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\b\u0010\u0016\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\u0012H\u0014J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\u0014\u0010\u001a\u001a\u00020\u00192\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0096\u0082\u0004J\n\u0010\u001d\u001a\u00020\u001eH\u0096\u0080\u0004R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/items/CustomTridentEntity;", "Lnet/minecraft/world/entity/Display$ItemDisplay;", "Lcom/nexomc/nexo/mechanics/trident/ICustomTridentEntity;", "_trident", "Lorg/bukkit/entity/Trident;", "mechanic", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "<init>", "(Lorg/bukkit/entity/Trident;Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "trident", "Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "spawn", "Lorg/bukkit/entity/Entity;", "location", "Lorg/bukkit/Location;", "tick", "", "addBubbleParticles", "pos", "Lnet/minecraft/world/phys/Vec3;", "setRotations", "handlePortal", "dismountsUnderwater", "", "equals", "other", "", "hashCode", "", "com.nexomc:commons"}
)
public final class CustomTridentEntity extends Display.ItemDisplay implements ICustomTridentEntity {
   @NotNull
   private final TridentMechanic mechanic;
   @NotNull
   private final ITridentWrapper trident;

   public CustomTridentEntity(@NotNull Trident _trident, @NotNull TridentMechanic mechanic) {
      EntityType itemDisplayType = EntityUtilsKt.getEntityUtils().itemDisplayType();
      Object world = _trident.getWorld();
      if (world == null) {
         throw new NullPointerException("null cannot be cast to non-null type org.bukkit.craftbukkit.CraftWorld");
      }
      super(itemDisplayType, (Level)((CraftWorld)world).getHandle());
      this.mechanic = mechanic;
      NMSHandler handler = NMSHandlers.handler();
      this.trident = ((CommonNMSHandler)handler).tridentWrapper(_trident);
      this.persist = false;
      this.setWidth(0.0F);
      this.setHeight(0.0F);
      this.setTransformationInterpolationDuration(1);
      this.setTransformationInterpolationDelay(1);
      super.entityData.set(new EntityDataAccessor(10, EntityDataSerializers.INT), 1);
      this.setItemTransform((ItemDisplayContext)ItemDisplayContext.BY_ID.apply(this.mechanic.getTransform().ordinal()));
      this.setItemStack(this.trident.getPickupItem());
   }

   @NotNull
   public final TridentMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public Entity spawn(@NotNull Location location) {
      this.setPosRaw(location.getX(), location.getY(), location.getZ());
      this.setRotations();
      World world = location.getWorld();
      ((CraftWorld)world).getHandle().addFreshEntity((net.minecraft.world.entity.Entity)this, SpawnReason.CUSTOM);
      CraftEntity bukkitEntity = this.getBukkitEntity();
      return (Entity)bukkitEntity;
   }

   public void tick() {
      super.tick();
      if (this.trident.isRemoved()) {
         this.remove(RemovalReason.DISCARDED);
      }

      this.setPosRaw(this.trident.getX(), this.trident.getY(), this.trident.getZ());
      this.setRotations();
      if (!this.trident.isInGround() && this.trident.isInWater()) {
         this.addBubbleParticles(this.trident.getPosition());
      }

   }

   private final void addBubbleParticles(Vec3 pos) {
      for(int i = 1; i < 5; ++i) {
         double offsetX = this.getDeltaMovement().x * (double)0.25F * (double)i;
         double offsetY = this.getDeltaMovement().y * (double)0.25F * (double)i;
         double offsetZ = this.getDeltaMovement().z * (double)0.25F * (double)i;
         this.level().getWorld().spawnParticle(Particle.BUBBLE, pos.x - offsetX, pos.y - offsetY, pos.z - offsetZ, 1, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, (double)0.0F);
      }

   }

   private final void setRotations() {
      float yRot = UtilsKt.getRadians(this.trident.getYRot() + this.mechanic.getRotation().y);
      float xRot = -UtilsKt.getRadians(this.trident.getXRot() + this.mechanic.getRotation().x);
      Quaternionf leftRot = (new Quaternionf()).rotationYXZ(yRot, xRot, 0.0F);
      ITridentWrapper iTridentWrapper = this.trident;
      this.setTransformation(iTridentWrapper.transformation(leftRot));
   }

   protected void handlePortal() {
   }

   public boolean dismountsUnderwater() {
      return false;
   }

   public boolean equals(@Nullable Object other) {
      return other == null ? false : super.equals(other);
   }

   public int hashCode() {
      return super.hashCode();
   }
}

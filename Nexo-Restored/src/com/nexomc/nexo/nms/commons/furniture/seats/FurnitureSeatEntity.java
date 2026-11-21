package com.nexomc.nexo.nms.commons.furniture.seats;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.mechanics.furniture.seats.IFurnitureSeatEntity;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\f\u0010\u0011\u001a\u00060\u0012j\u0002`\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0014J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\u0019H\u0014J\b\u0010\u001b\u001a\u00020\u0015H\u0016J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0016\u001a\u00020\u001eH\u0016J\u0014\u0010\u001f\u001a\u00020\u00192\b\u0010 \u001a\u0004\u0018\u00010!H\u0096\u0082\u0004J\n\u0010\"\u001a\u00020#H\u0096\u0080\u0004R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0005\u001a\u00020\u0006X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0007\u001a\u00020\bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006$"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/seats/FurnitureSeatEntity;", "Lnet/minecraft/world/entity/Interaction;", "Lcom/nexomc/nexo/mechanics/furniture/seats/IFurnitureSeatEntity;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "seat", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getSeat", "()Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "spawn", "Lorg/bukkit/entity/Interaction;", "Lcom/nexomc/nexo/mechanics/furniture/seats/BukkitInteraction;", "addPassenger", "", "passenger", "Lnet/minecraft/world/entity/Entity;", "removePassenger", "", "suppressCancellation", "tick", "getDismountLocationForPassenger", "Lnet/minecraft/world/phys/Vec3;", "Lnet/minecraft/world/entity/LivingEntity;", "equals", "other", "", "hashCode", "", "com.nexomc:commons"}
)
public final class FurnitureSeatEntity extends Interaction implements IFurnitureSeatEntity {
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final FurnitureSeat seat;

   public FurnitureSeatEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
      EntityType interactionType = EntityUtilsKt.getEntityUtils().interactionType();
      World world = baseEntity.getWorld();
      super(interactionType, (Level)((CraftWorld)world).getHandle());
      this.baseEntity = baseEntity;
      this.mechanic = mechanic;
      this.seat = seat;
      this.persist = false;
      this.setWidth(0.1F);
      this.setHeight(0.1F);
   }

   @NotNull
   public ItemDisplay getBaseEntity() {
      return this.baseEntity;
   }

   @NotNull
   public FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public FurnitureSeat getSeat() {
      return this.seat;
   }

   @NotNull
   public org.bukkit.entity.Interaction spawn() {
      Location location = this.getSeatLocation();
      this.setPos(location.getX(), location.getY(), location.getZ());
      World world = location.getWorld();
      ((CraftWorld)world).getHandle().addFreshEntity((Entity)this);
      this.getBukkitEntity().getPersistentDataContainer().set(FurnitureSeat.Companion.getSEAT_KEY(), DataType.UUID, this.uuid);
      CraftEntity bukkitEntity = this.getBukkitEntity();
      return (org.bukkit.entity.Interaction)bukkitEntity;
   }

   protected void addPassenger(@NotNull Entity passenger) {
      super.addPassenger(passenger);
      CraftPlayer craftPlayer = (passenger instanceof ServerPlayer ? (ServerPlayer)passenger : null) != null ? (passenger instanceof ServerPlayer ? (ServerPlayer)passenger : null).getBukkitEntity() : null;
      Player player2 = craftPlayer instanceof Player ? (Player)craftPlayer : null;
      if ((craftPlayer instanceof Player ? (Player)craftPlayer : null) != null) {
         Player player = player2;
         SchedulerUtils.INSTANCE.runTaskLater(4L, (() -> FurnitureSeatEntity.addPassenger$lambda$0(this, player)));
      }
   }

   protected boolean removePassenger(@NotNull Entity passenger, boolean suppressCancellation) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      if (!super.removePassenger(passenger, suppressCancellation)) {
         return false;
      } else {
         CraftPlayer craftPlayer = (passenger instanceof ServerPlayer ? (ServerPlayer)passenger : null) != null ? (passenger instanceof ServerPlayer ? (ServerPlayer)passenger : null).getBukkitEntity() : null;
         Player player2 = craftPlayer instanceof Player ? (Player)craftPlayer : null;
         if ((craftPlayer instanceof Player ? (Player)craftPlayer : null) == null) {
            return true;
         } else {
            Player player = player2;
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

            while(true) {
               if (iterator.hasNext()) {
                  MechanicFactory it = (MechanicFactory)iterator.next();
                  int value = 0;
                  MechanicFactory mechanicFactory2 = it;
                  if (!(it instanceof FurnitureFactory)) {
                     mechanicFactory2 = null;
                  }

                  it = (FurnitureFactory)mechanicFactory2;
                  if (it == null) {
                     continue;
                  }

                  mechanicFactory = it;
                  break;
               }

               mechanicFactory = null;
               break;
            }

            FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
            if (furnitureFactory != null) {
               IFurniturePacketManager packetManager = furnitureFactory.packetManager();
               if (packetManager != null) {
                  packetManager.sendBarrierHitboxPacket(this.getBaseEntity(), this.getMechanic(), player);
               }
            }

            if (!Bukkit.getServer().isStopping()) {
               SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
               CraftEntity bukkitEntity = this.getBukkitEntity();
               schedulerUtils.runAtEntityLater((org.bukkit.entity.Entity)bukkitEntity, 1L, (a0_ -> FurnitureSeatEntity.removePassenger$lambda$0(this, (org.bukkit.entity.Entity)a0_)));
            }

            return true;
         }
      }
   }

   public void tick() {
      if (this.getBaseEntity().isDead()) {
         this.remove(RemovalReason.DISCARDED);
      }

   }

   @NotNull
   public Vec3 getDismountLocationForPassenger(@NotNull LivingEntity passenger) {
      return new Vec3(this.getX(), this.getY() + (double)1, this.getZ());
   }

   public boolean equals(@Nullable Object other) {
      return other == null ? false : super.equals(other);
   }

   public int hashCode() {
      return super.hashCode();
   }

   private static final Unit addPassenger$lambda$0(FurnitureSeatEntity furnitureSeatEntity, Player player) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      FurnitureFactory furnitureFactory = (FurnitureFactory)mechanicFactory;
      if (furnitureFactory != null) {
         IFurniturePacketManager packetManager = furnitureFactory.packetManager();
         if (packetManager != null) {
            packetManager.removeBarrierHitboxPacket(furnitureSeatEntity.getBaseEntity(), furnitureSeatEntity.getMechanic(), player);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit removePassenger$lambda$0(FurnitureSeatEntity furnitureSeatEntity, org.bukkit.entity.Entity it) {
      furnitureSeatEntity.remove(RemovalReason.DISCARDED);
      return Unit.INSTANCE;
   }
}

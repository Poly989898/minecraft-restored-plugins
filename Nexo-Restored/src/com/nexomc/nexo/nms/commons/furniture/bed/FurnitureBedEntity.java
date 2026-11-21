package com.nexomc.nexo.nms.commons.furniture.bed;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedEntity;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.commons.EntityUtilsKt;
import java.util.UUID;
import kotlin.Metadata;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u00002\u00020\u00012\u00020\u0002B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0014J\u0018\u0010!\u001a\u00020\"2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010#\u001a\u00020\"H\u0014J\b\u0010$\u001a\u00020\u001eH\u0016J\b\u0010%\u001a\u00020\u001eH\u0014J\b\u0010&\u001a\u00020\"H\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\u00020\u00168VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "Lnet/minecraft/world/entity/Interaction;", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedEntity;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "<init>", "(Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;)V", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getBed", "()Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "seatPlayer", "Lorg/bukkit/entity/Player;", "bedPacket", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bukkitEntity", "Lorg/bukkit/entity/Entity;", "getBukkitEntity", "()Lorg/bukkit/entity/Entity;", "spawn", "Ljava/util/UUID;", "location", "Lorg/bukkit/Location;", "addPassenger", "", "passenger", "Lnet/minecraft/world/entity/Entity;", "removePassenger", "", "suppressCancellation", "tick", "handlePortal", "dismountsUnderwater", "com.nexomc:commons"}
)
public final class FurnitureBedEntity extends Interaction implements IFurnitureBedEntity {
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final FurnitureBed bed;
   @Nullable
   private Player seatPlayer;
   @Nullable
   private IFurnitureBedPacket bedPacket;

   public FurnitureBedEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureBed bed) {
      EntityType interactionType = EntityUtilsKt.getEntityUtils().interactionType();
      World world = baseEntity.getWorld();
      super(interactionType, (Level)((CraftWorld)world).getHandle());
      this.baseEntity = baseEntity;
      this.mechanic = mechanic;
      this.bed = bed;
      this.persist = false;
      this.setWidth(0.1F);
      this.setHeight(0.1F);
      this.yRotO = this.getYRot();
      this.setYBodyRot(this.yRotO);
   }

   @NotNull
   public final ItemDisplay getBaseEntity() {
      return this.baseEntity;
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public final FurnitureBed getBed() {
      return this.bed;
   }

   @NotNull
   public CraftEntity getBukkitEntity() {
      CraftEntity bukkitEntity = super.getBukkitEntity();
      return bukkitEntity;
   }

   @NotNull
   public UUID spawn(@NotNull Location location) {
      this.setPos(location.getX(), location.getY(), location.getZ());
      World world = location.getWorld();
      ((CraftWorld)world).getHandle().moonrise$getEntityLookup().addNewEntity((net.minecraft.world.entity.Entity)this, false);
      this.getBukkitEntity().getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING, this.mechanic.getItemID());
      this.getBukkitEntity().getPersistentDataContainer().set(FurnitureBed.Companion.getBED_KEY(), DataType.UUID, this.uuid);
      UUID uuid2 = this.uuid;
      return uuid2;
   }

   protected void addPassenger(@NotNull net.minecraft.world.entity.Entity passenger) {
      CraftEntity bukkitEntity = passenger.getBukkitEntity();
      Player player2 = bukkitEntity instanceof Player ? (Player)bukkitEntity : null;
      if ((bukkitEntity instanceof Player ? (Player)bukkitEntity : null) != null) {
         Player player = player2;
         super.addPassenger(passenger);
         this.seatPlayer = player;
         IFurnitureBedPacket iFurnitureBedPacket = EntityUtilsKt.getEntityUtils().createBedPacket(player, this.baseEntity, this, this.bed);
         int value = 0;
         iFurnitureBedPacket.spawn();
         this.bedPacket = iFurnitureBedPacket;
      }
   }

   protected boolean removePassenger(@NotNull net.minecraft.world.entity.Entity passenger, boolean suppressCancellation) {
      if (!super.removePassenger(passenger, suppressCancellation)) {
         return false;
      } else {
         this.dismountTo(this.getX(), this.getY(), this.getZ());
         IFurnitureBedPacket iFurnitureBedPacket = this.bedPacket;
         if (iFurnitureBedPacket != null) {
            iFurnitureBedPacket.remove();
         }

         this.bedPacket = null;
         this.getBukkitEntity().remove();
         return true;
      }
   }

   public void tick() {
      IFurnitureBedPacket iFurnitureBedPacket = this.bedPacket;
      if (iFurnitureBedPacket != null) {
         iFurnitureBedPacket.refresh();
      }

      if (!this.isAlive() || !this.valid) {
         ;
      }
   }

   protected void handlePortal() {
   }

   public boolean dismountsUnderwater() {
      return false;
   }
}

package com.nexomc.nexo.nms.v1_21_R10.furniture.bed;

import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedEntity;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0017J\u0006\u0010\u0018\u001a\u00020\u0013J\u0006\u0010\u0019\u001a\u00020\u0013J\b\u0010\f\u001a\u00020\rH\u0002J\b\u0010\u001a\u001a\u00020\u0013H\u0016J\b\u0010\u001b\u001a\u00020\u0013H\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R10/furniture/bed/FurnitureBedMannequinEntity;", "Lnet/minecraft/world/entity/decoration/Mannequin;", "bedPlayer", "Lnet/minecraft/server/level/ServerPlayer;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "level", "Lnet/minecraft/server/level/ServerLevel;", "<init>", "(Lnet/minecraft/server/level/ServerPlayer;Lorg/bukkit/entity/ItemDisplay;Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;Lnet/minecraft/server/level/ServerLevel;)V", "headFacing", "Lnet/minecraft/core/Direction;", "getHeadFacing", "()Lnet/minecraft/core/Direction;", "headRotCache", "", "spawn", "", "sleepingPos", "Lnet/minecraft/core/BlockPos;", "positionSyncPacket", "Lnet/minecraft/network/protocol/game/ClientboundTeleportEntityPacket;", "syncPlayerState", "updateHeadRotation", "tick", "handlePortal", "Companion", "com.nexomc:v1_21_R10"}
)
public final class FurnitureBedMannequinEntity extends Mannequin {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ServerPlayer bedPlayer;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final FurnitureBedEntity bedEntity;
   @NotNull
   private final Direction headFacing;
   private float headRotCache;
   @NotNull
   private static final List<EquipmentSlot> HUMANOID_SLOTS;

   public FurnitureBedMannequinEntity(@NotNull ServerPlayer bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull ServerLevel level) {
      Intrinsics.checkNotNullParameter(bedPlayer, "bedPlayer");
      Intrinsics.checkNotNullParameter(baseEntity, "baseEntity");
      Intrinsics.checkNotNullParameter(bedEntity, "bedEntity");
      Intrinsics.checkNotNullParameter(level, "level");
      super(EntityType.MANNEQUIN, (Level)level);
      this.bedPlayer = bedPlayer;
      this.baseEntity = baseEntity;
      this.bedEntity = bedEntity;
      this.headFacing = this.headFacing();
      this.headRotCache = Float.NaN;
      this.persist = false;
   }

   @NotNull
   public final Direction getHeadFacing() {
      return this.headFacing;
   }

   public final void spawn(@NotNull BlockPos sleepingPos) {
      Intrinsics.checkNotNullParameter(sleepingPos, "sleepingPos");
      this.setProfile(ResolvableProfile.createResolved(this.bedPlayer.gameProfile));
      this.setImmovable(true);
      this.setGlowingTag(this.bedPlayer.hasGlowingTag());
      this.syncPlayerState();
      this.setPos(this.bedEntity.getX(), this.bedEntity.getY() + 0.1125 * (double)this.bedPlayer.getScale(), this.bedEntity.getZ());
      this.setSleepingPos(sleepingPos);
      this.setPose(Pose.SLEEPING);
      Iterable $this$forEach$iv = (Iterable)HUMANOID_SLOTS;

      for(Object element$iv : $this$forEach$iv) {
         EquipmentSlot it = (EquipmentSlot)element$iv;
         int var7 = 0;
         this.setItemSlot(it, this.bedPlayer.getItemBySlot(it));
      }

      World var10000 = this.baseEntity.getWorld();
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type org.bukkit.craftbukkit.CraftWorld");
      ((CraftWorld)var10000).getHandle().moonrise$getEntityLookup().addNewEntity((Entity)this, false);
   }

   @NotNull
   public final ClientboundTeleportEntityPacket positionSyncPacket() {
      return new ClientboundTeleportEntityPacket(this.getId(), PositionMoveRotation.of((Entity)this), SetsKt.emptySet(), false);
   }

   public final void syncPlayerState() {
      this.setInvisible(this.bedPlayer.persistentInvisibility || this.bedPlayer.hasEffect(MobEffects.INVISIBILITY));
      AttributeInstance var10000 = this.getAttribute(Attributes.SCALE);
      if (var10000 != null) {
         var10000.setBaseValue((double)this.bedPlayer.getScale());
      }

   }

   public final void updateHeadRotation() {
      Float p0 = this.bedPlayer.getYRot();
      float var4 = this.headRotCache;
      int var6 = 0;
      Float var10000 = !Float.valueOf(var4).equals(p0) ? p0 : null;
      if (var10000 != null) {
         float playerYaw = var10000;
         this.headRotCache = playerYaw;
         float var10001;
         switch (FurnitureBedMannequinEntity.WhenMappings.$EnumSwitchMapping$0[this.headFacing.ordinal()]) {
            case 1 -> var10001 = -90.0F;
            case 2 -> var10001 = 90.0F;
            case 3 -> var10001 = -180.0F;
            default -> var10001 = 0.0F;
         }

         float yaw = playerYaw + var10001;
         yaw = (yaw < 0.0F ? 360.0F + yaw : yaw) % 360.0F;
         float clamped = yaw >= 315.0F ? yaw - (float)360 : (yaw <= 45.0F ? yaw : (yaw >= 180.0F ? -45.0F : 45.0F));
         this.setYHeadRot(clamped);
         this.yHeadRotO = clamped;
      }
   }

   private final Direction headFacing() {
      float yaw = this.baseEntity.getYaw();
      return !(yaw >= 135.0F) && !(yaw < -135.0F) ? (yaw >= -135.0F && yaw < -45.0F ? Direction.EAST : (yaw >= -45.0F && yaw < 45.0F ? Direction.SOUTH : (yaw >= 45.0F ? Direction.WEST : Direction.NORTH))) : Direction.NORTH;
   }

   public void tick() {
   }

   protected void handlePortal() {
   }

   static {
      EquipmentSlot[] var0 = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
      HUMANOID_SLOTS = CollectionsKt.listOf(var0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/nms/v1_21_R10/furniture/bed/FurnitureBedMannequinEntity$Companion;", "", "<init>", "()V", "HUMANOID_SLOTS", "", "Lnet/minecraft/world/entity/EquipmentSlot;", "com.nexomc:v1_21_R10"}
   )
   public static final class Companion {
      private Companion() {
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[net.minecraft.core.Direction.values().length];
         try { a_0[net.minecraft.core.Direction.WEST.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[net.minecraft.core.Direction.EAST.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[net.minecraft.core.Direction.NORTH.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}

package com.nexomc.nexo.nms.v1_21_R11.items;

import com.mojang.math.Transformation;
import com.nexomc.nexo.nms.commons.items.ITridentWrapper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftTrident;
import org.bukkit.entity.Trident;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\r8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\r8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u000fR\u0014\u0010\u0012\u001a\u00020\r8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u000fR\u0014\u0010\u0014\u001a\u00020\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u00198VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001c\u001a\u00020\u00198VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001bR\u0014\u0010\u001e\u001a\u00020\u001f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010 R\u0014\u0010!\u001a\u00020\u001f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b!\u0010 R\u0014\u0010\"\u001a\u00020\u001f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\"\u0010 ¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R11/items/TridentWrapper;", "Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "trident", "Lorg/bukkit/entity/Trident;", "<init>", "(Lorg/bukkit/entity/Trident;)V", "nmsEntity", "Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;", "pickupItem", "Lnet/minecraft/world/item/ItemStack;", "getPickupItem", "()Lnet/minecraft/world/item/ItemStack;", "x", "", "getX", "()D", "y", "getY", "z", "getZ", "position", "Lnet/minecraft/world/phys/Vec3;", "getPosition", "()Lnet/minecraft/world/phys/Vec3;", "xRot", "", "getXRot", "()F", "yRot", "getYRot", "isRemoved", "", "()Z", "isInGround", "isInWater", "transformation", "Lcom/mojang/math/Transformation;", "leftRot", "Lorg/joml/Quaternionf;", "com.nexomc:v1_21_R11"}
)
public final class TridentWrapper implements ITridentWrapper {
   @NotNull
   private final ThrownTrident nmsEntity;

   public TridentWrapper(@NotNull Trident trident) {
      Intrinsics.checkNotNullParameter(trident, "trident");
      super();
      ThrownTrident var10001 = ((CraftTrident)trident).getHandle();
      Intrinsics.checkNotNullExpressionValue(var10001, "getHandle(...)");
      this.nmsEntity = var10001;
   }

   @NotNull
   public ItemStack getPickupItem() {
      ItemStack var10000 = this.nmsEntity.getPickupItem();
      Intrinsics.checkNotNullExpressionValue(var10000, "getPickupItem(...)");
      return var10000;
   }

   public double getX() {
      return this.nmsEntity.getX();
   }

   public double getY() {
      return this.nmsEntity.getY();
   }

   public double getZ() {
      return this.nmsEntity.getZ();
   }

   @NotNull
   public Vec3 getPosition() {
      Vec3 var10000 = this.nmsEntity.position();
      Intrinsics.checkNotNullExpressionValue(var10000, "position(...)");
      return var10000;
   }

   public float getXRot() {
      return this.nmsEntity.getXRot();
   }

   public float getYRot() {
      return this.nmsEntity.getYRot();
   }

   public boolean isRemoved() {
      return this.nmsEntity.isRemoved();
   }

   public boolean isInGround() {
      return this.nmsEntity.isInGround();
   }

   public boolean isInWater() {
      return this.nmsEntity.isInWater();
   }

   @NotNull
   public Transformation transformation(@NotNull Quaternionf leftRot) {
      Intrinsics.checkNotNullParameter(leftRot, "leftRot");
      return new Transformation((Vector3fc)(new Vector3f()), (Quaternionfc)leftRot, (Vector3fc)(new Vector3f(1.0F)), (Quaternionfc)(new Quaternionf()));
   }
}

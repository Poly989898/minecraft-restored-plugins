package com.nexomc.nexo.nms.v1_21_R4.items;

import com.mojang.math.Transformation;
import com.nexomc.nexo.nms.commons.items.ITridentWrapper;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftTrident;
import org.bukkit.entity.Trident;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\r\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0011\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0010R\u0014\u0010\u0013\u001a\u00020\u000e8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0010R\u001c\u0010\u0015\u001a\n \n*\u0004\u0018\u00010\u00160\u00168VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u0014\u0010\u0019\u001a\u00020\u001a8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u001a8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001cR\u0014\u0010\u001f\u001a\u00020 8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010!R\u0014\u0010\"\u001a\u00020 8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\"\u0010!R\u0014\u0010#\u001a\u00020 8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b#\u0010!¨\u0006("},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R4/items/TridentWrapper;", "Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "trident", "Lorg/bukkit/entity/Trident;", "<init>", "(Lorg/bukkit/entity/Trident;)V", "nmsEntity", "Lnet/minecraft/world/entity/projectile/ThrownTrident;", "pickupItem", "Lnet/minecraft/world/item/ItemStack;", "kotlin.jvm.PlatformType", "getPickupItem", "()Lnet/minecraft/world/item/ItemStack;", "x", "", "getX", "()D", "y", "getY", "z", "getZ", "position", "Lnet/minecraft/world/phys/Vec3;", "getPosition", "()Lnet/minecraft/world/phys/Vec3;", "xRot", "", "getXRot", "()F", "yRot", "getYRot", "isRemoved", "", "()Z", "isInGround", "isInWater", "transformation", "Lcom/mojang/math/Transformation;", "leftRot", "Lorg/joml/Quaternionf;", "com.nexomc:v1_21_R4"}
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

   public ItemStack getPickupItem() {
      return this.nmsEntity.getPickupItem();
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

   public Vec3 getPosition() {
      return this.nmsEntity.position();
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
      return new Transformation(new Vector3f(), leftRot, new Vector3f(1.0F), new Quaternionf());
   }
}

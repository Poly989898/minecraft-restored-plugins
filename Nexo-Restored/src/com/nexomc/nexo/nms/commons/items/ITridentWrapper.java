package com.nexomc.nexo.nms.commons.items;

import com.mojang.math.Transformation;
import kotlin.Metadata;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0012\u0010\n\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\tR\u0012\u0010\f\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\tR\u0012\u0010\u000e\u001a\u00020\u000fX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0012\u0010\u0012\u001a\u00020\u0013X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u0012\u0010\u0016\u001a\u00020\u0013X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0015R\u0012\u0010\u0018\u001a\u00020\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u001aR\u0012\u0010\u001b\u001a\u00020\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001aR\u0012\u0010\u001c\u001a\u00020\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001a¨\u0006!À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "", "pickupItem", "Lnet/minecraft/world/item/ItemStack;", "getPickupItem", "()Lnet/minecraft/world/item/ItemStack;", "x", "", "getX", "()D", "y", "getY", "z", "getZ", "position", "Lnet/minecraft/world/phys/Vec3;", "getPosition", "()Lnet/minecraft/world/phys/Vec3;", "xRot", "", "getXRot", "()F", "yRot", "getYRot", "isRemoved", "", "()Z", "isInGround", "isInWater", "transformation", "Lcom/mojang/math/Transformation;", "leftRot", "Lorg/joml/Quaternionf;", "com.nexomc:commons"}
)
public interface ITridentWrapper {
   @NotNull
   ItemStack getPickupItem();

   double getX();

   double getY();

   double getZ();

   @NotNull
   Vec3 getPosition();

   float getXRot();

   float getYRot();

   boolean isRemoved();

   boolean isInGround();

   boolean isInWater();

   @NotNull
   Transformation transformation(@NotNull Quaternionf quaternionf);
}

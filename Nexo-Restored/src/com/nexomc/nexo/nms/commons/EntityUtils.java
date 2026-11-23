package com.nexomc.nexo.nms.commons;

import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedEntity;
import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedPacket;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0016J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u0016J\u000e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u0016J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005H\u0016J\f\u0010\r\u001a\u0006\u0012\u0002\b\u00030\u0005H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J(\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/nms/commons/EntityUtils;", "", "<init>", "()V", "interactionType", "Lnet/minecraft/world/entity/EntityType;", "Lnet/minecraft/world/entity/Interaction;", "itemDisplayType", "Lnet/minecraft/world/entity/Display$ItemDisplay;", "textDisplayType", "Lnet/minecraft/world/entity/Display$TextDisplay;", "shulkerType", "Lnet/minecraft/world/entity/monster/Shulker;", "happyGhastType", "whiteBed", "Lnet/minecraft/world/level/block/Block;", "createBedPacket", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bedPlayer", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "nextEntityId", "", "world", "Lorg/bukkit/World;", "com.nexomc:commons"}
)
public class EntityUtils {
   @NotNull
   public EntityType<Interaction> interactionType() {
      EntityType var10000 = EntityTypes.INTERACTION;
      Intrinsics.checkNotNullExpressionValue(var10000, "INTERACTION");
      return var10000;
   }

   @NotNull
   public EntityType<Display.ItemDisplay> itemDisplayType() {
      EntityType var10000 = EntityTypes.ITEM_DISPLAY;
      Intrinsics.checkNotNullExpressionValue(var10000, "ITEM_DISPLAY");
      return var10000;
   }

   @NotNull
   public EntityType<Display.TextDisplay> textDisplayType() {
      EntityType var10000 = EntityTypes.TEXT_DISPLAY;
      Intrinsics.checkNotNullExpressionValue(var10000, "TEXT_DISPLAY");
      return var10000;
   }

   @NotNull
   public EntityType<Shulker> shulkerType() {
      EntityType var10000 = EntityTypes.SHULKER;
      Intrinsics.checkNotNullExpressionValue(var10000, "SHULKER");
      return var10000;
   }

   @NotNull
   public EntityType<?> happyGhastType() {
      EntityType var10000 = EntityTypes.HAPPY_GHAST;
      Intrinsics.checkNotNullExpressionValue(var10000, "HAPPY_GHAST");
      return var10000;
   }

   @NotNull
   public Block whiteBed() {
Object white = Blocks.BED.white();
      Block var10000 = (Block)white;
      Intrinsics.checkNotNullExpressionValue(var10000, "WHITE_BED");
      return var10000;
   }

   @NotNull
   public IFurnitureBedPacket createBedPacket(@NotNull Player bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull FurnitureBed bed) {
      Intrinsics.checkNotNullParameter(bedPlayer, "bedPlayer");
      Intrinsics.checkNotNullParameter(baseEntity, "baseEntity");
      Intrinsics.checkNotNullParameter(bedEntity, "bedEntity");
      Intrinsics.checkNotNullParameter(bed, "bed");
      return new FurnitureBedPacket(bedPlayer, baseEntity, bedEntity, bed);
   }

   public int nextEntityId(@NotNull World world) {
      Intrinsics.checkNotNullParameter(world, "world");
      return ((org.bukkit.craftbukkit.CraftWorld)world).getHandle().getNextEntityId();
   }
}

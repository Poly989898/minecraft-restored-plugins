package com.nexomc.nexo.nms.v26_R2;

import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedEntity;
import com.nexomc.nexo.nms.v26_R2.furniture.bed.FurnitureBedMannequin;
import kotlin.Metadata;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0016J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u0016J\u000e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u0016J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005H\u0016J\f\u0010\r\u001a\u0006\u0012\u0002\b\u00030\u0005H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J(\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/EntityUtils;", "Lcom/nexomc/nexo/nms/commons/EntityUtils;", "<init>", "()V", "interactionType", "Lnet/minecraft/world/entity/EntityType;", "Lnet/minecraft/world/entity/Interaction;", "itemDisplayType", "Lnet/minecraft/world/entity/Display$ItemDisplay;", "textDisplayType", "Lnet/minecraft/world/entity/Display$TextDisplay;", "shulkerType", "Lnet/minecraft/world/entity/monster/Shulker;", "happyGhastType", "whiteBed", "Lnet/minecraft/world/level/block/Block;", "nextEntityId", "", "world", "Lorg/bukkit/World;", "createBedPacket", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bedPlayer", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "com.nexomc:v26_R2"}
)
public final class EntityUtils extends com.nexomc.nexo.nms.commons.EntityUtils {
   @NotNull
   public EntityType<Interaction> interactionType() {
      EntityType entityType = EntityTypes.INTERACTION;
      return entityType;
   }

   @NotNull
   public EntityType<Display.ItemDisplay> itemDisplayType() {
      EntityType entityType = EntityTypes.ITEM_DISPLAY;
      return entityType;
   }

   @NotNull
   public EntityType<Display.TextDisplay> textDisplayType() {
      EntityType entityType = EntityTypes.TEXT_DISPLAY;
      return entityType;
   }

   @NotNull
   public EntityType<Shulker> shulkerType() {
      EntityType entityType = EntityTypes.SHULKER;
      return entityType;
   }

   @NotNull
   public EntityType<?> happyGhastType() {
      EntityType entityType = EntityTypes.HAPPY_GHAST;
      return entityType;
   }

   @NotNull
   public Block whiteBed() {
      Object white = Blocks.BED.white();
      return (Block)white;
   }

   public int nextEntityId(@NotNull World world) {
      return ((CraftWorld)world).getHandle().getNextEntityId();
   }

   @NotNull
   public IFurnitureBedPacket createBedPacket(@NotNull Player bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull FurnitureBed bed) {
      return new FurnitureBedMannequin(bedPlayer, baseEntity, bedEntity, bed);
   }
}

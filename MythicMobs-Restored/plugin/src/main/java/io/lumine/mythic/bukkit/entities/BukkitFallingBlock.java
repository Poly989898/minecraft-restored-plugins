package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;

public class BukkitFallingBlock extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private ItemStack head;
   @Getter
   private Material type = Material.STONE;
   @Getter
   private boolean dropsItem = true;
   @Getter
   private boolean hurtsEntities = true;
   @Getter
   private boolean replaceSpawnLocationBlock = false;
   @Getter
   private boolean mimicPositionBlock = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.FALLING_BLOCK;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      String mat = mc.getString("Options.Block", "STONE");

      try {
         this.type = Material.getMaterial(mat.toUpperCase());
      } catch (Exception e) {
         MythicLogger.errorGenericConfig("Invalid falling block material specified");
      }

      this.head = ItemFactory.of(this.type).build();
      this.dropsItem = mc.getBoolean("Options.DropsItem", true);
      this.hurtsEntities = mc.getBoolean("Options.HurtsEntities", true);
      this.replaceSpawnLocationBlock = mc.getBoolean("Options.ReplaceSpawnLocationBlock", false);
      this.mimicPositionBlock = mc.getBoolean("Options.UseSpawnLocationType", false);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      Material type;
      if (this.mimicPositionBlock && location.getBlock().getType().isBlock() && !location.getBlock().getType().isAir()) {
         type = location.getBlock().getType();
      } else {
         type = this.type;
      }

      if (this.replaceSpawnLocationBlock) {
         location.getBlock().setType(Material.AIR);
      }

      return location.getWorld().spawnFallingBlock(location, type.createBlockData());
   }

   @Override
   public Entity applyOptions(Entity entity) {
      FallingBlock fallingBlock = (FallingBlock)entity;
      fallingBlock.setDropItem(this.dropsItem);
      fallingBlock.setHurtEntities(this.hurtsEntities);
      return fallingBlock;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof FallingBlock fallingBlock;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}

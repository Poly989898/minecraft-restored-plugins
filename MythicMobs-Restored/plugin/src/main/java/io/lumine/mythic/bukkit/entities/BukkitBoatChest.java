package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.volatilecode.VolatileEntityType;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.function.Consumer;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Boat.Type;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class BukkitBoatChest extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.BAMBOO_CHEST_RAFT).build();
   private String type = "OAK";

   @Override
   public EntityType getBukkitType() {
      return VolatileEntityType.CHEST_BOAT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig config) {
      this.type = config.getString("Options.BoatType", "BAMBOO");
      this.type = config.getString("Options.Type", this.type);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      EntityType entityType;
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_4)) {
         String str = this.type;

         entityType = switch (str) {
            case "ACACIA" -> EntityType.ACACIA_CHEST_BOAT;
            case "BAMBOO" -> EntityType.BAMBOO_CHEST_RAFT;
            case "BIRCH" -> EntityType.BIRCH_CHEST_BOAT;
            case "CHERRY" -> EntityType.CHERRY_CHEST_BOAT;
            case "DARK_OAK" -> EntityType.DARK_OAK_CHEST_BOAT;
            case "JUNGLE" -> EntityType.JUNGLE_CHEST_BOAT;
            case "MANGROVE" -> EntityType.MANGROVE_CHEST_BOAT;
            case "OAK" -> EntityType.OAK_CHEST_BOAT;
            case "SPRICE" -> EntityType.SPRUCE_CHEST_BOAT;
            default -> EntityType.OAK_CHEST_BOAT;
         };
      } else {
         entityType = VolatileEntityType.CHEST_BOAT;
      }

      return this.spawnEntity(location, entityType, reason, prespawnFunc);
   }

   @Override
   public Entity applyOptions(Entity e) {
      ChestBoat entity = (ChestBoat)e;
      if (ServerVersion.isBefore(MinecraftVersions.v1_21_4)) {
         entity.setBoatType(Type.valueOf(this.type.toUpperCase()));
      }

      return entity;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof ChestBoat;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}

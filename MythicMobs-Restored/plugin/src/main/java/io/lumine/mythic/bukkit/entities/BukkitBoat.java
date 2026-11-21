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
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Boat.Type;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class BukkitBoat extends BukkitEntityType {
   private final String headString = "";
   @Getter
   private final ItemStack head = ItemFactory.of(Material.OAK_BOAT).build();
   private String type = "OAK";

   @Override
   public EntityType getBukkitType() {
      return VolatileEntityType.BOAT;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig config) {
      this.type = config.getString("Options.BoatType", "OAK");
      this.type = config.getString("Options.Type", this.type);
   }

   @Override
   public Entity spawn(Location location, SpawnReason reason, Consumer<Entity> prespawnFunc) {
      EntityType entityType;
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_4)) {
         String str = this.type;

         entityType = switch (str) {
            case "ACACIA" -> EntityType.ACACIA_BOAT;
            case "BAMBOO" -> EntityType.BAMBOO_RAFT;
            case "BIRCH" -> EntityType.BIRCH_BOAT;
            case "CHERRY" -> EntityType.CHERRY_BOAT;
            case "DARK_OAK" -> EntityType.DARK_OAK_BOAT;
            case "JUNGLE" -> EntityType.JUNGLE_BOAT;
            case "MANGROVE" -> EntityType.MANGROVE_BOAT;
            case "OAK" -> EntityType.OAK_BOAT;
            case "SPRICE" -> EntityType.SPRUCE_BOAT;
            default -> EntityType.OAK_BOAT;
         };
      } else {
         entityType = VolatileEntityType.BOAT;
      }

      return this.spawnEntity(location, entityType, reason, prespawnFunc);
   }

   @Override
   public Entity applyOptions(Entity e) {
      Boat entity = (Boat)e;
      if (ServerVersion.isBefore(MinecraftVersions.v1_21_4)) {
         entity.setBoatType(Type.valueOf(this.type.toUpperCase()));
      }

      return entity;
   }

   @Override
   public boolean compare(Entity e) {
      return e instanceof Boat;
   }

   @Generated
   @Override
   public String getHeadString() {
      return "";
   }
}

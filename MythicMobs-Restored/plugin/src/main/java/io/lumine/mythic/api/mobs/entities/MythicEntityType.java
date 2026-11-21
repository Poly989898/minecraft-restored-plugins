package io.lumine.mythic.api.mobs.entities;

import com.google.common.collect.Maps;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public enum MythicEntityType {
   ALLAY(),
   ARMADILLO(),
   ARMOR_STAND(),
   AXOLOTL(),
   BABY_DROWNED(),
   BABY_HUSK(),
   BABY_PIGLIN(),
   BABY_PIGLIN_BRUTE(),
   BABY_PIG_ZOMBIE(),
   BABY_PIG_ZOMBIE_VILLAGER(),
   BABY_ZOGLIN(),
   BABY_ZOMBIE(),
   BABY_ZOMBIE_VILLAGER("BABY_VILLAGER_ZOMBIE"),
   BAT(),
   BEE(),
   BLAZE(),
   BLOCK_DISPLAY(),
   BOGGED(),
   BREEZE(),
   BOAT(),
   CAT(),
   CAMEL(),
   CAMEL_HUSK(),
   CAVE_SPIDER(),
   CHEST_BOAT(),
   CHICKEN(),
   COD(),
   COPPER_GOLEM(),
   COW(),
   CREAKING(),
   CREEPER(),
   CUSTOM(),
   DOLPHIN(),
   DONKEY(),
   DROWNED(),
   ELDER_GUARDIAN(),
   ENDER_DRAGON(),
   ENDERMAN(),
   ENDERMITE(),
   EVOKER(),
   EXPERIENCE_BOTTLE("THROWN_EXP_BOTTLE"),
   EXPERIENCE_ORB(),
   FALLING_BLOCK(),
   FOX(),
   FROG(),
   GHAST(),
   GIANT(),
   GOAT(),
   GLOW_SQUID("GLOWING_SQUID"),
   GUARDIAN(),
   HAPPY_GHAST(),
   HOGLIN(),
   HORSE(),
   HUSK(),
   ILLUSIONER(),
   INTERACTION(),
   IRON_GOLEM("VILLAGER_GOLEM"),
   ITEM("DROPPED_ITEM"),
   ITEM_DISPLAY(),
   LLAMA(),
   MAGMA_CUBE("LAVA_SLIME"),
   MANNEQUIN("NPC"),
   MARKER(),
   MINECART(),
   MINECART_CHEST("CHEST_MINECART"),
   MINECART_COMMAND("COMMAND_BLOCK_MINECART", "COMMAND_MINECART"),
   MINECART_FURNACE("FURNACE_MINECART"),
   MINECART_HOPPER("HOPPER_MINECART"),
   MINECART_MOB_SPAWNER("SPAWNER_MINECART"),
   MINECART_TNT("TNT_MINECART"),
   MULE(),
   MUSHROOM_COW("MOOSHROOM", "COW_MUSHROOM"),
   NAUTILUS(),
   OCELOT(),
   PANDA(),
   PARCHED(),
   PARROT(),
   PHANTOM(),
   PIG(),
   PIGLIN("PIGMAN"),
   PIGLIN_BRUTE(),
   ZOMBIFIED_PIGLIN("ZOMBIE_PIGMAN", "PIG_ZOMBIE", "ZOMBIE_PIG"),
   ZOMBIFIED_PIGLIN_VILLAGER(),
   PILLAGER(),
   POLAR_BEAR(),
   PRIMED_TNT("TNT"),
   PUFFERFISH(),
   RABBIT(),
   RAVAGER(),
   SALMON(),
   SHEEP(),
   SHULKER(),
   SILVERFISH(),
   SKELETON(),
   SKELETON_HORSE("SKELETAL_HORSE"),
   SLIME(),
   SNIFFER(),
   SNOWMAN("SNOW_GOLEM"),
   SPIDER(),
   STRAY(),
   STRIDER(),
   SQUID(),
   SULFUR_CUBE(),
   TADPOLE(),
   TEXT_DISPLAY(),
   TRADER_LLAMA(),
   TROPICAL_FISH(),
   TURTLE(),
   VEX(),
   VILLAGER(),
   VINDICATOR("VINDICATOR"),
   WANDERING_TRADER(),
   WARDEN(),
   WITCH(),
   WITHER(),
   WITHER_SKELETON(),
   WOLF(),
   ZOGLIN(),
   ZOMBIE(),
   ZOMBIE_HORSE("ZOMBIFIED_HORSE"),
   ZOMBIE_NAUTILUS("NAUTILUS_ZOMBIE"),
   ZOMBIE_VILLAGER("VILLAGER_ZOMBIE");

   private static final Map<String, MythicEntityType> ALIASES = new HashMap<>();
   @Getter
   private final String[] aliases;
   private final Map<String, Double> defaultAttributes = Maps.newConcurrentMap();
   private double defaultHeight = 2.0;
   private boolean defaultValuesInitialized = false;

   private MythicEntityType(String... aliases) {
      this.aliases = aliases;
   }

   public void initializeDefaultValues(BukkitAttributable attributable) {
      BukkitAttribute.values().forEach(attribute -> {
         if (attributable.getAttribute(attribute.toString()) != null) {
            double value = attributable.getAttribute(attribute.toString()).getBaseValue();
            this.defaultAttributes.put(attribute.getAttributeKey().toString(), value);
         }
      });
      this.defaultValuesInitialized = true;
   }

   public void initializeDefaultValues() {
      Location location = new Location(Bukkit.getWorlds().get(0), 0.0, 1000.0, 0.0);
      BukkitEntityType entityType = BukkitEntityType.getMythicEntity(this);
      if (entityType != null) {
         Entity entity = entityType.spawn(location, SpawnReason.OTHER, spawnedEntity -> {
            this.defaultHeight = spawnedEntity.getHeight();
            if (spawnedEntity instanceof LivingEntity livingEntity) {
               Registry.ATTRIBUTE.forEach(attribute -> {
                  if (livingEntity.getAttribute(attribute) != null) {
                     double value = livingEntity.getAttribute(attribute).getBaseValue();
                     this.defaultAttributes.put(attribute.getKey().toString(), value);
                  }
               });
            }
         });
         entity.remove();
         this.defaultValuesInitialized = true;
      }
   }

   public Optional<Double> getAttributeDefault(Attribute attribute) {
      return Optional.ofNullable(this.defaultAttributes.getOrDefault(attribute.getKey().toString(), null));
   }

   public static MythicEntityType get(String entityType) {
      if (entityType == null) {
         return null;
      } else {
         entityType = entityType.toUpperCase();
         MythicEntityType type = ALIASES.get(entityType);
         if (type == null) {
            try {
               EntityType.valueOf(entityType);
               return CUSTOM;
            } catch (Exception e) {
               return null;
            }
         } else {
            return type;
         }
      }
   }

   static {
      for (MythicEntityType met : values()) {
         ALIASES.put(met.toString().toUpperCase(), met);
         ALIASES.put(met.toString().toUpperCase().replace("_", ""), met);

         for (String alias : met.getAliases()) {
            ALIASES.put(alias.toUpperCase(), met);
            ALIASES.put(alias.toString().toUpperCase().replace("_", ""), met);
         }
      }
   }
}

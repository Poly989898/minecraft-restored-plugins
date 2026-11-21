package io.lumine.mythic.bukkit;

import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractPotionEffect;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.adapters.BukkitPotionEffect;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.items.RandomTableItem;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class BukkitAdapter {
   public static AbstractEntity adapt(Entity entity) {
      return entity == null ? null : new BukkitEntity(entity);
   }

   public static Entity adapt(AbstractEntity entity) {
      return entity == null ? null : entity.getBukkitEntity();
   }

   public static AbstractPlayer adapt(Player player) {
      return player == null ? null : new BukkitPlayer(player);
   }

   public static ItemStack adapt(AbstractItemStack item) {
      if (item == null) {
         return null;
      } else if (item instanceof BukkitItemStack bukkit) {
         return bukkit.getItemStack();
      } else if (item instanceof RandomTableItem randomTableItem) {
         return adapt(randomTableItem.getDropTable().generate().getSingleItem());
      } else {
         throw new IllegalArgumentException("cannot adapt unsupported platform item stack '" + item.getClass().getSimpleName() + "'.");
      }
   }

   public static BukkitItemStack adapt(ItemStack item) {
      return MythicBukkit.inst().getBootstrap().wrapItem(item);
   }

   public static BukkitItemStack adapt(Material material) {
      return adapt(new ItemStack(material, 1));
   }

   public static BukkitBlock adapt(Block block) {
      return new BukkitBlock(block.getBlockData());
   }

   public static ItemFlag adapt(AbstractItemFlag flag) {
      return switch (flag) {
         case HIDE_ENCHANTS -> ItemFlag.HIDE_ENCHANTS;
         case HIDE_ATTRIBUTES -> ItemFlag.HIDE_ATTRIBUTES;
         case HIDE_UNBREAKABLE -> ItemFlag.HIDE_UNBREAKABLE;
         case HIDE_DESTROYS -> ItemFlag.HIDE_DESTROYS;
         case HIDE_PLACED_ON -> ItemFlag.HIDE_PLACED_ON;
         case HIDE_ADDITIONAL_TOOLTIP -> ItemFlag.HIDE_ADDITIONAL_TOOLTIP;
         case HIDE_DYE -> ItemFlag.HIDE_DYE;
         case HIDE_ARMOR_TRIM -> ItemFlag.HIDE_ARMOR_TRIM;
         case HIDE_STORED_ENCHANTS -> ItemFlag.HIDE_STORED_ENCHANTS;
         case HIDE_POTION_EFFECTS -> ServerVersion.isBefore(MinecraftVersions.v1_20_5)
            ? ItemFlag.valueOf("HIDE_POTION_EFFECTS")
            : ItemFlag.HIDE_ADDITIONAL_TOOLTIP;
      };
   }

   public static Player adapt(AbstractPlayer player) {
      return player == null ? null : (Player)player.getBukkitEntity();
   }

   public static AbstractWorld adapt(World world) {
      return new BukkitWorld(world);
   }

   public static World adapt(AbstractWorld world) {
      if (world == null) {
         throw new IllegalArgumentException("World cannot be null");
      } else if (world instanceof BukkitWorld bukkitWorld) {
         return bukkitWorld.getBukkitWorld();
      } else {
         World match = Bukkit.getServer().getWorld(world.getName());
         if (match != null) {
            return match;
         } else {
            throw new IllegalArgumentException("Can't find a Bukkit world for " + world);
         }
      }
   }

   public static AbstractLocation adapt(Location location) {
      return location == null
         ? null
         : new AbstractLocation(adapt(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
   }

   public static Location adapt(AbstractLocation location) {
      return new Location(adapt(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
   }

   public static AbstractVector adapt(Vector vector) {
      return vector == null ? null : new AbstractVector(vector.getX(), vector.getY(), vector.getZ());
   }

   public static Vector adapt(AbstractVector vector) {
      return new Vector(vector.getX(), vector.getY(), vector.getZ());
   }

   public static PotionEffect adapt(AbstractPotionEffect effect) {
      if (effect == null) {
         return null;
      } else if (effect instanceof BukkitPotionEffect bukkitPotionEffect) {
         return bukkitPotionEffect.toBukkitEffect();
      } else {
         PotionEffectType type = PotionEffectType.getByName(effect.getEffectName());
         return type == null ? null : new PotionEffect(type, effect.getDuration(), effect.getLevel());
      }
   }

   @Deprecated
   public static AbstractBiome adapt(Biome b) {
      String biome = b.getKey().getNamespace() + ":" + b.getKey().getKey();
      Optional<AbstractBiome> abs = MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getBiome(biome);
      return abs.orElse(null);
   }

   public static EntitySnapshot getEntitySnapshot(MythicMob mm) {
      String entityType = mm.getEntityType().toString().toLowerCase();
      String snapshotString = "{id:\"minecraft:" + entityType + "\",BukkitValues:{\"mythicmobs:type\":\"" + mm.getInternalName() + "\"}}";
      return Bukkit.getEntityFactory().createEntitySnapshot(snapshotString);
   }
}

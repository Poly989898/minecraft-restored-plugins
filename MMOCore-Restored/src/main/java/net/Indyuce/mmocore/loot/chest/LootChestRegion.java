package net.Indyuce.mmocore.loot.chest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.LootChestSpawnEvent;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmocore.loot.RandomWeightedRoll;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LootChestRegion {
   private final String id;
   private final long chestSpawnPeriod;
   private final RegionBounds bounds;
   private final ChestAlgorithmOptions algOptions;
   private final Set<ChestTier> tiers = new LinkedHashSet<>();
   private final BukkitRunnable runnable = new BukkitRunnable() {
      public void run() {
         LootChestRegion.this.getBounds()
            .getPlayers()
            .filter(player -> player.getActivityTimeOut(PlayerActivity.LOOT_CHEST_SPAWN) == 0L)
            .findAny()
            .ifPresent(player -> LootChestRegion.this.spawnChest(player));
      }
   };
   private static final Random RANDOM = new Random();

   public LootChestRegion(ConfigurationSection config) {
      Validate.notNull(config, "Could not load config");
      this.id = config.getName().toLowerCase().replace("_", "-").replace(" ", "-");
      this.bounds = new RegionBounds(config.getConfigurationSection("bounds"));
      this.chestSpawnPeriod = config.getLong("spawn-period", 300L);
      this.algOptions = config.contains("algorithm-options")
         ? new ChestAlgorithmOptions(config.getConfigurationSection("algorithm-options"))
         : ChestAlgorithmOptions.DEFAULT;
      Validate.isTrue(config.isConfigurationSection("tiers"), "Could not find chest tiers");

      for (String key : config.getConfigurationSection("tiers").getKeys(false)) {
         try {
            this.tiers.add(new ChestTier(config.getConfigurationSection("tiers." + key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load tier '" + key + "' from chest region '" + this.id + "': " + exception.getMessage());
         }
      }

      Validate.isTrue(!this.tiers.isEmpty(), "Your region must have at least one chest tier");
      this.runnable.runTaskTimer(MMOCore.plugin, this.chestSpawnPeriod * 20L, this.chestSpawnPeriod * 20L);
   }

   public String getId() {
      return this.id;
   }

   public Set<ChestTier> getTiers() {
      return this.tiers;
   }

   public RegionBounds getBounds() {
      return this.bounds;
   }

   public long getChestSpawnPeriod() {
      return this.chestSpawnPeriod;
   }

   public BukkitRunnable getRunnable() {
      return this.runnable;
   }

   public void spawnChest(PlayerData player) {
      player.setLastActivity(PlayerActivity.LOOT_CHEST_SPAWN);
      ChestTier tier = this.rollTier(player);
      Location location = this.getRandomLocation(player.getPlayer().getLocation());
      if (location != null) {
         LootChest lootChest = new LootChest(tier, this, location.getBlock());
         LootBuilder builder = new LootBuilder(player, tier.rollCapacity(player));
         tier.getDropTable().collect(builder);
         LootChestSpawnEvent event = new LootChestSpawnEvent(player, lootChest, builder);
         Bukkit.getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            List<Integer> slots = new ArrayList<>();

            for (int j = 0; j < 27; j++) {
               slots.add(j);
            }

            location.getBlock().setType(Material.CHEST);
            Chest chest = (Chest)location.getBlock().getState();
            builder.getLoot().forEach(item -> {
               Integer slot = slots.get(RANDOM.nextInt(slots.size()));
               chest.getInventory().setItem(slot, item);
               slots.remove(slot);
            });
            MMOCore.plugin.lootChests.register(lootChest);
         }
      }
   }

   @NotNull
   public ChestTier rollTier(PlayerData player) {
      return new RandomWeightedRoll<>(player, this.tiers, MMOCore.plugin.configManager.lootChestsChanceWeight).rollItem();
   }

   public Location getRandomLocation(Location center) {
      for (int j = 0; j < this.algOptions.iterations; j++) {
         Location random = this.tryRandomDirection(center);
         if (random != null) {
            return random;
         }
      }

      return null;
   }

   public Location tryRandomDirection(Location center) {
      double a = RANDOM.nextDouble() * 2.0 * Math.PI;
      Vector dir = new Vector(Math.cos(a), 0.0, Math.sin(a))
         .multiply(this.algOptions.minRange + RANDOM.nextDouble() * (this.algOptions.maxRange - this.algOptions.minRange));
      Location random = center.add(dir);

      for (int h = 0; h <= this.algOptions.height * 2.0; h++) {
         int z = h % 2 == 0 ? h / 2 : -(h + 1) / 2;
         Location checked = random.clone().add(0.0, z, 0.0);
         if (this.isSuitable(checked)) {
            return checked;
         }
      }

      return null;
   }

   private boolean isSuitable(Location loc) {
      return !loc.getBlock().getType().isSolid() && loc.clone().add(0.0, -1.0, 0.0).getBlock().getType().isSolid();
   }
}

package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class BrewPotionExperienceSource extends ExperienceSource<PotionMeta> {
   private final double multiplier;
   private final List<PotionType> types = new ArrayList<>();

   public BrewPotionExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser);
      this.multiplier = config.getDouble("multiplier", 1.0);
      if (config.contains("effect")) {
         for (String key : config.getString("effect").split(",")) {
            this.types.add(PotionType.valueOf(key.toUpperCase().replace("-", "_")));
         }
      }
   }

   public boolean matchesParameter(PlayerData player, PotionMeta meta) {
      return this.types.isEmpty() || new ArrayList<>(this.types).stream().anyMatch(type -> meta.getBasePotionData().getType() == type);
   }

   @Override
   public ExperienceSourceManager<BrewPotionExperienceSource> newManager() {
      return new BrewPotionExperienceSource.Manager();
   }

   private static class Manager extends ExperienceSourceManager<BrewPotionExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(BrewEvent event) {
         Optional<Player> playerOpt = this.getNearbyPlayer(event.getBlock().getLocation());
         if (playerOpt.isPresent()) {
            ItemStack found = this.findPotion(event.getContents());
            if (found != null) {
               Bukkit.getScheduler().scheduleSyncDelayedTask(MMOCore.plugin, () -> {
                  ItemStack brewn = this.findPotion(event.getContents());
                  if (brewn != null) {
                     PlayerData data = PlayerData.get((OfflinePlayer)playerOpt.get());

                     for (BrewPotionExperienceSource source : this.getSources()) {
                        if (source.matches(data, (PotionMeta)brewn.getItemMeta())) {
                           new BrewPotionExperienceSource.PotionUpgrade(found, brewn).process(source, data.getPlayer());
                        }
                     }
                  }
               });
            }
         }
      }

      private ItemStack findPotion(BrewerInventory inv) {
         for (int j = 0; j < 3; j++) {
            ItemStack item = inv.getItem(j);
            if (item != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
               return item;
            }
         }

         return null;
      }

      private Optional<Player> getNearbyPlayer(Location loc) {
         return loc.getWorld().getPlayers().stream().filter(player -> player.getLocation().distanceSquared(loc) < 100.0).findAny();
      }
   }

   private static class PotionUpgrade {
      private double exp;

      public PotionUpgrade(ItemStack old, ItemStack brewn) {
         this(old.getType(), (PotionMeta)old.getItemMeta(), brewn.getType(), (PotionMeta)brewn.getItemMeta());
      }

      public PotionUpgrade(Material oldPot, PotionMeta old, Material brewnPot, PotionMeta brewn) {
         this.exp = this.exp + MMOCore.plugin.alchemyManager.getBaseExperience(brewn.getBasePotionData().getType());
         if (oldPot == Material.POTION && brewnPot == Material.SPLASH_POTION) {
            this.exp = this.exp * MMOCore.plugin.alchemyManager.splash;
         }

         if (oldPot == Material.POTION && brewnPot == Material.LINGERING_POTION) {
            this.exp = this.exp * MMOCore.plugin.alchemyManager.lingering;
         }

         if (!old.getBasePotionData().isExtended() && brewn.getBasePotionData().isExtended()) {
            this.exp = this.exp * MMOCore.plugin.alchemyManager.extend;
         }

         if (!old.getBasePotionData().isUpgraded() && brewn.getBasePotionData().isUpgraded()) {
            this.exp = this.exp * MMOCore.plugin.alchemyManager.upgrade;
         }
      }

      public void process(BrewPotionExperienceSource source, Player player) {
         source.getDispenser().giveExperience(PlayerData.get(player), this.exp * source.multiplier, player.getLocation(), EXPSource.SOURCE);
      }
   }
}

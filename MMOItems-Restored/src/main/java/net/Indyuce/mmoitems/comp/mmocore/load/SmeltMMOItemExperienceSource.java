package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Optional;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;

public class SmeltMMOItemExperienceSource extends SpecificExperienceSource<NBTItem> {
   private final String type;
   private final String id;

   public SmeltMMOItemExperienceSource(ExperienceDispenser experienceDispenser, MMOLineConfig mmoLineConfig) {
      super(experienceDispenser, mmoLineConfig);
      mmoLineConfig.validate(new String[]{"type", "id"});
      this.type = mmoLineConfig.getString("type").replace("-", "_").replace(" ", "_").toUpperCase();
      this.id = mmoLineConfig.getString("id").replace("-", "_").replace(" ", "_").toUpperCase();
   }

   public ExperienceSourceManager<SmeltMMOItemExperienceSource> newManager() {
      return new ExperienceSourceManager<SmeltMMOItemExperienceSource>() {
         @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
         public void a(BlockCookEvent event) {
            Optional nearbyPlayer = SmeltMMOItemExperienceSource.this.getNearbyPlayer(event.getBlock().getLocation());
            if (nearbyPlayer.isPresent()) {
               ItemStack result = event.getResult();
               NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(result);
               if (nBTItem.hasType()) {
                  PlayerData playerData = PlayerData.get((OfflinePlayer)nearbyPlayer.get());

                  for (SmeltMMOItemExperienceSource source : this.getSources()) {
                     if (source.matches(playerData, nBTItem)) {
                        source.giveExperience(playerData, 1.0, event.getBlock().getLocation().add(0.5, 1.0, 0.5));
                     }
                  }
               }
            }
         }
      };
   }

   private Optional<Player> getNearbyPlayer(Location location) {
      return location.getWorld().getPlayers().stream().filter(player -> player.getLocation().distanceSquared(location) < 100.0).findAny();
   }

   public boolean matchesParameter(PlayerData playerData, NBTItem nbtItem) {
      return nbtItem.getString("MMOITEMS_ITEM_TYPE").equals(this.type) && nbtItem.getString("MMOITEMS_ITEM_ID").equals(this.id);
   }
}

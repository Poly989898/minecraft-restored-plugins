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

   public SmeltMMOItemExperienceSource(ExperienceDispenser var1, MMOLineConfig var2) {
      super(var1, var2);
      var2.validate(new String[]{"type", "id"});
      this.type = var2.getString("type").replace("-", "_").replace(" ", "_").toUpperCase();
      this.id = var2.getString("id").replace("-", "_").replace(" ", "_").toUpperCase();
   }

   public ExperienceSourceManager<SmeltMMOItemExperienceSource> newManager() {
      return new ExperienceSourceManager<SmeltMMOItemExperienceSource>() {
         @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
         public void a(BlockCookEvent var1) {
            Optional var2 = SmeltMMOItemExperienceSource.this.getNearbyPlayer(var1.getBlock().getLocation());
            if (var2.isPresent()) {
               ItemStack var3 = var1.getResult();
               NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3);
               if (var4.hasType()) {
                  PlayerData var5 = PlayerData.get((OfflinePlayer)var2.get());

                  for (SmeltMMOItemExperienceSource var7 : this.getSources()) {
                     if (var7.matches(var5, var4)) {
                        var7.giveExperience(var5, 1.0, var1.getBlock().getLocation().add(0.5, 1.0, 0.5));
                     }
                  }
               }
            }
         }
      };
   }

   private Optional<Player> getNearbyPlayer(Location var1) {
      return var1.getWorld().getPlayers().stream().filter(var1x -> var1x.getLocation().distanceSquared(var1) < 100.0).findAny();
   }

   public boolean matchesParameter(PlayerData var1, NBTItem var2) {
      return var2.getString("MMOITEMS_ITEM_TYPE").equals(this.type) && var2.getString("MMOITEMS_ITEM_ID").equals(this.id);
   }
}

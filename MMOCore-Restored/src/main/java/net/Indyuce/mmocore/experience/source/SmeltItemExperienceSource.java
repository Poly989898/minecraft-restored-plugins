package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.inventory.ItemStack;

public class SmeltItemExperienceSource extends SpecificExperienceSource<ItemStack> {
   private final Material material;

   public SmeltItemExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      config.validate(new String[]{"type"});
      this.material = Material.valueOf(config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
   }

   @Override
   public ExperienceSourceManager<SmeltItemExperienceSource> newManager() {
      return new SmeltItemExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, ItemStack obj) {
      return obj.getType() == this.material;
   }

   private static class Manager extends ExperienceSourceManager<SmeltItemExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void a(BlockCookEvent event) {
         Optional<Player> player = this.getNearestPlayer(event.getBlock().getLocation());
         if (player.isPresent()) {
            ItemStack caught = event.getResult();
            if (!caught.hasItemMeta()) {
               PlayerData data = PlayerData.get((OfflinePlayer)player.get());

               for (SmeltItemExperienceSource source : this.getSources()) {
                  if (source.matches(data, caught)) {
                     source.giveExperience(data, 1.0, event.getBlock().getLocation());
                  }
               }
            }
         }
      }

      private Optional<Player> getNearestPlayer(Location loc) {
         Player[] nearby = loc.getWorld().getPlayers().stream().filter(player -> player.getLocation().distanceSquared(loc) < 100.0).toArray(Player[]::new);
         Player selected = null;
         double lastDist = 100.0;

         for (Player p : nearby) {
            double currDist = p.getLocation().distance(loc);
            if (currDist < lastDist) {
               lastDist = currDist;
               selected = p;
            }
         }

         return Optional.ofNullable(selected);
      }
   }
}

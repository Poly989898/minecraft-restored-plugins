package net.Indyuce.mmocore.listener.profession;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.version.VParticle;
import io.lumine.mythic.lib.version.VPotionEffectType;
import java.util.Random;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmocore.loot.chest.particle.SmallParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class PlayerCollectStats implements Listener {
   private static final Random RANDOM = new Random();

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void a(CustomBlockMineEvent event) {
      Player player = event.getPlayer();
      double h = event.getData().getStats().getStat("GATHERING_HASTE");
      if (h > 0.0 && RANDOM.nextDouble() < h * 0.045) {
         new SmallParticleEffect(player, VParticle.INSTANT_EFFECT.get());
         UtilityMethods.forcePotionEffect(player, VPotionEffectType.HASTE.get(), h / 2.0, (int)(1.0 + h / 7.0));
      }

      double f = event.getData().getStats().getStat("FORTUNE");
      if (f > 0.0 && RANDOM.nextDouble() < f * 0.045) {
         int a = (int)(1.5 * Math.sqrt(f / 1.1));

         for (ItemStack item : event.getDrops()) {
            item.setAmount(item.getAmount() + a);
         }
      }

      if (MythicLib.plugin.getVersion().getWrapper().isCropFullyGrown(event.getBlock())) {
         double l = event.getData().getStats().getStat("LUCK_OF_THE_FIELD");
         if (l > 0.0 && RANDOM.nextDouble() < l * 0.045) {
            int a = (int)(1.5 * Math.sqrt(l / 1.1));
            Location loc = event.getBlock().getLocation().add(0.5, 0.1, 0.5);
            loc.getWorld().spawnParticle(VParticle.HAPPY_VILLAGER.get(), loc.clone().add(0.0, 0.2, 0.0), 10, 0.3, 0.2, 0.3, 0.0);

            for (ItemStack item : event.getDrops()) {
               item.setAmount(item.getAmount() + a);
            }
         }
      }
   }
}

package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;

public abstract class Trigger {
   public static String STAT_MODIFIER_KEY = "mmocore_trigger";
   private final long delay;

   public Trigger(MMOLineConfig config) {
      this.delay = config.contains("delay") ? (long)(config.getDouble("delay") * 20.0) : 0L;
   }

   public boolean hasDelay() {
      return this.delay > 0L;
   }

   public long getDelay() {
      return this.delay;
   }

   public void schedule(PlayerData player) {
      if (this.delay <= 0L) {
         this.apply(player);
      } else {
         Bukkit.getScheduler().scheduleSyncDelayedTask(MMOCore.plugin, () -> this.apply(player), this.delay);
      }
   }

   public abstract void apply(PlayerData playerData);
}

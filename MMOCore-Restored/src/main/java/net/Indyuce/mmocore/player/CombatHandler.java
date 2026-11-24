package net.Indyuce.mmocore.player;

import io.lumine.mythic.lib.util.Closeable;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.event.PlayerCombatEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatHandler implements Closeable {
   private final PlayerData player;
   private long lastEntry = System.currentTimeMillis();
   private long lastHit = System.currentTimeMillis();
   private long invulnerableTill;
   private boolean pvpMode;
   @Nullable
   private BukkitTask task;

   public CombatHandler(PlayerData player) {
      this.player = player;
   }

   public void update() {
      this.lastHit = System.currentTimeMillis();
      this.invulnerableTill = 0L;
      this.player.getMMOPlayerData().getCooldownMap().applyCooldown("PvpMode", MMOCore.plugin.configManager.pvpModeCombatCooldown);
      if (this.isInCombat()) {
         this.task.cancel();
         this.task = this.newTask();
      } else {
         this.lastEntry = System.currentTimeMillis();
         ConfigMessage.fromKey("now-in-combat").send(this.player.getPlayer());
         Bukkit.getPluginManager().callEvent(new PlayerCombatEvent(this.player, true));
         this.task = this.newTask();
      }
   }

   @NotNull
   private BukkitTask newTask() {
      return Bukkit.getScheduler().runTaskLater(MMOCore.plugin, () -> this.quit(false), MMOCore.plugin.configManager.combatLogTimer / 50L);
   }

   public boolean isInPvpMode() {
      return this.pvpMode;
   }

   public void setPvpMode(boolean pvpMode) {
      this.pvpMode = pvpMode;
   }

   public long getLastHit() {
      return this.lastHit;
   }

   public long getLastEntry() {
      return this.lastEntry;
   }

   public long getInvulnerableTill() {
      return this.invulnerableTill;
   }

   public boolean isInCombat() {
      return this.task != null;
   }

   public boolean isInvulnerable() {
      return System.currentTimeMillis() < this.invulnerableTill;
   }

   public void setInvulnerable(double time) {
      this.invulnerableTill = System.currentTimeMillis() + (long)(time * 1000.0);
   }

   public boolean canQuitPvpMode() {
      return System.currentTimeMillis() > this.lastHit + MMOCore.plugin.configManager.pvpModeCombatTimeout * 1000.0;
   }

   private void quit(boolean cancelTask) {
      Validate.isTrue(this.isInCombat(), "Player not in combat");
      if (cancelTask) {
         this.task.cancel();
      }

      this.task = null;
      if (this.player.isOnline()) {
         Bukkit.getPluginManager().callEvent(new PlayerCombatEvent(this.player, false));
         ConfigMessage.fromKey("leave-combat").send(this.player.getPlayer());
      }
   }

   public void close() {
      if (this.isInCombat()) {
         this.quit(true);
      }

      this.lastHit = 0L;
      this.invulnerableTill = 0L;
      this.player.getMMOPlayerData().getCooldownMap().resetCooldown("PvpMode");
   }
}

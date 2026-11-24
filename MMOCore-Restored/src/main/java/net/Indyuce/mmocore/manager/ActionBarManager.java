package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.version.Attributes;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarManager extends BukkitRunnable {
   private int updateTicks;
   private int timeOut;
   private String barFormat;
   private boolean enabled;
   private boolean scheduled;

   public void reload(ConfigurationSection config) {
      this.enabled = config.getBoolean("enabled", false);
      this.timeOut = config.getInt("time-out", 60);
      this.updateTicks = config.getInt("ticks-to-update", 5);
      this.barFormat = config.getString("format", "<No Action Bar Format Found>");
      if (!this.scheduled && this.enabled) {
         this.runTaskTimer(MMOCore.plugin, 0L, this.updateTicks);
         this.scheduled = true;
      } else if (this.scheduled && !this.enabled) {
         this.cancel();
         this.scheduled = false;
      }
   }

   public long getTimeOut() {
      return this.timeOut;
   }

   public void run() {
      for (PlayerData data : PlayerData.getAll()) {
         if (data.isOnline() && !data.getPlayer().isDead() && !data.isCasting() && data.getActivityTimeOut(PlayerActivity.ACTION_BAR_MESSAGE) == 0L) {
            Placeholders holders = this.getActionBarPlaceholders(data);
            data.getPlayer()
               .spigot()
               .sendMessage(
                  ChatMessageType.ACTION_BAR,
                  TextComponent.fromLegacyText(
                     holders.apply(data.getPlayer(), data.getProfess().hasActionBar() ? data.getProfess().getActionBar() : this.barFormat)
                  )
               );
         }
      }
   }

   public Placeholders getActionBarPlaceholders(PlayerData data) {
      Placeholders holders = new Placeholders();
      holders.register("health", io.lumine.mythic.lib.manager.StatManager.format("MAX_HEALTH", data.getPlayer().getHealth()));
      holders.register(
         "max_health", io.lumine.mythic.lib.manager.StatManager.format("MAX_HEALTH", data.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue())
      );
      holders.register("mana_icon", data.getProfess().getManaDisplay().getIcon());
      holders.register("mana", io.lumine.mythic.lib.manager.StatManager.format("MAX_MANA", data.getMana()));
      holders.register("max_mana", io.lumine.mythic.lib.manager.StatManager.format("MAX_MANA", data.getStats().getStat("MAX_MANA")));
      holders.register("stamina", io.lumine.mythic.lib.manager.StatManager.format("MAX_STAMINA", data.getStamina()));
      holders.register("max_stamina", io.lumine.mythic.lib.manager.StatManager.format("MAX_STAMINA", data.getStats().getStat("MAX_STAMINA")));
      holders.register("stellium", io.lumine.mythic.lib.manager.StatManager.format("MAX_STELLIUM", data.getStellium()));
      holders.register("max_stellium", io.lumine.mythic.lib.manager.StatManager.format("MAX_STELLIUM", data.getStats().getStat("MAX_STELLIUM")));
      holders.register("class", data.getProfess().getName());
      holders.register("xp", MythicLib.plugin.getMMOConfig().decimal.format(data.getExperience()));
      holders.register("armor", io.lumine.mythic.lib.manager.StatManager.format("ARMOR", data.getPlayer().getAttribute(Attributes.ARMOR).getValue()));
      holders.register("level", String.valueOf(data.getLevel()));
      holders.register("name", data.getPlayer().getDisplayName());
      return holders;
   }
}

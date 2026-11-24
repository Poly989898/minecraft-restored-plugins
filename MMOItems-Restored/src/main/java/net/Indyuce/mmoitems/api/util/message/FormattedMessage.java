package net.Indyuce.mmoitems.api.util.message;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FormattedMessage {
   private final boolean actionBar;
   @NotNull
   private String message;

   public FormattedMessage(@NotNull Message var1) {
      this.message = var1.getRaw();
      this.actionBar = MMOItems.plugin.getConfig().getBoolean("action-bar-display." + var1.getActionBarConfigPath());
   }

   public FormattedMessage(String var1, boolean var2) {
      this.message = var1;
      this.actionBar = var2;
   }

   @NotNull
   public FormattedMessage format(@Nullable ChatColor var1, @NotNull String... var2) {
      if (this.message.isEmpty()) {
         return this;
      }

      if (var1 != null) {
         this.message = var1 + this.message;
      }

      for (byte var3 = 0; var3 < var2.length; var3 += 2) {
         this.message = this.message.replace(var2[var3], var2[var3 + 1]);
      }

      return this;
   }

   public void send(Player var1) {
      if (!this.message.isEmpty()) {
         this.message = MythicLib.plugin.parseColors(this.message);
         if (this.actionBar) {
            if (Bukkit.getPluginManager().isPluginEnabled("MMOCore")) {
               PlayerData.get(var1).setLastActivity(PlayerActivity.ACTION_BAR_MESSAGE);
            }

            MythicLib.plugin.getVersion().getWrapper().sendActionBar(var1, this.message);
         } else {
            var1.sendMessage(this.message);
         }
      }
   }

   @Override
   public String toString() {
      return this.message;
   }
}

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

   public FormattedMessage(@NotNull Message message) {
      this.message = message.getRaw();
      this.actionBar = MMOItems.plugin.getConfig().getBoolean("action-bar-display." + message.getActionBarConfigPath());
   }

   public FormattedMessage(String text, boolean flag) {
      this.message = text;
      this.actionBar = flag;
   }

   @NotNull
   public FormattedMessage format(@Nullable ChatColor chatColor, @NotNull String... texts) {
      if (this.message.isEmpty()) {
         return this;
      }

      if (chatColor != null) {
         this.message = chatColor + this.message;
      }

      for (byte b = 0; b < texts.length; b += 2) {
         this.message = this.message.replace(texts[b], texts[b + 1]);
      }

      return this;
   }

   public void send(Player player) {
      if (!this.message.isEmpty()) {
         this.message = MythicLib.plugin.parseColors(this.message);
         if (this.actionBar) {
            if (Bukkit.getPluginManager().isPluginEnabled("MMOCore")) {
               PlayerData.get(player).setLastActivity(PlayerActivity.ACTION_BAR_MESSAGE);
            }

            MythicLib.plugin.getVersion().getWrapper().sendActionBar(player, this.message);
         } else {
            player.sendMessage(this.message);
         }
      }
   }

   @Override
   public String toString() {
      return this.message;
   }
}

package net.Indyuce.mmocore.api;

import io.lumine.mythic.lib.MythicLib;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigMessage {
   private final String key;
   private final List<String> lines = new ArrayList<>();
   private final boolean papiPlaceholders;
   private final boolean actionbar;
   private final boolean raw;

   private ConfigMessage(@NotNull String key) {
      this.key = key;
      Object obj = MMOCore.plugin.configManager.getMessageObject(key);
      if (obj == null) {
         this.lines.add("<message_not_found:'" + key + "'>");
      } else if (obj instanceof List) {
         this.lines.addAll((List)obj);
      } else {
         String tostr = obj.toString();
         if (!tostr.isEmpty()) {
            this.lines.add(tostr);
         }
      }

      boolean hasPlaceholders = false;

      for (String str : this.lines) {
         if (str.contains("%")) {
            hasPlaceholders = true;
            break;
         }
      }

      this.papiPlaceholders = hasPlaceholders;
      this.actionbar = !this.lines.isEmpty() && this.lines.get(0).startsWith("%");
      if (this.actionbar) {
         this.lines.set(0, this.lines.get(0).substring(1));
      }

      this.raw = !this.lines.isEmpty() && (this.lines.get(0).startsWith("{") || this.lines.get(0).startsWith("["));
   }

   @NotNull
   public String asLine() {
      return this.lines.isEmpty() ? "" : this.lines.get(0);
   }

   @NotNull
   public List<String> getLines() {
      return this.lines;
   }

   @NotNull
   public ConfigMessage addPlaceholders(@NotNull Object... placeholders) {
      for (int n = 0; n < this.lines.size(); n++) {
         String line = this.lines.get(n);

         for (int j = 0; j < placeholders.length - 1; j += 2) {
            String placeholder = String.valueOf(placeholders[j]);
            line = line.replace("{" + placeholder + "}", String.valueOf(placeholders[j + 1]));
         }

         this.lines.set(n, line);
      }

      return this;
   }

   @Deprecated
   public void sendAsJSon(Player player) {
      this.send(player);
   }

   public void send(PlayerData playerData) {
      for (String line : this.lines) {
         this.send(playerData.getPlayer(), line);
      }
   }

   public void send(CommandSender player) {
      for (String line : this.lines) {
         this.send(player, line);
      }
   }

   public void send(Collection<? extends CommandSender> players) {
      for (CommandSender player : players) {
         for (String line : this.lines) {
            this.send(player, line);
         }
      }
   }

   private void send(@NotNull CommandSender recipient, String messageFormat) {
      Validate.notNull(recipient, "Recipient cannot be null");
      if (!(recipient instanceof Player)) {
         recipient.sendMessage(this.format(null, messageFormat));
      } else {
         Player player = (Player)recipient;
         String rawMessage = this.format(player, messageFormat);
         PlayerData playerData = PlayerData.has((Player)recipient) ? PlayerData.get((Player)recipient) : null;
         if (playerData != null && playerData.isOnline() && this.actionbar) {
            playerData.displayActionBar(rawMessage, this.raw);
         } else {
            if (this.raw) {
               if (this.actionbar) {
                  MythicLib.plugin.getVersion().getWrapper().sendActionBarRaw(player, rawMessage);
               } else {
                  MythicLib.plugin.getVersion().getWrapper().sendJson(player, rawMessage);
               }
            } else if (this.actionbar) {
               player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rawMessage));
            } else {
               player.sendMessage(rawMessage);
            }
         }
      }
   }

   @NotNull
   private String format(@Nullable Player player, String input) {
      input = MythicLib.plugin.parseColors(input);
      return this.papiPlaceholders && player != null ? MMOCore.plugin.placeholderParser.parse(player, input) : input;
   }

   @NotNull
   public static ConfigMessage fromKey(@NotNull String key, Object... placeholders) {
      Validate.notNull(MMOCore.plugin.configManager, "MMOCore has not finished enabling");
      ConfigMessage message = new ConfigMessage(key);
      if (placeholders.length != 0) {
         message.addPlaceholders(placeholders);
      }

      return message;
   }
}

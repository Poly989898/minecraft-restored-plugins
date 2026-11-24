package fr.phoenixdevt.mmoprofiles.bukkit.utils.message;

import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerMessage {
   private final Message message;
   private final List<String> format;

   public PlayerMessage(Message message) {
      this.format = (this.message = message).getCached();
   }

   public PlayerMessage format(Object... placeholders) {
      for (int k = 0; k < this.format.size(); k++) {
         this.format.set(k, this.apply(this.format.get(k), placeholders));
      }

      return this;
   }

   private String apply(String str, Object... placeholders) {
      for (int k = 0; k < placeholders.length; k += 2) {
         str = str.replace("{" + placeholders[k] + "}", placeholders[k + 1].toString());
      }

      return ChatColor.translateAlternateColorCodes('&', str);
   }

   public String getAsString() {
      StringBuilder builder = new StringBuilder();
      boolean notEmpty = false;

      for (String str : this.format) {
         if (notEmpty) {
            builder.append("\n");
         }

         builder.append(str);
         notEmpty = true;
      }

      return builder.toString();
   }

   public void send(Collection<? extends Player> senders) {
      senders.forEach(sender -> this.send(sender));
   }

   public void send(CommandSender sender) {
      if (!this.format.isEmpty()) {
         if (this.message.hasSound() && sender instanceof Player) {
            this.message.getSound().play((Player)sender);
         }

         this.format.forEach(str -> sender.sendMessage(str));
      }
   }
}

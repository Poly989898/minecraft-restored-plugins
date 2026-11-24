package net.Indyuce.mmoitems.api;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Consumer;

public class PluginUpdate {
   private final int id;
   private final Consumer<CommandSender> handler;
   private final List<String> description;

   public PluginUpdate(int count, String[] texts, Consumer<CommandSender> consumer) {
      Validate.notNull(consumer, "Update handler must not be null");
      Validate.notNull(texts, "Update description must not be null");
      this.id = count;
      this.handler = consumer;
      this.description = Arrays.asList(texts);
   }

   public int getId() {
      return this.id;
   }

   public void apply(CommandSender sender) {
      this.handler.accept(sender);
   }

   public List<String> getDescription() {
      return this.description;
   }

   public boolean hasDescription() {
      return !this.description.isEmpty();
   }
}

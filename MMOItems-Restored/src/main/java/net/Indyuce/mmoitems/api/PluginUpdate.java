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

   public PluginUpdate(int var1, String[] var2, Consumer<CommandSender> var3) {
      Validate.notNull(var3, "Update handler must not be null");
      Validate.notNull(var2, "Update description must not be null");
      this.id = var1;
      this.handler = var3;
      this.description = Arrays.asList(var2);
   }

   public int getId() {
      return this.id;
   }

   public void apply(CommandSender var1) {
      this.handler.accept(var1);
   }

   public List<String> getDescription() {
      return this.description;
   }

   public boolean hasDescription() {
      return !this.description.isEmpty();
   }
}

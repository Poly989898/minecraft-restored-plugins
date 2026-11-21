package it.ytnoos.lpx.commands;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {
   protected final LPXBukkit lpxBukkit;
   private final String name;
   private final String permission;
   private final String description;
   private final boolean playerOnly;
   private final String usage;

   protected SubCommand(LPXBukkit lpxBukkit2, String string, String string2, String string3, boolean flag) {
      this(lpxBukkit2, string, string2, string3, flag, "");
   }

   protected SubCommand(LPXBukkit lpxBukkit2, String string, String string2, String string3, boolean flag, String string4) {
      this.lpxBukkit = lpxBukkit2;
      this.name = string;
      this.permission = string2;
      this.description = string3;
      this.playerOnly = flag;
      this.usage = string4;
   }

   public abstract void execute(CommandSender commandSender, String[] strings);

   public List tabComplete(CommandSender commandSender, String[] strings) {
      return Collections.emptyList();
   }

   public String getName() {
      return this.name;
   }

   public String getPermission() {
      return this.permission;
   }

   public String getDescription() {
      return this.description;
   }

   public boolean isPlayerOnly() {
      return this.playerOnly;
   }

   public String getUsage() {
      return this.usage;
   }

   public void sendMessage(CommandSender commandSender, String string) {
      MessageFormatter.send(commandSender, this.lpxBukkit.getConfigManager().getMessagesConfig().getPrefix() + " " + string);
   }
}

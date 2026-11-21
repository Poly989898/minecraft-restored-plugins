package io.lumine.mythic.bukkit.utils.command;

import io.lumine.mythic.bukkit.utils.chat.ColorString;
import java.util.function.Consumer;
import org.bukkit.command.CommandSender;

public class CommandInterruptException extends Exception {
   private final Consumer<CommandSender> action;

   public static void makeAssertion(boolean condition, String failMsg) throws CommandInterruptException {
      if (!condition) {
         throw new CommandInterruptException(failMsg);
      }
   }

   public CommandInterruptException(Consumer<CommandSender> action) {
      this.action = action;
   }

   public CommandInterruptException(String message) {
      this.action = cs -> cs.sendMessage(ColorString.get(message));
   }

   public Consumer<CommandSender> getAction() {
      return this.action;
   }
}

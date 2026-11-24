package net.Indyuce.mmoitems.command.mmoitems;

import java.util.HashSet;
import java.util.Set;

public class GenerateCommandHandler {
   private final Set<String> arguments = new HashSet<>();

   public GenerateCommandHandler(String... texts) {
      for (String text : texts) {
         this.arguments.add(text.toLowerCase());
      }
   }

   public boolean hasArgument(String text) {
      for (String argument : this.arguments) {
         if (argument.startsWith("-" + text)) {
            return true;
         }
      }

      return false;
   }

   public String getValue(String text) {
      for (String argument : this.arguments) {
         if (argument.startsWith("-" + text + ":")) {
            return argument.substring(text.length() + 2);
         }
      }

      throw new IllegalArgumentException("Command has no argument '" + text + "'");
   }
}

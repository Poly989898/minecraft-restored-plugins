package net.Indyuce.mmoitems.command.mmoitems;

import java.util.HashSet;
import java.util.Set;

public class GenerateCommandHandler {
   private final Set<String> arguments = new HashSet<>();

   public GenerateCommandHandler(String... var1) {
      for (String var5 : var1) {
         this.arguments.add(var5.toLowerCase());
      }
   }

   public boolean hasArgument(String var1) {
      for (String var3 : this.arguments) {
         if (var3.startsWith("-" + var1)) {
            return true;
         }
      }

      return false;
   }

   public String getValue(String var1) {
      for (String var3 : this.arguments) {
         if (var3.startsWith("-" + var1 + ":")) {
            return var3.substring(var1.length() + 2);
         }
      }

      throw new IllegalArgumentException("Command has no argument '" + var1 + "'");
   }
}

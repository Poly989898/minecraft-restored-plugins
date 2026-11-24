package net.Indyuce.mmoitems.stat.data;

import org.apache.commons.lang.Validate;

public class CommandData {
   private final String command;
   private final double delay;
   private final boolean console;
   private final boolean op;

   public CommandData(String var1, double var2, boolean var4, boolean var5) {
      Validate.notNull(var1, "Command cannot be null");
      this.command = var1;
      this.delay = var2;
      this.console = var4;
      this.op = var5;
   }

   public String getCommand() {
      return this.command;
   }

   public double getDelay() {
      return this.delay;
   }

   public boolean hasDelay() {
      return this.delay > 0.0;
   }

   public boolean isConsoleCommand() {
      return this.console;
   }

   public boolean hasOpPerms() {
      return this.op;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         CommandData var2 = (CommandData)var1;
         return Double.compare(var2.delay, this.delay) == 0 && this.console == var2.console && this.op == var2.op && this.command.equals(var2.command);
      } else {
         return false;
      }
   }
}

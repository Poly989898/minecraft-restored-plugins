package net.Indyuce.mmoitems.stat.data;

import org.apache.commons.lang.Validate;

public class CommandData {
   private final String command;
   private final double delay;
   private final boolean console;
   private final boolean op;

   public CommandData(String text, double value, boolean flag, boolean flag2) {
      Validate.notNull(text, "Command cannot be null");
      this.command = text;
      this.delay = value;
      this.console = flag;
      this.op = flag2;
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
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         CommandData commandData = (CommandData)obj;
         return Double.compare(commandData.delay, this.delay) == 0 && this.console == commandData.console && this.op == commandData.op && this.command.equals(commandData.command);
      } else {
         return false;
      }
   }
}

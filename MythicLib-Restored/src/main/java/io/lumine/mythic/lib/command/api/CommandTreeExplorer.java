package io.lumine.mythic.lib.command.api;

import java.util.List;

public class CommandTreeExplorer {
   private final String[] args;
   private CommandTreeNode current;
   private int parameter = 0;

   public CommandTreeExplorer(CommandTreeRoot explored, String[] args) {
      this.current = explored;
      this.args = args;

      for (String arg : args) {
         if (this.parameter == 0 && this.current.hasChild(arg)) {
            this.current = this.current.getChild(arg);
         } else {
            this.parameter++;
         }
      }
   }

   public CommandTreeNode getNode() {
      return this.current;
   }

   public String[] getArguments() {
      return this.args;
   }

   public int extraCount() {
      return this.parameter;
   }

   public List<String> calculateTabCompletion() {
      return this.current.calculateTabCompletion(this, Math.max(0, this.parameter - 1));
   }
}

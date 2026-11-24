package me.TechsCode.UltraPermissions.base.command;

public class ExecuteCallback {
   private CommandNode<?, ?> lastNode;
   private ExecuteResult result;

   public ExecuteCallback(CommandNode<?, ?> commandNode, ExecuteResult executeResult) {
      this.lastNode = commandNode;
      this.result = executeResult;
   }

   public CommandNode<?, ?> getLastNode() {
      return this.lastNode;
   }

   public ExecuteResult getResult() {
      return this.result;
   }
}

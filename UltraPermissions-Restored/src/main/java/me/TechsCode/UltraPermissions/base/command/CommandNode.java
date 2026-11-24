package me.TechsCode.UltraPermissions.base.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArgument;
import me.TechsCode.UltraPermissions.base.misc.Callback;
import me.TechsCode.UltraPermissions.base.misc.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNode<T, ARGUMENT extends Argument<T>> {
   private final CommandNode<?, ?> parent;
   private final ARGUMENT argument;
   private final List<CommandNode<?, ?>> children;
   private final List<Requirement> requirements;
   private String description;
   private Action<T> action;

   public static CommandNode<String, SpecificArgument> create(String text) {
      return new CommandNode<>(null, new SpecificArgument(text));
   }

   public CommandNode(CommandNode<?, ?> parent, ARGUMENT argument) {
      this.parent = parent;
      this.children = new ArrayList<>();
      this.argument = (ARGUMENT)argument;
      this.requirements = new ArrayList<>();
      this.description = null;
      this.action = null;
   }

   public <NEW_T, NEW_ARGUMENT extends Argument<NEW_T>> CommandNode<T, ARGUMENT> newNode(NEW_ARGUMENT argument, Setter<CommandNode<NEW_T, NEW_ARGUMENT>> setter) {
      CommandNode node = new CommandNode(this, (ARGUMENT)argument);
      setter.set(node);
      this.children.add(node);
      return this;
   }

   public void description(String text) {
      this.description = text;
   }

   public void require(Requirement requirement) {
      this.requirements.add(requirement);
   }

   public void action(Action<T> action) {
      this.action = action;
   }

   public CommandNode<?, ?> getCurrentNode(CommandSender sender, Player player, List<String> texts) {
      return this.getCurrentNode(sender, player, texts, 0);
   }

   private CommandNode<?, ?> getCurrentNode(CommandSender sender, Player player, List<String> texts, int index) {
      if (texts.size() == index) {
         return null;
      }

      String argument = (String)texts.get(index);
      if (this.argument.hasMatch(argument)) {
         for (Requirement requirement : this.requirements) {
            if (!requirement.isMatching(sender, player, texts)) {
               return null;
            }
         }

         int remaining = texts.size() - (index + 1);
         if (remaining == 0) {
            return this;
         }

         for (CommandNode child : this.children) {
            CommandNode foundNode = child.getCurrentNode(sender, player, texts, index + 1);
            if (foundNode != null) {
               return foundNode;
            }
         }
      }

      return null;
   }

   public void execute(CommandSender sender, Player player, Arguments arguments, Callback<ExecuteCallback> callback) {
      this.execute(sender, player, arguments, 0, callback);
   }

   private boolean execute(CommandSender sender, Player player, Arguments arguments, int index, Callback<ExecuteCallback> callback) {
      if (arguments.size() == index) {
         return false;
      }

      String argument = arguments.get(index);
      if (this.argument.hasMatch(argument)) {
         for (Requirement requirement : this.requirements) {
            if (!requirement.isMatching(sender, player, arguments)) {
               requirement.onUnmatched(sender, player);
               return true;
            }
         }

         for (CommandNode child : this.children) {
            boolean executed = child.execute(sender, player, arguments, index + 1, callback);
            if (executed) {
               return true;
            }
         }

         if (this.action != null) {
            this.action.run(sender, player, arguments);
            callback.run(new ExecuteCallback(this, ExecuteResult.SUCCESSFUL));
         } else {
            callback.run(new ExecuteCallback(this, ExecuteResult.NO_ACTION));
         }

         return true;
      } else {
         return false;
      }
   }

   public String getUsagePath() {
      return this.getNodesOnPath().stream().map(node -> node.argument.getUsage()).collect(Collectors.joining(" "));
   }

   public ArgumentValue<T> getValue(Arguments arguments) {
      int depth = 0;

      for (CommandNode node = this.parent; node != null; depth++) {
         node = node.parent;
      }

      String argument = arguments.get(depth);
      return this.argument.get(argument);
   }

   public List<String> getSpareArguments(Arguments arguments) {
      int depth = 0;

      for (CommandNode node = this; node != null; depth++) {
         node = node.parent;
      }

      return arguments.subList(depth, arguments.size());
   }

   public List<CommandNode<?, ?>> getEndNodes() {
      if (this.children.size() == 0) {
         return Collections.singletonList(this);
      }

      ArrayList endNodes = new ArrayList();

      for (CommandNode child : this.children) {
         endNodes.addAll(child.getEndNodes());
      }

      return endNodes;
   }

   public List<CommandNode<?, ?>> getNodesOnPath() {
      ArrayList nodes = new ArrayList();
      nodes.add(this);
      CommandNode node = this;

      while (node != null) {
         node = node.parent;
         if (node != null) {
            nodes.add(node);
         }
      }

      Collections.reverse(nodes);
      return nodes;
   }

   public List<CommandNode<?, ?>> getChildren() {
      return this.children;
   }

   public boolean hasDescription() {
      return this.description != null;
   }

   public String getDescription() {
      return this.description;
   }

   public ARGUMENT getArgument() {
      return this.argument;
   }

   public List<Requirement> getRequirements() {
      return this.requirements;
   }

   public List<Requirement> getAllRequirements() {
      return this.getNodesOnPath().stream().flatMap(node -> node.getRequirements().stream()).filter(Objects::nonNull).collect(Collectors.toList());
   }
}

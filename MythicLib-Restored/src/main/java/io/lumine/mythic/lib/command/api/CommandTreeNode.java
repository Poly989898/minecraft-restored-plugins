package io.lumine.mythic.lib.command.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class CommandTreeNode {
   private final String id;
   private final CommandTreeNode parent;
   private final Map<String, CommandTreeNode> children = new HashMap<>();
   private final List<Parameter> parameters = new ArrayList<>();

   public CommandTreeNode(CommandTreeNode parent, String id) {
      this.id = id;
      this.parent = parent;
   }

   public String getId() {
      return this.id;
   }

   public String getPath() {
      return (this.hasParent() ? this.parent.getPath() + " " : "") + this.getId();
   }

   public Collection<CommandTreeNode> getChildren() {
      return this.children.values();
   }

   public boolean hasParameters() {
      return !this.parameters.isEmpty();
   }

   public List<Parameter> getParameters() {
      return this.parameters;
   }

   public boolean hasParent() {
      return this.parent != null;
   }

   public boolean hasChild(String id) {
      return this.children.containsKey(id.toLowerCase());
   }

   public CommandTreeNode getChild(String id) {
      return this.children.get(id.toLowerCase());
   }

   public void addChild(CommandTreeNode child) {
      this.children.put(child.getId(), child);
   }

   public void addParameter(Parameter parameter) {
      this.parameters.add(parameter);
   }

   @NotNull
   public abstract CommandTreeNode.CommandResult execute(CommandSender var1, String[] var2);

   public List<String> calculateTabCompletion(CommandTreeExplorer explorer, int parameterIndex) {
      List<String> list = new ArrayList<>();
      this.getChildren().forEach(child -> list.add(child.getId()));
      if (this.getParameters().size() > parameterIndex) {
         this.getParameters().get(parameterIndex).autoComplete(explorer, list);
      }

      return list;
   }

   public List<String> calculateUsageList() {
      return this.calculateUsageList(this.getPath(), new ArrayList<>());
   }

   private List<String> calculateUsageList(String path, List<String> usages) {
      if (this.hasParameters() || this.getChildren().isEmpty()) {
         usages.add(path + " " + this.formatParameters());
      }

      for (CommandTreeNode child : this.getChildren()) {
         child.calculateUsageList(path + " " + child.getId(), usages);
      }

      return usages;
   }

   public String formatParameters() {
      StringBuilder str = new StringBuilder();

      for (Parameter param : this.parameters) {
         str.append(param.format()).append(" ");
      }

      return str.length() == 0 ? str.toString() : str.substring(0, str.length() - 1);
   }

   public enum CommandResult {
      SUCCESS,
      FAILURE,
      THROW_USAGE;
   }
}

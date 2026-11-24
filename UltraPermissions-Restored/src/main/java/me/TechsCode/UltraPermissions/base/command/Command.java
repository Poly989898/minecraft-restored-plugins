package me.TechsCode.UltraPermissions.base.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArgument;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import me.TechsCode.UltraPermissions.base.reflection.titleAndActionbar.ActionBar;
import me.TechsCode.UltraPermissions.base.scheduler.RecurringTask;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Command extends BukkitCommand implements Listener {
   private static final Phrase USAGE_TITLE = Phrase.create("command.usage.title", "Usage:", Colors.BLUE);
   private final ActionBar actionBar;
   private final String command;
   private final ArrayList<String> alias;
   private final String perm;
   private boolean isCurrentlyRegistered;

   public Command(SpigotTechPlugin spigotTechPlugin, String text, String perm, String... texts) {
      this(spigotTechPlugin, text, "", "/" + text, perm, texts);
   }

   public Command(SpigotTechPlugin spigotTechPlugin, String text, String perm) {
      this(spigotTechPlugin, text, "", "/" + text, perm, new String[0]);
   }

   public Command(SpigotTechPlugin spigotTechPlugin, String text, String description, String usage, String perm, String... texts) {
      super(text, description, usage, Arrays.asList(texts));
      this.command = text;
      this.perm = perm;
      this.actionBar = new ActionBar(spigotTechPlugin);
      this.alias = new ArrayList<>();
      this.alias.addAll(Arrays.asList(texts));
      if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_13_R1)) {
         spigotTechPlugin.getBootstrap().getServer().getPluginManager().registerEvents(new CommandEvent(this.perm, this.command, this.alias), (Plugin)spigotTechPlugin.getBootstrap());
      }

      if (this.isRegistered()) {
         this.register();
      }

      RecurringTask recurringTask = spigotTechPlugin.getScheduler().runTaskTimer(() -> {
         if (this.isRegistered() && !this.isCurrentlyRegistered) {
            this.register();
         }

         if (!this.isRegistered() && this.isCurrentlyRegistered) {
            this.unregister();
         }
      }, 20L, 20L);
      spigotTechPlugin.addDisableHook(() -> {
         recurringTask.stop();
         if (this.isCurrentlyRegistered) {
            this.unregister();
         }
      });
   }

   public abstract void define(CommandSender sender, CommandNode<String, SpecificArgument> commandNode);

   public boolean execute(CommandSender sender, String text, String[] texts) {
      Arguments list = new Arguments();
      list.add(this.getName());
      list.addAll(Arrays.asList(texts));
      Player player = sender instanceof Player ? (Player)sender : null;
      CommandNode<String, SpecificArgument> commandNode = CommandNode.create(this.getName());
      this.define(sender, commandNode);
      commandNode.execute(
         sender,
         player,
         list,
         executeCallback -> {
            if (executeCallback.getResult() == ExecuteResult.NO_ACTION) {
               sender.sendMessage("");
               sender.sendMessage(USAGE_TITLE.get());

               for (CommandNode<?, ?> childNode : executeCallback.getLastNode().getChildren()) {
                  boolean flag = childNode.getAllRequirements().stream().allMatch(requirement -> requirement.isMatching(sender, player, list));
                  if (flag) {
                     sender.sendMessage(
                        Colors.GRAY
                           + "- "
                           + Colors.YELLOW
                           + "/"
                           + childNode.getUsagePath()
                           + (childNode.hasDescription() ? " " + Colors.GRAY + childNode.getDescription() : "")
                     );
                  }
               }
            }
         }
      );
      return false;
   }

   public List<String> tabComplete(CommandSender sender, String text, String[] texts) {
      Arguments list = new Arguments();
      list.add(this.getName());
      list.addAll(Arrays.asList(texts));
      Player player = sender instanceof Player ? (Player)sender : null;
      CommandNode<String, SpecificArgument> commandNode = CommandNode.create(this.getName());
      this.define(sender, commandNode);
      CommandNode<?, ?> currentNode = commandNode.getCurrentNode(sender, player, list.subList(0, list.size() - 1));
      if (currentNode == null) {
         return new ArrayList<>();
      }

      if (player != null) {
         List<CommandNode<?, ?>> endNodes = currentNode.getEndNodes();
         if (endNodes.size() == 1) {
            CommandNode<?, ?> fullCurrentNode = commandNode.getCurrentNode(sender, player, list);
            CommandNode<?, ?> endNode = endNodes.get(0);
            this.actionBar.sendActionBar(player, this.getTabCompletionActionBar(fullCurrentNode, endNode, list));
         }
      }

      return currentNode.getChildren()
         .stream()
         .flatMap(endNode -> endNode.getArgument().getTabCompletions().stream())
         .filter(completion -> completion.toLowerCase().startsWith(texts[texts.length - 1].toLowerCase()))
         .collect(Collectors.toList());
   }

   private String getTabCompletionActionBar(CommandNode<?, ?> commandNode, CommandNode<?, ?> endNode, Arguments list) {
      StringBuilder builder = new StringBuilder();
      int index = 0;

      for (CommandNode<?, ?> pathNode : endNode.getNodesOnPath()) {
         String usage = pathNode.getArgument().getUsage();
         boolean flag = index < list.size();
         String text = flag ? list.get(index) : null;
         if (index == list.size() - 1) {
            builder.append(" ").append(text.isEmpty() ? Colors.GREEN : Colors.YELLOW).append(text.isEmpty() ? usage : text);
         } else if (index < list.size()) {
            builder.append(" ").append(Colors.GRAY).append(text);
         } else {
            builder.append(" ").append(Colors.GRAY).append(usage);
         }

         index++;
      }

      String text = list.get(list.size() - 1);
      boolean flag = endNode.equals(commandNode) && !text.isEmpty() && commandNode.getArgument().hasMatch(text);
      return Colors.GRAY + "/" + builder.toString().trim() + (flag ? Colors.GREEN + "\u00a7l \u2713" : "");
   }

   private void register() {
      try {
         Field declaredField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         declaredField.setAccessible(true);
         CommandMap commandMap = (CommandMap)declaredField.get(Bukkit.getServer());
         commandMap.register("command", this);
         this.isCurrentlyRegistered = true;
      } catch (NoSuchFieldException | IllegalAccessException exception) {
         exception.printStackTrace();
      }
   }

   private void unregister() {
      try {
         Field declaredField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         declaredField.setAccessible(true);
         CommandMap commandMap = (CommandMap)declaredField.get(Bukkit.getServer());
         ArrayList<Field> fields = new ArrayList<>(Arrays.asList(commandMap.getClass().getDeclaredFields()));
         fields.addAll(new ArrayList<>(Arrays.asList(commandMap.getClass().getSuperclass().getDeclaredFields())));
         Field field = fields.stream().filter(knownCommandsField -> knownCommandsField.getName().equals("knownCommands")).findAny().get();
         field.setAccessible(true);
         Map map = (Map)field.get(commandMap);
         map.remove(this.getName());
         field.set(commandMap, map);
         this.isCurrentlyRegistered = false;
      } catch (NoSuchFieldException | IllegalAccessException exception) {
         exception.printStackTrace();
      }
   }

   public abstract boolean isRegistered();
}

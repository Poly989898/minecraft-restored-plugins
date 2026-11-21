package io.lumine.mythic.core.skills.commands;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.utils.MythicUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class CommandSkillExecutor implements Listener {
   private static final Set<CommandSkillExecutor.MetaSkillCommand> COMMANDS = new HashSet<>();
   private final Map<String, Function<MythicConfig, CustomCommandFunction>> commandFunctions = Maps.newConcurrentMap();
   private final Map<String, Function<MythicConfig, CustomCommandTabCompleteFunction>> tabCompleteFunctions = Maps.newConcurrentMap();

   public CommandSkillExecutor() {
      this.registerCustomCommandFunction("echo", mc -> (player, args, config) -> player.sendMessage(mc.getString("Message")));
      this.registerCustomCommandTabCompleteFunction("$worlds", c -> (player, args) -> Bukkit.getWorlds().stream().map(w -> w.getName()).toList());
      this.registerCustomCommandTabCompleteFunction("$players", c -> (player, args) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());
      this.registerCustomCommandTabCompleteFunction("$items", c -> (player, args) -> {
         ArrayList<String> list = new ArrayList<>();

         for (ItemType m : Registry.ITEM) {
            list.add(m.getKey().value());
         }

         return list;
      });
      this.registerCustomCommandTabCompleteFunction("$enchantments", c -> (player, args) -> {
         ArrayList<String> list = new ArrayList<>();

         for (Enchantment m : Registry.ENCHANTMENT) {
            list.add(m.getKey().toString());
         }

         return list;
      });
      this.registerCustomCommandTabCompleteFunction(
         "$mythicitems",
         c -> (player, args) -> MythicBukkit.inst()
            .getItemManager()
            .getItems()
            .stream()
            .filter(item -> !item.isHidden())
            .map(MythicItem::getInternalName)
            .toList()
      );
   }

   public void reloadCommands() {
      popCommands();
      this.loadCommands();
      pushCommands();
      syncCommands();
   }

   static void popCommands() {
      COMMANDS.forEach(c -> Bukkit.getCommandMap().getKnownCommands().remove(c.getName()));
   }

   void loadCommands() {
      COMMANDS.clear();
      ArrayList<CommandSkillExecutor.CommandGroup> commandGroups = new ArrayList<>();
      HashMap<String, CommandSkillExecutor.CommandGroup> commandGroupsBySkillName = new HashMap<>();

      for (Skill skill : MythicBukkit.inst().getSkillManager().getSkills()) {
         if (skill.getConfig() != null && skill.getConfig().isSet("Command")) {
            MythicConfig config = skill.getConfig().getNestedConfig("Command");
            String id = config.getString("Id", null);
            if (id != null) {
               CustomCommandFunction commandFunction;
               if (config.isSet("Function")) {
                  if (config.isConfigurationSection("Function")) {
                     MythicConfig sub = config.getNestedConfig("Function");
                     commandFunction = this.getCommandFunction(sub.getString("Type"), sub).orElse(null);
                  } else {
                     commandFunction = this.getCommandFunction(config.getString("Function"), null).orElse(null);
                  }
               } else {
                  commandFunction = null;
               }

               List<String> aliases = config.getStringList("Aliases", List.of());
               MythicConfig completionsConfig = config.isConfigurationSection("Completions") ? config.getNestedConfig("Completions") : null;
               String parent = getConfiguredParent(config);
               CommandSkillExecutor.CommandGroup commandGroup = new CommandSkillExecutor.CommandGroup(skill.getInternalName(), parent);
               LinkedHashSet<String> names = new LinkedHashSet<>();
               names.add(id);
               names.addAll(aliases);

               for (String name : names) {
                  if (name != null && !name.isBlank()) {
                     CommandSkillExecutor.MetaSkillCommand command = this.makeCommand(name, skill.getInternalName(), config, commandFunction, completionsConfig);
                     commandGroup.addCommand(command);
                  }
               }

               if (!commandGroup.commands().isEmpty()) {
                  commandGroups.add(commandGroup);
                  commandGroupsBySkillName.put(commandGroup.skill(), commandGroup);
               }
            }
         }
      }

      for (CommandSkillExecutor.CommandGroup commandGroup : commandGroups) {
         if (commandGroup.parent() == null) {
            COMMANDS.addAll(commandGroup.commands());
         } else {
            CommandSkillExecutor.CommandGroup parentGroup = commandGroupsBySkillName.get(commandGroup.parent());
            if (parentGroup == null) {
               MythicLogger.error(
                  "Skill command {0} specified parent skill {1}, but no custom skill command for that skill exists.",
                  commandGroup.skill(),
                  commandGroup.parent()
               );
            } else {
               for (CommandSkillExecutor.MetaSkillCommand parentCommand : parentGroup.commands()) {
                  for (CommandSkillExecutor.MetaSkillCommand childCommand : commandGroup.commands()) {
                     parentCommand.addChild(childCommand);
                  }
               }
            }
         }
      }

      int loadedCommands = commandGroups.stream().mapToInt(group -> group.commands().size()).sum();
      MythicLogger.log("Loaded {0} skill commands", loadedCommands);
   }

   static void pushCommands() {
      for (CommandSkillExecutor.MetaSkillCommand c : COMMANDS) {
         Bukkit.getCommandMap().getKnownCommands().put(c.getName(), c);
         Bukkit.getServer().getCommandMap().register(MythicBukkit.inst().getName(), c);
      }
   }

   static void syncCommands() {
      try {
         Method m = Bukkit.getServer().getClass().getDeclaredMethod("syncCommands");
         m.setAccessible(true);
         m.invoke(Bukkit.getServer());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   CommandSkillExecutor.MetaSkillCommand makeCommand(
      String name, String skill, MythicConfig commandConfig, CustomCommandFunction commandFunction, MythicConfig completionsConfig
   ) {
      CommandSkillExecutor.MetaSkillCommand cmd = new CommandSkillExecutor.MetaSkillCommand(normalizeCommandName(name), skill, commandConfig, commandFunction);
      if (completionsConfig != null) {
         for (String key : completionsConfig.getKeys()) {
            List<String> literals = completionsConfig.getStringList(key, List.of());
            CustomCommandTabCompleteFunction tabFunction = null;
            if (!literals.isEmpty()) {
               String first = literals.get(0);
               tabFunction = this.getTabCompleteFunction(first, completionsConfig).orElse(null);
            }

            cmd.addCompletion(key, literals, tabFunction);
         }
      }

      return cmd;
   }

   private static String normalizeCommandName(String name) {
      return name == null ? null : name.toLowerCase(Locale.ROOT);
   }

   private static String getConfiguredParent(MythicConfig config) {
      String parent = config.getString("Parent", null);
      return parent != null && !parent.isBlank() ? parent.trim() : null;
   }

   public void registerCustomCommandFunction(String key, Function<MythicConfig, CustomCommandFunction> function) {
      this.commandFunctions.put(key.toUpperCase(), function);
   }

   public void registerCustomCommandTabCompleteFunction(String key, Function<MythicConfig, CustomCommandTabCompleteFunction> function) {
      this.tabCompleteFunctions.put(key.toUpperCase(), function);
   }

   Optional<CustomCommandFunction> getCommandFunction(String key, MythicConfig config) {
      Function<MythicConfig, CustomCommandFunction> f = this.commandFunctions.get(key.toUpperCase());
      return f == null ? Optional.empty() : Optional.ofNullable(f.apply(config));
   }

   Optional<CustomCommandTabCompleteFunction> getTabCompleteFunction(String key, MythicConfig config) {
      Function<MythicConfig, CustomCommandTabCompleteFunction> f = this.tabCompleteFunctions.get(key.toUpperCase());
      return f == null ? Optional.empty() : Optional.ofNullable(f.apply(config));
   }

   private static class CommandGroup {
      private final String skill;
      private final String parent;
      private final List<CommandSkillExecutor.MetaSkillCommand> commands = new ArrayList<>();

      CommandGroup(String skill, String parent) {
         this.skill = skill;
         this.parent = parent;
      }

      String skill() {
         return this.skill;
      }

      String parent() {
         return this.parent;
      }

      List<CommandSkillExecutor.MetaSkillCommand> commands() {
         return this.commands;
      }

      void addCommand(CommandSkillExecutor.MetaSkillCommand command) {
         this.commands.add(command);
      }
   }

   static class MetaSkillCommand extends Command {
      String skill;
      MythicConfig commandConfig;
      CustomCommandFunction commandFunction;
      LinkedList<Pair<String, List<String>>> commandCompletions = new LinkedList<>();
      List<CustomCommandTabCompleteFunction> tabFunctions = new ArrayList<>();
      Map<String, CommandSkillExecutor.MetaSkillCommand> childCommands = new LinkedHashMap<>();

      protected MetaSkillCommand(String name, String skill, MythicConfig commandConfig, CustomCommandFunction commandFunction) {
         super(name, "", "", List.of());
         this.skill = skill;
         this.commandConfig = commandConfig;
         this.commandFunction = commandFunction;
      }

      public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
         if (args.length > 0) {
            CommandSkillExecutor.MetaSkillCommand childCommand = this.childCommands.get(CommandSkillExecutor.normalizeCommandName(args[0]));
            if (childCommand != null) {
               return childCommand.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
            }
         }

         if (sender instanceof Player player) {
            LivingEntity target = MythicUtil.getTargetedEntity(player);
            List<Entity> targets = new ArrayList<>();
            targets.add(target);
            MythicBukkit.inst().getAPIHelper().castSkill(player, this.skill, player, player.getLocation(), targets, null, 1.0F, meta -> {
               Log.info("execute command");
               if (this.commandFunction != null) {
                  Log.info("execute function");
                  this.commandFunction.execute(player, args, meta);
               }

               for (int i = 0; i < Math.min(args.length, this.commandCompletions.size()); i++) {
                  meta.getVariables().putString(this.commandCompletions.get(i).getKey(), args[i]);
               }

               if (args.length > this.commandCompletions.size()) {
                  meta.getVariables().putString("excessArgs", String.join(" ", Arrays.copyOfRange(args, this.commandCompletions.size(), args.length)));
               }
            });
            return true;
         } else {
            return false;
         }
      }

      @NotNull
      public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
         if (sender instanceof Player player) {
            if (args.length == 0) {
               return List.of();
            } else {
               if (args.length > 1) {
                  CommandSkillExecutor.MetaSkillCommand childCommand = this.childCommands.get(CommandSkillExecutor.normalizeCommandName(args[0]));
                  if (childCommand != null) {
                     return childCommand.tabComplete(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                  }
               }

               int index = args.length - 1;
               ArrayList<String> results = new ArrayList<>();
               if (index < this.tabFunctions.size()) {
                  CustomCommandTabCompleteFunction fn = this.tabFunctions.get(index);
                  if (fn != null) {
                     results.addAll(CollectionUtils.filterMatches(args[index], fn.tabComplete(player, args), 2));
                  } else if (index < this.commandCompletions.size()) {
                     results.addAll(CollectionUtils.filterMatches(args[index], (Collection<String>)this.commandCompletions.get(index).getValue(), 2));
                  }
               }

               if (index == 0 && !this.childCommands.isEmpty()) {
                  results.addAll(CollectionUtils.filterMatches(args[index], new ArrayList<>(this.childCommands.keySet()), 2));
               }

               return results;
            }
         } else {
            return List.of();
         }
      }

      public void addCompletion(String key, List<String> literals, CustomCommandTabCompleteFunction tabFunction) {
         this.commandCompletions.add(Pair.of(key, literals));
         this.tabFunctions.add(tabFunction);
      }

      public void addChild(CommandSkillExecutor.MetaSkillCommand childCommand) {
         this.childCommands.put(childCommand.getName(), childCommand);
      }
   }
}

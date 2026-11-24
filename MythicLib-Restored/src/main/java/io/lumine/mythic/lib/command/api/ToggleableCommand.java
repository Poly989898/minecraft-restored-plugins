package io.lumine.mythic.lib.command.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToggleableCommand {
   private final String name;
   private final String description;
   private final String permission;
   private final Function<ConfigurationSection, CommandTreeRoot> generator;
   private final List<String> aliases;
   @Nullable
   private final Predicate<Void> enabled;
   private final boolean forced;

   public ToggleableCommand(
      @NotNull String name,
      @Nullable String permission,
      @NotNull String description,
      @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
      @NotNull String... aliases
   ) {
      this(name, permission, description, generator, null, aliases);
   }

   public ToggleableCommand(
      @NotNull String name,
      @Nullable String permission,
      @NotNull String description,
      @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
      @Nullable Predicate<Void> enabled,
      @NotNull String... aliases
   ) {
      this(name, permission, description, generator, enabled, false, aliases);
   }

   public ToggleableCommand(
      @NotNull String name,
      @Nullable String permission,
      @NotNull String description,
      @NotNull Function<ConfigurationSection, CommandTreeRoot> generator,
      @Nullable Predicate<Void> enabled,
      boolean forced,
      @NotNull String... aliases
   ) {
      this.name = name;
      this.description = description;
      this.generator = generator;
      this.aliases = Arrays.asList(aliases);
      this.permission = permission;
      this.enabled = enabled;
      this.forced = forced;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @NotNull
   public String getDescription() {
      return this.description;
   }

   @NotNull
   public List<String> getAliases() {
      return this.aliases;
   }

   @Nullable
   public String getPermission() {
      return this.permission;
   }

   public boolean isForced() {
      return this.forced;
   }

   @NotNull
   public String getConfigPath() {
      return this.name.toLowerCase().replace("_", "-");
   }

   @Deprecated
   @NotNull
   public CommandTreeRoot generate(ConfigurationSection config) {
      return this.generator.apply(config);
   }

   @NotNull
   public BukkitCommand toBukkit(ConfigurationSection config) {
      return this.generator.apply(config).getCommand();
   }

   public boolean isEnabled() {
      return this.enabled == null || this.enabled.test(null);
   }
}

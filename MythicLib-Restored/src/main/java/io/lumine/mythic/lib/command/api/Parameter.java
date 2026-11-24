package io.lumine.mythic.lib.command.api;

import io.lumine.mythic.lib.MythicLib;
import java.util.List;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parameter {
   private final String key;
   @Nullable
   private final Boolean optional;
   private final BiConsumer<CommandTreeExplorer, List<String>> autoComplete;
   public static final Parameter PLAYER = new Parameter(
      "player", false, (explorer, list) -> Bukkit.getOnlinePlayers().forEach(online -> list.add(online.getName()))
   );
   @Deprecated
   public static final Parameter PLAYER_OPTIONAL = PLAYER.optional(true);
   public static final Parameter AMOUNT = new Parameter("amount", false, (explorer, list) -> {
      for (int j = 1; j <= 10; j++) {
         list.add(String.valueOf(j));
      }
   });
   @Deprecated
   public static final Parameter AMOUNT_OPTIONAL = AMOUNT.optional(true);
   public static final Parameter DURATION_TICKS = new Parameter("duration", false, (explorer, list) -> {
      for (int j = 1; j <= 10; j += 2) {
         list.add(String.valueOf(j * 20));
      }
   });
   public static final Parameter MATERIAL = new Parameter("material", false, (explorer, list) -> {
      for (Material material : Material.values()) {
         list.add(material.name());
      }
   });
   public static final Parameter STAT = new Parameter("stat", false, (explorer, list) -> list.addAll(MythicLib.plugin.getStats().getRegisteredStats()));

   @Deprecated
   public Parameter(@NotNull String key, @NotNull BiConsumer<CommandTreeExplorer, List<String>> autoComplete) {
      this(key, null, autoComplete);
   }

   @Deprecated
   public Parameter(@NotNull String key, @Nullable Boolean optional, @NotNull BiConsumer<CommandTreeExplorer, List<String>> autoComplete) {
      this.key = key;
      this.optional = optional;
      this.autoComplete = autoComplete;
   }

   public Parameter(@NotNull String key, boolean optional, @NotNull BiConsumer<CommandTreeExplorer, List<String>> autoComplete) {
      this.key = key;
      this.optional = optional;
      this.autoComplete = autoComplete;
   }

   @Deprecated
   public String getKey() {
      return this.key;
   }

   @NotNull
   public String format() {
      if (this.optional != null) {
         StringBuilder builder = new StringBuilder();
         builder.append(this.optional ? "(" : "<");
         builder.append(this.key);
         builder.append(this.optional ? ")" : ">");
         return builder.toString();
      } else {
         return this.key;
      }
   }

   @NotNull
   public Parameter key(@NotNull String key) {
      return new Parameter(key, this.optional, this.autoComplete);
   }

   @NotNull
   public Parameter optional(boolean optional) {
      return new Parameter(this.key, optional, this.autoComplete);
   }

   public void autoComplete(CommandTreeExplorer explorer, List<String> list) {
      this.autoComplete.accept(explorer, list);
   }
}

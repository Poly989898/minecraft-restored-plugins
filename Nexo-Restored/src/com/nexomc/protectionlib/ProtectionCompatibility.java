package com.nexomc.protectionlib;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003B\u0017\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00028\u0000¢\u0006\u0004\b\u0007\u0010\bJ\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H&J\u0018\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H&J\u0018\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H&J\u0018\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H&R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0006\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\f¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/protectionlib/ProtectionCompatibility;", "T", "Lorg/bukkit/plugin/Plugin;", "", "mainPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "plugin", "<init>", "(Lorg/bukkit/plugin/java/JavaPlugin;Lorg/bukkit/plugin/Plugin;)V", "getMainPlugin", "()Lorg/bukkit/plugin/java/JavaPlugin;", "getPlugin", "()Lorg/bukkit/plugin/Plugin;", "Lorg/bukkit/plugin/Plugin;", "canBuild", "", "player", "Lorg/bukkit/entity/Player;", "target", "Lorg/bukkit/Location;", "canBreak", "canInteract", "canUse", "protectionlib"}
)
public abstract class ProtectionCompatibility<T extends Plugin> {
   @NotNull
   private final JavaPlugin mainPlugin;
   @NotNull
   private final T plugin;

   public ProtectionCompatibility(@NotNull JavaPlugin mainPlugin, @NotNull T plugin) {
      Intrinsics.checkNotNullParameter(mainPlugin, "mainPlugin");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super();
      this.mainPlugin = mainPlugin;
      this.plugin = plugin;
   }

   @NotNull
   public final JavaPlugin getMainPlugin() {
      return this.mainPlugin;
   }

   @NotNull
   public final T getPlugin() {
      return this.plugin;
   }

   public abstract boolean canBuild(@NotNull Player var1, @NotNull Location var2);

   public abstract boolean canBreak(@NotNull Player var1, @NotNull Location var2);

   public abstract boolean canInteract(@NotNull Player var1, @NotNull Location var2);

   public abstract boolean canUse(@NotNull Player var1, @NotNull Location var2);
}

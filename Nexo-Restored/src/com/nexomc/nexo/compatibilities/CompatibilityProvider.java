package com.nexomc.nexo.compatibilities;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\u0015\u001a\u00020\u0014H\u0016J\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007J\r\u0010\b\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u0010\nR\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u0004\u0018\u00018\u0000X\u0084\u000e¢\u0006\u0010\n\u0002\u0010\r\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0010\"\u0004\b\u0011\u0010\u0012¨\u0006\u0016"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "T", "Lorg/bukkit/plugin/Plugin;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "pluginName", "", "plugin", "getPlugin", "()Lorg/bukkit/plugin/Plugin;", "setPlugin", "(Lorg/bukkit/plugin/Plugin;)V", "Lorg/bukkit/plugin/Plugin;", "isEnabled", "", "()Z", "setEnabled", "(Z)V", "enable", "", "disable", "com.nexomc:core"}
)
public class CompatibilityProvider<T extends Plugin> implements Listener {
   @Nullable
   private String pluginName;
   @Nullable
   private T plugin;
   private boolean isEnabled;

   @Nullable
   protected final T getPlugin() {
      return this.plugin;
   }

   protected final void setPlugin(@Nullable T t) {
      this.plugin = t;
   }

   public final boolean isEnabled() {
      return this.isEnabled;
   }

   public final void setEnabled(boolean flag) {
      this.isEnabled = flag;
   }

   public final void enable(@NotNull String pluginName) {
      Bukkit.getPluginManager().registerEvents(this, (Plugin)NexoPlugin.Companion.instance());
      this.isEnabled = true;
      this.pluginName = pluginName;
      CompatibilityProvider compatibilityProvider = this;

      try {
         CompatibilityProvider compatibilityProvider2 = compatibilityProvider;
         int value = 0;
         Plugin plugin2 = Bukkit.getPluginManager().getPlugin(pluginName);
         compatibilityProvider2.plugin = plugin2 instanceof Plugin ? plugin2 : null;
         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   public void disable() {
      HandlerList.unregisterAll(this);
      this.isEnabled = false;
   }

   @Nullable
   public final String pluginName() {
      return this.pluginName;
   }

   @Nullable
   public final T plugin() {
      return this.plugin;
   }
}

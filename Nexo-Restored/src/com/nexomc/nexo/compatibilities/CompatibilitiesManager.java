package com.nexomc.nexo.compatibilities;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.compatibilities.axiom.AxiomCompatibility;
import com.nexomc.nexo.compatibilities.blocklocker.BlockLockerCompatibility;
import com.nexomc.nexo.compatibilities.modelengine.ModelEngineCompatibility;
import com.nexomc.nexo.compatibilities.mythicmobs.MythicMobsCompatibility;
import com.nexomc.nexo.compatibilities.placeholderapi.PlaceholderAPICompatibility;
import com.nexomc.nexo.compatibilities.tab.TABCompatibility;
import com.nexomc.nexo.compatibilities.worldedit.WrappedWorldEdit;
import com.nexomc.nexo.compatibilities.worldguard.WorldGuardCompatibility;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.UtilsKt;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\u000bJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006J\u000e\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0006J*\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00062\u0012\u0010\u0013\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\b0\u00072\u0006\u0010\u0014\u001a\u00020\u000eJ\"\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u00062\u0012\u0010\u0013\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\b0\u0007J\u0012\u0010\u0016\u001a\u0006\u0012\u0002\b\u00030\b2\u0006\u0010\u000f\u001a\u00020\u0006J\u001a\u0010\u0017\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\b0\u00072\u0006\u0010\u000f\u001a\u00020\u0006J\u000e\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006J\u001e\u0010\u0019\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0010\u0012\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\b0\u00070\u0005J\u0016\u0010\u001a\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u0006\u0012\u0002\b\u00030\b0\u0005R&\u0010\u0004\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0010\u0012\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\b0\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u0006\u0012\u0002\b\u00030\b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/CompatibilitiesManager;", "", "<init>", "()V", "COMPAT_PROVIDERS", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Ljava/lang/Class;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "ACTIVE_COMPAT_PROVIDERS", "enableCompatibilies", "", "disableCompatibilities", "enableCompatibility", "", "pluginName", "disableCompatibility", "addCompatibility", "compatPlugin", "clazz", "tryEnable", "compatibilityPluginName", "activeCompatibility", "compatibility", "isCompatibilityEnabled", "compatProviders", "activeCompatProviders", "com.nexomc:core"}
)
public final class CompatibilitiesManager {
   @NotNull
   public static final CompatibilitiesManager INSTANCE = new CompatibilitiesManager();
   @NotNull
   private static final ConcurrentHashMap<String, Class<? extends CompatibilityProvider<?>>> COMPAT_PROVIDERS = new ConcurrentHashMap();
   @NotNull
   private static final ConcurrentHashMap<String, CompatibilityProvider<?>> ACTIVE_COMPAT_PROVIDERS = new ConcurrentHashMap();

   private CompatibilitiesManager() {
   }

   public final void enableCompatibilies() {
      WrappedWorldEdit.INSTANCE.registerParser();
      Bukkit.getPluginManager().registerEvents(new CompatibilityListener(), (Plugin)NexoPlugin.Companion.instance());
      Bukkit.getPluginManager().registerEvents(new CompatibilityListener(), (Plugin)NexoPlugin.Companion.instance());
      this.addCompatibility("PlaceholderAPI", PlaceholderAPICompatibility.class, true);
      this.addCompatibility("MythicMobs", MythicMobsCompatibility.class, true);
      this.addCompatibility("BlockLocker", BlockLockerCompatibility.class, true);
      this.addCompatibility("ModelEngine", ModelEngineCompatibility.class, true);
      this.addCompatibility("WorldGuard", WorldGuardCompatibility.class, true);
      this.addCompatibility("AxiomPaper", AxiomCompatibility.class, true);
      TABCompatibility.INSTANCE.registerPlaceholders();
   }

   public final void disableCompatibilities() {
      WrappedWorldEdit.INSTANCE.unregister();
      Map map = (Map)ACTIVE_COMPAT_PROVIDERS;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value = 0;
         String pluginName = (String)element.getKey();
         INSTANCE.disableCompatibility(pluginName);
      }

   }

   public final boolean enableCompatibility(@NotNull String pluginName) {
      CompatibilitiesManager compatibilitiesManager = this;

      Object obj3;
      try {
         CompatibilitiesManager compatibilitiesManager2 = compatibilitiesManager;
         int value = 0;
         if (!ACTIVE_COMPAT_PROVIDERS.containsKey(pluginName) && COMPAT_PROVIDERS.containsKey(pluginName) && PluginUtils.INSTANCE.isEnabled(pluginName)) {
            Map map = (Map)ACTIVE_COMPAT_PROVIDERS;
            Object obj = COMPAT_PROVIDERS.get(pluginName);
            Object newInstance = ((Class)obj).getConstructor().newInstance();
            CompatibilityProvider compatibilityProvider = (CompatibilityProvider)newInstance;
            int value2 = 0;
            compatibilityProvider.enable(pluginName);
            map.put(pluginName, newInstance);
            Message message = Message.PLUGIN_HOOKS;
            TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("plugin", pluginName)};
            message.log(tagResolvers);
         }

         obj3 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj3 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj2 = UtilsKt.printOnFailure(obj3, false);
      return (ResultShim.isFailure(obj2) ? null : obj2) != null;
   }

   public final void disableCompatibility(@NotNull String pluginName) {
      CompatibilitiesManager compatibilitiesManager = this;

      Object obj2;
      try {
         CompatibilitiesManager compatibilitiesManager2 = compatibilitiesManager;
         int value = 0;
         if (!ACTIVE_COMPAT_PROVIDERS.containsKey(pluginName)) {
            return;
         }

         CompatibilityProvider compatibilityProvider = (CompatibilityProvider)ACTIVE_COMPAT_PROVIDERS.remove(pluginName);
         if (compatibilityProvider != null) {
            CompatibilityProvider it = compatibilityProvider;
            int value2 = 0;
            compatibilityProvider = it.isEnabled() ? it : null;
            if (compatibilityProvider != null) {
               compatibilityProvider.disable();
            }
         }

         Message message = Message.PLUGIN_UNHOOKS;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("plugin", pluginName)};
         message.log(tagResolvers);
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      if (ResultShim.isFailure(obj)) {
      }

   }

   public final boolean addCompatibility(@NotNull String compatPlugin, @NotNull Class<? extends CompatibilityProvider<?>> clazz, boolean tryEnable) {
      CompatibilitiesManager compatibilitiesManager = this;

      Object obj2;
      try {
         CompatibilitiesManager compatibilitiesManager2 = compatibilitiesManager;
         int value = 0;
         ((Map)COMPAT_PROVIDERS).put(compatPlugin, clazz);
         obj2 = ResultShim.ok(!tryEnable || compatibilitiesManager2.enableCompatibility(compatPlugin));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = UtilsKt.printOnFailure(obj2, false);
      return (ResultShim.isFailure(obj) ? null : obj) != null;
   }

   public final boolean addCompatibility(@NotNull String compatibilityPluginName, @NotNull Class<? extends CompatibilityProvider<?>> clazz) {
      return this.addCompatibility(compatibilityPluginName, clazz, false);
   }

   @NotNull
   public final CompatibilityProvider<?> activeCompatibility(@NotNull String pluginName) {
      Object obj = ACTIVE_COMPAT_PROVIDERS.get(pluginName);
      return (CompatibilityProvider)obj;
   }

   @NotNull
   public final Class<? extends CompatibilityProvider<?>> compatibility(@NotNull String pluginName) {
      Object obj = COMPAT_PROVIDERS.get(pluginName);
      return (Class)obj;
   }

   public final boolean isCompatibilityEnabled(@NotNull String pluginName) {
      CompatibilityProvider compatibilityProvider = (CompatibilityProvider)ACTIVE_COMPAT_PROVIDERS.get(pluginName);
      return compatibilityProvider != null ? compatibilityProvider.isEnabled() : false;
   }

   @NotNull
   public final ConcurrentHashMap<String, Class<? extends CompatibilityProvider<?>>> compatProviders() {
      return COMPAT_PROVIDERS;
   }

   @NotNull
   public final ConcurrentHashMap<String, CompatibilityProvider<?>> activeCompatProviders() {
      return ACTIVE_COMPAT_PROVIDERS;
   }
}

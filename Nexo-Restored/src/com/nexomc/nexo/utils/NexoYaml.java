package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \b2\u00020\u0001:\u0001\bB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/utils/NexoYaml;", "Lorg/bukkit/configuration/file/YamlConfiguration;", "<init>", "()V", "load", "", "file", "Ljava/io/File;", "Companion", "com.nexomc:core"}
)
public final class NexoYaml extends YamlConfiguration {
   @NotNull
   public static final Companion Companion = new Companion();

   public void load(@NotNull File file) {
      NexoYaml nexoYaml = this;

      Object obj;
      try {
         NexoYaml nexoYaml2 = nexoYaml;
         int value = 0;
         super.load(file);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         Logs.logError("Error loading YAML configuration file: " + file.getName(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(it), false);
         }
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0007J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\rJ\u0016\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\rJ\u0016\u0010\u0014\u001a\u00020\r2\u000e\u0010\u0015\u001a\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u0016J\u0019\u0010\u0017\u001a\u0004\u0018\u00010\u00012\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u0000¢\u0006\u0002\b\u0019¨\u0006\u001a"},
      d2 = {"Lcom/nexomc/nexo/utils/NexoYaml$Companion;", "", "<init>", "()V", "isValidYaml", "", "file", "Ljava/io/File;", "loadConfiguration", "Lorg/bukkit/configuration/file/YamlConfiguration;", "saveConfig", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "copyConfigurationSection", "source", "target", "equals", "first", "second", "toSection", "map", "", "convertToConfigurationSection", "value", "convertToConfigurationSection$com_nexomc_core", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isValidYaml(@NotNull File file) {
         Companion companion = this;

         Object obj2;
         try {
            Companion companion2 = companion;
            int value = 0;
            (new YamlConfiguration()).load(file);
            obj2 = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = obj2;
         Throwable throwable = ResultShim.exceptionOrNull(obj2);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            Logs.logError("Error loading YAML configuration file: " + file.getPath(), false);
            Logs.logError("Ensure that your config is formatted correctly:", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               if (message != null) {
                  String p0 = message;
                  Logs logs = Logs.INSTANCE;
                  int value3 = 0;
                  Logs.logWarn(p0, false);
               }
            }
         }

         return (ResultShim.isFailure(obj) ? null : obj) != null;
      }

      @NotNull
      public final YamlConfiguration loadConfiguration(@NotNull File file) {
         Companion companion = this;

         Object obj2;
         try {
            Companion companion2 = companion;
            int value = 0;
            YamlConfiguration yamlConfiguration2 = new YamlConfiguration();
            int value2 = 0;
            yamlConfiguration2.load(file);
            obj2 = ResultShim.ok(yamlConfiguration2);
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = obj2;
         Throwable throwable = ResultShim.exceptionOrNull(obj2);
         if (throwable != null) {
            Throwable it = throwable;
            int value3 = 0;
            Logs.logError("Error loading YAML configuration file: " + file.getName(), false);
            Logs.logError("Ensure that your config is formatted correctly:", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               if (message != null) {
                  String str = message;
                  Logs logs = Logs.INSTANCE;
                  int value4 = 0;
                  Logs.logWarn(str, false);
               }
            }
         }

         YamlConfiguration yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure(obj) ? null : obj);
         if (yamlConfiguration == null) {
            yamlConfiguration = new YamlConfiguration();
         }

         return yamlConfiguration;
      }

      public final void saveConfig(@NotNull File file, @NotNull ConfigurationSection section) {
         Companion companion = this;

         Object obj;
         try {
            Companion companion2 = companion;
            int value = 0;
            YamlConfiguration config = companion2.loadConfiguration(file);
            CharSequence p0 = (CharSequence)section.getCurrentPath();
            if (p0 == null || p0.length() == 0) {
               companion2.copyConfigurationSection(section, (ConfigurationSection)config);
               config.save(file);
            } else {
               String currentPath = section.getCurrentPath();
               config.set(currentPath, section);
               config.save(file);
            }

            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            Logs.logError("Error saving YAML configuration file: " + file.getName(), false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               if (message != null) {
                  String p0 = message;
                  Logs logs = Logs.INSTANCE;
                  int value3 = 0;
                  Logs.logWarn(p0, false);
               }
            }
         }

      }

      public final void copyConfigurationSection(@NotNull ConfigurationSection source, @NotNull ConfigurationSection target) {
         Set keys = source.getKeys(false);
         Iterable iterable = (Iterable)keys;

         for(Object element : iterable) {
            String key = (String)element;
            int value = 0;
            Pair pair = kotlin.TuplesKt.to(source.get(key), target.get(key));
            Object sourceValue = pair.component1();
            Object targetValue = pair.component2();
            if (sourceValue instanceof ConfigurationSection) {
               ConfigurationSection configurationSection = targetValue instanceof ConfigurationSection ? (ConfigurationSection)targetValue : null;
               if ((targetValue instanceof ConfigurationSection ? (ConfigurationSection)targetValue : null) == null) {
                  configurationSection = target.createSection(key);
               }

               ConfigurationSection targetSection = configurationSection;
               NexoYaml.Companion.copyConfigurationSection((ConfigurationSection)sourceValue, targetSection);
            } else {
               target.set(key, sourceValue);
            }
         }

      }

      public final boolean equals(@NotNull ConfigurationSection first, @NotNull ConfigurationSection second) {
         Set keys = first.getKeys(false);
         Set firstKeys = keys;
         keys = second.getKeys(false);
         Set secondKeys = keys;
         if (!Intrinsics.areEqual(firstKeys, secondKeys)) {
            return false;
         } else {
            for(String key : (java.lang.Iterable<String>)(java.lang.Iterable)firstKeys) {
               Object firstValue = first.get(key);
               Object secondValue = second.get(key);
               if (!(firstValue instanceof ConfigurationSection) || !(secondValue instanceof ConfigurationSection)) {
                  if (firstValue instanceof ConfigurationSection || secondValue instanceof ConfigurationSection) {
                     return false;
                  }

                  if (!Intrinsics.areEqual(firstValue, secondValue)) {
                     return false;
                  }
               } else if (!this.equals((ConfigurationSection)firstValue, (ConfigurationSection)secondValue)) {
                  return false;
               }
            }

            return true;
         }
      }

      @NotNull
      public final ConfigurationSection toSection(@NotNull Map<?, ?> map) {
         YamlConfiguration yamlConfiguration = new YamlConfiguration();
         YamlConfiguration yamlConfiguration2 = yamlConfiguration;
         int value2 = 0;

         for(Map.Entry element : map.entrySet()) {
            int value3 = 0;
            Object key = element.getKey();
            Object value = element.getValue();
            if (key instanceof String) {
               yamlConfiguration2.set((String)key, NexoYaml.Companion.convertToConfigurationSection$com_nexomc_core(value));
            }
         }

         return (ConfigurationSection)yamlConfiguration;
      }

      @Nullable
      public final Object convertToConfigurationSection$com_nexomc_core(@Nullable Object value) {
         Object obj;
         if (value instanceof Map) {
            obj = this.toSection((Map)value);
         } else if (value instanceof List) {
            Iterable iterable = (Iterable)value;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               int value2 = 0;
               destination.add(this.convertToConfigurationSection$com_nexomc_core(item));
            }

            obj = (List)destination;
         } else {
            obj = value;
         }

         return obj;
      }
   }
}

package io.lumine.mythic.core.skills.placeholders;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlaceholdersLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.NodeListProp;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.ClassInfo;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.packs.PackExecutor;
import io.lumine.mythic.core.skills.placeholders.all.ConditionalPlaceholder;
import io.lumine.mythic.core.skills.placeholders.all.RandomLinePlaceholder;
import io.lumine.mythic.core.skills.placeholders.all.StaticPlaceholder;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializationRegistry;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderExecutor extends ReloadableModule<MythicBukkit> implements PlaceholderManager {
   private static final NodeListProp NODES = Property.NodeList(Scope.PACK, "");
   private final StaticPlaceholderRegistry staticPlaceholderRegistry = new StaticPlaceholderRegistry();
   private final List<PlaceholderParser> parsers = Lists.newArrayList();
   private final PlaceholderExecutor.PlaceholderClassTree placeholderClasses = new PlaceholderExecutor.PlaceholderClassTree();
   private final Map<String, PlaceholderExecutor.PlaceholderEntry> placeholderCache = new ConcurrentHashMap<>();
   private boolean initialized = false;
   private boolean acceptingRegisteredParsers = true;
   private static final PlaceholderExecutor.PlaceholderEntry emptyPlaceholderEntry = new PlaceholderExecutor.PlaceholderEntry(null, null);

   public PlaceholderExecutor(MythicBukkit plugin) {
      super(plugin, false);
   }

   @Override
   public void unload() {
      this.parsers.clear();
      this.staticPlaceholderRegistry.clear();
   }

   public void load(MythicBukkit plugin) {
      this.initialized = false;
      this.acceptingRegisteredParsers = true;
      this.staticPlaceholderRegistry.uncompile();
      this.placeholderCache.clear();
      this.placeholderClasses.getSubPlaceholders().remove("placeholder");
      PlaceholderClassSerializationRegistry.registerAll(plugin, "io.lumine.mythic.core.skills.placeholders.serialization.serializers");
      MetaKeywords.registerAll();

      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.getPlugin(), "io.lumine.mythic.core.skills.placeholders.all", MythicPlaceholder.class)) {
         this.register(clazz);
      }

      for (PackExecutor.ConfigFile configFile : this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.PLACEHOLDER)) {
         MythicConfigImpl baseConfig = new MythicConfigImpl(configFile.file());

         for (String node : baseConfig.getKeys("")) {
            if (baseConfig.isConfigurationSection(node)) {
               this.register("placeholder." + node, new ConditionalPlaceholder(this, baseConfig.getNestedConfig(node)));
            } else if (baseConfig.isList(node)) {
               this.register("placeholder." + node, new RandomLinePlaceholder(this, baseConfig.getStringList(node)));
            } else {
               String value = baseConfig.getString(node);
               this.register("placeholder." + node, new StaticPlaceholder(value, this));
            }
         }
      }

      try {
         MythicPlaceholdersLoadEvent mythicPlaceholdersLoadEvent = Events.callAndReturn(new MythicPlaceholdersLoadEvent(this));
      } catch (Throwable e) {
         MythicLogger.error("Error while handling MythicPlaceholdersLoadEvent", e);
      }

      this.staticPlaceholderRegistry.compile();
      this.initialized = true;
   }

   @Override
   public void register(Class<?> clazz) {
      try {
         MythicPlaceholder anno = clazz.getAnnotation(MythicPlaceholder.class);
         this.register(clazz, anno);
      } catch (Exception e) {
         MythicLogger.error("Failed to register placeholder " + clazz.getCanonicalName(), e);
      }
   }

   @Override
   public void register(Class<?> clazz, @NotNull MythicPlaceholder annotation) {
      String name = annotation.placeholder();
      String[] aliases = annotation.aliases();
      List<String> keys = new ArrayList<>(aliases.length + 1);
      keys.add(name);
      Collections.addAll(keys, aliases);
      if (!GenericPlaceholder.class.isAssignableFrom(clazz)) {
         if (Placeholder.class.isAssignableFrom(clazz)) {
            this.register(name, (Class<Placeholder>)clazz);

            for (String alias : annotation.aliases()) {
               this.register(alias, (Class<Placeholder>)clazz);
            }
         }
      } else {
         short metaPlaceholderArgsOffset = annotation.usedPlaceholderArguments();
         if (EntityScopedPlaceholder.class.isAssignableFrom(clazz)) {
            for (EntityScopedPlaceholder.TargetSource source : EntityScopedPlaceholder.TargetSource.values()) {
               EntityScopedPlaceholder.EntityScopedPlaceholderArguments context = EntityScopedPlaceholder.EntityScopedPlaceholderArguments.builder()
                  .manager(this)
                  .source(source)
                  .usedPlaceholderArguments(metaPlaceholderArgsOffset)
                  .build();

               for (String scopeAlias : source.getScopeAliases()) {
                  for (String key : keys) {
                     try {
                        this.register(
                           scopeAlias + "." + key,
                           (Class<Placeholder>)clazz,
                           context,
                           (Constructor<GenericPlaceholder<?>>)clazz.getConstructor(EntityScopedPlaceholder.EntityScopedPlaceholderArguments.class)
                        );
                     } catch (NoSuchMethodException e) {
                        MythicLogger.error("Failed to register placeholder " + scopeAlias + "." + key, e);
                     }
                  }
               }
            }
         } else {
            GenericPlaceholder.GenericPlaceholderArguments genericPlaceholderArguments = GenericPlaceholder.GenericPlaceholderArguments.builder()
               .manager(this)
               .usedPlaceholderArguments(metaPlaceholderArgsOffset)
               .build();

            for (String key : keys) {
               try {
                  this.register(
                     key,
                     (Class<Placeholder>)clazz,
                     genericPlaceholderArguments,
                     (Constructor<GenericPlaceholder<?>>)clazz.getConstructor(GenericPlaceholder.GenericPlaceholderArguments.class)
                  );
               } catch (NoSuchMethodException ex) {
                  MythicLogger.error("Failed to register placeholder " + key, ex);
               }
            }
         }
      }
   }

   @Override
   public void register(String[] keys, Placeholder transformer) {
      for (String key : keys) {
         this.register(key, transformer);
      }
   }

   @Override
   public void register(String key, Placeholder transformer) {
      if (transformer instanceof StaticPlaceholder staticPlaceholder) {
         this.staticPlaceholderRegistry.register(key, staticPlaceholder.getReplacement());
      }

      String[] split = key.split("\\.");
      PlaceholderExecutor.PlaceholderClassTree ref = this.placeholderClasses.getOrCreatePath(split);
      ref.setPlaceholder(transformer);
      if (this.initialized) {
         this.placeholderCache.clear();
         this.recheckForPlaceholders();
      }
   }

   @Deprecated
   public void register(String[] keys, Class<Placeholder> transformer) {
      for (String key : keys) {
         this.register(key, transformer);
      }
   }

   @Deprecated
   public void register(String key, Class<Placeholder> transformer) {
      this.register(key, transformer, null, null);
   }

   public void register(
      @NotNull String key,
      Class<Placeholder> transformer,
      GenericPlaceholder.GenericPlaceholderArguments transformerArgs,
      Constructor<GenericPlaceholder<?>> transformerConstructor
   ) {
      String[] split = key.split("\\.");
      PlaceholderExecutor.PlaceholderClassTree ref = this.placeholderClasses.getOrCreatePath(split);
      ref.setPlaceholderClass(transformer);
      if (transformerArgs != null) {
         ref.setTransformerArguments(transformerArgs);
      }

      if (transformerConstructor != null) {
         ref.setPlaceholderConstructor(transformerConstructor);
      }

      if (!GenericPlaceholder.class.isAssignableFrom(transformer)) {
         ClassInfo.of(transformer)
            .warnClassIsUsingDeprecatedAPI(
               "Legacy Placeholder detected. Please, do update to the new syntax: https://git.lumine.io/mythiccraft/MythicMobs/-/wikis/API/Placeholders"
            );
      }

      if (this.initialized) {
         this.placeholderCache.clear();
         this.recheckForPlaceholders();
      }
   }

   @NotNull
   @Override
   public PlaceholderExecutor.PlaceholderEntry getPlaceholder(@NotNull String key) {
      if (key.startsWith("&")) {
         return PlaceholderExecutor.PlaceholderEntry.ofEmpty();
      } else {
         PlaceholderExecutor.PlaceholderEntry cached = this.placeholderCache.get(key);
         if (cached != null) {
            return cached;
         } else {
            PlaceholderExecutor.PlaceholderEntry entry = this.resolvePlaceholder(key);
            if (entry != null) {
               this.placeholderCache.put(key, entry);
               return entry;
            } else {
               this.placeholderCache.put(key, PlaceholderExecutor.PlaceholderEntry.ofEmpty());
               return PlaceholderExecutor.PlaceholderEntry.ofEmpty();
            }
         }
      }
   }

   @Nullable
   private PlaceholderExecutor.PlaceholderEntry resolvePlaceholder(@NotNull String key) {
      String[] split = splitByDots(key);
      PlaceholderExecutor.PlaceholderClassTree ref = this.placeholderClasses;
      List<GenericPlaceholderSegment> allArgsList = new ArrayList<>();
      int extraArgsStartIndex = 0;
      List<Integer> wildcardArgs = new ArrayList<>();
      MythicLineConfig firstConfigFoundAmongAllArgs = null;
      boolean maximumDepthReached = false;

      for (int i = 0; i < split.length; i++) {
         String currentKey = split[i];
         PlaceholderExecutor.PlaceholderMythicLineConfig placeholderMythicLineConfig = currentKey.contains("{") && currentKey.contains("}")
            ? getTopLevelBraces(currentKey)
            : null;
         if (firstConfigFoundAmongAllArgs == null && placeholderMythicLineConfig != null) {
            firstConfigFoundAmongAllArgs = MythicLineConfigImpl.of(placeholderMythicLineConfig.config);
         }

         if (placeholderMythicLineConfig != null) {
            currentKey = placeholderMythicLineConfig.key;
         }

         if (!maximumDepthReached) {
            if (ref.getSubPlaceholders().containsKey(currentKey)) {
               ref = ref.getSubPlaceholders().get(currentKey);
            } else if (ref.getSubPlaceholders().containsKey("*")) {
               ref = ref.getSubPlaceholders().get("*");
               wildcardArgs.add(i);
            } else {
               if (i == 0) {
                  return null;
               }

               extraArgsStartIndex = i;
               maximumDepthReached = true;
            }
         }

         if (placeholderMythicLineConfig != null) {
            allArgsList.add(new GenericPlaceholderSegment(placeholderMythicLineConfig.key, MythicLineConfigImpl.of(placeholderMythicLineConfig.config)));
         } else {
            allArgsList.add(new GenericPlaceholderSegment(currentKey, null));
         }
      }

      Placeholder placeholder = null;
      List<GenericPlaceholderSegment> extraArgsList = extraArgsStartIndex > 0 ? allArgsList.subList(extraArgsStartIndex, allArgsList.size()) : List.of();
      String[] args = extraArgsList.stream().map(GenericPlaceholderSegment::key).toArray(String[]::new);
      if (ref.getPlaceholderConstructor() != null && ref.getTransformerArguments() != null) {
         GenericPlaceholder.GenericPlaceholderArguments constructorArguments = ref.getTransformerArguments()
            .toBuilder()
            .manager(this)
            .config(firstConfigFoundAmongAllArgs)
            .unparsedPlaceholder("<" + key + ">")
            .args(args)
            .clearAllArgs()
            .allArgs(allArgsList)
            .extraArgsStartIndex(extraArgsStartIndex)
            .clearWildcardArgs()
            .wildcardArgs(wildcardArgs)
            .build();

         try {
            placeholder = ref.getPlaceholderConstructor().newInstance(constructorArguments);
         } catch (Throwable e) {
            MythicLogger.error("Failed to construct placeholder " + key, e);
         }
      } else if (ref.getPlaceholderClass() != null) {
         Class<Placeholder> placeholderClass = ref.getPlaceholderClass();

         try {
            Constructor<Placeholder> placeholderConstructor = placeholderClass.getConstructor(PlaceholderExecutor.class, MythicLineConfig.class, String[].class);
            placeholder = placeholderConstructor.newInstance(this, firstConfigFoundAmongAllArgs, args);
         } catch (Throwable ex) {
            try {
               Constructor<Placeholder> placeholderConstructorx = placeholderClass.getConstructor(PlaceholderExecutor.class, String[].class);
               placeholder = placeholderConstructorx.newInstance(this, args);
            } catch (Throwable ex2) {
               try {
                  Constructor<Placeholder> placeholderConstructorxx = placeholderClass.getConstructor(PlaceholderExecutor.class);
                  placeholder = placeholderConstructorxx.newInstance(this);
               } catch (Throwable ex3) {
                  try {
                     Constructor<Placeholder> placeholderConstructorxxx = placeholderClass.getConstructor(String[].class);
                     placeholder = placeholderConstructorxxx.newInstance(args);
                  } catch (Throwable ex4) {
                     try {
                        Constructor<Placeholder> placeholderConstructorxxxx = placeholderClass.getConstructor();
                        placeholder = placeholderConstructorxxxx.newInstance();
                     } catch (Throwable ex5) {
                        MythicLogger.error("Failed to construct legacy placeholder " + key, ex);
                     }
                  }
               }
            }
         }
      } else if (ref.getPlaceholder() != null) {
         placeholder = ref.getPlaceholder();
      }

      return placeholder == null ? null : new PlaceholderExecutor.PlaceholderEntry(placeholder, String.join(".", args));
   }

   @Override
   public void registerParser(PlaceholderParser placeholderParser) {
      if (this.acceptingRegisteredParsers) {
         this.parsers.add(placeholderParser);
      }
   }

   public void unregisterParser(PlaceholderParser placeholderParser) {
      this.parsers.remove(placeholderParser);
   }

   @Override
   public void recheckForPlaceholders() {
      ArrayList<PlaceholderParser> parsers = Lists.newArrayList(this.parsers);
      parsers.forEach(parser -> {
         if (parser != null) {
            parser.tokenizeString();
         }
      });
   }

   @Deprecated
   public static List<String> parsePlaceholders(String input) {
      List<String> matches = new ArrayList<>();
      Stack<Integer> bracketIndices = new Stack<>();
      int startIndex = -1;
      int loopStartIndex = !input.startsWith("<<") && !input.startsWith(">>") ? 0 : 1;

      for (int i = loopStartIndex; i < input.length(); i++) {
         if (input.charAt(i) == '<') {
            if (bracketIndices.isEmpty()) {
               startIndex = i;
            }

            bracketIndices.push(i);
         } else if (input.charAt(i) == '>' && !bracketIndices.isEmpty()) {
            bracketIndices.pop();
            if (bracketIndices.isEmpty() && startIndex != -1) {
               matches.add(input.substring(startIndex + 1, i));
               startIndex = -1;
            }
         }
      }

      return matches;
   }

   private static String[] splitByDots(String input) {
      List<String> parts = new ArrayList<>();
      StringBuilder current = new StringBuilder();
      int angleDepth = 0;
      int braceDepth = 0;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == '<') {
            angleDepth++;
         } else if (c == '>') {
            angleDepth--;
         } else if (c == '{') {
            braceDepth++;
         } else if (c == '}') {
            braceDepth--;
         }

         if (c == '.' && angleDepth == 0 && braceDepth == 0) {
            parts.add(current.toString());
            current.setLength(0);
         } else {
            current.append(c);
         }
      }

      parts.add(current.toString());
      return parts.toArray(new String[0]);
   }

   @Nullable
   private static PlaceholderExecutor.PlaceholderMythicLineConfig getTopLevelBraces(@NotNull String input) {
      int angle = 0;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == '<') {
            angle++;
         } else if (c == '>') {
            angle--;
         } else if (c == '{' && angle == 0) {
            return new PlaceholderExecutor.PlaceholderMythicLineConfig(input.substring(0, i), getFirstBraces(input.substring(i)));
         }
      }

      return null;
   }

   @Nullable
   public static String getFirstBraces(@NotNull String input) {
      int braces = 0;
      int angles = 0;
      int startIndex = -1;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == '<') {
            angles++;
         } else if (c == '>') {
            angles--;
         } else if (angles == 0) {
            if (c == '{') {
               if (braces == 0) {
                  startIndex = i;
               }

               braces++;
            } else if (c == '}') {
               if (--braces == 0) {
                  return input.substring(startIndex, i + 1);
               }
            }
         }
      }

      return null;
   }

   @Generated
   public StaticPlaceholderRegistry getStaticPlaceholderRegistry() {
      return this.staticPlaceholderRegistry;
   }

   @Generated
   public List<PlaceholderParser> getParsers() {
      return this.parsers;
   }

   @Generated
   public boolean isInitialized() {
      return this.initialized;
   }

   @Generated
   public boolean isAcceptingRegisteredParsers() {
      return this.acceptingRegisteredParsers;
   }

   @Generated
   public void setAcceptingRegisteredParsers(boolean acceptingRegisteredParsers) {
      this.acceptingRegisteredParsers = acceptingRegisteredParsers;
   }

   private static class PlaceholderClassTree {
      private Class<Placeholder> placeholderClass = null;
      private Placeholder placeholder = null;
      private Constructor<GenericPlaceholder<?>> placeholderConstructor = null;
      private Map<String, PlaceholderExecutor.PlaceholderClassTree> subPlaceholders = new HashMap<>();
      private GenericPlaceholder.GenericPlaceholderArguments transformerArguments = null;

      private void clear() {
         this.subPlaceholders.clear();
         this.transformerArguments = null;
      }

      public PlaceholderExecutor.PlaceholderClassTree getOrCreatePath(String[] split) {
         PlaceholderExecutor.PlaceholderClassTree ref = this;

         for (String part : split) {
            ref.subPlaceholders.putIfAbsent(part, new PlaceholderExecutor.PlaceholderClassTree());
            ref = ref.subPlaceholders.get(part);
         }

         return ref;
      }

      @Generated
      public PlaceholderClassTree() {
      }

      @Generated
      public Class<Placeholder> getPlaceholderClass() {
         return this.placeholderClass;
      }

      @Generated
      public Placeholder getPlaceholder() {
         return this.placeholder;
      }

      @Generated
      public Constructor<GenericPlaceholder<?>> getPlaceholderConstructor() {
         return this.placeholderConstructor;
      }

      @Generated
      public Map<String, PlaceholderExecutor.PlaceholderClassTree> getSubPlaceholders() {
         return this.subPlaceholders;
      }

      @Generated
      public GenericPlaceholder.GenericPlaceholderArguments getTransformerArguments() {
         return this.transformerArguments;
      }

      @Generated
      public void setPlaceholderClass(Class<Placeholder> placeholderClass) {
         this.placeholderClass = placeholderClass;
      }

      @Generated
      public void setPlaceholder(Placeholder placeholder) {
         this.placeholder = placeholder;
      }

      @Generated
      public void setPlaceholderConstructor(Constructor<GenericPlaceholder<?>> placeholderConstructor) {
         this.placeholderConstructor = placeholderConstructor;
      }

      @Generated
      public void setSubPlaceholders(Map<String, PlaceholderExecutor.PlaceholderClassTree> subPlaceholders) {
         this.subPlaceholders = subPlaceholders;
      }

      @Generated
      public void setTransformerArguments(GenericPlaceholder.GenericPlaceholderArguments transformerArguments) {
         this.transformerArguments = transformerArguments;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PlaceholderExecutor.PlaceholderClassTree other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$placeholderClass = this.getPlaceholderClass();
            Object other$placeholderClass = other.getPlaceholderClass();
            if (this$placeholderClass == null ? other$placeholderClass == null : this$placeholderClass.equals(other$placeholderClass)) {
               Object this$placeholder = this.getPlaceholder();
               Object other$placeholder = other.getPlaceholder();
               if (this$placeholder == null ? other$placeholder == null : this$placeholder.equals(other$placeholder)) {
                  Object this$placeholderConstructor = this.getPlaceholderConstructor();
                  Object other$placeholderConstructor = other.getPlaceholderConstructor();
                  if (this$placeholderConstructor == null
                     ? other$placeholderConstructor == null
                     : this$placeholderConstructor.equals(other$placeholderConstructor)) {
                     Object this$subPlaceholders = this.getSubPlaceholders();
                     Object other$subPlaceholders = other.getSubPlaceholders();
                     if (this$subPlaceholders == null ? other$subPlaceholders == null : this$subPlaceholders.equals(other$subPlaceholders)) {
                        Object this$transformerArguments = this.getTransformerArguments();
                        Object other$transformerArguments = other.getTransformerArguments();
                        return this$transformerArguments == null
                           ? other$transformerArguments == null
                           : this$transformerArguments.equals(other$transformerArguments);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof PlaceholderExecutor.PlaceholderClassTree;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $placeholderClass = this.getPlaceholderClass();
         result = result * 59 + ($placeholderClass == null ? 43 : $placeholderClass.hashCode());
         Object $placeholder = this.getPlaceholder();
         result = result * 59 + ($placeholder == null ? 43 : $placeholder.hashCode());
         Object $placeholderConstructor = this.getPlaceholderConstructor();
         result = result * 59 + ($placeholderConstructor == null ? 43 : $placeholderConstructor.hashCode());
         Object $subPlaceholders = this.getSubPlaceholders();
         result = result * 59 + ($subPlaceholders == null ? 43 : $subPlaceholders.hashCode());
         Object $transformerArguments = this.getTransformerArguments();
         return result * 59 + ($transformerArguments == null ? 43 : $transformerArguments.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PlaceholderExecutor.PlaceholderClassTree(placeholderClass="
            + this.getPlaceholderClass()
            + ", placeholder="
            + this.getPlaceholder()
            + ", placeholderConstructor="
            + this.getPlaceholderConstructor()
            + ", subPlaceholders="
            + this.getSubPlaceholders()
            + ", transformerArguments="
            + this.getTransformerArguments()
            + ")";
      }
   }

   public record PlaceholderEntry(@Nullable Placeholder placeholder, @NotNull String arg) {
      public PlaceholderEntry(Placeholder placeholder, String arg) {
         this.placeholder = placeholder;
         this.arg = arg != null ? arg : "null";
      }

      @Contract(
         pure = true
      )
      private static PlaceholderExecutor.PlaceholderEntry ofEmpty() {
         return PlaceholderExecutor.emptyPlaceholderEntry;
      }
   }

   private record PlaceholderMythicLineConfig(String key, String config) {
   }
}

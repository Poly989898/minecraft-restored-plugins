package com.nexomc.nexo.recipes;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoTag;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.recipes.listeners.RecipeBuilderEvents;
import com.nexomc.nexo.recipes.listeners.RecipeEventManager;
import com.nexomc.nexo.recipes.listeners.SmithingRecipeEvents;
import com.nexomc.nexo.recipes.loaders.BlastingLoader;
import com.nexomc.nexo.recipes.loaders.BrewingLoader;
import com.nexomc.nexo.recipes.loaders.CampfireLoader;
import com.nexomc.nexo.recipes.loaders.FurnaceLoader;
import com.nexomc.nexo.recipes.loaders.ShapedLoader;
import com.nexomc.nexo.recipes.loaders.ShapelessLoader;
import com.nexomc.nexo.recipes.loaders.SmokingLoader;
import com.nexomc.nexo.recipes.loaders.StonecuttingLoader;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.ResourceManager;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\r\u001a\u00020\u000eJ\f\u0010\u000f\u001a\u00020\u000eH\u0007b\u0002\b\u0010J\b\u0010\u0011\u001a\u00020\u000eH\u0002J\u0010\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u00162\u0006\u0010\u0018\u001a\u00020\u0017H\u0002J,\u0010\u0019\u001a\"\u0012\u0004\u0012\u00020\u001b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u001aj\u0010\u0012\u0004\u0012\u00020\u001b\u0012\u0006\u0012\u0004\u0018\u00010\u0001`\u001c*\u00020\u0017H\u0002J\u0014\u0010\u001d\u001a\u0004\u0018\u00010\u00012\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u0002JD\u0010\u001f\u001a\u0016\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u001b\u0012\u0006\u0012\u0004\u0018\u00010\u00010 0\t2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00012\u001c\b\u0002\u0010!\u001a\u0016\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020\u001b\u0012\u0006\u0012\u0004\u0018\u00010\u00010 0\tH\u0002J(\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u00160\u00162\u0012\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u00160\u0016H\u0002J\u0018\u0010$\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u0017H\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006&"},
   d2 = {"Lcom/nexomc/nexo/recipes/RecipesManager;", "", "<init>", "()V", "recipesFolder", "Ljava/io/File;", "getRecipesFolder", "()Ljava/io/File;", "listeners", "", "Lorg/bukkit/event/Listener;", "getListeners$com_nexomc_core", "()Ljava/util/List;", "load", "", "reload", "Lkotlin/jvm/JvmStatic;", "disableRecipes", "registerConfigRecipes", "recipeType", "Lcom/nexomc/nexo/recipes/RecipeType;", "expandTaggedRecipes", "", "Lorg/bukkit/configuration/ConfigurationSection;", "section", "deepMutableMap", "Ljava/util/LinkedHashMap;", "", "Lkotlin/collections/LinkedHashMap;", "deepMutableValue", "value", "collectTagSlots", "", "out", "cartesianProduct", "lists", "registerRecipeByType", "recipeSection", "com.nexomc:core"}
)
public final class RecipesManager {
   @NotNull
   public static final RecipesManager INSTANCE = new RecipesManager();
   @NotNull
   private static final File recipesFolder;
   @NotNull
   private static final List<Listener> listeners;

   private RecipesManager() {
   }

   @NotNull
   public final File getRecipesFolder() {
      return recipesFolder;
   }

   @NotNull
   public final List<Listener> getListeners$com_nexomc_core() {
      return listeners;
   }

   public final void load() {
      NexoConverter.INSTANCE.processRecipes();
      RecipeEventManager.Companion.instance().resetRecipes();
      Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(RecipeType.values());

      for(Object element : iterable) {
         RecipeType p0 = (RecipeType)element;
         int value = 0;
         this.registerConfigRecipes(p0);
      }

      this.disableRecipes();
      ((Collection)listeners).add(RecipeEventManager.Companion.instance());
      ((Collection)listeners).add(new SmithingRecipeEvents());
      ((Collection)listeners).add(new RecipeBuilderEvents());
      iterable = (Iterable)listeners;

      for(Object obj : iterable) {
         Listener it = (Listener)obj;
         int value2 = 0;
         Bukkit.getPluginManager().registerEvents(it, (Plugin)NexoPlugin.Companion.instance());
      }

   }

   @JvmStatic
   public static final void reload() {
      NexoConverter.INSTANCE.processRecipes();
      RecipeEventManager.Companion.instance().resetRecipes();
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      String[] strs = new String[]{"recipes"};
      File recipesFolder = FileUtilsKt.resolve(dataFolder, strs);
      if (!recipesFolder.exists()) {
         recipesFolder.mkdirs();
         if (Settings.GENERATE_DEFAULT_CONFIGS.toBool()) {
            ResourceManager.INSTANCE.extractConfigsInFolder("recipes", "yml");
         }
      }

      Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(RecipeType.values());
      RecipesManager recipesManager = INSTANCE;

      for(Object element : iterable) {
         RecipeType p0 = (RecipeType)element;
         int value = 0;
         recipesManager.registerConfigRecipes(p0);
      }

      INSTANCE.disableRecipes();
      RecipesManager recipesManager2 = INSTANCE;
      iterable = (Iterable)listeners;
      int value2 = 0;

      for(Object obj : iterable) {
         Listener p0 = (Listener)obj;
         int value3 = 0;
         HandlerList.unregisterAll(p0);
      }

      ((List)iterable).clear();
      recipesManager2 = INSTANCE;
      ((Collection)listeners).add(RecipeEventManager.Companion.instance());
      recipesManager2 = INSTANCE;
      ((Collection)listeners).add(new SmithingRecipeEvents());
      recipesManager2 = INSTANCE;
      ((Collection)listeners).add(new RecipeBuilderEvents());
      recipesManager2 = INSTANCE;
      iterable = (Iterable)listeners;

      for(Object obj2 : iterable) {
         Listener it = (Listener)obj2;
         int value4 = 0;
         Bukkit.getPluginManager().registerEvents(it, (Plugin)NexoPlugin.Companion.instance());
      }

   }

   private final void disableRecipes() {
      File file = recipesFolder;
      String[] strs = new String[]{"disabled_recipes.yml"};
      File disabledFile = FileUtilsKt.resolve(file, strs);
      if (!disabledFile.exists()) {
         FilesKt.writeText(disabledFile, "disabled_recipes: []", kotlin.text.Charsets.UTF_8);
      }

      YamlConfiguration disabledRecipes = NexoYaml.Companion.loadConfiguration(disabledFile);
      Iterable iterable = (Iterable)NexoYamlKt.namespacedKeyList((ConfigurationSection)disabledRecipes, "disabled_recipes");

      for(Object element : iterable) {
         NamespacedKey p0 = (NamespacedKey)element;
         int value = 0;
         Bukkit.removeRecipe(p0);
      }

   }

   private final void registerConfigRecipes(RecipeType recipeType) {
      File file2 = recipesFolder;
      String[] strs = new String[]{recipeType.getId()};
      Iterable iterable = (Iterable)FileUtilsKt.listYamlFiles(FileUtilsKt.resolve(file2, strs), true);

      for(Object element : iterable) {
         File file = (File)element;
         int value = 0;
         Map map = NexoYamlKt.childSections((ConfigurationSection)NexoYaml.Companion.loadConfiguration(file));

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            ConfigurationSection section = (ConfigurationSection)entry.getValue();
            Iterable iterable2 = (Iterable)INSTANCE.expandTaggedRecipes(section);

            for(Object obj : iterable2) {
               ConfigurationSection it = (ConfigurationSection)obj;
               int value3 = 0;
               INSTANCE.registerRecipeByType(recipeType, it);
            }
         }
      }

   }

   private final List<ConfigurationSection> expandTaggedRecipes(ConfigurationSection section) {
      if (this.collectTagSlots(this.deepMutableMap(section), (List)null).isEmpty()) {
         return CollectionsKt.listOf(section);
      } else {
         Iterable iterable = (Iterable)this.collectTagSlots(this.deepMutableMap(section), (List)null);
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            Map it = (Map)item;
            int value = 0;
            destination.add(NexoTag.itemIds(String.valueOf(it.get("nexo_tag"))));
         }

         List tagItemIds = (List)destination;
         iterable = (Iterable)this.cartesianProduct(tagItemIds);
         destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
         int index = 0;

         for(Object obj : iterable) {
            int value2 = index++;
            if (value2 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            List combo = (List)obj;
            int value3 = 0;
            LinkedHashMap map = INSTANCE.deepMutableMap(section);
            Iterable iterable2 = (Iterable)INSTANCE.collectTagSlots(map, (List)null);
            int value5 = 0;

            for(Object obj2 : iterable2) {
               int slot = value5++;
               if (slot < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               Map ingredient = (Map)obj2;
               int value4 = 0;
               ingredient.remove("nexo_tag");
               ingredient.put("nexo_item", combo.get(slot));
            }

            destination.add((new YamlConfiguration()).createSection(section.getName() + "_" + value2, (Map)map));
         }

         return (List)destination;
      }
   }

   private final LinkedHashMap<String, Object> deepMutableMap(ConfigurationSection configurationSection) {
      Iterable entrySet = (Iterable)configurationSection.getValues(false).entrySet();
      Map destination = (Map)(new LinkedHashMap());

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value2 = 0;
         String key = (String)entry.getKey();
         Object value = entry.getValue();
         Pair pair = TuplesKt.to(key, INSTANCE.deepMutableValue(value));
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return (LinkedHashMap)destination;
   }

   private final Object deepMutableValue(Object value) {
      Object obj;
      if (value instanceof ConfigurationSection) {
         obj = this.deepMutableMap((ConfigurationSection)value);
      } else if (value instanceof Map) {
         Iterable entrySet = (Iterable)((Map)value).entrySet();
         Map destination = (Map)(new LinkedHashMap());

         for(Object element : entrySet) {
            Map.Entry entry = (Map.Entry)element;
            int value2 = 0;
            Object k = entry.getKey();
            Object v = entry.getValue();
            Pair pair = TuplesKt.to(String.valueOf(k), INSTANCE.deepMutableValue(v));
            destination.put(pair.getFirst(), pair.getSecond());
         }

         obj = destination;
      } else if (value instanceof List) {
         Iterable iterable = (Iterable)value;
         Collection collection = (Collection)((List)(new ArrayList()));

         for(Object item : iterable) {
            int value3 = 0;
            collection.add(INSTANCE.deepMutableValue(item));
         }

         obj = collection;
      } else {
         obj = value;
      }

      return obj;
   }

   private final List<Map<String, Object>> collectTagSlots(Object value, List<Map<String, Object>> out) {
      // the Kotlin default argument (an empty list) was lost in recovery: without it no custom recipe loads
      if (out == null) {
         out = new ArrayList<>();
      }

      if (TypeIntrinsics.isMutableMap(value)) {
         if (value == null) {
            throw new NullPointerException("null cannot be cast to non-null type kotlin.collections.MutableMap<kotlin.String, kotlin.Any?>");
         }

         Map map = TypeIntrinsics.asMutableMap(value);
         int value2 = 0;
         if (map.containsKey("nexo_tag")) {
            ((Collection)out).add(map);
         } else {
            Iterable iterable = (Iterable)map.values();

            for(Object element : iterable) {
               int value3 = 0;
               INSTANCE.collectTagSlots(element, out);
            }
         }
      } else if (value instanceof List) {
         Iterable iterable2 = (Iterable)value;

         for(Object obj : iterable2) {
            int value4 = 0;
            INSTANCE.collectTagSlots(obj, out);
         }
      }

      return out;
   }

   private final List<List<String>> cartesianProduct(List<? extends List<String>> lists) {
      Iterable iterable = (Iterable)lists;
      Object initial = CollectionsKt.listOf(CollectionsKt.emptyList());
      Object accumulator = initial;

      for(Object element : iterable) {
         List list = (List)element;
         int value = 0;
         Iterable iterable2 = (Iterable)accumulator;
         Collection destination = (Collection)(new ArrayList());

         for(Object obj : iterable2) {
            List combo = (List)obj;
            int value2 = 0;
            Iterable iterable3 = (Iterable)list;
            Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

            for(Object item : iterable3) {
               String it = (String)item;
               int value3 = 0;
               collection.add(CollectionsKt.plus((Collection)combo, it));
            }

            Iterable iterable4 = (Iterable)((List)collection);
            CollectionsKt.addAll(collection, iterable4);
         }

         accumulator = (List)destination;
      }

      return (List<List<String>>)accumulator;
   }

   private final void registerRecipeByType(RecipeType recipeType, ConfigurationSection recipeSection) {
      RecipesManager recipesManager = this;

      Object obj;
      try {
         RecipesManager recipesManager2 = recipesManager;
         int value = 0;
         switch (RecipesManager.WhenMappings.$EnumSwitchMapping$0[recipeType.ordinal()]) {
            case 1 -> (new ShapedLoader(recipeSection)).registerRecipe();
            case 2 -> (new ShapelessLoader(recipeSection)).registerRecipe();
            case 3 -> (new FurnaceLoader(recipeSection)).registerRecipe();
            case 4 -> (new BlastingLoader(recipeSection)).registerRecipe();
            case 5 -> (new SmokingLoader(recipeSection)).registerRecipe();
            case 6 -> (new StonecuttingLoader(recipeSection)).registerRecipe();
            case 7 -> (new BrewingLoader(recipeSection)).registerRecipe();
            case 8 -> (new CampfireLoader(recipeSection)).registerRecipe();
            default -> throw new NoWhenBranchMatchedException();
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      if (ResultShim.exceptionOrNull(obj) != null) {
         int value2 = 0;
         Message message = Message.BAD_RECIPE;
         TagResolver[] tagResolvers = new TagResolver[1];
         String name = recipeSection.getName();
         tagResolvers[0] = AdventureUtils.tagResolver("recipe", name);
         message.log(tagResolvers);
      }

   }

   static {
      File p0 = new File(NexoPlugin.Companion.instance().getDataFolder(), "recipes");
      int value = 0;
      p0.mkdirs();
      recipesFolder = p0;
      listeners = (List)(new ArrayList());
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.recipes.RecipeType.values().length];
         try { a_0[com.nexomc.nexo.recipes.RecipeType.SHAPED.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.SHAPELESS.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.FURNACE.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.BLASTING.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.SMOKER.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.STONECUTTING.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.BREWING.ordinal()] = 7; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.recipes.RecipeType.CAMPFIRE.ordinal()] = 8; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}

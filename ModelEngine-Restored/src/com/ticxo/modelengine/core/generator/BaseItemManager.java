package com.ticxo.modelengine.core.generator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.core.generator.java.BaseItem;
import com.ticxo.modelengine.core.generator.java.BaseItemTransformer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class BaseItemManager {
   private final ModelEngineAPI plugin;
   private final Gson gson;
   private final Map<BaseItemEnum, BaseItemManager.BaseItemCache> baseItems = new HashMap<>();
   private final Map<String, BlueprintBone> requested = new LinkedHashMap<>();
   private final File baseItemFolder;
   private final File itemModelFolder;
   private final File cachedIDJson;

   public BaseItemManager(ModelGeneratorImpl generator) {
      this.plugin = generator.getPlugin();
      this.gson = generator.getGson();
      this.baseItemFolder = TFile.createDirectory(generator.getAssetsFolder(), "minecraft", "models", "item");
      this.itemModelFolder = TFile.createDirectory(generator.getAssetsFolder(), "minecraft", "items");
      this.cachedIDJson = TFile.createFileOrEmpty(this.plugin.getDataFolder(), ".data", "cache.json");
   }

   public void updateModels() {
      Set<BaseItemEnum> items = ConfigProperty.ITEM_MODELS.getBaseItems();
      if (items.isEmpty()) {
         items.add(ConfigProperty.ITEM_MODEL.getBaseItem());
      }

      HashMap<BaseItemEnum, BaseItemManager.BaseItemCache> prevItems = new HashMap<>(this.baseItems);
      this.baseItems.clear();

      for (BaseItemEnum base : items) {
         String name = base.name().toLowerCase(Locale.ENGLISH);
         InputStream inputStream = this.plugin.getResource("pack/colorable/" + name + ".json");
         if (inputStream == null) {
            TLogger.warn("Unknown colorable item: " + name + ".");
         } else {
            InputStreamReader itemTemplateReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BaseItem baseItem = this.gson.fromJson(itemTemplateReader, BaseItem.class);
            baseItem.setName(name);
            baseItem.setBaseItemEnum(base);
            this.baseItems
               .computeIfAbsent(
                  base,
                  baseItemEnum -> {
                     BaseItemManager.BaseItemCache baseItemCache = prevItems.get(baseItemEnum);
                     return baseItemCache == null
                        ? new BaseItemManager.BaseItemCache(baseItemEnum, baseItem, new ModelIdCache())
                        : baseItemCache.setBaseItem(baseItem);
                  }
               );
         }
      }
   }

   public void requestId(BlueprintBone bone, Set<String> ids) {
      for (String id : ids) {
         this.requested.put(id, bone);
      }
   }

   public void endSession() {
      ModelCacheGroup group = new ModelCacheGroup();

      for (Entry<BaseItemEnum, BaseItemManager.BaseItemCache> entry : this.baseItems.entrySet()) {
         ModelIdCache cache = entry.getValue().getCache();
         cache.gatherExistingIds(entry.getKey(), this.requested);
         group.cache.put(entry.getKey().name(), cache);
      }

      for (Entry<String, BlueprintBone> entry : this.requested.entrySet()) {
         BaseItemManager.BaseItemCache cache = this.pollOptimalCache();
         cache.getCache().generateNewIds(cache.getBaseItemEnum(), entry.getKey(), entry.getValue());
      }

      for (Entry<BaseItemEnum, BaseItemManager.BaseItemCache> entry : this.baseItems.entrySet()) {
         ModelIdCache cache = entry.getValue().getCache();
         cache.endSession();
      }

      try (FileWriter writer = new FileWriter(this.cachedIDJson)) {
         writer.write(this.gson.toJson(group));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void clearOverrides() {
      this.baseItems.values().forEach(BaseItemManager.BaseItemCache::clearOverrides);
   }

   public void registerModels(String namespace, Map<String, String> aliases) {
      for (Entry<BaseItemEnum, BaseItemManager.BaseItemCache> entry : this.baseItems.entrySet()) {
         BaseItem baseItem = entry.getValue().getBaseItem();
         baseItem.addModels(namespace, entry.getValue().getCache(), aliases);
      }
   }

   public void createModelFiles() {
      this.baseItems.values().forEach(baseItemCache -> {
         BaseItem baseItem = baseItemCache.getBaseItem();
         File baseItemFile = TFile.createFile(this.baseItemFolder, baseItem.getName() + ".json");

         try (FileWriter writer = new FileWriter(baseItemFile)) {
            writer.write(this.gson.toJson(baseItem));
         } catch (IOException e) {
            e.printStackTrace();
         }

         ItemModel ver1214 = BaseItemTransformer.to1214Format(baseItem);
         File ver1214File = TFile.createFile(this.itemModelFolder, baseItem.getName() + ".json");

         try (FileWriter writer = new FileWriter(ver1214File)) {
            writer.write(this.gson.toJson(BaseItemTransformer.wrapModel(this.gson, ver1214, true)));
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }

   public void refreshCache() {
      TFile.recreateFile(this.cachedIDJson);

      try (FileReader reader = new FileReader(this.cachedIDJson)) {
         ModelCacheGroup cacheGroup = null;
         JsonObject generic = this.gson.fromJson(reader, JsonObject.class);
         if (generic != null) {
            String version = GSONUtils.get(generic, "version", JsonElement::getAsString, "R4.0.0");
            if (!generic.has("version") && !generic.has("cachedId")) {
               version = "B4.0.8";
            }

            TLogger.log(LogColor.BRIGHT_GREEN + "Loading cache version: " + version);
            switch (version) {
               case "R4.0.0":
                  BaseItemEnum item = ConfigProperty.ITEM_MODEL.getBaseItem();
                  ModelIdCache cache = this.gson.fromJson(generic, ModelIdCache.class);
                  cacheGroup = new ModelCacheGroup();
                  cacheGroup.cache.put(item.name(), cache);
                  break;
               case "B4.0.8":
                  JsonObject root = new JsonObject();
                  root.addProperty("version", "R4.0.8");
                  root.add("cache", generic);
                  cacheGroup = this.gson.fromJson(root, ModelCacheGroup.class);
                  break;
               case "R4.0.8":
                  cacheGroup = this.gson.fromJson(generic, ModelCacheGroup.class);
            }
         }

         if (cacheGroup == null) {
            TLogger.warn("Unable to read the model ID cache file. If the file is missing, ignore this warning.");
            cacheGroup = new ModelCacheGroup();

            for (BaseItemEnum baseItem : ConfigProperty.ITEM_MODELS.getBaseItems()) {
               cacheGroup.cache.put(baseItem.name(), new ModelIdCache());
            }
         }

         Set<BaseItemEnum> items = ConfigProperty.ITEM_MODELS.getBaseItems();
         if (items.isEmpty()) {
            items.add(ConfigProperty.ITEM_MODEL.getBaseItem());
         }

         for (Entry<String, ModelIdCache> entry : cacheGroup.cache.entrySet()) {
            BaseItemEnum item = BaseItemEnum.get(entry.getKey());
            if (item != null && items.contains(item)) {
               this.baseItems
                  .compute(
                     item,
                     (baseItemEnum, baseItemCache) -> baseItemCache == null
                        ? new BaseItemManager.BaseItemCache(baseItemEnum, new BaseItem(), entry.getValue())
                        : baseItemCache.setCache(entry.getValue())
                  );
            }
         }
      } catch (IOException e) {
         TLogger.error("Unable to read the model ID cache file. Is it corrupted?");
         e.printStackTrace();
      }
   }

   public void cleanUp() {
      this.baseItems.values().forEach(baseItemCache -> baseItemCache.getCache().cleanUp());
   }

   private BaseItemManager.BaseItemCache pollOptimalCache() {
      int load = Integer.MAX_VALUE;
      BaseItemManager.BaseItemCache cache = null;

      for (BaseItemManager.BaseItemCache baseItemCache : this.baseItems.values()) {
         int l = baseItemCache.getCache().getCacheLoad();
         if (l < load) {
            load = l;
            cache = baseItemCache;
         }
      }

      if (cache == null) {
         throw new RuntimeException("No cache is available!");
      } else {
         return cache;
      }
   }

   @AllArgsConstructor
   private static class BaseItemCache {
      @Getter
      private BaseItemEnum baseItemEnum;
      private BaseItem baseItem;
      private ModelIdCache cache;

      public BaseItem getBaseItem() {
         return this.baseItem;
      }

      public BaseItemManager.BaseItemCache setBaseItem(BaseItem baseItem) {
         this.baseItem = baseItem;
         return this;
      }

      public ModelIdCache getCache() {
         return this.cache;
      }

      public BaseItemManager.BaseItemCache setCache(ModelIdCache cache) {
         this.cache = cache;
         return this;
      }

      public void clearOverrides() {
         if (this.baseItem != null) {
            this.baseItem.clearOverrides();
         }
      }
   }
}

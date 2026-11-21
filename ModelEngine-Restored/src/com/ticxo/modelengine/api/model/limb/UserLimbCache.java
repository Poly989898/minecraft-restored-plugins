package com.ticxo.modelengine.api.model.limb;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class UserLimbCache {
   private final String name;
   private final String source;
   private final Map<String, String> classic = new HashMap<>();
   private final Map<String, String> slim = new HashMap<>();
   private transient Map<String, CompletableFuture<String>> requested = Maps.newConcurrentMap();
   private boolean classicGenerated;
   private boolean slimGenerated;

   public void initialize() {
      if (this.requested == null) {
         this.requested = Maps.newConcurrentMap();
      }
   }

   public void generate(SkinGeneratorService service, boolean slim) {
      this.generate(service, slim, null);
   }

   public void generate(SkinGeneratorService service, boolean slim, @Nullable Collection<String> limbTypes) {
      if (!service.isEnabled()) {
         TLogger.error(2, "Unable to generate user limb cache for " + this.name + ": Service is disabled!.");
      } else if (limbTypes == null || !this.isCovered(slim, limbTypes)) {
         byte[] imageBytes = Base64.getDecoder().decode(this.source);
         SkinSplitter splitter = ModelEngineAPI.getAPI().getSkinSplitter();
         Map<String, byte[]> skins = limbTypes == null ? splitter.splitSkin(imageBytes, slim) : splitter.splitSkin(imageBytes, slim, limbTypes);
         if (!skins.isEmpty()) {
            ArrayList<Entry<String, byte[]>> pending = new ArrayList<>();
            synchronized (this) {
               for (Entry<String, byte[]> entry : skins.entrySet()) {
                  if (!this.getMap(slim).containsKey(entry.getKey()) && !this.requested.containsKey(key(slim, entry.getKey()))) {
                     this.requested.put(key(slim, entry.getKey()), new CompletableFuture<>());
                     pending.add(entry);
                  }
               }
            }

            if (!pending.isEmpty()) {
               ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
               AtomicBoolean failed = new AtomicBoolean();
               AtomicBoolean stored = new AtomicBoolean();

               for (Entry<String, byte[]> entry : pending) {
                  String limbId = entry.getKey();
                  futures.add(service.generate(entry.getValue()).handle((s, throwable) -> {
                     CompletableFuture<String> future = this.requested.remove(key(slim, limbId));
                     if (throwable != null) {
                        failed.set(true);
                        TLogger.warn("Failed to generate skin limb '" + limbId + "' for " + this.name + ": " + describe(throwable));
                        if (future != null) {
                           future.completeExceptionally(throwable);
                        }

                        return null;
                     } else {
                        synchronized (this) {
                           this.getMap(slim).put(limbId, s);
                        }

                        stored.set(true);
                        if (future != null) {
                           future.complete(s);
                        }

                        return null;
                     }
                  }));
               }

               CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRunAsync(() -> {
                  if (!failed.get() && limbTypes == null) {
                     synchronized (this) {
                        this.setGenerated(slim, true);
                     }
                  }

                  if (stored.get()) {
                     this.saveCache();
                  }
               }).handle((unused, throwable) -> {
                  if (throwable != null) {
                     throwable.printStackTrace();
                  }

                  return null;
               });
            }
         }
      }
   }

   public boolean hasSkinUrl(String limbId, boolean slim) {
      synchronized (this) {
         return this.getMap(slim).containsKey(limbId);
      }
   }

   public CompletableFuture<String> getSkinUrl(String limbId, boolean slim) {
      synchronized (this) {
         String url = this.getMap(slim).get(limbId);
         return url != null ? CompletableFuture.completedFuture(url) : this.requested.computeIfAbsent(key(slim, limbId), s -> new CompletableFuture<>());
      }
   }

   public void saveCache() {
      ModelEngineAPI api = ModelEngineAPI.getAPI();
      String cacheId = UUID.nameUUIDFromBytes(this.source.getBytes()).toString();
      String header = cacheId.substring(0, 2);
      String remain = cacheId.substring(2);
      File file = TFile.createFile(api.getDataFolder(), ".data", "userlimbs", header, remain + ".json");

      try (FileWriter writer = new FileWriter(file)) {
         String data;
         synchronized (this) {
            data = api.getGson().toJson(this);
         }

         writer.write(data);
         TLogger.log(LogColor.BRIGHT_GREEN + "Limb data generated: " + this.name);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private synchronized boolean isCovered(boolean slim, Collection<String> limbTypes) {
      Map<String, String> map = this.getMap(slim);

      for (String limbId : limbTypes) {
         if (!map.containsKey(limbId) && !this.requested.containsKey(key(slim, limbId))) {
            return false;
         }
      }

      return true;
   }

   private static String key(boolean slim, String limbId) {
      return (slim ? "slim/" : "classic/") + limbId;
   }

   private Map<String, String> getMap(boolean slim) {
      return slim ? this.slim : this.classic;
   }

   private void setGenerated(boolean slim, boolean flag) {
      if (slim) {
         this.slimGenerated = flag;
      } else {
         this.classicGenerated = flag;
      }
   }

   public boolean isGenerated(boolean slim) {
      return slim ? this.slimGenerated : this.classicGenerated;
   }

   private static String describe(Throwable throwable) {
      Throwable cause = throwable;

      while (cause.getCause() != null && cause.getCause() != cause) {
         cause = cause.getCause();
      }

      String message = cause.getMessage();
      return message != null ? message : cause.getClass().getSimpleName();
   }
}

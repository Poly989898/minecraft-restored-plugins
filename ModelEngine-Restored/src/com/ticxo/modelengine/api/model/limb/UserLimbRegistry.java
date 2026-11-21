package com.ticxo.modelengine.api.model.limb;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.MojangAPI;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures.SkinModel;

public class UserLimbRegistry extends TUnaryRegistry<UserLimbCache> {
   private final Map<String, String> keyedSources = new HashMap<>();
   private final File skinDir;

   public UserLimbRegistry(ModelEngineAPI plugin) {
      this.skinDir = TFile.createDirectory(plugin.getDataFolder(), "skins");
   }

   public void generateDefaults() {
      ConfigProperty skins = ConfigProperty.DEFAULT_SKINS;
      ConfigurationSection section = skins.getSection();

      for (String key : section.getKeys(false)) {
         ConfigurationSection data = section.getConfigurationSection(key);
         if (data != null) {
            TLogger.log(LogColor.CYAN + "Loading default skin: " + key);
            CompletableFuture<String> base64 = this.getImageData(data);
            if (base64 == null) {
               TLogger.warn("Failed to eager load " + key + ": Missing data value.");
            } else {
               boolean slim = data.getBoolean("slim", false);
               base64.handle((s, throwable) -> {
                  if (throwable != null) {
                     throwable.printStackTrace();
                  } else {
                     this.keyedSources.put(key, s);
                     this.generate(key, s, slim);
                     TLogger.log(LogColor.BRIGHT_GREEN + "Registered default skin: " + key);
                  }

                  return null;
               });
            }
         }
      }
   }

   private CompletableFuture<String> getImageData(ConfigurationSection section) {
      for (String key : section.getKeys(false)) {
         switch (key) {
            case "base64":
               return CompletableFuture.supplyAsync(() -> section.getString(key));
            case "url":
               HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(section.getString(key)))
                  .timeout(Duration.of(30L, ChronoUnit.SECONDS))
                  .GET()
                  .build();
               HttpClient client = HttpClient.newHttpClient();
               return client.sendAsync(request, BodyHandlers.ofByteArray())
                  .thenApply(HttpResponse::body)
                  .thenApply(bytes -> Base64.getEncoder().encodeToString(bytes));
            case "path":
               File imgFile = TFile.createFile(this.skinDir, section.getString(key));
               if (imgFile.exists()) {
                  return CompletableFuture.<BufferedImage>supplyAsync(() -> {
                     try {
                        return ImageIO.read(imgFile);
                     } catch (IOException e) {
                        throw new RuntimeException(e);
                     }
                  }).thenApply(image -> {
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                     try {
                        ImageIO.write(image, "png", buffer);
                        return buffer;
                     } catch (IOException e) {
                        throw new RuntimeException(e);
                     }
                  }).thenApply(stream -> Base64.getEncoder().encodeToString(stream.toByteArray()));
               }
               break;
            case "username":
               String name = section.getString(key);
               return CompletableFuture.<UUID>supplyAsync(() -> MojangAPI.getUUIDFromUsername(name))
                  .thenApply(MojangAPI::fromUUID)
                  .thenCompose(MojangAPI::getRawSkinData);
            case "uuid":
               UUID uuid = UUID.fromString(section.getString(key));
               return CompletableFuture.<PlayerProfile>supplyAsync(() -> MojangAPI.fromUUID(uuid)).thenCompose(MojangAPI::getRawSkinData);
         }
      }

      return null;
   }

   public boolean tryLoad(String cacheId) {
      if (this.registry.containsKey(cacheId)) {
         return true;
      }

      ModelEngineAPI api = ModelEngineAPI.getAPI();
      Gson gson = api.getGson();
      String header = cacheId.substring(0, 2);
      String remain = cacheId.substring(2);
      File file = TFile.createFile(api.getDataFolder(), ".data", "userlimbs", header, remain + ".json");
      if (!file.exists()) {
         return false;
      }

      try {
         UserLimbCache cache = gson.fromJson(new FileReader(file), UserLimbCache.class);
         cache.initialize();
         this.register(cacheId, cache);
         return true;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public void generate(Player player) {
      PlayerProfile playerProfile = player.getPlayerProfile();
      boolean slim = playerProfile.getTextures().getSkinModel() == SkinModel.SLIM;
      MojangAPI.getRawSkinData(playerProfile).thenAccept(base64 -> this.generate(base64, slim)).handle(MiscUtils::handle);
   }

   public void generate(byte[] data, boolean slim) {
      this.generate(Base64.getEncoder().encodeToString(data), slim);
   }

   public UserLimbCache generate(String base64, boolean slim) {
      return this.generate(UUID.nameUUIDFromBytes(base64.getBytes()).toString(), base64, slim);
   }

   public UserLimbCache generate(String name, String base64, boolean slim) {
      synchronized (this.registry) {
         UserLimbCache cache = this.getOrCreate(name, base64);
         cache.generate(ModelEngineAPI.getSkinGeneratorService(), slim, this.getUsedLimbTypes());
         return cache;
      }
   }

   public CompletableFuture<String> getSkinUrlFromKey(String key, String limbType, boolean slim) {
      String src = this.keyedSources.get(key);
      return src == null
         ? CompletableFuture.failedFuture(new RuntimeException("Unknown limb source with key: " + key))
         : this.getSkinUrlFromSource(src, limbType, slim);
   }

   public CompletableFuture<String> getSkinUrlFromSource(String source, String limbType, boolean slim) {
      synchronized (this.registry) {
         String cacheId = UUID.nameUUIDFromBytes(source.getBytes()).toString();
         UserLimbCache cache = this.getOrCreate(cacheId, source);
         cache.generate(ModelEngineAPI.getSkinGeneratorService(), slim, Collections.singletonList(limbType));
         return cache.getSkinUrl(limbType, slim);
      }
   }

   private UserLimbCache getOrCreate(String name, String source) {
      String cacheId = UUID.nameUUIDFromBytes(source.getBytes()).toString();
      if (!this.tryLoad(cacheId)) {
         this.register(cacheId, new UserLimbCache(name, source));
      }

      return this.get(cacheId);
   }

   private Set<String> getUsedLimbTypes() {
      BoneBehaviorType type = BoneBehaviorTypes.USER_LIMB;
      if (type == null) {
         return Set.of();
      }

      String behaviorId = type.getId();
      HashSet<String> limbTypes = new HashSet<>();

      for (ModelBlueprint blueprint : ModelEngineAPI.getAPI().getModelRegistry().getValues()) {
         Map<String, BlueprintBone> flatMap = blueprint.getFlatMap();
         if (flatMap != null) {
            for (BlueprintBone bone : flatMap.values()) {
               Map<String, Object> behavior = bone.getBehaviors().get(behaviorId);
               if (behavior != null && behavior.get("type") instanceof String limbType) {
                  limbTypes.add(limbType);
               }
            }
         }
      }

      return limbTypes;
   }
}

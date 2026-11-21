package com.ticxo.modelengine.core.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.events.ModelRegistrationEvent;
import com.ticxo.modelengine.api.events.RegisterParserEvent;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.generator.assets.ItemModel;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.ModelParser;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.logger.LogColor;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.generator.atlas.AtlasManager;
import com.ticxo.modelengine.core.generator.java.BaseItemTransformer;
import com.ticxo.modelengine.core.generator.parser.blockbench.BlockbenchParser;
import com.ticxo.modelengine.core.javasupport.Java21Access;
import it.unimi.dsi.fastutil.Pair;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

@Getter
public class ModelGeneratorImpl implements ModelGenerator {
   private static final List<String> OVERLAYS = List.of("1_19_4", "1_21_2", "1_21_4", "1_21_5", "1_21_6", "26_1", "26_2");
   private final ModelEngineAPI plugin;
   private final Gson gson;
   private final List<ModelParser> parsers = new ArrayList<>();
   private final List<ModelAssets> assets = new ArrayList<>();
   private final AtlasManager atlasManager;
   private final BaseItemManager baseItemManager;
   private final ExecutorService generatorService = Executors.newFixedThreadPool(
      ConfigProperty.MAX_GENERATOR_THREADS.getIntOrIfNeg(() -> Runtime.getRuntime().availableProcessors())
   );
   private final File blueprintFolder;
   private final File internalFolder;
   private final File internalExampleFolder;
   private final File internalBlueprintFolder;
   private final File packFolder;
   private final File assetsFolder;
   private final File zippedResourcePack;
   private final Map<ModelGenerator.Phase, Set<Runnable>> tasks = Maps.newConcurrentMap();
   private final Set<ModelGenerator.Phase> executed = new HashSet<>();
   private String namespace;
   private String texturePath;
   private File modelFolder;
   private File itemsFolder;
   private boolean generateMeta;
   private boolean deleteOld;
   private boolean mergeItemModels;
   private Set<String> disabledOverlays = Set.of();
   private boolean initialized;
   private final long[] timer = new long[3];
   private final Map<String, String> textureNameCache = new HashMap<>();
   private final Set<ResourceLocation> writtenTextures = ConcurrentHashMap.newKeySet();
   private final Map<String, String> modelAliases = new ConcurrentHashMap<>();

   public ModelGeneratorImpl(ModelEngineAPI plugin) {
      this.plugin = plugin;
      this.gson = plugin.getGson();
      this.blueprintFolder = TFile.createDirectory(plugin.getDataFolder(), "blueprints");
      this.internalFolder = TFile.createDirectory(plugin.getDataFolder(), "internals");
      this.internalExampleFolder = TFile.createDirectory(this.internalFolder, "examples");
      this.internalBlueprintFolder = TFile.createDirectory(this.internalFolder, "blueprints");
      this.packFolder = TFile.createDirectory(plugin.getDataFolder(), "resource pack");
      this.assetsFolder = TFile.createDirectory(this.packFolder, "assets");
      this.zippedResourcePack = TFile.createFile(plugin.getDataFolder(), "resource pack.zip");
      this.atlasManager = new AtlasManager(this);
      this.baseItemManager = new BaseItemManager(this);
      this.baseItemManager.refreshCache();
      plugin.getConfigManager().registerReferenceUpdate(this::updateConfig);
      this.parsers.add(new BlockbenchParser(this));
      ModelEngineAPI.callEvent(new RegisterParserEvent(this.parsers));
   }

   @Override
   public void importModels(boolean isStartup) {
      this.executed.clear();
      this.queueTask(ModelGenerator.Phase.PRE_IMPORT, () -> this.timer[0] = System.nanoTime());
      this.queueTask(ModelGenerator.Phase.POST_IMPORT, () -> this.timer[0] = System.nanoTime() - this.timer[0]);
      this.queueTask(ModelGenerator.Phase.PRE_ASSETS, () -> this.timer[1] = System.nanoTime());
      this.queueTask(ModelGenerator.Phase.POST_ASSETS, () -> this.timer[1] = System.nanoTime() - this.timer[1]);
      this.queueTask(ModelGenerator.Phase.PRE_ZIPPING, () -> this.timer[2] = System.nanoTime());
      this.queueTask(ModelGenerator.Phase.POST_ZIPPING, () -> this.timer[2] = System.nanoTime() - this.timer[2]);
      this.queueTask(ModelGenerator.Phase.FINISHED, () -> {
         TLogger.log(LogColor.BRIGHT_GREEN + "Generator Profiled:");
         TLogger.log(LogColor.BRIGHT_GREEN + " - Import Phase: " + MiscUtils.FORMATTER.format(this.timer[0] / 1000000.0) + "ms");
         TLogger.log(LogColor.BRIGHT_GREEN + " - Assets Phase: " + MiscUtils.FORMATTER.format(this.timer[1] / 1000000.0) + "ms");
         TLogger.log(LogColor.BRIGHT_GREEN + " - Zipping Phase: " + MiscUtils.FORMATTER.format(this.timer[2] / 1000000.0) + "ms");
      });
      if (isStartup && !ConfigProperty.LATE_REGISTER.getBoolean()) {
         this.importModelsInternal(true);
      } else {
         DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(() -> this.importModelsInternal(isStartup)));
      }
   }

   @Override
   public void generateAssets(boolean isStartup) {
      if (!ConfigProperty.LATE_REGISTER.getBoolean() && ConfigProperty.LATE_ASSETS.getBoolean()) {
         DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(() -> this.generateAssetsInternal(isStartup)));
      } else {
         this.generateAssetsInternal(true);
      }
   }

   @Override
   public void zipResourcePack(boolean isStartup) {
      if (!ConfigProperty.ZIP.getBoolean()) {
         this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
      } else {
         if (!ConfigProperty.LATE_ASSETS.getBoolean() && ConfigProperty.LATE_ZIPPING.getBoolean()) {
            DualTicker.queueAsyncTask(() -> CompletableFuture.runAsync(this::zipResourcePackInternal));
         } else {
            this.zipResourcePackInternal();
         }
      }
   }

   @Override
   public void updateConfig() {
      this.namespace = ConfigProperty.NAMESPACE.getString().toLowerCase(Locale.ENGLISH);
      this.texturePath = ConfigProperty.TEXTURE_PATH.getString().toLowerCase(Locale.ENGLISH);
      this.modelFolder = TFile.createDirectory(this.assetsFolder, this.namespace, "models");
      this.itemsFolder = TFile.createDirectory(this.assetsFolder, this.namespace, "items");
      this.baseItemManager.updateModels();
      this.generateMeta = ConfigProperty.MCMETA.getBoolean();
      this.deleteOld = ConfigProperty.DELETE_OLD.getBoolean();
      this.mergeItemModels = ConfigProperty.MERGE_ITEM_MODELS.getBoolean();
      this.disabledOverlays = new HashSet<>();
      List<?> raw = ConfigProperty.DISABLED_OVERLAYS.getStringList();
      ArrayList<String> resolved = new ArrayList<>();

      for (Object entry : raw) {
         String text = String.valueOf(entry).trim();
         String version = OVERLAYS.stream().filter(overlay -> overlay.equals(text) || digitsOf(overlay).equals(digitsOf(text))).findFirst().orElse(null);
         if (version == null) {
            TLogger.warn("Unknown entry in Disabled-Overlays: " + text + ". Valid overlays: " + OVERLAYS);
         } else {
            this.disabledOverlays.add(version);
            resolved.add(version);
         }
      }

      if (!raw.equals(resolved)) {
         this.plugin.getConfigManager().set(ConfigProperty.DISABLED_OVERLAYS, resolved);
         this.plugin.getConfigManager().save();
      }
   }

   @Override
   public void queueTask(ModelGenerator.Phase post, Runnable task) {
      if (!this.executed.contains(ModelGenerator.Phase.FINISHED) && !this.executed.contains(post)) {
         this.tasks.computeIfAbsent(post, phase -> new LinkedHashSet<>()).add(task);
      } else {
         task.run();
      }
   }

   private void executeQueuedTask(ModelGenerator.Phase phase) {
      this.executed.add(phase);
      if (phase == ModelGenerator.Phase.FINISHED) {
         this.tasks.values().forEach(runnables -> runnables.forEach(Runnable::run));
         this.tasks.clear();
      } else {
         this.tasks.computeIfPresent(phase, (p, runnables) -> {
            runnables.forEach(Runnable::run);
            runnables.clear();
            return runnables;
         });
      }

      ModelEngineAPI.callEvent(new ModelRegistrationEvent(phase));
   }

   private void importModelsInternal(boolean isStartup) {
      TLogger.log();
      TLogger.log(LogColor.BOLD + LogColor.CYAN.toString() + "[Importing models]");
      this.initialized = false;
      this.executeQueuedTask(ModelGenerator.Phase.PRE_IMPORT);
      this.baseItemManager.refreshCache();
      this.textureNameCache.clear();
      ModelRegistry registry = this.plugin.getModelRegistry();
      registry.clearRegistry();
      this.copyInternalFiles();
      List<File> files = Lists.newArrayList();
      File[] fa = this.blueprintFolder.listFiles();
      if (fa != null) {
         files.addAll(Arrays.asList(fa));
      }

      fa = this.internalBlueprintFolder.listFiles();
      if (fa != null) {
         files.addAll(Arrays.asList(fa));
      }

      if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
         try {
            files.addAll(Java21Access.getMythicPackModelFiles.call());
         } catch (Throwable ex) {
            ex.printStackTrace();
         }
      }

      if (files.isEmpty()) {
         this.initialized = true;
         this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
      } else {
         Queue<File> compartment = new LinkedList<>();
         files.sort(Ordering.natural());
         ArrayList<CompletableFuture<Void>> importTasks = new ArrayList<>();

         outer:
         while (true) {
            for (File file : files) {
               if (!file.isFile()) {
                  if (file.isDirectory()) {
                     compartment.add(file);
                  }
               } else {
                  importTasks.add(
                     CompletableFuture.runAsync(
                           () -> {
                              boolean fileParsed = false;
                              ErrorCollector collector = new ErrorCollector(file.getName());

                              for (ModelParser parser : this.parsers) {
                                 if (parser.validateFile(file)) {
                                    try {
                                       Pair<ModelBlueprint, ModelAssets> modelData = parser.generate(file, collector);
                                       if (modelData != null) {
                                          ModelBlueprint blueprint = modelData.left();
                                          ModelAssets asset = modelData.right();
                                          blueprint.finalizeModel(collector);

                                          for (Entry<String, BlueprintBone> boneEntry : blueprint.getFlatMap().entrySet()) {
                                             BlueprintBone bone = boneEntry.getValue();
                                             if (bone.isRenderer()) {
                                                synchronized (this.baseItemManager) {
                                                   this.baseItemManager.requestId(bone, bone.getModelData().getMultiModels().getKeys());
                                                }

                                                if (this.mergeItemModels) {
                                                   bone.getModelData()
                                                      .setSingleComposite(
                                                         new ItemModelData.SingleComposite(new NamespacedKey(this.namespace, blueprint.getName()))
                                                      );
                                                   bone.getModelData().setVariant(bone.getName());
                                                } else {
                                                   bone.getModelData()
                                                      .setSingleComposite(
                                                         new ItemModelData.SingleComposite(
                                                            new NamespacedKey(this.namespace, blueprint.getName() + "/" + bone.getName())
                                                         )
                                                      );
                                                }
                                             }
                                          }

                                          synchronized (registry) {
                                             registry.registerBlueprint(blueprint);
                                          }

                                          synchronized (this.assets) {
                                             this.assets.add(asset);
                                          }

                                          fileParsed = true;
                                          break;
                                       }

                                       if (collector.hasError()) {
                                          collector.logAll();
                                       }
                                    } catch (Exception e) {
                                       e.printStackTrace();
                                       break;
                                    }
                                 }
                              }

                              if (!fileParsed) {
                                 IError.UNKNOWN_FORMAT.log(collector);
                              }

                              synchronized (this.timer) {
                                 collector.logAll();
                              }
                           },
                           this.generatorService
                        )
                        .handle((res, ex) -> {
                           if (ex != null) {
                              ex.printStackTrace();
                           }

                           return null;
                        })
                  );
               }
            }

            while (!compartment.isEmpty()) {
               File[] ca = compartment.poll().listFiles();
               if (ca != null && ca.length != 0) {
                  files = Arrays.asList(ca);
                  continue outer;
               }
            }

            CompletableFuture.allOf(importTasks.toArray(CompletableFuture[]::new)).thenRunAsync(() -> {
               this.initialized = true;
               this.baseItemManager.endSession();
               this.executeQueuedTask(ModelGenerator.Phase.POST_IMPORT);
               this.generateAssets(isStartup);
            }).handle((res, ex) -> {
               if (ex != null) {
                  ex.printStackTrace();
               }

               return null;
            });
            return;
         }
      }
   }

   private void generateAssetsInternal(boolean isStartup) {
      this.executeQueuedTask(ModelGenerator.Phase.PRE_ASSETS);
      this.baseItemManager.clearOverrides();
      this.atlasManager.reset();
      this.writtenTextures.clear();
      this.modelAliases.clear();
      if (this.deleteOld && !TFile.delete(this.packFolder)) {
         TLogger.error("Failed to clear old resource pack folder.");
      }

      ArrayList<CompletableFuture<Void>> assetTasks = new ArrayList<>();

      for (ModelAssets asset : this.assets) {
         assetTasks.add(
            CompletableFuture.runAsync(
                  () -> {
                     for (BlueprintTexture texture : asset.getTextures()) {
                        if (!"minecraft".equals(texture.getPath().getNamespace())) {
                           this.atlasManager.addSingle(texture.getPath());
                           if (this.writtenTextures.add(texture.getPath())) {
                              File png = TFile.createFile(this.assetsFolder, "textures", texture.getPath(), "png");
                              BufferedImage image = TFile.toImage(texture.getSource());

                              try {
                                 byte[] palette = PaletteImageWriter.encode(image);
                                 if (palette != null) {
                                    Files.write(png.toPath(), palette);
                                 } else {
                                    ImageIO.write(image, "png", png);
                                 }
                              } catch (IOException e) {
                                 e.printStackTrace();
                              }

                              if (texture.getMcMeta() != null && this.shouldGenerateMCMeta(image, texture)) {
                                 HashMap<String, BlueprintTexture.MCMeta> lazyMCMeta = new HashMap<>();
                                 File mcmeta = TFile.createFile(this.assetsFolder, "textures", texture.getPath(), "png.mcmeta");

                                 try (FileWriter writer = new FileWriter(mcmeta)) {
                                    lazyMCMeta.put("animation", texture.getMcMeta());
                                    writer.write(this.gson.toJson(lazyMCMeta));
                                 } catch (IOException e) {
                                    e.printStackTrace();
                                 }
                              }
                           }
                        }
                     }

                     ItemModel.Select select = this.mergeItemModels ? new ItemModel.Select() : null;
                     HashMap<String, String> writtenModels = new HashMap<>();

                     for (Collection<JavaItemModel> models : asset.getModels().values()) {
                        if (models.size() == 1) {
                           JavaItemModel model = models.iterator().next();
                           String json = this.gson.toJson(model);
                           String canonical = writtenModels.putIfAbsent(json, model.getName());
                           if (canonical == null) {
                              canonical = model.getName();
                              File modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName() + ".json");

                              try (FileWriter writer = new FileWriter(modelJson)) {
                                 writer.write(json);
                              } catch (IOException e) {
                                 e.printStackTrace();
                              }
                           } else {
                              this.modelAliases.put(asset.getName() + "/" + model.getName(), asset.getName() + "/" + canonical);
                           }

                           this.writeItemModel(
                              asset, model.getName(), new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + canonical)), select
                           );
                        } else {
                           int id = 0;
                           String modelName = "";
                           ItemModel.Composite composite = new ItemModel.Composite();

                           for (JavaItemModel model : models) {
                              model.finalizeModel();
                              File modelJson;
                              if (id == 0) {
                                 modelName = model.getName();
                                 modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName() + ".json");
                                 composite.getModels().add(new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + model.getName())));
                              } else {
                                 modelJson = TFile.createFile(this.modelFolder, asset.getName(), model.getName(), id + ".json");
                                 composite.getModels()
                                    .add(new ItemModel.Model(new NamespacedKey(this.namespace, asset.getName() + "/" + model.getName() + "/" + id)));
                              }

                              id++;

                              try (FileWriter writer = new FileWriter(modelJson)) {
                                 writer.write(this.gson.toJson(model));
                              } catch (IOException e) {
                                 e.printStackTrace();
                              }
                           }

                           this.writeItemModel(asset, modelName, composite, select);
                        }
                     }

                     if (select != null && !select.getCases().isEmpty()) {
                        select.setFallback(select.getCases().get(0).model());
                        this.writeItemJson(TFile.createFile(this.itemsFolder, asset.getName() + ".json"), select, asset);
                     }
                  },
                  this.generatorService
               )
               .handleAsync((res, ex) -> {
                  if (ex != null) {
                     ex.printStackTrace();
                  }

                  return null;
               }, this.generatorService)
         );
      }

      CompletableFuture.allOf(assetTasks.toArray(CompletableFuture[]::new)).thenRunAsync(() -> {
         this.assets.clear();
         this.baseItemManager.registerModels(this.namespace, this.modelAliases);
         this.baseItemManager.createModelFiles();
         this.baseItemManager.cleanUp();
         if (ConfigProperty.ATLAS.getBoolean()) {
            this.atlasManager.generateFile();
         }

         this.copyResources();
         this.executeQueuedTask(ModelGenerator.Phase.POST_ASSETS);
         this.zipResourcePack(isStartup);
      }, this.generatorService).handleAsync((res, ex) -> {
         if (ex != null) {
            ex.printStackTrace();
         }

         return null;
      }, this.generatorService);
   }

   private void writeItemModel(ModelAssets asset, String boneName, ItemModel itemModel, ItemModel.Select select) {
      if (select != null) {
         select.getCases().add(new ItemModel.Select.Case(itemModel, boneName));
      } else {
         this.writeItemJson(TFile.createFile(this.itemsFolder, asset.getName(), boneName + ".json"), itemModel, asset);
      }
   }

   private void writeItemJson(File itemJson, ItemModel itemModel, ModelAssets asset) {
      try (FileWriter writer = new FileWriter(itemJson)) {
         writer.write(this.gson.toJson(BaseItemTransformer.wrapModel(this.gson, itemModel, asset.getName().equals("head"))));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void copyInternalFiles() {
      TFile.copyResource(this.plugin, TFile.createFile(this.internalExampleFolder, "player_model.bbmodel"), "internal/player_model.bbmodel");
      TFile.copyResource(this.plugin, TFile.createFile(this.internalBlueprintFolder, "internal_fire.bbmodel"), "internal/internal_fire.bbmodel");
   }

   private void copyResources() {
      TFile.copyResource(this.plugin, this.packFolder, "pack", "pack.png");
      if (this.disabledOverlays.isEmpty()) {
         HashMap<String, String> overlayReplacements = new HashMap<>();

         for (String version : OVERLAYS) {
            overlayReplacements.put("modelengine_" + version, this.namespace + "_" + version);
         }

         TFile.copyResourceTemplated(this.plugin, this.packFolder, "pack", "pack.mcmeta", "pack.mcmeta", overlayReplacements);
      } else {
         this.copyFilteredPackMeta();
      }

      if (ConfigProperty.SHADER.getBoolean()) {
         Map<String, String> namespaceReplacements = Map.of("modelengine:", this.namespace + ":");
         TFile.copyResourceTemplated(
            this.plugin,
            this.packFolder,
            "pack",
            "assets/modelengine/items/player_head.json",
            "assets/" + this.namespace + "/items/player_head.json",
            namespaceReplacements
         );
         if (this.isOverlayEnabled("1_21_4")) {
            TFile.copyResourceTemplated(
               this.plugin,
               this.packFolder,
               "pack",
               "modelengine_1_21_4/assets/modelengine/items/player_head.json",
               this.namespace + "_1_21_4/assets/" + this.namespace + "/items/player_head.json",
               namespaceReplacements
            );
         }

         for (String limb : List.of("head", "left_arm", "left_leg", "right_arm", "right_leg", "slim_left", "slim_right", "torso")) {
            TFile.copyResource(
               this.plugin,
               this.packFolder,
               "pack",
               "assets/modelengine/models/custom/entities/player/" + limb + ".json",
               "assets/" + this.namespace + "/models/custom/entities/player/" + limb + ".json"
            );
         }

         if (this.isOverlayEnabled("1_19_4")) {
            TFile.copyResourceTemplated(
               this.plugin,
               this.packFolder,
               "pack",
               "modelengine_1_19_4/assets/minecraft/models/item/player_head.json",
               this.namespace + "_1_19_4/assets/minecraft/models/item/player_head.json",
               namespaceReplacements
            );
         }

         this.copyOverlayResource("1_21_2", "assets/minecraft/shaders/core/rendertype_entity_translucent.fsh");
         this.copyOverlayResource("1_21_2", "assets/minecraft/shaders/core/rendertype_entity_translucent.json");
         this.copyOverlayResource("1_21_2", "assets/minecraft/shaders/core/rendertype_entity_translucent.vsh");
         this.copyOverlayResource("1_21_2", "assets/minecraft/shaders/core/entity.fsh");
         this.copyOverlayResource("1_21_2", "assets/minecraft/shaders/core/entity.vsh");
         this.copyOverlayResource("1_21_5", "assets/minecraft/shaders/core/entity.fsh");
         this.copyOverlayResource("1_21_5", "assets/minecraft/shaders/core/entity.vsh");
         this.copyOverlayResource("1_21_6", "assets/minecraft/shaders/core/entity.fsh");
         this.copyOverlayResource("1_21_6", "assets/minecraft/shaders/core/entity.vsh");
         this.copyOverlayResource("26_1", "assets/minecraft/shaders/core/entity.fsh");
         this.copyOverlayResource("26_1", "assets/minecraft/shaders/core/entity.vsh");
         this.copyOverlayResource("26_2", "assets/minecraft/shaders/core/entity.fsh");
         this.copyOverlayResource("26_2", "assets/minecraft/shaders/core/entity.vsh");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/entity.fsh");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/entity.vsh");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.fsh");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.json");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/core/rendertype_entity_translucent.vsh");
         TFile.copyResource(this.plugin, this.packFolder, "pack", "assets/minecraft/shaders/include/player_shader.glsl");
      }
   }

   private void copyOverlayResource(String version, String relativePath) {
      if (this.isOverlayEnabled(version)) {
         TFile.copyResource(
            this.plugin, this.packFolder, "pack", "modelengine_" + version + "/" + relativePath, this.namespace + "_" + version + "/" + relativePath
         );
      }
   }

   private static String digitsOf(String value) {
      return value.replaceAll("[^0-9]", "");
   }

   private boolean isOverlayEnabled(String version) {
      return !this.disabledOverlays.contains(version);
   }

   private void copyFilteredPackMeta() {
      File file = TFile.createFile(this.packFolder, "pack.mcmeta");
      if (!file.exists()) {
         try (InputStream reader = this.plugin.getResource("pack/pack.mcmeta")) {
            if (reader != null) {
               JsonObject root = this.gson.fromJson(new String(reader.readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
               JsonObject overlays = root.getAsJsonObject("overlays");
               if (overlays != null) {
                  JsonArray kept = new JsonArray();

                  for (JsonElement element : overlays.getAsJsonArray("entries")) {
                     JsonObject entry = element.getAsJsonObject();
                     String version = entry.get("directory").getAsString().substring("modelengine_".length());
                     if (this.isOverlayEnabled(version)) {
                        entry.addProperty("directory", this.namespace + "_" + version);
                        kept.add(entry);
                     }
                  }

                  overlays.add("entries", kept);
               }

               Files.writeString(file.toPath(), this.gson.toJson(root), StandardCharsets.UTF_8);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private void zipResourcePackInternal() {
      this.executeQueuedTask(ModelGenerator.Phase.PRE_ZIPPING);

      try {
         ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.zippedResourcePack), 65536));
         File[] files = this.packFolder.listFiles();
         if (files == null) {
            zipOut.close();
            this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
            return;
         }

         Arrays.sort(files);

         for (File file : files) {
            TFile.zipFile(file, file.getName(), zipOut, false);
         }

         zipOut.close();
         TLogger.log();
         TLogger.log(LogColor.BRIGHT_GREEN + "Resource pack zipped.");
      } catch (IOException e) {
         e.printStackTrace();
      }

      this.executeQueuedTask(ModelGenerator.Phase.POST_ZIPPING);
      this.executeQueuedTask(ModelGenerator.Phase.FINISHED);
   }

   private boolean shouldGenerateMCMeta(BufferedImage image, BlueprintTexture texture) {
      if (texture.getMcMeta().isMustGenerate()) {
         return true;
      }

      if (!this.generateMeta) {
         return false;
      }

      float textureRatio = (float)image.getHeight() / image.getWidth();
      float uvRatio = (float)texture.getFrameHeight() / texture.getFrameWidth();
      return TMath.isSimilar(textureRatio, uvRatio) ? false : textureRatio / uvRatio > 1.0F;
   }
}

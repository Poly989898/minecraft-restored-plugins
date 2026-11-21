package com.ticxo.modelengine.core.generator.parser.blockbench;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypes;
import com.ticxo.modelengine.api.animation.keyframe.data.KeyframeReaderRegistry;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.VectorKeyframe;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.error.WarnBadTexture;
import com.ticxo.modelengine.api.error.WarnDuplicateTexture;
import com.ticxo.modelengine.api.error.WarningDuplicateBoneName;
import com.ticxo.modelengine.api.events.RegisterBehaviorParserEvent;
import com.ticxo.modelengine.api.generator.assets.BlueprintTexture;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.ModelParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchDeserializer;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.ModelGeneratorImpl;
import com.ticxo.modelengine.core.generator.parser.blockbench.json.DefaultBehaviorParser;
import com.ticxo.modelengine.core.generator.parser.blockbench.json.MCMetaDeserializer;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import it.unimi.dsi.fastutil.Pair;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class BlockbenchParser implements ModelParser {
   private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(BlockbenchModel.class, new BlockbenchDeserializer())
      .registerTypeAdapter(BlueprintTexture.MCMeta.class, new MCMetaDeserializer())
      .create();
   private final Set<BlockbenchBehaviorParser> behaviorParsers = new LinkedHashSet<>();
   private final ModelGeneratorImpl generator;
   private final ConcurrentHashMap<String, BlockbenchParser.CachedBBFile> bbmodelCache = new ConcurrentHashMap<>();

   public BlockbenchParser(ModelGeneratorImpl generator) {
      this.generator = generator;
      this.behaviorParsers.add(new DefaultBehaviorParser());
      ModelEngineAPI.callEvent(new RegisterBehaviorParserEvent(this.behaviorParsers));
   }

   @Override
   public boolean validateFile(File file) {
      return TFile.isExtension(file.getName(), "bbmodel");
   }

   @Override
   public Pair<ModelBlueprint, ModelAssets> generate(File file, ErrorCollector collector) throws Exception {
      String modelName = TFile.removeExtension(file.getName()).toLowerCase(Locale.ENGLISH);

      try {
         String cacheKey = file.getAbsolutePath();
         long lastModified = file.lastModified();
         long size = file.length();
         BlockbenchParser.CachedBBFile cached = this.bbmodelCache.get(cacheKey);
         String json;
         if (cached != null && cached.lastModified == lastModified && cached.size == size) {
            json = cached.json;
         } else {
            byte[] bytes = Files.readAllBytes(file.toPath());
            CRC32 crc = new CRC32();
            crc.update(bytes);
            long hash = crc.getValue();
            json = new String(bytes);
            if (cached != null && cached.hash == hash) {
               this.bbmodelCache.put(cacheKey, new BlockbenchParser.CachedBBFile(lastModified, size, cached.hash, cached.json));
            } else {
               this.bbmodelCache.put(cacheKey, new BlockbenchParser.CachedBBFile(lastModified, size, hash, json));
            }
         }

         BlockbenchModel blockbenchModel = this.gson.fromJson(json, BlockbenchModel.class);
         ModelBlueprint blueprint = new ModelBlueprint();
         blueprint.setName(modelName);
         this.populateBlueprint(blockbenchModel, blueprint, collector);
         blueprint.constructFlatBoneMap(collector);
         blueprint.cacheBoneBehaviors(collector);
         ModelAssets assets = new ModelAssets();
         assets.setName(modelName);
         this.populateAssets(blockbenchModel, blueprint, assets, collector);
         return Pair.of(blueprint, assets);
      } catch (Exception ex) {
         final Exception e = ex;
         collector.collect(new IError() {
            @Override
            public String getErrorMessage() {
               return "Error: " + e.getMessage();
            }

            @Override
            public IError.Severity getSeverity() {
               return IError.Severity.ERROR;
            }
         });
         return null;
      }
   }

   private void populateBlueprint(BlockbenchModel bbmodel, ModelBlueprint blueprint, ErrorCollector collector) {
      this.behaviorParsers.forEach(parser -> parser.processModel(collector, bbmodel, blueprint));
      HashMap<UUID, BlueprintBone> uuidToBone = new HashMap<>();
      bbmodel.getOutliner().forEach((uuid2, group) -> {
         BlueprintBone bone = this.readBone(collector, bbmodel, null, group, uuidToBone);
         if (blueprint.getBones().containsKey(bone.getName())) {
            new WarningDuplicateBoneName(bone.getName(), bone.getUuid()).log(collector);
            bone.setName(uuid2.toString());
         }

         blueprint.getBones().put(bone.getName(), bone);
         uuidToBone.put(bone.getUuid(), bone);
      });

      for (Entry<String, BlockbenchModel.Animation> entry : bbmodel.getAnimations().entrySet()) {
         String name = entry.getKey().toLowerCase(Locale.ENGLISH);
         BlockbenchModel.Animation value = entry.getValue();
         BlueprintAnimation blueprintAnimation = new BlueprintAnimation(blueprint, name);
         if (value.getEffects() != null) {
            Map<String, Map<Float, BlockbenchModel.Keyframe>> effectChannels = value.getEffects().getChannels();
            Map<Float, BlockbenchModel.Keyframe> script = effectChannels.get("timeline");
            if (script != null) {
               for (Entry<Float, BlockbenchModel.Keyframe> scriptEntry : script.entrySet()) {
                  BlockbenchModel.Keyframe bbFrame = scriptEntry.getValue();
                  ScriptKeyframe frame = blueprintAnimation.getGlobalTimeline().getKeyframe(scriptEntry.getKey(), KeyframeTypes.SCRIPT);

                  for (Map<String, String> data : bbFrame.getData_points()) {
                     String instructions = data.getOrDefault("script", "");

                     for (String instruction : instructions.split("\n")) {
                        frame.getScript().add(ScriptKeyframe.Script.from(instruction));
                     }
                  }
               }
            }
         }

         for (Entry<UUID, BlockbenchModel.Animator> timelineEntry : value.getAnimators().entrySet()) {
            BlockbenchModel.Animator animator = timelineEntry.getValue();
            if (!animator.getChannels().isEmpty()) {
               boolean hasKeyframes = false;
               Timeline timeline = new Timeline(blueprintAnimation, animator.getRotation_global() != null && animator.getRotation_global());
               hasKeyframes |= putVectorKeyframes(animator, "position", timeline, KeyframeTypes.POSITION, 0.0625F, 0.0625F, -0.0625F);
               hasKeyframes |= putVectorKeyframes(
                  animator, "rotation", timeline, KeyframeTypes.ROTATION, (float) (Math.PI / 180.0), (float) (-Math.PI / 180.0), (float) (-Math.PI / 180.0)
               );
               hasKeyframes |= putVectorKeyframes(animator, "scale", timeline, KeyframeTypes.SCALE, 1.0F, 1.0F, 1.0F);
               if (hasKeyframes) {
                  UUID uuid = animator.getUuid();
                  if (uuid != null) {
                     blueprintAnimation.getTimelines().put(uuid, timeline);
                  }
               }
            }
         }

         blueprintAnimation.setLength(value.getLength().floatValue());
         blueprintAnimation.setLoopMode(BlueprintAnimation.LoopMode.get(value.getLoop()));
         blueprintAnimation.setOverride(value.getOverride());
         blueprint.getAnimations().put(name, blueprintAnimation);
      }

      String[] lines = bbmodel.getAnimation_variable_placeholders().split("\n");

      for (String line : lines) {
         String[] pair = line.split("=", 2);
         if (pair.length >= 2) {
            blueprint.getAnimationsPlaceholders().put(pair[0], pair[1]);
         }
      }
   }

   private BlueprintBone readBone(
      ErrorCollector collector, BlockbenchModel model, @Nullable BlueprintBone parent, BlockbenchModel.Group group, Map<UUID, BlueprintBone> uuidToBone
   ) {
      BlueprintBone blueprintBone = new BlueprintBone();
      blueprintBone.setName(group.getName().toLowerCase(Locale.ENGLISH));
      blueprintBone.setUuid(group.getUuid());
      blueprintBone.setGlobalPosition(vector3f(group.getOrigin()).mul(-0.0625F, 0.0625F, -0.0625F));
      Vector3f rotation = vector3f(group.getRotation()).mul((float) (-Math.PI / 180.0), (float) (Math.PI / 180.0), (float) (-Math.PI / 180.0));
      blueprintBone.setLocalRotation(rotation);
      blueprintBone.getLocalQuaternion().rotateZYX(rotation.z, rotation.y, rotation.x);
      if (parent != null) {
         blueprintBone.setLocalPosition(blueprintBone.getGlobalPosition().sub(parent.getGlobalPosition(), new Vector3f()));
         Quaternionf boneQuaternion = new Quaternionf().rotationZYX(rotation.z, rotation.y, rotation.x);
         Quaternionf parentQuaternion = parent.getGlobalQuaternion();
         parentQuaternion.mul(boneQuaternion, boneQuaternion);
         Vector3f global = TMath.getEulerAnglesZYX(boneQuaternion, new Vector3f());
         blueprintBone.setGlobalRotation(new Vector3f(global.x, global.y, global.z));
         blueprintBone.setGlobalQuaternion(boneQuaternion);
         Vector3f rotatedLocal = blueprintBone.getLocalPosition().rotate(parentQuaternion, new Vector3f());
         blueprintBone.setRotatedGlobalPosition(rotatedLocal.add(parent.getRotatedGlobalPosition()));
      } else {
         blueprintBone.setLocalPosition(new Vector3f(blueprintBone.getGlobalPosition()));
         blueprintBone.setGlobalRotation(rotation);
         blueprintBone.setGlobalQuaternion(new Quaternionf(blueprintBone.getLocalQuaternion()));
         blueprintBone.setRotatedGlobalPosition(new Vector3f(blueprintBone.getGlobalPosition()));
      }

      blueprintBone.setParent(parent);
      this.behaviorParsers.forEach(parser -> parser.processBone(collector, model, group, blueprintBone));

      for (Entry<UUID, BlockbenchModel.Group> entry : group.getChildGroup().entrySet()) {
         BlueprintBone child = this.readBone(collector, model, blueprintBone, entry.getValue(), uuidToBone);
         if (blueprintBone.getChildren().containsKey(child.getName())) {
            new WarningDuplicateBoneName(child.getName(), child.getUuid()).log(collector);
            child.setName(child.getUuid().toString());
         }

         blueprintBone.getChildren().put(child.getName(), child);
      }

      for (UUID element : group.getElement()) {
         if (model.getElements().get(element) instanceof BlockbenchModel.AnimatableElement animatable) {
            BlueprintBone child = this.readElementAsBone(blueprintBone, animatable, uuidToBone);
            if (blueprintBone.getChildren().containsKey(child.getName())) {
               new WarningDuplicateBoneName(child.getName(), child.getUuid()).log(collector);
               child.setName(child.getUuid().toString());
            }

            blueprintBone.getChildren().put(child.getName(), child);
         }
      }

      uuidToBone.put(blueprintBone.getUuid(), blueprintBone);
      return blueprintBone;
   }

   private BlueprintBone readElementAsBone(@Nullable BlueprintBone parent, BlockbenchModel.AnimatableElement element, Map<UUID, BlueprintBone> uuidToBone) {
      BlueprintBone blueprintBone = new BlueprintBone();
      blueprintBone.setName(element.getName().toLowerCase(Locale.ENGLISH));
      blueprintBone.setUuid(element.getUuid());
      blueprintBone.setGlobalPosition(vector3f(element.getOrigin()).mul(-0.0625F, 0.0625F, -0.0625F));
      Vector3f rotation = vector3f(element.getRotation()).mul((float) (-Math.PI / 180.0), (float) (Math.PI / 180.0), (float) (-Math.PI / 180.0));
      blueprintBone.setLocalRotation(rotation);
      blueprintBone.getLocalQuaternion().rotateZYX(rotation.z, rotation.y, rotation.x);
      if (parent != null) {
         blueprintBone.setLocalPosition(blueprintBone.getGlobalPosition().sub(parent.getGlobalPosition(), new Vector3f()));
         Quaternionf boneQuaternion = new Quaternionf().rotationZYX(rotation.z, rotation.y, rotation.x);
         Quaternionf parentQuaternion = parent.getGlobalQuaternion();
         parentQuaternion.mul(boneQuaternion, boneQuaternion);
         Vector3f global = TMath.getEulerAnglesZYX(boneQuaternion, new Vector3f());
         blueprintBone.setGlobalRotation(new Vector3f(global.x, global.y, global.z));
         blueprintBone.setGlobalQuaternion(boneQuaternion);
         Vector3f rotatedLocal = blueprintBone.getLocalPosition().rotate(parentQuaternion, new Vector3f());
         blueprintBone.setRotatedGlobalPosition(rotatedLocal.add(parent.getRotatedGlobalPosition()));
      } else {
         blueprintBone.setLocalPosition(new Vector3f(blueprintBone.getGlobalPosition()));
         blueprintBone.setGlobalRotation(rotation);
         blueprintBone.setGlobalQuaternion(new Quaternionf(blueprintBone.getLocalQuaternion()));
         blueprintBone.setRotatedGlobalPosition(new Vector3f(blueprintBone.getGlobalPosition()));
      }

      blueprintBone.setParent(parent);
      uuidToBone.put(blueprintBone.getUuid(), blueprintBone);
      return blueprintBone;
   }

   private void populateAssets(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets, ErrorCollector collector) {
      this.populateTexture(bbmodel, blueprint, assets, collector);
      this.populateModel(bbmodel, blueprint, assets, collector);
   }

   private void populateTexture(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets, ErrorCollector collector) {
      Map<Integer, BlockbenchModel.Texture> textures = bbmodel.getTextures();

      for (Entry<Integer, BlockbenchModel.Texture> entry : textures.entrySet()) {
         Integer id = entry.getKey();
         BlockbenchModel.Texture bbTexture = entry.getValue();
         BlueprintTexture.MCMeta meta;
         if (bbTexture.getRaw_mcmeta() == null) {
            meta = new BlueprintTexture.MCMeta();
            meta.setFrametime(bbTexture.getFrame_time());
            meta.setInterpolate(MiscUtils.orDef(false, bbTexture.getFrame_interpolate()) ? true : null);
            if (bbTexture.getFrame_order() != null && !bbTexture.getFrame_order().isBlank()) {
               for (String frame : bbTexture.getFrame_order().split(" ")) {
                  meta.addFrame(TMath.tryParse(frame, 0));
               }
            }
         } else {
            meta = this.gson.fromJson(bbTexture.getRaw_mcmeta(), BlueprintTexture.MCMeta.class);
            meta.setMustGenerate(true);
         }

         BlueprintTexture texture = this.constructBlueprintTexture(id, bbmodel, bbTexture, meta, blueprint, collector);
         assets.getTextures().add(texture);
      }
   }

   @NotNull
   private BlueprintTexture constructBlueprintTexture(
      Integer id, BlockbenchModel bbmodel, BlockbenchModel.Texture bbTexture, BlueprintTexture.MCMeta meta, ModelBlueprint blueprint, ErrorCollector collector
   ) {
      BlueprintTexture texture = new BlueprintTexture();
      texture.setId(id);
      texture.setFrameWidth(MiscUtils.or(bbTexture.getUv_width(), bbmodel.getResolution().getWidth()));
      texture.setFrameHeight(MiscUtils.or(bbTexture.getUv_height(), bbmodel.getResolution().getHeight()));
      String namespace = bbTexture.getNamespace().isBlank() ? this.generator.getNamespace() : bbTexture.getNamespace();
      String folder = !namespace.equals(this.generator.getNamespace()) && !bbTexture.getFolder().isBlank()
         ? bbTexture.getFolder().toLowerCase(Locale.ENGLISH)
         : this.generator.getTexturePath();
      String name = TFile.removeExtension(bbTexture.getName()).toLowerCase(Locale.ENGLISH);
      ResourceLocation location = new ResourceLocation(namespace, folder + "/" + name);
      if (!location.isValid()) {
         ResourceLocation correct = new ResourceLocation(
            this.generator.getNamespace(), this.generator.getTexturePath() + "/" + MiscUtils.generateUUIDFromString(name)
         );
         new WarnBadTexture(location, correct).log(collector);
         location = correct;
      }

      Map<String, String> cache = this.generator.getTextureNameCache();
      if (Optional.ofNullable(cache.get(location.toString())).map(string -> !string.equals(bbTexture.getSource())).orElse(false)) {
         ResourceLocation correct = new ResourceLocation(
            this.generator.getNamespace(), this.generator.getTexturePath() + "/" + MiscUtils.generateUUIDFromString(blueprint.getName() + ":" + name)
         );
         new WarnDuplicateTexture(location, correct).log(collector);
         location = correct;
      }

      this.generator.getTextureNameCache().put(location.toString(), bbTexture.getSource());
      texture.setPath(location);
      texture.setMcMeta(meta);
      texture.setSource(bbTexture.getSource());
      return texture;
   }

   private void populateModel(BlockbenchModel bbmodel, ModelBlueprint blueprint, ModelAssets assets, ErrorCollector collector) {
      outer:
      for (Entry<String, BlueprintBone> entry : blueprint.getFlatMap().entrySet()) {
         String name = entry.getKey();
         BlueprintBone bone = entry.getValue();
         BlockbenchModel.Group bbBone = bbmodel.getGroup(bone.getUuid());
         if (bbBone != null && bbBone.isExport()) {
            for (BoneBehaviorType<?> behaviorType : bone.getCachedBehaviorProvider().keySet()) {
               if (behaviorType.isIgnoreCubes()) {
                  continue outer;
               }
            }

            Set<JavaItemModel> javaModels = null;
            int scale = 1;
            if (ModelEngineAPI.getConfigCache().isUseNewRotation()) {
               JavaItemModel model = this.createItemModel(bbmodel, bone, bbBone, assets);
               if (model != null) {
                  javaModels = Set.of(model);
                  scale = model.scaleToFit();
               }
            } else {
               ProcessedBone processedBone = this.process(bbmodel, bone, bbBone, assets, collector);
               javaModels = processedBone.getModels();
               scale = processedBone.getScale();
            }

            if (javaModels != null && !javaModels.isEmpty()) {
               bone.setRenderer(true);
               bone.setScale(scale);
               ItemModelData.MultiModels multiModel = bone.getModelData().getMultiModels();

               for (int i = 0; i < javaModels.size(); i++) {
                  multiModel.addSubModel(new ItemModelData.SubModel(blueprint.getName() + ":" + (i == 0 ? bone.getName() : bone.getName() + "/" + i)));
               }

               assets.getModels().put(name, javaModels);
            }
         }
      }
   }

   private JavaItemModel createItemModel(BlockbenchModel model, BlueprintBone blueprintBone, BlockbenchModel.Group group, ModelAssets assets) {
      JavaItemModel javaItemModel = new JavaItemModel();
      javaItemModel.setName(blueprintBone.getName());
      boolean isRenderer = false;

      for (UUID elementId : group.getElement()) {
         BlockbenchModel.Element bbElement = model.getElements().get(elementId);
         if (bbElement instanceof BlockbenchModel.Cube cube) {
            JavaItemModel.JavaElement element = new JavaItemModel.JavaElement();
            float[] boneOrigin = unwrap(group.getOrigin(), 3);
            float[] cubeOrigin = unwrap(cube.getOrigin(), 3);
            float[] from = unwrap(cube.getFrom(), 3);
            float[] to = unwrap(cube.getTo(), 3);
            element.from(boneOrigin, from, cube.getInflate());
            element.to(boneOrigin, to, cube.getInflate());
            JavaItemModel.JavaElement.RotationFree free = new JavaItemModel.JavaElement.RotationFree();
            free.origin(boneOrigin, cubeOrigin);
            free.rotation(unwrap(cube.getRotation(), 3));
            element.setRotation(free);

            for (Entry<String, BlockbenchModel.Face> faceEntry : cube.getFaces().entrySet()) {
               String dir = faceEntry.getKey();
               BlockbenchModel.Face bbFace = faceEntry.getValue();
               if (!bbFace.isEmpty()) {
                  int id = bbFace.getTexture();
                  if (assets.getTextures().size() > id) {
                     BlockbenchModel.Texture tex = model.getTextures().get(id);
                     JavaItemModel.JavaElement.Face face = new JavaItemModel.JavaElement.Face();
                     face.setRotation(bbFace.getRotation());
                     face.uv(
                        MiscUtils.or(tex.getUv_width(), model.getResolution().getWidth()),
                        MiscUtils.or(tex.getUv_height(), model.getResolution().getHeight()),
                        unwrap(bbFace.getUv(), 4)
                     );
                     face.setTexture("#" + id);
                     element.getFaces().put(dir, face);
                     Map<String, String> textures = javaItemModel.getTextures();
                     textures.computeIfAbsent(String.valueOf(id), s -> assets.getTextures().get(id).getPath().toString());
                     if (textures.size() == 1) {
                        textures.computeIfAbsent("particle", s -> "#" + id);
                     }
                  }
               }
            }

            if (!element.getFaces().isEmpty()) {
               isRenderer = true;
               javaItemModel.addElement(element);
            }

            if (cube.isTranslucent()) {
               blueprintBone.getModelData().setTranslucent(true);
            }
         }
      }

      return isRenderer ? javaItemModel : null;
   }

   private ProcessedBone process(BlockbenchModel model, BlueprintBone blueprintBone, BlockbenchModel.Group group, ModelAssets assets, ErrorCollector collector) {
      ProcessedBone bone = new ProcessedBone(blueprintBone.getName(), vector3f(group.getOrigin()), vector3f(group.getRotation()));

      for (UUID uuid : group.getElement()) {
         if (model.getElements().get(uuid) instanceof BlockbenchModel.Cube cube) {
            Map<Direction, ProcessedBone.Face> faces = new HashMap<>();

            for (Entry<String, BlockbenchModel.Face> faceEntry : cube.getFaces().entrySet()) {
               BlockbenchModel.Face face = faceEntry.getValue();
               if (!face.isEmpty()) {
                  Float[] faceUV = face.getUv();
                  ProcessedBone.UV uv = new ProcessedBone.UV(
                     faceUV[0] == null ? 0.0F : faceUV[0],
                     faceUV[1] == null ? 0.0F : faceUV[1],
                     faceUV[2] == null ? 0.0F : faceUV[2],
                     faceUV[3] == null ? 0.0F : faceUV[3],
                     face.getRotation() == null ? 0 : face.getRotation()
                  );
                  ProcessedBone.Face procFace = new ProcessedBone.Face(uv, face.getTexture());
                  faces.put(Direction.valueOf(faceEntry.getKey().toUpperCase(Locale.ENGLISH)), procFace);
               }
            }

            if (!faces.isEmpty()) {
               ProcessedBone.Cube procCube = new ProcessedBone.Cube(
                  cube.getName(),
                  vector3d(cube.getOrigin()).sub(bone.getBoneOrigin()),
                  vector3d(cube.getRotation()),
                  vector3d(cube.getFrom()).sub(bone.getBoneOrigin()),
                  vector3d(cube.getTo()).sub(bone.getBoneOrigin()),
                  faces,
                  cube.getInflate() == null ? 0.0F : cube.getInflate()
               );
               bone.getCubes().add(procCube);
               if (cube.isTranslucent()) {
                  blueprintBone.getModelData().setTranslucent(true);
               }
            }
         }
      }

      bone.splitModels(model, assets);
      return bone;
   }

   private static Vector3f vector3f(@Nullable Float[] vec) {
      return vec == null ? new Vector3f() : new Vector3f(vec[0] == null ? 0.0F : vec[0], vec[1] == null ? 0.0F : vec[1], vec[2] == null ? 0.0F : vec[2]);
   }

   private static Vector3d vector3d(@Nullable Float[] vec) {
      return vec == null
         ? new Vector3d()
         : new Vector3d(vec[0] == null ? 0.0 : vec[0].floatValue(), vec[1] == null ? 0.0 : vec[1].floatValue(), vec[2] == null ? 0.0 : vec[2].floatValue());
   }

   private static float[] unwrap(Float[] arr, int length) {
      if (arr == null) {
         return new float[length];
      }

      float[] a = new float[arr.length];

      for (int i = 0; i < arr.length; i++) {
         if (arr[i] != null) {
            a[i] = arr[i];
         }
      }

      return a;
   }

   private static boolean putVectorKeyframes(
      BlockbenchModel.Animator animator,
      String channel,
      Timeline timeline,
      KeyframeType<VectorKeyframe, Vector3f> keyframeType,
      float scaleX,
      float scaleY,
      float scaleZ
   ) {
      Map<Float, BlockbenchModel.Keyframe> frames = animator.getChannels().get(channel);
      if (frames != null && !frames.isEmpty()) {
         for (Entry<Float, BlockbenchModel.Keyframe> entry : frames.entrySet()) {
            Float time = entry.getKey();
            BlockbenchModel.Keyframe keyframe = entry.getValue();
            VectorKeyframe vectorKeyframe = timeline.getKeyframe(time, keyframeType);
            if (!keyframe.getData_points().isEmpty()) {
               KeyframeReaderRegistry reader = ModelEngineAPI.getAPI().getKeyframeReaderRegistry();
               Map<String, String> pre = keyframe.getData_points().get(0);
               vectorKeyframe.setXFactor(scaleX)
                  .setYFactor(scaleY)
                  .setZFactor(scaleZ)
                  .setX(reader.tryParse(pre.getOrDefault("x", "0")))
                  .setY(reader.tryParse(pre.getOrDefault("y", "0")))
                  .setZ(reader.tryParse(pre.getOrDefault("z", "0")));
               if (keyframe.getData_points().size() >= 2) {
                  Map<String, String> post = keyframe.getData_points().get(1);
                  vectorKeyframe.setDiscontinuous(true);
                  vectorKeyframe.setPostX(reader.tryParse(post.getOrDefault("x", "0")))
                     .setPostY(reader.tryParse(post.getOrDefault("y", "0")))
                     .setPostZ(reader.tryParse(post.getOrDefault("z", "0")));
               }
            }

            vectorKeyframe.setInterpolation(keyframe.getInterpolation());
            if (vectorKeyframe.isBezier()) {
               float[] leftTime = unwrap(keyframe.getBezier_left_time(), 3);
               float[] leftValue = unwrap(keyframe.getBezier_left_value(), 3);
               float[] rightTime = unwrap(keyframe.getBezier_right_time(), 3);
               float[] rightValue = unwrap(keyframe.getBezier_right_value(), 3);
               vectorKeyframe.setBezierLeftTime(leftTime[0], leftTime[1], leftTime[2]);
               vectorKeyframe.setBezierLeftValue(leftValue[0], leftValue[1], leftValue[2]);
               vectorKeyframe.setBezierRightTime(rightTime[0], rightTime[1], rightTime[2]);
               vectorKeyframe.setBezierRightValue(rightValue[0], rightValue[1], rightValue[2]);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private record CachedBBFile(long lastModified, long size, long hash, String json) {
   }
}

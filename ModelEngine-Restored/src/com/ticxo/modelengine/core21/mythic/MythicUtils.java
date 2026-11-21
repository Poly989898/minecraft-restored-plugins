package com.ticxo.modelengine.core21.mythic;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.core21.Java21Helper;
import com.ticxo.modelengine.core21.mythic.compatibility.MythicMountController;
import com.ticxo.modelengine.core21.mythic.compatibility.ProjectileEntity;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.auras.AuraAttachment.AttachmentTracker;
import io.lumine.mythic.core.skills.auras.attachment.MEGAttachment.MEGAttachmentTracker;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;
import io.lumine.mythic.core.skills.projectiles.ProjectileBullet.BulletTracker;
import io.lumine.mythic.core.skills.projectiles.bullet.MEGBullet.MEGBulletTracker;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Color;
import org.jetbrains.annotations.Nullable;

public class MythicUtils {
   public static String getOrNull(@Nullable PlaceholderString placeholderString) {
      return placeholderString == null ? null : placeholderString.get();
   }

   public static String getOrNull(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta) {
      return placeholderString == null ? null : placeholderString.get(meta);
   }

   public static String getOrNull(@Nullable PlaceholderString placeholderString, AbstractEntity entity) {
      return placeholderString == null ? null : placeholderString.get(entity);
   }

   public static String getOrNull(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta, AbstractEntity entity) {
      return placeholderString == null ? null : placeholderString.get(meta, entity);
   }

   public static String getOrNull(@Nullable PlaceholderString placeholderString, SkillCaster caster) {
      return placeholderString == null ? null : placeholderString.get(caster);
   }

   public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString) {
      return placeholderString == null ? null : placeholderString.get().toLowerCase(Locale.ENGLISH);
   }

   public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta) {
      return placeholderString == null ? null : placeholderString.get(meta).toLowerCase(Locale.ENGLISH);
   }

   public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, AbstractEntity entity) {
      return placeholderString == null ? null : placeholderString.get(entity).toLowerCase(Locale.ENGLISH);
   }

   public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, PlaceholderMeta meta, AbstractEntity entity) {
      return placeholderString == null ? null : placeholderString.get(meta, entity).toLowerCase(Locale.ENGLISH);
   }

   public static String getOrNullLowercase(@Nullable PlaceholderString placeholderString, SkillCaster caster) {
      return placeholderString == null ? null : placeholderString.get(caster).toLowerCase(Locale.ENGLISH);
   }

   public static Color getColor(@Nullable String colorString) {
      if (colorString == null) {
         return Color.WHITE;
      }

      if (colorString.startsWith("#")) {
         colorString = colorString.substring(1);
      }

      return Color.fromRGB(Integer.parseInt(colorString, 16));
   }

   public static ActiveModel getActiveModelOrNull(ModeledEntity entity, @Nullable String modelId) {
      return modelId == null ? null : entity.getModel(modelId.toLowerCase(Locale.ENGLISH)).orElse(null);
   }

   public static ModelBlueprint getBlueprintOrNull(@Nullable String modelid) {
      return modelid == null ? null : ModelEngineAPI.getBlueprint(modelid);
   }

   public static ProjectileEntity<?> getProjectileEntity(SkillMetadata meta) {
      return castProjectileEntity(meta).map(tracker -> Java21Helper.getMythicCompatibility().getMythicSupport().getTrackers().get(tracker)).orElse(null);
   }

   public static <T extends ProjectileBulletableTracker & IParentSkill> Optional<T> castProjectileEntity(SkillMetadata metadata) {
      return metadata.getCallingEvent() instanceof ProjectileBulletableTracker tracker ? castProjectileEntity(tracker) : Optional.empty();
   }

   public static <T extends ProjectileBulletableTracker & IParentSkill> Optional<T> castProjectileEntity(ProjectileBulletableTracker tracker) {
      return tracker instanceof IParentSkill ? Optional.of((T)tracker) : Optional.empty();
   }

   public static UUID getVFXUniqueId(SkillMetadata meta) {
      ProjectileEntity<?> base = getProjectileEntity(meta);
      return base != null ? base.getUUID() : meta.getCaster().getEntity().getUniqueId();
   }

   public static void executeOptModelId(ModeledEntity entity, String modelId, Consumer<ActiveModel> consumer) {
      if (modelId == null) {
         for (ActiveModel activeModel : entity.getModels().values()) {
            consumer.accept(activeModel);
         }
      } else {
         ActiveModel activeModel = getActiveModelOrNull(entity, modelId);
         if (activeModel != null) {
            consumer.accept(activeModel);
         }
      }
   }

   public static void executeBoneChild(boolean child, ModelBone bone, Consumer<ModelBone> boneConsumer) {
      boneConsumer.accept(bone);
      if (child) {
         LinkedList<ModelBone> queue = new LinkedList<>(bone.getChildren().values());

         while (!queue.isEmpty()) {
            ModelBone childBone = queue.pop();
            boneConsumer.accept(childBone);
            queue.addAll(childBone.getChildren().values());
         }
      }
   }

   public static MountControllerSupplier createControllerSupplier(Skill skill, SkillMetadata metadata) {
      return (entity, mount) -> new MythicMountController(entity, mount, skill, metadata);
   }

   public static List<File> getPackModelFiles() {
      List<File> files = Lists.newArrayList();

      for (Pack pack : MythicBukkit.inst().getPackManager().getPacks()) {
         File folder = pack.getPackFolder("Models");
         if (folder.exists()) {
            addFilesRecursively(folder, files);
         }

         folder = new File(pack.getPackFolder("Assets"), "modelengine");
         if (folder.exists()) {
            addFilesRecursively(folder, files);
         }
      }

      return files;
   }

   private static void addFilesRecursively(File folder, List<File> files) {
      File[] fileList = folder.listFiles();
      if (fileList != null) {
         for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".bbmodel")) {
               files.add(file);
            } else if (file.isDirectory()) {
               addFilesRecursively(file, files);
            }
         }
      }
   }

   public static Optional<ModeledEntity> findSkillableModel(SkillMetadata data, UUID target) {
      if (target != null) {
         ModeledEntity model = ModelEngineAPI.getModeledEntity(target);
         if (model != null) {
            return Optional.of(model);
         }
      }

      return getParentVFXModel(data);
   }

   public static Optional<ModeledEntity> getParentVFXModel(SkillMetadata meta) {
      Optional<Object> maybeBullet = meta.getMetadata("bullet");
      if (maybeBullet.isPresent()) {
         return getParentVFXModel((BulletTracker)maybeBullet.get());
      }

      Optional<Object> maybeAttachment = meta.getMetadata("attachment");
      return maybeAttachment.isPresent() ? getParentVFXModel((AttachmentTracker)maybeAttachment.get()) : Optional.empty();
   }

   private static Optional<ModeledEntity> getParentVFXModel(BulletTracker bulletTracker) {
      if (bulletTracker instanceof MEGBulletTracker megBulletTracker) {
         UUID entityId = megBulletTracker.getEntityId();
         ModeledEntity model = ModelEngineAPI.getModeledEntity(entityId);
         return Optional.ofNullable(model);
      } else {
         return Optional.empty();
      }
   }

   private static Optional<ModeledEntity> getParentVFXModel(AttachmentTracker bulletTracker) {
      if (bulletTracker instanceof MEGAttachmentTracker megBulletTracker) {
         UUID entityId = megBulletTracker.getEntityId();
         ModeledEntity model = ModelEngineAPI.getModeledEntity(entityId);
         return Optional.ofNullable(model);
      } else {
         return Optional.empty();
      }
   }
}

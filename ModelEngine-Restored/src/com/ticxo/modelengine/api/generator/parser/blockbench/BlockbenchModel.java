package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class BlockbenchModel {
   protected BlockbenchModel.Resolution resolution;
   protected Map<UUID, BlockbenchModel.Element> elements;
   protected Map<UUID, BlockbenchModel.Group> outliner;
   protected Map<Integer, BlockbenchModel.Texture> textures;
   protected Map<String, BlockbenchModel.Animation> animations;
   protected final transient Map<String, BlockbenchModel.Group> flatOutlinerByName = new HashMap<>();
   protected final transient Map<UUID, BlockbenchModel.Group> flatOutlinerByUUID = new HashMap<>();
   protected String animation_variable_placeholders;

   public BlockbenchModel preprocess() {
      LinkedList<BlockbenchModel.Group> queue = new LinkedList<>(this.outliner.values());

      while (!queue.isEmpty()) {
         BlockbenchModel.Group group = queue.poll();
         this.flatOutlinerByName.put(group.name, group);
         this.flatOutlinerByUUID.put(group.uuid, group);
         queue.addAll(group.getChildGroup().values());
      }

      return this;
   }

   public BlockbenchModel.Group getGroup(@NotNull String group) {
      return this.flatOutlinerByName.get(group);
   }

   public BlockbenchModel.Group getGroup(@NotNull UUID group) {
      return this.flatOutlinerByUUID.get(group);
   }

   public BlockbenchModel.Group removeGroup(@NotNull String group) {
      return this.getGroup(group);
   }

   public BlockbenchModel.Group removeGroup(@NotNull UUID group) {
      return this.getGroup(group);
   }

   public BlockbenchModel.Group removeGroup(@Nullable BlockbenchModel.Group group) {
      if (group == null) {
         return null;
      }

      this.flatOutlinerByName.remove(group.name);
      this.flatOutlinerByUUID.remove(group.uuid);
      if (group.parentGroup != null) {
         this.flatOutlinerByUUID.get(group.parentGroup).childGroup.remove(group.uuid);
      } else {
         this.outliner.remove(group.uuid);
      }

      return group;
   }

   @Generated
   public BlockbenchModel(
      BlockbenchModel.Resolution resolution,
      Map<UUID, BlockbenchModel.Element> elements,
      Map<UUID, BlockbenchModel.Group> outliner,
      Map<Integer, BlockbenchModel.Texture> textures,
      Map<String, BlockbenchModel.Animation> animations,
      String animation_variable_placeholders
   ) {
      this.resolution = resolution;
      this.elements = elements;
      this.outliner = outliner;
      this.textures = textures;
      this.animations = animations;
      this.animation_variable_placeholders = animation_variable_placeholders;
   }

   public interface AnimatableElement {
      String getName();

      UUID getUuid();

      Float[] getOrigin();

      Float[] getPosition();

      @Nullable
      Float[] getRotation();
   }

   @Getter
   public static class Animation {
      protected UUID uuid;
      protected String name;
      protected String loop;
      protected Boolean override;
      protected Float length;
      protected BlockbenchModel.Animator effects;
      protected Map<UUID, BlockbenchModel.Animator> animators = new HashMap<>();
      protected Map<String, BlockbenchModel.Animator> unknown_animators;
   }

   @Getter
   public static class Animator {
      protected String name;
      @Nullable
      protected UUID uuid;
      protected Boolean rotation_global;
      protected Map<String, Map<Float, BlockbenchModel.Keyframe>> channels = new HashMap<>();
   }

   @Getter
   public static class Camera extends BlockbenchModel.Element implements BlockbenchModel.AnimatableElement {
      protected Float[] position;
      protected Float[] rotation;

      @Override
      public Float[] getOrigin() {
         return this.position;
      }
   }

   @Getter
   public static class Cube extends BlockbenchModel.Element {
      protected String render_order;
      protected Float[] from;
      protected Float[] to;
      protected Float inflate = 0.0F;
      protected Float[] rotation;
      protected Float[] origin;
      protected Map<String, BlockbenchModel.Face> faces;

      public float width() {
         return Math.abs(this.to[0] - this.from[0] + this.inflate * 2.0F);
      }

      public float height() {
         return Math.abs(this.to[1] - this.from[1] + this.inflate * 2.0F);
      }

      public float depth() {
         return Math.abs(this.to[2] - this.from[2] + this.inflate * 2.0F);
      }

      public float centerX() {
         return TMath.lerp(this.from[0], this.to[0], 0.5F);
      }

      public float centerY() {
         return TMath.lerp(this.from[1], this.to[1], 0.5F);
      }

      public float centerZ() {
         return TMath.lerp(this.from[2], this.to[2], 0.5F);
      }

      public float minX() {
         return Math.min(this.from[0], this.to[0]);
      }

      public float minY() {
         return Math.min(this.from[1], this.to[1]);
      }

      public float minZ() {
         return Math.min(this.from[2], this.to[2]);
      }

      public float maxX() {
         return Math.max(this.from[0], this.to[0]);
      }

      public float maxY() {
         return Math.max(this.from[1], this.to[1]);
      }

      public float maxZ() {
         return Math.max(this.from[2], this.to[2]);
      }

      public boolean isTranslucent() {
         return "in_front".equals(this.render_order);
      }
   }

   @Getter
   public static class Element {
      protected String name;
      protected UUID uuid;
      protected String type;

      public Element() {
      }

      @Generated
      public Element(String name, UUID uuid, String type) {
         this.name = name;
         this.uuid = uuid;
         this.type = type;
      }
   }

   @Getter
   public static class Face {
      protected Float[] uv;
      protected Integer rotation;
      protected Integer texture;

      public boolean isEmpty() {
         for (int i = 0; i < this.uv.length; i++) {
            if (this.uv[i] == null) {
               this.uv[i] = 0.0F;
            }
         }

         return this.texture == null || TMath.isSimilar(this.uv[0], this.uv[2]) || TMath.isSimilar(this.uv[1], this.uv[3]);
      }
   }

   @Getter
   public static class Group {
      protected String name;
      protected Float[] origin;
      protected Float[] rotation;
      protected UUID uuid;
      protected UUID parentGroup;
      protected boolean export;
      protected final Set<UUID> element = new HashSet<>();
      protected final Map<UUID, BlockbenchModel.Group> childGroup = new HashMap<>();
   }

   @Getter
   public static class Keyframe {
      protected String channel;
      protected List<Map<String, String>> data_points = new ArrayList<>();
      protected Float time;
      protected String interpolation;
      protected Float[] bezier_left_time;
      protected Float[] bezier_left_value;
      protected Float[] bezier_right_time;
      protected Float[] bezier_right_value;
   }

   @Getter
   public static class Locator extends BlockbenchModel.Element {
      protected Float[] position;
      protected Float[] rotation;
      protected Boolean ignore_inherited_scale;
   }

   @Getter
   public static class NullObject extends BlockbenchModel.Element implements BlockbenchModel.AnimatableElement {
      protected Float[] position;
      protected String ik_target;
      protected String ik_source;
      protected Boolean lock_ik_target_rotation;

      @Override
      public Float[] getOrigin() {
         return this.position;
      }

      @Nullable
      @Override
      public Float[] getRotation() {
         return null;
      }
   }

   @Getter
   public static class Resolution {
      protected Integer width;
      protected Integer height;
   }

   @Getter
   public static class Texture {
      protected String name;
      protected String folder;
      protected String namespace;
      protected String id;
      protected Integer width;
      protected Integer height;
      protected Integer uv_width;
      protected Integer uv_height;
      protected Integer frame_time;
      protected String frame_order;
      protected Boolean frame_interpolate;
      protected UUID uuid;
      protected String raw_mcmeta;
      protected String source;
   }

   public static class Unknown extends BlockbenchModel.Element {
   }
}

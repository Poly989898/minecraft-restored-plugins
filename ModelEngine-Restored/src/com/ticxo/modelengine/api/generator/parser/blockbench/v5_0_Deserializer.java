package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ticxo.modelengine.api.utils.data.GSONUtils;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public class v5_0_Deserializer {
   private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(BlockbenchModel.Element.class, new v5_0_Deserializer.ElementDeserializer())
      .registerTypeAdapter(BlockbenchModel.Group.class, new v5_0_Deserializer.GroupDeserializer())
      .registerTypeAdapter(BlockbenchModel.Animation.class, new v5_0_Deserializer.AnimationDeserializer())
      .registerTypeAdapter(BlockbenchModel.Animator.class, new v5_0_Deserializer.AnimatorDeserializer())
      .registerTypeAdapter(BlockbenchModel.Face.class, new v5_0_Deserializer.FaceDeserializer())
      .create();

   protected static BlockbenchModel deserialize(JsonObject root) {
      BlockbenchModel.Resolution resolution = gson.fromJson(root.get("resolution"), BlockbenchModel.Resolution.class);
      HashMap<UUID, BlockbenchModel.Element> elements = new HashMap<>();
      GSONUtils.ifArray(root, "elements", e -> {
         BlockbenchModel.Element element = gson.fromJson(e, BlockbenchModel.Element.class);
         elements.put(element.uuid, element);
      });
      HashMap<UUID, BlockbenchModel.Group> groups = new HashMap<>();
      GSONUtils.ifArray(root, "groups", e -> {
         if (e.isJsonObject()) {
            BlockbenchModel.Group group = gson.fromJson(e, BlockbenchModel.Group.class);
            groups.put(group.uuid, group);
         }
      });
      HashMap<UUID, BlockbenchModel.Group> outliner = new HashMap<>();
      GSONUtils.ifArray(root, "outliner", e -> {
         if (e.isJsonObject()) {
            BlockbenchModel.Group g = nestGroups(e, groups);
            if (g != null) {
               outliner.put(g.uuid, g);
            }
         }
      });
      HashMap<Integer, BlockbenchModel.Texture> textures = new HashMap<>();
      HashMap<UUID, BlockbenchModel.Texture> textureCache = new HashMap<>();
      GSONUtils.ifArray(root, "textures", (index, e) -> {
         BlockbenchModel.Texture texture = gson.fromJson(e, BlockbenchModel.Texture.class);
         textures.put(index, texture);
         textureCache.put(texture.uuid, texture);
      });
      LinkedHashMap<String, BlockbenchModel.Animation> animations = new LinkedHashMap<>();
      GSONUtils.ifArray(root, "animations", e -> {
         BlockbenchModel.Animation animation = gson.fromJson(e, BlockbenchModel.Animation.class);
         animations.put(animation.name, animation);
      });
      GSONUtils.ifPresent(root, "mcmetas", element -> {
         JsonObject elementData = element.getAsJsonObject();

         for (Entry<String, JsonElement> entry : elementData.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            BlockbenchModel.Texture texture = textureCache.get(uuid);
            if (texture != null) {
               texture.raw_mcmeta = entry.getValue().toString();
            }
         }
      });
      String animationPlaceholder = GSONUtils.get(root, "animation_variable_placeholders", JsonElement::getAsString, "");
      return new BlockbenchModel(resolution, elements, outliner, textures, animations, animationPlaceholder).preprocess();
   }

   @Nullable
   private static BlockbenchModel.Group nestGroups(JsonElement element, Map<UUID, BlockbenchModel.Group> flatGroups) {
      JsonObject obj = element.getAsJsonObject();
      UUID uuid = GSONUtils.getAsUUID(obj.get("uuid"));
      BlockbenchModel.Group root = flatGroups.get(uuid);
      if (root == null) {
         return null;
      }

      GSONUtils.ifArray(element, "children", e -> {
         if (e.isJsonPrimitive()) {
            root.element.add(GSONUtils.getAsUUID(e));
         } else if (e.isJsonObject()) {
            BlockbenchModel.Group g = nestGroups(e, flatGroups);
            if (g != null) {
               root.getChildGroup().put(g.uuid, g);
               g.parentGroup = root.uuid;
            }
         }
      });
      return root;
   }

   static class AnimationDeserializer implements JsonDeserializer<BlockbenchModel.Animation> {
      public BlockbenchModel.Animation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         BlockbenchModel.Animation animation = new BlockbenchModel.Animation();
         animation.uuid = GSONUtils.get(jsonElement, "uuid", GSONUtils::getAsUUID);
         animation.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
         animation.loop = GSONUtils.get(jsonElement, "loop", JsonElement::getAsString);
         animation.override = GSONUtils.get(jsonElement, "override", JsonElement::getAsBoolean);
         animation.length = GSONUtils.get(jsonElement, "length", JsonElement::getAsFloat);
         GSONUtils.ifPresent(
            jsonElement,
            "animators",
            e -> {
               if (e.isJsonObject()) {
                  JsonObject animators = e.getAsJsonObject();

                  for (String key : animators.keySet()) {
                     BlockbenchModel.Animator animator = v5_0_Deserializer.gson
                        .fromJson(animators.getAsJsonObject(key), BlockbenchModel.Animator.class);
                     if (key.equals("effects")) {
                        animation.effects = animator;
                     } else {
                        try {
                           UUID uuid = UUID.fromString(key);
                           animator.uuid = uuid;
                           animation.animators.put(uuid, animator);
                        } catch (IllegalArgumentException ignored) {
                           if (animation.unknown_animators == null) {
                              animation.unknown_animators = new HashMap<>();
                           }

                           animation.unknown_animators.put(key, animator);
                        }
                     }
                  }
               }
            }
         );
         return animation;
      }
   }

   static class AnimatorDeserializer implements JsonDeserializer<BlockbenchModel.Animator> {
      public BlockbenchModel.Animator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         BlockbenchModel.Animator animator = new BlockbenchModel.Animator();
         animator.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
         animator.rotation_global = GSONUtils.get(jsonElement, "rotation_global", JsonElement::getAsBoolean);
         GSONUtils.ifArray(jsonElement, "keyframes", e -> {
            BlockbenchModel.Keyframe keyframe = v5_0_Deserializer.gson.fromJson(e, BlockbenchModel.Keyframe.class);
            switch (keyframe.channel.toUpperCase(Locale.ENGLISH)) {
               case "POSITION":
                  keyframe.getData_points().forEach(map -> this.tryFlipSign(map, "x"));
                  this.flipBezierValues(keyframe, 0);
                  break;
               case "ROTATION":
                  keyframe.getData_points().forEach(map -> {
                     this.tryFlipSign(map, "x");
                     this.tryFlipSign(map, "y");
                  });
                  this.flipBezierValues(keyframe, 0, 1);
            }

            animator.channels.computeIfAbsent(keyframe.channel, s -> new TreeMap<>()).put(keyframe.time, keyframe);
         });
         return animator;
      }

      private void tryFlipSign(Map<String, String> map, String axis) {
         String val = map.get(axis);
         if (val != null) {
            map.put(axis, this.flipSign(val));
         }
      }

      private String flipSign(String val) {
         try {
            double d = Double.parseDouble(val);
            return Double.toString(-d);
         } catch (IllegalArgumentException ignored) {
            return "-(" + val + ")";
         }
      }

      private void flipBezierValues(BlockbenchModel.Keyframe keyframe, int... axes) {
         this.flipBezierValue(keyframe.getBezier_left_value(), axes);
         this.flipBezierValue(keyframe.getBezier_right_value(), axes);
      }

      private void flipBezierValue(Float[] values, int... axes) {
         if (values != null) {
            for (int axis : axes) {
               if (axis < values.length && values[axis] != null) {
                  values[axis] = -values[axis];
               }
            }
         }
      }
   }

   static class ElementDeserializer implements JsonDeserializer<BlockbenchModel.Element> {
      public BlockbenchModel.Element deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject root = jsonElement.getAsJsonObject();
         String elementType = GSONUtils.get(root, "type", JsonElement::getAsString, "cube");
         Gson gson2 = v5_0_Deserializer.gson;

         return (BlockbenchModel.Element)gson2.fromJson(jsonElement, switch (elementType) {
            case "cube" -> BlockbenchModel.Cube.class;
            case "null_object" -> BlockbenchModel.NullObject.class;
            case "locator" -> BlockbenchModel.Locator.class;
            case "camera" -> BlockbenchModel.Camera.class;
            default -> BlockbenchModel.Unknown.class;
         });
      }
   }

   static class FaceDeserializer implements JsonDeserializer<BlockbenchModel.Face> {
      public BlockbenchModel.Face deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         BlockbenchModel.Face face = new BlockbenchModel.Face();
         face.uv = GSONUtils.get(jsonElement, "uv", e -> v5_0_Deserializer.gson.fromJson(e, Float[].class));
         face.rotation = GSONUtils.get(jsonElement, "rotation", JsonElement::getAsInt);
         face.texture = GSONUtils.get(jsonElement, "texture", JsonElement::getAsInt);
         return face;
      }
   }

   static class GroupDeserializer implements JsonDeserializer<BlockbenchModel.Group> {
      public BlockbenchModel.Group deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         BlockbenchModel.Group group = new BlockbenchModel.Group();
         group.name = GSONUtils.get(jsonElement, "name", JsonElement::getAsString);
         group.origin = GSONUtils.get(jsonElement, "origin", GSONUtils::getAsFloatArray);
         group.rotation = GSONUtils.get(jsonElement, "rotation", GSONUtils::getAsFloatArray);
         group.uuid = GSONUtils.get(jsonElement, "uuid", GSONUtils::getAsUUID);
         group.export = GSONUtils.get(jsonElement, "export", JsonElement::getAsBoolean, true);
         GSONUtils.ifArray(jsonElement, "children", e -> {
            if (e.isJsonPrimitive()) {
               group.element.add(GSONUtils.getAsUUID(e));
            } else if (e.isJsonObject()) {
               BlockbenchModel.Group childGroup = v5_0_Deserializer.gson.fromJson(e, BlockbenchModel.Group.class);
               group.childGroup.put(childGroup.uuid, childGroup);
               childGroup.parentGroup = group.uuid;
            }
         });
         return group;
      }
   }
}

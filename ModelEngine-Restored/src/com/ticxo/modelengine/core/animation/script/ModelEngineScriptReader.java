package com.ticxo.modelengine.core.animation.script;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.NameTag;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import org.bukkit.Color;

public class ModelEngineScriptReader extends TUnaryRegistry<BiConsumer<ActiveModel, Map<String, String>>> implements ScriptReader {
   private boolean shouldPrintWarning;

   public ModelEngineScriptReader() {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(this::updateConfig);
      this.register("changeparent", this::changeParent);
      this.register("partvis", this::partVisibility);
      this.register("tint", this::tint);
      this.register("enchant", this::enchant);
      this.register("tag", this::tag);
      this.register("changepart", this::changePart);
      this.register("remap", this::remap);
   }

   public void updateConfig() {
      this.shouldPrintWarning = ConfigProperty.SCRIPT_WARNING.getBoolean();
   }

   @Override
   public void read(IAnimationProperty property, String script) {
      String[] scriptSplit = script.split("\\{", 2);
      String name = scriptSplit[0].toLowerCase(Locale.ENGLISH);
      BiConsumer<ActiveModel, Map<String, String>> function = this.get(name);
      if (function == null) {
         if (this.shouldPrintWarning) {
            TLogger.warn("Unknown Model Engine script: " + script);
         }
      } else if (scriptSplit.length != 2) {
         if (this.shouldPrintWarning) {
            TLogger.warn("Invalid Model Engine script: " + script);
         }
      } else {
         ActiveModel model = property.getModel();
         ModelBlueprint blueprint = model.getBlueprint();
         HashMap<String, String> map = new HashMap<>();
         String[] parameters = scriptSplit[1].substring(0, scriptSplit[1].length() - 1).split(";");

         for (String param : parameters) {
            String[] entry = param.split("=", 2);
            map.put(entry[0].strip().toLowerCase(Locale.ENGLISH), entry.length == 2 ? this.getAnimationPlaceholder(blueprint, entry[1].strip()) : "");
         }

         function.accept(model, map);
      }
   }

   private String getAnimationPlaceholder(ModelBlueprint blueprint, String placeholder) {
      if (placeholder.startsWith("<") && placeholder.endsWith(">")) {
         String key = placeholder.substring(1, placeholder.length() - 1);
         return blueprint.getAnimationsPlaceholders().getOrDefault(key, placeholder);
      } else {
         return placeholder;
      }
   }

   private void changeParent(ActiveModel model, Map<String, String> param) {
      String parentPart = param.get("parent");
      String childPart = param.get("child");
      model.getBone(parentPart).ifPresent(parent -> model.getBone(childPart).ifPresent(child -> child.setParent(parent)));
   }

   private void partVisibility(ActiveModel model, Map<String, String> param) {
      String part = param.get("part");
      boolean visible = Boolean.parseBoolean(param.get("visible"));
      boolean exact = Boolean.parseBoolean(param.get("exact"));
      if (exact) {
         model.getBone(part).ifPresent(modelBone -> modelBone.setVisible(visible));
      } else {
         for (Entry<String, ModelBone> bone : model.getBones().entrySet()) {
            if (bone.getKey().contains(part)) {
               bone.getValue().setVisible(visible);
            }
         }
      }
   }

   private void tint(ActiveModel model, Map<String, String> param) {
      String part = param.get("part");
      String colorString = param.get("color");
      if (colorString.startsWith("#")) {
         colorString = colorString.substring(1);
      }

      Color color = Color.fromRGB(Integer.parseInt(colorString, 16));
      boolean exact = Boolean.parseBoolean(param.get("exact"));
      boolean damage = Boolean.parseBoolean(param.get("damage"));
      if (part.isBlank()) {
         if (damage) {
            model.setDamageTint(color);
         } else {
            model.setDefaultTint(color);
         }
      } else if (exact) {
         model.getBone(part).ifPresent(bone -> {
            if (damage) {
               bone.setDamageTint(color);
            } else {
               bone.setDefaultTint(color);
            }
         });
      } else {
         for (Entry<String, ModelBone> entry : model.getBones().entrySet()) {
            if (entry.getKey().contains(part)) {
               if (damage) {
                  entry.getValue().setDamageTint(color);
               } else {
                  entry.getValue().setDefaultTint(color);
               }
            }
         }
      }
   }

   private void enchant(ActiveModel model, Map<String, String> param) {
      String part = param.get("part");
      boolean enchant = Boolean.parseBoolean(param.get("enchant"));
      boolean exact = Boolean.parseBoolean(param.get("exact"));
      if (part != null && !part.isBlank()) {
         if (exact) {
            model.getBone(part).ifPresent(bone -> bone.setEnchanted(enchant));
         } else {
            for (Entry<String, ModelBone> entry : model.getBones().entrySet()) {
               if (entry.getKey().contains(part)) {
                  entry.getValue().setEnchanted(enchant);
               }
            }
         }
      } else {
         for (ModelBone value : model.getBones().values()) {
            value.setEnchanted(enchant);
         }
      }
   }

   private void tag(ActiveModel model, Map<String, String> param) {
      String part = param.get("part");
      String tag = param.get("tag");
      boolean visible = Boolean.parseBoolean(param.getOrDefault("visible", "true"));
      model.getBone(part).flatMap(modelBone -> modelBone.getBoneBehavior(BoneBehaviorTypes.NAMETAG)).ifPresent(nameTag -> {
         ((NameTag)nameTag).setString(tag);
         ((NameTag)nameTag).setVisible(visible);
      });
   }

   private void changePart(ActiveModel model, Map<String, String> param) {
      String part = param.get("part");
      String nModel = param.get("nmodel");
      String nPart = param.get("npart");
      model.getBone(part).ifPresent(bone -> {
         if (bone.isRenderer()) {
            ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(nModel);
            if (blueprint != null) {
               BlueprintBone blueprintBone = blueprint.getFlatMap().get(nPart);
               if (blueprintBone != null && blueprintBone.isRenderer()) {
                  bone.setModelScale(blueprintBone.getScale());
                  bone.setModel(blueprintBone);
               }
            }
         }
      });
   }

   private void remap(ActiveModel model, Map<String, String> param) {
      String nModel = param.get("model");
      String map = param.get("map");
      ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(nModel);
      if (blueprint != null) {
         if (map != null) {
            ModelBlueprint mapBlueprint = ModelEngineAPI.getBlueprint(map);
            if (mapBlueprint == null) {
               return;
            }

            for (String bone : mapBlueprint.getFlatMap().keySet()) {
               BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
               if (blueprintBone != null && blueprintBone.isRenderer()) {
                  model.getBone(bone).ifPresent(replaced -> {
                     if (replaced.isRenderer()) {
                        replaced.setModel(blueprintBone);
                     }
                  });
               }
            }
         } else {
            for (String bone : blueprint.getFlatMap().size() < model.getBones().size() ? blueprint.getFlatMap().keySet() : model.getBones().keySet()) {
               BlueprintBone blueprintBone = blueprint.getFlatMap().get(bone);
               if (blueprintBone != null && blueprintBone.isRenderer()) {
                  model.getBone(bone).ifPresent(replaced -> {
                     if (replaced.isRenderer()) {
                        replaced.setModelScale(blueprintBone.getScale());
                        replaced.setModel(blueprintBone);
                     }
                  });
               } else {
                  TLogger.log(blueprint.getName() + ": " + bone);
               }
            }
         }
      }
   }
}

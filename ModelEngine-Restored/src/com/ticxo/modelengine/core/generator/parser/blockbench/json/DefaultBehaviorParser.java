package com.ticxo.modelengine.core.generator.parser.blockbench.json;

import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.error.IError;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchBehaviorParser;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.type.PlayerLimb;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.joml.Vector3f;

public class DefaultBehaviorParser implements BlockbenchBehaviorParser {
   @Override
   public void processModel(ErrorCollector collector, BlockbenchModel model, ModelBlueprint blueprint) {
      this.searchBone(model, "hitbox", bone -> {
         Hitbox mainHitbox = this.readHitbox(collector, model, bone);
         if (mainHitbox == null) {
            return false;
         }

         blueprint.setMainHitbox(mainHitbox);
         return true;
      }, () -> IError.NO_HITBOX.log(collector));
      this.searchBone(model, "shadow", bone -> {
         float radius = this.readShadow(model, bone);
         if (radius < 1.0E-5F) {
            return false;
         }

         blueprint.setShadowRadius(radius);
         return true;
      }, null);
   }

   @Override
   public void processBone(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone) {
      String name = this.readOptions(model, group, bone);
      bone.setName(name);
   }

   private void searchBone(BlockbenchModel model, String name, Predicate<BlockbenchModel.Group> consumer, Runnable missing) {
      BlockbenchModel.Group group = model.getGroup(name);
      if (group != null && consumer.test(group)) {
         model.removeGroup(group);
      } else if (missing != null) {
         missing.run();
      }
   }

   private Hitbox readHitbox(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group hitbox) {
      Float[] boneOrigin = hitbox.getOrigin();

      for (UUID uuid : hitbox.getElement()) {
         BlockbenchModel.Element element = model.getElements().get(uuid);
         if (element instanceof BlockbenchModel.Cube cube) {
            Float[] cubeOrigin = cube.getOrigin();
            float eyeHeight = boneOrigin[1] <= 0.0F ? cubeOrigin[1] : boneOrigin[1];
            if (eyeHeight <= 0.0F) {
               IError.BAD_EYE_HEIGHT.log(collector);
            }

            return new Hitbox(cube.width() * 0.0625F, cube.height() * 0.0625F, cube.depth() * 0.0625F, eyeHeight * 0.0625F);
         }
      }

      return null;
   }

   private float readShadow(BlockbenchModel model, BlockbenchModel.Group shadow) {
      for (UUID uuid : shadow.getElement()) {
         BlockbenchModel.Element element = model.getElements().get(uuid);
         if (element instanceof BlockbenchModel.Cube cube) {
            return Math.max(cube.width(), cube.depth()) * 0.03125F;
         }
      }

      return -1.0F;
   }

   private String readOptions(BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone) {
      String name = bone.getName();
      Map<String, Map<String, Object>> options = bone.getBehaviors();
      if (name.startsWith("h_")) {
         name = name.substring(2);
         options.put(BoneBehaviorTypes.HEAD.getId(), new HashMap<>());
      } else if (name.startsWith("hi_")) {
         name = name.substring(3);
         options.put(BoneBehaviorTypes.HEAD.getId(), new HashMap<String, Object>() {
            {
               this.put("inherited", true);
            }
         });
      }

      if (name.equals("mount")) {
         options.put(BoneBehaviorTypes.MOUNT.getId(), new HashMap<String, Object>() {
            {
               this.put("driver", true);
            }
         });
         return name;
      }

      boolean isOBB = name.startsWith("ob_");
      if (!isOBB && !name.startsWith("b_")) {
         String[] split = name.split("_(?![^\\[]*\\])");
         if (!group.getElement().isEmpty()) {
            for (int i = 0; i < split.length; i++) {
               String tag = split[i];
               String[] paramSplit = tag.split("\\[|;|\\]");
               tag = paramSplit[0];
               final HashMap<String, String> paramMap = new HashMap<>();

               for (int p = 1; p < paramSplit.length; p++) {
                  String[] pair = paramSplit[p].split("=", 2);
                  if (pair.length == 2) {
                     paramMap.put(pair[0], pair[1]);
                  }
               }

               switch (tag) {
                  case "phead":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.HEAD);
                        }
                     });
                     break;
                  case "prarm":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.RIGHT_ARM);
                        }
                     });
                     break;
                  case "plarm":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.LEFT_ARM);
                        }
                     });
                     break;
                  case "pbody":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.BODY);
                        }
                     });
                     break;
                  case "prleg":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.RIGHT_LEG);
                        }
                     });
                     break;
                  case "plleg":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.LEFT_LEG);
                        }
                     });
                     break;
                  case "limb":
                     options.put(BoneBehaviorTypes.USER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("type", paramMap.get("type"));
                        }
                     });
                     break;
                  case "tl":
                     options.put(BoneBehaviorTypes.TAIL.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", true);
                        }
                     });
                     break;
                  case "tlf":
                     options.put(BoneBehaviorTypes.TAIL.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", false);
                        }
                     });
                     break;
                  default:
                     StringBuilder builder = new StringBuilder(tag);

                     for (int j = i + 1; j < split.length; j++) {
                        builder.append("_").append(split[j]);
                     }

                     return builder.toString();
               }
            }

            return name;
         } else {
            for (int i = 0; i < split.length; i++) {
               String tag = split[i];
               String[] paramSplit = tag.split("\\[|;|\\]");
               tag = paramSplit[0];
               final HashMap<String, String> paramMap = new HashMap<>();

               for (int p = 1; p < paramSplit.length; p++) {
                  String[] pair = paramSplit[p].split("=", 2);
                  if (pair.length == 2) {
                     paramMap.put(pair[0], pair[1]);
                  }
               }

               switch (tag) {
                  case "g":
                     options.put(BoneBehaviorTypes.GHOST.getId(), new HashMap<>());
                     break;
                  case "tag":
                     options.put(BoneBehaviorTypes.NAMETAG.getId(), new HashMap<>());
                     break;
                  case "p":
                     options.put(BoneBehaviorTypes.MOUNT.getId(), new HashMap<String, Object>() {
                        {
                           this.put("driver", false);
                        }
                     });
                     break;
                  case "ir":
                     options.put(BoneBehaviorTypes.ITEM.getId(), new HashMap<String, Object>() {
                        {
                           this.put("display", ItemDisplayTransform.THIRDPERSON_RIGHTHAND);
                        }
                     });
                     break;
                  case "il":
                     options.put(BoneBehaviorTypes.ITEM.getId(), new HashMap<String, Object>() {
                        {
                           this.put("display", ItemDisplayTransform.THIRDPERSON_LEFTHAND);
                        }
                     });
                     break;
                  case "ih":
                     options.put(BoneBehaviorTypes.ITEM.getId(), new HashMap<String, Object>() {
                        {
                           this.put("display", ItemDisplayTransform.HEAD);
                        }
                     });
                     break;
                  case "l":
                     options.put(BoneBehaviorTypes.LEASH.getId(), new HashMap<>());
                     break;
                  case "phead":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.HEAD);
                        }
                     });
                     break;
                  case "prarm":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.RIGHT_ARM);
                        }
                     });
                     break;
                  case "plarm":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.LEFT_ARM);
                        }
                     });
                     break;
                  case "pbody":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.BODY);
                        }
                     });
                     break;
                  case "prleg":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.RIGHT_LEG);
                        }
                     });
                     break;
                  case "plleg":
                     options.put(BoneBehaviorTypes.PLAYER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("limb", PlayerLimb.Limb.LEFT_LEG);
                        }
                     });
                     break;
                  case "limb":
                     options.put(BoneBehaviorTypes.USER_LIMB.getId(), new HashMap<String, Object>() {
                        {
                           this.put("type", paramMap.get("type"));
                        }
                     });
                     break;
                  case "seg":
                     options.put(BoneBehaviorTypes.SEGMENT.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", true);
                        }
                     });
                     break;
                  case "segf":
                     options.put(BoneBehaviorTypes.SEGMENT.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", false);
                        }
                     });
                     break;
                  case "tl":
                     options.put(BoneBehaviorTypes.TAIL.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", true);
                        }
                     });
                     break;
                  case "tlf":
                     options.put(BoneBehaviorTypes.TAIL.getId(), new HashMap<String, Object>() {
                        {
                           this.put("back_pivot", false);
                        }
                     });
                     break;
                  default:
                     StringBuilder builder = new StringBuilder(tag);

                     for (int j = i + 1; j < split.length; j++) {
                        builder.append("_").append(split[j]);
                     }

                     return builder.toString();
               }
            }

            return name;
         }
      } else {
         HashMap<String, Object> map = new HashMap<>();
         if (isOBB) {
            map.put("obb", true);
         }

         for (UUID uuid : group.getElement()) {
            BlockbenchModel.Element element = model.getElements().get(uuid);
            if (element instanceof BlockbenchModel.Cube cube) {
               map.put("dimension", new Hitbox(cube.width() * 0.0625, cube.height() * 0.0625, cube.depth() * 0.0625, 0.0));
               Float[] from = cube.getFrom();
               Float[] to = cube.getTo();
               map.put("origin", new Vector3f((from[0] + to[0]) * -0.03125F, (from[1] + to[1]) * 0.03125F, (from[2] + to[2]) * -0.03125F));
               Float[] rotation = cube.getRotation();
               map.put("rotation", rotation != null ? new Vector3f(-rotation[0], rotation[1], -rotation[2]).mul((float) (Math.PI / 180.0)) : new Vector3f());
               Float[] origin = cube.getOrigin();
               map.put("cube_origin", new Vector3f(origin[0] * -0.0625F, origin[1] * 0.0625F, origin[2] * -0.0625F));
               break;
            }
         }

         options.put(BoneBehaviorTypes.SUB_HITBOX.getId(), map);
         return name;
      }
   }
}

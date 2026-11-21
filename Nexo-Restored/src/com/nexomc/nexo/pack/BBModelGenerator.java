package com.nexomc.nexo.pack;
import team.unnamed.creative.metadata.Metadata;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.JsonBuilderKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.metadata.animation.AnimationMeta;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.serialize.minecraft.model.ModelSerializer;
import team.unnamed.creative.texture.Texture;

@kotlin.Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 22\u00020\u0001:\u0003012B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J \u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0002J(\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u001c\u001a\u00020\u0014H\u0002J \u0010\u001d\u001a\u00020\u00172\u0006\u0010\u001e\u001a\u00020\u00192\u0006\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u0019H\u0002J \u0010!\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0002J\u0018\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020\u0011H\u0002J\u001e\u0010&\u001a\u0004\u0018\u00010'2\n\u0010(\u001a\u00060)j\u0002`*2\u0006\u0010+\u001a\u00020,H\u0002J\u001e\u0010-\u001a\u0010\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0014\u0018\u00010.2\u0006\u0010/\u001a\u00020,H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u00063"},
   d2 = {"Lcom/nexomc/nexo/pack/BBModelGenerator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "getResourcePack", "()Lteam/unnamed/creative/ResourcePack;", "parseBBModels", "", "parseBBModel", "Lcom/nexomc/nexo/pack/BBModelGenerator$BBModelResult;", "modelKey", "Lnet/kyori/adventure/key/Key;", "bbmodelFile", "", "convertElements", "Lcom/google/gson/JsonArray;", "bbElements", "textureWidth", "", "textureHeight", "containsLegacyRotation", "", "x", "", "y", "z", "minPackFormat", "isMultipleOfStep", "value", "step", "epsilon", "normalizeUv", "uv", "convertGroups", "outliner", "elements", "parseAnimation", "Lteam/unnamed/creative/metadata/animation/AnimationMeta;", "texture", "Lcom/google/gson/JsonObject;", "Lcom/nexomc/nexo/utils/JsonObject;", "imageBytes", "", "pngDimensions", "Lkotlin/Pair;", "bytes", "BBTexture", "BBModelResult", "Companion", "com.nexomc:core"}
)
public final class BBModelGenerator {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private static final String[] DIRECTIONS;
   @NotNull
   private static final Regex WHITESPACE;
   @NotNull
   private static final Set<String> GUI_LIGHTS;

   public BBModelGenerator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
   }

   @NotNull
   public final ResourcePack getResourcePack() {
      return this.resourcePack;
   }

   public final void parseBBModels() {
      team.unnamed.creative.metadata.Metadata metadata;
      Map unknownFiles = this.resourcePack.unknownFiles();
      Map map = unknownFiles;
      Map destination = (Map)(new LinkedHashMap());

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value = 0;
         Object obj = element.getKey();
         if (StringsKt.endsWith((String)obj, ".bbmodel", false)) {
            destination.put(element.getKey(), element.getValue());
         }
      }

      map = destination;
      if (!destination.isEmpty()) {
         Map it = destination;
         int value2 = 0;
         Logs.logInfo("Converting " + it.size() + " BBModels into Models & Textures...");
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value3 = 0;
         String path = (String)entry.getKey();
         Writable writable = (Writable)entry.getValue();
         BBModelGenerator bbModelGenerator = this;

         Object obj2;
         try {
            BBModelGenerator bbModelGenerator2 = bbModelGenerator;
            int value4 = 0;
            String namespace = StringUtilsKt.substringBetween(path, "assets/", "/");
            Key key2 = Key.key(namespace, StringsKt.substringBeforeLast(StringsKt.substringAfter(StringsKt.substringAfter(path, namespace + "/", path), "/", StringsKt.substringAfter(path, namespace + "/", path)), ".", StringsKt.substringAfter(StringsKt.substringAfter(path, namespace + "/", path), "/", StringsKt.substringAfter(path, namespace + "/", path))));
            Key key = key2;
            String toUTF8String = writable.toUTF8String();
            BBModelResult bbModelResult = bbModelGenerator2.parseBBModel(key, toUTF8String);
            JsonObject model = bbModelResult.component1();
            Map textures = bbModelResult.component2();
            ModelSerializer.INSTANCE.deserializeFromJson(model, key, bbModelGenerator2.resourcePack.formats()).addTo(bbModelGenerator2.resourcePack);

            for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)textures.entrySet()) {
               Key key_11;
               BBTexture texture;
               block: {
                  int value5 = 0;
                  key_11 = (Key)entry2.getKey();
                  texture = (BBTexture)entry2.getValue();
                  AnimationMeta animation = texture.getAnimation();
                  if (animation != null) {
                     AnimationMeta it = animation;
                     int value6 = 0;
                     metadata = team.unnamed.creative.metadata.Metadata.metadata().addPart(it).build();
                     if (metadata != null) {
                        break block;
                     }
                  }

                  metadata = team.unnamed.creative.metadata.Metadata.empty();
               }

               team.unnamed.creative.metadata.Metadata meta = metadata;
               bbModelGenerator2.resourcePack.texture(Texture.texture(KeyUtils.INSTANCE.appendSuffix(key_11, ".png"), Writable.bytes(texture.getData()), meta));
            }

            if (Settings.PACK_PREFER_ITEMMODELS.toBool()) {
               bbModelGenerator2.resourcePack.item(Item.item(key, ItemModel.reference(key)));
            }

            obj2 = ResultShim.ok(bbModelGenerator2.resourcePack.removeUnknownFile(path));
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj2);
         if (throwable != null) {
            Throwable it = throwable;
            int value7 = 0;
            Logs.logWarn("Failed to convert BBModel <gold>" + path + "</gold>: " + it.getMessage(), false);
            if (Settings.DEBUG.toBool()) {
               it.printStackTrace();
            }
         }
      }

   }

   private final BBModelResult parseBBModel(Key modelKey, String bbmodelFile) {
      JsonObject bbmodel;
      JsonObject resolution;
      int value;
      block: {
         bbmodel = JsonParser.parseString(bbmodelFile).getAsJsonObject();
         resolution = bbmodel.getAsJsonObject("resolution");
         if (resolution != null) {
            Integer number = JsonBuilderKt.int_(resolution, "width");
            if (number != null) {
               value = number;
               break block;
            }
         }

         value = 16;
      }

      int textureWidth;
      block: {
         textureWidth = value;
         if (resolution != null) {
            Integer number2 = JsonBuilderKt.int_(resolution, "height");
            if (number2 != null) {
               value = number2;
               break block;
            }
         }

         value = 16;
      }

      int textureHeight = value;
      JsonObject textureMap = JsonBuilder.INSTANCE.getJsonObject();
      String str = modelKey.namespace();
      String namespace = str;
      str = modelKey.value();
      String prefix = StringsKt.substringAfterLast(str, "/", str);
      JsonArray jsonArray = JsonBuilderKt.jsonArray(bbmodel, "textures");
      Object2ObjectOpenHashMap object2ObjectOpenHashMap;
      if (jsonArray != null) {
         Iterable iterable = jsonArray;
         Object2ObjectOpenHashMap object2ObjectOpenHashMap2 = new Object2ObjectOpenHashMap();
         int value11 = 0;

         for(Object obj2 : iterable) {
            int value2 = value11++;
            JsonElement element = (JsonElement)obj2;
            int index = value2;
            int value3 = 0;
            JsonObject texture = element.getAsJsonObject();
            String str2 = JsonBuilderKt.string(texture, "source");
            Pair pair;
            if (str2 == null) {
               pair = null;
            } else {
               String source = str2;
               String textureName = StringsKt.substringAfter(source, "data:image/png;base64,", source);
               int it = 0;
               String str3 = !Intrinsics.areEqual(textureName, source) ? textureName : null;
               if (str3 == null) {
                  pair = null;
               } else {
                  byte[] imageBytes;
                  block: {
                     String base64Data = str3;
                     imageBytes = Base64.getDecoder().decode(base64Data);
                     textureName = StringsKt.removeSuffix(JsonBuilderKt.string(texture, "name", "texture_" + index), (CharSequence)".png");
                     str3 = JsonBuilderKt.string(texture, "namespace");
                     if (str3 != null) {
                        String it_10 = str3;
                        int value4 = 0;
                        str3 = !StringsKt.isBlank((CharSequence)it_10) ? it_10 : null;
                        if (str3 != null) {
                           break block;
                        }
                     }

                     str3 = namespace;
                  }

                  String textureNamespace;
                  block: {
                     textureNamespace = str3;
                     str3 = JsonBuilderKt.string(texture, "folder");
                     if (str3 != null) {
                        String it_9 = str3;
                        int value5 = 0;
                        str3 = !StringsKt.isBlank((CharSequence)it_9) ? it_9 : null;
                        if (str3 != null) {
                           char[] it_8 = new char[]{'/'};
                           str3 = StringsKt.trim(str3, it_8);
                           break block;
                        }
                     }

                     str3 = null;
                  }

                  String textureFolder = str3;
                  BBModelGenerator it_7 = this;

                  Object it_6;
                  try {
                     block: {
                        BBModelGenerator bbModelGenerator = it_7;
                        int value6 = 0;
                        if (textureFolder != null) {
                           int value7 = 0;
                           str3 = textureFolder + "/" + textureName;
                           if (str3 != null) {
                              break block;
                           }
                        }

                        str3 = prefix + "/" + textureName;
                     }

                     String path = str3;
                     it_6 = ResultShim.ok(Key.key(textureNamespace, path));
                  } catch (Throwable e) {
                     it_6 = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  Object obj;
                  if (ResultShim.exceptionOrNull(it_6) == null) {
                     obj = it_6;
                  } else {
                     int value8 = 0;
                     obj = Key.key(namespace, prefix + "/" + textureName);
                  }

                  Key textureKey = (Key)obj;
                  textureMap.addProperty(String.valueOf(index), textureKey.asString());
                  Key key = KeyUtils.INSTANCE.appendSuffix(textureKey, ".png");
                  pair = TuplesKt.to(key, new BBTexture(imageBytes, this.parseAnimation(texture, imageBytes)));
               }
            }

            if (pair != null) {
               Pair pair2 = pair;
               ((Map)object2ObjectOpenHashMap2).put(pair2.getFirst(), pair2.getSecond());
            }
         }

         object2ObjectOpenHashMap = object2ObjectOpenHashMap2;
      } else {
         object2ObjectOpenHashMap = null;
      }

      Map map = (Map)object2ObjectOpenHashMap;
      if ((Map)object2ObjectOpenHashMap == null) {
         map = MapsKt.emptyMap();
      }

      Map extractedTextures = map;
      JsonObject modelJson = JsonBuilder.INSTANCE.plus(new JsonObject(), "textures", textureMap);
      if (bbmodel.has("elements")) {
         JsonArray asJsonArray = bbmodel.getAsJsonArray("elements");
         JsonArray elements = this.convertElements(asJsonArray, textureWidth, textureHeight);
         modelJson.add("elements", elements);
      }

      if (bbmodel.has("display")) {
         modelJson.add("display", bbmodel.getAsJsonObject("display"));
      }

      String str4 = JsonBuilderKt.string(bbmodel, "gui_light");
      if (str4 == null) {
         JsonObject asJsonObject = bbmodel.getAsJsonObject("unhandled_root_fields");
         str4 = asJsonObject != null ? JsonBuilderKt.string(asJsonObject, "gui_light") : null;
         if (str4 == null) {
            Boolean flag = JsonBuilderKt.boolean_(bbmodel, "front_gui_light");
            if (flag != null) {
               boolean it = flag;
               int value9 = 0;
               str4 = it ? "front" : "side";
            } else {
               str4 = null;
            }
         }
      }

      String guiLight = str4;
      if (CollectionsKt.contains((Iterable)GUI_LIGHTS, guiLight)) {
         modelJson.addProperty("gui_light", guiLight);
      }

      JsonArray jsonArray2 = JsonBuilderKt.jsonArray(bbmodel, "outliner");
      if (jsonArray2 != null) {
         JsonArray outliner = jsonArray2;
         int value10 = 0;
         JsonArray jsonArray3 = bbmodel.getAsJsonArray("elements");
         if (jsonArray3 == null) {
            jsonArray3 = new JsonArray();
         }

         JsonArray groups = this.convertGroups(outliner, jsonArray3);
         if (groups.size() > 0) {
            modelJson.add("groups", groups);
         }
      }

      return new BBModelResult(modelJson, extractedTextures);
   }

   private final JsonArray convertElements(JsonArray bbElements, int textureWidth, int textureHeight) {
      team.unnamed.creative.metadata.pack.FormatVersion formatVersion;
      com.nexomc.nexo.utils.JsonBuilder jsonBuilder;
      JsonArray mcElements = new JsonArray();
      Iterator iterator = bbElements.iterator();
      Iterator iterator2 = iterator;

      while(iterator2.hasNext()) {
         JsonElement element = (JsonElement)iterator2.next();
         JsonObject it = element.getAsJsonObject();
         int value = 0;
         JsonObject jsonObject = !Intrinsics.areEqual(JsonBuilderKt.string(it, "type"), "locator") ? it : null;
         if (jsonObject != null) {
            JsonObject bbElement;
            Integer number;
            JsonObject jsonObject2;
            String str;
            block: {
               bbElement = jsonObject;
               jsonBuilder = JsonBuilder.INSTANCE;
               jsonObject2 = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(new JsonObject(), "from", JsonBuilderKt.jsonArray(bbElement, "from", JsonBuilderKt.NexoJsonArray(3, (Number)0))), "to", JsonBuilderKt.jsonArray(bbElement, "to", JsonBuilderKt.NexoJsonArray(3, (Number)16))), "shade", bbElement.get("shade"));
               str = "light_emission";
               number = JsonBuilderKt.int_(bbElement, "light_emission");
               if (number != null) {
                  Integer rot = number;
                  int it_5 = ((Number)rot).intValue();
                  String str2 = "light_emission";
                  JsonObject jsonObject3 = jsonObject2;
                  JsonBuilder jsonBuilder2 = jsonBuilder;
                  int value9 = 0;
                  boolean flag = it_5 != 0;
                  jsonBuilder = jsonBuilder2;
                  jsonObject2 = jsonObject3;
                  str = str2;
                  number = flag ? rot : null;
                  if ((flag ? rot : null) != null) {
                     number = RangesKt.coerceIn((int)(number), 0, 15);
                     break block;
                  }
               }

               number = null;
            }

            JsonObject mcElement = jsonBuilder.plus(jsonObject2, str, number);
            JsonArray asJsonArray = bbElement.getAsJsonArray("rotation");
            if (asJsonArray != null) {
               JsonArray it_4 = asJsonArray;
               int value2 = 0;
               asJsonArray = JsonBuilderKt.jsonArray(bbElement, "origin");
               if (asJsonArray == null) {
                  JsonArray mcFaces = JsonBuilder.INSTANCE.getJsonArray();
                  int value3 = 0;
                  mcFaces.add((Number)8);
                  mcFaces.add((Number)8);
                  mcFaces.add((Number)8);
                  asJsonArray = mcFaces;
               }

               JsonArray origin = asJsonArray;
               boolean rescale = JsonBuilderKt.boolean_(bbElement, "rescale", false);
               Iterable iterable = it_4;
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object item : iterable) {
                  JsonElement it_3 = (JsonElement)item;
                  int value4 = 0;
                  destination.add(it_3.getAsFloat());
               }

               float x;
               float y;
               float z;
               block: {
                  List list = (List)destination;
                  x = ((Number)list.get(0)).floatValue();
                  y = ((Number)list.get(1)).floatValue();
                  z = ((Number)list.get(2)).floatValue();
                  PackMeta packMeta = this.resourcePack.packMeta();
                  if (packMeta != null) {
                     PackFormat formats = packMeta.formats();
                     if (formats != null) {
                        formatVersion = formats.min();
                        if (formatVersion != null) {
                           break block;
                        }
                     }
                  }

                  formatVersion = NMSHandlers.handler().resourcepackFormat();
               }

               int minFormat = formatVersion.minor();
               JsonObject jsonObject4;
               if (this.containsLegacyRotation(x, y, z, minFormat)) {
                  Pair pair = x != 0.0F ? TuplesKt.to("x", x) : (y != 0.0F ? TuplesKt.to("y", y) : (z != 0.0F ? TuplesKt.to("z", z) : TuplesKt.to((Object)null, 0.0F)));
                  String axis = (String)pair.component1();
                  float angle = ((Number)pair.component2()).floatValue();
                  jsonObject4 = axis != null && angle != 0.0F ? JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "origin", origin), "axis", axis), "angle", angle), "rescale", rescale) : null;
               } else {
                  jsonObject4 = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "origin", origin), "rescale", rescale), "x", x), "y", y), "z", z);
               }

               JsonObject rot = jsonObject4;
               JsonBuilder jsonBuilder3 = JsonBuilder.INSTANCE;
               if (rot != null) {
                  jsonBuilder3.plus(mcElement, "rotation", rot);
               }
            }

            JsonObject asJsonObject = bbElement.getAsJsonObject("faces");
            if (asJsonObject != null) {
               JsonObject rot = asJsonObject;
               JsonObject faces = rot;
               int value5 = 0;
               JsonObject mcFaces = JsonBuilder.INSTANCE.getJsonObject();
               Boolean flag2 = JsonBuilderKt.boolean_(bbElement, "box_uv");
               boolean useBoxUv = flag2 != null ? flag2 : false;
               String[] strs = DIRECTIONS;
               int value6 = 0;

               for(int i = strs.length; value6 < i; ++value6) {
                  String direction = strs[value6];
                  JsonObject jsonObject5 = faces.getAsJsonObject(direction);
                  if (jsonObject5 != null) {
                     JsonObject face = jsonObject5;
                     Integer number2 = JsonBuilderKt.int_(face, "texture");
                     if (number2 != null) {
                        int texture = number2;
                        JsonObject jsonObject6 = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "texture", "#" + texture);
                        JsonArray jsonArray = JsonBuilderKt.jsonArray(face, "uv");
                        if (jsonArray != null) {
                           JsonArray uv = jsonArray;
                           int value7 = 0;
                           JsonArray mcUv = useBoxUv ? uv : this.normalizeUv(uv, textureWidth, textureHeight);
                           jsonObject6.add("uv", mcUv);
                        }

                        JsonBuilder jsonBuilder4 = JsonBuilder.INSTANCE;
                        JsonBuilder jsonBuilder5 = JsonBuilder.INSTANCE;
                        JsonBuilder jsonBuilder6 = JsonBuilder.INSTANCE;
                        JsonObject jsonObject7 = jsonObject6;
                        String str3 = "rotation";
                        Integer number3 = JsonBuilderKt.int_(face, "rotation");
                        if (number3 != null) {
                           Integer uv = number3;
                           int uv_2 = ((Number)uv).intValue();
                           String str4 = "rotation";
                           JsonBuilder jsonBuilder7 = jsonBuilder6;
                           JsonBuilder jsonBuilder8 = jsonBuilder5;
                           JsonBuilder jsonBuilder9 = jsonBuilder4;
                           int value8 = 0;
                           boolean flag3 = uv_2 != 0;
                           jsonBuilder4 = jsonBuilder9;
                           jsonBuilder5 = jsonBuilder8;
                           jsonBuilder6 = jsonBuilder7;
                           jsonObject7 = jsonObject6;
                           str3 = str4;
                           number3 = flag3 ? uv_2 : null;
                        } else {
                           number3 = null;
                        }

                        jsonBuilder4.plus(jsonBuilder5.plus(jsonBuilder6.plus(jsonObject7, str3, number3), "tintindex", face.get("tintindex")), "cullface", JsonBuilderKt.string(face, "cullface"));
                        mcFaces.add(direction, jsonObject6);
                     }
                  }
               }

               mcElement.add("faces", mcFaces);
            }

            mcElements.add(mcElement);
         }
      }

      return mcElements;
   }

   private final boolean containsLegacyRotation(float x, float y, float z, int minPackFormat) {
      ClosedFloatingPointRange range = RangesKt.rangeTo(-45.0F, 45.0F);
      float step = 22.5F;
      float epsilon = 1.0E-4F;
      if (Math.abs(x) < epsilon && Math.abs(y) < epsilon && Math.abs(z) < epsilon) {
         return minPackFormat < 75;
      } else if (range.contains((Comparable)x) && range.contains((Comparable)y) && range.contains((Comparable)z)) {
         int nonZeroCount = 0;
         if (Math.abs(x) > epsilon) {
            ++nonZeroCount;
         }

         if (Math.abs(y) > epsilon) {
            ++nonZeroCount;
         }

         if (Math.abs(z) > epsilon) {
            ++nonZeroCount;
         }

         if (nonZeroCount > 1) {
            return false;
         } else {
            boolean isSemiLegacy = minPackFormat >= 63 && minPackFormat < 75;
            if (!this.isMultipleOfStep(x, step, epsilon)) {
               return isSemiLegacy;
            } else if (!this.isMultipleOfStep(y, step, epsilon)) {
               return isSemiLegacy;
            } else if (!this.isMultipleOfStep(z, step, epsilon)) {
               return isSemiLegacy;
            } else {
               return minPackFormat < 75;
            }
         }
      } else {
         return false;
      }
   }

   private final boolean isMultipleOfStep(float value, float step, float epsilon) {
      float quotient = value / step;
      return Math.abs(quotient - (float)MathKt.roundToInt(quotient)) < epsilon;
   }

   private final JsonArray normalizeUv(JsonArray uv, int textureWidth, int textureHeight) {
        JsonArray normalized = new JsonArray();
        normalized.add(Float.valueOf(uv.get(0).getAsFloat() * 16.0f / (float)textureWidth));
        normalized.add(Float.valueOf(uv.get(1).getAsFloat() * 16.0f / (float)textureHeight));
        normalized.add(Float.valueOf(uv.get(2).getAsFloat() * 16.0f / (float)textureWidth));
        normalized.add(Float.valueOf(uv.get(3).getAsFloat() * 16.0f / (float)textureHeight));
        return normalized;
    }

   private final JsonArray convertGroups(JsonArray outliner, JsonArray elements) {
      Iterable iterable = elements;
      Object2ObjectOpenHashMap map = new Object2ObjectOpenHashMap();
      int value7 = 0;

      for(Object obj : iterable) {
         int value = value7++;
         JsonElement element = (JsonElement)obj;
         int index = value;
         int value8 = 0;
         JsonObject asJsonObject = element.getAsJsonObject();
         String str = JsonBuilderKt.string(asJsonObject, "uuid");
         Pair pair;
         if (str != null) {
            String it = str;
            int value2 = 0;
            pair = TuplesKt.to(it, index);
         } else {
            pair = null;
         }

         if (pair != null) {
            Pair pair2 = pair;
            ((Map)map).put(pair2.getFirst(), pair2.getSecond());
         }
      }

      Object2ObjectOpenHashMap elementUuids = map;
      JsonBuilder jsonBuilder = JsonBuilder.INSTANCE;
      JsonArray jsonArray = new JsonArray();
      iterable = outliner;
      JsonArray jsonArray2 = jsonArray;
      JsonBuilder jsonBuilder2 = jsonBuilder;
      Collection destination = (Collection)(new ArrayList());

      for(Object obj2 : iterable) {
         int value3 = 0;
         JsonElement entry = (JsonElement)obj2;
         int value4 = 0;
         if (entry.isJsonPrimitive()) {
            elementUuids.get(entry.getAsString());
         } else if (entry.isJsonObject()) {
            JsonObject group = entry.getAsJsonObject();
            int value5 = 0;
            jsonBuilder = JsonBuilder.INSTANCE;
            JsonBuilder jsonBuilder3 = JsonBuilder.INSTANCE;
            JsonBuilder jsonBuilder4 = JsonBuilder.INSTANCE;
            JsonBuilder jsonBuilder5 = JsonBuilder.INSTANCE;
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = jsonBuilder5.plus(jsonObject, "name", JsonBuilderKt.string(group, "name", "group"));
            JsonArray asJsonArray = group.getAsJsonArray("origin");
            if (asJsonArray == null) {
               asJsonArray = JsonBuilderKt.NexoJsonArray(3, (Number)0);
            }

            jsonBuilder.plus(jsonBuilder3.plus(jsonBuilder4.plus(jsonObject2, "origin", asJsonArray), "color", JsonBuilderKt.int_(group, "color", 0)), "children", this.convertGroups(JsonBuilderKt.jsonArray(group, "children", new JsonArray()), elements));
         }

         Object obj3 = Unit.INSTANCE;
         int value6 = 0;
         destination.add(obj3);
      }

      List list = (List)destination;
      return jsonBuilder2.plus(jsonArray2, list);
   }

   private final AnimationMeta parseAnimation(JsonObject texture, byte[] imageBytes) {
      java.util.List list;
      java.lang.Integer number;
      Pair pair = this.pngDimensions(imageBytes);
      if (pair == null) {
         return null;
      } else {
         Pair pair2 = pair;
         int width = ((Number)pair2.component1()).intValue();
         int height = ((Number)pair2.component2()).intValue();
         if (width > 0 && height > width && height % width == 0) {
            int frameCount;
            block: {
               frameCount = height / width;
               Integer number2 = JsonBuilderKt.int_(texture, "frame_time");
               if (number2 != null) {
                  Integer number3 = number2;
                  int it = ((Number)number3).intValue();
                  int value6 = 0;
                  number2 = it > 0 ? number3 : null;
                  if ((it > 0 ? number3 : null) != null) {
                     number = number2;
                     break block;
                  }
               }

               number = 1;
            }

            int frameTime;
            boolean interpolate;
            block: {
               frameTime = number;
               Boolean flag = JsonBuilderKt.boolean_(texture, "frame_interpolate");
               interpolate = flag != null ? flag : false;
               String orderType = JsonBuilderKt.string(texture, "frame_order_type");
               String str = JsonBuilderKt.string(texture, "frame_order");
               String frameOrder = str != null ? StringsKt.trim((CharSequence)str).toString() : null;
               if (Intrinsics.areEqual(orderType, "custom")) {
                  CharSequence charSequence = (CharSequence)frameOrder;
                  if (charSequence != null && !StringsKt.isBlank(charSequence)) {
                     charSequence = (CharSequence)frameOrder;
                     Regex regex = WHITESPACE;
                     byte b = 0;
                     Iterable iterable = (Iterable)regex.split(charSequence, b);
                     Collection destination = (Collection)(new ArrayList());

                     for(Object element : iterable) {
                        int value = 0;
                        String it = (String)element;
                        int value2 = 0;
                        Integer number4 = StringsKt.toIntOrNull(it);
                        if (number4 != null) {
                           Integer number5 = number4;
                           int i = ((Number)number5).intValue();
                           int value3 = 0;
                           number4 = (0 <= i ? i < frameCount : false) ? number5 : null;
                        } else {
                           number4 = null;
                        }

                        if (number4 != null) {
                           Object obj2 = number4;
                           int value4 = 0;
                           destination.add(obj2);
                        }
                     }

                     list = (List)destination;
                     break block;
                  }
               }

               list = Intrinsics.areEqual(orderType, "backwards") ? CollectionsKt.toList((Iterable)RangesKt.downTo(frameCount - 1, 0)) : (Intrinsics.areEqual(orderType, "back_and_forth") ? CollectionsKt.plus((Iterable)RangesKt.until(0, frameCount), (Iterable)RangesKt.downTo(frameCount - 2, 1)) : CollectionsKt.toList((Iterable)RangesKt.until(0, frameCount)));
            }

            Collection collection = (Collection)list;
            Object obj;
            if (collection.isEmpty()) {
               int value5 = 0;
               obj = CollectionsKt.toList((Iterable)RangesKt.until(0, frameCount));
            } else {
               obj = collection;
            }

            List frames = (List)obj;
            AnimationMeta.Builder builder = AnimationMeta.animation().frameTime(frameTime).interpolate(interpolate);
            int[] ints = CollectionsKt.toIntArray((Collection)frames);
            return builder.frames(Arrays.copyOf(ints, ints.length)).build();
         } else {
            return null;
         }
      }
   }

   private final Pair<Integer, Integer> pngDimensions(byte[] bytes) {
      return bytes.length < 24 ? null : TuplesKt.to(pngDimensions$int32(bytes, 16), pngDimensions$int32(bytes, 20));
   }

   private static final int pngDimensions$int32(byte[] bytes, int offset) {
      return (bytes[offset] & 255) << 24 | (bytes[offset + 1] & 255) << 16 | (bytes[offset + 2] & 255) << 8 | bytes[offset + 3] & 255;
   }

   static {
      String[] strs = new String[]{"north", "east", "south", "west", "up", "down"};
      DIRECTIONS = strs;
      WHITESPACE = new Regex("\\s+");
      strs = new String[]{"front", "side"};
      GUI_LIGHTS = SetsKt.setOf(strs);
   }

   @kotlin.Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0014\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004J\n\u0010\u0014\u001a\u00020\u0015HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016"},
      d2 = {"Lcom/nexomc/nexo/pack/BBModelGenerator$BBTexture;", "", "data", "", "animation", "Lteam/unnamed/creative/metadata/animation/AnimationMeta;", "<init>", "([BLteam/unnamed/creative/metadata/animation/AnimationMeta;)V", "getData", "()[B", "getAnimation", "()Lteam/unnamed/creative/metadata/animation/AnimationMeta;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class BBTexture {
      @NotNull
      private final byte[] data;
      @Nullable
      private final AnimationMeta animation;

      public BBTexture(@NotNull byte[] data, @Nullable AnimationMeta animation) {
         super();
         this.data = data;
         this.animation = animation;
      }

      @NotNull
      public final byte[] getData() {
         return this.data;
      }

      @Nullable
      public final AnimationMeta getAnimation() {
         return this.animation;
      }

      @NotNull
      public final byte[] component1() {
         return this.data;
      }

      @Nullable
      public final AnimationMeta component2() {
         return this.animation;
      }

      @NotNull
      public final BBTexture copy(@NotNull byte[] data, @Nullable AnimationMeta animation) {
         return new BBTexture(data, animation);
      }

      @NotNull
      public String toString() {
         String str = Arrays.toString(this.data);
         return "BBTexture(data=" + str + ", animation=" + this.animation + ")";
      }

      public int hashCode() {
         int result = Arrays.hashCode(this.data);
         result = result * 31 + (this.animation == null ? 0 : this.animation.hashCode());
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof BBTexture)) {
            return false;
         } else {
            BBTexture bbTexture = (BBTexture)other;
            if (!Intrinsics.areEqual(this.data, bbTexture.data)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.animation, bbTexture.animation);
            }
         }
      }
   }

   @kotlin.Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\n\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006¢\u0006\u0004\b\t\u0010\nJ\r\u0010\u000f\u001a\u00060\u0003j\u0002`\u0004HÆ\u0003J\u0015\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006HÆ\u0003J-\u0010\u0011\u001a\u00020\u00002\f\b\u0002\u0010\u0002\u001a\u00060\u0003j\u0002`\u00042\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006HÆ\u0001J\u0014\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0015\u001a\u00020\u0016HÖ\u0081\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004R\u0015\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001d\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/pack/BBModelGenerator$BBModelResult;", "", "modelJson", "Lcom/google/gson/JsonObject;", "Lcom/nexomc/nexo/utils/JsonObject;", "textures", "", "Lnet/kyori/adventure/key/Key;", "Lcom/nexomc/nexo/pack/BBModelGenerator$BBTexture;", "<init>", "(Lcom/google/gson/JsonObject;Ljava/util/Map;)V", "getModelJson", "()Lcom/google/gson/JsonObject;", "getTextures", "()Ljava/util/Map;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class BBModelResult {
      @NotNull
      private final JsonObject modelJson;
      @NotNull
      private final Map<Key, BBTexture> textures;

      public BBModelResult(@NotNull JsonObject modelJson, @NotNull Map<Key, BBTexture> textures) {
         super();
         this.modelJson = modelJson;
         this.textures = textures;
      }

      @NotNull
      public final JsonObject getModelJson() {
         return this.modelJson;
      }

      @NotNull
      public final Map<Key, BBTexture> getTextures() {
         return this.textures;
      }

      @NotNull
      public final JsonObject component1() {
         return this.modelJson;
      }

      @NotNull
      public final Map<Key, BBTexture> component2() {
         return this.textures;
      }

      @NotNull
      public final BBModelResult copy(@NotNull JsonObject modelJson, @NotNull Map<Key, BBTexture> textures) {
         return new BBModelResult(modelJson, textures);
      }

      @NotNull
      public String toString() {
         return "BBModelResult(modelJson=" + this.modelJson + ", textures=" + this.textures + ")";
      }

      public int hashCode() {
         int result = this.modelJson.hashCode();
         result = result * 31 + this.textures.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof BBModelResult)) {
            return false;
         } else {
            BBModelResult bbModelResult = (BBModelResult)other;
            if (!Intrinsics.areEqual(this.modelJson, bbModelResult.modelJson)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.textures, bbModelResult.textures);
            }
         }
      }
   }

   @kotlin.Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
      d2 = {"Lcom/nexomc/nexo/pack/BBModelGenerator$Companion;", "", "<init>", "()V", "DIRECTIONS", "", "", "[Ljava/lang/String;", "WHITESPACE", "Lkotlin/text/Regex;", "GUI_LIGHTS", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}

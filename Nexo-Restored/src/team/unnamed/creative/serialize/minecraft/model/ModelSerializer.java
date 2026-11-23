package team.unnamed.creative.serialize.minecraft.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.base.Axis3D;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;
import team.unnamed.creative.texture.TextureUV;

@Internal
public final class ModelSerializer implements JsonResourceSerializer<Model>, JsonResourceDeserializer<Model> {
   private static final float MINECRAFT_UV_UNIT = 16.0F;
   private static final Logger LOGGER = Logger.getLogger(ModelSerializer.class.getName());
   public static final ModelSerializer INSTANCE = new ModelSerializer();
   public static final ResourceCategoryImpl<Model> CATEGORY;

   public void serializeToJson(Model model, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();
      Key parent = model.parent();
      if (parent != null) {
         writer.name("parent").value(KeySerializer.toString(parent));
      }

      Map<ItemTransform.Type, ItemTransform> display = model.display();
      if (!display.isEmpty()) {
         writer.name("display").beginObject();

         for(Map.Entry<ItemTransform.Type, ItemTransform> entry : display.entrySet()) {
            writer.name(((ItemTransform.Type)entry.getKey()).name().toLowerCase(Locale.ROOT));
            writeItemTransform(writer, (ItemTransform)entry.getValue());
         }

         writer.endObject();
      }

      List<Element> elements = model.elements();
      if (!elements.isEmpty()) {
         writer.name("elements").beginArray();
         int minPackFormat = packFormat.min().major();
         boolean writeLegacy = minPackFormat < 75;
         if (writeLegacy) {
            for(Element element : elements) {
               ElementRotation rotation = element.rotation();
               if (rotation != null) {
                  writeLegacy = rotation.containsLegacyRotation(writeLegacy, minPackFormat);
               }
            }
         }

         for(Element element : elements) {
            writeElement(writer, element, writeLegacy);
         }

         writer.endArray();
      }

      boolean ambientOcclusion = model.ambientOcclusion();
      if (!ambientOcclusion) {
         writer.name("ambientocclusion").value(ambientOcclusion);
      }

      writeTextures(writer, model.textures());
      Model.GuiLight guiLight = model.guiLight();
      if (guiLight != null) {
         writer.name("gui_light").value(guiLight.name().toLowerCase(Locale.ROOT));
      }

      List<ItemOverride> overrides = model.overrides();
      if (!overrides.isEmpty()) {
         writer.name("overrides").beginArray();

         for(ItemOverride override : overrides) {
            writeItemOverride(writer, override);
         }

         writer.endArray();
      }

      writer.endObject();
   }

   public Model deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) {
      JsonObject objectNode = node.getAsJsonObject();
      Key parent = null;
      if (objectNode.has("parent")) {
         parent = Key.key(objectNode.get("parent").getAsString());
      }

      Map<ItemTransform.Type, ItemTransform> display = new LinkedHashMap();
      if (objectNode.has("display")) {
         JsonObject displayNode = objectNode.getAsJsonObject("display");

         for(Map.Entry<String, JsonElement> entry : displayNode.entrySet()) {
            ItemTransform.Type type = ItemTransform.Type.valueOf(((String)entry.getKey()).toUpperCase(Locale.ROOT));
            display.put(type, readItemTransform((JsonElement)entry.getValue()));
         }
      }

      List<Element> elements = new ArrayList();
      if (objectNode.has("elements")) {
         for(JsonElement elementNode : objectNode.getAsJsonArray("elements")) {
            elements.add(readElement(elementNode, packFormat, key));
         }
      }

      ModelTextures texture = ModelTextures.builder().build();
      if (objectNode.has("textures")) {
         texture = readTextures(objectNode.get("textures"));
      }

      Model.GuiLight guiLight = null;
      if (objectNode.has("gui_light")) {
         guiLight = Model.GuiLight.valueOf(objectNode.get("gui_light").getAsString().toUpperCase(Locale.ROOT));
      }

      List<ItemOverride> overrides = new ArrayList();
      if (objectNode.has("overrides")) {
         for(JsonElement overrideNode : objectNode.getAsJsonArray("overrides")) {
            overrides.add(readItemOverride(overrideNode));
         }
      }

      return Model.model().key(key).parent(parent).display(display).elements(elements).ambientOcclusion(GsonUtil.getBoolean(objectNode, "ambientocclusion", true)).textures(texture).guiLight(guiLight).overrides(overrides).build();
   }

   private static void writeElement(JsonWriter writer, Element element, boolean writeLegacy) throws IOException {
      writer.beginObject().name("from");
      GsonUtil.writeVector3Float(writer, element.from());
      writer.name("to");
      GsonUtil.writeVector3Float(writer, element.to());
      ElementRotation rotation = element.rotation();
      if (rotation != null) {
         writer.name("rotation");
         writeElementRotation(writer, rotation, writeLegacy);
      }

      boolean shade = element.shade();
      if (!shade) {
         writer.name("shade").value(shade);
      }

      int lightEmission = element.lightEmission();
      if (lightEmission != 0) {
         writer.name("light_emission").value((long)lightEmission);
      }

      writer.name("faces").beginObject();

      for(Map.Entry<CubeFace, ElementFace> entry : element.faces().entrySet()) {
         CubeFace type = (CubeFace)entry.getKey();
         ElementFace face = (ElementFace)entry.getValue();
         writer.name(type.name().toLowerCase(Locale.ROOT)).beginObject();
         if (face.uv() != null) {
            TextureUV uv = face.uv();
            TextureUV defaultUv = getDefaultUvForFace(type, element.from(), element.to());
            if (uv != null && !uv.equals(defaultUv)) {
               writer.name("uv");
               writer.beginArray();
               writer.value(uv.from().x() * 16.0F);
               writer.value(uv.from().y() * 16.0F);
               writer.value(uv.to().x() * 16.0F);
               writer.value(uv.to().y() * 16.0F);
               writer.endArray();
            }
         }

         writer.name("texture").value(face.texture());
         if (face.cullFace() != null) {
            writer.name("cullface").value(face.cullFace().name().toLowerCase(Locale.ROOT));
         }

         if (face.rotation() != 0) {
            writer.name("rotation").value((long)face.rotation());
         }

         if (face.tintIndex() != -1) {
            writer.name("tintindex").value((long)face.tintIndex());
         }

         writer.endObject();
      }

      writer.endObject().endObject();
   }

   private static TextureUV getDefaultUvForFace(CubeFace face, Vector3Float from, Vector3Float to) {
      from = from.divide(16.0F);
      to = to.divide(16.0F);
      TextureUV var10000;
      switch (face) {
         case WEST -> var10000 = TextureUV.uv(from.z(), 1.0F - to.y(), to.z(), 1.0F - from.y());
         case EAST -> var10000 = TextureUV.uv(1.0F - to.z(), 1.0F - to.y(), 1.0F - from.z(), 1.0F - from.y());
         case DOWN -> var10000 = TextureUV.uv(from.x(), 1.0F - to.z(), to.x(), 1.0F - from.z());
         case UP -> var10000 = TextureUV.uv(from.x(), from.z(), to.x(), to.z());
         case NORTH -> var10000 = TextureUV.uv(1.0F - to.x(), 1.0F - to.y(), 1.0F - from.x(), 1.0F - from.y());
         case SOUTH -> var10000 = TextureUV.uv(from.x(), 1.0F - to.y(), to.x(), 1.0F - from.y());
         default -> throw new IllegalArgumentException("Unknown face: " + String.valueOf(face));
      }

      return var10000;
   }

   private static Element readElement(JsonElement node, PackFormat packFormat, Key modelKey) {
      JsonObject objectNode = node.getAsJsonObject();
      ElementRotation rotation = null;
      if (objectNode.has("rotation")) {
         rotation = readElementRotation(objectNode.get("rotation"), packFormat);
      }

      Map<CubeFace, ElementFace> faces = new LinkedHashMap();
      Iterator var6 = objectNode.getAsJsonObject("faces").entrySet().iterator();

      while(true) {
         CubeFace face;
         JsonObject elementFaceNode;
         TextureUV uv;
         CubeFace cullFace;
         while(true) {
            if (!var6.hasNext()) {
               return Element.element().from(GsonUtil.readVector3Float(objectNode.get("from"))).to(GsonUtil.readVector3Float(objectNode.get("to"))).rotation(rotation).shade(GsonUtil.getBoolean(objectNode, "shade", true)).lightEmission(GsonUtil.getInt(objectNode, "light_emission", 0)).faces(faces).build();
            }

            Map.Entry<String, JsonElement> entry = (Map.Entry)var6.next();
            face = CubeFace.valueOf(((String)entry.getKey()).toUpperCase(Locale.ROOT));
            elementFaceNode = ((JsonElement)entry.getValue()).getAsJsonObject();
            uv = null;
            if (elementFaceNode.has("uv")) {
               JsonArray array = elementFaceNode.getAsJsonArray("uv");
               float u1 = array.get(0).getAsFloat();
               float v1 = array.get(1).getAsFloat();
               float u2 = array.get(2).getAsFloat();
               float v2 = array.get(3).getAsFloat();
               uv = TextureUV.uv((new Vector2Float(u1, v1)).divide(16.0F), (new Vector2Float(u2, v2)).divide(16.0F));
            }

            cullFace = null;
            if (!elementFaceNode.has("cullface")) {
               break;
            }

            try {
               cullFace = CubeFace.valueOf(elementFaceNode.get("cullface").getAsString().toUpperCase(Locale.ROOT));
               break;
            } catch (IllegalArgumentException var16) {
            }
         }

         faces.put(face, ElementFace.face().uv(uv).texture(elementFaceNode.get("texture").getAsString()).cullFace(cullFace).rotation(GsonUtil.getInt(elementFaceNode, "rotation", 0)).tintIndex(GsonUtil.getInt(elementFaceNode, "tintindex", -1)).build());
      }
   }

   private static void writeElementRotation(JsonWriter writer, ElementRotation rotation, boolean writeLegacy) throws IOException {
      writer.beginObject().name("origin");
      GsonUtil.writeVector3Float(writer, rotation.origin());
      if (!writeLegacy) {
         writer.name("x").value(rotation.rotation().x()).name("y").value(rotation.rotation().y()).name("z").value(rotation.rotation().z());
      } else {
         writer.name("axis").value(rotation.axis().name().toLowerCase(Locale.ROOT)).name("angle").value(rotation.angle());
      }

      boolean rescale = rotation.rescale();
      if (rescale) {
         writer.name("rescale").value(rescale);
      }

      writer.endObject();
   }

   private static ElementRotation readElementRotation(JsonElement node, PackFormat packFormat) {
      JsonObject objectNode = node.getAsJsonObject();
      Vector3Float rotation;
      if (objectNode.has("axis") && objectNode.has("angle")) {
         Axis3D axis = Axis3D.valueOf(objectNode.get("axis").getAsString().toUpperCase(Locale.ROOT));
         float angle = objectNode.get("angle").getAsFloat();
         float abs = Math.abs(angle);
         if (packFormat.min().major() < 75 && (abs > 45.0F || abs < -45.0F)) {
            throw new IllegalArgumentException("Angle must be between [-45.0, 45.0] (inclusive), but was " + abs);
         }

         rotation = Vector3Float.ZERO.with(axis, angle);
      } else {
         float x = objectNode.get("x").getAsFloat();
         float y = objectNode.get("y").getAsFloat();
         float z = objectNode.get("z").getAsFloat();
         rotation = new Vector3Float(x, y, z);
      }

      return ElementRotation.builder().rotation(rotation).origin(GsonUtil.readVector3Float(objectNode.get("origin"))).rescale(GsonUtil.getBoolean(objectNode, "rescale", false)).build();
   }

   private static void writeItemOverride(JsonWriter writer, ItemOverride override) throws IOException {
      writer.beginObject().name("predicate").beginObject();

      for(ItemPredicate predicate : override.predicate()) {
         writer.name(predicate.name());
         Object value = predicate.value();
         Objects.requireNonNull(value);
         byte var6 = 0;
         //$FF: var6->value
         //0->java/lang/Long
         //1->java/lang/Float
         //2->java/lang/Double
         //3->java/lang/Number
         //4->java/lang/String
         //5->java/lang/Boolean
         switch (com.nexomc.nexo.decomp.TypeSwitch.index(value, java.lang.Long.class, java.lang.Float.class, java.lang.Double.class, java.lang.Number.class, java.lang.String.class, java.lang.Boolean.class)) {
            case 0:
               Long l = (Long)value;
               writer.value((Number)l);
               break;
            case 1:
               Float v = (Float)value;
               writer.value((Number)v);
               break;
            case 2:
               Double d = (Double)value;
               writer.value((Number)d);
               break;
            case 3:
               Number number = (Number)value;
               writer.value(number);
               break;
            case 4:
               String s = (String)value;
               writer.value(s);
               break;
            case 5:
               Boolean b = (Boolean)value;
               writer.value(b);
               break;
            default:
               throw new IOException("Unknown predicate value type: " + value.getClass().getName());
         }
      }

      writer.endObject().name("model").value(KeySerializer.toString(override.model())).endObject();
   }

   private static ItemOverride readItemOverride(JsonElement node) {
      JsonObject objectNode = node.getAsJsonObject();
      Key key = Key.key(objectNode.get("model").getAsString());
      List<ItemPredicate> predicates = new ArrayList();

      for(Map.Entry<String, JsonElement> predicateEntry : objectNode.getAsJsonObject("predicate").entrySet()) {
         Object object = getObject(predicateEntry);
         predicates.add(ItemPredicate.custom((String)predicateEntry.getKey(), object));
      }

      return ItemOverride.of(key, predicates);
   }

   private static Object getObject(Map.Entry<String, JsonElement> predicateEntry) {
      JsonElement value = (JsonElement)predicateEntry.getValue();
      Object object;
      if (value.isJsonPrimitive()) {
         JsonPrimitive primitive = value.getAsJsonPrimitive();
         if (primitive.isNumber()) {
            object = primitive.getAsNumber();
         } else if (primitive.isBoolean()) {
            object = primitive.getAsBoolean();
         } else {
            object = primitive.getAsString();
         }
      } else {
         object = value.getAsString();
      }

      return object;
   }

   private static void writeItemTransform(JsonWriter writer, ItemTransform transform) throws IOException {
      writer.beginObject();
      Vector3Float rotation = transform.rotation();
      if (!rotation.equals(ItemTransform.DEFAULT_ROTATION)) {
         writer.name("rotation");
         GsonUtil.writeVector3Float(writer, rotation);
      }

      Vector3Float translation = transform.translation();
      if (!translation.equals(ItemTransform.DEFAULT_TRANSLATION)) {
         writer.name("translation");
         GsonUtil.writeVector3Float(writer, translation);
      }

      Vector3Float scale = transform.scale();
      if (!scale.equals(ItemTransform.DEFAULT_SCALE)) {
         writer.name("scale");
         GsonUtil.writeVector3Float(writer, scale);
      }

      writer.endObject();
   }

   private static ItemTransform readItemTransform(JsonElement node) {
      JsonObject objectNode = node.getAsJsonObject();
      Vector3Float rotation = ItemTransform.DEFAULT_ROTATION;
      Vector3Float translation = ItemTransform.DEFAULT_TRANSLATION;
      Vector3Float scale = ItemTransform.DEFAULT_SCALE;
      if (objectNode.has("rotation")) {
         rotation = GsonUtil.readVector3Float(objectNode.get("rotation"));
      }

      if (objectNode.has("translation")) {
         translation = GsonUtil.readVector3Float(objectNode.get("translation"));
         translation = new Vector3Float(Math.max(-80.0F, Math.min(80.0F, translation.x())), Math.max(-80.0F, Math.min(80.0F, translation.y())), Math.max(-80.0F, Math.min(80.0F, translation.z())));
      }

      if (objectNode.has("scale")) {
         scale = GsonUtil.readVector3Float(objectNode.get("scale"));
         scale = new Vector3Float(Math.min(4.0F, scale.x()), Math.min(4.0F, scale.y()), Math.min(4.0F, scale.z()));
      }

      return ItemTransform.transform(rotation, translation, scale);
   }

   private static void writeTextures(JsonWriter writer, ModelTextures texture) throws IOException {
      ModelTexture particle = texture.particle();
      List<ModelTexture> layers = texture.layers();
      Map<String, ModelTexture> variables = texture.variables();
      if (particle != null || !layers.isEmpty() || !variables.isEmpty()) {
         writer.name("textures");
         writer.beginObject();

         for(int i = 0; i < layers.size(); ++i) {
            writer.name("layer" + i);
            writeModelTexture(writer, (ModelTexture)layers.get(i));
         }

         for(Map.Entry<String, ModelTexture> variable : variables.entrySet()) {
            writer.name((String)variable.getKey());
            writeModelTexture(writer, (ModelTexture)variable.getValue());
         }

         writer.name("particle");
         if (particle == null) {
            if (!layers.isEmpty()) {
               particle = (ModelTexture)layers.getFirst();
            } else {
               particle = (ModelTexture)variables.values().stream().findFirst().orElse(null);
            }
         }

         writeModelTexture(writer, particle);
         writer.endObject();
      }
   }

   private static void writeModelTexture(JsonWriter writer, ModelTexture texture) throws IOException {
      if (!texture.forceTranslucent()) {
         if (texture.reference() != null) {
            writer.value("#" + texture.reference());
         } else {
            writer.value(KeySerializer.toString(texture.key()));
         }
      } else {
         writer.beginObject();
         writer.name("sprite");
         if (texture.reference() != null) {
            writer.value("#" + texture.reference());
         } else {
            writer.value(KeySerializer.toString(texture.key()));
         }

         writer.name("force_translucent").value(texture.forceTranslucent());
      }

   }

   private static ModelTextures readTextures(JsonElement node) {
      JsonObject objectNode = node.getAsJsonObject();
      ModelTexture particle = null;
      List<ModelTexture> layers = new ArrayList(objectNode.entrySet().size());
      Map<String, ModelTexture> variables = new LinkedHashMap();

      for(Map.Entry<String, JsonElement> entry : objectNode.entrySet()) {
         String key = (String)entry.getKey();
         JsonElement value = (JsonElement)entry.getValue();
         boolean forceTranslucent = false;
         String sprite;
         if (value.isJsonPrimitive()) {
            sprite = value.getAsString();
         } else if (value instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject)value;
            sprite = jsonObject.get("sprite").getAsString();
            forceTranslucent = jsonObject.get("force_translucent").getAsBoolean();
         } else {
            sprite = "#missingno";
         }

         ModelTexture parsed = readTextureField(sprite, forceTranslucent, key, layers, variables);
         if ("particle".equals(key)) {
            particle = parsed;
         }
      }

      return ModelTextures.builder().particle(particle).layers(layers).variables(variables).build();
   }

   private static ModelTexture readTextureField(String valueString, boolean translucent, String key, List<ModelTexture> layers, Map<String, ModelTexture> variables) {
      ModelTexture texture = valueString.charAt(0) == '#' ? ModelTexture.ofReference(valueString.substring(1), translucent) : ModelTexture.ofKey(Key.key(valueString), translucent);
      if ("particle".equals(key)) {
         return texture;
      } else {
         if (key.startsWith("layer")) {
            int layer = Integer.parseInt(key.substring("layer".length()));
            layers.add(texture);
         } else {
            variables.put(key, texture);
         }

         return null;
      }
   }

   static {
      CATEGORY = new ResourceCategoryImpl<Model>("models", ".json", ResourceContainer::models, INSTANCE);
   }
}

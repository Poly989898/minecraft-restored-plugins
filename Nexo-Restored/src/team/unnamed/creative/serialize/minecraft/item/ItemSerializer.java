package team.unnamed.creative.serialize.minecraft.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.DyeColor;
import team.unnamed.creative.base.HeadType;
import team.unnamed.creative.base.Pose;
import team.unnamed.creative.base.WoodType;
import team.unnamed.creative.item.BundleSelectedItemModel;
import team.unnamed.creative.item.CompositeItemModel;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.EmptyItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.SpecialItemModel;
import team.unnamed.creative.item.Transformation;
import team.unnamed.creative.item.property.BlockStateItemStringProperty;
import team.unnamed.creative.item.property.CompassItemNumericProperty;
import team.unnamed.creative.item.property.ComponentItemBooleanProperty;
import team.unnamed.creative.item.property.ComponentItemStringProperty;
import team.unnamed.creative.item.property.CountItemNumericProperty;
import team.unnamed.creative.item.property.CustomModelDataItemBooleanProperty;
import team.unnamed.creative.item.property.CustomModelDataItemNumericProperty;
import team.unnamed.creative.item.property.CustomModelDataItemStringProperty;
import team.unnamed.creative.item.property.DamageItemNumericProperty;
import team.unnamed.creative.item.property.HasComponentItemBooleanProperty;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.property.KeybindDownItemBooleanProperty;
import team.unnamed.creative.item.property.LocalTimeItemStringProperty;
import team.unnamed.creative.item.property.NoFieldItemBooleanProperty;
import team.unnamed.creative.item.property.NoFieldItemNumericProperty;
import team.unnamed.creative.item.property.NoFieldItemStringProperty;
import team.unnamed.creative.item.property.TimeItemNumericProperty;
import team.unnamed.creative.item.property.UseCycleItemNumericProperty;
import team.unnamed.creative.item.property.UseDurationItemNumericProperty;
import team.unnamed.creative.item.special.BannerSpecialRender;
import team.unnamed.creative.item.special.BedSpecialRender;
import team.unnamed.creative.item.special.ChestSpecialRender;
import team.unnamed.creative.item.special.CopperGolemStatueSpecialRender;
import team.unnamed.creative.item.special.HeadSpecialRender;
import team.unnamed.creative.item.special.NoFieldSpecialRender;
import team.unnamed.creative.item.special.ShulkerBoxSpecialRender;
import team.unnamed.creative.item.special.SignSpecialRender;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.item.tint.ConstantTintSource;
import team.unnamed.creative.item.tint.CustomModelDataTintSource;
import team.unnamed.creative.item.tint.GrassTintSource;
import team.unnamed.creative.item.tint.KeyedAndBackedTintSource;
import team.unnamed.creative.item.tint.TintSource;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.GsonUtil;
import team.unnamed.creative.serialize.minecraft.ResourceCategoryImpl;
import team.unnamed.creative.serialize.minecraft.base.KeySerializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.JsonResourceSerializer;

public final class ItemSerializer implements JsonResourceSerializer<Item>, JsonResourceDeserializer<Item> {
   public static final ItemSerializer INSTANCE = new ItemSerializer();
   public static final ResourceCategoryImpl<Item> CATEGORY;

   private ItemSerializer() {
   }

   private void serializeItemModel(ItemModel model, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();
      Objects.requireNonNull(model);
      byte var5 = 0;
      //$FF: var5->value
      //0->team/unnamed/creative/item/EmptyItemModel
      //1->team/unnamed/creative/item/ReferenceItemModel
      //2->team/unnamed/creative/item/SpecialItemModel
      //3->team/unnamed/creative/item/CompositeItemModel
      //4->team/unnamed/creative/item/ConditionItemModel
      //5->team/unnamed/creative/item/SelectItemModel
      //6->team/unnamed/creative/item/RangeDispatchItemModel
      //7->team/unnamed/creative/item/BundleSelectedItemModel
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(model, team.unnamed.creative.item.EmptyItemModel.class, team.unnamed.creative.item.ReferenceItemModel.class, team.unnamed.creative.item.SpecialItemModel.class, team.unnamed.creative.item.CompositeItemModel.class, team.unnamed.creative.item.ConditionItemModel.class, team.unnamed.creative.item.SelectItemModel.class, team.unnamed.creative.item.RangeDispatchItemModel.class, team.unnamed.creative.item.BundleSelectedItemModel.class)) {
         case 0:
            EmptyItemModel ignored = (EmptyItemModel)model;
            writer.name("type").value("empty");
            break;
         case 1:
            ReferenceItemModel referenceItemModel = (ReferenceItemModel)model;
            this.writeReference(writer, referenceItemModel);
            break;
         case 2:
            SpecialItemModel specialItemModel = (SpecialItemModel)model;
            this.writeSpecial(writer, specialItemModel);
            break;
         case 3:
            CompositeItemModel compositeItemModel = (CompositeItemModel)model;
            this.writeComposite(writer, compositeItemModel, packFormat);
            break;
         case 4:
            ConditionItemModel conditionItemModel = (ConditionItemModel)model;
            this.writeCondition(writer, conditionItemModel, packFormat);
            break;
         case 5:
            SelectItemModel selectItemModel = (SelectItemModel)model;
            this.writeSelect(writer, selectItemModel, packFormat);
            break;
         case 6:
            RangeDispatchItemModel rangeDispatchItemModel = (RangeDispatchItemModel)model;
            this.writeRangeDispatch(writer, rangeDispatchItemModel, packFormat);
            break;
         case 7:
            BundleSelectedItemModel ignored = (BundleSelectedItemModel)model;
            writer.name("type").value("bundle/selected_item");
            break;
         default:
            throw new IllegalArgumentException("Unknown item model type: " + String.valueOf(model.getClass()));
      }

      writer.endObject();
   }

   public @NotNull ItemModel deserializeItemModel(JsonElement unknownNode) throws IOException {
      JsonObject node = unknownNode.getAsJsonObject();
      Key type = Key.key(node.get("type").getAsString());
      if (!type.namespace().equals("minecraft")) {
         throw new IllegalArgumentException("Unknown item model type: " + String.valueOf(type));
      } else {
         Object var10000;
         switch (type.value()) {
            case "empty" -> var10000 = ItemModel.empty();
            case "model" -> var10000 = this.readReference(node);
            case "special" -> var10000 = this.readSpecial(node);
            case "composite" -> var10000 = this.readComposite(node);
            case "condition" -> var10000 = this.readCondition(node);
            case "select" -> var10000 = this.readSelect(node);
            case "range_dispatch" -> var10000 = this.readRangeDispatch(node);
            case "bundle/selected_item" -> var10000 = ItemModel.bundleSelectedItem();
            default -> throw new IllegalArgumentException("Unknown item model type: " + String.valueOf(type));
         }

         return (ItemModel)var10000;
      }
   }

   public void serializeToJson(Item item, JsonWriter writer, PackFormat packFormat) throws IOException {
      writer.beginObject();
      writer.name("model");
      this.serializeItemModel(item.model(), writer, packFormat);
      boolean handAnimationOnSwap = item.handAnimationOnSwap();
      if (!handAnimationOnSwap) {
         writer.name("hand_animation_on_swap").value(handAnimationOnSwap);
      }

      boolean oversizedInGui = item.oversizedInGui();
      if (oversizedInGui) {
         writer.name("oversized_in_gui").value(oversizedInGui);
      }

      float swapAnimationScale = item.swapAnimationScale();
      if (swapAnimationScale != 1.0F) {
         writer.name("swap_animation_scale").value(swapAnimationScale);
      }

      writer.endObject();
   }

   public Item deserializeFromJson(JsonElement node, Key key, PackFormat packFormat) throws IOException {
      JsonObject jsonObject = node.getAsJsonObject();
      ItemModel model = this.deserializeItemModel(jsonObject.get("model"));
      boolean handAnimationOnSwap = jsonObject.has("hand_animation_on_swap") ? jsonObject.get("hand_animation_on_swap").getAsBoolean() : true;
      boolean oversizedInGui = jsonObject.has("oversized_in_gui") ? jsonObject.get("oversized_in_gui").getAsBoolean() : false;
      float swapAnimationScale = jsonObject.has("swap_animation_scale") ? jsonObject.get("swap_animation_scale").getAsFloat() : 1.0F;
      return Item.item(key, model, handAnimationOnSwap, oversizedInGui, swapAnimationScale);
   }

   private void writeReference(@NotNull JsonWriter writer, @NotNull ReferenceItemModel model) throws IOException {
      writer.name("type").value("model");
      writer.name("model").value(KeySerializer.toString(model.model()));
      writer.name("tints").beginArray();

      for(TintSource tint : model.tints()) {
         writer.beginObject();
         Objects.requireNonNull(tint);
         byte var6 = 0;
         //$FF: var6->value
         //0->team/unnamed/creative/item/tint/ConstantTintSource
         //1->team/unnamed/creative/item/tint/CustomModelDataTintSource
         //2->team/unnamed/creative/item/tint/GrassTintSource
         //3->team/unnamed/creative/item/tint/KeyedAndBackedTintSource
         switch (com.nexomc.nexo.decomp.TypeSwitch.index(tint, team.unnamed.creative.item.tint.ConstantTintSource.class, team.unnamed.creative.item.tint.CustomModelDataTintSource.class, team.unnamed.creative.item.tint.GrassTintSource.class, team.unnamed.creative.item.tint.KeyedAndBackedTintSource.class)) {
            case 0:
               ConstantTintSource constantTintSource = (ConstantTintSource)tint;
               writer.name("type").value("constant");
               writer.name("value").value((long)constantTintSource.tint());
               break;
            case 1:
               CustomModelDataTintSource customModelDataTintSource = (CustomModelDataTintSource)tint;
               writer.name("type").value("custom_model_data");
               int index = customModelDataTintSource.index();
               if (index != 0) {
                  writer.name("index").value((long)index);
               }

               writer.name("default").value((long)customModelDataTintSource.defaultTint());
               break;
            case 2:
               GrassTintSource grassTintSource = (GrassTintSource)tint;
               writer.name("type").value("grass");
               writer.name("temperature").value(grassTintSource.temperature());
               writer.name("downfall").value(grassTintSource.downfall());
               break;
            case 3:
               KeyedAndBackedTintSource keyedAndBackedTintSource = (KeyedAndBackedTintSource)tint;
               writer.name("type").value(KeySerializer.toString(keyedAndBackedTintSource.key()));
               writer.name("default").value((long)keyedAndBackedTintSource.defaultTint());
               break;
            default:
               throw new IllegalArgumentException("Unknown tint source type: " + String.valueOf(tint.getClass()));
         }

         writer.endObject();
      }

      writer.endArray();
      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull ReferenceItemModel readReference(@NotNull JsonObject node) {
      Key model = Key.key(node.get("model").getAsString());
      List<TintSource> tints = new ArrayList();
      if (node.has("tints")) {
         for(JsonElement tintElement : node.getAsJsonArray("tints")) {
            JsonObject tintObject = tintElement.getAsJsonObject();
            Key type = Key.key(tintObject.get("type").getAsString());
            if (!type.namespace().equals("minecraft")) {
               throw new IllegalArgumentException("Unknown tint source type: " + String.valueOf(type));
            }

            switch (type.value()) {
               case "constant":
                  tints.add(TintSource.constant(tintObject.get("value").getAsInt()));
                  break;
               case "custom_model_data":
                  int index = tintObject.has("index") ? tintObject.get("index").getAsInt() : 0;
                  tints.add(TintSource.customModelData(index, GsonUtil.parseColor(tintObject.get("default"))));
                  break;
               case "grass":
                  float temperature = tintObject.get("temperature").getAsFloat();
                  float downfall = tintObject.get("downfall").getAsFloat();
                  tints.add(TintSource.grass(temperature, downfall));
                  break;
               case "dye":
                  tints.add(TintSource.dye(GsonUtil.parseColor(tintObject.get("default"))));
                  break;
               case "firework":
                  tints.add(TintSource.firework(GsonUtil.parseColor(tintObject.get("default"))));
                  break;
               case "map_color":
                  tints.add(TintSource.mapColor(GsonUtil.parseColor(tintObject.get("default"))));
                  break;
               case "potion":
                  tints.add(TintSource.potion(GsonUtil.parseColor(tintObject.get("default"))));
                  break;
               default:
                  throw new IllegalArgumentException("Unknown tint source type: " + String.valueOf(type));
            }
         }
      }

      Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
      return ItemModel.reference(model, tints, transformation);
   }

   private void writeSpecial(@NotNull JsonWriter writer, @NotNull SpecialItemModel model) throws IOException {
      writer.name("type").value("special");
      writer.name("model").beginObject();
      SpecialRender render = model.render();
      Objects.requireNonNull(render);
      byte var5 = 0;
      //$FF: var5->value
      //0->team/unnamed/creative/item/special/BannerSpecialRender
      //1->team/unnamed/creative/item/special/BedSpecialRender
      //2->team/unnamed/creative/item/special/ChestSpecialRender
      //3->team/unnamed/creative/item/special/SignSpecialRender
      //4->team/unnamed/creative/item/special/HeadSpecialRender
      //5->team/unnamed/creative/item/special/ShulkerBoxSpecialRender
      //6->team/unnamed/creative/item/special/CopperGolemStatueSpecialRender
      //7->team/unnamed/creative/item/special/NoFieldSpecialRender
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(render, team.unnamed.creative.item.special.BannerSpecialRender.class, team.unnamed.creative.item.special.BedSpecialRender.class, team.unnamed.creative.item.special.ChestSpecialRender.class, team.unnamed.creative.item.special.SignSpecialRender.class, team.unnamed.creative.item.special.HeadSpecialRender.class, team.unnamed.creative.item.special.ShulkerBoxSpecialRender.class, team.unnamed.creative.item.special.CopperGolemStatueSpecialRender.class, team.unnamed.creative.item.special.NoFieldSpecialRender.class)) {
         case 0:
            BannerSpecialRender bannerRender = (BannerSpecialRender)render;
            writer.name("type").value("banner");
            writer.name("color").value(bannerRender.color().name().toLowerCase(Locale.ENGLISH));
            break;
         case 1:
            BedSpecialRender bedRender = (BedSpecialRender)render;
            writer.name("type").value("bed");
            writer.name("texture").value(KeySerializer.toString(bedRender.texture()));
            break;
         case 2:
            ChestSpecialRender chestRender = (ChestSpecialRender)render;
            writer.name("type").value("chest");
            writer.name("texture").value(KeySerializer.toString(chestRender.texture()));
            float openness = chestRender.openness();
            if (openness != 0.0F) {
               writer.name("openness").value(openness);
            }
            break;
         case 3:
            SignSpecialRender signRender = (SignSpecialRender)render;
            writer.name("type").value(signRender.hanging() ? "hanging_sign" : "standing_sign");
            writer.name("wood_type").value(signRender.woodType().name().toLowerCase(Locale.ENGLISH));
            Key texture = signRender.texture();
            if (texture != null) {
               writer.name("texture").value(KeySerializer.toString(texture));
            }
            break;
         case 4:
            HeadSpecialRender headRender = (HeadSpecialRender)render;
            writer.name("type").value("head");
            writer.name("kind").value(headRender.kind().name().toLowerCase(Locale.ENGLISH));
            Key texture = headRender.texture();
            if (texture != null) {
               writer.name("texture").value(KeySerializer.toString(texture));
            }

            float animation = headRender.animation();
            if (animation != 0.0F) {
               writer.name("animation").value(animation);
            }
            break;
         case 5:
            ShulkerBoxSpecialRender shulkerBoxRender = (ShulkerBoxSpecialRender)render;
            writer.name("type").value("shulker_box");
            writer.name("texture").value(KeySerializer.toString(shulkerBoxRender.texture()));
            float openness = shulkerBoxRender.openness();
            if (openness != 0.0F) {
               writer.name("openness").value(openness);
            }

            CubeFace orientation = shulkerBoxRender.orientation();
            if (orientation != ShulkerBoxSpecialRender.DEFAULT_ORIENTATION) {
               writer.name("orientation").value(orientation.name().toLowerCase(Locale.ENGLISH));
            }
            break;
         case 6:
            CopperGolemStatueSpecialRender copperGolemStatueRender = (CopperGolemStatueSpecialRender)render;
            writer.name("type").value("copper_golem_statue");
            writer.name("texture").value(KeySerializer.toString(copperGolemStatueRender.texture()));
            writer.name("pose").value(copperGolemStatueRender.pose().name().toLowerCase(Locale.ENGLISH));
            break;
         case 7:
            NoFieldSpecialRender noFieldRender = (NoFieldSpecialRender)render;
            writer.name("type").value(KeySerializer.toString(noFieldRender.key()));
            break;
         default:
            throw new IllegalArgumentException("Unknown special render type: " + String.valueOf(render.getClass()));
      }

      writer.endObject();
      writer.name("base").value(KeySerializer.toString(model.base()));
      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull SpecialItemModel readSpecial(@NotNull JsonObject node) {
      JsonObject modelNode = node.getAsJsonObject("model");
      Key type = Key.key(modelNode.get("type").getAsString());
      if (!type.namespace().equals("minecraft")) {
         throw new IllegalArgumentException("Unknown special render type: " + String.valueOf(type));
      } else {
         SpecialRender render;
         switch (type.value()) {
            case "banner":
               render = SpecialRender.banner(DyeColor.valueOf(modelNode.get("color").getAsString().toUpperCase(Locale.ENGLISH)));
               break;
            case "bed":
               render = SpecialRender.bed(Key.key(modelNode.get("texture").getAsString()));
               break;
            case "chest":
               Key chestTexture = Key.key(modelNode.get("texture").getAsString());
               float openness = modelNode.has("openness") ? modelNode.get("openness").getAsFloat() : 0.0F;
               render = SpecialRender.chest(chestTexture, openness);
               break;
            case "hanging_sign":
            case "standing_sign":
               boolean hanging = type.asMinimalString().equals("hanging_sign");
               WoodType woodType = WoodType.valueOf(modelNode.get("wood_type").getAsString().toUpperCase(Locale.ENGLISH));
               Key signTexture = modelNode.has("texture") ? Key.key(modelNode.get("texture").getAsString()) : null;
               render = hanging ? SpecialRender.hangingSign(woodType, signTexture) : SpecialRender.standingSign(woodType, signTexture);
               break;
            case "head":
               HeadType kind = HeadType.valueOf(modelNode.get("kind").getAsString().toUpperCase(Locale.ENGLISH));
               Key headTexture = modelNode.has("texture") ? Key.key(modelNode.get("texture").getAsString()) : null;
               float animation = modelNode.has("animation") ? modelNode.get("animation").getAsFloat() : 0.0F;
               render = SpecialRender.head(kind, headTexture, animation);
               break;
            case "player_head":
               render = SpecialRender.playerHead();
               break;
            case "shulker_box":
               Key shulkerBoxTexture = Key.key(modelNode.get("texture").getAsString());
               float shulkerBoxOpenness = modelNode.has("openness") ? modelNode.get("openness").getAsFloat() : 0.0F;
               CubeFace orientation = modelNode.has("orientation") ? CubeFace.valueOf(modelNode.get("orientation").getAsString().toUpperCase(Locale.ENGLISH)) : ShulkerBoxSpecialRender.DEFAULT_ORIENTATION;
               render = SpecialRender.shulkerBox(shulkerBoxTexture, shulkerBoxOpenness, orientation);
               break;
            case "conduit":
               render = SpecialRender.conduit();
               break;
            case "decorated_pot":
               render = SpecialRender.decoratedPot();
               break;
            case "shield":
               render = SpecialRender.shield();
               break;
            case "trident":
               render = SpecialRender.trident();
               break;
            case "copper_golem_statue":
               Pose pose = Pose.valueOf(modelNode.get("pose").getAsString().toUpperCase(Locale.ENGLISH));
               Key texture = Key.key(modelNode.get("texture").getAsString());
               render = SpecialRender.copperGolemStatue(pose, texture);
               break;
            default:
               throw new IllegalArgumentException("Unknown special render type: " + String.valueOf(type));
         }

         Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
         return ItemModel.special(render, Key.key(node.get("base").getAsString()), transformation);
      }
   }

   private void writeComposite(@NotNull JsonWriter writer, @NotNull CompositeItemModel model, PackFormat packFormat) throws IOException {
      writer.name("type").value("composite");
      writer.name("models").beginArray();

      for(ItemModel child : model.models()) {
         this.serializeItemModel(child, writer, packFormat);
      }

      writer.endArray();
      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull CompositeItemModel readComposite(@NotNull JsonObject node) throws IOException {
      List<ItemModel> models = new ArrayList();

      for(JsonElement childElement : node.getAsJsonArray("models")) {
         models.add(this.deserializeItemModel(childElement));
      }

      Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
      return ItemModel.composite(models, transformation);
   }

   private void writeCondition(@NotNull JsonWriter writer, @NotNull ConditionItemModel model, PackFormat packFormat) throws IOException {
      writer.name("type").value("condition");
      ItemBooleanProperty condition = model.condition();
      Objects.requireNonNull(condition);
      byte var6 = 0;
      //$FF: var6->value
      //0->team/unnamed/creative/item/property/CustomModelDataItemBooleanProperty
      //1->team/unnamed/creative/item/property/HasComponentItemBooleanProperty
      //2->team/unnamed/creative/item/property/KeybindDownItemBooleanProperty
      //3->team/unnamed/creative/item/property/ComponentItemBooleanProperty
      //4->team/unnamed/creative/item/property/NoFieldItemBooleanProperty
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(condition, team.unnamed.creative.item.property.CustomModelDataItemBooleanProperty.class, team.unnamed.creative.item.property.HasComponentItemBooleanProperty.class, team.unnamed.creative.item.property.KeybindDownItemBooleanProperty.class, team.unnamed.creative.item.property.ComponentItemBooleanProperty.class, team.unnamed.creative.item.property.NoFieldItemBooleanProperty.class)) {
         case 0:
            CustomModelDataItemBooleanProperty customModelDataItemBooleanProperty = (CustomModelDataItemBooleanProperty)condition;
            writer.name("property").value("custom_model_data");
            int index = customModelDataItemBooleanProperty.index();
            if (index != 0) {
               writer.name("index").value((long)index);
            }
            break;
         case 1:
            HasComponentItemBooleanProperty hasComponent = (HasComponentItemBooleanProperty)condition;
            writer.name("property").value("has_component");
            writer.name("component").value(hasComponent.component());
            boolean ignoreDefault = hasComponent.ignoreDefault();
            if (ignoreDefault) {
               writer.name("ignore_default").value(ignoreDefault);
            }
            break;
         case 2:
            KeybindDownItemBooleanProperty keybindDownItemBooleanProperty = (KeybindDownItemBooleanProperty)condition;
            writer.name("property").value("keybind_down");
            writer.name("keybind").value(keybindDownItemBooleanProperty.key());
            break;
         case 3:
            ComponentItemBooleanProperty component = (ComponentItemBooleanProperty)condition;
            writer.name("property").value("component");
            writer.name("predicate").value(component.predicate());
            writer.name("value").jsonValue(component.value().toString());
            break;
         case 4:
            NoFieldItemBooleanProperty noFieldItemBooleanProperty = (NoFieldItemBooleanProperty)condition;
            writer.name("property").value(KeySerializer.toString(noFieldItemBooleanProperty.key()));
            break;
         default:
            throw new IllegalArgumentException("Unknown condition type: " + String.valueOf(condition.getClass()));
      }

      writer.name("on_true");
      this.serializeItemModel(model.onTrue(), writer, packFormat);
      writer.name("on_false");
      this.serializeItemModel(model.onFalse(), writer, packFormat);
      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull ConditionItemModel readCondition(@NotNull JsonObject node) throws IOException {
      Key property = Key.key(node.get("property").getAsString());
      if (!property.namespace().equals("minecraft")) {
         throw new IllegalArgumentException("Unknown condition property: " + String.valueOf(property));
      } else {
         ItemBooleanProperty condition;
         switch (property.value()) {
            case "custom_model_data":
               int index = node.has("index") ? node.get("index").getAsInt() : 0;
               condition = ItemBooleanProperty.customModelData(index);
               break;
            case "has_component":
               String component = node.get("component").getAsString();
               boolean ignoreDefault = node.has("ignore_default") ? node.get("ignore_default").getAsBoolean() : false;
               condition = ItemBooleanProperty.hasComponent(component, ignoreDefault);
               break;
            case "keybind_down":
               condition = ItemBooleanProperty.keybindDown(node.get("keybind").getAsString());
               break;
            case "broken":
               condition = ItemBooleanProperty.broken();
               break;
            case "bundle/has_selected_item":
               condition = ItemBooleanProperty.bundleHasSelectedItem();
               break;
            case "carried":
               condition = ItemBooleanProperty.carried();
               break;
            case "damaged":
               condition = ItemBooleanProperty.damaged();
               break;
            case "extended_view":
               condition = ItemBooleanProperty.extendedView();
               break;
            case "fishing_rod/cast":
               condition = ItemBooleanProperty.fishingRodCast();
               break;
            case "selected":
               condition = ItemBooleanProperty.selected();
               break;
            case "using_item":
               condition = ItemBooleanProperty.usingItem();
               break;
            case "view_entity":
               condition = ItemBooleanProperty.viewEntity();
               break;
            case "component":
               String predicate = node.get("predicate").getAsString();
               JsonElement value = node.get("value");
               condition = ItemBooleanProperty.component(predicate, value);
               break;
            default:
               throw new IllegalArgumentException("Unknown condition property: " + String.valueOf(property));
         }

         Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
         return ItemModel.conditional(condition, this.deserializeItemModel(node.get("on_true")), this.deserializeItemModel(node.get("on_false")), transformation);
      }
   }

   private void writeSelect(@NotNull JsonWriter writer, @NotNull SelectItemModel model, PackFormat packFormat) throws IOException {
      writer.name("type").value("select");
      ItemStringProperty property = model.property();
      Objects.requireNonNull(property);
      byte var6 = 0;
      //$FF: var6->value
      //0->team/unnamed/creative/item/property/BlockStateItemStringProperty
      //1->team/unnamed/creative/item/property/CustomModelDataItemStringProperty
      //2->team/unnamed/creative/item/property/LocalTimeItemStringProperty
      //3->team/unnamed/creative/item/property/ComponentItemStringProperty
      //4->team/unnamed/creative/item/property/NoFieldItemStringProperty
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(property, team.unnamed.creative.item.property.BlockStateItemStringProperty.class, team.unnamed.creative.item.property.CustomModelDataItemStringProperty.class, team.unnamed.creative.item.property.LocalTimeItemStringProperty.class, team.unnamed.creative.item.property.ComponentItemStringProperty.class, team.unnamed.creative.item.property.NoFieldItemStringProperty.class)) {
         case 0:
            BlockStateItemStringProperty blockStateItemStringProperty = (BlockStateItemStringProperty)property;
            writer.name("property").value("block_state");
            writer.name("block_state_property").value(blockStateItemStringProperty.property());
            break;
         case 1:
            CustomModelDataItemStringProperty customModelDataItemStringProperty = (CustomModelDataItemStringProperty)property;
            writer.name("property").value("custom_model_data");
            int index = customModelDataItemStringProperty.index();
            if (index != 0) {
               writer.name("index").value((long)index);
            }
            break;
         case 2:
            LocalTimeItemStringProperty localTimeProperty = (LocalTimeItemStringProperty)property;
            writer.name("property").value("local_time");
            writer.name("pattern").value(localTimeProperty.pattern());
            String locale = localTimeProperty.locale();
            if (!locale.equals("")) {
               writer.name("locale").value(locale);
            }

            String timezone = localTimeProperty.timezone();
            if (timezone != null) {
               writer.name("timezone").value(timezone);
            }
            break;
         case 3:
            ComponentItemStringProperty component = (ComponentItemStringProperty)property;
            writer.name("property").value("component").name("component").value(component.component());
            break;
         case 4:
            NoFieldItemStringProperty noFieldItemStringProperty = (NoFieldItemStringProperty)property;
            writer.name("property").value(KeySerializer.toString(noFieldItemStringProperty.key()));
            break;
         default:
            throw new IllegalArgumentException("Unknown select property type: " + String.valueOf(property.getClass()));
      }

      writer.name("cases").beginArray();

      for(SelectItemModel.Case _case : model.cases()) {
         writer.beginObject();
         writer.name("when");
         List<JsonElement> when = _case.when();
         if (when.size() == 1 && ((JsonElement)when.getFirst()).isJsonPrimitive()) {
            writer.jsonValue(((JsonElement)when.getFirst()).toString());
         } else {
            writer.beginArray();

            for(JsonElement value : when) {
               writer.jsonValue(value.toString());
            }

            writer.endArray();
         }

         writer.name("model");
         this.serializeItemModel(_case.model(), writer, packFormat);
         writer.endObject();
         if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
            TransformationSerializer.writeToWriter(writer, model.transformation());
         }
      }

      writer.endArray();
      ItemModel fallback = model.fallback();
      if (fallback != null) {
         writer.name("fallback");
         this.serializeItemModel(fallback, writer, packFormat);
      }

      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull SelectItemModel readSelect(@NotNull JsonObject node) throws IOException {
      Key propertyType = Key.key(node.get("property").getAsString());
      if (!propertyType.namespace().equals("minecraft")) {
         throw new IllegalArgumentException("Unknown select property type: " + String.valueOf(propertyType));
      } else {
         ItemStringProperty property;
         switch (propertyType.value()) {
            case "block_state":
               property = ItemStringProperty.blockState(node.get("block_state_property").getAsString());
               break;
            case "custom_model_data":
               int index = node.has("index") ? node.get("index").getAsInt() : 0;
               property = ItemStringProperty.customModelData(index);
               break;
            case "local_time":
               String pattern = node.get("pattern").getAsString();
               String locale = node.has("locale") ? node.get("locale").getAsString() : "";
               String timezone = node.has("timezone") ? node.get("timezone").getAsString() : null;
               property = ItemStringProperty.localTime(locale, timezone, pattern);
               break;
            case "charge_type":
               property = ItemStringProperty.chargeType();
               break;
            case "context_dimension":
               property = ItemStringProperty.contextDimension();
               break;
            case "context_entity_type":
               property = ItemStringProperty.contextEntityType();
               break;
            case "display_context":
               property = ItemStringProperty.displayContext();
               break;
            case "main_hand":
               property = ItemStringProperty.mainHand();
               break;
            case "trim_material":
               property = ItemStringProperty.trimMaterial();
               break;
            case "component":
               property = ItemStringProperty.component(node.get("component").getAsString());
               break;
            default:
               throw new IllegalArgumentException("Unknown select property type: " + String.valueOf(propertyType));
         }

         List<SelectItemModel.Case> cases = new ArrayList();

         for(JsonElement caseElement : node.getAsJsonArray("cases")) {
            JsonObject caseObject = caseElement.getAsJsonObject();
            List<JsonElement> when = new ArrayList();
            JsonElement whenNode = caseObject.get("when");
            if (whenNode instanceof JsonArray) {
               for(JsonElement whenElement : (JsonArray)whenNode) {
                  when.add(whenElement);
               }
            } else {
               when.add(whenNode);
            }

            Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
            cases.add(SelectItemModel.Case._case(this.deserializeItemModel(caseObject.get("model")), when, transformation));
         }

         ItemModel fallback = node.has("fallback") ? this.deserializeItemModel(node.get("fallback")) : null;
         Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
         return ItemModel.select(property, cases, fallback, transformation);
      }
   }

   private void writeRangeDispatch(@NotNull JsonWriter writer, @NotNull RangeDispatchItemModel model, PackFormat packFormat) throws IOException {
      writer.name("type").value("range_dispatch");
      ItemNumericProperty property = model.property();
      Objects.requireNonNull(property);
      byte var6 = 0;
      //$FF: var6->value
      //0->team/unnamed/creative/item/property/CompassItemNumericProperty
      //1->team/unnamed/creative/item/property/CountItemNumericProperty
      //2->team/unnamed/creative/item/property/CustomModelDataItemNumericProperty
      //3->team/unnamed/creative/item/property/DamageItemNumericProperty
      //4->team/unnamed/creative/item/property/TimeItemNumericProperty
      //5->team/unnamed/creative/item/property/UseCycleItemNumericProperty
      //6->team/unnamed/creative/item/property/UseDurationItemNumericProperty
      //7->team/unnamed/creative/item/property/NoFieldItemNumericProperty
      switch (com.nexomc.nexo.decomp.TypeSwitch.index(property, team.unnamed.creative.item.property.CompassItemNumericProperty.class, team.unnamed.creative.item.property.CountItemNumericProperty.class, team.unnamed.creative.item.property.CustomModelDataItemNumericProperty.class, team.unnamed.creative.item.property.DamageItemNumericProperty.class, team.unnamed.creative.item.property.TimeItemNumericProperty.class, team.unnamed.creative.item.property.UseCycleItemNumericProperty.class, team.unnamed.creative.item.property.UseDurationItemNumericProperty.class, team.unnamed.creative.item.property.NoFieldItemNumericProperty.class)) {
         case 0:
            CompassItemNumericProperty compassProperty = (CompassItemNumericProperty)property;
            writer.name("property").value("compass");
            writer.name("target").value(compassProperty.target().name().toLowerCase(Locale.ENGLISH));
            boolean wobble = compassProperty.wobble();
            if (!wobble) {
               writer.name("wobble").value(wobble);
            }
            break;
         case 1:
            CountItemNumericProperty countItemNumericProperty = (CountItemNumericProperty)property;
            writer.name("property").value("count");
            boolean normalize = countItemNumericProperty.normalize();
            if (!normalize) {
               writer.name("normalize").value(normalize);
            }
            break;
         case 2:
            CustomModelDataItemNumericProperty customModelDataItemNumericProperty = (CustomModelDataItemNumericProperty)property;
            writer.name("property").value("custom_model_data");
            int index = customModelDataItemNumericProperty.index();
            if (index != 0) {
               writer.name("index").value((long)index);
            }
            break;
         case 3:
            DamageItemNumericProperty damageItemNumericProperty = (DamageItemNumericProperty)property;
            writer.name("property").value("damage");
            boolean normalize = damageItemNumericProperty.normalize();
            if (!normalize) {
               writer.name("normalize").value(normalize);
            }
            break;
         case 4:
            TimeItemNumericProperty timeProperty = (TimeItemNumericProperty)property;
            writer.name("property").value("time");
            boolean wobble = timeProperty.wobble();
            if (!wobble) {
               writer.name("wobble").value(wobble);
            }

            writer.name("source").value(timeProperty.source().name().toLowerCase(Locale.ENGLISH));
            break;
         case 5:
            UseCycleItemNumericProperty useCycleItemNumericProperty = (UseCycleItemNumericProperty)property;
            writer.name("property").value("use_cycle");
            float period = useCycleItemNumericProperty.period();
            if (period != 1.0F) {
               writer.name("period").value(period);
            }
            break;
         case 6:
            UseDurationItemNumericProperty useDurationItemNumericProperty = (UseDurationItemNumericProperty)property;
            writer.name("property").value("use_duration");
            boolean remaining = useDurationItemNumericProperty.remaining();
            if (remaining) {
               writer.name("remaining").value(remaining);
            }
            break;
         case 7:
            NoFieldItemNumericProperty noFieldItemNumericProperty = (NoFieldItemNumericProperty)property;
            writer.name("property").value(KeySerializer.toString(noFieldItemNumericProperty.key()));
            break;
         default:
            throw new IllegalArgumentException("Unknown range dispatch property type: " + String.valueOf(property.getClass()));
      }

      float scale = model.scale();
      if (scale != 1.0F) {
         writer.name("scale").value(scale);
      }

      writer.name("entries").beginArray();

      for(RangeDispatchItemModel.Entry entry : model.entries()) {
         writer.beginObject();
         writer.name("threshold").value(entry.threshold());
         writer.name("model");
         this.serializeItemModel(entry.model(), writer, packFormat);
         writer.endObject();
      }

      writer.endArray();
      ItemModel fallback = model.fallback();
      if (fallback != null) {
         writer.name("fallback");
         this.serializeItemModel(fallback, writer, packFormat);
      }

      if (model.transformation() != null && model.transformation() != Transformation.DEFAULT) {
         TransformationSerializer.writeToWriter(writer, model.transformation());
      }

   }

   private @NotNull RangeDispatchItemModel readRangeDispatch(@NotNull JsonObject node) throws IOException {
      Key propertyType = Key.key(node.get("property").getAsString());
      if (!propertyType.namespace().equals("minecraft")) {
         throw new IllegalArgumentException("Unknown range dispatch property type: " + String.valueOf(propertyType));
      } else {
         ItemNumericProperty property;
         switch (propertyType.value()) {
            case "compass":
               CompassItemNumericProperty.Target target = CompassItemNumericProperty.Target.valueOf(node.get("target").getAsString().toUpperCase(Locale.ENGLISH));
               boolean wobble = node.has("wobble") ? node.get("wobble").getAsBoolean() : true;
               property = ItemNumericProperty.compass(target, wobble);
               break;
            case "count":
               boolean normalize = node.has("normalize") ? node.get("normalize").getAsBoolean() : true;
               property = ItemNumericProperty.count(normalize);
               break;
            case "custom_model_data":
               int index = node.has("index") ? node.get("index").getAsInt() : 0;
               property = ItemNumericProperty.customModelData(index);
               break;
            case "damage":
               boolean damageNormalize = node.has("normalize") ? node.get("normalize").getAsBoolean() : true;
               property = ItemNumericProperty.damage(damageNormalize);
               break;
            case "time":
               boolean timeWobble = node.has("wobble") ? node.get("wobble").getAsBoolean() : true;
               TimeItemNumericProperty.Source source = TimeItemNumericProperty.Source.valueOf(node.get("source").getAsString().toUpperCase(Locale.ENGLISH));
               property = ItemNumericProperty.time(timeWobble, source);
               break;
            case "use_cycle":
               float period = node.has("period") ? node.get("period").getAsFloat() : 1.0F;
               property = ItemNumericProperty.useCycle(period);
               break;
            case "use_duration":
               boolean remaining = node.has("remaining") ? node.get("remaining").getAsBoolean() : false;
               property = ItemNumericProperty.useDuration(remaining);
               break;
            case "bundle/fullness":
               property = ItemNumericProperty.bundleFullness();
               break;
            case "cooldown":
               property = ItemNumericProperty.cooldown();
               break;
            case "crossbow/pull":
               property = ItemNumericProperty.crossbowPull();
               break;
            default:
               throw new IllegalArgumentException("Unknown range dispatch property type: " + String.valueOf(propertyType));
         }

         float scale = node.has("scale") ? node.get("scale").getAsFloat() : 1.0F;
         List<RangeDispatchItemModel.Entry> entries = new ArrayList();

         for(JsonElement entryElement : node.getAsJsonArray("entries")) {
            JsonObject entryObject = entryElement.getAsJsonObject();
            entries.add(RangeDispatchItemModel.Entry.entry(entryObject.get("threshold").getAsFloat(), this.deserializeItemModel(entryObject.get("model"))));
         }

         ItemModel fallback = node.has("fallback") ? this.deserializeItemModel(node.get("fallback")) : null;
         Transformation transformation = TransformationSerializer.readFromJson(node.get("transformation"));
         return ItemModel.rangeDispatch(property, scale, entries, fallback, transformation);
      }
   }

   static {
      CATEGORY = new ResourceCategoryImpl<Item>("items", ".json", ResourceContainer::items, INSTANCE);
   }
}

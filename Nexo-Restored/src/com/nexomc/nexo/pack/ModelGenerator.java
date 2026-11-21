package com.nexomc.nexo.pack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.NexoMeta;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.SpecialItemModel;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.property.UseDurationItemNumericProperty;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.item.tint.KeyedAndBackedTintSource;
import team.unnamed.creative.item.tint.TintSource;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\u001e\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002J\u0012\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/pack/ModelGenerator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "predicateGenerator", "Lcom/nexomc/nexo/pack/PredicateGenerator;", "generateModels", "", "addOverrides", "key", "Lnet/kyori/adventure/key/Key;", "overrides", "", "Lteam/unnamed/creative/model/ItemOverride;", "generateModelBuilder", "Lteam/unnamed/creative/model/Model$Builder;", "nexoMeta", "Lcom/nexomc/nexo/items/NexoMeta;", "com.nexomc:core"}
)
public final class ModelGenerator {
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private final PredicateGenerator predicateGenerator;

   public ModelGenerator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.predicateGenerator = new PredicateGenerator(this.resourcePack);
   }

   public final void generateModels() {
      it.unimi.dsi.fastutil.objects.ObjectArrayList objectArrayList;
      java.util.Map map;
      net.kyori.adventure.key.Key key;
      Iterable iterable = (Iterable)NexoItems.items();
      Object2ObjectLinkedOpenHashMap object2ObjectLinkedOpenHashMap = new Object2ObjectLinkedOpenHashMap();

      for(Object element : iterable) {
         ItemBuilder p0 = (ItemBuilder)element;
         int value = 0;
         Object obj3 = p0.getType();
         ((ObjectArrayList)object2ObjectLinkedOpenHashMap.computeIfAbsent(obj3, (java.util.function.Function)(k -> new ObjectArrayList()))).add(element);
      }

      Map map3 = (Map)object2ObjectLinkedOpenHashMap;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
         int value2 = 0;
         Material type = (Material)entry.getKey();
         ObjectArrayList items = (ObjectArrayList)entry.getValue();
         ModelGenerator modelGenerator = this;

         Object obj4;
         try {
            ModelGenerator modelGenerator2 = modelGenerator;
            int value3 = 0;
            Key data = ItemStack.of(type).getData(DataComponentTypes.ITEM_MODEL);
            Item item2;
            if (data != null) {
               Key it = data;
               int value4 = 0;
               item2 = ResourcePackUtilsKt.itemOrVanilla(modelGenerator2.resourcePack, it);
            } else {
               item2 = null;
            }

            obj4 = ResultShim.ok(item2);
         } catch (Throwable e) {
            obj4 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Item vanillaItemModel = (Item)(ResultShim.isFailure(obj4) ? null : obj4);
         PredicateGenerator predicateGenerator2 = this.predicateGenerator;
         List overrides = predicateGenerator2.generateBaseModelOverrides(type, (List)items);
         String str = type.toString().toLowerCase(Locale.ROOT);
         Key key2 = Key.key("item/" + str);
         this.addOverrides(key2, overrides);
         String str2 = type.toString().toLowerCase(Locale.ROOT);
         Key key3 = Key.key("block/" + str2);
         this.addOverrides(key3, overrides);
         Iterable iterable2 = (Iterable)items;

         for(Object obj5 : iterable2) {
            ItemBuilder item = (ItemBuilder)obj5;
            int value5 = 0;
            NexoMeta it = item.getNexoMeta();
            int value6 = 0;
            NexoMeta nexoMeta2 = it.getContainsPackInfo() && it.getGenerateModel().isTrue() ? it : null;
            if (nexoMeta2 != null) {
               NexoMeta nexoMeta = nexoMeta2;
               value6 = 0;
               Model.Builder builder2 = this.generateModelBuilder(nexoMeta);
               if (builder2 != null) {
                  Model.Builder builder;
                  block: {
                     builder = builder2;
                     Model model2 = this.resourcePack.model(nexoMeta.getParentModel());
                     if (model2 != null) {
                        Map map2 = model2.display();
                        if (map2 != null) {
                           Map it_10 = map2;
                           int value7 = 0;
                           map2 = !it_10.isEmpty() ? it_10 : null;
                           if (map2 != null) {
                              Map p0 = map2;
                              int value8 = 0;
                              map = (Map)(new Object2ObjectLinkedOpenHashMap(p0));
                              break block;
                           }
                        }
                     }

                     map = DisplayProperties.INSTANCE.fromMaterial(type);
                  }

                  Map display = map;
                  ResourcePack resourcePack2 = this.resourcePack;
                  key3 = nexoMeta.getModel();
                  if (key3 != null) {
                     block: {
                        Model model3 = resourcePack2.model(key3);
                        if (model3 != null) {
                           List list = model3.overrides();
                           if (list != null) {
                              Collection p0 = (Collection)list;
                              int value9 = 0;
                              objectArrayList = new ObjectArrayList(p0);
                              break block;
                           }
                        }

                        objectArrayList = new ObjectArrayList();
                     }

                     ObjectArrayList overrides_9 = objectArrayList;
                     Model model4 = builder.display(display).overrides((List)overrides_9).build();
                     Model model = model4;
                     this.resourcePack.model(model);
                  }
               }
            }

            String str3 = NexoItems.idFromItem(item);
            if (str3 != null) {
               str3 = str3.toLowerCase(Locale.ROOT);
               if (str3 != null) {
                  String itemId;
                  block: {
                     itemId = str3;
                     Key key4 = item.getItemModel();
                     if (key4 != null) {
                        key = key4.key();
                        if (key != null) {
                           break block;
                        }
                     }

                     key = Key.key("nexo", itemId);
                  }

                  Key itemKey = key;
                  Model model5 = this.resourcePack.model(itemKey);
                  if (model5 == null) {
                     Key key5 = item.getNexoMeta().getModel();
                     if (key5 != null) {
                        Key it_8 = key5;
                        ResourcePack throwingModel = this.resourcePack;
                        int value10 = 0;
                        model5 = ResourcePackUtilsKt.modelOrVanilla(throwingModel, it_8);
                     } else {
                        model5 = null;
                     }

                     if (model5 == null) {
                        continue;
                     }
                  }

                  Model reference = model5;
                  Color color = item.getColor();
                  KeyedAndBackedTintSource keyedAndBackedTintSource;
                  if (color != null) {
                     int p0 = color.asRGB();
                     int value11 = 0;
                     keyedAndBackedTintSource = TintSource.dye(p0);
                  } else {
                     keyedAndBackedTintSource = null;
                  }

                  Collection it_7 = (Collection)CollectionsKt.listOfNotNull(keyedAndBackedTintSource);
                  Object obj;
                  if (it_7.isEmpty()) {
                     int value12 = 0;
                     ItemModel itemModel2 = vanillaItemModel != null ? vanillaItemModel.model() : null;
                     obj = (itemModel2 instanceof ReferenceItemModel ? (ReferenceItemModel)itemModel2 : null) != null ? (itemModel2 instanceof ReferenceItemModel ? (ReferenceItemModel)itemModel2 : null).tints() : null;
                  } else {
                     obj = it_7;
                  }

                  List list2 = (List)obj;
                  if (list2 == null) {
                     list2 = CollectionsKt.emptyList();
                  }

                  List tintSources = list2;
                  it_7 = (Collection)tintSources;
                  Object obj2;
                  if (it_7.isEmpty()) {
                     int value13 = 0;
                     obj2 = CollectionsKt.listOf(TintSource.dye(-1));
                  } else {
                     obj2 = it_7;
                  }

                  List referenceTints = (List)obj2;
                  NexoMeta meta = item.getNexoMeta();
                  Model model6 = meta.dyeableModel(this.resourcePack, reference);
                  if (model6 == null) {
                     model6 = this.resourcePack.model(KeyUtils.INSTANCE.appendSuffix(itemKey, "_dyeable"));
                  }

                  Model dyeableModel = model6;
                  model6 = meta.throwingModel(this.resourcePack, reference);
                  if (model6 == null) {
                     model6 = this.resourcePack.model(KeyUtils.INSTANCE.appendSuffix(itemKey, "_throwing"));
                  }

                  Model throwingModel = model6;
                  model6 = meta.blockingModel(this.resourcePack, reference);
                  if (model6 == null) {
                     model6 = this.resourcePack.model(KeyUtils.INSTANCE.appendSuffix(itemKey, "_blocking"));
                  }

                  Model blockingModel = model6;
                  model6 = meta.castModel(this.resourcePack, reference);
                  if (model6 == null) {
                     model6 = this.resourcePack.model(KeyUtils.INSTANCE.appendSuffix(itemKey, "_cast"));
                  }

                  Model castModel = model6;
                  model6 = meta.brokenModel(this.resourcePack, reference);
                  if (model6 == null) {
                     model6 = this.resourcePack.model(KeyUtils.INSTANCE.appendSuffix(itemKey, "_broken"));
                  }

                  Model brokenModel = model6;
                  List pullingModels = meta.pullingModels(this.resourcePack, reference, referenceTints);
                  List compositeModels = meta.compositeModels(this.resourcePack);
                  Boolean oversizedInGui = item.getNexoMeta().getHandSwapAnimation();
                  boolean p0 = true;
                  int value17 = 0;
                  Boolean handSwapAnimation = !Boolean.valueOf(p0).equals(oversizedInGui) ? oversizedInGui : null;
                  Boolean p0_6 = item.getNexoMeta().getOversizedInGui();
                  boolean p0_5 = false;
                  int value14 = 0;
                  oversizedInGui = !Boolean.valueOf(p0_5).equals(p0_5) ? p0_5 : null;
                  Float itemModel = item.getNexoMeta().getSwapAnimationScale();
                  float scale = 1.0F;
                  int value15 = 0;
                  Float swapAnimationScale = !Float.valueOf(scale).equals(itemModel) ? itemModel : null;
                  if (this.resourcePack.item(itemKey) == null) {
                     ItemModel itemModel3;
                     if (!((Collection)pullingModels).isEmpty()) {
                        scale = type == Material.CROSSBOW ? 1.0F : 0.05F;
                        ItemNumericProperty itemNumericProperty;
                        if (type == Material.CROSSBOW) {
                           itemNumericProperty = ItemNumericProperty.crossbowPull();
                           itemNumericProperty = itemNumericProperty;
                        } else {
                           UseDurationItemNumericProperty useDuration = ItemNumericProperty.useDuration();
                           itemNumericProperty = useDuration;
                        }

                        ItemNumericProperty property = itemNumericProperty;
                        ConditionItemModel conditionItemModel = ItemModel.conditional(ItemBooleanProperty.usingItem(), ItemModel.rangeDispatch(property, scale, pullingModels), ItemModel.reference(reference.key(), referenceTints));
                        ConditionItemModel pullingModel = conditionItemModel;
                        List chargedCases = meta.chargedArrowCases(referenceTints);
                        if (!((Collection)chargedCases).isEmpty()) {
                           SelectItemModel selectItemModel = ItemModel.select(ItemStringProperty.chargeType(), chargedCases, pullingModel);
                           itemModel3 = selectItemModel;
                        } else {
                           itemModel3 = pullingModel;
                        }
                     } else if (dyeableModel != null) {
                        itemModel3 = ItemModel.reference(reference.key());
                        ReferenceItemModel falseModel = (ReferenceItemModel)(itemModel3);
                        Key key6 = dyeableModel.key();
                        TintSource[] tintSources2 = new TintSource[1];
                        Color color2 = item.getColor();
                        tintSources2[0] = TintSource.dye(color2 != null ? color2.asRGB() : Color.WHITE.asRGB());
                        ReferenceItemModel referenceItemModel = ItemModel.reference(key6, tintSources2);
                        ReferenceItemModel trueModel = referenceItemModel;
                        ConditionItemModel modelKey = ItemModel.conditional(ItemBooleanProperty.hasComponent("minecraft:dyed_color"), trueModel, falseModel);
                        itemModel3 = modelKey;
                     } else if (castModel == null && brokenModel == null && throwingModel == null && blockingModel == null) {
                        if (oversizedInGui == null && handSwapAnimation == null && swapAnimationScale == null && !item.hasItemModel()) {
                           if (((Collection)tintSources).isEmpty()) {
                              continue;
                           }

                           itemModel3 = ItemModel.reference(reference.key(), tintSources);
                           itemModel3 = itemModel3;
                        } else {
                           Key key7 = item.getNexoMeta().getModel();
                           if (key7 == null) {
                              key7 = Key.key("nexo:" + itemId);
                           }

                           Key modelKey = key7;
                           if (type == Material.PLAYER_HEAD) {
                              SpecialItemModel specialItemModel = ItemModel.special(SpecialRender.playerHead(), modelKey);
                              itemModel3 = specialItemModel;
                           } else {
                              ReferenceItemModel referenceItemModel2 = ItemModel.reference(modelKey, referenceTints);
                              itemModel3 = referenceItemModel2;
                           }
                        }
                     } else {
                        Pair pair;
                        if (castModel != null) {
                           pair = TuplesKt.to(castModel.key(), ItemBooleanProperty.fishingRodCast());
                        } else if (brokenModel != null) {
                           pair = TuplesKt.to(brokenModel.key(), ItemBooleanProperty.broken());
                        } else {
                           Model model7 = throwingModel;
                           if (throwingModel == null) {
                              model7 = blockingModel;
                           }

                           pair = TuplesKt.to(model7.key(), ItemBooleanProperty.usingItem());
                        }

                        Pair falseModel = pair;
                        Object component1 = falseModel.component1();
                        Key trueReference = (Key)component1;
                        component1 = falseModel.component2();
                        ItemBooleanProperty property = (ItemBooleanProperty)component1;
                        ReferenceItemModel referenceItemModel3 = ItemModel.reference(reference.key(), referenceTints);
                        ReferenceItemModel falseModel_4 = referenceItemModel3;
                        referenceItemModel3 = ItemModel.reference(trueReference, referenceTints);
                        ReferenceItemModel trueModel = referenceItemModel3;
                        ConditionItemModel conditionItemModel2 = ItemModel.conditional(property, trueModel, falseModel_4);
                        itemModel3 = conditionItemModel2;
                     }

                     ItemModel itemModel5 = itemModel3;
                     boolean hasCompositeModels = !((Collection)compositeModels).isEmpty();
                     ItemModel itemModel4;
                     if (hasCompositeModels) {
                        int value16 = 0;
                        SpreadBuilder spreadBuilder = new SpreadBuilder(2);
                        spreadBuilder.add(itemModel5);
                        Collection collection = (Collection)compositeModels;
                        spreadBuilder.addSpread(collection.toArray(new ItemModel[0]));
                        itemModel4 = ItemModel.composite((ItemModel[])spreadBuilder.toArray(new ItemModel[spreadBuilder.size()]));
                        itemModel4 = itemModel4;
                     } else {
                        itemModel4 = itemModel5;
                     }

                     ItemModel itemModel_2 = itemModel4;
                     Item.item(itemKey, itemModel_2, handSwapAnimation != null ? handSwapAnimation : true, oversizedInGui != null ? oversizedInGui : false, swapAnimationScale != null ? swapAnimationScale : 1.0F).addTo(this.resourcePack);
                  }
               }
            }
         }
      }

   }

   private final void addOverrides(Key key, List<? extends ItemOverride> overrides) {
      Model model2 = ResourcePackUtilsKt.modelOrVanilla(this.resourcePack, key);
      if (model2 != null) {
         Model model = model2;
         Key key2 = model.parent();
         if (key2 == null) {
            Model model3 = VanillaResourcePack.INSTANCE.getResourcePack().model(key);
            key2 = model3 != null ? model3.parent() : null;
            if (key2 == null) {
               key2 = Model.BUILT_IN_ENTITY;
            }
         }

         Key parent = key2;
         List list = model.overrides();
         List overrides_1 = CollectionsKt.plus((Collection)list, (Iterable)overrides);
         Model model4 = model.toBuilder().parent(parent).overrides(overrides_1).build();
         Model overrideModel = model4;
         this.resourcePack.model(overrideModel);
      }
   }

   private final Model.Builder generateModelBuilder(NexoMeta nexoMeta) {
      java.util.List<team.unnamed.creative.model.ModelTexture> modelTextures;
      if (nexoMeta.getModel() == null) {
         return null;
      } else {
         String parent;
         ModelTextures.Builder builder;
         block: {
            String str = nexoMeta.getParentModel().value();
            parent = str;
            ModelTextures modelTextures2 = nexoMeta.getModelTextures();
            if (modelTextures2 != null) {
               builder = modelTextures2.toBuilder();
               if (builder != null) {
                  break block;
               }
            }

            builder = ModelTextures.builder();
         }

         ModelTextures.Builder textures;
         block: {
            textures = builder;
            ModelTextures modelTextures3 = nexoMeta.getModelTextures();
            if (modelTextures3 != null) {
               modelTextures = modelTextures3.layers();
               if (modelTextures != null) {
                  break block;
               }
            }

            modelTextures = CollectionsKt.emptyList();
         }

         List layers = modelTextures;
         ModelTexture modelTexture = (ModelTexture)CollectionsKt.firstOrNull(layers);
         if (modelTexture == null) {
            modelTexture = ModelTexture.ofKey(Key.key(""));
         }

         ModelTexture defaultTexture = modelTexture;
         ModelTextures modelTextures4 = nexoMeta.getModelTextures();
         Map map = modelTextures4 != null ? modelTextures4.variables() : null;
         if (map == null || map.isEmpty()) {
            textures.layers(CollectionsKt.emptyList());
            if (!Intrinsics.areEqual(parent, "block/cube") && !Intrinsics.areEqual(parent, "block/cube_directional") && !Intrinsics.areEqual(parent, "block/cube_mirrored")) {
               if (!Intrinsics.areEqual(parent, "block/cube_all") && !Intrinsics.areEqual(parent, "block/cube_mirrored_all")) {
                  if (Intrinsics.areEqual(parent, "block/cross")) {
                     Intrinsics.checkNotNullExpressionValue(textures.addVariable("cross", defaultTexture), "addVariable(...)");
                  } else if (StringsKt.startsWith(parent, "block/orientable", false)) {
                     textures.addVariable("front", defaultTexture);
                     ModelTexture orNull = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                     if (orNull == null) {
                        orNull = defaultTexture;
                     }

                     textures.addVariable("side", orNull);
                     if (!StringsKt.endsWith(parent, "vertical", false)) {
                        orNull = (ModelTexture)CollectionsKt.getOrNull(layers, 2);
                        if (orNull == null) {
                           orNull = defaultTexture;
                        }

                        textures.addVariable("top", orNull);
                     }

                     if (StringsKt.endsWith(parent, "with_bottom", false)) {
                        orNull = (ModelTexture)CollectionsKt.getOrNull(layers, 3);
                        if (orNull == null) {
                           orNull = defaultTexture;
                        }

                        textures.addVariable("bottom", orNull);
                     }

                     Unit unit = Unit.INSTANCE;
                  } else if (StringsKt.startsWith(parent, "block/cube_column", false)) {
                     textures.addVariable("end", defaultTexture);
                     ModelTexture modelTexture2 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                     if (modelTexture2 == null) {
                        modelTexture2 = defaultTexture;
                     }

                     ModelTextures.Builder builder2 = textures.addVariable("side", modelTexture2);
                  } else if (!Intrinsics.areEqual(parent, "block/cube_bottom_top") && !StringsKt.contains((CharSequence)parent, (CharSequence)"block/slab", false) && !StringsKt.endsWith(parent, "stairs", false)) {
                     if (Intrinsics.areEqual(parent, "block/cube_top")) {
                        textures.addVariable("top", defaultTexture);
                        ModelTexture modelTexture3 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                        if (modelTexture3 == null) {
                           modelTexture3 = defaultTexture;
                        }

                        ModelTextures.Builder builder3 = textures.addVariable("side", modelTexture3);
                     } else if (StringsKt.contains((CharSequence)parent, (CharSequence)"block/door_", false)) {
                        textures.addVariable("bottom", defaultTexture);
                        ModelTexture modelTexture4 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                        if (modelTexture4 == null) {
                           modelTexture4 = defaultTexture;
                        }

                        ModelTextures.Builder builder4 = textures.addVariable("top", modelTexture4);
                     } else if (!StringsKt.contains((CharSequence)parent, (CharSequence)"trapdoor", false) && !StringsKt.contains((CharSequence)parent, (CharSequence)"chain", false)) {
                        if (StringsKt.contains((CharSequence)parent, (CharSequence)"lantern", false)) {
                           Intrinsics.checkNotNullExpressionValue(textures.addVariable("lantern", defaultTexture), "addVariable(...)");
                        } else if (StringsKt.contains((CharSequence)parent, (CharSequence)"template_bars", false)) {
                           textures.addVariable("bars", defaultTexture);
                           ModelTexture modelTexture5 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                           if (modelTexture5 == null) {
                              modelTexture5 = defaultTexture;
                           }

                           ModelTextures.Builder builder5 = textures.addVariable("edge", modelTexture5);
                        } else {
                           Intrinsics.checkNotNullExpressionValue(textures.layers(layers), "layers(...)");
                        }
                     } else {
                        Intrinsics.checkNotNullExpressionValue(textures.addVariable("texture", defaultTexture), "addVariable(...)");
                     }
                  } else {
                     textures.addVariable("bottom", defaultTexture);
                     ModelTexture modelTexture6 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
                     if (modelTexture6 == null) {
                        modelTexture6 = defaultTexture;
                     }

                     textures.addVariable("side", modelTexture6);
                     modelTexture6 = (ModelTexture)CollectionsKt.getOrNull(layers, 2);
                     if (modelTexture6 == null) {
                        modelTexture6 = defaultTexture;
                     }

                     ModelTextures.Builder builder6 = textures.addVariable("top", modelTexture6);
                  }
               } else {
                  Intrinsics.checkNotNullExpressionValue(textures.addVariable("all", defaultTexture), "addVariable(...)");
               }
            } else {
               ModelTexture modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 2);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               textures.addVariable("particle", modelTexture7);
               textures.addVariable("down", defaultTexture);
               modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 1);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               textures.addVariable("up", modelTexture7);
               modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 2);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               textures.addVariable("north", modelTexture7);
               modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 3);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               textures.addVariable("south", modelTexture7);
               modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 4);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               textures.addVariable("west", modelTexture7);
               modelTexture7 = (ModelTexture)CollectionsKt.getOrNull(layers, 5);
               if (modelTexture7 == null) {
                  modelTexture7 = defaultTexture;
               }

               ModelTextures.Builder builder7 = textures.addVariable("east", modelTexture7);
            }
         }

         Model.Builder model = Model.model();
         Key key = nexoMeta.getModel();
         return key == null ? null : model.key(key).parent(nexoMeta.getParentModel()).textures(textures.build());
      }
   }
}

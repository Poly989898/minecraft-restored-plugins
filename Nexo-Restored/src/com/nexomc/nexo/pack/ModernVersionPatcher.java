package com.nexomc.nexo.pack;
import com.nexomc.nexo.pack.DisplayProperties;
import com.nexomc.nexo.pack.NexoOverlay;
import com.nexomc.nexo.pack.PackGenerator;
import com.nexomc.nexo.pack.VanillaResourcePack;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.item.CompositeItemModel;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.SpecialItemModel;
import team.unnamed.creative.item.Transformation;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.property.UseDurationItemNumericProperty;
import team.unnamed.creative.item.special.HeadSpecialRender;
import team.unnamed.creative.item.tint.KeyedAndBackedTintSource;
import team.unnamed.creative.item.tint.TintSource;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.overlay.Overlay;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0014\u0010\b\u001a\u00020\t*\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J*\u0010\f\u001a\u00020\t2\b\u0010\r\u001a\u0004\u0018\u00010\t2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0002J!\u0010\u0013\u001a\r\u0012\t\u0012\u00070\u0014¢\u0006\u0002\b\u00150\u000f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0016\u001a\u0004\u0018\u00010\u0017*\b\u0012\u0004\u0012\u00020\u00180\u000f8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001aR \u0010\u001b\u001a\u0004\u0018\u00010\u0017*\b\u0012\u0004\u0012\u00020\u00180\u000f8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001aR\u0018\u0010\u001d\u001a\u00020\u001e*\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010 R\u001a\u0010!\u001a\u0004\u0018\u00010\"*\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b#\u0010$R\u001a\u0010%\u001a\u0004\u0018\u00010\"*\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b&\u0010$R\u0018\u0010'\u001a\u00020\u001e*\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b(\u0010 R\u0018\u0010)\u001a\u00020\u001e*\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b*\u0010 R9\u0010+\u001a \u0012\u000e\u0012\f0\u0012¢\u0006\u0002\b\u0015¢\u0006\u0002\b-\u0012\f\u0012\n /*\u0004\u0018\u00010.0.0,8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b2\u00103\u001a\u0004\b0\u00101R9\u00104\u001a \u0012\u000e\u0012\f0\u0012¢\u0006\u0002\b\u0015¢\u0006\u0002\b-\u0012\f\u0012\n /*\u0004\u0018\u000105050,8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b7\u00103\u001a\u0004\b6\u00101R\u001a\u00108\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u001e09X\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010:\u001a\u0018\u0012\u0004\u0012\u000205\u0012\u000e\u0012\f0\u0012¢\u0006\u0002\b\u0015¢\u0006\u0002\b-09X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010;\u001a\u0012\u0012\u000e\u0012\f0<¢\u0006\u0002\b\u0015¢\u0006\u0002\b-0\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006="},
   d2 = {"Lcom/nexomc/nexo/pack/ModernVersionPatcher;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "convertResources", "", "withTransformation", "Lteam/unnamed/creative/item/ItemModel;", "transformation", "Lteam/unnamed/creative/item/Transformation;", "modelObject", "baseItemModel", "overrides", "", "Lteam/unnamed/creative/model/ItemOverride;", "modelKey", "Lnet/kyori/adventure/key/Key;", "modelEntries", "Lteam/unnamed/creative/item/RangeDispatchItemModel$Entry;", "Lorg/jetbrains/annotations/NotNull;", "customModelData", "", "Lteam/unnamed/creative/model/ItemPredicate;", "getCustomModelData", "(Ljava/util/List;)Ljava/lang/Float;", "pull", "getPull", "pulling", "", "getPulling", "(Lteam/unnamed/creative/model/ItemOverride;)Z", "charged", "Lteam/unnamed/creative/item/SelectItemModel$Case;", "getCharged", "(Lteam/unnamed/creative/model/ItemOverride;)Lteam/unnamed/creative/item/SelectItemModel$Case;", "firework", "getFirework", "blocking", "getBlocking", "cast", "getCast", "standardItemModels", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lkotlin/jvm/internal/EnhancedNullability;", "Lteam/unnamed/creative/item/Item;", "kotlin.jvm.PlatformType", "getStandardItemModels", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "standardItemModels$delegate", "Lkotlin/Lazy;", "standardModels", "Lteam/unnamed/creative/model/Model;", "getStandardModels", "standardModels$delegate", "simpleItemModelPredicate", "Lkotlin/Function1;", "itemKeyPredicate", "DEFAULT_TINT", "Lteam/unnamed/creative/item/tint/KeyedAndBackedTintSource;", "com.nexomc:core"}
)
public final class ModernVersionPatcher {
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private final Lazy standardItemModels$delegate;
   @NotNull
   private final Lazy standardModels$delegate;
   @NotNull
   private final Function1<ItemModel, Boolean> simpleItemModelPredicate;
   @NotNull
   private final Function1<Model, Key> itemKeyPredicate;
   @NotNull
   private final List<KeyedAndBackedTintSource> DEFAULT_TINT;

   public ModernVersionPatcher(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.standardItemModels$delegate = LazyKt.lazy((() -> ModernVersionPatcher.standardItemModels_delegate$lambda$0(this)));
      this.standardModels$delegate = LazyKt.lazy((() -> ModernVersionPatcher.standardModels_delegate$lambda$0(this)));
      this.simpleItemModelPredicate = ModernVersionPatcher::simpleItemModelPredicate$lambda$0;
      this.itemKeyPredicate = ModernVersionPatcher::itemKeyPredicate$lambda$0;
      this.DEFAULT_TINT = CollectionsKt.listOf(TintSource.dye(-1));
   }

   public final void convertResources() {
      team.unnamed.creative.item.ItemModel itemModel;
      boolean flag;
      boolean flag2;
      int value;
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;
      Function1 keySelector = this.itemKeyPredicate;
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : iterable) {
         destination.put(keySelector.invoke(element), element);
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)destination.entrySet()) {
         int value2 = 0;
         Object key = entry.getKey();
         Key itemKey = (Key)key;
         Model model = (Model)entry.getValue();
         Item item = this.resourcePack.item(itemKey);
         if (item == null) {
            item = this.getStandardItemModels().get(itemKey);
         }

         Item standardItem = item;
         Collection collection = (Collection)model.overrides();
         Object obj;
         if (collection.isEmpty()) {
            int value3 = 0;
            if (!((standardItem != null ? standardItem.model() : null) instanceof SpecialItemModel)) {
               continue;
            }

            DisplayProperties displayProperties = DisplayProperties.INSTANCE;
            if (Intrinsics.areEqual(displayProperties.fromModel(model), model.display())) {
               continue;
            }

            obj = CollectionsKt.emptyList();
         } else {
            obj = collection;
         }

         List overrides;
         boolean handSwap;
         block: {
            overrides = (List)obj;
            handSwap = standardItem != null ? standardItem.handAnimationOnSwap() : true;
            if (Intrinsics.areEqual(itemKey.value(), "player_head")) {
               ItemTransform itemTransform = model.display().get(ItemTransform.Type.GUI);
               if (!Intrinsics.areEqual(itemTransform != null ? itemTransform.scale() : null, Vector3Float.ONE)) {
                  flag = true;
                  break block;
               }
            }

            flag = standardItem != null ? standardItem.oversizedInGui() : false;
         }

         boolean oversizedInGui;
         block: {
            oversizedInGui = flag;
            if (standardItem != null) {
               int value4 = 0;
               itemModel = convertResources$lambda$0$mergeOverridesIntoItemModel(this, overrides, model, itemKey, standardItem);
               if (itemModel != null) {
                  break block;
               }
            }

            itemModel = this.modelObject(standardItem != null ? standardItem.model() : null, overrides, itemKey);
         }

         ItemModel finalNewItemModel = itemModel;
         this.resourcePack.item(Item.item(itemKey, finalNewItemModel, handSwap, oversizedInGui));
         Collection overlays = this.resourcePack.overlays();
         Iterable iterable2 = (Iterable)overlays;

         for(Object obj2 : iterable2) {
            Overlay overlay = (Overlay)obj2;
            int value5 = 0;
            Item item2 = overlay.item(itemKey);
            if (item2 != null) {
               Item overlayItem;
               block: {
                  overlayItem = item2;
                  handSwap = overlayItem.handAnimationOnSwap();
                  if (Intrinsics.areEqual(itemKey.value(), "player_head")) {
                     ItemTransform itemTransform2 = model.display().get(ItemTransform.Type.GUI);
                     if (!Intrinsics.areEqual(itemTransform2 != null ? itemTransform2.scale() : null, Vector3Float.ONE)) {
                        flag2 = true;
                        break block;
                     }
                  }

                  flag2 = overlayItem.oversizedInGui();
               }

               boolean oversizedInGui_54 = flag2;
               ItemModel itemModel2 = convertResources$lambda$0$mergeOverridesIntoItemModel(this, overrides, model, itemKey, overlayItem);
               if (itemModel2 != null) {
                  ItemModel mergedOverlayItem = itemModel2;
                  overlay.item(Item.item(itemKey, mergedOverlayItem, handSwap, oversizedInGui_54));
               }
            }
         }
      }

      models = this.resourcePack.models();
      iterable = (Iterable)models;

      for(Object obj3 : iterable) {
         Model model = (Model)obj3;
         int value6 = 0;
         Model model2 = VanillaResourcePack.INSTANCE.getResourcePack().model(model.key());
         if (model2 != null) {
            Model vanillaModel = model2;
            Key key2 = model.parent();
            Key parent = !Intrinsics.areEqual(key2, Model.BUILT_IN_ENTITY) ? key2 : null;
            model2 = model.toBuilder().overrides(CollectionsKt.emptyList()).parent(parent).build();
            Model newModel = model2;
            if (!Intrinsics.areEqual(newModel, model) || !Intrinsics.areEqual(newModel, vanillaModel)) {
               this.resourcePack.model(newModel);
            }

            Iterable iterable3 = (Iterable)model.overrides();
            if (CollectionsKt.count(iterable3) > 0) {
               block: {
                  List it = (List)iterable3;
                  int value7 = 0;
                  PackFormat formats = this.resourcePack.formats();
                  if (formats != null) {
                     FormatVersion formatVersion = formats.min();
                     if (formatVersion != null) {
                        value = formatVersion.major();
                        break block;
                     }
                  }

                  value = NexoOverlay.V1_21_1.getFormat();
               }

               int minFormat = value;
               if (minFormat <= NexoOverlay.V1_21_1.getFormat()) {
                  NexoOverlay.V1_21_1.overlay(this.resourcePack).model(model);
               }

               if (minFormat <= NexoOverlay.V1_21_3.getFormat()) {
                  NexoOverlay.V1_21_3.overlay(this.resourcePack).model(model);
               }
            }
         }
      }

      Overlay overlay2 = VanillaResourcePack.INSTANCE.getResourcePack().overlay("V26_1");
      if (overlay2 != null) {
         Overlay overlay = overlay2;
         capacity = 0;
         Collection items = overlay.items();
         Iterable iterable4 = (Iterable)items;

         for(Object obj4 : iterable4) {
            Item vanillaItem = (Item)obj4;
            int value8 = 0;
            Transformation transformation2 = vanillaItem.model().transformation();
            if (transformation2 != null) {
               Transformation transformation = transformation2;
               Item item3 = this.resourcePack.item(vanillaItem.key());
               if (item3 != null) {
                  Item existing = item3;
                  ItemModel itemModel3 = existing.model();
                  ItemModel remapped = this.withTransformation(itemModel3, transformation);
                  this.resourcePack.item(Item.item(existing.key(), remapped, existing.handAnimationOnSwap(), existing.oversizedInGui()));
                  Collection collection2 = this.resourcePack.overlays();
                  Iterable iterable5 = (Iterable)collection2;

                  for(Object obj5 : iterable5) {
                     Overlay rpOverlay = (Overlay)obj5;
                     int value9 = 0;
                     Item item4 = rpOverlay.item(vanillaItem.key());
                     if (item4 != null) {
                        Item overlayItem = item4;
                        itemModel3 = overlayItem.model();
                        ItemModel remappedOverlay = this.withTransformation(itemModel3, transformation);
                        rpOverlay.item(Item.item(overlayItem.key(), remappedOverlay, overlayItem.handAnimationOnSwap(), overlayItem.oversizedInGui()));
                     }
                  }
               }
            }
         }
      }

   }

   private final ItemModel withTransformation(ItemModel itemModel2, Transformation transformation) {
      ItemModel itemModel;
      if (itemModel2 instanceof ReferenceItemModel) {
         itemModel = ItemModel.reference(((ReferenceItemModel)itemModel2).model(), ((ReferenceItemModel)itemModel2).tints(), transformation);
         itemModel = itemModel;
      } else if (itemModel2 instanceof RangeDispatchItemModel) {
         itemModel = ItemModel.rangeDispatch(((RangeDispatchItemModel)itemModel2).property(), ((RangeDispatchItemModel)itemModel2).scale(), ((RangeDispatchItemModel)itemModel2).entries(), ((RangeDispatchItemModel)itemModel2).fallback(), transformation);
         itemModel = itemModel;
      } else if (itemModel2 instanceof SelectItemModel) {
         itemModel = ItemModel.select(((SelectItemModel)itemModel2).property(), ((SelectItemModel)itemModel2).cases(), ((SelectItemModel)itemModel2).fallback(), transformation);
         itemModel = itemModel;
      } else if (itemModel2 instanceof ConditionItemModel) {
         itemModel = ItemModel.conditional(((ConditionItemModel)itemModel2).condition(), ((ConditionItemModel)itemModel2).onTrue(), ((ConditionItemModel)itemModel2).onFalse(), transformation);
         itemModel = itemModel;
      } else if (itemModel2 instanceof CompositeItemModel) {
         itemModel = ItemModel.composite(((CompositeItemModel)itemModel2).models(), transformation);
         itemModel = itemModel;
      } else if (itemModel2 instanceof SpecialItemModel) {
         itemModel = ItemModel.special(((SpecialItemModel)itemModel2).render(), ((SpecialItemModel)itemModel2).base(), transformation);
         itemModel = itemModel;
      } else {
         itemModel = itemModel2;
      }

      return itemModel;
   }

   private final ItemModel modelObject(ItemModel baseItemModel, List<? extends ItemOverride> overrides, Key modelKey) {
        RangeDispatchItemModel.Builder builder;
        RangeDispatchItemModel.Builder builder2 = builder = ItemModel.rangeDispatch().property(ItemNumericProperty.customModelData());
        boolean bl = false;
        if (modelKey != null) {
            ItemModel itemModel = baseItemModel;
            if (itemModel == null) {
                ReferenceItemModel referenceItemModel = ItemModel.reference(modelKey);
                Intrinsics.checkNotNullExpressionValue((Object)referenceItemModel, "reference(...)");
                itemModel = referenceItemModel;
            }
            builder2.fallback(itemModel);
        }
        RangeDispatchItemModel rangeDispatchItemModel = builder.addEntries(this.modelEntries(overrides)).build();
        Intrinsics.checkNotNullExpressionValue((Object)rangeDispatchItemModel, "build(...)");
        return rangeDispatchItemModel;
    }

   private final List<RangeDispatchItemModel.Entry> modelEntries(List<? extends ItemOverride> overrides) {
      Iterable iterable = (Iterable)overrides;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         ItemOverride override = (ItemOverride)element;
         int value2 = 0;
         List predicate = override.predicate();
         Float customModelData = this.getCustomModelData(predicate);
         RangeDispatchItemModel.Entry entry = customModelData != null ? RangeDispatchItemModel.Entry.entry(customModelData, ItemModel.reference(override.model())) : null;
         if (entry != null) {
            Object it = entry;
            int value3 = 0;
            destination.add(it);
         }
      }

      return (List)destination;
   }

   private final Float getCustomModelData(List<? extends ItemPredicate> list) {
      Iterable iterable = (Iterable)list;
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value = 0;
            if (!Intrinsics.areEqual(it.name(), "custom_model_data")) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      return StringsKt.toFloatOrNull(String.valueOf((ItemPredicate)obj != null ? ((ItemPredicate)obj).value() : null));
   }

   private final Float getPull(List<? extends ItemPredicate> list) {
      Iterable iterable = (Iterable)list;
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value = 0;
            if (!Intrinsics.areEqual(it.name(), "pull")) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      return StringsKt.toFloatOrNull(String.valueOf((ItemPredicate)obj != null ? ((ItemPredicate)obj).value() : null));
   }

   private final boolean getPulling(ItemOverride itemOverride) {
      List predicate = itemOverride.predicate();
      Iterable iterable = (Iterable)predicate;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value = 0;
            if (Intrinsics.areEqual(it.name(), "pulling")) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private final SelectItemModel.Case getCharged(ItemOverride itemOverride2) {
      int value = 0;
      List predicate = itemOverride2.predicate();
      Iterable iterable = (Iterable)predicate;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value2 = 0;
            if (Intrinsics.areEqual(it.name(), "charged")) {
               flag = true;
               break;
            }
         }
      }

      ItemOverride itemOverride = flag ? itemOverride2 : null;
      SelectItemModel.Case caseValue2;
      if (itemOverride != null) {
         Key model = itemOverride.model();
         if (model != null) {
            Key it = model;
            int value3 = 0;
            ItemModel itemModel = ItemModel.reference(it);
            JsonElement[] jsonElements = new JsonElement[]{new JsonPrimitive("arrow")};
            caseValue2 = SelectItemModel.Case._case(itemModel, jsonElements);
            return caseValue2;
         }
      }

      caseValue2 = null;
      return caseValue2;
   }

   private final SelectItemModel.Case getFirework(ItemOverride itemOverride2) {
      int value = 0;
      List predicate = itemOverride2.predicate();
      Iterable iterable = (Iterable)predicate;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value2 = 0;
            if (Intrinsics.areEqual(it.name(), "firework")) {
               flag = true;
               break;
            }
         }
      }

      ItemOverride itemOverride = flag ? itemOverride2 : null;
      SelectItemModel.Case caseValue2;
      if (itemOverride != null) {
         Key model = itemOverride.model();
         if (model != null) {
            Key it = model;
            int value3 = 0;
            ItemModel itemModel = ItemModel.reference(it);
            JsonElement[] jsonElements = new JsonElement[]{new JsonPrimitive("rocket")};
            caseValue2 = SelectItemModel.Case._case(itemModel, jsonElements);
            return caseValue2;
         }
      }

      caseValue2 = null;
      return caseValue2;
   }

   private final boolean getBlocking(ItemOverride itemOverride) {
      List predicate = itemOverride.predicate();
      Iterable iterable = (Iterable)predicate;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value = 0;
            if (Intrinsics.areEqual(it.name(), "blocking")) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private final boolean getCast(ItemOverride itemOverride) {
      List predicate = itemOverride.predicate();
      Iterable iterable = (Iterable)predicate;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemPredicate it = (ItemPredicate)element;
            int value = 0;
            if (Intrinsics.areEqual(it.name(), "cast")) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   @NotNull
   public final Object2ObjectOpenHashMap<Key, Item> getStandardItemModels() {
      Lazy lazy = this.standardItemModels$delegate;
      return (Object2ObjectOpenHashMap)lazy.getValue();
   }

   @NotNull
   public final Object2ObjectOpenHashMap<Key, Model> getStandardModels() {
      Lazy lazy = this.standardModels$delegate;
      return (Object2ObjectOpenHashMap)lazy.getValue();
   }

   private static final ItemModel convertResources$lambda$0$mergeOverridesIntoItemModel(ModernVersionPatcher modernVersionPatcher, List<? extends ItemOverride> overrides, Model model, Key itemKey, Item item) {
      ItemModel itemModel = item.model();
      Function1 function1 = modernVersionPatcher.simpleItemModelPredicate;
      ItemModel itemModel2 = !(Boolean)function1.invoke(itemModel) ? itemModel : null;
      if (itemModel2 == null) {
         return null;
      } else {
         ItemModel baseItemModel = itemModel2;
         Transformation transformation = itemModel2.transformation();
         ItemModel itemModel3;
         if (baseItemModel instanceof RangeDispatchItemModel) {
            ItemModel itemModel4 = ((RangeDispatchItemModel)baseItemModel).fallback();
            itemModel3 = itemModel4;
            if (itemModel4 == null) {
               itemModel3 = baseItemModel;
            }

            ItemModel fallback = itemModel3;
            List newBase = ((RangeDispatchItemModel)baseItemModel).entries();
            Collection collection = (Collection)newBase;
            Iterable iterable = (Iterable)overrides;
            Collection collection2 = collection;
            Collection destination = (Collection)(new ArrayList());

            for(Object element : iterable) {
               int value43 = 0;
               ItemOverride it = (ItemOverride)element;
               int key = 0;
               ItemModel itemModel5;
               if (fallback instanceof SpecialItemModel && ((SpecialItemModel)fallback).render() instanceof HeadSpecialRender) {
                  itemModel5 = ItemModel.special(((SpecialItemModel)fallback).render(), it.model());
                  itemModel5 = itemModel5;
               } else {
                  ReferenceItemModel referenceItemModel = ItemModel.reference(it.model(), (List)modernVersionPatcher.DEFAULT_TINT);
                  itemModel5 = referenceItemModel;
               }

               ItemModel model_47 = itemModel5;
               List predicate = it.predicate();
               Float customModelData = modernVersionPatcher.getCustomModelData(predicate);
               RangeDispatchItemModel.Entry entry = customModelData != null ? RangeDispatchItemModel.Entry.entry(customModelData, model_47) : null;
               if (entry != null) {
                  Object obj9 = entry;
                  int value = 0;
                  destination.add(obj9);
               }
            }

            List list = (List)destination;
            iterable = (Iterable)CollectionsKt.plus(collection2, (Iterable)list);
            HashSet hashSet = new HashSet();
            ArrayList arrayList = new ArrayList();

            for(Object obj10 : iterable) {
               RangeDispatchItemModel.Entry it = (RangeDispatchItemModel.Entry)obj10;
               int value44 = 0;
               Object threshold = it.threshold();
               if (hashSet.add(threshold)) {
                  arrayList.add(obj10);
               }
            }

            List entries = (List)arrayList;
            itemModel3 = ItemModel.rangeDispatch(ItemNumericProperty.customModelData(), 1.0F, entries, fallback, transformation);
         } else if (baseItemModel instanceof SelectItemModel) {
            ItemNumericProperty itemNumericProperty = ItemNumericProperty.customModelData();
            Iterable iterable2 = (Iterable)overrides;
            float value2 = 1.0F;
            ItemNumericProperty itemNumericProperty2 = itemNumericProperty;
            Collection collection11 = (Collection)(new ArrayList());

            for(Object obj11 : iterable2) {
               int value3 = 0;
               ItemOverride override = (ItemOverride)obj11;
               int value4 = 0;
               ItemModel itemModel6;
               if (baseItemModel instanceof SpecialItemModel && ((SpecialItemModel)baseItemModel).render() instanceof HeadSpecialRender) {
                  itemModel6 = ItemModel.special(((SpecialItemModel)baseItemModel).render(), override.model());
                  itemModel6 = itemModel6;
               } else {
                  ReferenceItemModel referenceItemModel2 = ItemModel.reference(override.model());
                  itemModel6 = referenceItemModel2;
               }

               ItemModel model_46 = itemModel6;
               List list2 = override.predicate();
               Float number = modernVersionPatcher.getCustomModelData(list2);
               RangeDispatchItemModel.Entry entry2 = number != null ? RangeDispatchItemModel.Entry.entry(number, model_46) : null;
               if (entry2 != null) {
                  Object obj12 = entry2;
                  int value5 = 0;
                  collection11.add(obj12);
               }
            }

            List list3 = (List)collection11;
            itemModel3 = ItemModel.rangeDispatch(itemNumericProperty2, value2, list3, baseItemModel, transformation);
         } else if (baseItemModel instanceof ConditionItemModel) {
            team.unnamed.creative.item.SelectItemModel.Case caseValue2;
            ItemModel itemModel7;
            java.lang.Object obj;
            java.lang.Object obj2;
            team.unnamed.creative.item.SelectItemModel.Case caseValue3;
            Iterable iterable3 = (Iterable)overrides;
            Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();

            for(Object obj13 : iterable3) {
               ItemOverride it = (ItemOverride)obj13;
               int value6 = 0;
               List list4 = it.predicate();
               Float number2 = modernVersionPatcher.getCustomModelData(list4);
               if (number2 != null) {
                  Float number6 = number2;
                  float it_45 = ((Number)number6).floatValue();
                  int value7 = 0;
                  number2 = it_45 != 0.0F ? number6 : null;
               } else {
                  number2 = null;
               }

               Object obj14 = number2;
               ((ObjectArrayList)map.computeIfAbsent(obj14, (java.util.function.Function)(k -> new ObjectArrayList()))).add(obj13);
            }

            Object2ObjectLinkedOpenHashMap grouped = map;
            int value8 = 0;
            String asString = itemKey.asString();
            Pair pair;
            if (StringsKt.endsWith(asString, "bow", false)) {
               Collection collection3 = grouped.values();
               Iterable iterable4 = (Iterable)collection3;
               Collection collection12 = (Collection)(new ArrayList());
               int value45 = 0;

               for(Object obj15 : iterable4) {
                  ObjectArrayList it = (ObjectArrayList)obj15;
                  int value46 = 0;
                  Iterable iterable5 = (Iterable)it;
                  Collection collection13 = (Collection)(new ArrayList());

                  for(Object obj16 : iterable5) {
                     ItemOverride it_42 = (ItemOverride)obj16;
                     if (modernVersionPatcher.getPulling(it_42)) {
                        collection13.add(obj16);
                     }
                  }

                  Iterable iterable6 = (Iterable)((List)collection13);
                  CollectionsKt.addAll(collection13, iterable6);
               }

               List list5 = (List)collection12;
               Collection collection4 = grouped.values();
               iterable4 = (Iterable)collection4;
               List pullingEntries = list5;
               collection12 = (Collection)(new ArrayList());
               value45 = 0;

               for(Object obj17 : iterable4) {
                  ObjectArrayList it = (ObjectArrayList)obj17;
                  int value47 = 0;
                  Iterable iterable7 = (Iterable)it;
                  Collection collection14 = (Collection)(new ArrayList());

                  for(Object obj18 : iterable7) {
                     ItemOverride it_38 = (ItemOverride)obj18;
                     if (!modernVersionPatcher.getPulling(it_38)) {
                        collection14.add(obj18);
                     }
                  }

                  Iterable iterable8 = (Iterable)((List)collection14);
                  CollectionsKt.addAll(collection14, iterable8);
               }

               pair = TuplesKt.to(pullingEntries, (List)collection12);
            } else {
               String str = itemKey.asString();
               if (StringsKt.endsWith(str, "shield", false)) {
                  Collection collection5 = grouped.values();
                  Iterable iterable9 = (Iterable)collection5;
                  Collection collection15 = (Collection)(new ArrayList());

                  for(Object obj19 : iterable9) {
                     int value9 = 0;
                     ObjectArrayList it = (ObjectArrayList)obj19;
                     int value10 = 0;
                     Iterable iterable10 = (Iterable)it;
                     Iterator iterator2 = iterable10.iterator();

                     ItemOverride itemOverride;
                     while(true) {
                        if (!iterator2.hasNext()) {
                           itemOverride = null;
                           break;
                        }

                        Object obj20 = iterator2.next();
                        ItemOverride it_36 = (ItemOverride)obj20;
                        int value11 = 0;
                        if (modernVersionPatcher.getBlocking(it_36)) {
                           itemOverride = (ItemOverride)obj20;
                           break;
                        }
                     }

                     ItemOverride itemOverride2 = itemOverride;
                     if (itemOverride2 != null) {
                        Object obj21 = itemOverride2;
                        int value12 = 0;
                        collection15.add(obj21);
                     }
                  }

                  List list6 = (List)collection15;
                  Collection collection6 = grouped.values();
                  iterable9 = (Iterable)collection6;
                  List pullingEntries = list6;
                  collection15 = (Collection)(new ArrayList());

                  for(Object obj22 : iterable9) {
                     int value13 = 0;
                     ObjectArrayList it = (ObjectArrayList)obj22;
                     int value14 = 0;
                     Iterable iterable11 = (Iterable)it;

                     block: {
                        for(Object obj23 : iterable11) {
                           ItemOverride it_35 = (ItemOverride)obj23;
                           int value15 = 0;
                           if (!modernVersionPatcher.getBlocking(it_35)) {
                              obj = obj23;
                              break block;
                           }
                        }

                        obj = null;
                     }

                     ItemOverride itemOverride3 = (ItemOverride)obj;
                     if (itemOverride3 != null) {
                        Object obj24 = itemOverride3;
                        int value16 = 0;
                        collection15.add(obj24);
                     }
                  }

                  pair = TuplesKt.to(pullingEntries, (List)collection15);
               } else {
                  str = itemKey.asString();
                  if (StringsKt.endsWith(str, "fishing_rod", false)) {
                     Collection collection7 = grouped.values();
                     Iterable iterable12 = (Iterable)collection7;
                     Collection collection16 = (Collection)(new ArrayList());

                     for(Object obj25 : iterable12) {
                        int value17 = 0;
                        ObjectArrayList it = (ObjectArrayList)obj25;
                        int value18 = 0;
                        Iterable iterable13 = (Iterable)it;
                        Iterator iterator3 = iterable13.iterator();

                        ItemOverride itemOverride4;
                        while(true) {
                           if (!iterator3.hasNext()) {
                              itemOverride4 = null;
                              break;
                           }

                           Object obj26 = iterator3.next();
                           ItemOverride it_34 = (ItemOverride)obj26;
                           int value19 = 0;
                           if (modernVersionPatcher.getCast(it_34)) {
                              itemOverride4 = (ItemOverride)obj26;
                              break;
                           }
                        }

                        ItemOverride itemOverride5 = itemOverride4;
                        if (itemOverride5 != null) {
                           Object obj27 = itemOverride5;
                           int value20 = 0;
                           collection16.add(obj27);
                        }
                     }

                     List list7 = (List)collection16;
                     Collection collection8 = grouped.values();
                     iterable12 = (Iterable)collection8;
                     List pullingEntries = list7;
                     collection16 = (Collection)(new ArrayList());

                     for(Object obj28 : iterable12) {
                        int value21 = 0;
                        ObjectArrayList it = (ObjectArrayList)obj28;
                        int value22 = 0;
                        Iterable iterable14 = (Iterable)it;

                        block: {
                           for(Object obj29 : iterable14) {
                              ItemOverride it_33 = (ItemOverride)obj29;
                              int value23 = 0;
                              if (!modernVersionPatcher.getCast(it_33)) {
                                 obj2 = obj29;
                                 break block;
                              }
                           }

                           obj2 = null;
                        }

                        ItemOverride itemOverride6 = (ItemOverride)obj2;
                        if (itemOverride6 != null) {
                           Object obj30 = itemOverride6;
                           int value24 = 0;
                           collection16.add(obj30);
                        }
                     }

                     pair = TuplesKt.to(pullingEntries, (List)collection16);
                  } else {
                     Collection collection9 = grouped.values();
                     Iterable iterable15 = (Iterable)collection9;
                     Collection collection17 = (Collection)(new ArrayList());

                     for(Object obj31 : iterable15) {
                        ObjectArrayList it = (ObjectArrayList)obj31;
                        ItemOverride itemOverride7 = (ItemOverride)CollectionsKt.firstOrNull((List)it);
                        if (itemOverride7 != null) {
                           Object obj32 = itemOverride7;
                           collection17.add(obj32);
                        }
                     }

                     List list8 = (List)collection17;
                     Collection collection10 = grouped.values();
                     iterable15 = (Iterable)collection10;
                     List pullingEntries = list8;
                     collection17 = (Collection)(new ArrayList());

                     for(Object obj33 : iterable15) {
                        ObjectArrayList it = (ObjectArrayList)obj33;
                        ItemOverride itemOverride8 = (ItemOverride)CollectionsKt.lastOrNull((List)it);
                        if (itemOverride8 != null) {
                           Object obj34 = itemOverride8;
                           collection17.add(obj34);
                        }
                     }

                     pair = TuplesKt.to(pullingEntries, (List)collection17);
                  }
               }
            }

            Pair defaultDisplay = pair;
            List trueOverrides = (List)defaultDisplay.component1();
            List falseOverrides = (List)defaultDisplay.component2();
            Iterable iterable16 = (Iterable)trueOverrides;
            Map map2 = (Map)(new LinkedHashMap());

            for(Object obj35 : iterable16) {
               ItemOverride it = (ItemOverride)obj35;
               int value25 = 0;
               List list9 = it.predicate();
               Object obj36 = modernVersionPatcher.getCustomModelData(list9);
               Object obj37 = map2.get(obj36);
               Object obj3;
               if (obj37 == null) {
                  int overrides_30 = 0;
                  Object answer = (List)(new ArrayList());
                  map2.put(obj36, answer);
                  obj3 = answer;
               } else {
                  obj3 = obj37;
               }

               List list16 = (List)obj3;
               list16.add(obj35);
            }

            Map map3 = map2;
            Collection collection18 = (Collection)(new ArrayList());

            for(Map.Entry entry6 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
               Object obj4;
               block: {
                  int value48 = 0;
                  int value26 = 0;
                  Float cmd = (Float)entry6.getKey();
                  List overrides_28 = (List)entry6.getValue();
                  ItemOverride itemOverride9 = (ItemOverride)CollectionsKt.firstOrNull(overrides_28);
                  if (itemOverride9 != null) {
                     ItemOverride it = itemOverride9;
                     int value27 = 0;
                     ReferenceItemModel referenceItemModel3 = ItemModel.reference(it.model());
                     if (referenceItemModel3 != null) {
                        ReferenceItemModel baseOverrideModel = referenceItemModel3;
                        Float pullingEntries = 1.0F;
                        float it_27 = ((Number)pullingEntries).floatValue();
                        value27 = 0;
                        Float number3 = !Intrinsics.areEqual(itemKey.asMinimalString(), "bow") ? pullingEntries : null;
                        float scale = number3 != null ? number3 : 0.05F;
                        Iterable iterable17 = (Iterable)CollectionsKt.drop((Iterable)overrides_28, 1);
                        value27 = 0;
                        Collection collection19 = (Collection)(new ArrayList());

                        for(Object obj38 : iterable17) {
                           int value28 = 0;
                           ItemOverride it_23 = (ItemOverride)obj38;
                           int value29 = 0;
                           List list10 = it_23.predicate();
                           number3 = modernVersionPatcher.getPull(list10);
                           RangeDispatchItemModel.Entry entry3 = number3 != null ? RangeDispatchItemModel.Entry.entry(number3, ItemModel.reference(it_23.model())) : null;
                           if (entry3 != null) {
                              Object obj39 = entry3;
                              int value30 = 0;
                              collection19.add(obj39);
                           }
                        }

                        block2: {
                           List it_22 = (List)collection19;
                           int value49 = 0;
                           List pullingEntries_21 = !it_22.isEmpty() ? it_22 : null;
                           if (pullingEntries_21 != null) {
                              int value31 = 0;
                              String str2 = itemKey.asString();
                              ItemNumericProperty itemNumericProperty3;
                              if (StringsKt.contains((CharSequence)str2, (CharSequence)"crossbow", false)) {
                                 itemNumericProperty3 = ItemNumericProperty.crossbowPull();
                                 itemNumericProperty3 = itemNumericProperty3;
                              } else {
                                 UseDurationItemNumericProperty useDuration = ItemNumericProperty.useDuration();
                                 itemNumericProperty3 = useDuration;
                              }

                              ItemNumericProperty property = itemNumericProperty3;
                              RangeDispatchItemModel it_20 = ItemModel.rangeDispatch(property, scale, pullingEntries_21, baseOverrideModel);
                              if (it_20 != null) {
                                 itemModel7 = it_20;
                                 break block2;
                              }
                           }

                           itemModel7 = baseOverrideModel;
                        }

                        ItemModel finalModel = itemModel7;
                        obj4 = cmd != null ? RangeDispatchItemModel.Entry.entry(cmd, finalModel) : null;
                        break block;
                     }
                  }

                  obj4 = null;
               }

               if (obj4 != null) {
                  Object obj40 = obj4;
                  int value32 = 0;
                  collection18.add(obj40);
               }
            }

            List onTrueCmdEntries = (List)collection18;
            Iterable iterable18 = (Iterable)falseOverrides;
            Map map4 = (Map)(new LinkedHashMap());

            for(Object obj41 : iterable18) {
               ItemOverride it = (ItemOverride)obj41;
               int value50 = 0;
               List list11 = it.predicate();
               Object obj42 = modernVersionPatcher.getCustomModelData(list11);
               Object obj43 = map4.get(obj42);
               Object obj5;
               if (obj43 == null) {
                  int value33 = 0;
                  Object obj44 = (List)(new ArrayList());
                  map4.put(obj42, obj44);
                  obj5 = obj44;
               } else {
                  obj5 = obj43;
               }

               List list17 = (List)obj5;
               list17.add(value50);
            }

            Map map5 = map4;
            Collection collection20 = (Collection)(new ArrayList());

            for(Map.Entry entry7 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map5.entrySet()) {
               Object obj6;
               block: {
                  int it = 0;
                  int value34 = 0;
                  Float cmd = (Float)entry7.getKey();
                  List overrides_12 = (List)entry7.getValue();
                  ItemOverride itemOverride10 = (ItemOverride)CollectionsKt.firstOrNull(overrides_12);
                  if (itemOverride10 != null) {
                     ItemOverride it_11 = itemOverride10;
                     int value35 = 0;
                     ReferenceItemModel referenceItemModel4 = ItemModel.reference(it_11.model());
                     if (referenceItemModel4 != null) {
                        ReferenceItemModel baseOverrideModel = referenceItemModel4;
                        Iterator finalModel = ((Iterable)overrides_12).iterator();

                        while(true) {
                           if (finalModel.hasNext()) {
                              it_11 = (ItemOverride)finalModel.next();
                              value35 = 0;
                              SelectItemModel.Case it_10 = modernVersionPatcher.getFirework(it_11);
                              if (it_10 == null) {
                                 continue;
                              }

                              caseValue2 = it_10;
                              break;
                           }

                           caseValue2 = null;
                           break;
                        }

                        SelectItemModel.Case firework = caseValue2;
                        Iterator iterator = ((Iterable)overrides_12).iterator();

                        while(true) {
                           if (iterator.hasNext()) {
                              ItemOverride it_9 = (ItemOverride)iterator.next();
                              int value36 = 0;
                              SelectItemModel.Case caseValue4 = modernVersionPatcher.getCharged(it_9);
                              if (caseValue4 == null) {
                                 continue;
                              }

                              caseValue3 = caseValue4;
                              break;
                           }

                           caseValue3 = null;
                           break;
                        }

                        SelectItemModel.Case charged = caseValue3;
                        ItemModel itemModel8;
                        if (charged == null && firework == null) {
                           itemModel8 = baseOverrideModel;
                        } else {
                           SelectItemModel.Builder builder = ItemModel.select().property(ItemStringProperty.chargeType()).fallback(baseOverrideModel);
                           int value51 = 0;
                           if (charged != null) {
                              int value52 = 0;
                              builder.addCase(charged);
                           } else {
                              Object obj7 = null;
                           }

                           if (firework != null) {
                              int value37 = 0;
                              builder.addCase(firework);
                           } else {
                              Object obj8 = null;
                           }

                           itemModel8 = builder.build();
                           itemModel8 = itemModel8;
                        }

                        ItemModel finalModel_7 = itemModel8;
                        obj6 = cmd != null ? RangeDispatchItemModel.Entry.entry(cmd, finalModel_7) : null;
                        break block;
                     }
                  }

                  obj6 = null;
               }

               if (obj6 != null) {
                  Object obj45 = obj6;
                  int value38 = 0;
                  collection20.add(obj45);
               }
            }

            List onFalseCmdEntries = (List)collection20;
            RangeDispatchItemModel rangeDispatchItemModel = ItemModel.rangeDispatch(ItemNumericProperty.customModelData(), 1.0F, onTrueCmdEntries, ((ConditionItemModel)baseItemModel).onTrue(), transformation);
            RangeDispatchItemModel rangeDispatchItemModel2 = ItemModel.rangeDispatch(ItemNumericProperty.customModelData(), 1.0F, onFalseCmdEntries, ((ConditionItemModel)baseItemModel).onFalse(), transformation);
            itemModel3 = ItemModel.conditional(((ConditionItemModel)baseItemModel).condition(), rangeDispatchItemModel2, rangeDispatchItemModel2);
         } else if (baseItemModel instanceof ReferenceItemModel) {
            ItemNumericProperty itemNumericProperty4 = ItemNumericProperty.customModelData();
            Iterable iterable19 = (Iterable)overrides;
            float value39 = 1.0F;
            ItemNumericProperty itemNumericProperty5 = itemNumericProperty4;
            Collection collection21 = (Collection)(new ArrayList());

            for(Object obj46 : iterable19) {
               int value40 = 0;
               ItemOverride override = (ItemOverride)obj46;
               int value53 = 0;
               List list12 = override.predicate();
               Float number4 = modernVersionPatcher.getCustomModelData(list12);
               RangeDispatchItemModel.Entry entry4;
               if (number4 != null) {
                  float cmd = number4;
                  entry4 = RangeDispatchItemModel.Entry.entry(cmd, ItemModel.reference(override.model(), ((ReferenceItemModel)baseItemModel).tints()));
               } else {
                  entry4 = null;
               }

               if (entry4 != null) {
                  Object obj47 = entry4;
                  int it = 0;
                  collection21.add(obj47);
               }
            }

            List list13 = (List)collection21;
            itemModel3 = ItemModel.rangeDispatch(itemNumericProperty5, value39, list13, baseItemModel, transformation);
         } else if (baseItemModel instanceof SpecialItemModel) {
            DisplayProperties displayProperties = DisplayProperties.INSTANCE;
            Map defaultDisplay = displayProperties.fromModel(model);
            SpecialItemModel specialItemModel = ItemModel.special(((SpecialItemModel)baseItemModel).render(), !Intrinsics.areEqual(model.display(), defaultDisplay) ? model.key() : ((SpecialItemModel)baseItemModel).base(), transformation);
            if (!((Collection)overrides).isEmpty()) {
               ItemNumericProperty itemNumericProperty6 = ItemNumericProperty.customModelData();
               Iterable iterable20 = (Iterable)overrides;
               float value41 = 1.0F;
               ItemNumericProperty itemNumericProperty7 = itemNumericProperty6;
               Collection collection22 = (Collection)(new ArrayList());

               for(Object obj48 : iterable20) {
                  int value54 = 0;
                  ItemOverride override = (ItemOverride)obj48;
                  int value55 = 0;
                  List list14 = override.predicate();
                  Float number5 = modernVersionPatcher.getCustomModelData(list14);
                  RangeDispatchItemModel.Entry entry5;
                  if (number5 != null) {
                     float cmd = number5;
                     entry5 = RangeDispatchItemModel.Entry.entry(cmd, ItemModel.special(((SpecialItemModel)baseItemModel).render(), override.model()));
                  } else {
                     entry5 = null;
                  }

                  if (entry5 != null) {
                     Object obj49 = entry5;
                     int value42 = 0;
                     collection22.add(obj49);
                  }
               }

               List list15 = (List)collection22;
               RangeDispatchItemModel newBase = ItemModel.rangeDispatch(itemNumericProperty7, value41, list15, specialItemModel, transformation);
               itemModel3 = newBase;
            } else {
               itemModel3 = specialItemModel;
            }
         } else {
            itemModel3 = baseItemModel;
         }

         return itemModel3;
      }
   }

   private static final Object2ObjectOpenHashMap standardItemModels_delegate$lambda$0(ModernVersionPatcher modernVersionPatcher) {
      Object obj;
      try {
         int value = 0;
         NexoPackReader nexoPackReader = NexoPackReader.INSTANCE;
         File[] listFiles = PackGenerator.Companion.getExternalPacks().listFiles();
         File[] files = listFiles;
         NexoPackReader nexoPackReader2 = nexoPackReader;
         int value2 = 0;
         int value3 = files.length;

         while(true) {
            if (value2 >= value3) {
               throw new NoSuchElementException("Array contains no element matching the predicate.");
            }

            Object element = files[value2];
            int value4 = 0;
            String name = ((File)element).getName();
            if (StringsKt.startsWith(name, "RequiredPack_", false)) {
               obj = ResultShim.ok(nexoPackReader2.readFile((File)element, (PackFormat)null));
               break;
            }

            ++value2;
         }
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      ResourcePack requiredPack = (ResourcePack)(ResultShim.isFailure(obj) ? resourcePack2 : obj);
      requiredPack.removeItem(Key.key("player_head"));
      Collection items = requiredPack.items();
      Iterable iterable = (Iterable)items;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object obj2 : iterable) {
         Item it = (Item)obj2;
         Item it_4 = it;
         if (Intrinsics.areEqual(it_4.key().namespace(), "minecraft")) {
            destination.add(obj2);
         }
      }

      ObjectArrayList requiredItems = (ObjectArrayList)destination;
      items = VanillaResourcePack.INSTANCE.getResourcePack().items();
      iterable = (Iterable)CollectionsKt.plus(items, (Iterable)requiredItems);
      Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map map = (Map)result;

      for(Object obj3 : iterable) {
         Item item = (Item)obj3;
         int value5 = 0;
         map.put(item.key(), obj3);
      }

      return (Object2ObjectOpenHashMap)map;
   }

   private static final Object2ObjectOpenHashMap standardModels_delegate$lambda$0(ModernVersionPatcher modernVersionPatcher) {
      Object obj;
      try {
         int value = 0;
         NexoPackReader nexoPackReader = NexoPackReader.INSTANCE;
         File[] listFiles = PackGenerator.Companion.getExternalPacks().listFiles();
         File[] files = listFiles;
         NexoPackReader nexoPackReader2 = nexoPackReader;
         int value2 = 0;
         int value3 = files.length;

         while(true) {
            if (value2 >= value3) {
               throw new NoSuchElementException("Array contains no element matching the predicate.");
            }

            Object element = files[value2];
            int value4 = 0;
            String name = ((File)element).getName();
            if (StringsKt.startsWith(name, "RequiredPack_", false)) {
               obj = ResultShim.ok(nexoPackReader2.readFile((File)element, (PackFormat)null));
               break;
            }

            ++value2;
         }
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      ResourcePack requiredPack = (ResourcePack)(ResultShim.isFailure(obj) ? resourcePack2 : obj);
      requiredPack.removeItem(Key.key("player_head"));
      Collection models = requiredPack.models();
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object obj2 : iterable) {
         Model it = (Model)obj2;
         Model it_2 = it;
         if (Intrinsics.areEqual(it_2.key().namespace(), "minecraft")) {
            destination.add(obj2);
         }
      }

      ObjectArrayList requiredModels = (ObjectArrayList)destination;
      models = VanillaResourcePack.INSTANCE.getResourcePack().models();
      iterable = (Iterable)CollectionsKt.plus(models, (Iterable)requiredModels);
      Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map map = (Map)result;

      for(Object obj3 : iterable) {
         Model model = (Model)obj3;
         int value5 = 0;
         map.put(model.key(), obj3);
      }

      return (Object2ObjectOpenHashMap)map;
   }

   private static final boolean simpleItemModelPredicate$lambda$0(ItemModel item) {
      return item instanceof ReferenceItemModel && ((ReferenceItemModel)item).tints().isEmpty();
   }

   private static final Key itemKeyPredicate$lambda$0(Model model) {
      String asString = model.key().asString();
      return Key.key(StringUtilsKt.remove(StringUtilsKt.remove(asString, "block/"), "item/"));
   }
}

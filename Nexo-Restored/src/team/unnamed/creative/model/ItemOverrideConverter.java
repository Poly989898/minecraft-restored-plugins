package team.unnamed.creative.model;

import com.google.gson.JsonPrimitive;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;

public final class ItemOverrideConverter {
   public static void patchPack(ResourcePack resourcePack) {
      for(Model model : resourcePack.models()) {
         if (model.key().namespace().equals("minecraft") && (model.key().value().startsWith("block/") || model.key().value().startsWith("item/"))) {
            Key itemKey = Key.key(model.key().namespace(), model.key().value().replace("block/", "").replace("item/", ""));
            Item standardItem = resourcePack.item(itemKey);
            if (standardItem != null && standardItem.model() instanceof ReferenceItemModel) {
               standardItem = null;
            }

            if (standardItem == null) {
               RangeDispatchItemModel.Builder builder = ItemModel.rangeDispatch().property(ItemNumericProperty.customModelData());
               builder.scale(1.0F).fallback(ItemModel.reference(model.key()));

               for(ItemOverride override : model.overrides()) {
                  Integer cmd = getCustomModelData(override);
                  if (cmd != null) {
                     builder.addEntry((float)cmd, ItemModel.reference(override.model()));
                  }
               }

               resourcePack.item(Item.item(itemKey, builder.build()));
            } else {
               String modelValue = model.key().value();
               if (!modelValue.endsWith("crossbow") && !modelValue.endsWith("bow") && !modelValue.endsWith("player_head") && !modelValue.endsWith("shulker_box")) {
                  ItemModel override = standardItem.model();
                  if (override instanceof SelectItemModel) {
                     SelectItemModel selectModel = (SelectItemModel)override;
                     SelectItemModel.Builder builder = ItemModel.select();
                     builder.property(ItemStringProperty.customModelData()).fallback(selectModel);

                     for(ItemOverride itemOverride : model.overrides()) {
                        Integer cmd = getCustomModelData(itemOverride);
                        if (cmd != null) {
                           builder.addCase(ItemModel.reference(itemOverride.model()), new JsonPrimitive(cmd));
                        }
                     }

                     resourcePack.item(Item.item(itemKey, builder.build()));
                  }

                  override = standardItem.model();
                  if (override instanceof ConditionItemModel) {
                     ConditionItemModel conditionItemModel = (ConditionItemModel)override;
                     Map<Integer, List<ItemOverride>> grouped = (Map)model.overrides().stream().filter((o) -> getCustomModelData(o) != 0).collect(Collectors.groupingBy(ItemOverrideConverter::getCustomModelData));
                     List<ItemOverride> trueOverrides = grouped.values().stream().map(List::getLast).toList();
                     RangeDispatchItemModel.Builder trueBuilder = ItemModel.rangeDispatch();
                     List<ItemOverride> falseOverrides = grouped.values().stream().map(List::getFirst).toList();
                     RangeDispatchItemModel.Builder falseBuilder = ItemModel.rangeDispatch();
                     trueBuilder.addEntry(RangeDispatchItemModel.Entry.entry(0.0F, conditionItemModel.onTrue()));
                     falseBuilder.addEntry(RangeDispatchItemModel.Entry.entry(0.0F, conditionItemModel.onFalse()));

                     for(ItemOverride ov : trueOverrides) {
                        Integer cmd = getCustomModelData(ov);
                        if (cmd != null) {
                           trueBuilder.addEntry((float)cmd, ItemModel.reference(ov.model()));
                        }
                     }

                     for(ItemOverride ov : falseOverrides) {
                        Integer cmd = getCustomModelData(ov);
                        if (cmd != null) {
                           falseBuilder.addEntry((float)cmd, ItemModel.reference(ov.model()));
                        }
                     }

                     conditionItemModel = ItemModel.conditional(conditionItemModel.condition(), trueBuilder.build(), falseBuilder.build());
                     resourcePack.item(Item.item(itemKey, conditionItemModel));
                  }

                  override = standardItem.model();
                  if (override instanceof ReferenceItemModel) {
                     ReferenceItemModel referenceItemModel = (ReferenceItemModel)override;
                     RangeDispatchItemModel.Builder builder = ItemModel.rangeDispatch();
                     builder.fallback(standardItem.model()).property(ItemNumericProperty.customModelData());

                     for(ItemOverride ov : model.overrides()) {
                        Integer cmd = getCustomModelData(ov);
                        if (cmd != null) {
                           builder.addEntry((float)cmd, ItemModel.reference(ov.model(), referenceItemModel.tints()));
                        }
                     }

                     resourcePack.item(Item.item(itemKey, builder.build()));
                  }
               }
            }
         }
      }

   }

   private static Integer getCustomModelData(ItemOverride override) {
      return (Integer)override.predicate().stream().filter((p) -> p.name().equals("custom_model_data")).findFirst().map((p) -> Integer.parseInt(p.value().toString())).orElse(null);
   }
}

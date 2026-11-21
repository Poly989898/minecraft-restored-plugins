package team.unnamed.creative.item;

import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.item.property.ItemBooleanProperty;

public interface ConditionItemModel extends ItemModel {
   @NotNull ItemBooleanProperty condition();

   @NotNull ItemModel onTrue();

   @NotNull ItemModel onFalse();
}

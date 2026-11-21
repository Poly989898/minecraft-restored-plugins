package team.unnamed.creative.item;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface CompositeItemModel extends ItemModel {
   @NotNull List<ItemModel> models();
}

package team.unnamed.creative.item;

import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.item.tint.TintSource;

public interface ReferenceItemModel extends ItemModel {
   @NotNull Key model();

   @NotNull List<TintSource> tints();
}

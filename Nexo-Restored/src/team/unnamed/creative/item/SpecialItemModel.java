package team.unnamed.creative.item;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.item.special.SpecialRender;

public interface SpecialItemModel extends ItemModel {
   @NotNull SpecialRender render();

   @NotNull Key base();
}

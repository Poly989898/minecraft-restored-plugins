package team.unnamed.creative.item.special;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

public interface ChestSpecialRender extends SpecialRender {
   Key NORMAL_CHEST_TEXTURE = Key.key("minecraft", "normal");
   Key TRAPPED_CHEST_TEXTURE = Key.key("minecraft", "trapped");
   Key ENDER_CHEST_TEXTURE = Key.key("minecraft", "ender");
   Key GIFT_CHEST_TEXTURE = Key.key("minecraft", "christmas");
   @Experimental
   float DEFAULT_OPENNESS = 0.0F;

   @NotNull Key texture();

   float openness();
}

package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.data.StringData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface TemplateOption {
   default void whenLoaded(@NotNull ReadMMOItem mmoitem) {
   }

   @Nullable
   default StringData getLoadedNBT(@NotNull ArrayList<ItemTag> storedTags) {
      throw new RuntimeException("Not supported");
   }

   default void whenApplied(@NotNull ItemStackBuilder item, @NotNull StringData data) {
      throw new RuntimeException("Not supported");
   }

   @NotNull
   default ArrayList<ItemTag> getAppliedNBT(@NotNull StringData data) {
      throw new RuntimeException("Not supported");
   }
}

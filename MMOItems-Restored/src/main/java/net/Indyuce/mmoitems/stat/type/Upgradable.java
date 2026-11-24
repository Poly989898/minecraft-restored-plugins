package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Upgradable {
   @NotNull
   UpgradeInfo loadUpgradeInfo(@Nullable Object var1) throws IllegalArgumentException;

   @NotNull
   StatData apply(@NotNull StatData var1, @NotNull UpgradeInfo var2, int var3);

   default void preprocess(@NotNull MMOItem item) {
   }

   default void midprocess(@NotNull MMOItem item) {
   }

   default void postprocess(@NotNull MMOItem item) {
   }
}

package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Upgradable {
   @NotNull
   UpgradeInfo loadUpgradeInfo(@Nullable Object obj) throws IllegalArgumentException;

   @NotNull
   StatData apply(@NotNull StatData statData, @NotNull UpgradeInfo upgradeInfo, int count);

   default void preprocess(@NotNull MMOItem item) {
   }

   default void midprocess(@NotNull MMOItem item) {
   }

   default void postprocess(@NotNull MMOItem item) {
   }
}

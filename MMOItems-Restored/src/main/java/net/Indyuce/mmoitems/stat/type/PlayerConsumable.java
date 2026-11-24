package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerConsumable {
   void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag);
}

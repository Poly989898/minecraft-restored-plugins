package net.Indyuce.mmoitems.stat.type;

import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerConsumable {
   void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3);
}

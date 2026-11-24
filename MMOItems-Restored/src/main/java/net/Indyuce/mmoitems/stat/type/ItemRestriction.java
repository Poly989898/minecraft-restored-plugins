package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;

public interface ItemRestriction {
   boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag);

   default boolean isDynamic() {
      return false;
   }
}

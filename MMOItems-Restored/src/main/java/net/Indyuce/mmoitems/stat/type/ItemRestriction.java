package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;

public interface ItemRestriction {
   boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3);

   default boolean isDynamic() {
      return false;
   }
}

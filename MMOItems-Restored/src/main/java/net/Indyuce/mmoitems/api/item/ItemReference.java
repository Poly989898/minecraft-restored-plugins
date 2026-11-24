package net.Indyuce.mmoitems.api.item;

import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import net.Indyuce.mmoitems.api.Type;

public interface ItemReference extends CooldownObject {
   Type getType();

   String getId();

   default String getCooldownPath() {
      return "mmoitem_" + this.getId().toLowerCase();
   }
}

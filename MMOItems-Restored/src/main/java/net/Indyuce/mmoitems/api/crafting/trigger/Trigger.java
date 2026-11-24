package net.Indyuce.mmoitems.api.crafting.trigger;

import net.Indyuce.mmoitems.api.player.PlayerData;

public abstract class Trigger {
   private final String id;

   public Trigger(String text) {
      this.id = text;
   }

   public String getId() {
      return this.id;
   }

   public abstract void whenCrafting(PlayerData playerData);
}

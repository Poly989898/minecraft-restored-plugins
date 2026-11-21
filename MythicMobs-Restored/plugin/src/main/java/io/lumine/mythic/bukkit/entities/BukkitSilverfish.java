package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.EntityType;

public class BukkitSilverfish extends BukkitEntityType {
   private Boolean preventBlockInfection = false;

   @Override
   public EntityType getBukkitType() {
      return EntityType.SILVERFISH;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.preventBlockInfection = mc.getBoolean("Options.PreventBlockInfection", false);
   }

   public boolean getPreventBlockInfection() {
      return this.preventBlockInfection;
   }
}

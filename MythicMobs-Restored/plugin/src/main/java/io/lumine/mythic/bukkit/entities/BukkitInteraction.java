package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;

public class BukkitInteraction extends BukkitEntityType {
   private float height;
   private float width;
   private boolean responsive;

   @Override
   public EntityType getBukkitType() {
      return EntityType.INTERACTION;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.height = mc.getFloat("Options.Height", 1.0F);
      this.width = mc.getFloat("Options.Width", this.height);
      this.responsive = mc.getBoolean("Options.Responsive", true);
   }

   @Override
   public Entity applyOptions(Entity e) {
      if (e instanceof Interaction interaction) {
         interaction.setInteractionHeight(this.height);
         interaction.setInteractionWidth(this.width);
         interaction.setResponsive(this.responsive);
      }

      return e;
   }
}

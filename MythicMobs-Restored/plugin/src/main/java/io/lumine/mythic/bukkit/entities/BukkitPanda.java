package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;

@Getter
public class BukkitPanda extends BukkitEntityType {
   private AgeableProperty ageableProperty;
   private String mainGene;
   private String hiddenGene;

   @Override
   public EntityType getBukkitType() {
      return EntityType.PANDA;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.mainGene = mc.getString("Options.MainGene", null);
      this.hiddenGene = mc.getString("Options.HiddenGene", null);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Panda e = (Panda)entity;
      this.ageableProperty.applyProperties(entity);
      if (this.mainGene != null) {
         e.setMainGene(Gene.valueOf(this.mainGene.toUpperCase()));
      }

      if (this.hiddenGene != null) {
         e.setHiddenGene(Gene.valueOf(this.hiddenGene.toUpperCase()));
      }

      return e;
   }
}

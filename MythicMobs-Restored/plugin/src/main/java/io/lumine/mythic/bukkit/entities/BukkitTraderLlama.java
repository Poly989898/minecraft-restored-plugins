package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Llama.Color;

@Getter
public class BukkitTraderLlama extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   private TameableProperty tameableProperty;
   private Color color;
   private boolean carryingChest;
   private boolean tamed;

   @Override
   public EntityType getBukkitType() {
      return EntityType.TRADER_LLAMA;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.WHEAT, new Material[]{Material.HAY_BLOCK}));
      this.carryingChest = mc.getBoolean("Options.CarryingChest", false);
      this.tamed = mc.getBoolean("Options.Tamed", false);
      String strColor = mc.getString("Options.Color", "WHITE");

      try {
         this.color = Color.valueOf(strColor.toUpperCase());
      } catch (Exception e) {
         MythicLogger.errorEntityConfig(this, mc, "Invalid Llama color specified");
         this.color = Color.WHITE;
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      TraderLlama e = (TraderLlama)entity;
      this.ageableProperty.applyProperties(entity);
      e.setColor(this.color);
      if (this.carryingChest) {
         e.setCarryingChest(true);
      }

      if (this.tamed) {
         e.setTamed(true);
      }

      return e;
   }
}

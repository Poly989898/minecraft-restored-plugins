package io.lumine.mythic.bukkit.entities;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.entities.properties.AgeableProperty;
import io.lumine.mythic.bukkit.entities.properties.TameableEntityType;
import io.lumine.mythic.bukkit.entities.properties.TameableProperty;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

@Getter
public class BukkitWolf extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   private TameableProperty tameableProperty;
   private boolean angry = false;
   private DyeColor color = null;
   private boolean tameable = false;
   private String variant;
   private DyeColor collarColor;

   @Override
   public EntityType getBukkitType() {
      return EntityType.WOLF;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(mc, Sets.immutableEnumSet(Material.BONE, new Material[0]));
      this.angry = mc.getBoolean("Options.Angry", false);
      this.variant = mc.getString(new String[]{"Options.Variant", "Options.WolfType"}, null);
      String collarColor = mc.getString(new String[]{"Options.Color", "Options.CollarColor"}, null);
      if (collarColor != null) {
         try {
            this.collarColor = DyeColor.valueOf(collarColor.toUpperCase());
         } catch (Throwable e) {
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Wolf e = (Wolf)entity;
      e = (Wolf)this.ageableProperty.applyProperties(e);
      e.setAngry(this.angry);
      if (this.variant != null) {
         this.getPlugin().getVolatileCodeHandler().getCompatibilityHandler().setWolfVariant(e, this.variant);
      }

      if (this.collarColor != null) {
         e.setCollarColor(this.collarColor);
      }

      return e;
   }

   public static AbstractPlayer getOwner(ActiveMob am) {
      return BukkitAdapter.adapt(am.getEntity()) instanceof Wolf w && w.getOwner() instanceof Player ? BukkitAdapter.adapt((Player)w.getOwner()) : null;
   }
}

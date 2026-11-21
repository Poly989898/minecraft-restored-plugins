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
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

@Getter
public class BukkitHorse extends BukkitEntityType implements TameableEntityType {
   private AgeableProperty ageableProperty;
   private TameableProperty tameableProperty;
   private String horseStyle;
   private String horseType;
   private String horseColor;
   private String horseArmor;
   private boolean horseChest;
   private boolean horseSaddled;
   private boolean horseTamed;

   @Override
   public EntityType getBukkitType() {
      return EntityType.HORSE;
   }

   @Override
   public int getHeight() {
      return 2;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      this.ageableProperty = new AgeableProperty(mc);
      this.tameableProperty = new TameableProperty(
         mc,
         Sets.immutableEnumSet(
            Material.APPLE, new Material[]{Material.GOLDEN_APPLE, Material.SUGAR, Material.WHEAT, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_CARROT}
         )
      );
      this.horseArmor = mc.getString("Options.HorseArmor");
      this.horseChest = mc.getBoolean("Options.HorseCarryingChest", false);
      this.horseStyle = mc.getString("Options.HorseStyle");
      this.horseType = mc.getString("Options.HorseType");
      this.horseColor = mc.getString("Options.HorseColor");
      this.horseColor = mc.getString("Options.Color", this.horseColor);
      this.horseSaddled = mc.getBoolean("Options.HorseSaddled", this.horseArmor != null);
      this.horseSaddled = mc.getBoolean("Options.Saddled", this.horseSaddled);
      this.horseTamed = mc.getBoolean("Options.HorseTamed", false);
      this.horseTamed = mc.getBoolean("Options.Tamed", this.horseTamed);
   }

   @Override
   public Entity applyOptions(Entity entity) {
      Horse e = (Horse)entity;
      this.ageableProperty.applyProperties(entity);
      HorseInventory hi = e.getInventory();
      if (this.horseStyle != null) {
         e.setStyle(Style.valueOf(this.horseStyle.toUpperCase()));
      }

      if (this.horseColor != null) {
         try {
            e.setColor(Color.valueOf(this.horseColor.toUpperCase()));
         } catch (Exception ex2) {
            MythicLogger.error("invalid horse color");
         }
      }

      if (this.horseTamed) {
         e.setTamed(true);
      }

      if (this.horseSaddled) {
         hi.setSaddle(new ItemStack(Material.SADDLE));
      }

      if (this.horseArmor != null) {
         String ex = this.horseArmor.toUpperCase();
         switch (ex) {
            case "DIAMOND":
            case "DIAMOND_HORSE_ARMOR":
               hi.setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR));
               break;
            case "GOLD":
            case "GOLDEN":
            case "GOLDEN_HORSE_ARMOR":
               hi.setArmor(new ItemStack(Material.GOLDEN_HORSE_ARMOR));
               break;
            default:
               hi.setArmor(new ItemStack(Material.IRON_HORSE_ARMOR));
         }
      }

      return e;
   }
}

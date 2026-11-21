package io.lumine.mythic.bukkit.entities.properties;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Cat.Type;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Parrot.Variant;
import org.bukkit.entity.TropicalFish.Pattern;

public class ColoredProperty implements EntityPropertySet {
   private String strColor;
   private Object color;

   public ColoredProperty(MythicConfig mc) {
      this.strColor = mc.getString("Options.Color", "WHITE");
   }

   public ColoredProperty(MythicLineConfig mc) {
      this.strColor = mc.getString("color", "WHITE");
   }

   public ColoredProperty(String color) {
      this.strColor = color;
   }

   @Override
   public Entity applyProperties(Entity entity) {
      if (entity instanceof Horse) {
         if (this.color == null) {
            try {
               this.color = Color.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception e) {
               this.color = Color.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Horse");
            }
         }

         Color color = (Color)this.color;
         Horse target = (Horse)entity;
         target.setColor(color);
      } else if (entity instanceof Llama) {
         if (this.color == null) {
            try {
               this.color = org.bukkit.entity.Llama.Color.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex) {
               this.color = org.bukkit.entity.Llama.Color.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Llama");
            }
         }

         org.bukkit.entity.Llama.Color color = (org.bukkit.entity.Llama.Color)this.color;
         Llama target = (Llama)entity;
         target.setColor(color);
      } else if (entity instanceof TraderLlama) {
         if (this.color == null) {
            try {
               this.color = org.bukkit.entity.Llama.Color.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex2) {
               this.color = org.bukkit.entity.Llama.Color.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for TraderLlama");
            }
         }

         org.bukkit.entity.Llama.Color color = (org.bukkit.entity.Llama.Color)this.color;
         TraderLlama target = (TraderLlama)entity;
         target.setColor(color);
      } else if (entity instanceof Parrot) {
         if (this.color == null) {
            try {
               this.color = Variant.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex3) {
               this.color = Variant.BLUE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Parrot");
            }
         }

         Variant color = (Variant)this.color;
         Parrot target = (Parrot)entity;
         target.setVariant(color);
      } else if (entity instanceof Sheep) {
         if (this.color == null) {
            try {
               this.color = DyeColor.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex4) {
               this.color = DyeColor.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Sheep");
            }
         }

         DyeColor color = (DyeColor)this.color;
         Sheep target = (Sheep)entity;
         target.setColor(color);
      } else if (entity instanceof Shulker) {
         if (this.color == null) {
            try {
               this.color = DyeColor.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex5) {
               this.color = DyeColor.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Shulker");
            }
         }

         DyeColor color = (DyeColor)this.color;
         Shulker target = (Shulker)entity;
         target.setColor(color);
      } else if (entity instanceof TropicalFish) {
         if (this.color == null) {
            try {
               this.color = Pattern.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex6) {
               this.color = Pattern.GLITTER;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for TropicalFish");
            }
         }

         Pattern color = (Pattern)this.color;
         TropicalFish target = (TropicalFish)entity;
         target.setPattern(color);
      } else if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_14) && entity instanceof Cat) {
         if (this.color == null) {
            try {
               this.color = Type.valueOf(this.strColor.toUpperCase());
            } catch (Error | Exception ex7) {
               this.color = Type.WHITE;
               MythicLogger.error("Invalid color '" + this.strColor + "' configured for Horse");
            }
         }

         Type color = (Type)this.color;
         Cat target = (Cat)entity;
         target.setCatType(color);
      }

      return entity;
   }
}

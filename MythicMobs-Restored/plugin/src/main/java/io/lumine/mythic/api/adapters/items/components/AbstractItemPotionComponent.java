package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitPotionEffect;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.potion.PotionType;

@Getter
@Setter
@ToString
public class AbstractItemPotionComponent implements AbstractItemComponent {
   private PlaceholderString customName;
   private PlaceholderColor potionColor;
   private PotionType basePotionType;
   private Float durationScale;
   private Collection<BukkitPotionEffect> effects = new ArrayList<>();

   public AbstractItemPotionComponent(MythicItem mythicItem, MythicConfig config) {
      this.customName = config.getPlaceholderString("Potion.Name", null);
      if (config.isSet("Potion.Type")) {
         String typeName = config.getString("Potion.Type");

         try {
            this.basePotionType = PotionType.valueOf(typeName.toUpperCase(Locale.ROOT).replace("MINECRAFT:", ""));
         } catch (IllegalArgumentException e) {
            MythicLogger.errorItemConfig(mythicItem, config, "Invalid Potion.Type '" + typeName + "'");
         }
      }

      if (config.isSet("Potion.DurationScale")) {
         this.durationScale = config.getFloat("Potion.DurationScale");
         if (this.durationScale < 0.0F) {
            MythicLogger.errorItemConfig(mythicItem, config, "Potion.DurationScale cannot be less than 0, using 0 instead");
            this.durationScale = 0.0F;
         }
      }

      this.potionColor = config.getPlaceholderColor("Options.Color", null);
      this.potionColor = config.getPlaceholderColor("Potion.Color", this.potionColor == null ? null : this.potionColor.toString());
      Collection<String> in = config.getStringList("Potion.Effects");
      in.addAll(config.getStringList("PotionEffects"));

      for (String line : in) {
         if (line.contains("{")) {
            MythicLineConfigImpl foodEffectConfig = MythicLineConfigImpl.of(line);
            String foodEffectType = foodEffectConfig.getKey();
            int foodEffectDuration = foodEffectConfig.getInteger(new String[]{"duration", "d"}, 60);
            int foodEffectLevel = foodEffectConfig.getInteger(new String[]{"level", "lvl", "l"}, 0);
            boolean ambientParticles = foodEffectConfig.getBoolean(new String[]{"ambientparticles", "ambient", "a"}, false);
            boolean hasParticles = foodEffectConfig.getBoolean(new String[]{"hasparticles", "particles", "p"}, true);
            boolean hasIcon = foodEffectConfig.getBoolean(new String[]{"hasicon", "icon", "i"}, true);
            BukkitPotionEffect effect = new BukkitPotionEffect(foodEffectType, foodEffectDuration, foodEffectLevel, ambientParticles, hasParticles, hasIcon);
            this.effects.add(effect);
         } else {
            try {
               String[] sp = line.split(" ");
               if (sp.length < 3) {
                  MythicLogger.errorItemConfig(mythicItem, config, "A potion effect is configured incorrectly. Format is 'EFFECT DURATION LEVEL'. Line=" + line);
               } else {
                  int duration = Integer.parseInt(sp[1]);
                  int level = Integer.parseInt(sp[2]);
                  BukkitPotionEffect effect = new BukkitPotionEffect(sp[0].toUpperCase(), duration, level, true, true, true);
                  this.effects.add(effect);
               }
            } catch (Throwable ex) {
               MythicLogger.errorItemConfig(mythicItem, config, "A potion effect is configured incorrectly. Format is 'EFFECT DURATION LEVEL'. Line=" + line);
            }
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyPotionComponent(dropMetadata, this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemPotionComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$durationScale = this.getDurationScale();
         Object other$durationScale = other.getDurationScale();
         if (this$durationScale == null ? other$durationScale == null : this$durationScale.equals(other$durationScale)) {
            Object this$customName = this.getCustomName();
            Object other$customName = other.getCustomName();
            if (this$customName == null ? other$customName == null : this$customName.equals(other$customName)) {
               Object this$potionColor = this.getPotionColor();
               Object other$potionColor = other.getPotionColor();
               if (this$potionColor == null ? other$potionColor == null : this$potionColor.equals(other$potionColor)) {
                  Object this$basePotionType = this.getBasePotionType();
                  Object other$basePotionType = other.getBasePotionType();
                  if (this$basePotionType == null ? other$basePotionType == null : this$basePotionType.equals(other$basePotionType)) {
                     Object this$effects = this.getEffects();
                     Object other$effects = other.getEffects();
                     return this$effects == null ? other$effects == null : this$effects.equals(other$effects);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof AbstractItemPotionComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $durationScale = this.getDurationScale();
      result = result * 59 + ($durationScale == null ? 43 : $durationScale.hashCode());
      Object $customName = this.getCustomName();
      result = result * 59 + ($customName == null ? 43 : $customName.hashCode());
      Object $potionColor = this.getPotionColor();
      result = result * 59 + ($potionColor == null ? 43 : $potionColor.hashCode());
      Object $basePotionType = this.getBasePotionType();
      result = result * 59 + ($basePotionType == null ? 43 : $basePotionType.hashCode());
      Object $effects = this.getEffects();
      return result * 59 + ($effects == null ? 43 : $effects.hashCode());
   }
}

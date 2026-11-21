package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class AbstractItemBlocksAttacksComponent implements AbstractItemComponent {
   private Float blockDelaySeconds;
   private Float disableCooldownScale;
   private List<AbstractItemBlocksAttacksComponent.DamageReduction> damageReductions = new ArrayList<>();
   private AbstractItemBlocksAttacksComponent.ItemDamage itemDamage;
   private Object hitBlockSound;
   private Object disabledSound;
   private String bypassedBy;

   public AbstractItemBlocksAttacksComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("BlockDelaySeconds")) {
         this.blockDelaySeconds = config.getFloat("BlockDelaySeconds");
         if (this.blockDelaySeconds < 0.0F) {
            MythicLogger.errorGenericConfig(config, "BlockDelaySeconds cannot be less than 0, using 0 instead");
            this.blockDelaySeconds = 0.0F;
         }
      }

      if (config.isSet("DisableCooldownScale")) {
         this.disableCooldownScale = config.getFloat("DisableCooldownScale");
         if (this.disableCooldownScale < 0.0F) {
            MythicLogger.errorGenericConfig(config, "DisableCooldownScale cannot be less than 0, using 1 instead");
            this.disableCooldownScale = 1.0F;
         }
      }

      if (!config.isSet("DamageReductions")) {
         this.damageReductions
            .add(new AbstractItemBlocksAttacksComponent.DamageReduction(AbstractItemBlocksAttacksComponent.DamageTypeSelector.all(), 0.0F, 1.0F, 90.0F));
      }

      for (Map<?, ?> map : config.getMapList("DamageReductions")) {
         AbstractItemBlocksAttacksComponent.DamageTypeSelector selector = this.parseDamageTypeSelector(config, map.get("Type"));
         boolean hasBase = map.containsKey("Base");
         boolean hasFactor = map.containsKey("Factor");
         if (!hasBase && !hasFactor) {
            MythicLogger.errorGenericConfig(config, "DamageReductions entry must specify Base and/or Factor");
         } else {
            float base = hasBase ? this.toFloat(map.get("Base"), 0.0F) : 0.0F;
            float factor = hasFactor ? this.toFloat(map.get("Factor"), 0.0F) : 0.0F;
            float angle = map.containsKey("HorizontalBlockingAngle") ? this.toFloat(map.get("HorizontalBlockingAngle"), 90.0F) : 90.0F;
            if (angle < 0.0F) {
               MythicLogger.errorGenericConfig(config, "HorizontalBlockingAngle cannot be less than 0, using 90 instead");
               angle = 90.0F;
            }

            this.damageReductions.add(new AbstractItemBlocksAttacksComponent.DamageReduction(selector, base, factor, angle));
         }
      }

      if (config.isSet("ItemDamage")) {
         MythicConfig sub = config.getNestedConfig("ItemDamage");
         Float threshold = sub.isSet("Threshold") ? sub.getFloat("Threshold") : null;
         Float base = sub.isSet("Base") ? sub.getFloat("Base") : null;
         Float factor = sub.isSet("Factor") ? sub.getFloat("Factor") : null;
         if (threshold != null && threshold < 0.0F) {
            MythicLogger.errorGenericConfig(sub, "ItemDamage.Threshold cannot be less than 0, using 0 instead");
            threshold = 0.0F;
         }

         if (base != null && base < 0.0F) {
            MythicLogger.errorGenericConfig(sub, "ItemDamage.Base cannot be less than 0, using 0 instead");
            base = 0.0F;
         }

         if (factor != null && factor < 0.0F) {
            MythicLogger.errorGenericConfig(sub, "ItemDamage.Factor cannot be less than 0, using 1.5 instead");
            factor = 1.5F;
         }

         this.itemDamage = new AbstractItemBlocksAttacksComponent.ItemDamage(threshold, base, factor);
      }

      if (config.isSet("HitBlockSound")) {
         this.hitBlockSound = MythicConfig.normalizeNamespacedKey(config.getString("HitBlockSound"));
      }

      if (config.isSet("DisabledSound")) {
         this.disabledSound = MythicConfig.normalizeNamespacedKey(config.getString("DisabledSound"));
      }

      if (config.isSet("BypassedBy")) {
         String bypass = config.getString("BypassedBy");
         if (bypass.startsWith("#")) {
            bypass = bypass.substring(1);
         }

         this.bypassedBy = MythicConfig.normalizeNamespacedKey(bypass);
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyBlocksAttacksComponent(dropMetadata, this);
   }

   private float toFloat(Object obj, float def) {
      return obj instanceof Number ? ((Number)obj).floatValue() : def;
   }

   private AbstractItemBlocksAttacksComponent.DamageTypeSelector parseDamageTypeSelector(MythicConfig config, Object typeObj) {
      if (typeObj == null) {
         return AbstractItemBlocksAttacksComponent.DamageTypeSelector.all();
      } else if (typeObj instanceof String) {
         return new AbstractItemBlocksAttacksComponent.DamageTypeSelector((String)typeObj, null, false);
      } else if (typeObj instanceof List) {
         ArrayList<String> list = new ArrayList<>();

         for (Object o : (List)typeObj) {
            if (o != null) {
               list.add(String.valueOf(o));
            }
         }

         return new AbstractItemBlocksAttacksComponent.DamageTypeSelector(null, list, false);
      } else {
         MythicLogger.errorGenericConfig(config, "DamageReductions.Type must be a String or List<String>");
         return AbstractItemBlocksAttacksComponent.DamageTypeSelector.all();
      }
   }

   @Generated
   public Float getBlockDelaySeconds() {
      return this.blockDelaySeconds;
   }

   @Generated
   public Float getDisableCooldownScale() {
      return this.disableCooldownScale;
   }

   @Generated
   public List<AbstractItemBlocksAttacksComponent.DamageReduction> getDamageReductions() {
      return this.damageReductions;
   }

   @Generated
   public AbstractItemBlocksAttacksComponent.ItemDamage getItemDamage() {
      return this.itemDamage;
   }

   @Generated
   public Object getHitBlockSound() {
      return this.hitBlockSound;
   }

   @Generated
   public Object getDisabledSound() {
      return this.disabledSound;
   }

   @Generated
   public String getBypassedBy() {
      return this.bypassedBy;
   }

   @Generated
   public void setBlockDelaySeconds(Float blockDelaySeconds) {
      this.blockDelaySeconds = blockDelaySeconds;
   }

   @Generated
   public void setDisableCooldownScale(Float disableCooldownScale) {
      this.disableCooldownScale = disableCooldownScale;
   }

   @Generated
   public void setDamageReductions(List<AbstractItemBlocksAttacksComponent.DamageReduction> damageReductions) {
      this.damageReductions = damageReductions;
   }

   @Generated
   public void setItemDamage(AbstractItemBlocksAttacksComponent.ItemDamage itemDamage) {
      this.itemDamage = itemDamage;
   }

   @Generated
   public void setHitBlockSound(Object hitBlockSound) {
      this.hitBlockSound = hitBlockSound;
   }

   @Generated
   public void setDisabledSound(Object disabledSound) {
      this.disabledSound = disabledSound;
   }

   @Generated
   public void setBypassedBy(String bypassedBy) {
      this.bypassedBy = bypassedBy;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemBlocksAttacksComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$blockDelaySeconds = this.getBlockDelaySeconds();
         Object other$blockDelaySeconds = other.getBlockDelaySeconds();
         if (this$blockDelaySeconds == null ? other$blockDelaySeconds == null : this$blockDelaySeconds.equals(other$blockDelaySeconds)) {
            Object this$disableCooldownScale = this.getDisableCooldownScale();
            Object other$disableCooldownScale = other.getDisableCooldownScale();
            if (this$disableCooldownScale == null ? other$disableCooldownScale == null : this$disableCooldownScale.equals(other$disableCooldownScale)) {
               Object this$damageReductions = this.getDamageReductions();
               Object other$damageReductions = other.getDamageReductions();
               if (this$damageReductions == null ? other$damageReductions == null : this$damageReductions.equals(other$damageReductions)) {
                  Object this$itemDamage = this.getItemDamage();
                  Object other$itemDamage = other.getItemDamage();
                  if (this$itemDamage == null ? other$itemDamage == null : this$itemDamage.equals(other$itemDamage)) {
                     Object this$hitBlockSound = this.getHitBlockSound();
                     Object other$hitBlockSound = other.getHitBlockSound();
                     if (this$hitBlockSound == null ? other$hitBlockSound == null : this$hitBlockSound.equals(other$hitBlockSound)) {
                        Object this$disabledSound = this.getDisabledSound();
                        Object other$disabledSound = other.getDisabledSound();
                        if (this$disabledSound == null ? other$disabledSound == null : this$disabledSound.equals(other$disabledSound)) {
                           Object this$bypassedBy = this.getBypassedBy();
                           Object other$bypassedBy = other.getBypassedBy();
                           return this$bypassedBy == null ? other$bypassedBy == null : this$bypassedBy.equals(other$bypassedBy);
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
      return other instanceof AbstractItemBlocksAttacksComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $blockDelaySeconds = this.getBlockDelaySeconds();
      result = result * 59 + ($blockDelaySeconds == null ? 43 : $blockDelaySeconds.hashCode());
      Object $disableCooldownScale = this.getDisableCooldownScale();
      result = result * 59 + ($disableCooldownScale == null ? 43 : $disableCooldownScale.hashCode());
      Object $damageReductions = this.getDamageReductions();
      result = result * 59 + ($damageReductions == null ? 43 : $damageReductions.hashCode());
      Object $itemDamage = this.getItemDamage();
      result = result * 59 + ($itemDamage == null ? 43 : $itemDamage.hashCode());
      Object $hitBlockSound = this.getHitBlockSound();
      result = result * 59 + ($hitBlockSound == null ? 43 : $hitBlockSound.hashCode());
      Object $disabledSound = this.getDisabledSound();
      result = result * 59 + ($disabledSound == null ? 43 : $disabledSound.hashCode());
      Object $bypassedBy = this.getBypassedBy();
      return result * 59 + ($bypassedBy == null ? 43 : $bypassedBy.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "AbstractItemBlocksAttacksComponent(blockDelaySeconds="
         + this.getBlockDelaySeconds()
         + ", disableCooldownScale="
         + this.getDisableCooldownScale()
         + ", damageReductions="
         + this.getDamageReductions()
         + ", itemDamage="
         + this.getItemDamage()
         + ", hitBlockSound="
         + this.getHitBlockSound()
         + ", disabledSound="
         + this.getDisabledSound()
         + ", bypassedBy="
         + this.getBypassedBy()
         + ")";
   }

   public class DamageReduction {
      private AbstractItemBlocksAttacksComponent.DamageTypeSelector type;
      private float base;
      private float factor;
      private float horizontalBlockingAngle;

      @Generated
      public AbstractItemBlocksAttacksComponent.DamageTypeSelector getType() {
         return this.type;
      }

      @Generated
      public float getBase() {
         return this.base;
      }

      @Generated
      public float getFactor() {
         return this.factor;
      }

      @Generated
      public float getHorizontalBlockingAngle() {
         return this.horizontalBlockingAngle;
      }

      @Generated
      public void setType(AbstractItemBlocksAttacksComponent.DamageTypeSelector type) {
         this.type = type;
      }

      @Generated
      public void setBase(float base) {
         this.base = base;
      }

      @Generated
      public void setFactor(float factor) {
         this.factor = factor;
      }

      @Generated
      public void setHorizontalBlockingAngle(float horizontalBlockingAngle) {
         this.horizontalBlockingAngle = horizontalBlockingAngle;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemBlocksAttacksComponent.DamageReduction other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Float.compare(this.getBase(), other.getBase()) != 0) {
            return false;
         } else if (Float.compare(this.getFactor(), other.getFactor()) != 0) {
            return false;
         } else if (Float.compare(this.getHorizontalBlockingAngle(), other.getHorizontalBlockingAngle()) != 0) {
            return false;
         } else {
            Object this$type = this.getType();
            Object other$type = other.getType();
            return this$type == null ? other$type == null : this$type.equals(other$type);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractItemBlocksAttacksComponent.DamageReduction;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + Float.floatToIntBits(this.getBase());
         result = result * 59 + Float.floatToIntBits(this.getFactor());
         result = result * 59 + Float.floatToIntBits(this.getHorizontalBlockingAngle());
         Object $type = this.getType();
         return result * 59 + ($type == null ? 43 : $type.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractItemBlocksAttacksComponent.DamageReduction(type="
            + this.getType()
            + ", base="
            + this.getBase()
            + ", factor="
            + this.getFactor()
            + ", horizontalBlockingAngle="
            + this.getHorizontalBlockingAngle()
            + ")";
      }

      @Generated
      public DamageReduction(
         final AbstractItemBlocksAttacksComponent.DamageTypeSelector type, final float base, final float factor, final float horizontalBlockingAngle
      ) {
         this.type = type;
         this.base = base;
         this.factor = factor;
         this.horizontalBlockingAngle = horizontalBlockingAngle;
      }
   }

   public static class DamageTypeSelector {
      private String single;
      private List<String> many;
      private boolean all;

      public static AbstractItemBlocksAttacksComponent.DamageTypeSelector all() {
         return new AbstractItemBlocksAttacksComponent.DamageTypeSelector(null, null, true);
      }

      @Generated
      public String getSingle() {
         return this.single;
      }

      @Generated
      public List<String> getMany() {
         return this.many;
      }

      @Generated
      public boolean isAll() {
         return this.all;
      }

      @Generated
      public void setSingle(String single) {
         this.single = single;
      }

      @Generated
      public void setMany(List<String> many) {
         this.many = many;
      }

      @Generated
      public void setAll(boolean all) {
         this.all = all;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemBlocksAttacksComponent.DamageTypeSelector other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.isAll() != other.isAll()) {
            return false;
         } else {
            Object this$single = this.getSingle();
            Object other$single = other.getSingle();
            if (this$single == null ? other$single == null : this$single.equals(other$single)) {
               Object this$many = this.getMany();
               Object other$many = other.getMany();
               return this$many == null ? other$many == null : this$many.equals(other$many);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractItemBlocksAttacksComponent.DamageTypeSelector;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (this.isAll() ? 79 : 97);
         Object $single = this.getSingle();
         result = result * 59 + ($single == null ? 43 : $single.hashCode());
         Object $many = this.getMany();
         return result * 59 + ($many == null ? 43 : $many.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractItemBlocksAttacksComponent.DamageTypeSelector(single=" + this.getSingle() + ", many=" + this.getMany() + ", all=" + this.isAll() + ")";
      }

      @Generated
      public DamageTypeSelector(String single, List<String> many, boolean all) {
         this.single = single;
         this.many = many;
         this.all = all;
      }
   }

   public class ItemDamage {
      private Float threshold;
      private Float base;
      private Float factor;

      @Generated
      public Float getThreshold() {
         return this.threshold;
      }

      @Generated
      public Float getBase() {
         return this.base;
      }

      @Generated
      public Float getFactor() {
         return this.factor;
      }

      @Generated
      public void setThreshold(Float threshold) {
         this.threshold = threshold;
      }

      @Generated
      public void setBase(Float base) {
         this.base = base;
      }

      @Generated
      public void setFactor(Float factor) {
         this.factor = factor;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemBlocksAttacksComponent.ItemDamage other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$threshold = this.getThreshold();
            Object other$threshold = other.getThreshold();
            if (this$threshold == null ? other$threshold == null : this$threshold.equals(other$threshold)) {
               Object this$base = this.getBase();
               Object other$base = other.getBase();
               if (this$base == null ? other$base == null : this$base.equals(other$base)) {
                  Object this$factor = this.getFactor();
                  Object other$factor = other.getFactor();
                  return this$factor == null ? other$factor == null : this$factor.equals(other$factor);
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
         return other instanceof AbstractItemBlocksAttacksComponent.ItemDamage;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $threshold = this.getThreshold();
         result = result * 59 + ($threshold == null ? 43 : $threshold.hashCode());
         Object $base = this.getBase();
         result = result * 59 + ($base == null ? 43 : $base.hashCode());
         Object $factor = this.getFactor();
         return result * 59 + ($factor == null ? 43 : $factor.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractItemBlocksAttacksComponent.ItemDamage(threshold="
            + this.getThreshold()
            + ", base="
            + this.getBase()
            + ", factor="
            + this.getFactor()
            + ")";
      }

      @Generated
      public ItemDamage(final Float threshold, final Float base, final Float factor) {
         this.threshold = threshold;
         this.base = base;
         this.factor = factor;
      }
   }
}

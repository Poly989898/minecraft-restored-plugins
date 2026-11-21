package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class AbstractItemToolComponent implements AbstractItemComponent {
   private Integer damagePerBlock;
   private Float defaultMiningSpeed;
   private List<AbstractItemToolComponent.AbstractToolRule> rules = new ArrayList<>();

   public AbstractItemToolComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("DamagePerBlock")) {
         this.damagePerBlock = config.getInt("DamagePerBlock");
      }

      if (config.isSet("DefaultMiningSpeed")) {
         this.defaultMiningSpeed = config.getFloat("DefaultMiningSpeed");
         if (this.defaultMiningSpeed < 0.0F) {
            MythicLogger.errorGenericConfig(config, "DefaultMiningSpeed cannot be less than 0, using 1.0f instead");
            this.defaultMiningSpeed = 1.0F;
         }
      }

      for (Map<?, ?> map : config.getMapList("Rules")) {
         String materialsString = (String)map.get("materials");
         float speed = map.containsKey("speed") ? ((Number)map.get("speed")).floatValue() : 1.0F;
         boolean isCorrectForBlock = map.containsKey("isCorrectForBlock") ? (Boolean)map.get("isCorrectForBlock") : false;
         String[] split = materialsString.split(",");
         ArrayList<Material> materials = new ArrayList<>();

         for (String entry : split) {
            entry = entry.trim();
            boolean isTag = entry.startsWith("#");
            String tagName = isTag ? entry.substring(1) : entry;
            if (!tagName.isEmpty() && (isTag || split.length == 1)) {
               NamespacedKey key = NamespacedKey.fromString(tagName.toLowerCase(Locale.ROOT));
               Tag<Material> tag = key == null ? null : Bukkit.getTag("blocks", key, Material.class);
               if (tag != null) {
                  this.rules.add(new AbstractItemToolComponent.AbstractToolRule(null, tag, speed, isCorrectForBlock));
                  continue;
               }

               if (isTag) {
                  MythicLogger.errorGenericConfig(config, "Block tag " + entry + " is not a valid block tag");
                  continue;
               }
            }

            Material material = Material.matchMaterial(entry);
            if (material != null) {
               materials.add(material);
            } else {
               MythicLogger.errorGenericConfig(config, "Material " + entry + " is not a valid material");
            }
         }

         if (!materials.isEmpty()) {
            this.rules.add(new AbstractItemToolComponent.AbstractToolRule(materials, null, speed, isCorrectForBlock));
         }
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyToolComponent(this);
   }

   @Generated
   public Integer getDamagePerBlock() {
      return this.damagePerBlock;
   }

   @Generated
   public Float getDefaultMiningSpeed() {
      return this.defaultMiningSpeed;
   }

   @Generated
   public List<AbstractItemToolComponent.AbstractToolRule> getRules() {
      return this.rules;
   }

   @Generated
   public void setDamagePerBlock(Integer damagePerBlock) {
      this.damagePerBlock = damagePerBlock;
   }

   @Generated
   public void setDefaultMiningSpeed(Float defaultMiningSpeed) {
      this.defaultMiningSpeed = defaultMiningSpeed;
   }

   @Generated
   public void setRules(List<AbstractItemToolComponent.AbstractToolRule> rules) {
      this.rules = rules;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemToolComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$damagePerBlock = this.getDamagePerBlock();
         Object other$damagePerBlock = other.getDamagePerBlock();
         if (this$damagePerBlock == null ? other$damagePerBlock == null : this$damagePerBlock.equals(other$damagePerBlock)) {
            Object this$defaultMiningSpeed = this.getDefaultMiningSpeed();
            Object other$defaultMiningSpeed = other.getDefaultMiningSpeed();
            if (this$defaultMiningSpeed == null ? other$defaultMiningSpeed == null : this$defaultMiningSpeed.equals(other$defaultMiningSpeed)) {
               Object this$rules = this.getRules();
               Object other$rules = other.getRules();
               return this$rules == null ? other$rules == null : this$rules.equals(other$rules);
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
      return other instanceof AbstractItemToolComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $damagePerBlock = this.getDamagePerBlock();
      result = result * 59 + ($damagePerBlock == null ? 43 : $damagePerBlock.hashCode());
      Object $defaultMiningSpeed = this.getDefaultMiningSpeed();
      result = result * 59 + ($defaultMiningSpeed == null ? 43 : $defaultMiningSpeed.hashCode());
      Object $rules = this.getRules();
      return result * 59 + ($rules == null ? 43 : $rules.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "AbstractItemToolComponent(damagePerBlock="
         + this.getDamagePerBlock()
         + ", defaultMiningSpeed="
         + this.getDefaultMiningSpeed()
         + ", rules="
         + this.getRules()
         + ")";
   }

   public class AbstractToolRule {
      private List<Material> materials;
      private Tag<Material> tag;
      private Float speed;
      private Boolean isCorrectForBlock;

      @Generated
      public List<Material> getMaterials() {
         return this.materials;
      }

      @Generated
      public Tag<Material> getTag() {
         return this.tag;
      }

      @Generated
      public Float getSpeed() {
         return this.speed;
      }

      @Generated
      public Boolean getIsCorrectForBlock() {
         return this.isCorrectForBlock;
      }

      @Generated
      public void setMaterials(List<Material> materials) {
         this.materials = materials;
      }

      @Generated
      public void setTag(Tag<Material> tag) {
         this.tag = tag;
      }

      @Generated
      public void setSpeed(Float speed) {
         this.speed = speed;
      }

      @Generated
      public void setIsCorrectForBlock(Boolean isCorrectForBlock) {
         this.isCorrectForBlock = isCorrectForBlock;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemToolComponent.AbstractToolRule other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$speed = this.getSpeed();
            Object other$speed = other.getSpeed();
            if (this$speed == null ? other$speed == null : this$speed.equals(other$speed)) {
               Object this$isCorrectForBlock = this.getIsCorrectForBlock();
               Object other$isCorrectForBlock = other.getIsCorrectForBlock();
               if (this$isCorrectForBlock == null ? other$isCorrectForBlock == null : this$isCorrectForBlock.equals(other$isCorrectForBlock)) {
                  Object this$materials = this.getMaterials();
                  Object other$materials = other.getMaterials();
                  if (this$materials == null ? other$materials == null : this$materials.equals(other$materials)) {
                     Object this$tag = this.getTag();
                     Object other$tag = other.getTag();
                     return this$tag == null ? other$tag == null : this$tag.equals(other$tag);
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
         return other instanceof AbstractItemToolComponent.AbstractToolRule;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $speed = this.getSpeed();
         result = result * 59 + ($speed == null ? 43 : $speed.hashCode());
         Object $isCorrectForBlock = this.getIsCorrectForBlock();
         result = result * 59 + ($isCorrectForBlock == null ? 43 : $isCorrectForBlock.hashCode());
         Object $materials = this.getMaterials();
         result = result * 59 + ($materials == null ? 43 : $materials.hashCode());
         Object $tag = this.getTag();
         return result * 59 + ($tag == null ? 43 : $tag.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractItemToolComponent.AbstractToolRule(materials="
            + this.getMaterials()
            + ", tag="
            + this.getTag()
            + ", speed="
            + this.getSpeed()
            + ", isCorrectForBlock="
            + this.getIsCorrectForBlock()
            + ")";
      }

      @Generated
      public AbstractToolRule(final List<Material> materials, final Tag<Material> tag, final Float speed, final Boolean isCorrectForBlock) {
         this.materials = materials;
         this.tag = tag;
         this.speed = speed;
         this.isCorrectForBlock = isCorrectForBlock;
      }
   }
}

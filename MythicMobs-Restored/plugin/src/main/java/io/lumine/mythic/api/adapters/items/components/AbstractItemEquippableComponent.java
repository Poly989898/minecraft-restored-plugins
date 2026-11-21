package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;

@Getter
@Setter
@ToString
public class AbstractItemEquippableComponent implements AbstractItemComponent {
   public static final String SECTION_KEY = "Equippable";
   public static final String KEY_MODEL = "Equippable.Model";
   public static final String KEY_SLOT = "Equippable.Slot";
   public static final String KEY_EQUIP_SOUND = "Equippable.EquipSound";
   public static final String KEY_CAMERA_OVERLAY = "Equippable.CameraOverlay";
   public static final String KEY_ENTITY_TYPES = "Equippable.EntityTypes";
   public static final String KEY_DISPENSABLE = "Equippable.Dispensable";
   public static final String KEY_SWAPPABLE = "Equippable.Swappable";
   public static final String KEY_DAMAGE_ON_HURT = "Equippable.DamageOnHurt";
   public static final String KEY_EQUIP_ON_INTERACT = "Equippable.EquipOnInteract";
   public static final String KEY_CAN_BE_SHEARED = "Equippable.CanBeSheared";
   public static final String KEY_SHEAR_SOUND = "Equippable.ShearSound";
   private NamespacedKey model;
   private EquipmentSlot slot;
   private String equipSound;
   private NamespacedKey cameraOverlay;
   private List<String> entityTypes;
   private Boolean dispensable;
   private Boolean swappable;
   private Boolean damageOnHurt;
   private Boolean equipOnInteract;
   private Boolean canBeSheared;
   private NamespacedKey shearSound;

   public AbstractItemEquippableComponent(MythicItem item) {
      this.slot = this.getDefaultSlot(item.getMaterial());
   }

   public AbstractItemEquippableComponent(MythicItem item, MythicConfig config) {
      String model = config.getString("Equippable.Model");
      if (model != null) {
         if (!model.contains(":")) {
            model = "minecraft:" + model;
         }

         this.model = NamespacedKey.fromString(model);
      }

      String slot = config.getString("Equippable.Slot");
      if (slot != null) {
         this.slot = EquipmentSlot.valueOf(slot);
      } else {
         this.slot = this.getDefaultSlot(item.getMaterial());
      }

      String cameraOverlay = config.getString("Equippable.CameraOverlay");
      if (cameraOverlay != null) {
         if (!cameraOverlay.contains(":")) {
            cameraOverlay = "minecraft:" + cameraOverlay;
         }

         this.cameraOverlay = NamespacedKey.fromString(cameraOverlay);
      }

      if (config.isSet("Equippable.Dispensable")) {
         this.dispensable = config.getBoolean("Equippable.Dispensable");
      }

      if (config.isSet("Equippable.Swappable")) {
         this.swappable = config.getBoolean("Equippable.Swappable");
      }

      if (config.isSet("Equippable.DamageOnHurt")) {
         this.damageOnHurt = config.getBoolean("Equippable.DamageOnHurt");
      }

      if (config.isSet("Equippable.EquipOnInteract")) {
         this.equipOnInteract = config.getBoolean("Equippable.EquipOnInteract");
      }

      if (config.isSet("Equippable.CanBeSheared")) {
         this.canBeSheared = config.getBoolean("Equippable.CanBeSheared");
      }

      String shearSound = config.getString("Equippable.ShearSound");
      if (shearSound != null && !shearSound.isEmpty()) {
         this.shearSound = NamespacedKey.fromString(MythicConfig.normalizeNamespacedKey(shearSound));
         if (this.shearSound == null) {
            MythicLogger.errorItemConfig(item, config, "Invalid Equippable.ShearSound '" + shearSound + "'");
         }
      }

      String equipSoundConfig = config.getString("Equippable.EquipSound");
      if (equipSoundConfig != null) {
         this.equipSound = equipSoundConfig;
      }

      this.entityTypes = config.getStringList("Equippable.EntityTypes", new ArrayList<>());
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyEquippableComponent(this);
   }

   private EquipmentSlot getDefaultSlot(Material material) {
      String mat = material.toString();
      if (mat.contains("_HELMET")) {
         return EquipmentSlot.HEAD;
      } else if (mat.contains("_CHESTPLATE")) {
         return EquipmentSlot.CHEST;
      } else if (mat.contains("_LEGGINGS")) {
         return EquipmentSlot.LEGS;
      } else if (mat.contains("_BOOTS")) {
         return EquipmentSlot.FEET;
      } else {
         return mat.equals("SHIELD") ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
      }
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemEquippableComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$dispensable = this.getDispensable();
         Object other$dispensable = other.getDispensable();
         if (this$dispensable == null ? other$dispensable == null : this$dispensable.equals(other$dispensable)) {
            Object this$swappable = this.getSwappable();
            Object other$swappable = other.getSwappable();
            if (this$swappable == null ? other$swappable == null : this$swappable.equals(other$swappable)) {
               Object this$damageOnHurt = this.getDamageOnHurt();
               Object other$damageOnHurt = other.getDamageOnHurt();
               if (this$damageOnHurt == null ? other$damageOnHurt == null : this$damageOnHurt.equals(other$damageOnHurt)) {
                  Object this$equipOnInteract = this.getEquipOnInteract();
                  Object other$equipOnInteract = other.getEquipOnInteract();
                  if (this$equipOnInteract == null ? other$equipOnInteract == null : this$equipOnInteract.equals(other$equipOnInteract)) {
                     Object this$canBeSheared = this.getCanBeSheared();
                     Object other$canBeSheared = other.getCanBeSheared();
                     if (this$canBeSheared == null ? other$canBeSheared == null : this$canBeSheared.equals(other$canBeSheared)) {
                        Object this$model = this.getModel();
                        Object other$model = other.getModel();
                        if (this$model == null ? other$model == null : this$model.equals(other$model)) {
                           Object this$slot = this.getSlot();
                           Object other$slot = other.getSlot();
                           if (this$slot == null ? other$slot == null : this$slot.equals(other$slot)) {
                              Object this$equipSound = this.getEquipSound();
                              Object other$equipSound = other.getEquipSound();
                              if (this$equipSound == null ? other$equipSound == null : this$equipSound.equals(other$equipSound)) {
                                 Object this$cameraOverlay = this.getCameraOverlay();
                                 Object other$cameraOverlay = other.getCameraOverlay();
                                 if (this$cameraOverlay == null ? other$cameraOverlay == null : this$cameraOverlay.equals(other$cameraOverlay)) {
                                    Object this$entityTypes = this.getEntityTypes();
                                    Object other$entityTypes = other.getEntityTypes();
                                    if (this$entityTypes == null ? other$entityTypes == null : this$entityTypes.equals(other$entityTypes)) {
                                       Object this$shearSound = this.getShearSound();
                                       Object other$shearSound = other.getShearSound();
                                       return this$shearSound == null ? other$shearSound == null : this$shearSound.equals(other$shearSound);
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
      return other instanceof AbstractItemEquippableComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $dispensable = this.getDispensable();
      result = result * 59 + ($dispensable == null ? 43 : $dispensable.hashCode());
      Object $swappable = this.getSwappable();
      result = result * 59 + ($swappable == null ? 43 : $swappable.hashCode());
      Object $damageOnHurt = this.getDamageOnHurt();
      result = result * 59 + ($damageOnHurt == null ? 43 : $damageOnHurt.hashCode());
      Object $equipOnInteract = this.getEquipOnInteract();
      result = result * 59 + ($equipOnInteract == null ? 43 : $equipOnInteract.hashCode());
      Object $canBeSheared = this.getCanBeSheared();
      result = result * 59 + ($canBeSheared == null ? 43 : $canBeSheared.hashCode());
      Object $model = this.getModel();
      result = result * 59 + ($model == null ? 43 : $model.hashCode());
      Object $slot = this.getSlot();
      result = result * 59 + ($slot == null ? 43 : $slot.hashCode());
      Object $equipSound = this.getEquipSound();
      result = result * 59 + ($equipSound == null ? 43 : $equipSound.hashCode());
      Object $cameraOverlay = this.getCameraOverlay();
      result = result * 59 + ($cameraOverlay == null ? 43 : $cameraOverlay.hashCode());
      Object $entityTypes = this.getEntityTypes();
      result = result * 59 + ($entityTypes == null ? 43 : $entityTypes.hashCode());
      Object $shearSound = this.getShearSound();
      return result * 59 + ($shearSound == null ? 43 : $shearSound.hashCode());
   }
}

package io.lumine.mythic.core.drops;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public abstract class EquipSlot {
   public static final EquipSlot HEAD = new EquipSlot("HEAD", EquipmentSlot.HEAD, "HELMET", "HELM", "HAT") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemHead(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemHead();
      }
   };
   public static final EquipSlot CHEST = new EquipSlot("CHEST", EquipmentSlot.CHEST, "CHESTPIECE", "CHESTPLATE", "BODY") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemChest(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemChest();
      }
   };
   public static final EquipSlot LEGS = new EquipSlot("LEGS", EquipmentSlot.LEGS, "LEGGINGS", "PANTS") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemLegs(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemLegs();
      }
   };
   public static final EquipSlot FEET = new EquipSlot("FEET", EquipmentSlot.FEET, "BOOTS", "SHOES") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemFeet(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemFeet();
      }
   };
   public static final EquipSlot HAND = new EquipSlot("HAND", EquipmentSlot.HAND, "WEAPON", "MAINHAND") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemMainHand(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemMainHand();
      }
   };
   public static final EquipSlot OFFHAND = new EquipSlot("OFFHAND", EquipmentSlot.OFF_HAND, "SHIELD") {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
         entity.equipItemOffHand(item);
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return entity.getItemOffhand();
      }
   };
   public static final EquipSlot NONE = new EquipSlot(null, null) {
      @Override
      public void equip(AbstractEntity entity, AbstractItemStack item) {
      }

      @Override
      public Optional<AbstractItemStack> get(AbstractEntity entity) {
         return Optional.empty();
      }
   };
   private static final Map<String, EquipSlot> slots = new ConcurrentHashMap<>();
   @Getter
   private final String slot;
   @Getter
   private final EquipmentSlot bukkitSlot;
   @Getter
   private final String[] aliases;
   @Getter
   private final String slotToCheck;
   private final Collection<String> slotAliases = new HashSet<>();

   public EquipSlot(String slotNBT, EquipmentSlot bukkitSlot, String... aliases) {
      this(slotNBT, slotNBT, bukkitSlot, aliases);
   }

   public EquipSlot(String slotNBT, String slotToCheck, EquipmentSlot bukkitSlot, String... aliases) {
      this.slot = slotNBT;
      this.bukkitSlot = bukkitSlot;
      this.aliases = aliases;
      this.slotToCheck = slotToCheck;
   }

   public void register() {
      if (this.slot != null) {
         slots.put(this.slot, this);
         this.slotAliases.add(this.slot);

         for (String alias : this.aliases) {
            slots.put(alias, this);
            this.slotAliases.add(alias);
         }
      }
   }

   public void unregister() {
      if (this.slot != null) {
         slots.remove(this.slot);
         this.slotAliases.remove(this.slot);

         for (String alias : this.aliases) {
            slots.remove(alias);
            this.slotAliases.remove(alias);
         }
      }
   }

   public boolean matches(String slot) {
      for (String alias : this.slotAliases) {
         if (alias.equalsIgnoreCase(slot)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      return this.slot;
   }

   public abstract void equip(AbstractEntity abstractEntity, AbstractItemStack abstractItemStack);

   public void preventDrop(AbstractEntity entity) {
      if (this.bukkitSlot != null && entity.getBukkitEntity() instanceof Mob mob) {
         EntityEquipment ee = mob.getEquipment();
         if (ee != null) {
            switch (this.bukkitSlot) {
               case HEAD:
                  ee.setHelmetDropChance(0.0F);
                  break;
               case CHEST:
                  ee.setChestplateDropChance(0.0F);
                  break;
               case LEGS:
                  ee.setLeggingsDropChance(0.0F);
                  break;
               case FEET:
                  ee.setBootsDropChance(0.0F);
                  break;
               case HAND:
                  ee.setItemInMainHandDropChance(0.0F);
                  break;
               case OFF_HAND:
                  ee.setItemInOffHandDropChance(0.0F);
            }
         }
      }
   }

   public abstract Optional<AbstractItemStack> get(AbstractEntity abstractEntity);

   public static Collection<EquipSlot> values() {
      return slots.values();
   }

   public static boolean isEquipSlot(String key) {
      for (EquipSlot slot : values()) {
         if (slot.matches(key)) {
            return true;
         }
      }

      return false;
   }

   public static EquipSlot of(String key) {
      for (EquipSlot slot : values()) {
         if (slot.matches(key)) {
            return slot;
         }
      }

      return NONE;
   }

   static {
      HEAD.register();
      CHEST.register();
      LEGS.register();
      FEET.register();
      HAND.register();
      OFFHAND.register();
      NONE.register();
   }
}

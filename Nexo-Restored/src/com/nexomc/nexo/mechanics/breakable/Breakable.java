package com.nexomc.nexo.mechanics.breakable;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.breaker.ToolTypeSpeedModifier;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.nexo.utils.drops.Loot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nJ\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016J\u000e\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001f\u001a\u00020\u0014HÖ\u0081\u0004J\n\u0010 \u001a\u00020!HÖ\u0081\u0004R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "", "hardness", "", "drop", "Lcom/nexomc/nexo/utils/drops/Drop;", "<init>", "(DLcom/nexomc/nexo/utils/drops/Drop;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getHardness", "()D", "setHardness", "(D)V", "getDrop", "()Lcom/nexomc/nexo/utils/drops/Drop;", "setDrop", "(Lcom/nexomc/nexo/utils/drops/Drop;)V", "breakTime", "", "player", "Lorg/bukkit/entity/Player;", "speedMultiplier", "toolSpeedMultiplier", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "com.nexomc:core"}
)
public final class Breakable {
   private double hardness;
   @NotNull
   private Drop drop;

   public Breakable(double hardness, @NotNull Drop drop) {
      super();
      this.hardness = hardness;
      this.drop = drop;
   }

   public final double getHardness() {
      return this.hardness;
   }

   public final void setHardness(double value) {
      this.hardness = value;
   }

   @NotNull
   public final Drop getDrop() {
      return this.drop;
   }

   public final void setDrop(@NotNull Drop drop2) {
      this.drop = drop2;
   }

   public Breakable(@NotNull ConfigurationSection section) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      this(1.0D, com.nexomc.nexo.utils.drops.Drop.Companion.emptyDrop());
      String itemId = NexoYamlKt.getRootId(section);
      this.hardness = section.getDouble("hardness", (double)1.0F);
      Breakable breakable = this;
      ConfigurationSection dropSection = NexoYamlKt.section(section, "drop");
      Drop drop2;
      if (dropSection == null) {
         Loot[] loots = new Loot[]{new Loot(itemId, (double)1.0F)};
         drop2 = new Drop(CollectionsKt.mutableListOf(loots), false, false, itemId);
      } else {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Drop.Companion companion = Drop.Companion;

         block: {
            for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               MechanicFactory mechanicFactory2 = it;
               if (!(it instanceof CustomBlockFactory)) {
                  mechanicFactory2 = null;
               }

               it = (CustomBlockFactory)mechanicFactory2;
               if (it != null) {
                  mechanicFactory = it;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         MechanicFactory mechanicFactory3 = mechanicFactory;
         breakable = this;
         drop2 = companion.createDrop((CustomBlockFactory)mechanicFactory3 != null ? ((CustomBlockFactory)mechanicFactory3).getToolTypes() : null, dropSection, itemId);
      }

      breakable.drop = drop2;
   }

   public final int breakTime(@NotNull Player player) {
      double damage = this.speedMultiplier(player) / this.hardness / (double)30.0F;
      return damage > (double)1.0F ? 0 : (int)Math.ceil((double)1 / damage);
   }

   public final double speedMultiplier(@NotNull Player player) {
      float value;
      float value2;
      Float number;
      Float number2;
      Float submerged;
      boolean flag;
      ItemStack itemStack = player.getInventory().getItemInMainHand();
      ItemStack itemInMainHand = itemStack;
      Iterable iterable = (Iterable)ToolTypeSpeedModifier.VANILLA;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         ToolTypeSpeedModifier it = (ToolTypeSpeedModifier)element;
         int value3 = 0;
         if (it.toolTypes().contains(itemInMainHand.getType())) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      Iterator iterator = iterable.iterator();
      Object obj;
      if (!iterator.hasNext()) {
         obj = null;
      } else {
         Object minElem = iterator.next();
         if (!iterator.hasNext()) {
            obj = minElem;
         } else {
            ToolTypeSpeedModifier it = (ToolTypeSpeedModifier)minElem;
            int value8 = 0;
            float minValue = it.speedModifier();

            do {
               Object obj2 = iterator.next();
               ToolTypeSpeedModifier it_3 = (ToolTypeSpeedModifier)obj2;
               int value4 = 0;
               float speedModifier = it_3.speedModifier();
               if (Float.compare(minValue, speedModifier) > 0) {
                  minElem = obj2;
                  minValue = speedModifier;
               }
            } while(iterator.hasNext());

            obj = minElem;
         }
      }

      block: {
         ToolTypeSpeedModifier toolTypeSpeedModifier = (ToolTypeSpeedModifier)obj;
         if (toolTypeSpeedModifier != null) {
            int value5 = 0;
            ToolTypeSpeedModifier effEnchant = this.drop.isToolEnough(itemInMainHand) ? toolTypeSpeedModifier : null;
            if (effEnchant != null) {
               value = effEnchant.speedModifier();
               break block;
            }
         }

         value = ToolTypeSpeedModifier.EMPTY.speedModifier();
      }

      float multiplier = value;
      if (this.drop.isTypeEnough(itemInMainHand)) {
         multiplier *= 3.3333333F;
      }

      block: {
         Attribute attribute = Attribute.MINING_EFFICIENCY;
         if (attribute != null) {
            Attribute p0 = attribute;
            AttributeInstance attributeInstance = player.getAttribute(p0);
            if (attributeInstance != null) {
               number = (float)attributeInstance.getValue();
               break block;
            }
         }

         number = null;
      }

      Float miningEff = number;
      int effEnchant = itemInMainHand.getEnchantmentLevel(Enchantment.EFFICIENCY);
      if (multiplier > 1.0F) {
         multiplier += miningEff != null ? miningEff : (float)(Math.pow((double)effEnchant, (double)2.0F) + (double)1);
      }

      float value6;
      block: {
         value2 = multiplier;
         Attribute attribute2 = Attribute.BLOCK_BREAK_SPEED;
         if (attribute2 != null) {
            Attribute attribute3 = attribute2;
            int p0 = 0;
            AttributeInstance attributeInstance2 = player.getAttribute(attribute3);
            value2 = multiplier;
            if (attributeInstance2 != null) {
               value6 = (float)attributeInstance2.getValue();
               break block;
            }
         }

         value6 = 1.0F;
      }

      multiplier = value2 * value6;
      if (player.isUnderWater()) {
         block: {
            Attribute attribute4 = Attribute.SUBMERGED_MINING_SPEED;
            if (attribute4 != null) {
               Attribute p0 = attribute4;
               int value7 = 0;
               AttributeInstance attributeInstance3 = player.getAttribute(p0);
               if (attributeInstance3 != null) {
                  number2 = (float)attributeInstance3.getValue();
                  break block;
               }
            }

            number2 = null;
         }

         block: {
            submerged = number2;
            EntityEquipment equipment = player.getEquipment();
            if (equipment != null) {
               ItemStack helmet = equipment.getHelmet();
               if (helmet != null) {
                  flag = helmet.containsEnchantment(Enchantment.AQUA_AFFINITY);
                  break block;
               }
            }

            flag = false;
         }

         boolean aquaAffinity = flag;
         multiplier *= submerged != null ? submerged.floatValue() : (aquaAffinity ? 1.0F : 0.2F);
      }

      if (!player.isOnGround()) {
         multiplier /= 5.0F;
      }

      return (double)multiplier;
   }

   public final double toolSpeedMultiplier(@NotNull Player player) {
      float value;
      Float number;
      ItemStack itemStack = player.getInventory().getItemInMainHand();
      ItemStack itemInMainHand = itemStack;
      Iterable iterable = (Iterable)ToolTypeSpeedModifier.VANILLA;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         ToolTypeSpeedModifier it = (ToolTypeSpeedModifier)element;
         int value2 = 0;
         if (it.toolTypes().contains(itemInMainHand.getType())) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      Iterator iterator = iterable.iterator();
      Object obj;
      if (!iterator.hasNext()) {
         obj = null;
      } else {
         Object minElem = iterator.next();
         if (!iterator.hasNext()) {
            obj = minElem;
         } else {
            ToolTypeSpeedModifier it = (ToolTypeSpeedModifier)minElem;
            int value5 = 0;
            float minValue = it.speedModifier();

            do {
               Object obj2 = iterator.next();
               ToolTypeSpeedModifier it_1 = (ToolTypeSpeedModifier)obj2;
               int value3 = 0;
               float speedModifier = it_1.speedModifier();
               if (Float.compare(minValue, speedModifier) > 0) {
                  minElem = obj2;
                  minValue = speedModifier;
               }
            } while(iterator.hasNext());

            obj = minElem;
         }
      }

      block: {
         ToolTypeSpeedModifier toolTypeSpeedModifier = (ToolTypeSpeedModifier)obj;
         if (toolTypeSpeedModifier != null) {
            int value4 = 0;
            ToolTypeSpeedModifier effEnchant = this.drop.isToolEnough(itemInMainHand) ? toolTypeSpeedModifier : null;
            if (effEnchant != null) {
               value = effEnchant.speedModifier();
               break block;
            }
         }

         value = ToolTypeSpeedModifier.EMPTY.speedModifier();
      }

      float multiplier = value;
      if (this.drop.isTypeEnough(itemInMainHand)) {
         multiplier *= 3.3333333F;
      }

      block: {
         Attribute attribute = Attribute.MINING_EFFICIENCY;
         if (attribute != null) {
            Attribute p0 = attribute;
            AttributeInstance attributeInstance = player.getAttribute(p0);
            if (attributeInstance != null) {
               number = (float)attributeInstance.getValue();
               break block;
            }
         }

         number = null;
      }

      Float miningEff = number;
      int effEnchant = itemInMainHand.getEnchantmentLevel(Enchantment.EFFICIENCY);
      if (multiplier > 1.0F) {
         multiplier += miningEff != null ? miningEff : (float)(Math.pow((double)effEnchant, (double)2.0F) + (double)1);
      }

      return (double)multiplier;
   }

   public final double component1() {
      return this.hardness;
   }

   @NotNull
   public final Drop component2() {
      return this.drop;
   }

   @NotNull
   public final Breakable copy(double hardness, @NotNull Drop drop) {
      return new Breakable(hardness, drop);
   }

   @NotNull
   public String toString() {
      return "Breakable(hardness=" + this.hardness + ", drop=" + this.drop + ")";
   }

   public int hashCode() {
      int result = Double.hashCode(this.hardness);
      result = result * 31 + this.drop.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Breakable)) {
         return false;
      } else {
         Breakable breakable = (Breakable)other;
         if (Double.compare(this.hardness, breakable.hardness) != 0) {
            return false;
         } else {
            return Intrinsics.areEqual(this.drop, breakable.drop);
         }
      }
   }

   public Breakable() {
      this(1.0D, com.nexomc.nexo.utils.drops.Drop.Companion.emptyDrop());
   }
}

package io.lumine.mythic.lib.damage;

import io.lumine.mythic.lib.element.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class DamageMetadata implements Cloneable {
   private final List<DamagePacket> packets = new ArrayList<>();
   @Nullable
   private final DamagePacket initialPacket;
   @Deprecated
   private boolean weaponCrit;
   @Deprecated
   private boolean skillCrit;
   @Deprecated
   private final Set<Element> elementalCrit = new HashSet<>();
   public static final double MINIMAL_DAMAGE = 0.01;

   public DamageMetadata() {
      this.initialPacket = null;
   }

   private DamageMetadata(@Nullable DamagePacket initialPacket) {
      this.initialPacket = initialPacket;
   }

   public DamageMetadata(double damage, DamageType... types) {
      this(damage, null, types);
   }

   public DamageMetadata(double damage, @Nullable Element element, DamageType... types) {
      this.initialPacket = new DamagePacket(damage, element, types);
      this.packets.add(this.initialPacket);
   }

   @Nullable
   public DamagePacket getInitialPacket() {
      return this.initialPacket;
   }

   public boolean isWeaponCriticalStrike() {
      return this.weaponCrit;
   }

   public void registerWeaponCriticalStrike() {
      this.weaponCrit = true;
   }

   public boolean isSkillCriticalStrike() {
      return this.skillCrit;
   }

   public void registerSkillCriticalStrike() {
      this.skillCrit = true;
   }

   public boolean isElementalCriticalStrike(Element el) {
      return this.elementalCrit.contains(el);
   }

   public void registerElementalCriticalStrike(Element el) {
      this.elementalCrit.add(el);
   }

   public double getDamage() {
      double d = 0.0;

      for (DamagePacket packet : this.packets) {
         d += packet.getFinalValue();
      }

      return Math.max(0.01, d);
   }

   public double getDamage(@Nullable Element element) {
      double d = 0.0;

      for (DamagePacket packet : this.packets) {
         if (Objects.equals(packet.getElement(), element)) {
            d += packet.getFinalValue();
         }
      }

      return d;
   }

   public double getDamage(DamageType type) {
      double d = 0.0;

      for (DamagePacket packet : this.packets) {
         if (packet.hasType(type)) {
            d += packet.getFinalValue();
         }
      }

      return d;
   }

   @NotNull
   public Map<Element, Double> mapElementalDamage() {
      Map<Element, Double> mapped = new HashMap<>();

      for (DamagePacket packet : this.packets) {
         if (packet.getElement() != null) {
            mapped.put(packet.getElement(), mapped.getOrDefault(packet.getElement(), 0.0) + packet.getFinalValue());
         }
      }

      return mapped;
   }

   @NotNull
   public List<DamagePacket> getPackets() {
      return this.packets;
   }

   @NotNull
   public Set<DamageType> collectTypes() {
      Set<DamageType> collected = new HashSet<>();

      for (DamagePacket packet : this.packets) {
         for (DamageType type : packet.getTypes()) {
            collected.add(type);
         }
      }

      return collected;
   }

   @NotNull
   public Set<Element> collectElements() {
      Set<Element> collected = new HashSet<>();

      for (DamagePacket packet : this.packets) {
         if (packet.getElement() != null) {
            collected.add(packet.getElement());
         }
      }

      return collected;
   }

   public boolean hasType(DamageType type) {
      for (DamagePacket packet : this.packets) {
         if (packet.hasType(type)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasElement(@Nullable Element element) {
      for (DamagePacket packet : this.packets) {
         if (Objects.equals(packet.getElement(), element)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public DamageMetadata add(double value, @NotNull DamageType... types) {
      this.packets.add(new DamagePacket(value, types));
      return this;
   }

   @NotNull
   public DamageMetadata add(double value, @Nullable Element element, @NotNull DamageType... types) {
      this.packets.add(new DamagePacket(value, element, types));
      return this;
   }

   @NotNull
   public DamageMetadata multiplicativeModifier(double coefficient) {
      for (DamagePacket packet : this.packets) {
         packet.multiplicativeModifier(coefficient);
      }

      return this;
   }

   @NotNull
   public DamageMetadata additiveModifier(double multiplier) {
      for (DamagePacket packet : this.packets) {
         packet.additiveModifier(multiplier);
      }

      return this;
   }

   @NotNull
   public DamageMetadata multiplicativeModifier(double coefficient, @NotNull DamageType damageType) {
      for (DamagePacket packet : this.packets) {
         if (packet.hasType(damageType)) {
            packet.multiplicativeModifier(coefficient);
         }
      }

      return this;
   }

   @NotNull
   public DamageMetadata multiplicativeModifier(double coefficient, @Nullable Element element) {
      for (DamagePacket packet : this.packets) {
         if (Objects.equals(packet.getElement(), element)) {
            packet.multiplicativeModifier(coefficient);
         }
      }

      return this;
   }

   @NotNull
   public DamageMetadata additiveModifier(double multiplier, @NotNull DamageType damageType) {
      for (DamagePacket packet : this.packets) {
         if (packet.hasType(damageType)) {
            packet.additiveModifier(multiplier);
         }
      }

      return this;
   }

   @NotNull
   public DamageMetadata additiveModifier(double coefficient, @NotNull Element element) {
      for (DamagePacket packet : this.packets) {
         if (Objects.equals(packet.getElement(), element)) {
            packet.additiveModifier(coefficient);
         }
      }

      return this;
   }

   public DamageMetadata clone() {
      DamageMetadata clone = new DamageMetadata(this.initialPacket);

      for (DamagePacket packet : this.packets) {
         clone.packets.add(packet.clone());
      }

      return clone;
   }

   @Override
   public String toString() {
      StringBuilder damageTypes = new StringBuilder("§3Damage Meta{");
      boolean packetAppended = false;

      for (DamagePacket packet : this.packets) {
         if (packetAppended) {
            damageTypes.append("§3;");
         }

         packetAppended = true;
         damageTypes.append(packet);
      }

      return damageTypes.append("§3}").toString();
   }
}

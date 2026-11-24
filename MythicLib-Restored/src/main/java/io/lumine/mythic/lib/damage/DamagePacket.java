package io.lumine.mythic.lib.damage;

import io.lumine.mythic.lib.element.Element;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamagePacket implements Cloneable {
   @NotNull
   private DamageType[] types;
   private double value;
   private double additiveModifiers;
   private double multiplicativeModifiers = 1.0;
   @Nullable
   private Element element;

   public DamagePacket(double value, @NotNull DamageType... types) {
      this(value, null, types);
   }

   public DamagePacket(double value, @Nullable Element element, @NotNull DamageType... types) {
      this.value = value;
      this.types = types;
      this.element = element;
   }

   public double getValue() {
      return this.value;
   }

   @NotNull
   public DamageType[] getTypes() {
      return this.types;
   }

   @Nullable
   public Element getElement() {
      return this.element;
   }

   public void setTypes(@NotNull DamageType[] types) {
      this.types = Objects.requireNonNull(types, "Damage type array cannot be null");
   }

   public void setValue(double value) {
      Validate.isTrue(value >= 0.0, "Value cannot be negative");
      this.value = value;
   }

   public void setElement(@Nullable Element element) {
      this.element = element;
   }

   public void multiplicativeModifier(double coefficient) {
      Validate.isTrue(coefficient >= 0.0, "Coefficient cannot be negative");
      this.multiplicativeModifiers *= coefficient;
   }

   public void additiveModifier(double multiplier) {
      this.additiveModifiers += multiplier;
   }

   public double getFinalValue() {
      return this.value * Math.max(0.0, 1.0 + this.additiveModifiers) * this.multiplicativeModifiers;
   }

   public boolean hasType(DamageType type) {
      for (DamageType checked : this.types) {
         if (checked == type) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      StringBuilder damageTypes = new StringBuilder();
      damageTypes.append("§e")
         .append("(")
         .append(this.value)
         .append("*")
         .append(this.additiveModifiers)
         .append("*")
         .append(this.multiplicativeModifiers)
         .append(")")
         .append("x");
      boolean damageAppended = false;

      for (DamageType type : this.types) {
         if (damageAppended) {
            damageTypes.append("§3/");
         }

         damageAppended = true;
         switch (type) {
            case WEAPON:
               damageTypes.append("§7");
               break;
            case PHYSICAL:
               damageTypes.append("§8");
               break;
            case PROJECTILE:
               damageTypes.append("§a");
               break;
            case MAGIC:
               damageTypes.append("§9");
               break;
            case SKILL:
               damageTypes.append("§f");
               break;
            default:
               damageTypes.append("§c");
         }

         damageTypes.append(type);
         if (this.element != null) {
            damageTypes.append(",El=").append(this.element.getId());
         }
      }

      return damageTypes.toString();
   }

   public DamagePacket clone() {
      DamagePacket clone = new DamagePacket(this.value, this.types);
      clone.additiveModifiers = this.additiveModifiers;
      clone.multiplicativeModifiers = this.multiplicativeModifiers;
      clone.element = this.element;
      return clone;
   }
}

package io.lumine.mythic.lib.element;

import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ElementalDamagePacket extends DamagePacket {
   @NotNull
   private Element element;

   public ElementalDamagePacket(double value, @NotNull Element element, @NotNull DamageType... types) {
      super(value, types);
      this.element = Objects.requireNonNull(element, "Element cannot be null");
   }

   @NotNull
   @Override
   public Element getElement() {
      return this.element;
   }

   @Override
   public void setElement(@NotNull Element element) {
      this.element = Objects.requireNonNull(element, "Element cannot be null");
   }
}

package io.lumine.mythic.lib.api.itemtype;

import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaType implements ItemType {
   private final Material material;

   public VanillaType(Material material) {
      this.material = Objects.requireNonNull(material, "Material cannot be null");
   }

   @Override
   public boolean matches(ItemStack stack) {
      return stack.getType() == this.material;
   }

   @Override
   public String display() {
      return this.material.name();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VanillaType that = (VanillaType)o;
         return this.material == that.material;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.material);
   }
}

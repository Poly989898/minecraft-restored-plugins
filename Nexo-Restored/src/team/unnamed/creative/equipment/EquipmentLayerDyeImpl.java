package team.unnamed.creative.equipment;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record EquipmentLayerDyeImpl(@Nullable Integer colorWhenUndyed) implements EquipmentLayerDye {
   static final EquipmentLayerDye EMPTY = new EquipmentLayerDyeImpl((Integer)null);

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{colorWhenUndyed=" + this.colorWhenUndyed + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         EquipmentLayerDyeImpl that = (EquipmentLayerDyeImpl)o;
         return Objects.equals(this.colorWhenUndyed, that.colorWhenUndyed);
      } else {
         return false;
      }
   }
}

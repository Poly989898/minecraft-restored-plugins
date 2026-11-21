package team.unnamed.creative.item.property;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record NoFieldItemStringPropertyImpl(Key key) implements NoFieldItemStringProperty {
   static final NoFieldItemStringProperty CHARGE_TYPE = new NoFieldItemStringPropertyImpl("charge_type");
   static final NoFieldItemStringProperty CONTEXT_DIMENSION = new NoFieldItemStringPropertyImpl("context_dimension");
   static final NoFieldItemStringProperty CONTEXT_ENTITY_TYPE = new NoFieldItemStringPropertyImpl("context_entity_type");
   static final NoFieldItemStringProperty DISPLAY_CONTEXT = new NoFieldItemStringPropertyImpl("display_context");
   static final NoFieldItemStringProperty MAIN_HAND = new NoFieldItemStringPropertyImpl("main_hand");
   static final NoFieldItemStringProperty TRIM_MATERIAL = new NoFieldItemStringPropertyImpl("trim_material");

   NoFieldItemStringPropertyImpl(final @NotNull Key key) {
      this.key = (Key)Objects.requireNonNull(key, "key");
   }

   private NoFieldItemStringPropertyImpl(@Subst("context_dimension") @NotNull String keyValue) {
      this(Key.key("minecraft", keyValue));
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         NoFieldItemStringPropertyImpl that = (NoFieldItemStringPropertyImpl)o;
         return this.key.equals(that.key);
      } else {
         return false;
      }
   }
}

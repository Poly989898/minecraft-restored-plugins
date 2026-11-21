package team.unnamed.creative.item.property;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record NoFieldItemBooleanPropertyImpl(Key key) implements NoFieldItemBooleanProperty {
   static final NoFieldItemBooleanProperty BROKEN = new NoFieldItemBooleanPropertyImpl("broken");
   static final NoFieldItemBooleanProperty BUNDLE_HAS_SELECTED_ITEM = new NoFieldItemBooleanPropertyImpl("bundle/has_selected_item");
   static final NoFieldItemBooleanProperty CARRIED = new NoFieldItemBooleanPropertyImpl("carried");
   static final NoFieldItemBooleanProperty DAMAGED = new NoFieldItemBooleanPropertyImpl("damaged");
   static final NoFieldItemBooleanProperty EXTENDED_VIEW = new NoFieldItemBooleanPropertyImpl("extended_view");
   static final NoFieldItemBooleanProperty FISHING_ROD_CAST = new NoFieldItemBooleanPropertyImpl("fishing_rod/cast");
   static final NoFieldItemBooleanProperty SELECTED = new NoFieldItemBooleanPropertyImpl("selected");
   static final NoFieldItemBooleanProperty USING_ITEM = new NoFieldItemBooleanPropertyImpl("using_item");
   static final NoFieldItemBooleanProperty VIEW_ENTITY = new NoFieldItemBooleanPropertyImpl("view_entity");

   NoFieldItemBooleanPropertyImpl(final @NotNull Key key) {
      this.key = (Key)Objects.requireNonNull(key, "key");
   }

   NoFieldItemBooleanPropertyImpl(@Subst("minecraft:broken") @NotNull String key) {
      this(Key.key(key));
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
         NoFieldItemBooleanPropertyImpl that = (NoFieldItemBooleanPropertyImpl)o;
         return this.key.equals(that.key);
      } else {
         return false;
      }
   }
}

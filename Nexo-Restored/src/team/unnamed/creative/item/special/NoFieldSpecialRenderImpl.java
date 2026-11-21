package team.unnamed.creative.item.special;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record NoFieldSpecialRenderImpl(Key key) implements NoFieldSpecialRender {
   static final NoFieldSpecialRender CONDUIT = new NoFieldSpecialRenderImpl("conduit");
   static final NoFieldSpecialRender DECORATED_POT = new NoFieldSpecialRenderImpl("decorated_pot");
   static final NoFieldSpecialRender SHIELD = new NoFieldSpecialRenderImpl("shield");
   static final NoFieldSpecialRender TRIDENT = new NoFieldSpecialRenderImpl("trident");
   static final NoFieldSpecialRender PLAYER_HEAD = new NoFieldSpecialRenderImpl("player_head");

   NoFieldSpecialRenderImpl(final @NotNull Key key) {
      this.key = (Key)Objects.requireNonNull(key, "key");
   }

   private NoFieldSpecialRenderImpl(@Subst("conduit") @NotNull String keyValue) {
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
         NoFieldSpecialRenderImpl that = (NoFieldSpecialRenderImpl)o;
         return this.key.equals(that.key);
      } else {
         return false;
      }
   }
}

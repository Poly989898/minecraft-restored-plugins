package team.unnamed.creative.metadata.texture;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface TextureMeta extends MetadataPart {
   boolean DEFAULT_BLUR = false;
   boolean DEFAULT_CLAMP = false;

   @Contract("_, _ -> new")
   static @NotNull TextureMeta texture(boolean blur, boolean clamp) {
      return new TextureMetaImpl(blur, clamp);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("_, _ -> new")
   static @NotNull TextureMeta of(boolean blur, boolean clamp) {
      return new TextureMetaImpl(blur, clamp);
   }

   boolean blur();

   boolean clamp();
}

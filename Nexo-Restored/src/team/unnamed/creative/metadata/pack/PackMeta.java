package team.unnamed.creative.metadata.pack;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.metadata.MetadataPart;

@NonExtendable
public interface PackMeta extends MetadataPart {
   @NotNull PackFormat formats();

   @NotNull Component description();

   static @NotNull PackMeta of(@NotNull PackFormat format, @NotNull Component description) {
      return new PackMetaImpl(format, description);
   }
}

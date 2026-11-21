package team.unnamed.creative.serialize.minecraft;

import java.util.Collection;
import java.util.function.Function;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;

@Internal
public interface ResourceCategory<T extends Keyed & ResourcePackPart> {
   @NotNull String folder(PackFormat var1);

   @NotNull String extension(PackFormat var1);

   @NotNull ResourceDeserializer<T> deserializer();

   @NotNull Function<ResourceContainer, Collection<T>> lister();

   @NotNull ResourceSerializer<T> serializer();

   default @NotNull String pathOf(@NotNull T resource, PackFormat packFormat) {
      Key key = resource.key();
      String var10000 = key.namespace();
      return "assets/" + var10000 + "/" + this.folder(packFormat) + "/" + key.value() + this.extension(packFormat);
   }
}

package team.unnamed.creative.serialize.minecraft;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;

@Internal
public final class ResourceCategoryImpl<T extends Keyed & ResourcePackPart> implements ResourceCategory<T> {
   private final String folder;
   private final String extension;
   private final Function<ResourceContainer, Collection<T>> lister;
   private final ResourceDeserializer<T> deserializer;
   private final ResourceSerializer<T> serializer;

   public ResourceCategoryImpl(String folder, String extension, Function<ResourceContainer, Collection<T>> lister, ResourceDeserializer<T> deserializer, ResourceSerializer<T> serializer) {
      this.folder = (String)Objects.requireNonNull(folder, "folder");
      this.extension = (String)Objects.requireNonNull(extension, "extension");
      this.lister = (Function)Objects.requireNonNull(lister, "lister");
      this.deserializer = (ResourceDeserializer)Objects.requireNonNull(deserializer, "deserializer");
      this.serializer = (ResourceSerializer)Objects.requireNonNull(serializer, "serializer");
   }

   public <TCodec extends ResourceSerializer<T> & ResourceDeserializer<T>> ResourceCategoryImpl(String folder, String extension, Function<ResourceContainer, Collection<T>> lister, TCodec codec) {
      this(folder, extension, lister, codec, codec);
   }

   public @NotNull String folder(PackFormat packFormat) {
      return this.folder;
   }

   public @NotNull String extension(PackFormat packFormat) {
      return this.extension;
   }

   public @NotNull ResourceDeserializer<T> deserializer() {
      return this.deserializer;
   }

   public @NotNull Function<ResourceContainer, Collection<T>> lister() {
      return this.lister;
   }

   public @NotNull ResourceSerializer<T> serializer() {
      return this.serializer;
   }
}

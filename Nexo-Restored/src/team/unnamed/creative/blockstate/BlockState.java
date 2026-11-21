package team.unnamed.creative.blockstate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

@NonExtendable
public interface BlockState extends ResourcePackPart, Keyed {
   @NotNull Key key();

   Map<String, MultiVariant> variants();

   List<Selector> multipart();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.blockState(this);
   }

   static BlockState of(Key key, Map<String, MultiVariant> variants, List<Selector> multipart) {
      return new BlockStateImpl(key, variants, multipart);
   }

   static BlockState of(Key key, Map<String, MultiVariant> variants) {
      return new BlockStateImpl(key, variants, Collections.emptyList());
   }

   static BlockState of(Key key, List<Selector> multipart) {
      return new BlockStateImpl(key, Collections.emptyMap(), multipart);
   }

   static BlockState of(Key key, Selector... multipart) {
      return new BlockStateImpl(key, Collections.emptyMap(), Arrays.asList(multipart));
   }
}

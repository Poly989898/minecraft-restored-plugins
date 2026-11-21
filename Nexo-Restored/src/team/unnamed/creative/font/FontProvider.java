package team.unnamed.creative.font;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FontProvider {
   @Contract("_, _, _, _ -> new")
   static @NotNull BitMapFontProvider bitMap(@NotNull Key file, int height, int ascent, @NotNull List<String> characters) {
      return new BitMapFontProviderImpl(file, height, ascent, characters);
   }

   @Contract("-> new")
   static BitMapFontProvider.Builder bitMap() {
      return new BitMapFontProviderImpl.BuilderImpl();
   }

   static SpaceFontProvider.Builder space() {
      return new SpaceFontProvider.Builder();
   }

   static SpaceFontProvider space(Map<String, Integer> advances) {
      return new SpaceFontProvider(advances);
   }

   static TrueTypeFontProvider.Builder trueType() {
      return new TrueTypeFontProvider.Builder();
   }

   static @NotNull ReferenceFontProvider reference(@NotNull Key id) {
      return new ReferenceFontProviderImpl(id);
   }

   static @NotNull ReferenceFontProvider reference(@NotNull Font font) {
      return reference(font.key());
   }

   @Contract("_, _ -> new")
   static @NotNull UnihexFontProvider unihex(@NotNull Key file, @NotNull List<UnihexFontProvider.SizeOverride> sizes) {
      return new UnihexFontProviderImpl(file, sizes);
   }

   @Contract("-> new")
   static UnihexFontProvider.@NotNull Builder unihex() {
      return new UnihexFontProviderImpl.BuilderImpl();
   }
}

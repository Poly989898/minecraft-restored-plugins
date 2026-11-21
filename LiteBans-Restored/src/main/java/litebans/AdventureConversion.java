package litebans;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class AdventureConversion {
   public static @NotNull TextComponent computeTextComponent(CharSequence message) {
      return obtainLegacyComponentSerializer().deserialize(message.toString());
   }

   public static Component computeComponent(CharSequence message) {
      return GsonComponentSerializer.gson().deserialize(message.toString());
   }

   public static @NotNull LegacyComponentSerializer obtainLegacyComponentSerializer() {
      return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build();
   }
}

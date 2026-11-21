package litebans;

import org.jetbrains.annotations.Nullable;

public final class EmbedField {
   private final String name;
   private final String value;
   private final boolean inline;
   final DiscordEmbed kotlinHelper;

   public EmbedField(@Nullable DiscordEmbed kotlinHelper, @Nullable String text, String text2, boolean flag) {
      this.kotlinHelper = kotlinHelper;
      this.name = text;
      this.value = text2;
      this.inline = flag;
   }

   public final String getName() {
      return this.name;
   }

   public final String getValue() {
      return this.value;
   }

   public final boolean isInline() {
      return this.inline;
   }
}

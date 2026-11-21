package litebans;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DiscordEmbed {
   private String title;
   private String description;
   private String url;
   private int color;
   private EmbedFooter footer;
   private String thumbnailUrl;
   private EmbedImage image;
   private EmbedAuthor author;
   private final List fields;

   public DiscordEmbed(
      @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   int value,
   @Nullable EmbedFooter namePair2,
   @Nullable String text4,
   @Nullable EmbedImage kotlinHelper2,
   @Nullable EmbedAuthor kotlinHelper3,
   @NotNull List list
   ) {
      this.title = text;
      this.description = text2;
      this.url = text3;
      this.color = value;
      this.footer = namePair2;
      this.thumbnailUrl = text4;
      this.image = kotlinHelper2;
      this.author = kotlinHelper3;
      this.fields = list;
   }

   public DiscordEmbed(String text, String text2, String text3, int value, EmbedFooter namePair2, String text4, EmbedImage kotlinHelper2, EmbedAuthor kotlinHelper3, List list, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 1) != 0) {
         text = null;
      }

      if ((value2 & 2) != 0) {
         text2 = null;
      }

      if ((value2 & 4) != 0) {
         text3 = null;
      }

      if ((value2 & 8) != 0) {
         value = 16777215;
      }

      if ((value2 & 16) != 0) {
         namePair2 = null;
      }

      if ((value2 & 32) != 0) {
         text4 = null;
      }

      if ((value2 & 64) != 0) {
         kotlinHelper2 = null;
      }

      if ((value2 & 128) != 0) {
         kotlinHelper3 = null;
      }

      if ((value2 & 256) != 0) {
         list = new ArrayList();
      }

      this(text, text2, text3, value, namePair2, text4, kotlinHelper2, kotlinHelper3, list);
   }

   public final String getTitle() {
      return this.title;
   }

   public final String getDescription() {
      return this.description;
   }

   public final String getUrl() {
      return this.url;
   }

   public final int getColor() {
      return this.color;
   }

   public final EmbedFooter getFooter() {
      return this.footer;
   }

   public final String getThumbnailUrl() {
      return this.thumbnailUrl;
   }

   public final EmbedImage getImage() {
      return this.image;
   }

   public final EmbedAuthor getAuthor() {
      return this.author;
   }

   public final List getFields() {
      return this.fields;
   }

   public final DiscordEmbed computeKotlinHelper(@Nullable String text, @Nullable String text2, boolean flag) {
      this.fields.add(new EmbedField(this, text, text2, flag));
      return this;
   }

   public DiscordEmbed() {
      this(null, null, null, 0, null, null, null, null, null, 511, null);
   }
}

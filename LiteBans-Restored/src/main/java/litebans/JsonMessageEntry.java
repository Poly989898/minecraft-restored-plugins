package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonMessageEntry {
   private final String text2;
   private final JSONObject jSONObject;
   private final JsonMessageTemplate[] jsonMessageTemplates;

   public JsonMessageEntry(@NotNull String text, @Nullable JSONObject jsonObject, @NotNull JsonMessageTemplate[] jsonMessageTemplates) {
      this.text2 = text;
      this.jSONObject = jsonObject;
      this.jsonMessageTemplates = jsonMessageTemplates;
   }

   public final String getText2() {
      return this.text2;
   }

   public final JSONObject getJSONObject() {
      return this.jSONObject;
   }

   public final JsonMessageTemplate[] getJsonMessageTemplates() {
      return this.jsonMessageTemplates;
   }
}

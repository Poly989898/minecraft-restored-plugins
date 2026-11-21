package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "centertext",
   version = "5.10.1"
)
public class CenterTextPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private List<Character> singlePixels = new ArrayList<>(List.of(' ', ' ', ' ', ' '));
   private PlaceholderString value;
   private PlaceholderInt pixelWidth;
   private PlaceholderBoolean bold;

   public CenterTextPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
      if (metaContext.config != null) {
         this.value = metaContext.config.getPlaceholderString(new String[]{"value", "v", "text", "line"}, "");
         this.pixelWidth = metaContext.config.getPlaceholderInteger(new String[]{"pixelwidth", "width", "w", "pw"}, 100);
         String singlePixelsString = metaContext.config.getString(new String[]{"singlepixelunicodes", "spus", "spu", "singleunicodes"}, " , , , ");
         this.bold = metaContext.config.getPlaceholderBoolean(new String[]{"bold", "b"}, false);
         String[] singlePixelArray = singlePixelsString.split(",");

         for (int i = 0; i < 4; i++) {
            String unicode = i < singlePixelArray.length ? singlePixelArray[i] : " ";
            this.singlePixels.set(i, unicode.charAt(0));
         }
      }
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      String line = this.value.get(placeholderContext);
      return MythicUtil.getCenteredText(line, this.pixelWidth.get(placeholderContext), this.bold.get(placeholderContext), this.singlePixels);
   }
}

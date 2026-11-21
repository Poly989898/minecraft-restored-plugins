package io.lumine.mythic.core.menus.custom.icons;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.menus.custom.CustomMenuContext;

public class IconStaticLoreLine implements IconLoreTemplateSingleLine {
   private final PlaceholderString line;

   public IconStaticLoreLine(String line) {
      this.line = PlaceholderString.of(line);
   }

   @Override
   public boolean isApplicable(CustomMenuContext context) {
      return true;
   }

   @Override
   public String getLine(CustomMenuContext context) {
      return this.line.get(context);
   }
}

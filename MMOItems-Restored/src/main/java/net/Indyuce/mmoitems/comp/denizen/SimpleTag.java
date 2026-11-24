package net.Indyuce.mmoitems.comp.denizen;

import com.denizenscript.denizencore.objects.ObjectTag;

public abstract class SimpleTag implements ObjectTag {
   private String prefix;

   public String getPrefix() {
      return this.prefix;
   }

   public ObjectTag setPrefix(String text) {
      this.prefix = text;
      return this;
   }
}

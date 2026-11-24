package net.Indyuce.mmoitems.util;

import java.util.function.Supplier;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import org.jetbrains.annotations.NotNull;

public class LanguageFile extends ConfigFile {
   private boolean change;

   public LanguageFile(String text) {
      super(MMOItems.plugin, "/language", text);
   }

   @NotNull
   public String computeTranslation(String text, Supplier<String> supplier) {
      String text2 = this.getConfig().getString(text);
      if (text2 == null) {
         this.change = true;
         this.getConfig().set(text, text2 = (String)supplier.get());
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not find translation for '" + text + "', generating it");
      }

      return text2;
   }

   @Override
   public void save() {
      if (this.change) {
         super.save();
      }
   }
}

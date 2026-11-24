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
      String translation = this.getConfig().getString(text);
      if (translation == null) {
         this.change = true;
         this.getConfig().set(text, translation = (String)supplier.get());
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not find translation for '" + text + "', generating it");
      }

      return translation;
   }

   @Override
   public void save() {
      if (this.change) {
         super.save();
      }
   }
}

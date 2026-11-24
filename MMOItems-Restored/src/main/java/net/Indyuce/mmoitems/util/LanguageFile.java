package net.Indyuce.mmoitems.util;

import java.util.function.Supplier;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import org.jetbrains.annotations.NotNull;

public class LanguageFile extends ConfigFile {
   private boolean change;

   public LanguageFile(String var1) {
      super(MMOItems.plugin, "/language", var1);
   }

   @NotNull
   public String computeTranslation(String var1, Supplier<String> var2) {
      String var3 = this.getConfig().getString(var1);
      if (var3 == null) {
         this.change = true;
         this.getConfig().set(var1, var3 = (String)var2.get());
         MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not find translation for '" + var1 + "', generating it");
      }

      return var3;
   }

   @Override
   public void save() {
      if (this.change) {
         super.save();
      }
   }
}

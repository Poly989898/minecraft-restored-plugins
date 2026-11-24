package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.recipe.gui.RecipeEditorGUI;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TierManager implements Reloadable {
   private final Map<String, ItemTier> tiers = new HashMap<>();

   public TierManager() {
      this.reload();
   }

   @Override
   public void reload() {
      this.tiers.clear();
      FriendlyFeedbackProvider var1 = new FriendlyFeedbackProvider(FFPMMOItems.get());
      var1.activatePrefix(true, "Tiers");
      ConfigFile var2 = new ConfigFile("item-tiers");

      for (String var4 : var2.getConfig().getKeys(false)) {
         ConfigurationSection var5 = RecipeEditorGUI.getSection(var2.getConfig(), var4);

         try {
            this.register(new ItemTier(var5));
         } catch (IllegalArgumentException var7) {
            var1.log(FriendlyFeedbackCategory.ERROR, "Cannot register tier '$u{0}$b';$f {1}", new String[]{var4, var7.getMessage()});
         }
      }

      var1.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
      var1.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
   }

   public void register(@NotNull ItemTier var1) {
      this.tiers.put(var1.getId(), var1);
   }

   public boolean has(@Nullable String var1) {
      return var1 == null ? false : this.tiers.containsKey(var1);
   }

   @NotNull
   public ItemTier getOrThrow(@Nullable String var1) {
      Validate.isTrue(
         this.tiers.containsKey(var1), FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find tier with ID '$r{0}$b'", new String[]{var1})
      );
      return this.tiers.get(var1);
   }

   @Nullable
   public ItemTier get(@Nullable String var1) {
      return var1 == null ? null : this.tiers.get(var1);
   }

   @NotNull
   public Collection<ItemTier> getAll() {
      return this.tiers.values();
   }

   @Deprecated
   @Nullable
   public ItemTier findTier(@NotNull MMOItem var1) {
      return var1.getTier();
   }
}

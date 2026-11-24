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
      FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
      friendlyFeedbackProvider.activatePrefix(true, "Tiers");
      ConfigFile configFile = new ConfigFile("item-tiers");

      for (String key : configFile.getConfig().getKeys(false)) {
         ConfigurationSection section = RecipeEditorGUI.getSection(configFile.getConfig(), key);

         try {
            this.register(new ItemTier(section));
         } catch (IllegalArgumentException exception) {
            friendlyFeedbackProvider.log(FriendlyFeedbackCategory.ERROR, "Cannot register tier '$u{0}$b';$f {1}", new String[]{key, exception.getMessage()});
         }
      }

      friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.ERROR, MMOItems.getConsole());
      friendlyFeedbackProvider.sendTo(FriendlyFeedbackCategory.FAILURE, MMOItems.getConsole());
   }

   public void register(@NotNull ItemTier itemTier) {
      this.tiers.put(itemTier.getId(), itemTier);
   }

   public boolean has(@Nullable String text) {
      return text == null ? false : this.tiers.containsKey(text);
   }

   @NotNull
   public ItemTier getOrThrow(@Nullable String text) {
      Validate.isTrue(
         this.tiers.containsKey(text), FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not find tier with ID '$r{0}$b'", new String[]{text})
      );
      return this.tiers.get(text);
   }

   @Nullable
   public ItemTier get(@Nullable String text) {
      return text == null ? null : this.tiers.get(text);
   }

   @NotNull
   public Collection<ItemTier> getAll() {
      return this.tiers.values();
   }

   @Deprecated
   @Nullable
   public ItemTier findTier(@NotNull MMOItem mmoItem) {
      return mmoItem.getTier();
   }
}

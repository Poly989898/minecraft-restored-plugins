package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.ItemOptions;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractClassSelect extends EditableInventory {
   public AbstractClassSelect(String id) {
      super(id);
   }

   public abstract class AbstractClassGeneratedInventory extends GeneratedInventory {
      @Nullable
      public final Runnable profileCallback;
      public final PlayerData playerData;

      public AbstractClassGeneratedInventory(Navigator navigator, PlayerData playerData, @Nullable Runnable profileCallback) {
         super(navigator, AbstractClassSelect.this);
         this.playerData = playerData;
         this.profileCallback = profileCallback;
      }

      public void open() {
         if (this.profileCallback != null) {
            this.getNavigator().blockClosing();
         }

         super.open();
      }
   }

   public abstract class AbstractClassItem<T extends GeneratedInventory> extends SimpleItem<T> {
      protected final PlayerClass playerClass;

      public AbstractClassItem(ConfigurationSection config, int substringIndex) {
         super(config);
         Validate.isTrue(config.getString("function").length() > substringIndex, "Couldn't find the class associated to: " + config.getString("function"));
         String classId = UtilityMethods.enumName(config.getString("function").substring(substringIndex));
         this.playerClass = MMOCore.plugin.classManager.getOrThrow(classId);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void preprocessLore(@NotNull T inv, int n, @NotNull List<String> lore) {
         int index = lore.indexOf("{lore}");
         if (index >= 0) {
            lore.remove(index);
            lore.addAll(index, this.playerClass.getDescription());
         }

         index = lore.indexOf("{attribute-lore}");
         if (index >= 0) {
            lore.remove(index);
            lore.addAll(index, this.playerClass.getAttributeDescription());
         }
      }

      public ItemStack getDisplayedItem(T inv, int n) {
         ItemOptions options = n == 0 ? ItemOptions.item(n, this.playerClass.getIcon()) : ItemOptions.index(n);
         return super.getDisplayedItem(inv, options);
      }

      @NotNull
      public Placeholders getPlaceholders(T inv, int n) {
         Placeholders placeholders = super.getPlaceholders(inv, n);
         placeholders.register("name", this.playerClass.getName());
         return placeholders;
      }
   }
}

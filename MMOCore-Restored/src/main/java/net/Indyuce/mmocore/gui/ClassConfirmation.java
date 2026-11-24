package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.GoBackItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import net.Indyuce.mmocore.player.ClassDataContainer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassConfirmation extends AbstractClassSelect {
   private final PlayerClass playerClass;

   public ClassConfirmation(PlayerClass playerClass, boolean isDefault) {
      super("class-confirm-" + (isDefault ? "default" : UtilityMethods.ymlName(playerClass.getId())));
      this.playerClass = playerClass;
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equalsIgnoreCase("yes")) {
         return new ClassConfirmation.YesItem(config);
      } else {
         return function.equalsIgnoreCase("back") ? new GoBackItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(AbstractClassSelect.AbstractClassGeneratedInventory prev, boolean forceSetClass) {
      return new ClassConfirmation.ClassConfirmationInventory(prev.getNavigator(), prev.playerData, this.playerClass, forceSetClass, prev.profileCallback);
   }

   public class ClassConfirmationInventory extends AbstractClassSelect.AbstractClassGeneratedInventory {
      private final PlayerClass profess;
      private final boolean forceSetClass;

      public ClassConfirmationInventory(
         Navigator navigator, PlayerData playerData, PlayerClass profess, boolean forceSetClass, @Nullable Runnable profileCallback
      ) {
         super(navigator, playerData, profileCallback);
         this.profess = profess;
         this.forceSetClass = forceSetClass;
      }

      @NotNull
      public String getRawName() {
         return this.guiName.replace("{class}", this.profess.getName());
      }
   }

   public class UnlockedItem extends PhysicalItem<ClassConfirmation.ClassConfirmationInventory> {
      public UnlockedItem(ConfigurationSection config) {
         super(config);
      }

      public Placeholders getPlaceholders(ClassConfirmation.ClassConfirmationInventory inv, int n) {
         PlayerClass profess = inv.profess;
         ClassDataContainer info = inv.forceSetClass ? inv.playerData : inv.playerData.getClassInfo(profess);
         Placeholders holders = new Placeholders();
         double nextLevelExp = inv.playerData.getLevelUpExperience();
         double ratio = info.getExperience() / nextLevelExp;
         StringBuilder bar = new StringBuilder(ChatColor.BOLD + "");
         int chars = (int)(ratio * 20.0);

         for (int j = 0; j < 20; j++) {
            bar.append(j == chars ? "" + ChatColor.WHITE + ChatColor.BOLD : "").append("|");
         }

         holders.register("percent", this.ONE_DIGIT.format(ratio * 100.0));
         holders.register("progress", bar.toString());
         holders.register("class", profess.getName());
         holders.register("unlocked_skills", info.mapSkillLevels().size());
         holders.register("class_skills", profess.getSkills().size());
         holders.register("next_level", nextLevelExp + "");
         holders.register("level", info.getLevel());
         holders.register("exp", info.getExperience());
         holders.register("skill_points", info.getSkillPoints());
         return holders;
      }
   }

   public class YesItem extends InventoryItem<ClassConfirmation.ClassConfirmationInventory> {
      private final InventoryItem<ClassConfirmation.ClassConfirmationInventory> unlocked;
      private final InventoryItem<ClassConfirmation.ClassConfirmationInventory> locked;
      private final boolean enableInitialMMOProfilesClassSelectSound;

      public YesItem(ConfigurationSection config) {
         super(config);
         Validate.isTrue(config.contains("unlocked"), "Could not load 'unlocked' config");
         Validate.isTrue(config.contains("locked"), "Could not load 'locked' config");
         this.enableInitialMMOProfilesClassSelectSound = config.getBoolean("enable_initial_mmoprofiles_class_select_sound");
         this.unlocked = ClassConfirmation.this.new UnlockedItem(config.getConfigurationSection("unlocked"));
         this.locked = new PhysicalItem<ClassConfirmation.ClassConfirmationInventory>(config.getConfigurationSection("locked")) {
            public Placeholders getPlaceholders(ClassConfirmation.ClassConfirmationInventory inv, int n) {
               Placeholders holders = new Placeholders();
               holders.register("class", inv.profess.getName());
               return holders;
            }
         };
      }

      public ItemStack getDisplayedItem(@NotNull ClassConfirmation.ClassConfirmationInventory inv, int n) {
         return inv.playerData.hasSavedClass(inv.profess) ? this.unlocked.getDisplayedItem(inv, n) : this.locked.getDisplayedItem(inv, n);
      }

      public void onClick(@NotNull ClassConfirmation.ClassConfirmationInventory inv, @NotNull InventoryClickEvent event) {
         PlayerChangeClassEvent called = new PlayerChangeClassEvent(inv.playerData, inv.profess);
         Bukkit.getPluginManager().callEvent(called);
         if (!called.isCancelled()) {
            inv.getNavigator().unblockClosing();
            inv.playerData.giveClassPoints(-1);
            if (inv.forceSetClass) {
               inv.playerData.setClass(inv.profess);
            } else {
               (inv.playerData.hasSavedClass(inv.profess)
                     ? inv.playerData.getClassInfo(inv.profess)
                     : new SavedClassInformation(MMOCore.plugin.playerDataManager.getDefaultData()))
                  .load(inv.profess, inv.playerData);
            }

            if (this.enableInitialMMOProfilesClassSelectSound || inv.profileCallback == null) {
               ConfigMessage.fromKey("class-select", "class", inv.profess.getName()).send(inv.playerData);
               MMOCore.plugin.soundManager.getSound(SoundEvent.SELECT_CLASS).playTo(inv.getPlayer());
            }

            inv.getPlayer().closeInventory();
            if (inv.profileCallback != null) {
               inv.profileCallback.run();
            }
         }
      }
   }
}

package net.Indyuce.mmoitems.stat.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.StatChoice;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ChooseStat extends StringStat {
   private final List<StatChoice> choices = new ArrayList<>();

   public ChooseStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, Material... materials) {
      super(text, material, name, texts, compatibleTypes, materials);
   }

   public void addChoices(StatChoice... statChoices) {
      this.choices.addAll(Arrays.asList(statChoices));
   }

   @Nullable
   public StatChoice getChoice(String text) {
      for (StatChoice choice : this.choices) {
         if (choice.getId().equals(text)) {
            return choice;
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      Validate.isTrue(!this.choices.isEmpty(), "Invalid choice-based stat '" + this.getId() + ": no options to choose from.");
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set(this.getPath(), null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         String text = editionInventory.getEditedSection().getString(this.getPath());
         int index = text == null ? -1 : this.choices.indexOf(this.getChoice(text));
         if (++index >= this.choices.size()) {
            index = 0;
         }

         StatChoice statChoice = this.choices.get(index);
         editionInventory.getEditedSection().set(this.getPath(), statChoice.getId());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to " + statChoice + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringData> optional) {
      Validate.isTrue(!this.choices.isEmpty(), "Invalid choice-based stat '" + this.getId() + ": no options to choose from.");
      StatChoice statChoice = optional.isPresent() ? this.getChoice(((StringData)optional.get()).toString()) : null;
      texts.add(ChatColor.GRAY + "Current Value: " + (statChoice != null ? ChatColor.GREEN + statChoice.getId() : ChatColor.RED + "None"));
      if (statChoice != null && statChoice.getHint() != null) {
         for (String text : MMOUtils.trimString(50, statChoice.getHint())) {
            texts.add(ChatColor.GRAY + " " + text);
         }
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Right click to return to default value.");
      texts.add(ChatColor.YELLOW + "►" + " Left click to cycle through the available options:");

      for (StatChoice choice : this.choices) {
         String text = choice.equals(statChoice) ? ChatColor.RED.toString() + ChatColor.BOLD : ChatColor.GOLD.toString();
         texts.add(text + "  " + "▸" + " " + ChatColor.GRAY + choice.getId());
      }
   }
}

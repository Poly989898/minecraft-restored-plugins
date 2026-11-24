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

   public ChooseStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void addChoices(StatChoice... var1) {
      this.choices.addAll(Arrays.asList(var1));
   }

   @Nullable
   public StatChoice getChoice(String var1) {
      for (StatChoice var3 : this.choices) {
         if (var3.getId().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      Validate.isTrue(!this.choices.isEmpty(), "Invalid choice-based stat '" + this.getId() + ": no options to choose from.");
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set(this.getPath(), null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         String var3 = var1.getEditedSection().getString(this.getPath());
         int var4 = var3 == null ? -1 : this.choices.indexOf(this.getChoice(var3));
         if (++var4 >= this.choices.size()) {
            var4 = 0;
         }

         StatChoice var5 = this.choices.get(var4);
         var1.getEditedSection().set(this.getPath(), var5.getId());
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to " + var5 + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringData> var2) {
      Validate.isTrue(!this.choices.isEmpty(), "Invalid choice-based stat '" + this.getId() + ": no options to choose from.");
      StatChoice var3 = var2.isPresent() ? this.getChoice(((StringData)var2.get()).toString()) : null;
      var1.add(ChatColor.GRAY + "Current Value: " + (var3 != null ? ChatColor.GREEN + var3.getId() : ChatColor.RED + "None"));
      if (var3 != null && var3.getHint() != null) {
         for (String var7 : MMOUtils.trimString(50, var3.getHint())) {
            var1.add(ChatColor.GRAY + " " + var7);
         }
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Right click to return to default value.");
      var1.add(ChatColor.YELLOW + "►" + " Left click to cycle through the available options:");

      for (StatChoice var9 : this.choices) {
         String var10 = var9.equals(var3) ? ChatColor.RED.toString() + ChatColor.BOLD : ChatColor.GOLD.toString();
         var1.add(var10 + "  " + "▸" + " " + ChatColor.GRAY + var9.getId());
      }
   }
}

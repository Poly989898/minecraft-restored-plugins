package net.Indyuce.mmoitems.api.edition;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.input.ChatEdition;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;

public class StatEdition implements Edition {
   private final EditionInventory inv;
   private final ItemStat stat;
   private final Object[] info;

   public StatEdition(EditionInventory editionInventory, ItemStat itemStat, Object... objs) {
      this.inv = editionInventory;
      this.stat = itemStat;
      this.info = objs;
   }

   @Override
   public PluginInventory getInventory() {
      return this.inv;
   }

   public ItemStat getStat() {
      return this.stat;
   }

   public Object[] getData() {
      return this.info;
   }

   @Override
   public void enable(String... texts) {
      this.inv.getPlayer().closeInventory();
      this.inv.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");

      for (String text : texts) {
         this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', text));
      }

      this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Type 'cancel' to abort editing.");
      new ChatEdition(this);
      this.inv.getPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Edition", "See chat.", 10, 40, 10);
   }

   @Override
   public boolean processInput(String text) {
      try {
         this.stat.whenInput(this.inv, text, this.info);
         return true;
      } catch (RuntimeException exception) {
         if (!exception.getMessage().isEmpty()) {
            this.inv.getFFP().log(FriendlyFeedbackCategory.ERROR, exception.getMessage(), new String[0]);
         }

         this.inv.getFFP().sendTo(FriendlyFeedbackCategory.ERROR, this.inv.getPlayer());
         this.inv.getFFP().sendTo(FriendlyFeedbackCategory.FAILURE, this.inv.getPlayer());
         this.inv.getFFP().clearFeedback();
         return false;
      }
   }

   @Override
   public boolean shouldGoBack() {
      return true;
   }
}

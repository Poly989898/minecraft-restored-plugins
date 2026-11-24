package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringStat extends ItemStat<StringData, StringData> {
   public StringStat(String text, Material material, String name, String[] texts, String[] compatibleTypes, Material... materials) {
      super(text, material, name, texts, compatibleTypes, materials);
   }

   public StringData whenInitialized(Object obj) {
      return new StringData(obj.toString());
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(stringData));
      itemStackBuilder.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", stringData.toString()));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData stringData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), stringData.toString()));
      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set(this.getPath(), null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         new StatEdition(editionInventory, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      editionInventory.getEditedSection().set(this.getPath(), text);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to '" + MythicLib.plugin.parseColors(text) + ChatColor.GRAY + "'.");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public StringData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         String text = (String)tagAtPath.getValue();
         return new StringData(text);
      } else {
         return null;
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringData> optional) {
      if (optional.isPresent()) {
         String colors = MythicLib.plugin.parseColors(((StringData)optional.get()).toString());
         colors = colors.length() > 40 ? colors.substring(0, 40) + "..." : colors;
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + colors);
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public StringData getClearStatData() {
      return new StringData("");
   }
}

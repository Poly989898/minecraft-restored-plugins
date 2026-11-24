package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ActionLeftClick extends StringStat {
   public static final String LEGACY_ID = "STAFF_SPIRIT";
   public static final String LEGACY_PATH = "MMOITEMS_STAFF_SPIRIT";

   public ActionLeftClick() {
      super(
         "ON_LEFT_CLICK",
         Material.COMMAND_BLOCK_MINECART,
         "Left Click Action",
         new String[]{
            "ID of skill ran when left clicking. When used,",
            "The item will naturally apply item costs like",
            "mana, stamina, cooldown. This option overrides the",
            "script provided by the item type."
         },
         new String[]{"weapon"}
      );
      this.setAliases("STAFF_SPIRIT");
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = UtilityMethods.enumName(text);
      MythicLib.plugin.getSkills().getHandlerOrThrow(text2);
      editionInventory.getEditedSection().set(this.getPath(), text2);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to '" + text2 + "'");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), UtilityMethods.enumName(stringData.toString())));
   }

   @Deprecated
   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      if (readMMOItem.getNBT().hasTag("MMOITEMS_STAFF_SPIRIT")) {
         list.add(new ItemTag(this.getNBTPath(), ItemTag.getTagAtPath("MMOITEMS_STAFF_SPIRIT", readMMOItem.getNBT(), SupportedNBTTagValues.STRING).getValue()));
      }

      StringData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }
}

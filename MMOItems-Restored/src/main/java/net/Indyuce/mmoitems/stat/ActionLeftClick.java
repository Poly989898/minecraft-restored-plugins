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
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = UtilityMethods.enumName(var2);
      MythicLib.plugin.getSkills().getHandlerOrThrow(var4);
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to '" + var4 + "'");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      var1.addItemTag(new ItemTag(this.getNBTPath(), UtilityMethods.enumName(var2.toString())));
   }

   @Deprecated
   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      if (var1.getNBT().hasTag("MMOITEMS_STAFF_SPIRIT")) {
         var2.add(new ItemTag(this.getNBTPath(), ItemTag.getTagAtPath("MMOITEMS_STAFF_SPIRIT", var1.getNBT(), SupportedNBTTagValues.STRING).getValue()));
      }

      StringData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }
}

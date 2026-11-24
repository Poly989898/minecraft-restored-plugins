package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ActionRightClick extends StringStat {
   public ActionRightClick() {
      super(
         "ON_RIGHT_CLICK",
         Material.COMMAND_BLOCK_MINECART,
         "Right Click Action",
         new String[]{
            "ID of skill ran when right clicking. When used,",
            "The item will naturally apply item costs like",
            "mana, stamina, cooldown. This option overrides the",
            "script provided by the item type."
         },
         new String[]{"weapon"}
      );
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
}

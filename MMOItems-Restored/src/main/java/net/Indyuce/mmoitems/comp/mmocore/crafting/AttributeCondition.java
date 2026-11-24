package net.Indyuce.mmoitems.comp.mmocore.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class AttributeCondition extends Condition {
   private final PlayerAttribute attribute;
   private final int points;

   public AttributeCondition(MMOLineConfig var1) {
      super("attribute");
      var1.validate(new String[]{"attribute", "points"});
      this.points = var1.getInt("points");
      String var2 = var1.getString("attribute").toLowerCase().replace("_", "-");
      Validate.isTrue(MMOCore.plugin.attributeManager.has(var2), "Could not find attribute " + var2);
      this.attribute = MMOCore.plugin.attributeManager.get(var2);
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#level#", "" + this.points).replace("#attribute#", this.attribute.getName());
   }

   @Override
   public boolean isMet(PlayerData var1) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer()).getAttributes().getAttribute(this.attribute) >= this.points;
   }

   @Override
   public void whenCrafting(PlayerData var1) {
   }
}

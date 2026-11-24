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

   public AttributeCondition(MMOLineConfig mmoLineConfig) {
      super("attribute");
      mmoLineConfig.validate(new String[]{"attribute", "points"});
      this.points = mmoLineConfig.getInt("points");
      String text = mmoLineConfig.getString("attribute").toLowerCase().replace("_", "-");
      Validate.isTrue(MMOCore.plugin.attributeManager.has(text), "Could not find attribute " + text);
      this.attribute = MMOCore.plugin.attributeManager.get(text);
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#level#", "" + this.points).replace("#attribute#", this.attribute.getName());
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      return net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer()).getAttributes().getAttribute(this.attribute) >= this.points;
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
   }
}

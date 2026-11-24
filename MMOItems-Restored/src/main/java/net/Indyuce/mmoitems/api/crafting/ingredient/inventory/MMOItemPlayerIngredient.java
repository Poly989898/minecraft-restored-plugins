package net.Indyuce.mmoitems.api.crafting.ingredient.inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gson.JsonParser;

public class MMOItemPlayerIngredient extends PlayerIngredient {
   private final String type;
   private final String id;
   private final int upgradeLevel;

   public MMOItemPlayerIngredient(NBTItem nbtItem) {
      super(nbtItem);
      this.type = nbtItem.getString("MMOITEMS_ITEM_TYPE");
      this.id = nbtItem.getString("MMOITEMS_ITEM_ID");
      String mMOITEMSUPGRADE = nbtItem.getString("MMOITEMS_UPGRADE");
      this.upgradeLevel = !mMOITEMSUPGRADE.isEmpty() ? new JsonParser().parse(mMOITEMSUPGRADE).getAsJsonObject().get("Level").getAsInt() : 0;
   }

   public String getType() {
      return this.type;
   }

   public String getId() {
      return this.id;
   }

   public int getUpgradeLevel() {
      return this.upgradeLevel;
   }
}

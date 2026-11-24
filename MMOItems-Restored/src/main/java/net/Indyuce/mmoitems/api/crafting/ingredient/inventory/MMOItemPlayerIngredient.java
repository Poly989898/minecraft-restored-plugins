package net.Indyuce.mmoitems.api.crafting.ingredient.inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gson.JsonParser;

public class MMOItemPlayerIngredient extends PlayerIngredient {
   private final String type;
   private final String id;
   private final int upgradeLevel;

   public MMOItemPlayerIngredient(NBTItem var1) {
      super(var1);
      this.type = var1.getString("MMOITEMS_ITEM_TYPE");
      this.id = var1.getString("MMOITEMS_ITEM_ID");
      String var2 = var1.getString("MMOITEMS_UPGRADE");
      this.upgradeLevel = !var2.isEmpty() ? new JsonParser().parse(var2).getAsJsonObject().get("Level").getAsInt() : 0;
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

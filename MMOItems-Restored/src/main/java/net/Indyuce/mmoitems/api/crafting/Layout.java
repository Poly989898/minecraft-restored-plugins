package net.Indyuce.mmoitems.api.crafting;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Layout {
   private final String id;
   private final List<Integer> recipeSlots;
   private final List<Integer> queueSlots;
   private final int size;
   private final int recipePreviousSlot;
   private final int recipeNextSlot;
   private final int queuePreviousSlot;
   private final int queueNextSlot;

   public Layout(String var1, FileConfiguration var2) {
      this.id = var1;
      this.size = var2.getInt("slots");
      ConfigurationSection var3 = var2.getConfigurationSection("layout");
      this.recipeSlots = var3.getIntegerList("recipe-slots");
      this.queueSlots = var3.getIntegerList("queue-slots");
      this.recipePreviousSlot = var3.getInt("recipe-previous-slot", 18);
      this.recipeNextSlot = var3.getInt("recipe-next-slot", 26);
      this.queuePreviousSlot = var3.getInt("queue-previous-slot", 37);
      this.queueNextSlot = var3.getInt("queue-next-slot", 43);
   }

   public String getId() {
      return this.id;
   }

   public int getSize() {
      return this.size;
   }

   public List<Integer> getRecipeSlots() {
      return this.recipeSlots;
   }

   public List<Integer> getQueueSlots() {
      return this.queueSlots;
   }

   public int getRecipePreviousSlot() {
      return this.recipePreviousSlot;
   }

   public int getRecipeNextSlot() {
      return this.recipeNextSlot;
   }

   public int getQueuePreviousSlot() {
      return this.queuePreviousSlot;
   }

   public int getQueueNextSlot() {
      return this.queueNextSlot;
   }
}

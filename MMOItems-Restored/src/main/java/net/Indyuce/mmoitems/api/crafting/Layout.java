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

   public Layout(String text, FileConfiguration config) {
      this.id = text;
      this.size = config.getInt("slots");
      ConfigurationSection layout = config.getConfigurationSection("layout");
      this.recipeSlots = layout.getIntegerList("recipe-slots");
      this.queueSlots = layout.getIntegerList("queue-slots");
      this.recipePreviousSlot = layout.getInt("recipe-previous-slot", 18);
      this.recipeNextSlot = layout.getInt("recipe-next-slot", 26);
      this.queuePreviousSlot = layout.getInt("queue-previous-slot", 37);
      this.queueNextSlot = layout.getInt("queue-next-slot", 43);
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

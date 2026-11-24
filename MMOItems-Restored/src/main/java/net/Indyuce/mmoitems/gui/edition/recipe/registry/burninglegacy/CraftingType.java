package net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.version.VMaterial;
import net.Indyuce.mmoitems.manager.RecipeManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Deprecated
public enum CraftingType {
   FURNACE(23, "The Furnace Recipe for this item", Material.FURNACE, RecipeManager.BurningRecipeType.FURNACE),
   BLAST(29, "The Blast Furnace Recipe for this item", VMaterial.BLAST_FURNACE, RecipeManager.BurningRecipeType.BLAST, 1, 14),
   SMOKER(30, "The Smoker Recipe for this item", VMaterial.SMOKER, RecipeManager.BurningRecipeType.SMOKER, 1, 14),
   CAMPFIRE(32, "The Campfire Recipe for this item", VMaterial.CAMPFIRE, RecipeManager.BurningRecipeType.CAMPFIRE, 1, 14);

   private final int slot;
   private final String lore;
   private final Material material;
   private final int[] mustBeHigher;
   private final RecipeManager.BurningRecipeType burning;

   CraftingType(int nullxx, String nullxxx, VMaterial nullxxxx, @Nullable RecipeManager.BurningRecipeType nullxxxxx, int... nullxxxxxx) {
      this(nullxx, nullxxx, nullxxxx.get(), nullxxxxx, nullxxxxxx);
   }

   CraftingType(int nullxx, String nullxxx, Material nullxxxx, @Nullable RecipeManager.BurningRecipeType nullxxxxx, int... nullxxxxxx) {
      this.slot = nullxx;
      this.lore = nullxxx;
      this.material = nullxxxx;
      this.mustBeHigher = nullxxxxxx;
      this.burning = nullxxxxx;
   }

   public ItemStack getItem() {
      return new ItemStack(this.material);
   }

   public int getSlot() {
      return this.slot;
   }

   public String getName() {
      return UtilityMethods.caseOnWords(this.name().toLowerCase());
   }

   public String getLore() {
      return this.lore;
   }

   public RecipeManager.BurningRecipeType getBurningType() {
      return this.burning;
   }

   public boolean shouldAdd() {
      return this.mustBeHigher.length == 0 || MythicLib.plugin.getVersion().isStrictlyHigher(this.mustBeHigher);
   }

   public static CraftingType getBySlot(int count) {
      for (CraftingType craftingType : values()) {
         if (craftingType.getSlot() == count) {
            return craftingType;
         }
      }

      return null;
   }
}

package io.lumine.mythic.lib.skill.result.def;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemSkillResult extends VectorSkillResult {
   @Nullable
   private final ItemStack item;
   @Nullable
   private final Material defaultMaterial;

   public ItemSkillResult(@NotNull SkillMetadata skillMeta) {
      this(skillMeta, null);
   }

   public ItemSkillResult(@NotNull SkillMetadata skillMeta, @Nullable Material defaultMaterial) {
      super(skillMeta);
      this.defaultMaterial = defaultMaterial;
      org.bukkit.inventory.EquipmentSlot actionHand = skillMeta.getCaster().getActionHand().toBukkit();
      this.item = skillMeta.getCaster().getPlayer().getInventory().getItem(actionHand);
   }

   @NotNull
   public ItemStack getItem() {
      return UtilityMethods.isAir(this.item) ? new ItemStack(this.defaultMaterial) : this.item;
   }

   @Override
   public boolean isSuccessful() {
      return super.isSuccessful() && (!UtilityMethods.isAir(this.item) || this.defaultMaterial != null);
   }
}

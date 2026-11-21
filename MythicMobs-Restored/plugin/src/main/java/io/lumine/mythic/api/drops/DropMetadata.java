package io.lumine.mythic.api.drops;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public interface DropMetadata extends Cloneable, PlaceholderMeta {
   Optional<SkillCaster> getDropper();

   Optional<AbstractEntity> getCause();

   float getAmount();

   void setAmount(float value);

   int getGenerations();

   int tick();

   @Override
   SkillCaster getCaster();

   @Override
   AbstractEntity getTrigger();

   default PlaceholderMeta getExtraPlaceholderMeta() {
      return null;
   }

   Optional<ItemStack> getItem();

   void setItem(ItemStack itemStack);

   double getLevel();

   void setLevel(double value);

   DropMetadata clone();

   DropMetadata deeperClone();

   double getBias();

   void setBias(double value);
}

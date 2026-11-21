package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScopedName;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;

public class ItemVariableDrop extends ItemDrop implements IItemDrop {
   protected final PlaceholderString variableIdentifier;

   public ItemVariableDrop(String line, MythicLineConfig config) {
      this(line, config, 1.0);
   }

   public ItemVariableDrop(String line, MythicLineConfig config, double amount) {
      this(line, config, new RandomDouble(amount));
   }

   public ItemVariableDrop(String line, MythicLineConfig config, RandomDouble amount) {
      super(line, config, amount);
      this.variableIdentifier = config.getPlaceholderString(new String[]{"variable", "var", "name", "n"}, null);
      if (this.variableIdentifier == null) {
         MythicLogger.errorDropConfig(this, config, "The \"variable\" attribute must be defined");
      }
   }

   @Override
   public AbstractItemStack getDrop(DropMetadata meta, double amount) {
      if (this.variableIdentifier == null) {
         return null;
      } else {
         VariableScopedName variableName = VariableScopedName.of(this.variableIdentifier.get(meta));
         VariableRegistry registry;
         if (meta.getExtraPlaceholderMeta() != null && meta.getExtraPlaceholderMeta() instanceof SkillMetadata skillMetadata) {
            registry = MythicBukkit.inst().getVariableManager().getRegistry(variableName.scope(), skillMetadata);
         } else {
            registry = MythicBukkit.inst().getVariableManager().getRegistry(variableName.scope(), meta);
         }

         if (registry == null) {
            return null;
         } else {
            return registry.get(variableName.name().get(meta)) instanceof ItemVariable itemVariable ? itemVariable.get() : null;
         }
      }
   }
}

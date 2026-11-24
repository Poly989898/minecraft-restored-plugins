package io.lumine.mythic.lib.gui.editable.item;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.placeholder.EmptyPlaceholders;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.config.YamlUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

/** A GUI item without placeholders. Its configuration may define a script ("on-click") run when it is clicked. */
public class SimpleItem<T extends GeneratedInventory> extends PhysicalItem<T> {
   private final Script script;

   public SimpleItem(ConfigurationSection config) {
      this(null, config);
   }

   public SimpleItem(InventoryItem<T> parent, ConfigurationSection config) {
      super(parent, config);
      Object scriptConfig = YamlUtils.get(config, "on-click", "on_click");
      this.script = scriptConfig == null ? null : MythicLib.plugin.getSkills().loadScript(scriptConfig);
   }

   @Override
   public Placeholders getPlaceholders(T inv, int n) {
      return new EmptyPlaceholders();
   }

   @Override
   public void onClick(T inv, InventoryClickEvent event) {
      if (this.script != null) {
         this.script.cast(SkillMetadata.of(inv.getMMOPlayerData()));
      }
   }
}

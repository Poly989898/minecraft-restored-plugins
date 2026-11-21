package io.lumine.mythic.core.menus.spawners.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorButton;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorButtons;
import io.lumine.mythic.core.menus.spawners.SpawnerEditorMenuContext;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Material;

public class ToggleSpawnerAttrButton extends SpawnerEditorButton {
   private final String label;
   private final String attribute;
   private final Predicate<MythicSpawner> currentValue;

   public ToggleSpawnerAttrButton(SpawnerEditorButtons parent, int sort, String label, String attribute, Predicate<MythicSpawner> currentValue) {
      super(parent, sort);
      this.label = label;
      this.attribute = attribute;
      this.currentValue = currentValue;
   }

   @Override
   public String getLabel() {
      return this.label;
   }

   @Override
   public Icon<SpawnerEditorMenuContext> getIcon() {
      return IconBuilder.<SpawnerEditorMenuContext>create()
         .material(
            (Function<SpawnerEditorMenuContext, Material>)(context -> this.currentValue.test(context.getSpawner()) ? Material.LIME_DYE : Material.GRAY_DYE)
         )
         .name("<green>" + this.label)
         .hideFlags()
         .lore(
            context -> Lists.newArrayList(
               new String[]{"&c", "<yellow>Current: <white>" + this.currentValue.test(context.getSpawner()), "<red> ", "<gray>\u22b3 <yellow>Click to Toggle"}
            )
         )
         .click((context, player) -> {
            MythicSpawner spawner = context.getSpawner();
            boolean next = !this.currentValue.test(spawner);
            MythicBukkit.inst().getSpawnerManager().setSpawnerAttribute(spawner, this.attribute, String.valueOf(next));
            spawner.save();
            player.playSound(player.getLocation(), "ui.button.click", 1.0F, 1.0F);
            context.openMenu(player);
         })
         .build();
   }
}

package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class WorldPickerView extends PageableGUI<World> {
   private static final Phrase ENTRY_ACTION = Phrase.create("worldPickerView.entry.action", "Click to **select** World", Colors.GRAY, Colors.YELLOW);

   public WorldPickerView(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
   }

   public abstract void onSelect(World world);

   @Override
   public SearchFeature<World> getSearch() {
      return new BasicSearch<World>() {
         public String[] getSearchableText(World world) {
            return new String[]{world.getName()};
         }
      };
   }

   public World[] getObjects() {
      return Bukkit.getWorlds().toArray(new World[0]);
   }

   public void construct(Button button, World world) {
      button.material(XMaterial.YELLOW_STAINED_GLASS).name(Animation.wave(world.getName(), Colors.YELLOW, Colors.WHITE)).lore(ENTRY_ACTION.get());
      button.action(actionType -> this.onSelect(world));
   }
}

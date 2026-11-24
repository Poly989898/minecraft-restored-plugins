package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.element.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.util.ElementStatType;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@HasCategory(cat = "elemental")
@Deprecated
public class FakeElementalStat extends DoubleStat implements InternalStat {
   public FakeElementalStat(Element var1, ElementStatType var2) {
      super(var2.getConcatenatedTagPath(var1), Material.BARRIER, "Fictive Stat", new String[0]);
   }

   @Override
   public NumericStatFormula whenInitialized(Object var1) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull DoubleData var2) {
      throw new RuntimeException("Fictive item stat");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData var1) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      throw new RuntimeException("Fictive item stat");
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<NumericStatFormula> var2) {
      throw new RuntimeException("Fictive item stat");
   }
}

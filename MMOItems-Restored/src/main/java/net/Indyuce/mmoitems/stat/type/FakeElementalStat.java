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
   public FakeElementalStat(Element element, ElementStatType elementStatType) {
      super(elementStatType.getConcatenatedTagPath(element), Material.BARRIER, "Fictive Stat", new String[0]);
   }

   @Override
   public NumericStatFormula whenInitialized(Object obj) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull DoubleData doubleData) {
      throw new RuntimeException("Fictive item stat");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull DoubleData doubleData) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      throw new RuntimeException("Fictive item stat");
   }

   @Nullable
   @Override
   public DoubleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      throw new RuntimeException("Fictive item stat");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<NumericStatFormula> optional) {
      throw new RuntimeException("Fictive item stat");
   }
}

package com.nexomc.nexo.utils.inventories;

import com.nexomc.libs.gui.guis.Gui;
import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.utils.Reloadable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\t\u001a\u00020\nH\u0016J\u0012\u0010\u000b\u001a\u00060\u0007R\u00020\b2\u0006\u0010\f\u001a\u00020\rJ\u001c\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013R\u001e\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u00060\u0007R\u00020\b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/utils/inventories/InventoryManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "itemsViews", "", "Ljava/util/UUID;", "Lcom/nexomc/nexo/utils/inventories/ItemsView$DirectoryGui;", "Lcom/nexomc/nexo/utils/inventories/ItemsView;", "reload", "", "itemsView", "player", "Lorg/bukkit/entity/Player;", "recipesShowcase", "Lcom/nexomc/libs/gui/guis/Gui;", "page", "", "filteredRecipes", "", "Lcom/nexomc/nexo/recipes/CustomRecipe;", "com.nexomc:core"}
)
public final class InventoryManager implements Reloadable {
   @NotNull
   private final Map<UUID, ItemsView.DirectoryGui> itemsViews = (Map)(new LinkedHashMap());

   public void reload() {
      this.itemsViews.clear();
   }

   @NotNull
   public final ItemsView.DirectoryGui itemsView(@NotNull Player player) {
      Object obj = this.itemsViews.computeIfAbsent(player.getUniqueId(), (a0_ -> InventoryManager.itemsView$lambda$0(a0_)));
      return (ItemsView.DirectoryGui)obj;
   }

   @NotNull
   public final Gui recipesShowcase(int page, @NotNull List<CustomRecipe> filteredRecipes) {
      return (new RecipesView()).create(page, filteredRecipes);
   }

   private static final ItemsView.DirectoryGui itemsView$lambda$0(UUID it) {
      return (new ItemsView()).create();
   }

   private static final ItemsView.DirectoryGui itemsView$lambda$1(Function1 tmp0, Object p0) {
      return (ItemsView.DirectoryGui)tmp0.invoke(p0);
   }
}

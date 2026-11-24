package me.TechsCode.UltraPermissions.base.gui.pageableview;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class PageableGUI<OBJ> extends GUI {
   private static final Phrase NEXT_TITLE = Phrase.create("pageableView.next.title", "Next");
   private static final Phrase NEXT_ACTION = Phrase.create("pageableView.next.action", "Click to go to the next page", Colors.GRAY);
   private static final Phrase PREVIOUS_TITLE = Phrase.create("pageableView.previous.title", "Previous");
   private static final Phrase PREVIOUS_ACTION = Phrase.create("pageableView.previous.action", "Click to go to the previous page", Colors.GRAY);
   private static final Phrase SEARCH_TITLE = Phrase.create("pageableView.search.title", "Search");
   private static final Phrase SEARCH_SUBTITLE = Phrase.create("pageableView.search.subtitle", "Type in a search term", Colors.GRAY);
   private static final Phrase SEARCH_CLOSE_ACTION = Phrase.create("pageableView.search.closeAction", "Click to **close** the search", Colors.GRAY, Colors.RED);
   private final int maxObjectsPerPage = 36;
   private final SpigotTechPlugin plugin;
   private int page = 0;
   private int pages;
   private String searchTerm = null;

   public PageableGUI(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
      this.plugin = spigotTechPlugin;
   }

   public abstract String getTitle();

   @Override
   protected void construct(Model model) {
      model.setTitle(this.getTitle() + " (" + (this.page + 1) + "/" + this.pages + ")");
      model.setSlots(54);
      SearchFeature search = this.getSearch();
      List list = Arrays.stream(this.getObjects()).filter(value -> {
         if (search == null) {
            return true;
         }

         if (this.searchTerm == null) {
            return true;
         }

         for (String text : search.getSearchableText((OBJ)value)) {
            if (text != null && text.toLowerCase().contains(this.searchTerm.toLowerCase())) {
               return true;
            }
         }

         return false;
      }).collect(Collectors.toList());
      int count = Math.min(list.size(), this.page * 36);
      int endIndex = Math.min(list.size(), count + 36);
      List pageItems = list.subList(count, endIndex);
      int slot = 1;

      for (Object obj : pageItems) {
         model.button(button -> this.construct(button, (OBJ)obj), slot);
         slot++;
      }

      this.pages = (int)Math.ceil(list.size() / 36.0);
      if (this.page != 0) {
         model.button(
            button -> {
               button.material(XMaterial.ARROW)
                  .name(Animation.fading(PREVIOUS_TITLE.get() + " page (" + (this.page - 1) + ")", Colors.DarkCyan, Colors.Cyan))
                  .lore(PREVIOUS_ACTION.get());
               button.action(actionType -> {
                  this.page--;
                  if (this.page < 0) {
                     this.page = 0;
                  }
               });
            },
            46
         );
      }

      if (this.page + 1 < this.pages) {
         model.button(
            button -> {
               button.material(XMaterial.ARROW)
                  .name(Animation.fading(NEXT_TITLE.get() + " page (" + (this.page + 1) + ")", Colors.DarkCyan, Colors.Cyan))
                  .lore(NEXT_ACTION.get());
               button.action(actionType -> this.page++);
            },
            54
         );
      }

      if (search != null) {
         if (this.searchTerm == null) {
            model.button(button -> {
               button.material(search.getIcon()).name(Animation.fading(search.getTitle(), Colors.DarkCyan, Colors.Cyan)).lore(search.getAction());
               button.action(actionType -> new UserInput(this.p, this.plugin, SEARCH_TITLE.get(), SEARCH_SUBTITLE.get(), "") {
                  @Override
                  public void onClose(Player closingPlayer) {
                     PageableGUI.this.reopen();
                  }

                  @Override
                  public boolean onResult(String text) {
                     PageableGUI.this.searchTerm = text;
                     PageableGUI.this.page = 0;
                     PageableGUI.this.reopen();
                     return true;
                  }
               });
            }, this.getLeftOptionSlot());
         } else {
            model.button(button -> {
               button.material(XMaterial.REDSTONE_BLOCK).name(Animation.fading(search.getTitle(), Colors.RED, Colors.WHITE)).lore(SEARCH_CLOSE_ACTION.get());
               button.action(actionType -> {
                  this.searchTerm = null;
                  this.page = 0;
               });
            }, this.getLeftOptionSlot());
         }
      }

      if (this.hasBackButton()) {
         model.button(50, button -> Common.BackButton(button, actionType -> this.onBack()));
      }
   }

   protected int getLeftOptionSlot() {
      return this.page != 0 ? 48 : 47;
   }

   protected int getRightOptionSlot() {
      return this.pages <= 1 ? 52 : 53;
   }

   public boolean hasBackButton() {
      return true;
   }

   public abstract void onBack();

   public abstract SearchFeature<OBJ> getSearch();

   public abstract OBJ[] getObjects();

   public abstract void construct(Button button, OBJ value);
}

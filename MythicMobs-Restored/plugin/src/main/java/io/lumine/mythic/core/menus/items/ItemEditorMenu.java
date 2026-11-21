package io.lumine.mythic.core.menus.items;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import java.util.List;
import java.util.function.Function;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEditorMenu extends MythicMenu<ItemEditorMenuContext> {
   private ItemEditorButtons buttons = new ItemEditorButtons();

   public ItemEditorMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/item-editor.yml");
   }

   public void open(Player player, MythicItem mythicItem) {
      this.open(player, new ItemEditorMenuContext(mythicItem, new ItemMenuContext()));
   }

   public void open(Player player, ItemEditorMenuContext context) {
      List<ItemEditorButton> buttons = Lists.newArrayList(this.buttons.getButtons());
      buttons.removeIf(itemButton -> !itemButton.isApplicable(context.getItem()));
      this.open(player, context, Text.colorize("Editing Item: " + context.getItem().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<ItemEditorMenuContext> build(EditableMenuBuilder<ItemEditorMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("ITEM_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .itemStack((Function<ItemEditorMenuContext, ItemStack>)(context -> context.getItem().getCachedMenuItem()))
               .hideFlags(false)
               .click((context, player) -> {
                  player.performCommand("mythicmobs i get " + context.getItem().getInternalName());
                  player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
               })
         );
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().hideFlags(false).click((context, player) -> {
         CommandHelper.sendEditorMessage(player, "Type <red>DELETE <white>to confirm deleting", "Type anything else to abort");
         player.closeInventory();
         ChatPrompt.listen(player, input -> {
            if (!input.equals("DELETE")) {
               context.openMenu(player);
               return ChatPrompt.Response.ACCEPTED;
            } else {
               MythicItem item = context.getItem();
               this.getPlugin().getItemManager().deleteItem(item);
               context.openPreviousMenu(player);
               player.playSound(player.getLocation(), "entity.zombie.break_wooden_door", 1.0F, 1.0F);
               return ChatPrompt.Response.ACCEPTED;
            }
         }).thenAcceptSync(in -> {});
      }));
      return builder;
   }
}

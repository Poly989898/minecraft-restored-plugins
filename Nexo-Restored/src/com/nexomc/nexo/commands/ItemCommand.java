package com.nexomc.nexo.commands;

import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J'\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0000¢\u0006\u0002\b\f¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/commands/ItemCommand;", "", "<init>", "()V", "sendItemInfo", "", "sender", "Lnet/kyori/adventure/audience/Audience;", "builder", "Lcom/nexomc/nexo/items/ItemBuilder;", "itemId", "", "sendItemInfo$com_nexomc_core", "com.nexomc:core"}
)
public final class ItemCommand {
   @NotNull
   public static final ItemCommand INSTANCE = new ItemCommand();

   private ItemCommand() {
   }

   public final void sendItemInfo$com_nexomc_core(@NotNull Audience sender, @NotNull ItemBuilder builder, @Nullable String itemId) {
      sender.sendMessage(AdventureUtilsKt.deserialize("<dark_aqua>ItemID: <aqua>" + itemId));
      sender.sendMessage(AdventureUtilsKt.deserialize("<dark_green>CustomModelData: <green>" + builder.getNexoMeta().getCustomModelData()));
      sender.sendMessage(AdventureUtilsKt.deserialize("<dark_green>Material: <green>" + builder.referenceCopy().getType()));
      Key itemModel = builder.getItemModel();
      if (itemModel != null) {
         String asString = itemModel.asString();
         if (asString != null) {
            String it = asString;
            int value = 0;
            sender.sendMessage(AdventureUtilsKt.deserialize("<dark_green>ItemModel Name: <green>" + it));
         }
      }

      itemModel = builder.getNexoMeta().getModel();
      if (itemModel != null) {
         String str = itemModel.asString();
         if (str != null) {
            String it = str;
            int value2 = 0;
            sender.sendMessage(AdventureUtilsKt.deserialize("<dark_green>Model Name: <green>" + it));
         }
      }

      Logs.newline();
   }
}

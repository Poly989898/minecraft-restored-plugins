package com.nexomc.nexo.compatibilities.mythicmobs;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import com.nexomc.nexo.items.ItemBuilder;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u0006*\u00020\u0007H\u0007b\u0002\b\b¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/mythicmobs/MythicMobsCompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lio/lumine/mythic/bukkit/MythicBukkit;", "<init>", "()V", "onMythicDropLoadEvent", "", "Lio/lumine/mythic/bukkit/events/MythicDropLoadEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class MythicMobsCompatibility extends CompatibilityProvider<MythicBukkit> {
   @EventHandler
   public final void onMythicDropLoadEvent(@NotNull MythicDropLoadEvent mythicDropLoadEvent) {
      String dropName = mythicDropLoadEvent.getDropName();
      dropName = dropName.toLowerCase(Locale.ROOT);
      if (Intrinsics.areEqual(dropName, "nexo")) {
         dropName = mythicDropLoadEvent.getContainer().getLine();
         dropName = dropName.toLowerCase(Locale.ROOT);
         CharSequence charSequence = (CharSequence)dropName;
         String[] strs = new String[]{" ", ":"};
         String orNull = CollectionsKt.getOrNull(StringsKt.split(charSequence, strs, false, 0), 1);
         if (orNull != null) {
            CharSequence charSequence2 = (CharSequence)orNull;
            String[] strs2 = new String[]{" ", "{"};
            List list = StringsKt.split(charSequence2, strs2, false, 0);
            if (list != null) {
               String str = (String)CollectionsKt.firstOrNull(list);
               if (str != null) {
                  String itemId = str;
                  ItemBuilder itemBuilder = NexoItems.itemFromId(itemId);
                  if (itemBuilder != null) {
                     ItemStack itemStack = itemBuilder.build();
                     if (itemStack != null) {
                        ItemStack item = itemStack;
                        String line = mythicDropLoadEvent.getContainer().getLine();
                        MythicLineConfig config = mythicDropLoadEvent.getConfig();
                        mythicDropLoadEvent.register((IDrop)(new NexoDrop(line, item, config)));
                        return;
                     }
                  }

                  return;
               }
            }
         }

      }
   }
}

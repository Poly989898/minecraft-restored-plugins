package com.nexomc.nexo.mechanics.misc.custom.listeners;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.misc.custom.fields.CustomEvent;
import com.nexomc.nexo.utils.actions.ClickAction;
import com.nexomc.nexo.utils.clock.Clock;
import com.nexomc.nexo.utils.clock.ClockFactory;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B-\b\u0004\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u0006\u0010\u0016\u001a\u00020\u0015J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cR\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\b\u001a\u00020\tX\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/listeners/CustomListener;", "Lorg/bukkit/event/Listener;", "itemID", "", "cooldown", "Lkotlin/time/Duration;", "event", "Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "clickAction", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "<init>", "(Ljava/lang/String;Lkotlin/time/Duration;Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;Lcom/nexomc/nexo/utils/actions/ClickAction;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "getItemID", "()Ljava/lang/String;", "getEvent", "()Lcom/nexomc/nexo/mechanics/misc/custom/fields/CustomEvent;", "getClickAction", "()Lcom/nexomc/nexo/utils/actions/ClickAction;", "clockFactory", "Lcom/nexomc/nexo/utils/clock/ClockFactory;", "register", "", "unregister", "perform", "", "player", "Lorg/bukkit/entity/Player;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public abstract class CustomListener implements Listener {
   @Nullable
   private final String itemID;
   @NotNull
   private final CustomEvent event;
   @NotNull
   private final ClickAction clickAction;
   @NotNull
   private final ClockFactory clockFactory;

   protected CustomListener(String itemID, Duration cooldown, CustomEvent event, ClickAction clickAction) {
      super();
      this.itemID = itemID;
      this.event = event;
      this.clickAction = clickAction;
      this.clockFactory = new ClockFactory(cooldown != null ? Duration.times_UwyO8pc(cooldown.unbox_impl(), 50) : Duration.Companion.getZERO_UwyO8pc());
   }

   @Nullable
   protected final String getItemID() {
      return this.itemID;
   }

   @NotNull
   protected final CustomEvent getEvent() {
      return this.event;
   }

   @NotNull
   protected final ClickAction getClickAction() {
      return this.clickAction;
   }

   public void register() {
      Bukkit.getPluginManager().registerEvents(this, (Plugin)NexoPlugin.Companion.instance());
   }

   public final void unregister() {
      HandlerList.unregisterAll(this);
   }

   public final boolean perform(@NotNull Player player, @Nullable ItemStack itemStack) {
      if (!this.clickAction.canRun(player)) {
         return false;
      } else {
         Clock clock2 = this.clockFactory.clock(player);
         if (clock2 == null) {
            return false;
         } else {
            Clock clock = clock2;
            if (!clock.isFinished()) {
               clock.sendToPlayer(player);
               return false;
            } else {
               clock.reset();
               this.clickAction.performActions(player);
               if (this.event.isOneUsage()) {
                  if (itemStack != null) {
                     itemStack.subtract();
                  }
               }

               return true;
            }
         }
      }
   }
}

package io.lumine.mythic.lib.api.event;

import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import java.util.Objects;
import javax.annotation.Nullable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SynchronizedDataLoadEvent extends Event {
   private final SynchronizedDataManager<?, ?> manager;
   private final SynchronizedDataHolder holder;
   @Nullable
   private final Event profileEvent;
   private static final HandlerList HANDLERS = new HandlerList();

   public SynchronizedDataLoadEvent(SynchronizedDataManager<?, ?> manager, SynchronizedDataHolder holder) {
      this(manager, holder, null);
   }

   public SynchronizedDataLoadEvent(@NotNull SynchronizedDataManager<?, ?> manager, @NotNull SynchronizedDataHolder holder, @NotNull Event profileEvent) {
      this.holder = holder;
      this.manager = manager;
      this.profileEvent = profileEvent;
   }

   public SynchronizedDataManager<?, ?> getManager() {
      return this.manager;
   }

   public SynchronizedDataHolder getHolder() {
      return this.holder;
   }

   public boolean syncIsFull() {
      return this.holder.getMMOPlayerData().hasFullySynchronized();
   }

   @NotNull
   public Event getProfileEvent() {
      return Objects.requireNonNull(this.profileEvent, "No corresponding profile event");
   }

   public boolean hasProfileEvent() {
      return this.profileEvent != null;
   }

   public HandlerList getHandlers() {
      return HANDLERS;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }
}

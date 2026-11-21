package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public interface CancellableTriggerMetadata {
   String CANCELLABLE_EVENT_METADATA_KEY = "trigger-event-cancellable";

   boolean isCancelled();

   static void captureEvent(SkillMetadata meta, Event event) {
      if (event instanceof Cancellable cancellable) {
         meta.setMetadata("trigger-event-cancellable", cancellable);
      }
   }

   static boolean isEventCancelled(SkillMetadata meta) {
      return meta.getTriggerMetadata() instanceof CancellableTriggerMetadata cancellableMetadata
         ? cancellableMetadata.isCancelled()
         : meta.getMetadata(Cancellable.class, "trigger-event-cancellable").<Boolean>map(Cancellable::isCancelled).orElse(false);
   }

   static boolean resolveCancelled(SkillMetadata meta) {
      if (meta.isParentCancelled()) {
         return true;
      } else {
         IParentSkill callingEvent = meta.getCallingEvent();
         return callingEvent != null && callingEvent.getCancelled() ? true : isEventCancelled(meta);
      }
   }
}

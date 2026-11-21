package io.lumine.mythic.core.skills;

import io.lumine.mythic.api.skills.ExecutionAudienceState;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ExecutionAudienceStateImpl implements ExecutionAudienceState {
   private Set<UUID> audience = null;

   public ExecutionAudienceStateImpl() {
   }

   public ExecutionAudienceStateImpl(Collection<UUID> initial) {
      if (initial == null) {
         this.audience = null;
      } else {
         this.audience = new HashSet<>(initial);
      }
   }

   @Override
   public synchronized Optional<Set<UUID>> get() {
      return this.audience == null ? Optional.empty() : Optional.of(Collections.unmodifiableSet(this.audience));
   }

   @Override
   public synchronized void set(Collection<UUID> audience) {
      if (audience == null) {
         this.audience = null;
      } else {
         this.audience = new HashSet<>(audience);
      }
   }

   @Override
   public synchronized boolean isSet() {
      return this.audience != null;
   }

   @Override
   public synchronized boolean addAll(Collection<UUID> uuids) {
      if (uuids != null && !uuids.isEmpty()) {
         Set<UUID> cur = this.audience == null ? new HashSet<>() : new HashSet<>(this.audience);
         boolean changed = cur.addAll(uuids);
         this.audience = cur;
         return changed;
      } else {
         return false;
      }
   }

   @Override
   public synchronized boolean removeAll(Collection<UUID> uuids) {
      if (uuids != null && !uuids.isEmpty() && this.audience != null) {
         Set<UUID> cur = new HashSet<>(this.audience);
         boolean changed = cur.removeAll(uuids);
         this.audience = cur;
         return changed;
      } else {
         return false;
      }
   }

   @Override
   public synchronized void clear() {
      this.audience = null;
   }
}

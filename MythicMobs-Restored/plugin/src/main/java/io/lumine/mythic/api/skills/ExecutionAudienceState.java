package io.lumine.mythic.api.skills;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ExecutionAudienceState {
   Optional<Set<UUID>> get();

   void set(Collection<UUID> uuids);

   boolean isSet();

   boolean addAll(Collection<UUID> uuids);

   boolean removeAll(Collection<UUID> uuids);

   void clear();
}

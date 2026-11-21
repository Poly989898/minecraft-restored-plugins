package io.lumine.mythic.bukkit.utils.network.messaging;

import java.util.Set;
import javax.annotation.Nonnull;

public interface InstanceData {
   @Nonnull
   String getId();

   @Nonnull
   Set<String> getGroups();
}

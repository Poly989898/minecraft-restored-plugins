package io.lumine.mythic.bukkit.utils.storage.players;

import javax.annotation.Nonnull;

public interface MultiProfile extends Profile {
   void setSubProfileId(int value);

   @Nonnull
   int getSubProfileId();

   String getSubProfileName();
}

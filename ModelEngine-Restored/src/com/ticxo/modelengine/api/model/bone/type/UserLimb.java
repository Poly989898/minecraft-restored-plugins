package com.ticxo.modelengine.api.model.bone.type;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface UserLimb {
   void setTexture(@Nullable Player player);

   void setTexture(@Nullable PlayerProfile profile);

   void setPlaceholder(String placeholder);

   String getPlaceholder();

   String getLimbType();

   void setStaticSkin(boolean slim, @Nullable String url);
}

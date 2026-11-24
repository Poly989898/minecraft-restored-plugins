package net.Indyuce.mmocore.api.player.social;

import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Sound;

public class FriendRequest extends Request {
   public FriendRequest(PlayerData creator, PlayerData target) {
      super(creator, target);
   }

   @Override
   public void whenDenied() {
      this.getTarget().getPlayer().playSound(this.getTarget().getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }

   @Override
   public void whenAccepted() {
      this.getCreator().setLastActivity(PlayerActivity.FRIEND_REQUEST, 0L);
      this.getCreator().addFriend(this.getTarget().getUniqueId());
      this.getTarget().addFriend(this.getCreator().getUniqueId());
      if (this.getCreator().isOnline()) {
         ConfigMessage.fromKey("now-friends", "player", this.getTarget().getPlayer().getName()).send(this.getCreator().getPlayer());
         ConfigMessage.fromKey("now-friends", "player", this.getCreator().getPlayer().getName()).send(this.getTarget().getPlayer());
      }
   }
}

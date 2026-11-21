package io.lumine.mythic.bukkit.utils.prompts.chat;

import org.bukkit.entity.Player;

public class ChatResponse {
   public final Player player;
   public final String response;

   public ChatResponse(Player player, String response) {
      this.player = player;
      this.response = response;
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getResponse() {
      return this.response;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ChatResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$player = this.getPlayer();
         Object other$player = other.getPlayer();
         if (this$player == null ? other$player == null : this$player.equals(other$player)) {
            Object this$response = this.getResponse();
            Object other$response = other.getResponse();
            return this$response == null ? other$response == null : this$response.equals(other$response);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof ChatResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $player = this.getPlayer();
      result = result * 59 + ($player == null ? 43 : $player.hashCode());
      Object $response = this.getResponse();
      return result * 59 + ($response == null ? 43 : $response.hashCode());
   }

   @Override
   public String toString() {
      return "ChatResponse(player=" + this.getPlayer() + ", response=" + this.getResponse() + ")";
   }
}

package net.Indyuce.mmocore.skill.cast;

public enum PlayerKey {
   LEFT_CLICK(false),
   RIGHT_CLICK(false),
   DROP(true),
   SWAP_HANDS(true),
   CROUCH(false);

   private final boolean cancellableEvent;

   PlayerKey(boolean cancelableEvent) {
      this.cancellableEvent = cancelableEvent;
   }

   public boolean shouldCancelEvent() {
      return this.cancellableEvent;
   }
}

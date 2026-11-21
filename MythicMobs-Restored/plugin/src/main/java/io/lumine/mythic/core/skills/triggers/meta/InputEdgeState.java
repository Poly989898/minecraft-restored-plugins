package io.lumine.mythic.core.skills.triggers.meta;

public record InputEdgeState(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
   public static InputEdgeState from(InputState previous, InputState current) {
      InputState oldState = previous == null ? InputState.EMPTY : previous;
      InputState newState = current == null ? InputState.EMPTY : current;
      return new InputEdgeState(
         !oldState.forward() && newState.forward(),
         !oldState.backward() && newState.backward(),
         !oldState.left() && newState.left(),
         !oldState.right() && newState.right(),
         !oldState.jump() && newState.jump(),
         !oldState.sneak() && newState.sneak(),
         !oldState.sprint() && newState.sprint()
      );
   }

   public boolean any() {
      return this.forward || this.backward || this.left || this.right || this.jump || this.sneak || this.sprint;
   }
}

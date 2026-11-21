package io.lumine.mythic.core.skills.triggers.meta;

import java.lang.reflect.Method;
import java.util.Locale;

public record InputState(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
   public static final InputState EMPTY = new InputState(false, false, false, false, false, false, false);
   private static Method isForwardMethod;
   private static Method isBackwardMethod;
   private static Method isLeftMethod;
   private static Method isRightMethod;
   private static Method isJumpMethod;
   private static Method isSneakMethod;
   private static Method isSprintMethod;
   private static boolean methodsCached = false;
   private static boolean supported = true;

   private static void initMethods(Class<?> inputClass) {
      if (!methodsCached && supported) {
         try {
            isForwardMethod = inputClass.getMethod("isForward");
            isBackwardMethod = inputClass.getMethod("isBackward");
            isLeftMethod = inputClass.getMethod("isLeft");
            isRightMethod = inputClass.getMethod("isRight");
            isJumpMethod = inputClass.getMethod("isJump");
            isSneakMethod = inputClass.getMethod("isSneak");
            isSprintMethod = inputClass.getMethod("isSprint");
            methodsCached = true;
         } catch (NoSuchMethodException e) {
            supported = false;
         }
      }
   }

   public static InputState from(Object input) {
      if (input == null) {
         return EMPTY;
      } else {
         initMethods(input.getClass());
         if (!supported) {
            return EMPTY;
         } else {
            try {
               return new InputState(
                  (Boolean)isForwardMethod.invoke(input),
                  (Boolean)isBackwardMethod.invoke(input),
                  (Boolean)isLeftMethod.invoke(input),
                  (Boolean)isRightMethod.invoke(input),
                  (Boolean)isJumpMethod.invoke(input),
                  (Boolean)isSneakMethod.invoke(input),
                  (Boolean)isSprintMethod.invoke(input)
               );
            } catch (Exception e) {
               return EMPTY;
            }
         }
      }
   }

   public boolean isPressed(String key) {
      if (key == null) {
         return false;
      } else {
         String str = key.toLowerCase(Locale.ROOT);

         return switch (str) {
            case "forward", "w" -> this.forward;
            case "backward", "s" -> this.backward;
            case "left", "a" -> this.left;
            case "right", "d" -> this.right;
            case "jump" -> this.jump;
            case "sneak" -> this.sneak;
            case "sprint" -> this.sprint;
            default -> false;
         };
      }
   }
}

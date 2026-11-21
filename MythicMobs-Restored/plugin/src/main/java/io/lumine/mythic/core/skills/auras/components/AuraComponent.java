package io.lumine.mythic.core.skills.auras.components;

public interface AuraComponent {
   default void auraStart() {
   }

   default void auraTick() {
   }

   default void auraStop() {
   }
}

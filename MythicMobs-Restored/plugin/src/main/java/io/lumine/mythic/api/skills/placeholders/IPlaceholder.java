package io.lumine.mythic.api.skills.placeholders;

public interface IPlaceholder {
   boolean isStatic();

   boolean isPresent();

   String serialize();
}

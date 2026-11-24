package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.gson.JsonObject;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public enum RequestType {
   PLAYER_CHOSE_PROFILE(obj -> new PlayerChoseProfileRequest(obj)),
   PLAYER_UNSELECT_PROFILE(obj -> new PlayerUnselectProfileRequest(obj)),
   @Deprecated
   PROFILE_SELECTION_FALLBACK(obj -> new ProfileSelectionFallbackRequest(obj)),
   @Deprecated
   SERVER_DISPATCH_PLAYER_INFO(obj -> new ProfileRequest(obj)),
   @Deprecated
   PLAYER_SWITCH_PROFILE(obj -> new ProfileRequest(obj));

   private final Function<JsonObject, Request> reader;

   RequestType(Function<JsonObject, Request> reader) {
      this.reader = reader;
   }

   @NotNull
   public Request readRequest(JsonObject requestObject) {
      return this.reader.apply(requestObject);
   }
}

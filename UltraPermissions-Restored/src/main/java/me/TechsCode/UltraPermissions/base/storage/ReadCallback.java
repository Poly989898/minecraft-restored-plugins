package me.TechsCode.UltraPermissions.base.storage;

import java.util.HashMap;
import com.google.gson.JsonObject;

public interface ReadCallback {
   void onSuccess(HashMap<String, JsonObject> map);

   void onFailure(Exception exception);
}

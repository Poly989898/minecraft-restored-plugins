package com.ticxo.modelengine.api.nms.entity.wrapper;

import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;

public interface BodyRotationController extends DataIO {
   default void tick() {
   }

   float getYHeadRot();

   void setYHeadRot(float rot);

   float getXHeadRot();

   void setXHeadRot(float rot);

   float getYBodyRot();

   void setYBodyRot(float rot);

   boolean isHeadClampUneven();

   void setHeadClampUneven(boolean isHeadClampUneven);

   boolean isBodyClampUneven();

   void setBodyClampUneven(boolean isBodyClampUneven);

   float getMaxHeadAngle();

   void setMaxHeadAngle(float maxHeadAngle);

   float getMaxBodyAngle();

   void setMaxBodyAngle(float maxBodyAngle);

   float getMinHeadAngle();

   void setMinHeadAngle(float minHeadAngle);

   float getMinBodyAngle();

   void setMinBodyAngle(float minBodyAngle);

   float getStableAngle();

   void setStableAngle(float stableAngle);

   boolean isPlayerMode();

   void setPlayerMode(boolean playerMode);

   int getRotationDelay();

   void setRotationDelay(int rotationDelay);

   int getRotationDuration();

   void setRotationDuration(int rotationDuration);

   @Override
   default void save(SavedData data) {
      data.putBoolean("head", this.isHeadClampUneven());
      data.putBoolean("body", this.isBodyClampUneven());
      data.putFloat("max_head", this.getMaxHeadAngle());
      data.putFloat("max_body", this.getMaxBodyAngle());
      data.putFloat("min_head", this.getMinHeadAngle());
      data.putFloat("min_body", this.getMinBodyAngle());
      data.putFloat("stable", this.getStableAngle());
      data.putBoolean("player", this.isPlayerMode());
      data.putInt("delay", this.getRotationDelay());
      data.putInt("duration", this.getRotationDuration());
   }

   @Override
   default void load(SavedData data) {
      this.setHeadClampUneven(data.getBoolean("head"));
      this.setBodyClampUneven(data.getBoolean("body"));
      this.setMaxHeadAngle(data.getFloat("max_head"));
      this.setMaxBodyAngle(data.getFloat("max_body"));
      this.setMinHeadAngle(data.getFloat("min_head"));
      this.setMinBodyAngle(data.getFloat("min_body"));
      this.setStableAngle(data.getFloat("stable"));
      this.setPlayerMode(data.getBoolean("player"));
      this.setRotationDelay(data.getInt("delay"));
      this.setRotationDuration(data.getInt("duration"));
   }
}

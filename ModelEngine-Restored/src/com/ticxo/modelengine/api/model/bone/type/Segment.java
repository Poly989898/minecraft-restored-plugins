package com.ticxo.modelengine.api.model.bone.type;

import org.joml.Vector3f;

public interface Segment {
   void setBounded(boolean bounded);

   boolean isBounded();

   void setMinX(float minX);

   float getMinX();

   void setMaxX(float maxX);

   float getMaxX();

   void setMinY(float minY);

   float getMinY();

   void setMaxY(float maxY);

   float getMaxY();

   void setMinZ(float minZ);

   float getMinZ();

   void setMaxZ(float maxZ);

   float getMaxZ();

   void setAlignRate(float alignRate);

   float getAlignRate();

   void setElasticity(float elasticity);

   float getElasticity();

   Vector3f getWorldLocation();

   Vector3f getDelta();
}

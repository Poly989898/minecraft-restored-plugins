package com.ticxo.modelengine.api.utils.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Triple<A, B, C> {
   private A first;
   private B second;
   private C third;
}

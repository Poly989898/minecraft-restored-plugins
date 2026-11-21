package com.nexomc.nexo.nms;

import kotlin.Metadata;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\bf\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002J\u0015\u0010\u0003\u001a\u00028\u00002\u0006\u0010\u0004\u001a\u00028\u0000H&¢\u0006\u0002\u0010\u0005¨\u0006\u0006À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/Transformer;", "T", "", "transform", "input", "(Ljava/lang/Object;)Ljava/lang/Object;", "com.nexomc:core"}
)
public interface Transformer<T> {
   T transform(T t);
}

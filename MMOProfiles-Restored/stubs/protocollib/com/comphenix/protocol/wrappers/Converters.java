package com.comphenix.protocol.wrappers;
public class Converters {
    public interface Converter<T> {}
    public static <T> Converter<T> passthrough(Class<T> type) { return null; }
}

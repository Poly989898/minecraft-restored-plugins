/*
 * Reconstructed for LPX (fork-only class). Not part of upstream PacketEvents.
 *
 * Upstream PacketEvents is distributed under the GNU GPL v3 (see
 * packetevents-lpx/LICENSE-PacketEvents.txt); this file follows the same license.
 */
package com.github.retrooper.packetevents.wrapper;

/**
 * Member-name lookup helper used by LPX through reflection (for example
 * {@code getRecipeManager} on the server's recipe holder).
 *
 * <p>The original fork ships a table, encrypted and keyed per build, that maps readable
 * names to the obfuscated names of older server jars. That table is NOT reconstructed here:
 * its key and entries were not recovered without running the original code, which was not
 * done. Every lookup therefore returns the name it was given. This is correct for servers
 * whose classes already use readable names (Paper 26.x), and wrong only for obfuscated
 * server jars that need the fork's mapping. The signatures match the original calls:
 * {@code b(name, owner, parameterTypes)} for methods and {@code c(owner, name)} for fields.
 */
public final class e1 {

    private e1() {
    }

    /** Class-name mapping (identity). */
    public static String a(String className) {
        return className;
    }

    /** Method-name mapping (identity). */
    public static String b(String methodName, Class owner, Class[] parameterTypes) {
        return methodName;
    }

    /** Field-name mapping (identity). */
    public static String c(Class owner, String fieldName) {
        return fieldName;
    }
}

package com.jofkos.signs.utils.nms;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * <b>ReflectionUtil</b> - Reflection handler for NMS and CraftBukkit.<br>
 * Caches the packet related methods and is asynchronous.
 * <p>
 * This class does not handle null checks as most of the requests are from the
 * other utility classes that already handle null checks.
 * <p>
 * <a href="https://wiki.vg/Protocol">Clientbound Packets</a> are considered fake
 * updates to the client without changing the actual data. Since all the data is handled
 * by the server.
 *
 * @author Crypto Morin, Stijn Bannink
 * @version 2.1
 */
public class ReflectionUtil {
    /**
     * We use reflection mainly to avoid writing a new class for version barrier.
     * The version barrier is for NMS that uses the Minecraft version as the main package name.
     * <p>
     * E.g. EntityPlayer in 1.15 is in the class {@code net.minecraft.server.v1_15_R1}
     * but in 1.14 it's in {@code net.minecraft.server.v1_14_R1}
     * In order to maintain cross-version compatibility we cannot import these classes.
     */
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final String CRAFTBUKKIT = "org.bukkit.craftbukkit." + VERSION + '.';
    public static final String NMS = "net.minecraft.server." + VERSION + '.';

    private ReflectionUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static Class<?> wrapperToPrimitive(Class<?> clazz) {
        if (clazz == Boolean.class) return boolean.class;
        if (clazz == Integer.class) return int.class;
        if (clazz == Double.class) return double.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Short.class) return short.class;
        if (clazz == Byte.class) return byte.class;
        if (clazz == Void.class) return void.class;
        if (clazz == Character.class) return char.class;
        return clazz;
    }

    private static Class<?>[] toParamTypes(Object... params) {
        return Arrays.stream(params)
                .map(obj -> wrapperToPrimitive(obj.getClass()))
                .toArray(Class<?>[]::new);
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get a NMS (net.minecraft.server) class.
     *
     * @param name the name of the class.
     * @return the NMS class or null if not found.
     * @since 1.0.0
     */
    public static Class<?> getNMSClass(String name) {
        return getClass(NMS + name);
    }

    /**
     * Get a CraftBukkit (org.bukkit.craftbukkit) class.
     *
     * @param name the name of the class to load.
     * @return the CraftBukkit class or null if not found.
     * @since 1.0.0
     */
    public static Class<?> getCraftClass(String name) {
        return getClass(CRAFTBUKKIT + name);
    }

    public static Object callConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object callDeclaredConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getDeclaredConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object callMethod(Object obj, String method, Object... params) {
        try {
            Method m = obj.getClass().getMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(obj, params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object callMethod(Class<?> clazz, String method, Object... params) {
        try {
            Method m = clazz.getMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(null, params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object callDeclaredMethod(Object obj, String method, Object... params) {
        try {
            Method m = obj.getClass().getDeclaredMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(obj, params);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getField(Object object, String field) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getDeclaredField(Object object, String field) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getHandle(Entity e) {
        return callMethod(e, "getHandle");
    }

    public static Object getHandle(World w) {
        return callMethod(w, "getHandle");
    }

    public static Object getPlayerConnection(Object handle) {
        return getDeclaredField(handle, "playerConnection");
    }

    public static Object getPlayerConnection(Player p) {
        return getPlayerConnection(getHandle(p));
    }

    /**
     * Sends a packet to the player asynchronously if they're online.
     * Packets are thread-safe.
     *
     * @param player  the player to send the packet to.
     * @param packets the packets to send.
     * @return the async thread handling the packet.
     * @see #sendPacketSync(Player, Object...)
     * @since 1.0.0
     */
    public static CompletableFuture<Void> sendPacket(Player player, Object... packets) {
        return CompletableFuture.runAsync(() -> sendPacketSync(player, packets)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    /**
     * Sends a packet to the player synchronously if they're online.
     *
     * @param player  the player to send the packet to.
     * @param packets the packets to send.
     * @see #sendPacket(Player, Object...)
     * @since 2.0.0
     */
    public static void sendPacketSync(Player player, Object... packets) {
        try {
            Object connection = getPlayerConnection(player);
            if (connection != null) {
                for (Object packet : packets) callMethod(connection, "sendPacket", packet);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
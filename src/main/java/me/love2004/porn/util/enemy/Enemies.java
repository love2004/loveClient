/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ConcurrentSet
 */
package me.love2004.porn.util.enemy;

import io.netty.util.internal.ConcurrentSet;

public class Enemies {
    private static ConcurrentSet<Enemy> enemies = new ConcurrentSet();

    public static void addEnemy(String name) {
        enemies.add((Enemy) new Enemy(name));
    }

    public static void delEnemy(String name) {
        enemies.remove((Object)Enemies.getEnemyByName(name));
    }

    public static Enemy getEnemyByName(String name) {
        for (Enemy e : enemies) {
            if (!e.username.equalsIgnoreCase(name)) continue;
            return e;
        }
        return null;
    }

    public static ConcurrentSet<Enemy> getEnemies() {
        return enemies;
    }

    public static boolean isEnemy(String name) {
        return enemies.stream().anyMatch(enemy -> enemy.username.equalsIgnoreCase(name));
    }
}


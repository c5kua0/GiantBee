package me.giantbee;

import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class GiantBeeSkills {

    private static long venomCooldown = 0;
    private static long rushCooldown = 0;
    private static long swarmCooldown = 0;
    private static long guardCooldown = 0;
    private static long wrathCooldown = 0;

    private GiantBeeSkills() {}

    public static void run(
            GiantBeePlugin plugin,
            Bee boss,
            Player target,
            int phase
    ) {

        long now = System.currentTimeMillis();

        double distance =
                boss.getLocation()
                        .distance(target.getLocation());

        /*
         * VENOM STING
         */
        if (now >= venomCooldown &&
                distance <= 12) {

            venom(boss, target);

            venomCooldown =
                    now + 8000;
        }

        /*
         * BEE RUSH
         */
        if (now >= rushCooldown &&
                distance > 6 &&
                distance <= 25) {

            rush(boss, target);

            rushCooldown =
                    now + 12000;
        }

        /*
         * SWARM
         */
        if (phase >= 2 &&
                now >= swarmCooldown) {

            swarm(boss, target);

            swarmCooldown =
                    now + 20000;
        }

        /*
         * ROYAL GUARD
         */
        if (phase >= 2 &&
                boss.getHealth() <= 180 &&
                now >= guardCooldown) {

            guard(boss);

            guardCooldown =
                    now + 25000;
        }

        /*
         * QUEEN'S WRATH
         */
        if (phase == 3 &&
                now >= wrathCooldown) {

            wrath(plugin, boss);

            wrathCooldown =
                    now + 35000;
        }
    }

    private static void venom(
            Bee boss,
            Player target
    ) {

        target.damage(5.0, boss);

        target.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.POISON,
                        100,
                        0
                )
        );

        target.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SLOWNESS,
                        60,
                        0
                )
        );

        target.sendActionBar(
                net.kyori.adventure.text.Component.text(
                        "VENOM STING!"
                )
        );

        target.getWorld().playSound(
                target.getLocation(),
                Sound.ENTITY_BEE_STING,
                2f,
                0.8f
        );
    }

    private static void rush(
            Bee boss,
            Player target
    ) {

        Vector direction =
                target.getLocation()
                        .toVector()
                        .subtract(
                                boss.getLocation()
                                        .toVector()
                        )
                        .normalize();

        boss.setVelocity(
                direction.multiply(1.8)
        );

        target.sendActionBar(
                net.kyori.adventure.text.Component.text(
                        "BEE RUSH!"
                )
        );
    }

    private static void swarm(
            Bee boss,
            Player target
    ) {

        World world = boss.getWorld();

        Bukkit.broadcast(
                net.kyori.adventure.text.Component.text(
                        "GIANT BEE summoned a SWARM!"
                )
        );

        for (int i = 0; i < 4; i++) {

            Bee minion =
                    world.spawn(
                            boss.getLocation(),
                            Bee.class
                    );

            minion.customName(
                    net.kyori.adventure.text.Component.text(
                            "Swarm Bee"
                    )
            );

            minion.setMaxHealth(12);
            minion.setHealth(12);

            minion.setTarget(target);
        }
    }

    private static void guard(Bee boss) {

        boss.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.RESISTANCE,
                        120,
                        3
                )
        );

        boss.addPotionEffect(
                new PotionEffect(
                        PotionEffectType.REGENERATION,
                        120,
                        2
                )
        );

        Bukkit.broadcast(
                net.kyori.adventure.text.Component.text(
                        "ROYAL GUARD!"
                )
        );

        boss.getWorld().spawnParticle(
                Particle.ENCHANT,
                boss.getLocation(),
                100,
                2,
                2,
                2,
                0.5
        );
    }

    private static void wrath(
            GiantBeePlugin plugin,
            Bee boss
    ) {

        Bukkit.broadcast(
                net.kyori.adventure.text.Component.text(
                        "⚠ QUEEN'S WRATH ⚠"
                )
        );

        Location center =
                boss.getLocation();

        center.getWorld().spawnParticle(
                Particle.FLAME,
                center,
                180,
                3,
                3,
                3,
                0.08
        );

        center.getWorld().playSound(
                center,
                Sound.ENTITY_WITHER_SPAWN,
                2f,
                1.4f
        );

        plugin.getServer()
                .getScheduler()
                .runTaskLater(
                        plugin,
                        () -> {

                            if (boss.isDead()) {
                                return;
                            }

                            for (Player player :
                                    center.getWorld()
                                            .getPlayers()) {

                                if (player.getLocation()
                                        .distanceSquared(center)
                                        <= 144) {

                                    player.damage(
                                            12.0,
                                            boss
                                    );

                                    player.setVelocity(
                                            new Vector(
                                                    0,
                                                    1.2,
                                                    0
                                            )
                                    );

                                    player.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.POISON,
                                                    120,
                                                    1
                                            )
                                    );
                                }
                            }
                        },
                        40L
                );
    }
}
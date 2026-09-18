package me.giantbee;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class GiantBeeListener implements Listener {

    private final GiantBeePlugin plugin;
    private final GiantBeeBoss boss;

    public GiantBeeListener(
            GiantBeePlugin plugin,
            GiantBeeBoss boss
    ) {

        this.plugin = plugin;
        this.boss = boss;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Bee bee)) {
            return;
        }

        if (bee != boss.getBee()) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL ||
                event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                event.getCause() == EntityDamageEvent.DamageCause.LAVA) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAttack(
            EntityDamageByEntityEvent event
    ) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!(event.getDamager() instanceof Bee bee)) {
            return;
        }

        if (bee != boss.getBee()) {
            return;
        }

        if (Math.random() <= 0.25) {

            player.addPotionEffect(
                    new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.POISON,
                            60,
                            0
                    )
            );

            player.sendActionBar(
                    Component.text(
                            "VENOM!"
                    ).color(NamedTextColor.DARK_GREEN)
            );
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        Entity entity = event.getEntity();

        if (!(entity instanceof Bee bee)) {
            return;
        }

        if (bee != boss.getBee()) {
            return;
        }

        event.getDrops().clear();
        event.setDroppedExp(0);

        Location location =
                bee.getLocation();

        /*
         * Boss announcement
         */
        Bukkit.broadcast(
                Component.text(
                        "✦ GIANT BEE DEFEATED ✦"
                ).color(NamedTextColor.GOLD)
        );

        /*
         * Death effects
         */
        location.getWorld().spawnParticle(
                Particle.FIREWORK,
                location,
                120,
                2,
                2,
                2,
                0.1
        );

        location.getWorld().playSound(
                location,
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                3f,
                1f
        );

        /*
         * Rewards
         */
        for (Player player :
                location.getWorld().getPlayers()) {

            if (player.getLocation()
                    .distanceSquared(location) > 625) {

                continue;
            }

            player.giveExp(30);

            player.getInventory().addItem(
                    new ItemStack(
                            Material.HONEYCOMB,
                            8
                    )
            );

            player.getInventory().addItem(
                    new ItemStack(
                            Material.GOLD_INGOT,
                            4
                    )
            );

            player.sendMessage(
                    Component.text(
                            "BOSS REWARD: 8 Honeycomb + 4 Gold + 30 XP"
                    ).color(NamedTextColor.GOLD)
            );

            /*
             * Rare reward
             */
            if (Math.random() < 0.15) {

                player.getInventory().addItem(
                        new ItemStack(
                                Material.BEE_SPAWN_EGG,
                                1
                        )
                );

                player.sendMessage(
                        Component.text(
                                "RARE DROP: Bee Spawn Egg!"
                        ).color(
                                NamedTextColor.LIGHT_PURPLE
                        )
                );
            }

            /*
             * Legendary reward
             */
            if (Math.random() < 0.05) {

                player.getInventory().addItem(
                        new ItemStack(
                                Material.HONEY_BLOCK,
                                1
                        )
                );

                player.sendMessage(
                        Component.text(
                                "LEGENDARY DROP: Honey Block!"
                        ).color(
                                NamedTextColor.GOLD
                        )
                );
            }
        }

        boss.remove();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (boss.getBossBar() != null &&
                boss.isAlive()) {

            event.getPlayer()
                    .showBossBar(
                            boss.getBossBar()
                    );
        }
    }
}
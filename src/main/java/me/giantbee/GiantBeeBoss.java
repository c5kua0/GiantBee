package me.giantbee;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiantBeeBoss {

    private final GiantBeePlugin plugin;

    private Bee bee;
    private BossBar bossBar;
    private BukkitTask aiTask;

    private int phase = 1;
    private int tickCounter = 0;

    private final Map<UUID, Long> players = new HashMap<>();

    public GiantBeeBoss(GiantBeePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn the Giant Bee.
     */
    public void spawn(Location location) {

        if (bee != null && !bee.isDead()) {
            return;
        }

        bee = location.getWorld().spawn(location, Bee.class);

        bee.setCustomName("§6§lGIANT BEE");
        bee.setCustomNameVisible(true);

        // Health
        if (bee.getAttribute(Attribute.MAX_HEALTH) != null) {
            bee.getAttribute(Attribute.MAX_HEALTH).setBaseValue(300.0);
        }

        bee.setHealth(300.0);

        // Movement speed
        if (bee.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            bee.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.55);
        }

        // Giant size
        if (bee.getAttribute(Attribute.SCALE) != null) {
            bee.getAttribute(Attribute.SCALE).setBaseValue(3.0);
        }

        bee.setRemoveWhenFarAway(false);

        createBossBar();

        startAI();

        bee.getWorld().playSound(
                bee.getLocation(),
                Sound.ENTITY_BEE_LOOP,
                5.0f,
                0.5f
        );

        bee.getWorld().spawnParticle(
                Particle.CLOUD,
                bee.getLocation(),
                30,
                1.0,
                1.0,
                1.0,
                0.1
        );

        Bukkit.broadcast(
                Component.text("⚠ GIANT BEE HAS SPAWNED!")
                        .color(NamedTextColor.GOLD)
        );
    }

    /**
     * Create the boss bar.
     */
    private void createBossBar() {

        bossBar = BossBar.bossBar(
                Component.text("GIANT BEE")
                        .color(NamedTextColor.GOLD),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addViewer(player);
        }
    }

    /**
     * Main AI loop.
     */
    private void startAI() {

        aiTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {

                    if (bee == null || bee.isDead()) {
                        stopAI();
                        return;
                    }

                    updateBossBar();

                    LivingEntity target = getNearestPlayer();

                    if (target != null) {
                        bee.setTarget(target);

                        players.put(
                                target.getUniqueId(),
                                System.currentTimeMillis()
                        );
                    }

                    updatePhase();

                    tickCounter++;

                    /*
                     * Skill cycle
                     */
                    if (tickCounter % 100 == 0) {
                        if (phase == 1) {
                            GiantBeeSkills.venomSting(plugin, this);
                        }
                    }

                    if (tickCounter % 160 == 0) {
                        if (phase >= 2) {
                            GiantBeeSkills.beeRush(plugin, this);
                        }
                    }

                    if (tickCounter % 220 == 0) {
                        if (phase >= 2) {
                            GiantBeeSkills.swarm(plugin, this);
                        }
                    }

                    if (tickCounter % 300 == 0) {
                        if (phase >= 3) {
                            GiantBeeSkills.royalGuard(plugin, this);
                        }
                    }

                    if (tickCounter % 400 == 0) {
                        if (phase >= 3) {
                            GiantBeeSkills.queensWrath(plugin, this);
                        }
                    }

                    /*
                     * Ambient particles
                     */
                    if (tickCounter % 10 == 0) {
                        bee.getWorld().spawnParticle(
                                Particle.HAPPY_VILLAGER,
                                bee.getLocation(),
                                4,
                                0.8,
                                0.8,
                                0.8,
                                0.05
                        );
                    }

                },
                1L,
                1L
        );
    }

    /**
     * Update boss bar.
     */
    private void updateBossBar() {

        if (bossBar == null || bee == null) {
            return;
        }

        double health = bee.getHealth();

        double maxHealth = 300.0;

        float progress = (float) Math.max(
                0.0,
                Math.min(1.0, health / maxHealth)
        );

        bossBar.progress(progress);

        String phaseText;

        if (phase == 1) {
            phaseText = "PHASE 1";
        } else if (phase == 2) {
            phaseText = "PHASE 2";
        } else {
            phaseText = "PHASE 3";
        }

        bossBar.name(
                Component.text(
                        "GIANT BEE • " + phaseText
                ).color(NamedTextColor.GOLD)
        );
    }

    /**
     * Change phases based on health.
     */
    private void updatePhase() {

        if (bee == null) {
            return;
        }

        double health = bee.getHealth();

        if (health <= 100.0 && phase < 3) {

            phase = 3;

            Bukkit.broadcast(
                    Component.text(
                            "⚠ GIANT BEE HAS ENTERED PHASE 3!"
                    ).color(NamedTextColor.RED)
            );

            bee.getWorld().playSound(
                    bee.getLocation(),
                    Sound.ENTITY_BEE_HURT,
                    5.0f,
                    0.5f
            );

            bee.getWorld().spawnParticle(
                    Particle.FLAME,
                    bee.getLocation(),
                    50,
                    1.5,
                    1.5,
                    1.5,
                    0.1
            );

        } else if (health <= 200.0 && phase < 2) {

            phase = 2;

            Bukkit.broadcast(
                    Component.text(
                            "⚠ GIANT BEE HAS ENTERED PHASE 2!"
                    ).color(NamedTextColor.YELLOW)
            );

            bee.getWorld().playSound(
                    bee.getLocation(),
                    Sound.ENTITY_BEE_HURT,
                    4.0f,
                    0.7f
            );

            bee.getWorld().spawnParticle(
                    Particle.CLOUD,
                    bee.getLocation(),
                    40,
                    1.5,
                    1.5,
                    1.5,
                    0.1
            );
        }
    }

    /**
     * Find the nearest player.
     */
    private LivingEntity getNearestPlayer() {

        if (bee == null || bee.getWorld() == null) {
            return null;
        }

        Player nearest = null;
        double nearestDistance = 32.0 * 32.0;

        for (Player player : bee.getWorld().getPlayers()) {

            if (!player.isAlive()) {
                continue;
            }

            double distance =
                    player.getLocation()
                            .distanceSquared(bee.getLocation());

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }

    /**
     * Check whether an entity is the Giant Bee.
     */
    public boolean isBoss(LivingEntity entity) {

        return bee != null
                && entity != null
                && entity.getUniqueId().equals(bee.getUniqueId());
    }

    /**
     * Get the actual Bee entity.
     */
    public Bee getBee() {
        return bee;
    }

    /**
     * Get current phase.
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Check if the boss exists.
     */
    public boolean exists() {
        return bee != null && !bee.isDead();
    }

    /**
     * Get players who participated.
     */
    public Map<UUID, Long> getPlayers() {
        return players;
    }

    /**
     * Remove the boss.
     */
    public void kill() {

        if (bee != null && !bee.isDead()) {
            bee.remove();
        }

        stopAI();

        if (bossBar != null) {
            bossBar.removeViewer(Bukkit.getOnlinePlayers());
        }

        bossBar = null;
        bee = null;
        phase = 1;
        tickCounter = 0;
        players.clear();
    }

    /**
     * Stop AI task.
     */
    private void stopAI() {

        if (aiTask != null) {
            aiTask.cancel();
            aiTask = null;
        }
    }
}
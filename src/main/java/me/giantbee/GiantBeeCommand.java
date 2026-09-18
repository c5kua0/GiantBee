package me.giantbee;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GiantBeeCommand
        implements CommandExecutor, TabCompleter {

    private final GiantBeePlugin plugin;
    private final GiantBeeBoss boss;

    public GiantBeeCommand(
            GiantBeePlugin plugin,
            GiantBeeBoss boss
    ) {

        this.plugin = plugin;
        this.boss = boss;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "Only players can use this command."
            );

            return true;
        }

        if (!player.hasPermission(
                "giantbee.admin"
        )) {

            player.sendMessage(
                    "§cYou don't have permission."
            );

            return true;
        }

        if (args.length == 0) {

            sendHelp(player);

            return true;
        }

        switch (args[0].toLowerCase()) {

            case "spawn":

                boss.spawn(player);

                break;

            case "kill":

                if (boss.isAlive()) {

                    boss.remove();

                    player.sendMessage(
                            "§cGiant Bee removed."
                    );

                } else {

                    player.sendMessage(
                            "§cThere is no Giant Bee alive."
                    );
                }

                break;

            case "info":

                player.sendMessage(
                        "§6§lGIANT BEE"
                );

                player.sendMessage(
                        "§eHealth: §f300"
                );

                player.sendMessage(
                        "§eSize: §f3x"
                );

                player.sendMessage(
                        "§ePhase 1: §fVenom Sting + Bee Rush"
                );

                player.sendMessage(
                        "§6Phase 2: §fSwarm + Royal Guard"
                );

                player.sendMessage(
                        "§4Phase 3: §fQueen's Wrath"
                );

                break;

            default:

                sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {

        player.sendMessage(
                "§6§l===== GIANT BEE ====="
        );

        player.sendMessage(
                "§e/giantbee spawn"
        );

        player.sendMessage(
                "§e/giantbee kill"
        );

        player.sendMessage(
                "§e/giantbee info"
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {

            return Arrays.asList(
                    "spawn",
                    "kill",
                    "info"
            );
        }

        return Collections.emptyList();
    }
}
package me.b77.archaiccore.Commands;

import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

public class ArchaicCommand implements CommandExecutor {

    private final ArchaicCommands commands = new ArchaicCommands();

    private final PlayerDatabase playerDatabase = new PlayerDatabase();
    private final TeamDatabase teamDatabase = new TeamDatabase();
    private final BeaconDatabase beaconDatabase = new BeaconDatabase();
    private final HashMap<Player, Integer> pendingMoves = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = ChatColor.stripColor(player.getDisplayName());

            if (command.getName().equalsIgnoreCase("archaic")) {
                if (args.length > 0) {
                    String subCommand = args[0].toLowerCase();
                    switch (subCommand) {
                        case "move":
                            commands.handleMoveCommand(player, args, playerName);
                            break;
                        case "beacons":
                            commands.handleBeaconsCommand(player, args);
                            break;
                        case "teams":
                            commands.handleTeamsCommand(player, args, playerName);
                            break;
                        case "confirm":
                            commands.handleConfirmCommand(player, args, playerName);
                            break;
                        case "transfer":
                            commands.handleTransferCommand(player, args, playerName);
                        default:
                            player.sendMessage("Unknown sub-command. Type /archaic help for help.");
                            break;
                    }
                } else {
                    player.sendMessage("Usage: /archaic <move/beacons/teams> [arguments]");
                }
            } else {
                player.sendMessage("Unknown command. Type /help for help.");
            }
        }
        return true;
    }

}
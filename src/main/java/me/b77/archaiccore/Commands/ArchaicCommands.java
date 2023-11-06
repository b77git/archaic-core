package me.b77.archaiccore.Commands;

import me.b77.archaiccore.beacon.BeaconGui;
import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import me.b77.archaiccore.team.TeamGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class ArchaicCommands {

    private final PlayerDatabase playerDatabase = new PlayerDatabase();
    private final TeamDatabase teamDatabase = new TeamDatabase();
    private final BeaconDatabase beaconDatabase = new BeaconDatabase(); // Assuming you have a BeaconDatabase class
    private final HashMap<Player, Integer> pendingMoves = new HashMap<>();

    public void handleTeamsCommand(Player player, String[] args, String playerName) {
        if (args.length == 1) {
            TeamGUI gui = new TeamGUI();
            gui.displayGUI(player);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("create")) {
            handleTeamCreateCommand(player, playerName, args);
        } else if (args.length == 2 && args[1].equalsIgnoreCase("leave")) {
            handleTeamLeaveCommand(player, playerName, args);
        } else {
            player.sendMessage("Unknown command. Type /help for help.");
            System.out.println(args.length);
        }
    }

    private void handleTeamCreateCommand(Player player, String playerName, String[] args) {
        String teamName = args[2];

        // Check if a team with the same name already exists
        if (teamDatabase.teamExists(teamName)) {
            player.sendMessage("A team with the name '" + teamName + "' already exists.");
            return;
        }

        if (playerDatabase.getTeamId(playerName) == 0) {
            int teamId = teamDatabase.getCount() + 1;
            teamDatabase.addTeam(teamName, playerName, teamId);
            playerDatabase.setTeamId(playerName, teamId);
            playerDatabase.setLeaderStatus(playerName, 1);
            player.getInventory().addItem(new ItemStack(Material.BEACON));
            player.sendMessage(teamName + " has been created!");
        } else {
            player.sendMessage("You are already in a team!");
        }
    }


    private void handleTeamLeaveCommand(Player player, String playerName, String[] args) {
        if (teamDatabase.get_Broken(playerDatabase.getTeamId(playerName)) == 1){
            player.sendMessage("You can not leave due to a broken beacon!");
            return;
        }
        playerDatabase.leave_team(playerName);
        player.sendMessage("You have left your team!");
    }

    public void handleBeaconsCommand(Player player, String[] args) {
        if (player.isOp()) {
            if (args.length == 1) {
                BeaconGui gui = new BeaconGui();
                gui.displayGUI(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not an admin!");
        }
    }

    public void handleMoveCommand(Player player, String[] args, String playerName) {
        int teamId = playerDatabase.getTeamId(playerName);

        if (teamId == 0) {
            player.sendMessage("You must be in a team to use this command.");
            return;
        }

        if (pendingMoves.containsKey(player)) {
            player.sendMessage("You have already requested a move. Use /archaic confirm to confirm.");
        } else {
            pendingMoves.put(player, teamId);
            player.sendMessage("You have requested a move. Use /archaic confirm to confirm.");
        }
    }

    public void handleConfirmCommand(Player player, String[] args, String playerName) {
        if (!pendingMoves.containsKey(player)) {
            player.sendMessage("You haven't requested a move. Use /archaic move first.");
            return;
        }

        int teamId = pendingMoves.get(player);

        if (teamId == 0) {
            player.sendMessage("You are not in a team with a placed beacon.");
            return;
        }

        if (teamDatabase.getMoveCount(teamId) == 2){
            player.sendMessage("You have already moved the beacon twice.");
            return;
        }

        if (!playerDatabase.isLeader(playerName) && !player.isOp()) {
            player.sendMessage("Only the team leader or an operator can use this command.");
            return;
        }

        ArrayList<Integer> beaconCoords = beaconDatabase.getCoords(teamId);

        if (beaconCoords.isEmpty()) {
            player.sendMessage("Your team has not placed a beacon.");
            return;
        }

        Location beaconLocation = new Location(player.getWorld(), beaconCoords.get(0), beaconCoords.get(1), beaconCoords.get(2));
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (!onlinePlayer.getName().equals(player.getName()) && teamDatabase.get_team_id(ChatColor.stripColor(onlinePlayer.getName())) != teamId) {
                Location playerLocation = onlinePlayer.getLocation();
                double distance = beaconLocation.distance(playerLocation);

                if (distance <= 300) {
                    player.sendMessage("There are players from other teams within 300 blocks of your beacon. Command not executed.");
                }
            }
        }

        beaconDatabase.breakBeacon(teamId);

        Block beaconBlock = beaconLocation.getBlock();
        beaconBlock.setType(Material.AIR);

        player.getInventory().addItem(new ItemStack(Material.BEACON));

        teamDatabase.set_placed(teamId, 0);
        beaconDatabase.setTeamId(teamId, -beaconDatabase.getTeamId(teamId));

        teamDatabase.incrementMoveCount(teamId);

        player.sendMessage("Move successful! You have " + (2 - (teamDatabase.getMoveCount(teamId))) + " move(s) remaining.");
        pendingMoves.remove(player);

    }

    public void handleTransferCommand(Player player, String[] args, String playerName) {
        if (args.length == 1) {
            player.sendMessage("Usage: /archaic transfer <new_leader>");
            return;
        }

        String newLeaderName = args[1];
        String requesterName = ChatColor.stripColor(player.getDisplayName());

        if (playerDatabase.isLeader(requesterName)) {
            Player newLeader = Bukkit.getPlayerExact(newLeaderName);

            if (newLeader != null) {
                String newLeaderDisplayName = newLeader.getDisplayName();

                int teamId = playerDatabase.getTeamId(requesterName);
                if (teamId == playerDatabase.getTeamId(newLeaderName)) {
                    playerDatabase.setLeaderStatus(requesterName, 0);
                    playerDatabase.setLeaderStatus(newLeaderName, 1);

                    player.sendMessage("Leadership transferred to " + newLeaderDisplayName);
                    newLeader.sendMessage("You are now the leader of the team.");

                    for (Player teamMember : Bukkit.getOnlinePlayers()) {
                        String teamMemberName = ChatColor.stripColor(teamMember.getDisplayName());
                        if (playerDatabase.getTeamId(teamMemberName) == teamId && !teamMemberName.equals(requesterName)) {
                            teamMember.sendMessage(newLeaderDisplayName + " is now the leader of your team.");
                        }
                    }
                } else {
                    player.sendMessage("The new leader must be a member of your team.");
                }
            } else {
                player.sendMessage("Player '" + newLeaderName + "' is not online.");
            }
        } else {
            player.sendMessage("You must be the team leader to transfer leadership.");
        }
    }
}


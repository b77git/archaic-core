package me.b77.archaiccore.EventListeners;

import me.b77.archaiccore.Discord;
import me.b77.archaiccore.database.BeaconDatabase;
import me.b77.archaiccore.database.PlayerDatabase;
import me.b77.archaiccore.database.TeamDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.util.ArrayList;

public class KillEvent implements Listener {
    @EventHandler
    public void onDeath(PlayerRespawnEvent event){
        BeaconDatabase beaconDatabase = new BeaconDatabase();
        PlayerDatabase playerdatabase = new PlayerDatabase();

        Player player = event.getPlayer();
        String name = ChatColor.stripColor(player.getDisplayName());
        int teamid = playerdatabase.getTeamId(name);

        if (teamid != 0){
            ArrayList<Integer> coords = beaconDatabase.getCoords(teamid);

            World overworld = Bukkit.getWorld("world");

            if (overworld != null) {
                Location spawnLocation = new Location(overworld, coords.get(0) + 0.5, coords.get(1) + 1, coords.get(2) + 0.5, 0, 0);
                event.setRespawnLocation(spawnLocation);
            }
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) throws InterruptedException {
        PlayerDatabase playerdatabase = new PlayerDatabase();
        TeamDatabase teamdatabase = new TeamDatabase();

        Player killed = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller().getPlayer();

        String killerName = ChatColor.stripColor(killer.getDisplayName());
        String killedName = ChatColor.stripColor(killed.getDisplayName());
        int killed_id = playerdatabase.getTeamId(killedName);

        int killedDeaths = killed.getStatistic(Statistic.DEATHS);
        int killerKills = killer.getStatistic(Statistic.PLAYER_KILLS);
        ItemStack weapon = killer.getInventory().getItemInMainHand();

        KillMessage message = new KillMessage();
        message.send_embed(message.create_embed(killerName, killedName, killerKills + 1, killedDeaths + 1));

        if (playerdatabase.getlastlife(killedName) == 1){
            Bukkit.broadcastMessage(ChatColor.RED + killedName + " has been ELIMINATED!");
            for (Player player : Bukkit.getOnlinePlayers())
            {
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER,3.0f, 1.0f);
            }
            killed.getPlayer().kickPlayer("You have been eliminated!");
            Bukkit.getBanList(BanList.Type.NAME).addBan(killed.getName(), ChatColor.RED + "You have been eliminated but be sure to check discord for a revive event!", null, null);
        }

        if (teamdatabase.get_Broken(killed_id) == 1){
            playerdatabase.setlastlife(killedName);
            killed.sendMessage(ChatColor.AQUA + "Careful you are on your last life");
        }



    }
}

class KillMessage {
    private final JDA jda = Discord.get_JDA();

    public EmbedBuilder create_embed(String killer, String killed, int kills, int deaths){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(147,112,219));
        eb.setTitle(killer + " has killed " + killed);
        eb.addField("**" + killed + " still has a beacon and has not been eliminated**", "" , false);
        if (deaths == 1) {
            eb.addField(killed + "  has died a total of" + deaths + " time", "", false);
        } else {
            eb.addField(killed + "  has been killed a total of" + deaths + " times", "", false);
        }
        if (kills == 1) {
            eb.addField(killer + " Has killed " + kills + " player", "", false);
        } else {
            eb.addField(killer + " Has killed " + kills + " players", "", false);
        }
        eb.setThumbnail("https://minotar.net/avatar/" + killed);
        eb.setFooter("Bot developed by B77 for use of Archaic Network");
        return eb;

    }
    public void send_embed(EmbedBuilder eb) throws InterruptedException {
        TextChannel txtChannel = jda.awaitReady().getTextChannelById("1166864842653704232");
        txtChannel.sendMessageEmbeds(eb.build()).queue();
    }
}

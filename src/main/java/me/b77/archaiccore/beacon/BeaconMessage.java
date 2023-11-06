package me.b77.archaiccore.beacon;

import me.b77.archaiccore.Discord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class BeaconMessage {
    private final JDA jda = Discord.get_JDA();

    public EmbedBuilder create_embed(int x, int y, int z, String breaking_team, String broken_team){

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(147,112,219));
        eb.setTitle("Team Beacon Broken");
        eb.addField("", "Team Name - " + broken_team
                + "\nBreaking Team - " + breaking_team
                + "\nBeacon Location - " + x + ", " + y + ", " + z +
                "\n\nThis team beacon has been broken! \n" +
                "They have one life remaining.\n" +
                "Go eliminate them or scavenge any remaining loot!", false);
        eb.setThumbnail("https://minotar.net/avatar/" + breaking_team);
        eb.setFooter("Bot developed by B77 for use of Archaic Network");
        return eb;

    }
    public void send_embed(EmbedBuilder eb) throws InterruptedException {
        TextChannel txtChannel = jda.awaitReady().getTextChannelById("1166864842653704232");
        txtChannel.sendMessageEmbeds(eb.build()).queue();
    }
}
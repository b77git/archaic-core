package me.b77.archaiccore;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Discord {
    private static JDA jda;

    public static void main(String[] args){
        start_bot();

    }
    public static void start_bot() {
        String token = "MTEzMjgzMjQwMjM5NDMzMzI5Ng.GQxn59.zM3oH6B6rbI2t0E_JmDgzXei9ZeDqnCROaq9eY";
        jda = JDABuilder.createDefault(token).build();
        Discord bot = new Discord();
    }

    public static JDA get_JDA(){
        return jda;
    }
}

package me.b77.archaiccore.database;

import java.io.File;
import java.sql.*;

public class PlayerDatabase {
    Database database = new Database();

    public static void createNewTable() {
        File database_file = new File("jdbc:sqlite:" + me.b77.archaiccore.ArchaicCore.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "archaiccore.db");


        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS players (\n"
                + "	username TEXT NOT NULL UNIQUE,\n"
                + "	uuid text NOT NULL UNIQUE, \n"
                + "	teamid INTEGER NOT NULL DEFAULT 0,\n"
                + "	teamleader INTEGER NOT NULL DEFAULT 0,\n"
                + " lastlife INTEGER NOT NULL DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(String.valueOf(database_file));
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isLeader(String playerName) {
        String sql = "SELECT leaderstatus FROM players WHERE username = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int leaderStatus = rs.getInt("leaderstatus");
                return leaderStatus == 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void addPlayer(String username, String uuid, int teamid, int teamleader) {
        String sql = "INSERT INTO players(username,uuid,teamid,teamleader) VALUES(?,?,?,?)";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, uuid);
            pstmt.setInt(3, teamid);
            pstmt.setInt(4, teamleader);
            pstmt.executeUpdate();
            System.out.println("Player " + username + " with the uuid" + uuid + " has been added");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setLeaderStatus(String username, int leader){

        String sqlUpdate = "UPDATE players "
                + "SET teamleader = ? "
                + "WHERE username = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, leader);
            pstmt.setString(2, username);


            pstmt.executeUpdate();
            System.out.println("Player " + username + " has been updated " + leader);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setTeamId(String username, int teamid){
        String sqlUpdate = "UPDATE players "
                + "SET teamid = ? "
                + "WHERE username = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, teamid);
            pstmt.setString(2, username);


            pstmt.executeUpdate();
            System.out.println("Player " + username + " has been updated to team " + teamid);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getTeamId(String username) {
        String sql = "SELECT teamid FROM players WHERE username='" + username + "'" ;


        int teamid = 0;

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
                teamid = rs.getInt(1);
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(teamid);
        return teamid;
    }

    public void setlastlife(String username){
        String sqlUpdate = "UPDATE players "
                + "SET lastlife = ? "
                + "WHERE username = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, 1);
            pstmt.setString(2, username);


            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getlastlife(String username) {
        String sql = "SELECT lastlife FROM players WHERE username='" + username + "'" ;


        int lastlife = 0;

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
                lastlife = rs.getInt(1);
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(lastlife);
        return lastlife;
    }


    public boolean player_exists(String username) {
        String sql = "SELECT username FROM players WHERE username = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;


        }
    }

    public void leave_team(String username){
        PlayerDatabase players = new PlayerDatabase();
        TeamDatabase teams = new TeamDatabase();
        int teamid = players.getTeamId(username);
        teams.setMemberCount(teamid, teams.getTeamCount(teamid) - 1);
        players.setTeamId(username, 0);
        players.setLeaderStatus(username, 0);

    }

    public void join_team(String username, int teamid){
        PlayerDatabase players = new PlayerDatabase();
        TeamDatabase teams = new TeamDatabase();

        teams.setMemberCount(teamid, teams.getTeamCount(teamid) + 1);
        players.setTeamId(username, teamid);
        players.setLeaderStatus(username, teamid);
    }
}

package me.b77.archaiccore.database;

import java.io.File;
import java.sql.*;

public class TeamDatabase {
    private final Database database = new Database();

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS teams (\n"
                + "	teamname TEXT NOT NULL UNIQUE,\n"
                + "	membercount INTEGER NOT NULL DEFAULT 1,\n"
                + "	teamleader TEXT NOT NULL,\n"
                + "	teamid INTEGER NOT NULL UNIQUE,\n"
                + "	beaconplaced INTEGER NOT NULL DEFAULT 0,\n"
                + "	beaconbroken INTEGER NOT NULL DEFAULT 0, \n"
                + "	movecount INTEGER NOT NULL DEFAULT 0 \n"
                + ");";

        try (Connection conn = DriverManager.getConnection(getDatabaseFile());
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setMemberCount(int teamid, int count){
        String sqlUpdate = "UPDATE teams SET membercount = ? WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, count);
            pstmt.setInt(2, teamid);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getTeamCount(int teamid){
        String sql = "SELECT membercount FROM teams WHERE teamid='" + teamid + "'" ;
        return executeIntQuery(sql);
    }

    public String getTeamleader(int teamid){
        String sql = "SELECT teamleader FROM teams WHERE teamid='" + teamid + "'" ;
        return executeStringQuery(sql);
    }

    public String getTeamname(int teamid){
        String sql = "SELECT teamname FROM teams WHERE teamid='" + teamid + "'" ;
        return executeStringQuery(sql);
    }

    public int get_team_id(String teamname){
        String sql = "SELECT teamid FROM teams WHERE teamname='" + teamname + "'" ;
        return executeIntQuery(sql);
    }

    public void addTeam(String teamname, String teamleader, int teamid) {
        String sql = "INSERT INTO teams(teamname,teamleader,teamid) VALUES(?,?,?)";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teamname);
            pstmt.setString(2, teamleader);
            pstmt.setInt(3, teamid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getCount(){
        String sql = "SELECT Count(*) FROM teams";
        return executeIntQuery(sql);
    }

    public void set_Broken(int teamid, int broken){
        String sqlUpdate = "UPDATE teams SET beaconbroken = ? WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, broken);
            pstmt.setInt(2, teamid);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int get_Broken(int teamid){
        String sql = "SELECT beaconbroken FROM teams WHERE teamid='" + teamid + "'" ;
        return executeIntQuery(sql);
    }

    public int get_placed(int teamid) {
        String sql = "SELECT beaconplaced FROM teams WHERE teamid='" + teamid + "'" ;
        return executeIntQuery(sql);
    }

    public void set_placed(int teamid, int value) {
        String sqlUpdate = "UPDATE teams SET beaconplaced = ? WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, value);
            pstmt.setInt(2, teamid);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getMoveCount(int teamId){
        String sql = "SELECT movecount FROM teams WHERE teamid='" + teamId + "'" ;

        int moveCount = 0;

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
                moveCount = rs.getInt(1);
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return moveCount;
    }

    public void incrementMoveCount(int teamId){
        String sqlUpdate = "UPDATE teams "
                + "SET movecount = movecount + 1 "
                + "WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, teamId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean teamExists(String teamName) {
        String sql = "SELECT COUNT(*) FROM teams WHERE teamname = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teamName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private String executeStringQuery(String sql) {
        String result = "";

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if(rs.next()) {
                result = rs.getString(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private int executeIntQuery(String sql) {
        int result = 0;

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if(rs.next()) {
                result = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static String getDatabaseFile() {
        return "jdbc:sqlite:" + me.b77.archaiccore.ArchaicCore.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "archaiccore.db";
    }
}

package me.b77.archaiccore.database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class BeaconDatabase {
    Database database = new Database();

    public static void createNewTable() {
        File databaseFile = new File("jdbc:sqlite:" + me.b77.archaiccore.ArchaicCore.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "archaiccore.db");

        String sql = "CREATE TABLE IF NOT EXISTS beacons (\n"
                + "	teamid INTEGER NOT NULL DEFAULT 0,\n"
                + "	x INTEGER NOT NULL DEFAULT 0,\n"
                + "	y INTEGER NOT NULL DEFAULT 0,\n"
                + "	z INTEGER NOT NULL DEFAULT 0\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(String.valueOf(databaseFile));
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBeacon(int x, int y, int z, int teamid) {
        String sql = "INSERT INTO beacons(x,y,z,teamid) VALUES(?,?,?,?)";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, x);
            pstmt.setInt(2, y);
            pstmt.setInt(3, z);
            pstmt.setInt(4, teamid);
            pstmt.executeUpdate();
            System.out.println("Added team " + teamid + " beacon to the list!" );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean beaconExists(int teamid) {
        String sql = "SELECT teamid FROM beacons WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamid);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public ArrayList<Integer> getCoords(int teamid) {
        String sql = "SELECT x,y,z FROM beacons WHERE teamid='" + teamid + "'" ;

        ArrayList<Integer> coords = new ArrayList<>(3);

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()){
                coords.add(rs.getInt(1));
                coords.add(rs.getInt(2));
                coords.add(rs.getInt(3));
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return coords;
    }

    public void setTeamId(int teamId, int value) {
        String sqlUpdate = "UPDATE beacons SET teamid = ? WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, value);
            pstmt.setInt(2, teamId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getTeamId(int teamId) {
        String sql = "SELECT x, y, z FROM beacons WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");

                if (x != 0 && y != 0 && z != 0) {
                    return teamId;
                }
            }

            return -999;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -999;
        }
    }



    public ArrayList<Integer> getTeams(){
        String sql = "SELECT teamid FROM beacons";

        ArrayList<Integer> teams = new ArrayList<>();

        try (Connection conn = database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                teams.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return teams;
    }

    public Boolean compareCoords(int teamid, int x, int y, int z){
        ArrayList<Integer> coords = new ArrayList<>(3);
        coords.add(x);
        coords.add(y);
        coords.add(z);

        return coords.equals(getCoords(teamid));
    }

    public void breakBeacon(int teamId) {
        String sqlUpdate = "UPDATE teams SET beaconplaced = 0 WHERE teamid = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {

            pstmt.setInt(1, teamId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

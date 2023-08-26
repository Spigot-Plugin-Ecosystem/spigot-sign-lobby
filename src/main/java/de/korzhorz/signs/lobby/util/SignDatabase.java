package de.korzhorz.signs.lobby.util;

import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.handlers.DatabaseHandler;
import de.korzhorz.signs.lobby.handlers.MySQLHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SignDatabase extends DatabaseHandler {
    @Override
    public void createTables() {
        if(!this.requireDatabaseConnection()) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS `Signs_ServerInformation` (";
        sql += "`serverName` VARCHAR(63) NOT NULL PRIMARY KEY,";
        sql += "`serverMotd` VARCHAR(255) NOT NULL DEFAULT \"\",";
        sql += "`serverMaxPlayers` INT(11) NOT NULL DEFAULT 0,";
        sql += "`serverOnlinePlayers` INT(11) NOT NULL DEFAULT 0,";
        sql += "`online` TINYINT(1) NOT NULL DEFAULT 0,";
        sql += "`maintenance` TINYINT(1) NOT NULL DEFAULT 0";
        sql += ");";

        try(PreparedStatement preparedStatement = MySQLHandler.getConnection().prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ServerData> getServerData() {
        if(!this.requireDatabaseConnection()) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM `Signs_ServerInformation`;";

        try(PreparedStatement preparedStatement = MySQLHandler.getConnection().prepareStatement(sql)) {
            ResultSet result = preparedStatement.executeQuery();

            List<ServerData> serverDataResult = new ArrayList<>();

            while(result.next()) {
                String serverName = result.getString("serverName");
                String serverMotd = result.getString("serverMotd");
                int serverMaxPlayers = result.getInt("serverMaxPlayers");
                int serverOnlinePlayers = result.getInt("serverOnlinePlayers");
                boolean online = result.getBoolean("online");
                boolean maintenance = result.getBoolean("maintenance");

                ServerData serverData = new ServerData(serverName, serverMotd, serverMaxPlayers, serverOnlinePlayers, online, maintenance);
                serverDataResult.add(serverData);
            }

            return serverDataResult;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public ServerData getServerData(String serverName) {
        if(!this.requireDatabaseConnection()) {
            return null;
        }

        String sql = "SELECT * FROM `Signs_ServerInformation` WHERE `serverName` = ?;";

        try(PreparedStatement preparedStatement = MySQLHandler.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, serverName);

            ResultSet result = preparedStatement.executeQuery();

            if(result.next()) {
                String serverMotd = result.getString("serverMotd");
                int serverMaxPlayers = result.getInt("serverMaxPlayers");
                int serverOnlinePlayers = result.getInt("serverOnlinePlayers");
                boolean online = result.getBoolean("online");
                boolean maintenance = result.getBoolean("maintenance");

                return new ServerData(serverName, serverMotd, serverMaxPlayers, serverOnlinePlayers, online, maintenance);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

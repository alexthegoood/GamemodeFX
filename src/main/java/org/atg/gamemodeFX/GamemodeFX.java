package org.atg.gamemodeFX;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public final class GamemodeFX extends JavaPlugin implements Listener {

    ArrayList<Player> spectatorPlayers = new ArrayList<>();
    ArrayList<Player> onlinePlayers = new ArrayList<>();

    private final Logger log = getLogger();
    private final JavaPlugin plugin = this;

    @Override
    public void onEnable() {
        log.info("Plugin is enabled");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        log.info("Plugin is disabled");
    }

    private void joinSpectators(Player player){
        spectatorPlayers.add(player);
        onlinePlayers.remove(player);
        for(Player players : onlinePlayers){
            players.hidePlayer(plugin, player);
        }
        for(Player spectators : spectatorPlayers){
            player.showPlayer(plugin, spectators);
        }
    }

    private void quitSpectators(Player player){
        spectatorPlayers.remove(player);
        onlinePlayers.add(player);
        for(Player spectators : spectatorPlayers){
            player.hidePlayer(plugin, spectators);
        }
        for(Player players : onlinePlayers){
            players.showPlayer(plugin, player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SPECTATOR) {
            event.joinMessage(null);
            joinSpectators(player);
        } else {
            quitSpectators(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SPECTATOR) event.quitMessage(null);
        spectatorPlayers.remove(player);
        onlinePlayers.remove(player);
    }

    @EventHandler
    public void onGamemodeSwitch(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();
        if(event.getNewGameMode() == GameMode.SPECTATOR) joinSpectators(player);
        else if(player.getGameMode() == GameMode.SPECTATOR) quitSpectators(player);
    }
}

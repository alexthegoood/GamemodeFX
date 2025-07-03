package org.atg.gamemodeFX;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
        for(Player players : onlinePlayers) players.hidePlayer(plugin, player);
        for(Player spectators : spectatorPlayers) player.showPlayer(plugin, spectators);
    }

    private void quitSpectators(Player player){
        spectatorPlayers.remove(player);
        onlinePlayers.add(player);
        for(Player spectators : spectatorPlayers) player.hidePlayer(plugin, spectators);
        for(Player players : onlinePlayers) players.showPlayer(plugin, player);
    }

    private void playGamemodeChange(Player player, Particle particle, int count, float ofsX, float ofsY, float ofsZ, float extra, Sound sound, float pitch){
        // ctrl+c && ctrl+v from JQParticle plugin
        Location playerLocation = player.getLocation();

        float playerScale = (float) player.getAttribute(Attribute.SCALE).getValue();
        float playerHeight = 1.7671875f;

        float playerCenterY = playerHeight*playerScale;
        int occupiedBlocksHeightY = (int) Math.ceil(playerCenterY);

        Location centerY = playerLocation.clone().add(0.0, playerCenterY/1.85, 0.0);
        World world = player.getWorld();

        world.spawnParticle(Particle.FLASH, centerY, 1, 0.0, 0.0, 0.0, 0.0);
        world.spawnParticle(particle, centerY, count*occupiedBlocksHeightY, ofsX*playerScale, ofsY*playerScale, ofsZ*playerScale, extra*playerScale);
        world.playSound(centerY, sound, playerScale/2, pitch);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.SPECTATOR){
            event.joinMessage(null);
            joinSpectators(player);
        }else{
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
        if(event.getNewGameMode() == GameMode.SPECTATOR){
            joinSpectators(player);
            playGamemodeChange(
                    player,
                    Particle.OMINOUS_SPAWNING,
                    15,
                    0.075f,
                    0.25f,
                    0.075f,
                    0.35f,
                    Sound.BLOCK_TRIAL_SPAWNER_SPAWN_ITEM_BEGIN,
                    0.0f
            );
        } else if(player.getGameMode() == GameMode.SPECTATOR){
            quitSpectators(player);
            playGamemodeChange(
                    event.getPlayer(),
                    Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,
                    20,
                    0.35f,
                    0.45f,
                    0.35f,
                    0.0f,
                    Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB,
                    0.7f
            );
        }
    }
}

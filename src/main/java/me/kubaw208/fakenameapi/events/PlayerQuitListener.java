package me.kubaw208.fakenameapi.events;

import me.kubaw208.fakenameapi.FakeNameAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final FakeNameAPI fakeNameAPI;

    public PlayerQuitListener(FakeNameAPI fakeNameAPI) {
        this.fakeNameAPI = fakeNameAPI;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        fakeNameAPI.getFakeNames().remove(player);

        for(var fakeNameData : fakeNameAPI.getFakeNames().values()) {
            if(!fakeNameData.receivers().contains(player)) continue;

            fakeNameData.receivers().remove(player);
        }
    }

}
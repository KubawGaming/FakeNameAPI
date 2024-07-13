package me.kubaw208.fakenameapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import lombok.Getter;
import me.kubaw208.fakenameapi.events.PlayerInfoUpdateListener;
import me.kubaw208.fakenameapi.events.PlayerQuitListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FakeNameAPI {

    private final JavaPlugin plugin;
    private boolean isRegistered = false;
    @Getter private final HashMap<UUID, FakeNameData> fakeNames = new HashMap<>();

    public FakeNameAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public record FakeNameData(String fakeName, Collection<Player> receivers) {}

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, Player... packetReceivers) {
        var packet = new WrapperPlayServerPlayerInfoUpdate(
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                        new UserProfile(playerToChange.getUniqueId(), playerToChange.getName()),
                        true,
                        playerToChange.getPing(),
                        null,
                        Component.text(newFakeName),
                        null
                ));

        if(isRegistered)
            fakeNames.put(playerToChange.getUniqueId(), new FakeNameData(newFakeName, List.of(packetReceivers)));

        for(Player receiver : packetReceivers) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, packet);
        }
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, Collection<? extends Player> packetReceivers) {
        setFakeName(playerToChange, newFakeName, packetReceivers.toArray(new Player[0]));
    }

    /**
     * Sends packet with fake name for given player to given packet receivers.
     * @param playerToChange player that nick will be changed
     * @param newFakeName new fake name for `playerToChange`
     * @param packetReceivers players that will see new fake name for `playerToChange`
     */
    public void setFakeName(Player playerToChange, String newFakeName, List<Player> packetReceivers) {
        setFakeName(playerToChange, newFakeName, packetReceivers.toArray(new Player[0]));
    }

    /**
     * Resets fake name to default player name for all players on the server.
     * @param playerToChange player that nick will be reset
     */
    public void resetName(Player playerToChange) {
        setFakeName(playerToChange, playerToChange.getName(), Bukkit.getOnlinePlayers());
        fakeNames.remove(playerToChange.getUniqueId());
    }

    /**
     * Register API events that keep fake nicks set up
     */
    public void registerEvents() {
        if(isRegistered) return;

        isRegistered = true;
        PacketEvents.getAPI().getEventManager().registerListener(new PlayerInfoUpdateListener(this), PacketListenerPriority.HIGH);
        plugin.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), plugin);
    }

}
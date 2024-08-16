package me.kubaw208.fakenameapi.events;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import me.kubaw208.fakenameapi.FakeNameAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PlayerInfoUpdateListener implements PacketListener {

    private final FakeNameAPI fakeNameAPI;

    public PlayerInfoUpdateListener(FakeNameAPI fakeNameAPI) {
        this.fakeNameAPI = fakeNameAPI;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(event.getPacketType() != PacketType.Play.Server.PLAYER_INFO_UPDATE) return;
        if(!(event.getPlayer() instanceof Player player)) return;

        var packet = new WrapperPlayServerPlayerInfoUpdate(event);

        for(var entry : packet.getEntries()) {
            if(!fakeNameAPI.getFakeNames().containsKey(entry.getGameProfile().getUUID())) continue;
            if(fakeNameAPI.getFakeNames().get(entry.getGameProfile().getUUID()).get(player.getUniqueId()) == null) continue;

            entry.getGameProfile().setName(fakeNameAPI.getFakeNames().get(entry.getGameProfile().getUUID()).get(player.getUniqueId()));
            entry.setDisplayName(Component.text(fakeNameAPI.getFakeNames().get(entry.getGameProfile().getUUID()).get(player.getUniqueId())));
        }
    }

}
package de.korzhorz.signs.lobby.listeners.pluginchannels;

import com.google.common.io.ByteArrayDataInput;
import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.util.SignUtil;
import de.korzhorz.signs.lobby.util.bungeecord.PluginChannelEvent;

public class PC_SignsUpdate implements PluginChannelEvent {
    @Override
    public String getHandledSubChannel() {
        return "signs:update";
    }

    @Override
    public void handle(ByteArrayDataInput byteArrayDataInput) {
        String serverName = byteArrayDataInput.readUTF();

        // Retrieve changed server data and update the signs
        ServerData serverData = ServerData.getUpdatedServerData(serverName);
        SignUtil.updateSigns(serverName, serverData);
    }
}

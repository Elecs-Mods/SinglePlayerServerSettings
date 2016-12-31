package elec332.singleplayerserversettings;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Elec332 on 19-7-2016.
 */
public enum SinglePlayerServerSettingsConfig {

    INSTANCE;

    private SinglePlayerServerSettingsConfig(){
        this.configuration = new Configuration(new File(SinglePlayerServerSettingsCore.getConfigFolder(), SinglePlayerServerSettingsModContainer.MODNAME + ".cfg"));
        this.onlineMode = false;
    }

    private final Configuration configuration;
    private static final String onlineMode_desc, port_desc, motd_desc, allowPvp_desc;
    private static final int VANILLA_DEFAULT_PORT;
    private boolean onlineMode, allowPvp;
    private int port;
    private String motd;

    public boolean loadConfig(){
        this.configuration.load();
        this.onlineMode = this.configuration.getBoolean("Online-Mode", Configuration.CATEGORY_CLIENT, onlineMode, onlineMode_desc);
        this.allowPvp = this.configuration.getBoolean("Allow-PvP", Configuration.CATEGORY_CLIENT, allowPvp, allowPvp_desc);
        int oldPort = this.port;
        this.port = this.configuration.getInt("Port", Configuration.CATEGORY_CLIENT, VANILLA_DEFAULT_PORT, -1, 65535, port_desc);
        this.motd = this.configuration.getString("MOTD", Configuration.CATEGORY_CLIENT, "%p - %w", motd_desc);
        if (this.configuration.hasChanged()){
            this.configuration.save();
        }
        return this.port != oldPort;
    }

    public boolean onlineMode(){
        return onlineMode;
    }

    public boolean allowPvp(){
        return allowPvp;
    }

    public int getPort(){
        if (port < 0 || port > 65535){
            try {
                return vanillaRandomPort();
            } catch (Exception e){
                return -1;
            }
        }
        return port;
    }

    public String getServerMOTD(MinecraftServer server){
        return motd.replace("%p", server.getServerOwner()).replace("%w", server.getEntityWorld().getWorldInfo().getWorldName());
    }

    private int vanillaRandomPort() throws IOException {
        ServerSocket serversocket = null;
        int i = -1;
        try {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        } finally {
            try {
                if (serversocket != null) {
                    serversocket.close();
                }
            } catch (IOException var8) {
                //;
            }
        }
        return i;
    }

    static {
        VANILLA_DEFAULT_PORT = 25565;
        onlineMode_desc = "When set to false, the LAN world will also allow connections from offline clients. This allows you to LAN a world without an active internet connection.";
        allowPvp_desc = "Controls the PvP on the LAN worlds.";
        port_desc = "Allows you to set a port your LAN world will always use, set to -1 to use vanilla's random port assignment. When \""+VANILLA_DEFAULT_PORT+"\" is used, the players do not need to enter a port (MC-default).";
        motd_desc = "The MOTD of the LAN server, %p = player %w = world name";
    }

}

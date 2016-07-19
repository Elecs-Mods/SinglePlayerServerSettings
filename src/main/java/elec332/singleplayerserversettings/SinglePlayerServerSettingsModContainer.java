package elec332.singleplayerserversettings;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Created by Elec332 on 18-7-2016.
 */
public class SinglePlayerServerSettingsModContainer extends DummyModContainer {

    public SinglePlayerServerSettingsModContainer(){
        super(new ModMetadata());
        ModMetadata md = getMetadata();
        md.authorList = Lists.newArrayList("Elec332");
        md.description = "Allows for playing on LAN worlds without an active internet connection.";
        md.modId = "sss";
        md.name = MODNAME;
    }

    protected static final String MODNAME = "SinglePlayerServerSettings";

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void onServerStarting(FMLServerStartingEvent event){
        MinecraftServer server = event.getServer();
        SinglePlayerServerSettingsConfig.INSTANCE.loadConfig();
        setServerSettings(server);
        ClientCommandHandler.instance.registerCommand(new CommandBase() {

            @Override
            public String getCommandName() {
                return "SSSReload";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 0;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "SSSReload";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                boolean b = SinglePlayerServerSettingsConfig.INSTANCE.loadConfig();
                setServerSettings(server);
                sendModMessage(sender, "Config reloaded.");
                if (b){
                    sendModMessage(sender, "The new port will be used when the world is reloaded.");
                }
            }

        });
    }

    private void sendModMessage(ICommandSender sender, String message){
        sender.addChatMessage(new TextComponentString("["+MODNAME+"] "+message));
    }

    private void setServerSettings(MinecraftServer server){
        server.setOnlineMode(SinglePlayerServerSettingsConfig.INSTANCE.onlineMode());
        server.setMOTD(SinglePlayerServerSettingsConfig.INSTANCE.getServerMOTD(server));
    }

}

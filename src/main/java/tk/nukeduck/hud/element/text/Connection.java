package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingBoolean;

public class Connection extends TextElement {
	private SettingBoolean playerCount, showIp, latency;

	private String ip = "localServer";

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		playerCount.set(true);
		showIp.set(true);
		latency.set(true);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public Connection() {
		super("connection");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(playerCount = new SettingBoolean("playerCount").setValuePrefix(SettingBoolean.VISIBLE));
		settings.add(showIp = new SettingBoolean("showIp"));
		settings.add(latency = new SettingBoolean("latency"));
	}

	@SubscribeEvent
	public void onConnect(ClientConnectedToServerEvent event) {
		if(!event.isLocal()) {
			ip = event.getManager().getRemoteAddress().toString();
		} else {
			ip = "localServer";
		}
	}

	@Override
	protected List<String> getText() {
		List<String> toRender = new ArrayList<String>(3);

		if(playerCount.get()) {
			int players = MC.getConnection().getPlayerInfoMap().size();
			String conn = I18n.format(players != 1 ? "betterHud.hud.players" : "betterHud.hud.player", players);
			toRender.add(conn);
		}

		if(showIp.get()) {
			toRender.add(I18n.format(ip.equals("localServer") ? "betterHud.hud.localServer" : "betterHud.hud.ip", ip));
		}

		if(latency.get() && MC.getCurrentServerData() != null) {
			NetworkPlayerInfo info = MC.getConnection().getPlayerInfo(MC.player.getUniqueID());

			if(info != null) {
				int ping = info.getResponseTime();
				toRender.add(I18n.format("betterHud.hud.ping", ping));
			}
		}
		return toRender;
	}
}

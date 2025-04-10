package dev.theagameplayer.afktimer.event;

import org.apache.logging.log4j.Logger;

import dev.theagameplayer.afktimer.AFKTimerMod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;

public final class AFKServerEvents {
	private static final Logger LOGGER = AFKTimerMod.LOGGER;
	private static MinecraftServer server;
	public static boolean serverActive;
	public static int serverTime;
	public static int serverTick;
	
	public static final void serverStarted(final ServerStartedEvent eventIn) {
		server = eventIn.getServer();
	}
	
	public static final void serverTick(final TickEvent.ServerTickEvent eventIn) {
		if (server != null && eventIn.phase == TickEvent.Phase.END && serverActive) {
			serverTick++;
			if (serverTick > serverTime) {
				serverActive = false;
				serverTime = 0;
				serverTick = 0;
				server.halt(false);
				LOGGER.info("Server Timer ended!");
			}
		}
	}
}

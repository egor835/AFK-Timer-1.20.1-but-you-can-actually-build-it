package dev.theagameplayer.afktimer.event;

import org.apache.logging.log4j.Logger;

import com.mojang.realmsclient.RealmsMainScreen;

import dev.theagameplayer.afktimer.AFKTimerMod;
import dev.theagameplayer.afktimer.client.commands.AFKClientCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;

public final class AFKClientEvents {
	private static final Logger LOGGER = AFKTimerMod.LOGGER;
	public static boolean clientQuitGame;
	public static boolean clientActive;
	public static int clientTime;
	public static int clientTick;
	
	public static final void loggingOut(final ClientPlayerNetworkEvent.LoggingOut eventIn) {
		clientActive = false;
		clientTime = 0;
		clientTick = 0;
	}
	
	public static final void registerClientCommands(final RegisterClientCommandsEvent eventIn) {
		AFKClientCommands.build(eventIn.getDispatcher());
	}

	public static final void clientTick(final TickEvent.ClientTickEvent eventIn) {
		if (eventIn.phase == TickEvent.Phase.END && clientActive) {
			clientTick++;
			if (clientTick > clientTime) {;
				clientActive = false;
				clientTime = 0;
				clientTick = 0;
				disconnectClient(Minecraft.getInstance());
				LOGGER.info("Client Timer ended!");
			}
		}
	}

	private static final void disconnectClient(final Minecraft mcIn) {
		final boolean local = mcIn.isLocalServer();
		final boolean realms = mcIn.isConnectedToRealms();
		mcIn.level.disconnect();
		if (local) {
			mcIn.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
		} else {
			mcIn.clearLevel();
		}
		if (clientQuitGame) {
			mcIn.stop();
		} else {
			final TitleScreen titleScreen = new TitleScreen();
			if (local) {
				mcIn.setScreen(titleScreen);
			} else if (realms) {
				mcIn.setScreen(new RealmsMainScreen(titleScreen));
			} else {
				mcIn.setScreen(new JoinMultiplayerScreen(titleScreen));
			}
		}
	}
}

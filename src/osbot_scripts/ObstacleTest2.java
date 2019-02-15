package osbot_scripts;

import java.awt.Graphics2D;
import java.io.IOException;

import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import osbot_scripts.bot.utils.BotCommands;
import osbot_scripts.bot.utils.Coordinates;
import osbot_scripts.config.Config;
import osbot_scripts.database.DatabaseUtilities;
import osbot_scripts.events.LoginEvent;
import osbot_scripts.events.MandatoryEventsExecution;
import osbot_scripts.login.LoginHandler;
import osbot_scripts.qp7.progress.ObstacleTest3;
import osbot_scripts.qp7.progress.TradeBeforeBanWaves;
import osbot_scripts.scripttypes.types.MiningType;

@ScriptManifest(author = "pim97", info = "OBSTACLE_TEST_2", logo = "", name = "OBSTACLE_TEST_2", version = 1.0)
public class ObstacleTest2 extends Script {

	private ObstacleTest3 test;

	private LoginEvent login;

	private MandatoryEventsExecution ev = new MandatoryEventsExecution(this, login);

	@Override
	public int onLoop() throws InterruptedException {
		try {
			if (!test.isLoggedIn()) {
				return random(1000, 2000);
			}

			if (getDialogues().isPendingContinuation()) {
				getDialogues().clickContinue();
			}

			if (getGoldfarmMining().isLoggedIn()) {
				ev.fixedMode();
				ev.fixedMode2();
				ev.executeAllEvents();
			}

			if (Coordinates.isOnTutorialIsland(this)) {
				DatabaseUtilities.updateStageProgress(this, "TUT_ISLAND", 0, login.getUsername(), login);
				BotCommands.killProcess((MethodProvider) this, (Script) this, "SHOULD BE ON TUT ISLAND MINING 15",
						login);
			}


			getGoldfarmMining().onLoop();
			
			return random(1000, 2000);

		} catch (Exception e) {
			log(DatabaseUtilities.exceptionToString(e, this, login));
		}
		return random(1000, 2000);
	}

	// Breaking for set amount of minutes because has done a few laps
	// if (getGoldfarmMining().getDoneLaps() > 15) {
	// log("Taking a break...");
	// Thread.sleep(5000);
	// DatabaseUtilities.updateAccountBreakTill(this,
	// getGoldfarmMining().getEvent().getUsername(), 30);
	// BotCommands.killProcess((MethodProvider)this, (Script) this);
	// }
	// return random(20, 80);

	@Override
	public void onPaint(Graphics2D g) {
		getGoldfarmMining().onPaint(g);
	}

	@Override
	public void onStart() throws InterruptedException {
		try {
			if (!Config.NO_LOGIN) {
				login = LoginHandler.login(this, getParameters());
				login.setScript("MINING_RIMMINGTON_CLAY");
				// DatabaseUtilities.updateLoginStatus(this, login.getUsername(), "LOGGED_IN",
				// login);
			}
			test = new ObstacleTest3(login, (Script) this);
			test.setScriptAbstract(new MiningType());
			getGoldfarmMining().setQuest(false);

			if (!Config.NO_LOGIN) {
				if (login != null && login.getUsername() != null) {
					// int guessedTaskNumber =
					// getGoldfarmMining().getTaskHandler().getTaskNumberOnCurrentLocation();
					// if (guessedTaskNumber > 0) {
					// getGoldfarmMining().setQuestStageStep(guessedTaskNumber);
					// } else {
					// getGoldfarmMining().setQuestStageStep(0);
					// }
					getGoldfarmMining().setQuestStageStep(0);

					// Integer.parseInt(DatabaseUtilities.getQuestProgress(this,
					// login.getUsername(), login)

					// Integer.parseInt(DatabaseUtilities.getQuestProgress(this,
					// login.getUsername())));

//					DatabaseUtilities.updateStageProgress(this, "MINING_RIMMINGTON_CLAY", 0,
//							getGoldfarmMining().getEvent().getUsername(), login);
				}
			}

			getGoldfarmMining().exchangeContext(getBot());
			getGoldfarmMining().onStart();

			TradeBeforeBanWaves.trade2(getGoldfarmMining());
			// TradeBeforeBanWaves.trade1(getGoldfarmMining());
		} catch (Exception e) {
			log(DatabaseUtilities.exceptionToString(e, this, login));
		}
	}

	/**
	 * @return the goldfarmMining
	 */
	public ObstacleTest3 getGoldfarmMining() {
		return test;
	}

	/**
	 * @param goldfarmMining
	 *            the goldfarmMining to set
	 */
	public void setGoldfarmMining(ObstacleTest3 goldfarmMining) {
		this.test = goldfarmMining;
	}

}
package osbot_scripts;

import java.awt.Graphics2D;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import osbot_scripts.bot.utils.BotCommands;
import osbot_scripts.bot.utils.Coordinates;
import osbot_scripts.bot.utils.RandomUtil;
import osbot_scripts.config.Config;
import osbot_scripts.database.DatabaseUtilities;
import osbot_scripts.events.LoginEvent;
import osbot_scripts.events.MandatoryEventsExecution;
import osbot_scripts.login.LoginHandler;
import osbot_scripts.qp7.progress.MiningLevelTo15Configuration;

@ScriptManifest(author = "pim97", info = "MINING_LEVEL_TO_15", logo = "", name = "MINING_LEVEL_TO_15", version = 1.0)
public class MiningLevelTo15 extends Script {

	private MiningLevelTo15Configuration goldfarmMining;

	private LoginEvent login;

	private MandatoryEventsExecution ev = new MandatoryEventsExecution(this, login);

	@Override
	public int onLoop() throws InterruptedException {

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
			BotCommands.killProcess((MethodProvider) this, (Script) this, "SHOULD BE ON TUT ISLAND MINING 15", login);
		}

		// Account must have atleast 7 quest points, otherwise set it back to quesiton
		if (getQuests().getQuestPoints() < 7) {
			DatabaseUtilities.updateStageProgress(this, RandomUtil.gextNextAccountStage(this).name(), 0,
					login.getUsername(), login);
			BotCommands.killProcess((MethodProvider) this, (Script) this, "LESS THAN 7 QP MINING 15", login);
		}

		// If mining is equals or bigger than 15, then it can proceed to mining iron
		if (getSkills().getStatic(Skill.MINING) >= 31) {
			Thread.sleep(5000);
			DatabaseUtilities.updateStageProgress(this, RandomUtil.gextNextAccountStage(this).name(), 0,
					login.getUsername(), login);
			BotCommands.killProcess((MethodProvider) this, (Script) this, "HIGHER THAN 15 MINING MINING 15", login);
		}

		log("G.E. task: "
				+ (getGoldfarmMining().getGrandExchangeTask() != null ? getGoldfarmMining().getGrandExchangeTask()
						: "NULL"));

		if (login.hasFinished()) {
			if (getGoldfarmMining().getGrandExchangeTask() == null) {
				getGoldfarmMining().getTaskHandler().taskLoop();
			} else {
				getGoldfarmMining().onLoop();
			}
		}

		// Breaking for set amount of minutes because has done a few laps
		// if (getGoldfarmMining().getDoneLaps() > 15) {
		// log("Taking a break...");
		// Thread.sleep(5000);
		// DatabaseUtilities.updateAccountBreakTill(this,
		// getGoldfarmMining().getEvent().getUsername(), 30);
		// BotCommands.killProcess((MethodProvider)this, (Script) this);
		// }
		return random(20, 80);
	}

	@Override
	public void onPaint(Graphics2D g) {
		getGoldfarmMining().onPaint(g);
	}

	@Override
	public void onStart() throws InterruptedException {
		if (!Config.NO_LOGIN) {
			login = LoginHandler.login(this, getParameters());
			login.setScript("MINING_LEVEL_TO_15");
			DatabaseUtilities.updateLoginStatus(this, login.getUsername(), "LOGGED_IN", login);
		}
		goldfarmMining = new MiningLevelTo15Configuration(login, (Script) this);
		getGoldfarmMining().setQuest(false);

		if (!Config.NO_LOGIN) {
			if (login != null && login.getUsername() != null) {
				getGoldfarmMining().setQuestStageStep(0);
				// Integer.parseInt(DatabaseUtilities.getQuestProgress(this,
				// login.getUsername())));

				DatabaseUtilities.updateStageProgress(this, "MINING_LEVEL_TO_15", 0,
						getGoldfarmMining().getEvent().getUsername(), login);
			}
		}

		getGoldfarmMining().exchangeContext(getBot());
		getGoldfarmMining().onStart();

	}

	/**
	 * @return the goldfarmMining
	 */
	public MiningLevelTo15Configuration getGoldfarmMining() {
		return goldfarmMining;
	}

	/**
	 * @param goldfarmMining
	 *            the goldfarmMining to set
	 */
	public void setGoldfarmMining(MiningLevelTo15Configuration goldfarmMining) {
		this.goldfarmMining = goldfarmMining;
	}

}

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
import osbot_scripts.database.DatabaseUtilities;
import osbot_scripts.events.LoginEvent;
import osbot_scripts.events.MandatoryEventsExecution;
import osbot_scripts.login.LoginHandler;
import osbot_scripts.qp7.progress.IronMinerConfiguration;

@ScriptManifest(author = "pim97", info = "MINING_IRON_ORE", logo = "", name = "MINING_IRON_ORE", version = 1.0)
public class IronMiner extends Script {

	private IronMinerConfiguration goldfarmMining;

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
			DatabaseUtilities.updateStageProgress(this, "TUT_ISLAND", 0, login.getUsername());
			BotCommands.killProcess((MethodProvider) this, (Script) this, "SHOULD BE ON TUT ISLAND IRON MINER");
		}

		// Account must have atleast 7 quest points, otherwise set it back to quesiton
		if (getQuests().getQuestPoints() < 7) {
			DatabaseUtilities.updateStageProgress(this, RandomUtil.gextNextAccountStage(this).name(), 0,
					login.getUsername());
			BotCommands.killProcess((MethodProvider) this, (Script) this, "NOT ENOUGH QUEST POINTS IRON MINER");
		}

		// Breaking for 30 minutes because has done a few laps
		// if (getGoldfarmMining().getDoneLaps() > 10) {
		// log("Taking a break...");
		// Thread.sleep(5000);
		// DatabaseUtilities.updateAccountBreakTill(this,
		// getGoldfarmMining().getEvent().getUsername(), 30);
		// BotCommands.killProcess((MethodProvider)this, (Script) this);
		// }

		// When skilling isn't 15 yet, and thus can't mine iron
		if (getSkills().getStatic(Skill.MINING) < 15) {
			DatabaseUtilities.updateStageProgress(this, "MINING_LEVEL_TO_15", 0,
					getGoldfarmMining().getEvent().getUsername());
			BotCommands.killProcess((MethodProvider) this, (Script) this, "NOT HIGH ENOUGH LEVEL IRON MINER");
		}

		log("G.E. task: "
				+ (getGoldfarmMining().getGrandExchangeTask() != null ? getGoldfarmMining().getGrandExchangeTask()
						: "NULL"));

		if (getGoldfarmMining().getGrandExchangeTask() == null) {
			getGoldfarmMining().getTaskHandler().taskLoop();
		} else {
			getGoldfarmMining().onLoop();
		}

		return random(20, 80);
	}

	@Override
	public void onPaint(Graphics2D g) {
		getGoldfarmMining().onPaint(g);
	}

	@Override
	public void onStart() throws InterruptedException {
		login = LoginHandler.login(this, getParameters());
		login.setScript("MINING_IRON_ORE");
		DatabaseUtilities.updateLoginStatus(this, login.getUsername(), "LOGGED_IN");
		goldfarmMining = new IronMinerConfiguration(login, (Script) this);

		getGoldfarmMining().setQuest(false);
		if (login != null && login.getUsername() != null) {
			getGoldfarmMining().setQuestStageStep(0);
			// Integer.parseInt(DatabaseUtilities.getQuestProgress(this,
			// login.getUsername())));
		}

		getGoldfarmMining().exchangeContext(getBot());
		getGoldfarmMining().onStart();
		DatabaseUtilities.updateStageProgress(this, "MINING_IRON_ORE", 0, getGoldfarmMining().getEvent().getUsername());
	}

	/**
	 * @return the goldfarmMining
	 */
	public IronMinerConfiguration getGoldfarmMining() {
		return goldfarmMining;
	}

	/**
	 * @param goldfarmMining
	 *            the goldfarmMining to set
	 */
	public void setGoldfarmMining(IronMinerConfiguration goldfarmMining) {
		this.goldfarmMining = goldfarmMining;
	}

}
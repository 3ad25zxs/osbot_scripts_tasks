package osbot_scripts;

import java.awt.Graphics2D;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import com.mysql.cj.protocol.Protocol.GetProfilerEventHandlerInstanceFunction;

import osbot_scripts.database.DatabaseUtilities;
import osbot_scripts.events.DisableAudioEvent;
import osbot_scripts.events.LoginEvent;
import osbot_scripts.events.ToggleRoofsHiddenEvent;
import osbot_scripts.events.ToggleShiftDropEvent;
import osbot_scripts.framework.AccountStage;
import osbot_scripts.login.LoginHandler;
import osbot_scripts.sections.BankGuideSection;
import osbot_scripts.sections.CharacterCreationSection;
import osbot_scripts.sections.ChurchGuideSection;
import osbot_scripts.sections.CombatGuideSection;
import osbot_scripts.sections.CookingGuideSection;
import osbot_scripts.sections.GuilinorGuideSection;
import osbot_scripts.sections.MiningGuideSection;
import osbot_scripts.sections.QuestGuideSection;
import osbot_scripts.sections.SurvivalExpertSection;
import osbot_scripts.sections.TutorialSection;
import osbot_scripts.sections.WizardGuideSection;
import osbot_scripts.sections.decide.CheckInWhatArea;
import osbot_scripts.sections.total.progress.MainState;

@ScriptManifest(author = "pim97@github & dormic@osbot", info = "completes the tutorial island for you", logo = "", name = "TUT_ISLAND", version = 0)
public class TutorialScript extends Script {

	/**
	 * Auto disabled or not
	 */
	private boolean isAudioDisabled;

	/**
	 * The current mainstate
	 */
	public static MainState mainState = MainState.CREATE_CHARACTER_DESIGN;

	/**
	 * Creating a character with random outfit
	 */
	private final TutorialSection characterCreationSection = new CharacterCreationSection();

	/**
	 * Talking to the guilinor guide
	 */
	private final TutorialSection guilinorGuideSection = new GuilinorGuideSection();

	/**
	 * Survival Expert section
	 */
	private final TutorialSection survivalExpertSection = new SurvivalExpertSection();

	/**
	 * 
	 */
	private final TutorialSection cookingGuideSection = new CookingGuideSection();

	/**
	 * 
	 */
	private final TutorialSection questGuideSection = new QuestGuideSection();

	/**
	 * 
	 */
	private final TutorialSection miningGuideSection = new MiningGuideSection();

	/**
	 * 
	 */
	private final TutorialSection combatGuideSection = new CombatGuideSection();

	/**
	 * 
	 */
	private final TutorialSection bankingAreaSection = new BankGuideSection();

	/**
	 * 
	 */
	private final TutorialSection churchGuideSection = new ChurchGuideSection();

	/**
	 * 
	 */
	private final TutorialSection wizardGuideSection = new WizardGuideSection();
	
	/**
	 * Loops
	 */
	@Override
	public int onLoop() throws InterruptedException {

		if (!new Area(3030, 3148, 3170, 3040)
				.contains(myPlayer().getPosition())) {
			mainState = MainState.IN_LUMBRIDGE;
			log("Succesfully completed!");
			String username = null;
			if (getParameters() != null) {
				String[] params = getParameters().split("_"); // split the _ character!!!!!!
				username = params[0];
			}
			DatabaseUtilities.updateStageProgress(this, AccountStage.QUEST_COOK_ASSISTANT.name(), 0, username);
			stop();
			return -1;
		}

		
		if (mainState != MainState.CREATE_CHARACTER_DESIGN && mainState != MainState.TALK_TO_GIELINOR_GUIDE_ONE) {
			executeAllEvents();
		}

		log(mainState);

		if (mainState == MainState.CREATE_CHARACTER_DESIGN) {
			characterCreationSection.onLoop();
		} else if (mainState == MainState.TALK_TO_GIELINOR_GUIDE_ONE) {
			guilinorGuideSection.onLoop();
		} else if (mainState == MainState.SURVIVAL_EXPERT) {
			survivalExpertSection.onLoop();
		} else if (mainState == MainState.COOKING_GUIDE_SECTION) {
			cookingGuideSection.onLoop();
		} else if (mainState == MainState.QUEST_SECTION) {
			questGuideSection.onLoop();
		} else if (mainState == MainState.MINING_SECTION) {
			miningGuideSection.onLoop();
		} else if (mainState == MainState.COMBAT_SECTION) {
			combatGuideSection.onLoop();
		} else if (mainState == MainState.BANKING_AREA_SECTION) {
			bankingAreaSection.onLoop();
		} else if (mainState == MainState.CHURCH_GUIDE_SECTION) {
			churchGuideSection.onLoop();
		} else if (mainState == MainState.WIZARD_GUIDE_SECTION) {
			wizardGuideSection.onLoop();
		} else if (mainState == MainState.IN_LUMBRIDGE) {
			while (getClient().isLoggedIn()) {
				getLogoutTab().logOut();

				Thread.sleep(5000);
				log("Trying to logout...");
			}
			stop();
		}

		return random(600, 1200);
	}


	@Override
	public void onStart() throws InterruptedException {
		getCharacterCreationSection().exchangeContext(getBot());
		getGuilinorGuideSection().exchangeContext(getBot());
		getSurvivalExpertSection().exchangeContext(getBot());
		getCookingGuideSection().exchangeContext(getBot());
		getQuestGuideSection().exchangeContext(getBot());
		getMiningGuideSection().exchangeContext(getBot());
		getCombatGuideSection().exchangeContext(getBot());
		getBankingAreaSection().exchangeContext(getBot());
		getChurchGuideSection().exchangeContext(getBot());
		getWizardGuideSection().exchangeContext(getBot());

		mainState = CheckInWhatArea.getState(this);
		log("Set state to: " + mainState);

		// prevents script from skipping character customization
		sleep(4000);
	}

	@Override
	public void onExit() throws InterruptedException {

	}

	@Override
	public void onPaint(Graphics2D g) {

	}

	/**
	 * 
	 */
	private void executeAllEvents() {
		if (!getSettings().areRoofsEnabled()) {
			Event toggleRoofsHiddenEvent = new ToggleRoofsHiddenEvent();
			execute(toggleRoofsHiddenEvent);
		}
		if (!isAudioDisabled) {
			isAudioDisabled = disableAudio();
		} else if (!getSettings().isShiftDropActive()) {
			toggleShiftDrop();
		}
	}

	/**
	 * @return the guilinorGuideSection
	 */
	public TutorialSection getGuilinorGuideSection() {
		return guilinorGuideSection;
	}

	private boolean disableAudio() {
		Event disableAudioEvent = new DisableAudioEvent();
		execute(disableAudioEvent);
		return disableAudioEvent.hasFinished();
	}

	private boolean toggleShiftDrop() {
		Event toggleShiftDrop = new ToggleShiftDropEvent();
		execute(toggleShiftDrop);
		return toggleShiftDrop.hasFinished();
	}

	/**
	 * 
	 * @return
	 */
	public TutorialSection getCharacterCreationSection() {
		return characterCreationSection;
	}

	public TutorialSection getSurvivalExpertSection() {
		return survivalExpertSection;
	}

	/**
	 * @return the cookingGuideSection
	 */
	public TutorialSection getCookingGuideSection() {
		return cookingGuideSection;
	}

	/**
	 * @return the questGuideSection
	 */
	public TutorialSection getQuestGuideSection() {
		return questGuideSection;
	}

	/**
	 * @return the miningGuideSection
	 */
	public TutorialSection getMiningGuideSection() {
		return miningGuideSection;
	}

	/**
	 * @return the combatGuideSection
	 */
	public TutorialSection getCombatGuideSection() {
		return combatGuideSection;
	}

	/**
	 * @return the bankingAreaSection
	 */
	public TutorialSection getBankingAreaSection() {
		return bankingAreaSection;
	}

	/**
	 * @return the churchGuideSection
	 */
	public TutorialSection getChurchGuideSection() {
		return churchGuideSection;
	}

	public TutorialSection getWizardGuideSection() {
		return wizardGuideSection;
	}

}

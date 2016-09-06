package ca.sfu.pacmacro.Controller;

import ca.sfu.pacmacro.Model.Character;

/**
 * Used to tell the CharacterManager which characters to draw on the map.
 *
 * Only really matters for the CharacterManager in the SpectatorActivity, since that's
 * the only activity that displays the characters on the map.
 */
public class CharacterDisplayCriteria {
    public static final int CRITERIA_PLAYER = 0;
    public static final int CRITERIA_GHOST_TEAM = 1;
    public static final int CRITERIA_PACMAN_TEAM = 2;

    public static final String EXTRA_KEY = "TeamDisplayCriteria";

    boolean hideGhosts;
    boolean hidePacman;

    public CharacterDisplayCriteria(int criteriaPreset) {
        switch (criteriaPreset) {
            case CRITERIA_GHOST_TEAM:
                hideGhosts = false;
                hidePacman = true;
                break;
            case CRITERIA_PACMAN_TEAM:
                hideGhosts = false;
                hidePacman = false;
                break;
            case CRITERIA_PLAYER:
                hidePacman = false;
                hideGhosts = false;
                break;
        }
    }

    public boolean isCharacterHidden(Character character) {
        if (character.getType() == Character.CharacterType.PACMAN) {
            // If Pacman is on the Power Pill, he should be seen by everyone
            if (character.getState() == Character.CharacterState.POWERUP) {
                return false;
            }
            return hidePacman;
        }
        else {
            return hideGhosts;
        }
    }
}

package fi.tuska.jalkametri.dao;

public interface DrinkStatus {

    enum DrivingState {
        DrivingOK, DrivingMaybe, DrivingNo
    };

    /**
     * @return the weight (in grams) of alcohol in a standard drink (i.e., one portion). In Finland, this is 12.0 = 12
     * grams.
     */
    double getStandardDrinkAlcoholWeight();

    /**
     * @return the alcohol level, in promilles. E.g, 1.0 means 1.0 promilles. In Finland, 0.5 promilles is the car
     * driving limit.
     */
    double getAlcoholLevel();

    double getMaxAlcoholAmount();

    double getMaxAlcoholLevel();

    double getTotalAlcoholPortions();

    /**
     * Returns the time (in hours) to sober state (all alcohol burned).
     *
     * @return the time (in hours) remaining until the user is sober
     */
    double getHoursToSober();

    /**
     * Returns the time (in hours) to an accepted alcohol level.
     *
     * @param acceptedLevel the accepted level of alcohol in user (in promilles)
     * @return the time (in hours) remaining until the user is at accepted alcohol level
     */
    double getHoursToAlcoholLevel(final double acceptedLevel);

    /** @return the amount of alcohol currently in the user (in grams) */
    double getAlcoholAmount();

    /** @return the driving state of the user */
    DrivingState getDrivingState(Preferences prefs);
}

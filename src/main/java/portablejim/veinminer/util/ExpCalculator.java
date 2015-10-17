package portablejim.veinminer.util;

/**
 * Class to calculate exp values.
 */
public class ExpCalculator {
    private static int LEVEL15EXP = 255;
    private static int LEVEL30EXP = 825;

    private static float area15to30(int x) {
        return ((float)3*x*x)/2+17*x;
    }

    private static int value15to30(int x) {
        return 3*x + 17;
    }

    private static float area30plus(int x) {
        return ((float)7*x*x)/2+62*x;
    }

    private static int value30plus(int x) {
        return 7*x + 62;
    }

    private static int xpBarCap(int xpLevel)
    {
        return xpLevel >= 30 ? 62 + (xpLevel - 30) * 7 : (xpLevel >= 15 ? 17 + (xpLevel - 15) * 3 : 17);
    }

    private static int additionalExp(int expLevel, float xpBar) {
        return (int) ((float)xpBarCap(expLevel) * xpBar);
    }

    public static int getExp(int expLevel, float expBar) {
        if(expLevel <= 15) {
            return 17 * expLevel + additionalExp(expLevel, expBar);
        }
        else if(expLevel <= 30) {
            int diff = expLevel - 15;
            return (int) (area15to30(diff) - area15to30(0) - ((float)value15to30(diff) - (float)value15to30(0))/2.0 + 255 + additionalExp(expLevel, expBar));
        }
        else {
            int diff = expLevel - 30;
            return (int) (area30plus(diff) - area30plus(0) - ((float)value30plus(diff) - (float)value30plus(0))/2.0 + 825 + additionalExp(expLevel, expBar));

        }
    }
}

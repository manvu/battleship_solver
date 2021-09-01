
import battleship.BattleShip;

/**
 * Starting code for COMP10205 - Assignment#6
 *
 * @author mark.yendt
 */

public class A6 {
    static final int NUMBEROFGAMES = 10000;

    public static void startingSolution() {
        int totalShots = 0;
        System.out.println(BattleShip.version());


        for (int game = 0; game < NUMBEROFGAMES; game++) {

            BattleShip battleShip = new BattleShip();
            DrInvisible_Bot bot = new DrInvisible_Bot(battleShip);

            while (!battleShip.allSunk()) {
                bot.fireShot();

            }

            int gameShots = battleShip.totalShotsTaken();
            totalShots += gameShots;
        }

        System.out.printf("DrInvisible_Bot - The Average # of Shots required in %d games to sink all Ships = %.2f\n", NUMBEROFGAMES, (double) totalShots / NUMBEROFGAMES);
    }

    public static void main(String[] args) {
        startingSolution();
    }
}

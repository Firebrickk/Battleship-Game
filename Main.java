package battleship;
import java.io.IOException;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class Main {

    public static void main(String[] args) throws Exception {
        Field field = new Field();
        field.game();
    }

}

enum Ships {

    AIRCRAFT_CARRIER(5, "Aircraft Carrier"),
    BATTLESHIP(4, "Battleship"),
    SUBMARINE(3, "Submarine"),
    CRUISER(3, "Cruiser"),
    DESTROYER(2, "Destroyer");

    private final int squares;
    private final String name;

    Ships(int squares, String name) {
        this.squares = squares;
        this.name = name;
    }

    public int getSquares() {
        return squares;
    }

    public String getName() {
        return name;
    }

     static int[] getCoordinates(int squares) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int[] coordinates;
        do {
            String input = reader.readLine();
            char[] k = new char[input.length()];
            for (int i = 0; i < input.length(); i++) {
                k[i] = input.charAt(i);
            }
            int x1 = 0;
            int x2 = 0;
            int y1 = k[0] - 64;
            int y2 = 0;
            switch (input.length()) {
                case 5:
                    x1 = k[1] - 48;
                    y2 = k[3] - 64;
                    x2 = k[4] - 48;
                    break;
                case 6:
                    if (k[2] == '0') {
                        x1 = 10;
                        y2 = k[4] - 64;
                        x2 = k[5] - 48;
                    } else {
                        x1 = k[1] - 48;
                        y2 = k[3] - 64;
                        x2 = 10;
                    }
                    break;
                case 7:
                    x1 = 10;
                    y2 = k[4] - 64;
                    x2 = 10;
                    break;
                default:
                    break;
            }
            coordinates = new int[]{y1, x1, y2, x2};

            if (coordinates[0] != coordinates[2] && (coordinates[1] != coordinates[3])) {
                System.out.println("Error! Wrong coordinates");
            } else if (Math.abs(coordinates[0] - coordinates[2]) != squares - 1 && Math.abs(coordinates[1] - coordinates[3]) != squares - 1) {
                System.out.println("Error! Wrong length");
            } else {
                break;
            }
        }
        while (true);

        return coordinates;
    }
}

class Field {

    char[][] fields = new char[11][11];

    Field() {
        for (char[] field : fields) {
            Arrays.fill(field, '~');
        }
    }

    protected void game() throws Exception {
        Field field1 = new Field();
        Field field2 = new Field();
        System.out.println("Player 1, place your ships on the game field");
        takePositions(field1);
        enterKey();
        System.out.println("Player 2, place your ships to the game field");
        takePositions(field2);
        System.out.println("The game starts!");
        enterKey();
        while (true) {
            fieldPrinter(field2, true);
            System.out.println("---------------------");
            fieldPrinter(field1, false);
            System.out.println("Player 1, it's your turn:");
            mapAfterShot(field2);
            if (checkForEnd(field2)) {
                break;
            }
            enterKey();
            fieldPrinter(field1, true);
            System.out.println("---------------------");
            fieldPrinter(field2, false);
            System.out.println("Player 2, it's your turn:");
            mapAfterShot(field1);
            if (checkForEnd(field1)) {
                break;
            }
            enterKey();
        }
    }

    protected void takePositions(Field field) throws Exception {
        int[] coordinates;
        fieldPrinter(field, false);
        for (Ships ships : Ships.values()) {
            System.out.printf("Enter the coordinates of the %s (%d cells):", ships.getName(), ships.getSquares());
            System.out.println();
            System.out.println();
            do {
                coordinates = Ships.getCoordinates(ships.getSquares());
            } while (checkForBump(field, coordinates));
            addShip(field, coordinates);
            fieldPrinter(field, false);
        }
    }

    protected boolean checkForHit(Field field, int[] shot) {
        return field.fields[shot[0]][shot[1]] == 'O' || field.fields[shot[0]][shot[1]] == 'X';
    }

    protected boolean checkForSink(Field field, int[] shot) {
        char[][] check = new char[12][12];
        for (int i = 0; i < field.fields.length; i++) {
            System.arraycopy(field.fields[i], 0, check[i], 0, field.fields.length);
        }
        return check[shot[0] - 1][shot[1]] == 'O' ||
                check[shot[0] + 1][shot[1]] == 'O' ||
                check[shot[0]][shot[1] - 1] == 'O' ||
                check[shot[0]][shot[1] + 1] == 'O';
    }

    protected boolean checkForEnd(Field field) {
        boolean end = true;
        for (char[] chars : field.fields) {
            for (int j = 0; j < field.fields.length; j++) {
                if (chars[j] == 'O') {
                    end = false;
                    break;
                }
            }
            if (!end) {
                break;
            }
        }
        return end;
    }

    @SuppressWarnings("all")
    protected void enterKey() {
        System.out.println("Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void mapAfterShot (Field field) throws Exception  {
        int[] shot = getCoordinatesShot();
        boolean b;
        if (checkForHit(field, shot)) {
            field.fields[shot[0]][shot[1]] = 'X';
            b = true;
        } else {
            field.fields[shot[0]][shot[1]] = 'M';
            b = false;
        }
        fieldPrinter(field, true);
        if (checkForEnd(field)) {
            System.out.println("You sank the last ship. You won. Congratulations!");
        } else if (b && !checkForSink(field, shot) ) {
            System.out.println("You sank a ship!");
        } else if (b) {
            System.out.println("You hit a ship!");
        } else {
            System.out.println("You missed!");
        }
    }

    protected boolean checkForBump(Field field, int[] coordinates) {
        char[][] check = new char[12][12];
        boolean bump = false;
        for (int i = 0; i < field.fields.length; i++) {
            System.arraycopy(field.fields[i], 0, check[i], 0, field.fields.length);
        }
        for (int i = Math.min(coordinates[0], coordinates[2]) - 1; i <= Math.max(coordinates[0], coordinates[2]) + 1; i++) {
            for (int j = Math.min(coordinates[1], coordinates[3]) - 1; j <= Math.max(coordinates[1], coordinates[3]) + 1; j++) {
                if (check[i][j] == 'O') {
                    System.out.println("Error! Shipwreck!");
                    bump = true;
                    break;
                }
            }
            if (bump) {
                break;
            }
        }
        return bump;
    }

    protected void fieldPrinter(Field field, boolean fog) {
        field.fields[0][0] = ' ';
        char numb = '1';
        char let = 'A';
        for (int i = 1; i < field.fields.length; i++) {
            field.fields[0][i] = numb;
            numb++;
            field.fields[i][0] = let;
            let ++;
        }
        field.fields[0][10] ='1';
        System.out.println();
        for (int i = 0; i < field.fields.length; i++) {
            for (int j = 0; j < field.fields.length; j++) {
                if (j != field.fields.length - 1) {
                    if (fog && field.fields[i][j] == 'O') {
                        System.out.print("~ ");
                    } else {
                        System.out.printf("%c%c", field.fields[i][j], ' ');
                    }
                } else {
                    if (fog && field.fields[i][j] == 'O') {
                        System.out.print("~");
                    } else {
                        System.out.printf("%c", field.fields[i][j]);
                    }
                }
                if (i == 0 && j == 10) {
                    System.out.print("0");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    protected void addShip(Field field, int[] coordinates) {
        if(coordinates[0] == coordinates[2]) {
            for (int i = Math.min(coordinates[1], coordinates[3]); i <= Math.max(coordinates[1], coordinates[3]); i++) {
                field.fields[coordinates[0]][i] = 'O';
            }
        } else {
            for (int i = Math.min(coordinates[0], coordinates[2]); i <= Math.max(coordinates[0], coordinates[2]); i++) {
                field.fields[i][coordinates[1]] = 'O';
            }
        }
    }

    protected int[] getCoordinatesShot() throws Exception {

        int[] shot;
        do {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            char[] k = new char[input.length()];
            for (int i = 0; i < input.length(); i++) {
                k[i] = input.charAt(i);
            }
            int x = 0;
            int y = 0;
            switch (input.length()) {
                case 2:
                    x = k[1] - 48;
                    y = k[0] - 64;
                    break;
                case 3:
                    if (k[2] == '0' && k[1] == '1') {
                        x = 10;
                        y = k[0] - 64;
                    } else{
                        k[0] = 'K';
                    }
                    break;
                default:
                    break;
            }
            shot = new int[]{y, x};
            if (k[0] >= 'A' && k[0] <= 'J' && k[1] >= '1' && k[1] <= '9' && input.length() <= 3) {
                break;
            } else {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            }

        } while (true);
        return shot;
    }


}
import java.util.Scanner; // Импорт класса Scanner для ввода данных с клавиатуры
import java.util.InputMismatchException; // Импорт класса InputMismatchException для обработки исключений при неверном вводе

public class Player {

    static final char SPACE_CHAR = ' ';
    private String name;
    private Field board;
    private char mark;
    private Scanner sc;

    Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
        this.sc = new Scanner(System.in);
    }

    void setBoard(Field board){
        this.board = board;
    }

    public String makeMove() { // Метод для выполнения хода игрока
        int row;
        int col;

        while (true) {

            while (true) {
                System.out.println("\n" + name + ", which row do you want to put " + mark + "? ");
                try {
                    row = sc.nextInt();

                    if (row < 0 || row > 2) {
                        System.out.println("Invalid input, string must be between 0 and 2");

                    } else {
                        break;
                    }
                } catch (InputMismatchException e) { // Обработка исключения при неверном вводе типа данных

                    System.out.println("This is not an integer. Please enter an integer between 0 and 2.");

                    sc.next();

                }
            }

            while (true) {
                System.out.println("\n" + name + ", which column do you want to put in " + mark + "? ");
                try {
                    col = sc.nextInt();

                    if (col < 0 || col > 2) {
                        System.out.println("Invalid input, string must be between 0 and 2");
                        continue;
                    } else {
                        break;
                    }
                } catch (InputMismatchException e) {

                    System.out.println("This is not an integer. Please enter an integer between 0 and 2.");

                    sc.next();

                    continue;
                }
            }


            if (board.getMark(row, col) != SPACE_CHAR) { // Проверка, занята ли указанная клетка на игровом поле
                System.out.println("This place is already taken. Please enter the row and column again.");

                continue;
            }


            board.addMark(row, col, mark); // Добавление отметки на игровое поле
            return new String(row + " " + col); // Возврат координат сделанного хода
        }
    }

}

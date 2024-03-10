import java.io.BufferedReader;
import java.io.IOException; // Импорт класса IOException для обработки ошибок ввода-вывода
import java.io.InputStreamReader;
import java.sql.Connection; // Импорт класса Connection для работы с базой данных
import java.sql.PreparedStatement; // Импорт класса PreparedStatement для выполнения SQL-запросов
import java.sql.SQLException;
import java.sql.Timestamp; // Импорт класса Timestamp для работы с временем

public class Gameplay {
    static final char LETTER_O = 'O';
    static final char LETTER_X = 'X';
    private Field board;
    private Player myPlayer;
    private boolean playingX; // Переменная для определения, играют ли крестики
    private BufferedReader stdInBufferedReader; // Объект BufferedReader для чтения ввода с клавиатуры
    private Connection connection; // Экземпляр Connection для работы с базой данных
    private String startTime; // Строка для хранения времени начала игры

    private String name; // Строка для хранения имени игрока


    public Gameplay(boolean playingX, Connection connection) {
        board = null;
        myPlayer = null;
        this.playingX = playingX; // Установка режима игры (крестики или нолики)
        stdInBufferedReader = null;
        this.connection = connection;
    }
    public Field getBoard(){
        return board;
    }
    public void setBoard(Field board) {
        this.board = board;
    }


    public boolean getPlayingX(){
        return playingX;
    }

    // Метод для настройки начала игры
    public void setupGame() throws IOException {
        board = new Field(); // Создание нового экземпляра игрового поля
        System.out.println("The game is started...");
        startTime = getCurrentTime(); // Получение времени начала игры

        System.out.print("\nPlease enter your name: \n");
        stdInBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        name = stdInBufferedReader.readLine();
        while (name == null) {
            System.out.print("Please try again: ");
            name = stdInBufferedReader.readLine();
        }

        // Создание экземпляра игрока в зависимости от режима игры (крестики или нолики)
        if (playingX) {
            myPlayer = new Player(name, LETTER_X);
        } else {
            myPlayer = new Player(name, LETTER_O);
        }
        myPlayer.setBoard(board);
        board.display(); // Отображение игрового поля
    }

    // Метод для проверки окончания игры
    public boolean checkGameEnd() {
        String endTime = getCurrentTime(); // Получение времени завершения игры
        String result = getGameResult(); // Получение результата игры

        // Проверка победы крестиков или ноликов, соответственно режиму игры
        if ((board.xWins() && playingX) || (board.oWins() && !playingX)) {
            saveGameDataToDatabase(startTime, endTime, name, result); // Сохранение данных игры в базу данных
            System.out.println("THE GAME IS OVER: YOU WIN!!! :)");
            return true;
        } else if ((board.xWins() && !playingX) || (board.oWins() && playingX)) {
            System.out.println("THE GAME IS OVER: You lose.");
            return true;
        } else if (board.isFull()) { // Если доска заполнена, но никто не победил
            name = "No one";
            saveGameDataToDatabase(startTime, endTime, name, result); // Сохранение данных игры в базу данных
            System.out.println("THE GAME IS OVER: It is a tie!");
            return true;
        }
        return false; // Возвращается false, если игра продолжается
    }

    // Метод для совершения хода текущего игрока
    public String makeMyMove(){
        String moveMade = myPlayer.makeMove(); // Совершение хода текущего игрока
        board.display(); // Отображение игрового поля после хода
        return moveMade;
    }

    // Метод для обработки хода оппонента
    public void makeOpponentMove(String opponentMove){
        String[] parts = opponentMove.split(" "); // Разделение входной строки на части
        int row = Integer.parseInt(parts[0]); // Получение номера строки
        int col = Integer.parseInt(parts[1]); // Получение номера столбца
        char opponentMark = (playingX) ? LETTER_O : LETTER_X; // Получение символа оппонента
        board.addMark(row, col, opponentMark); // Добавление хода оппонента на игровое поле

        System.out.println("\nThe other player made a move!");
        board.display();
    }

    // Метод для определения, сделал ли оппонент последний ход
    public boolean opponentMadeLastMove() {
        return (((board.getMarkCount() % 2) == 0 && playingX) || ((board.getMarkCount() % 2) == 1 && !playingX));
    }

    // Метод для получения результата игры
    public String getGameResult() {
        if ((board.xWins() && playingX) || (board.oWins() && !playingX)) {
            return "win";
        } else if ((board.xWins() && !playingX) || (board.oWins() && playingX)) {
            return "lose";
        } else {
            return "tie";
        }
    }

    // Метод для сохранения данных игры в базу данных
    public void saveGameDataToDatabase(String startTime, String endTime, String winnerName, String result) {
        try {
            String sql = "INSERT INTO game_data (start_time, end_time, winner_name, game_result) VALUES (?, ?, ?, ?)"; // SQL-запрос для добавления данных игры в базу данных
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, startTime);
            statement.setString(2, endTime);
            statement.setString(3, winnerName);
            statement.setString(4, result);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения текущего времени
    private String getCurrentTime() {
        java.util.Date date = new java.util.Date();
        return new Timestamp(date.getTime()).toString();
    }
}

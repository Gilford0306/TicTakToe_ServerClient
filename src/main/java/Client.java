import java.io.BufferedReader; // Импорт класса BufferedReader для чтения данных из потока ввода
import java.io.IOException; // Импорт класса IOException для обработки ошибок ввода-вывода
import java.io.InputStreamReader; // Импорт класса InputStreamReader для чтения байтового потока ввода в символьный поток
import java.io.PrintWriter; // Импорт класса PrintWriter для записи данных в поток вывода
import java.net.Socket; // Импорт класса Socket для установки соединения с сервером по сети
import java.sql.Connection; // Импорт класса Connection для установления соединения с базой данных
import java.sql.DriverManager; // Импорт класса DriverManager для управления драйверами JDBC
import java.sql.SQLException; // Импорт класса SQLException для обработки ошибок SQL

public class Client {
    private PrintWriter socketOut;
    private Socket socket;
    private BufferedReader socketIn;
    private String serverName;
    private int gamePort;
    private Gameplay gameplay;
    private Connection connection;

    // Конструктор класса Client
    public Client(String serverName, int portNumber) {
        this.serverName = serverName;
        try {
            establishDatabaseConnection();
            socket = new Socket(serverName, portNumber);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }
    }

    private void establishDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/tictactoe_db";
        String username = "root";
        String password = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для подключения к лобби игры "Крестики-нолики"
    public void connectToTicTacToeLobby() throws IOException {
        String response = "";
        try {
            response = socketIn.readLine();
            gamePort = Integer.parseInt(response); // Парсинг строки в число - порт игры
        } catch (NumberFormatException e) {
            System.err.println(response + " is not a valid integer. Exiting...");
            System.exit(-1);
        }
        System.out.println("Game port: " + gamePort);
    }

    // Метод для отключения от лобби игры "Крестики-нолики"
    public void disconnectFromTicTacToeLobby() throws IOException {
        socketIn.close(); // Закрытие потока ввода
    }

    // Метод для подключения к игре
    public void connectToGame() throws IOException {
        // Создание нового сокета для подключения к другому игроку/доске
        System.out.println("Attempting to connect to port " + gamePort);
        socket = new Socket(serverName, gamePort);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter((socket.getOutputStream()), true);
        if (socketIn.readLine().equals("Connected")) {
            System.out.println("Connection established!");
        } else {
            throw new IOException("Did not receive expected connection message");
        }
    }

    // Метод для настройки игрового процесса
    public void setupGameplay() throws IOException {
        boolean playingX; // Флаг, указывающий, играет ли текущий игрок за крестики
        if ((gamePort % 2) == 1) {
            playingX = true; // Установка флага, что текущий игрок играет за крестики
        } else {
            playingX = false; // Установка флага, что текущий игрок играет за нолики
        }

        gameplay = new Gameplay(playingX, connection); // Создание экземпляра класса Gameplay для управления игрой
        gameplay.setupGame();

        if (gameplay.getPlayingX()) { // Проверка, играет ли текущий игрок за крестики
            System.out.println("Waiting for another player to join...");
            System.out.println("You will be playing X\n");
            String player2connected = socketIn.readLine();
            if (player2connected.equals("P2connected")) {
                System.out.println("The other player has connected!");
            }
        } else {
            System.out.println("You are playing Os and move first.");
        }
    }

    // Метод для приема хода от другого игрока
    public String receiveMove() throws IOException {
        System.out.println("The other player is making a move...");
        return socketIn.readLine(); // Возвращение строки с ходом от другого игрока
    }

    // Метод для выполнения хода в игре
    public void playGame() throws IOException {
        if (!(gameplay.getPlayingX())) {
            String gameplayResponse = gameplay.makeMyMove(); // Выполнение хода текущим игроком
            socketOut.println(gameplayResponse); // Отправка хода на сервер
        }
        while (true) {
            String opponentMove = receiveMove(); // Получение хода от другого игрока
            gameplay.makeOpponentMove(opponentMove); // Обработка хода другого игрока
            if (gameplay.checkGameEnd()) { // Проверка завершения игры
                break;
            }
            String refereeResponse = gameplay.makeMyMove(); // Выполнение хода текущим игроком
            socketOut.println(refereeResponse); // Отправка хода на сервер
            if (gameplay.checkGameEnd()) {
                break;
            }
        }

        if (gameplay.opponentMadeLastMove()) { // Проверка, сделал ли другой игрок последний ход
            socketOut.println("QUIT"); // Отправка сообщения о завершении игры на сервер
        }
    }


    public void disconnectFromGame() throws IOException {
        socketIn.close();
        socketOut.close();
    }

    // Метод для начала игры "Крестики-нолики"
    public void playTicTacToe() {
        try {
            connectToTicTacToeLobby(); // Подключение к лобби игры
            disconnectFromTicTacToeLobby(); // Закрытие потока ввода
            connectToGame(); // Подключение к игре
            setupGameplay(); // Настройка игрового процесса
            playGame(); // Выполнение игры
            disconnectFromGame(); // Отключение от игры
        } catch (IOException e) { // Обработка ошибок ввода-вывода
            System.out.println("Error occurred while playing game."); // Вывод сообщения об ошибке
            System.err.println(e.getStackTrace()); // Вывод стека вызовов исключения
            System.err.println(e.getMessage()); // Вывод сообщения об ошибке
            System.out.println("Exiting..."); // Вывод сообщения о завершении программы
            System.exit(-1); // Выход из программы с кодом ошибки
        }
    }

    // Главный метод программы
    public static void main(String[] args) {
        Client aClient = new Client("localhost", 9000);
        aClient.playTicTacToe(); // Начало игры "Крестики-нолики"
    }
}

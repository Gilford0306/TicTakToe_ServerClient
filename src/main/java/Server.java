import java.io.IOException; // Импорт класса IOException для обработки ошибок ввода-вывода
import java.io.PrintWriter; // Импорт класса PrintWriter для записи данных в сокет
import java.net.ServerSocket; // Импорт класса ServerSocket для создания серверного сокета
import java.net.Socket; // Импорт класса Socket для установки соединения с клиентом
import java.util.concurrent.*; // Импорт классов для работы с многопоточностью

public class Server {
    PrintWriter out; // Объект PrintWriter для записи в сокет
    Socket aSocket; // Объект Socket для общения с клиентом
    ServerSocket serverSocket; // Объект ServerSocket для прослушивания подключений клиентов
    ExecutorService threadPool; // Пул потоков для управления несколькими клиентскими подключениями параллельно
    int portNumberOfGameForNextPlayer; // Номер порта для следующей игры для игрока

    // Конструктор класса Server
    public Server() throws IOException {
        serverSocket = new ServerSocket(9000);
        System.out.println("Server is running");
        threadPool = Executors.newFixedThreadPool(2); // Создание пула потоков фиксированного размера с двумя потоками
        portNumberOfGameForNextPlayer = 9001; // Инициализация номера порта для следующей игры
    }

    // Метод для запуска сервера TicTacToe
    public void hostTicTacToeServer(){
        try {
            while(true) {
                connectToNextPlayer();
                connectPlayerToGame();
                disconnectFromPlayer();
                portNumberOfGameForNextPlayer++;
            }
        } catch (IOException e) {
            System.err.println("Received an IOException, exiting...");
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    // Метод для принятия подключения следующего игрока
    public void connectToNextPlayer() throws IOException {
        aSocket = serverSocket.accept();
        System.out.println("A new player connected");
        out = new PrintWriter((aSocket.getOutputStream()), true); // Создание PrintWriter для записи в сокет
    }


    public void connectPlayerToGame() throws IOException {
        if(portNumberOfGameForNextPlayer % 2 == 1) { // Проверка, является ли номер порта нечетным
            // Запуск нового экземпляра Gamestart для игры игрока
            threadPool.execute(new Gamestart(portNumberOfGameForNextPlayer, portNumberOfGameForNextPlayer+1));
        }
        out.println(portNumberOfGameForNextPlayer); // Отправка номера порта игроку
    }

    public void disconnectFromPlayer() throws IOException {
        out.close();
    }

    // Основной метод для запуска сервера
    public static void main(String[] args) throws IOException {
        Server myserver = new Server();
        myserver.hostTicTacToeServer();
    }
}

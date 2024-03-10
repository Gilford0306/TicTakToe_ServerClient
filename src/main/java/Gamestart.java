import java.io.*; // Импорт всех классов из пакета java.io для работы с вводом-выводом
import java.net.ServerSocket; // Импорт класса ServerSocket для создания серверного сокета
import java.net.Socket; // Импорт класса Socket для работы с сокетами
public class Gamestart implements Runnable { //Runnable функциональный интерфейс, который используется для описания объектов, которые могут быть запущены в отдельном потоке

    private int player1PortNumber;
    private Socket player1Socket;
    private ServerSocket player1ServerSocket;
    private PrintWriter outputToPlayer1;
    private BufferedReader inputFromPlayer1;
    private int player2PortNumber;
    private Socket player2Socket;
    private ServerSocket player2ServerSocket;
    private PrintWriter outputToPlayer2;
    private BufferedReader inputFromPlayer2;

    public Gamestart(int player1PortNumber, int player2PortNumber) { // Установка портов для игроков
        this.player1PortNumber = player1PortNumber;
        this.player2PortNumber = player2PortNumber;
    }

    public void connectToPlayers() throws IOException {
        player1ServerSocket = new ServerSocket(player1PortNumber);
        player1Socket = player1ServerSocket.accept(); // Ожидание подключения  игрока
        outputToPlayer1 = new PrintWriter((player1Socket.getOutputStream()), true); // Создание потока для отправки данных первому игроку
        inputFromPlayer1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream())); // Создание потока для приёма данных от первого игрока
        outputToPlayer1.println("Connected");
        System.out.println("The new player is connected to a game on port " + player1PortNumber);

        player2ServerSocket = new ServerSocket(player2PortNumber);
        player2Socket = player2ServerSocket.accept();
        outputToPlayer2 = new PrintWriter((player2Socket.getOutputStream()), true);
        inputFromPlayer2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
        outputToPlayer2.println("Connected");
        System.out.println("The new player is connected to a game on port " + player2PortNumber);
        outputToPlayer1.println("P2connected");
    }

    public void runTheGame() throws IOException{ // Метод для запуска игры
        while(true) {
            String player2Input = inputFromPlayer2.readLine(); // Считывание ввода от игрока
            if(player2Input == null || player2Input.equals("QUIT")) { // Проверка на завершение игры
                break;
            }
            outputToPlayer1.println(player2Input); // Отправка ввода от второго игрока первому игроку

            String player1Input = inputFromPlayer1.readLine();
            if (player1Input == null || player1Input.equals("QUIT")) {
                break;
            }
            outputToPlayer2.println(player1Input);
        }
    }

    public void closeIOStreams() throws IOException{

        outputToPlayer1.close();
        outputToPlayer2.close();
        inputFromPlayer1.close();
        inputFromPlayer2.close();
    }

    @Override
    public void run() { // Реализация метода run интерфейса Runnable
        try {
            connectToPlayers();
            runTheGame();
            closeIOStreams();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

public class Field {
    static final char SPACE_CHAR = ' ';
    static final char LETTER_O = 'O';
    static final char LETTER_X = 'X';
    private char theBoard[][]; // Двумерный массив для хранения состояния доски
    private int markCount; // Счетчик установленных знаков на доске


    public Field() {
        markCount = 0;
        theBoard = new char[3][];
        // Инициализация доски пустыми знаками
        for (int i = 0; i < 3; i++) {
            theBoard[i] = new char[3];
            for (int j = 0; j < 3; j++)
                theBoard[i][j] = SPACE_CHAR;
        }
    }

    // Метод для получения знака на указанной позиции на доске
    public char getMark(int row, int col) {
        return theBoard[row][col];
    }

    // Метод для получения количества установленных знаков на доске
    public int getMarkCount() {
        return markCount;
    }

    // Метод для проверки заполненности доски
    public boolean isFull() {
        return markCount == 9; // Возвращает true, если все клетки доски заполнены
    }

    // Метод для проверки победы знака крестика
    public boolean xWins() {
        if (checkWinner(LETTER_X) == 1)
            return true; // Возвращает true, если крестики победили
        else
            return false;
    }


    public boolean oWins() {
        if (checkWinner(LETTER_O) == 1)
            return true; // Возвращает true, если нолики победили
        else
            return false;
    }

    // Метод для отображения текущего состояния доски
    public void display() {
        displayColumnHeaders(); // Отображение заголовков столбцов
        addHyphens(); // Отображение горизонтальных линий
        // Построчное отображение доски с установленными знаками
        for (int row = 0; row < 3; row++) {
            addSpaces();
            System.out.print("    row " + row + ' ');
            for (int col = 0; col < 3; col++)
                System.out.print("|  " + getMark(row, col) + "  ");
            System.out.println("|");
            addSpaces();
            addHyphens();
        }
    }

    // Метод для установки знака на указанную позицию на доске
    public void addMark(int row, int col, char mark) {
        theBoard[row][col] = mark;
        markCount++;
    }

    // Метод для очистки доски
    public void clear() {
        // Сброс всех знаков на доске до пустых знаков
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                theBoard[i][j] = SPACE_CHAR;
        markCount = 0;
    }

    // Метод для проверки победителя
    int checkWinner(char mark) {
        int row, col;
        int result = 0;

        // Проверка победы по горизонталям
        for (row = 0; result == 0 && row < 3; row++) {
            int row_result = 1;
            for (col = 0; row_result == 1 && col < 3; col++)
                if (theBoard[row][col] != mark)
                    row_result = 0;
            if (row_result != 0)
                result = 1;
        }

        // Проверка победы по вертикалям
        for (col = 0; result == 0 && col < 3; col++) {
            int col_result = 1;
            for (row = 0; col_result != 0 && row < 3; row++)
                if (theBoard[row][col] != mark)
                    col_result = 0;
            if (col_result != 0)
                result = 1;
        }

        // Проверка победы по диагоналям
        if (result == 0) {
            int diag1Result = 1;
            for (row = 0; diag1Result != 0 && row < 3; row++)
                if (theBoard[row][row] != mark)
                    diag1Result = 0;
            if (diag1Result != 0)
                result = 1;
        }
        if (result == 0) {
            int diag2Result = 1;
            for (row = 0; diag2Result != 0 && row < 3; row++)
                if (theBoard[row][2 - row] != mark)
                    diag2Result = 0;
            if (diag2Result != 0)
                result = 1;
        }
        return result;
    }

    // Метод для отображения вертикальных линий
    void displayColumnHeaders() {
        System.out.print("          ");
        for (int j = 0; j < 3; j++)
            System.out.print("|col " + j);
        System.out.println();
    }

    // Метод для добавления горизонтальных линий
    void addHyphens() {
        System.out.print("          ");
        for (int j = 0; j < 3; j++)
            System.out.print("+-----");
        System.out.println("+");
    }

    // Метод для добавления пустых клеток на доске
    void addSpaces() {
        System.out.print("          ");
        for (int j = 0; j < 3; j++)
            System.out.print("|     ");
        System.out.println("|");
    }
}

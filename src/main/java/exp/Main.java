package exp;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class Main {
    private static final String FILE_PATH = "output.txt";
    private static final Object lock = new Object();
    private static long lastReadPosition = 0; // Позиция, с которой нужно читать файл


    public static void main(String[] args) {
        // Поток для записи четных чисел
        Thread evenThread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                // Диапазон целых чисел не задан. Ограничил его для простоты.
                int number = random.nextInt(50) * 2;
                writeToFile(number);
                try {
                    Thread.sleep(1000); // Задержка для наглядности
                } catch (InterruptedException e) {
                    System.err.println("Поток был прерван: " + e.getMessage());
                    // Статус прерывания
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Поток для записи нечетных чисел
        Thread oddThread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                // Диапазон целых чисел не задан. Ограничил его для простоты.
                int number = random.nextInt(50) * 2 + 1;
                writeToFile(number);
                try {
                    Thread.sleep(1000); // Задержка
                } catch (InterruptedException e) {
                    System.err.println("Поток был прерван: " + e.getMessage());
                    // Статус прерывания
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Поток для чтения файла и вывода в консоль
        Thread readerThread = new Thread(() -> {
            while (true) {
                readNewFromFile();
                try {
                    Thread.sleep(500); // Частота проверки файла
                } catch (InterruptedException e) {
                    System.err.println("Поток был прерван: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        });

        evenThread.start();
        oddThread.start();
        readerThread.start();
    }

    private static void writeToFile(int number) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
                writer.write(number + "\n");
            } catch (IOException e) {
                // Обработка ошибки записи в файл
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
        }
    }

    private static void readNewFromFile() {
        synchronized (lock) {
            try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r")) {
                file.seek(lastReadPosition); // Перемещаемся на последнюю прочитанную позицию
                String line;
                while ((line = file.readLine()) != null) {
                    System.out.println("Read from file: " + line);
                }
                lastReadPosition = file.getFilePointer(); // Запоминаем новую позицию
            } catch (FileNotFoundException e) {
                //Обработка ошибки "Файл не найден"
                System.err.println("Файл не найден: " + e.getMessage());
            } catch (IOException e) {
                // Обработка других ошибок ввода-вывода
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            }
        }
    }
}

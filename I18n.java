package Warehouse;

public class I18n {
    public static final String EMPTY_CONTAINER = "ПУСТАЯ ТАРА";
    public static final String NUMBER_OF_TASKS = "Число заданий";
    public static final String NECESSARY_NUMBER_OF_STOCKKEEPERS = "Необходимое количество кладовщиков";
    public static final String STOCKKEEPER = "Кладовщик";
    public static final String TOTAL_DISTANCE = "Суммарный пробег кладовщика в метрах";
    public static final String DISTANCE_TIME = "Время, потраченное на движение (сек)";
    public static final String ADDITIONAL_TIME = "Время, потраченное на отборку (сек)";
    public static final String TOTAL_TIME = "Общее время (сек)";
    public static final String REPLENISHMENT = "Информация по пополнениям";
    public static final String DELIVERY = "Информация по отгрузке";
    public static final String TASKS_INFO = "Информация по заданиям";
    public static final String TRUCKS_INFO = "Информация по взятым заданиям кладовщиками";
    public static final String TRUCK = "кладовщик";
    public static final String TASK = "задание";
    public static final String TIMES = " раз(а)";
    public static final String TASK_EXECUTION_TIME = "Время выполнения задания";
    public static final String TASK_SHELFS = "Посещенные ячейки";
    public static final String PLANNING_ERROR = "Не удалось распределить задания по кладовщикам. Необходимо уменьшить максимальное время выполнения заказа";
    public static final String WRONG_TIME_FORMAT = "Неверный формат времени. Введите время, используя следующий формат: \"03:00\". ";
    public static final String WRONG_TIME_INTERVAL_FORMAT = "Неверный формат задания промежутка времени. Введите промежуток, используя следующий формат: \"03:00-04:00\". ";
    public static final String WRONG_WAREHOUSE_DIRECTION = "Ячейка с направлением движения между 1 и 2 рядом должна содержать значения \"0\" или \"1\" (0 - вниз, 1 - вверх). ";
    public static final String WRONG_COORDINATES = "Неверный формат задания координат. Введите координаты, используя следующий формат: \"(21.1; 75)\". ";
    public static final String INPUT_ERROR_LOG = "Ошибка при попытке чтения входного файла. Подробнее: ";
    public static final String INPUT_ERROR_RESULT = "Ошибка при попытке чтения входного файла. Более подробную информацию можно найти в файле test.log. ";
    
    public static String shelfNotFound(String name) {
        return "Не удалось найти ячейку c названием \"" + name + "\". Добавьте ячейку с данным названием в файле с топологией склада или исправьте название. ";
    }
    
    public static String errorLine(int lineNumber, String filename) {
        return "Исправьте строку " + lineNumber + " в файле " + filename + ". ";
    }
    
    public static String wrongFormatOfFile(String filename) {
        return "Неверный формат файла " + filename + ". ";
    }
    
    public static String wrongTop_LeftAndBottom_RightCoordinates(int row) {
        return "Неверное задание левой верхней и правой нижней вершины " + row + " ряда. ";
    }
    
    public static String wrongNumberOfShelfs(int row) {
        return "Неверное задание числа ячеек в ряду " + row + ". ";
    }
    
    public static String wrongCell(int lineNumber, int row, String filename) {
        return "Исправьте ячейку, находящуюся в строке " + lineNumber + " и в столбце " + row + " файла " + filename + ". ";
    }
    
    public static String wrongNumberOfShelfs(int row, String filename) {
        return "Исправьте число ячеек в ряде " + row + " в файле " + filename + ". ";
    }
}

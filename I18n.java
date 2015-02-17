package Warehouse;

public class I18n {
    public static final String EMPTY_CONTAINER = "������ ����";
    public static final String NUMBER_OF_TASKS = "����� �������";
    public static final String NECESSARY_NUMBER_OF_STOCKKEEPERS = "����������� ���������� �����������";
    public static final String STOCKKEEPER = "���������";
    public static final String TOTAL_DISTANCE = "��������� ������ ���������� � ������";
    public static final String DISTANCE_TIME = "�����, ����������� �� �������� (���)";
    public static final String ADDITIONAL_TIME = "�����, ����������� �� ������� (���)";
    public static final String TOTAL_TIME = "����� ����� (���)";
    public static final String REPLENISHMENT = "���������� �� �����������";
    public static final String DELIVERY = "���������� �� ��������";
    public static final String TASKS_INFO = "���������� �� ��������";
    public static final String TRUCKS_INFO = "���������� �� ������ �������� ������������";
    public static final String TRUCK = "���������";
    public static final String TASK = "�������";
    public static final String TIMES = " ���(�)";
    public static final String TASK_EXECUTION_TIME = "����� ���������� �������";
    public static final String TASK_SHELFS = "���������� ������";
    public static final String PLANNING_ERROR = "�� ������� ������������ ������� �� �����������. ���������� ��������� ������������ ����� ���������� ������";
    public static final String WRONG_TIME_FORMAT = "�������� ������ �������. ������� �����, ��������� ��������� ������: \"03:00\". ";
    public static final String WRONG_TIME_INTERVAL_FORMAT = "�������� ������ ������� ���������� �������. ������� ����������, ��������� ��������� ������: \"03:00-04:00\". ";
    public static final String WRONG_WAREHOUSE_DIRECTION = "������ � ������������ �������� ����� 1 � 2 ����� ������ ��������� �������� \"0\" ��� \"1\" (0 - ����, 1 - �����). ";
    public static final String WRONG_COORDINATES = "�������� ������ ������� ���������. ������� ����������, ��������� ��������� ������: \"(21.1; 75)\". ";
    public static final String INPUT_ERROR_LOG = "������ ��� ������� ������ �������� �����. ���������: ";
    public static final String INPUT_ERROR_RESULT = "������ ��� ������� ������ �������� �����. ����� ��������� ���������� ����� ����� � ����� test.log. ";
    
    public static String shelfNotFound(String name) {
        return "�� ������� ����� ������ c ��������� \"" + name + "\". �������� ������ � ������ ��������� � ����� � ���������� ������ ��� ��������� ��������. ";
    }
    
    public static String errorLine(int lineNumber, String filename) {
        return "��������� ������ " + lineNumber + " � ����� " + filename + ". ";
    }
    
    public static String wrongFormatOfFile(String filename) {
        return "�������� ������ ����� " + filename + ". ";
    }
    
    public static String wrongTop_LeftAndBottom_RightCoordinates(int row) {
        return "�������� ������� ����� ������� � ������ ������ ������� " + row + " ����. ";
    }
    
    public static String wrongNumberOfShelfs(int row) {
        return "�������� ������� ����� ����� � ���� " + row + ". ";
    }
    
    public static String wrongCell(int lineNumber, int row, String filename) {
        return "��������� ������, ����������� � ������ " + lineNumber + " � � ������� " + row + " ����� " + filename + ". ";
    }
    
    public static String wrongNumberOfShelfs(int row, String filename) {
        return "��������� ����� ����� � ���� " + row + " � ����� " + filename + ". ";
    }
}

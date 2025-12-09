package component;

import java.io.Serializable;

public enum NumberRome implements Serializable {
    // הגדרת הקבועים והערך המספרי המשויך להם (1-based)
    I(1),
    II(2),
    III(3),
    IV(4),
    V(5);

    private final int value; // שדה פרטי שיחזיק את הערך המספרי

    // קונסטרוקטור
    NumberRome(int value) {
        this.value = value;
    }

    // 1. המרה מתו רומי למספר שלם (Integer)
    /**
     * Retrieves the integer value associated with the Roman numeral constant.
     * @return The integer value (1-5).
     */
    public int toInt() {
        return value;
    }

    // 2. המרה ממספר שלם לתו רומי (NumerRome enum)
    /**
     * Converts an integer value (1-5) to the corresponding Roman numeral enum constant.
     * @param value The integer to convert.
     * @return The matching NumerRome constant.
     * @throws IllegalArgumentException if the value is out of range.
     */
    public static NumberRome fromInt(int value) {
        for (NumberRome numeral : NumberRome.values()) {
            if (numeral.value == value) {
                return numeral;
            }
        }
        throw new IllegalArgumentException("Invalid Roman numeral value: " + value + ". Must be between 1 and " + NumberRome.values().length);
    }
}
package com.s23010664.unimate;

/**
 * Represents a single activity item (event, lecture, or assignment)
 * used for displaying in lists across the app.
 */
public class ActivityItem {
    private String type;   // "event", "lecture", or "assignment"
    private int id;        // Row ID from the database
    private String name;   // Display name/title
    private String date;   // Date string (YYYY-MM-DD)
    private String time;   // Time string

    public ActivityItem(String type, int id, String name, String date, String time) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getType() { return type; }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }

    /**
     * Returns a display label like "[Event]", "[Lecture]", or "[Assignment]".
     */
    public String getTypeLabel() {
        switch (type) {
            case "event": return "[Event]";
            case "lecture": return "[Lecture]";
            case "assignment": return "[Assignment]";
            default: return "[Activity]";
        }
    }
}

package kfs.golem.scenes.data;

public class BubbleData {
    public String id;           // "dialog1"
    public Integer dialogIndex; // optional (null pokud chybí)
    public String type;         // "dialog" / "narration"
    public String speaker;      // optional
    public String text;

    public String textColor;    // optional "#ffffffff"
    public String bubbleColor;  // optional "#000000ff"

    public Float initDelay;     // optional
    public Float duration;
}

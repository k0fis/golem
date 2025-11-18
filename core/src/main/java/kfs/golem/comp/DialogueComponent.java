package kfs.golem.comp;

import kfs.golem.ecs.KfsComp;

public class DialogueComponent implements KfsComp {
    public String[] lines;       // jednotlivé věty dialogu
    public int index = 0;        // aktuální věta
    public boolean active = false;  // jestli je dialog aktivní
    public float timeVisible = 0;   // čas, jak dlouho je řádek zobrazený
}

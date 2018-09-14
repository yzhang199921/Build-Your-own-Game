package byog.Core;

import java.io.Serializable;

public class SGame implements Serializable {
    private String commands;
    private boolean game;
    private String seedValue;
    private String name;

    public SGame(String c, boolean g, String s, String n) {
        commands = c;
        game = g;
        seedValue = s;
        this.name = n;
    }

    public String getCommands() {
        return commands;
    }


    public String getSeedValue() {
        return seedValue;
    }

    public String getName() {
        return name;
    }
}

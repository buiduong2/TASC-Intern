package com.log;

import java.util.List;

public class Chunk {
    private int index;
    private List<String> lines;

    public Chunk(int index, List<String> lines) {
        this.index = index;
        this.lines = lines;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

}

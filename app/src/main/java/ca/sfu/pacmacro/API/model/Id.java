package ca.sfu.pacmacro.API.model;

/**
 * ID data structure for use with the API
 */
public class Id {
    private int id;

    public Id(int id) {
        this.id = id;
    }

    public int getIdAsInt() {
        return id;
    }
}

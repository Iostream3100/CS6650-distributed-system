package server;

import java.io.Serializable;

/**
 * proposed id.
 */
public class ProposeId implements Comparable, Serializable {
    // increasing number
    public final int number;

    // unique server id
    public final int serverId;


    /**
     * a new proposed id with value
     * @param number number
     * @param serverId server id
     */
    public ProposeId(int number, int serverId) {
        this.number = number;
        this.serverId = serverId;
    }


    @Override
    public int compareTo(Object o) {
        ProposeId other = (ProposeId) o;
        return this.number != other.number ?
                this.number - other.number :
                this.serverId - other.serverId;
    }

    @Override
    public String toString() {
        return String.format("[Number: %d, serverID: %d]", number, serverId);
    }

    @Override
    public boolean equals(Object obj) {
        return number == ((ProposeId) obj).number
                && serverId == ((ProposeId) obj).serverId;
    }
}

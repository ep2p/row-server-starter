package labs.psychogen.row.repository;

import labs.psychogen.row.RowEndpoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetEndpointRepository implements EndpointRepository {
    private final Set<RowEndpoint> set;

    public SetEndpointRepository() {
        this.set = new HashSet<>();
    }


    @Override
    public void addEndpoint(RowEndpoint rowEndpoint) {
        set.add(rowEndpoint);
    }

    @Override
    public void remove(RowEndpoint rowEndpoint) {
        set.remove(rowEndpoint);
    }

    @Override
    public Collection<RowEndpoint> getAll() {
        return set;
    }
}

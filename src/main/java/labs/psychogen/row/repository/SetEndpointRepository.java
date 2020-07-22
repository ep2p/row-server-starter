package labs.psychogen.row.repository;

import labs.psychogen.row.RowEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SetEndpointRepository implements EndpointRepository {
    private final Set<RowEndpoint> set;

    public SetEndpointRepository() {
        this.set = new HashSet<>();
    }


    @Override
    public void addEndpoint(RowEndpoint rowEndpoint) {
        set.add(rowEndpoint);
        log.debug("Registered endpoint "+ rowEndpoint.getFinalAddress() + " ["+rowEndpoint.getRowMethod().getName()+"]");
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

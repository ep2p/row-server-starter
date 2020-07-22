package labs.psychogen.row.filter;

import labs.psychogen.row.domain.protocol.RequestDto;
import labs.psychogen.row.domain.protocol.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
public class RowFilterChain {
    private List<RowFilter> filters;

    public RowFilterChain(List<RowFilter> filters) {
        this.filters = filters;
        Assert.notNull(filters, "Filters cant be null");
        Assert.isTrue(filters.size() > 0, "Initial filter size cant be 0");
    }

    public void addFilter(RowFilter rowFilter){
        filters.add(rowFilter);
    }

    public boolean hasFilter(Class c){
        for (RowFilter filter : filters) {
            if (filter.getClass().equals(c)) {
                return true;
            }
        }
        return false;
    }

    public void addFilterBefore(RowFilter rowFilter, Class before){
        for (RowFilter filter : filters) {
            if(filter.getClass().equals(before)){
                int i = filters.indexOf(filter);
                filters.add( i, rowFilter);
                log.info("added filter " + rowFilter + " before "+ before);
                return;
            }
        }
    }

    public void addFilterAfter(RowFilter rowFilter, Class after){
        for (RowFilter filter : filters) {
            if(filter.getClass().equals(after)){
                int i = filters.indexOf(filter);
                filters.add( i+1, rowFilter);
                log.info("added filter " + rowFilter + " after "+ after);
                return;
            }
        }
    }

    public void filter(RequestDto requestDto, ResponseDto response, WebSocketSession session) throws Exception {
        for (RowFilter filter : filters) {
            if (!filter.filter(requestDto, response, session)) {
                return;
            }
        }
    }
}

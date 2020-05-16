package labs.psychogen.row;

import labs.psychogen.row.controller.MyDto;
import org.springframework.stereotype.Service;

@Service
public class MyService {
    public void alter(MyDto myDto){
        myDto.setField("altered");
    }
}

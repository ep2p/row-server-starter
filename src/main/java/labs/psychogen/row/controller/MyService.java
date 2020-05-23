package labs.psychogen.row.controller;

import org.springframework.stereotype.Service;

@Service
public class MyService {
    public void alter(MyDto myDto){
        myDto.setField("altered");
    }
}

package labs.psychogen.row.controller;

import labs.psychogen.row.RowController;
import labs.psychogen.row.RowIgnore;
import labs.psychogen.row.RowQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RowController
public class TestController {
    private final MyService myService;

    @Autowired
    public TestController(MyService myService) {
        this.myService = myService;
    }

    @PostMapping("/post")
    public @ResponseBody
    MyDto post(String fakeField, @RequestBody MyDto myDto){
        System.out.println(myDto.getField());
        myService.alter(myDto);
        return myDto;
    }

    @GetMapping("/get")
    public @ResponseBody MyDto get(@RowQuery String query){
        return new MyDto("hey");
    }

    @RowIgnore
    @GetMapping("/ignore")
    public @ResponseBody MyDto get2(@RowQuery String query){
        return new MyDto("hey");
    }

    @DeleteMapping("/delete")
    public @ResponseBody MyDto delete(){
        return new MyDto("hey");
    }

    @PutMapping("/put")
    public @ResponseBody MyDto put(){
        return new MyDto("hey");
    }

    @PatchMapping("/patch")
    public @ResponseBody MyDto patch(){
        return new MyDto("hey");
    }
}

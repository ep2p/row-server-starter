package labs.psychogen.row;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRowServer(basePackages = {"labs.psychogen.row"})
public class SpringbootRestOverWebsocketStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestOverWebsocketStarterApplication.class, args);
	}

}

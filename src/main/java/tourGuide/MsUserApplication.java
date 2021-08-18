package tourGuide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class MsUserApplication {

	public static void main(String[] args) {

		Locale.setDefault(new Locale("en", "US"));
		SpringApplication.run(MsUserApplication.class, args);
	}

}

package vg.Brando.Flores.hackathon;  // Capitalizado exacto

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "vg.Brando.Flores.hackathon.repository")
public class ProyectoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
        System.out.println("App en puerto 8085 - Swagger: http://localhost:8085/swagger-ui.html");
    }
}
package com.ucbcba.seminario.joel.erroresformatodocumentosacademicos;

import com.ucbcba.seminario.joel.erroresformatodocumentosacademicos.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class ErroresFormatoDocumentosAcademicosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErroresFormatoDocumentosAcademicosApplication.class, args);
	}

}


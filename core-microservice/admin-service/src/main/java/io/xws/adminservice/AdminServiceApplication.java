package io.xws.adminservice;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class AdminServiceApplication 
{
	//@Value("${http.client.ssl.trust-store}")
    //private Resource trustStore;

	//@Value("${http.client.ssl.trust-store-password}")
    //private String trustStorePassword;

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() throws Exception {
		Resource trustStore = new ClassPathResource("adminTrustStore.jks");
		
		SSLContext sslContext = new SSLContextBuilder()
			      .loadTrustMaterial(trustStore.getURL(), "secretpassword".toCharArray())
			      .build();
			    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
			    HttpClient httpClient = HttpClients.custom()
			      .setSSLSocketFactory(socketFactory)
			      .build();
			    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
			    return new RestTemplate(factory);
	}

	public static void main(String[] args) 
	{
		SpringApplication.run(AdminServiceApplication.class, args);
	}

}

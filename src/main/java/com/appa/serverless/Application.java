package com.appa.serverless;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.catalog.FunctionTypeUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;

import com.appa.serverless.model.Response;
import com.appa.serverless.repository.ShakeProductRepository;
import com.appa.serverless.repository.ShakeProductRepositoryImpl;
import com.appa.serverless.repository.OrderRepository;
import com.appa.serverless.repository.OrderRepositoryImpl;
import com.appa.serverless.repository.UserRepository;
import com.appa.serverless.repository.UserRepositoryImpl;

//https://docs.spring.io/spring-cloud-function/docs/current/reference/html/aws.html#_functional_bean_definitions
@SpringBootApplication

public class Application implements ApplicationContextInitializer<GenericApplicationContext> {

	// private static final java.util.logging.Logger log =
	// java.util.logging.Logger.getLogger(Application.class.getName());

	@Autowired
	private ShakeProductRepository shakeProductRepository = new ShakeProductRepositoryImpl();

	@Autowired
	private UserRepository userRepository = new UserRepositoryImpl();

	@Autowired
	private OrderRepository orderRepository = new OrderRepositoryImpl();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// ----------- Product -----------

	@Bean
	Function<String, Response> getAllShake() {
		return value -> this.shakeProductRepository.getAllShake();
	}

	// ----------- Order -----------

	@Bean
	Function<String, Response> createOrder() {
		return value -> this.orderRepository.createOrder(value);
	}

	@Bean
	Function<String, Response> getOrderByUser() {
		return value -> this.orderRepository.getOrderByUser(value);
	}

	// ----------- Auth -----------

	@Bean
	Function<String, Response> login() {
		return value -> this.userRepository.login(value);
	}

	@Bean
	Function<String, Response> register() {
		return value -> this.userRepository.register(value);
	}

	@Override
	// the handler is
	// org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest
	public void initialize(GenericApplicationContext context) {

		// ----------- Product -----------

		context.registerBean(ShakeProductRepositoryImpl.class);
		context.registerBean("getAllShake", FunctionRegistration.class, () -> {
			FunctionRegistration<Function<String, Response>> registration = new FunctionRegistration<>(getAllShake());
			registration.type(FunctionTypeUtils.functionType(String.class, Response.class));
			return registration;
		});

		// ----------- Order -----------

		context.registerBean(OrderRepositoryImpl.class);
		context.registerBean("createOrder", FunctionRegistration.class, () -> {
			FunctionRegistration<Function<String, Response>> registration = new FunctionRegistration<>(createOrder());
			registration.type(FunctionTypeUtils.functionType(String.class, Response.class));
			return registration;
		});
		context.registerBean("getOrderByUser", FunctionRegistration.class, () -> {
			FunctionRegistration<Function<String, Response>> registration = new FunctionRegistration<>(
					getOrderByUser());
			registration.type(FunctionTypeUtils.functionType(String.class, Response.class));
			return registration;
		});

		// ----------- Auth -----------

		context.registerBean(UserRepositoryImpl.class);
		context.registerBean("login", FunctionRegistration.class, () -> {
			FunctionRegistration<Function<String, Response>> registration = new FunctionRegistration<>(login());
			registration.type(FunctionTypeUtils.functionType(String.class, Response.class));
			return registration;
		});
		context.registerBean("register", FunctionRegistration.class, () -> {
			FunctionRegistration<Function<String, Response>> registration = new FunctionRegistration<>(register());
			registration.type(FunctionTypeUtils.functionType(String.class, Response.class));
			return registration;
		});

	}

}

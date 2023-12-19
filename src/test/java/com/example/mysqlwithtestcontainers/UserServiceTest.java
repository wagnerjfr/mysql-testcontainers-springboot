package com.example.mysqlwithtestcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.mysqlwithtestcontainers.util.TestUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {
	@ClassRule
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest");
	static UserService userService;
	static ConnectionPool connectionPool;
	static TestUtils testUtils;

	@BeforeAll
	static void startDb() throws SQLException {
		mySQLContainer.start();
		String url = mySQLContainer.getJdbcUrl();
		connectionPool =  new ConnectionPool(url, mySQLContainer.getUsername(), mySQLContainer.getPassword());
		testUtils = new TestUtils(connectionPool);
		testUtils.setUpData();
		userService = new UserService(connectionPool);
	}

	@AfterAll
	static void stopDb(){
		mySQLContainer.stop();
	}

	@Test
	public void containerRunning(){
		// Assert whether the container is running
		assertTrue(mySQLContainer.isRunning());
	}

	@Test
	public void getUser() throws SQLException {
		// Generate a random user using test utilities
		User testUser = testUtils.getRandomUser();

		// Call the userService to retrieve a user based on the generated user's ID
		User user = userService.getUser(testUser.getId());

		// Assert that the retrieved user's ID matches the generated user's ID
		assertEquals(testUser.getId(), user.getId());

		// Assert that the retrieved user's name matches the generated user's name
		assertEquals(testUser.getName(), user.getName());
	}

	@Test
	public void createUser() throws SQLException {
		// Generate a random string for the test user's name
		String testName = testUtils.getRandomString(10);

		// Create a new User object with the generated name
		User testUser = new User(testName);

		// Call the userService to create a new user and get the result
		User newUser = userService.createUser(testUser);

		// Assert that the created user is not null
		assertNotNull(newUser);

		// Retrieve the user from the userService based on the newly created user's ID
		User user = userService.getUser(newUser.getId());

		// Assert that the retrieved user's name matches the originally generated name
		assertEquals(testName, user.getName());
	}
}

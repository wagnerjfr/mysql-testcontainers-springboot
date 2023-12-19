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
class MysqlWithTestcontainersApplicationTests {
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
		userService = new UserService( connectionPool);
	}

	@AfterAll
	static void stopDb(){
		mySQLContainer.stop();
	}

	@Test
	public void containerRunning(){
		assertTrue(mySQLContainer.isRunning());
	}

	@Test
	public void getUser() throws SQLException {
		User testUser = testUtils.getRandomUser();
		User user = userService.getUser(testUser.getId());
		assertEquals(testUser.getId(), user.getId());
		assertEquals(testUser.getName(), user.getName());
	}

	@Test
	public void createUser() throws SQLException {
		String testName = testUtils.getRandomString(10);
		User testUser = new User(testName);
		User newUser = userService.createUser(testUser);
		assertNotNull(newUser);
		User user = userService.getUser(newUser.getId());
		assertEquals(testName, user.getName());
	}
}

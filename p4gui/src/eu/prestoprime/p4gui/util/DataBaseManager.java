/**
 * DataBaseManager.java
 * Author: Francesco Rosso (rosso@eurix.it)
 * Contributors: Francesco Gallo (gallo@eurix.it)
 * 
 * This file is part of PrestoPRIME Preservation Platform (P4).
 * 
 * Copyright (C) 2009-2012 EURIX Srl, Torino, Italy
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.prestoprime.p4gui.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.prestoprime.p4gui.P4GUI;
import eu.prestoprime.p4gui.P4GUI.P4guiProperty;
import eu.prestoprime.p4gui.model.P4Service;
import eu.prestoprime.p4gui.model.User;

public class DataBaseManager {

	private static DataBaseManager instance;
	private static String dbHome = P4GUI.getProperty(P4guiProperty.DERBY_HOME);

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Connection connection;

	public static DataBaseManager getInstance() {
		if (instance == null)
			instance = new DataBaseManager();
		return instance;
	}

	protected DataBaseManager() {
		try {
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			Class.forName(driver).newInstance();

			connection = DriverManager.getConnection("jdbc:derby:" + dbHome + ";create=true");

			logger.debug("DataBaseManager instance created properly");

			try {
				this.createTables();
				this.deleteUser("pprime");

				this.addUser("pprime", "p4admin@eurixgroup.com");
				this.activateUser("pprime", "pprime", "P4Admin", "euriX", "+39 011 2303729", "via G. Carcano, 26 - 10100 Torino");
				this.addP4Service("pprime", new URL("https://localhost/p4ws"), "pprime");
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("Unable to create default superadmin user...");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				logger.error("Unable to add static p4service for superadmin user... Check Java code...");
			}

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("Unable to load database driver...");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to instantiate a connection with the database...");
		}

	}

	public void createTables() throws SQLException {
		try {
			connection.setAutoCommit(false);

			try (PreparedStatement createUsersTable = connection.prepareStatement("CREATE TABLE users(username varchar(40) PRIMARY KEY, password varchar(40) NOT NULL, email varchar(50), activated smallint, name varchar(100), institution varchar(100), phone varchar(40), address varchar(100))");) {

				createUsersTable.executeUpdate();
			} catch (SQLException e) {
				logger.debug("Unable to overwrite table USERS...");
				connection.rollback();
				return;
			}

			try (PreparedStatement createP4ServicesTable = connection.prepareStatement("CREATE TABLE p4service(username varchar(40) CONSTRAINT P4SERVICE_USERNAME_FK REFERENCES users(username), url varchar(100), userID varchar(100) NOT NULL)");) {

				createP4ServicesTable.executeUpdate();
			} catch (SQLException e) {
				logger.debug("Unable to overwrite table P4SERVICE...");
				connection.rollback();
				return;
			}

			connection.commit();

		} finally {
			connection.setAutoCommit(true);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.stopDataBase();
	}

	public void stopDataBase() throws SQLException {
		connection.close();

		DriverManager.getConnection("jdbc:derby:" + dbHome + ";shutdown=true");
		instance = null;

		logger.debug("DB stopped");
	}

	/**
	 * Checks if the specified username is available.
	 */
	public synchronized boolean isUsernameAvailable(String username) throws SQLException {

		try (PreparedStatement countUsername = connection.prepareStatement("SELECT count(*) AS count FROM users WHERE username=?");) {

			connection.setAutoCommit(false);

			countUsername.setString(1, username);

			ResultSet res = countUsername.executeQuery();
			if (res.next()) {
				if (res.getInt("count") == 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}

		return true;
	}

	/**
	 * Returns the complete list of registered usernames.
	 */
	public synchronized List<String> getRegisteredUsernames() throws SQLException {
		List<String> usernames = new ArrayList<>();

		try (PreparedStatement registeredUsers = connection.prepareStatement("SELECT username FROM users");) {

			connection.setAutoCommit(false);

			ResultSet res = registeredUsers.executeQuery();
			while (res.next()) {
				usernames.add(res.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}

		return usernames;
	}

	/**
	 * Adds a new user to the GUI database.
	 */
	public synchronized void addUser(String username, String email) throws SQLException {
		try (PreparedStatement addUserStatement = connection.prepareStatement("INSERT INTO users(username, password, email, activated) VALUES (?, ?, ?, ?)");) {

			connection.setAutoCommit(false);

			if (!this.isUsernameAvailable(username)) {
				throw new SQLException("Username not available...");
			}

			addUserStatement.setString(1, username);
			addUserStatement.setString(2, "changeit");
			addUserStatement.setString(3, email);
			addUserStatement.setBoolean(4, false);

			if (addUserStatement.executeUpdate() == 1) {
				connection.commit();

				logger.debug("User " + username + " added...");
			} else {
				connection.rollback();

				logger.debug("User " + username + " couldn't be added...");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}
	}

	/**
	 * Retrieves the specified user.
	 */
	public synchronized User getUserByUsername(String username) throws SQLException {

		try (PreparedStatement getUserByUsername = connection.prepareStatement("SELECT * FROM users WHERE username=?");) {

			connection.setAutoCommit(false);

			getUserByUsername.setString(1, username);

			ResultSet res = getUserByUsername.executeQuery();
			if (res.next()) {
				return new User(res.getString("username"), res.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}

		return null;
	}

	/**
	 * Checks if valid credentials.
	 * 
	 * @return The User object representing current user
	 */
	public synchronized User getUserByUsernameAndPassword(String username, String password) throws SQLException {

		try (PreparedStatement loginUser = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=? AND activated<>0");) {
			connection.setAutoCommit(false);

			loginUser.setString(1, username);
			loginUser.setString(2, password);

			ResultSet res = loginUser.executeQuery();
			if (res.next()) {
				User user = new User(res.getString("username"), res.getString("email"));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}

		return null;
	}

	/**
	 * Retrieves the user from hashCode.
	 * 
	 * @return The username.
	 */
	public synchronized User getUserByHashCode(int hashCode) throws SQLException {

		try (PreparedStatement getUserByHashCode = connection.prepareStatement("SELECT * FROM users WHERE activated=0");) {

			connection.setAutoCommit(false);

			ResultSet res = getUserByHashCode.executeQuery();
			while (res.next()) {
				if (res.getString("username").hashCode() == hashCode) {
					return new User(res.getString("username"), res.getString("email"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.setAutoCommit(true);
		}

		return null;
	}

	/**
	 * Activates an already present user.
	 */
	public synchronized void activateUser(String username, String password, String name, String institution, String phone, String address) throws SQLException {

		try (PreparedStatement activateAccount = connection.prepareStatement("UPDATE users SET activated=1, password=?, name=?, institution=?, phone=?, address=? WHERE username=?");) {

			connection.setAutoCommit(false);

			activateAccount.setString(1, password);
			activateAccount.setString(2, name);
			activateAccount.setString(3, institution);
			activateAccount.setString(4, phone);
			activateAccount.setString(5, address);
			activateAccount.setString(6, username);

			if (activateAccount.executeUpdate() == 1) {
				connection.commit();

				logger.debug("User " + username + " activated...");
			} else {
				connection.rollback();

				logger.debug("User " + username + " couldn't be activated...");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}
	}

	/**
	 * Deletes the user and its P4 Services.
	 */
	public synchronized void deleteUser(String username) throws SQLException {

		try (PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE username=?"); PreparedStatement deleteAllP4Services = connection.prepareStatement("DELETE FROM p4service WHERE username=?");) {

			connection.setAutoCommit(false);

			deleteAllP4Services.setString(1, username);

			if (deleteAllP4Services.executeUpdate() == 0) {
				logger.debug("No P4Services for user " + username + " to be deleted...");
			} else {
				logger.debug("All P4Services for user" + username + " deleted...");
			}

			deleteUser.setString(1, username);
			if (deleteUser.executeUpdate() == 0) {
				connection.rollback();

				logger.debug("User " + username + " couldn't be deleted...");
			} else {
				connection.commit();

				logger.debug("Deleted user " + username + "...");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}
	}

	/**
	 * Adds a new P4 Service to a specific user. If user already has defined a
	 * service with the same URL, it updates the userID to the new one.
	 */
	public synchronized void addP4Service(String username, URL url, String userID) throws SQLException {

		try (PreparedStatement updateP4Service = connection.prepareStatement("UPDATE p4service SET userID=? WHERE username=? AND url=?"); PreparedStatement addP4Service = connection.prepareStatement("INSERT INTO p4service(username, url, userID) VALUES(?, ?, ?)");) {

			connection.setAutoCommit(false);

			updateP4Service.setString(1, userID);
			updateP4Service.setString(2, username);
			updateP4Service.setString(3, url.toString());

			if (updateP4Service.executeUpdate() == 0) {
				addP4Service.setString(1, username);
				addP4Service.setString(2, url.toString());
				addP4Service.setString(3, userID);
				if (addP4Service.executeUpdate() == 0) {
					connection.rollback();

					logger.debug("P4Service " + url + " couldn.t be added for user " + username + "...");
				} else {
					connection.commit();

					logger.debug("P4Service " + url + " added for user " + username + "...");
				}
			} else {
				connection.commit();

				logger.debug("P4Service " + url + " updated for user " + username + "...");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}
	}

	/**
	 * Returns the list of available P4 Services for specified user.
	 */
	public synchronized List<P4Service> getP4Services(String username) throws SQLException {

		List<P4Service> p4services = new ArrayList<>();

		try (PreparedStatement getP4Services = connection.prepareStatement("SELECT * FROM p4service WHERE username=?");) {

			connection.setAutoCommit(false);

			getP4Services.setString(1, username);

			ResultSet res = getP4Services.executeQuery();
			while (res.next()) {
				try {
					p4services.add(new P4Service(new URL(res.getString("url")), res.getString("userID")));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}

		return p4services;
	}
}
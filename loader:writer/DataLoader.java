package javaFiles;

import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.UUID;

/**
 * @author Gavin Humphries
 * @author Ryan Caudill
 *         DataLoader class is used to retrieve data from JSON files
 *         method for projects.json and users.json
 */
public class DataLoader extends DataConstants {

	/**
	 * getUsers function retrieves user data from Users.json
	 * 
	 * @return arraylist of users
	 */
	public static ArrayList<User> getUsers() {//////////////////////////////////////////////////////////
		ArrayList<User> users = new ArrayList<User>();

		try {
			FileReader reader = new FileReader(USER_FILE_NAME);
			JSONParser parser = new JSONParser();
			JSONArray usersJSON = (JSONArray) parser.parse(reader);

			for (int i = 0; i < usersJSON.size(); i++) {
				JSONObject userJSON = (JSONObject) usersJSON.get(i);
				String firstName = (String) userJSON.get(USER_FIRST_NAME);
				String lastName = (String) userJSON.get(USER_LAST_NAME);
				String email = (String) userJSON.get(USER_EMAIL);
				String password = (String) userJSON.get(USER_PASSWORD);
				UUID id = UUID.fromString((String) userJSON.get(USER_ID));
				boolean isMaster = (boolean) userJSON.get(USER_ISMASTER);
				Long lPoints = (Long) userJSON.get(USER_POINTS);
				int points = lPoints.intValue();

				users.add(new User(email, firstName, lastName, isMaster, password, id, points));
			}

			return users;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * getProjects retrieves project data from Projects.json
	 * 
	 * @return arraylist of projects
	 */
	public static ArrayList<Project> getProjects() {///////////////////////////////////////////////////
		ArrayList<Project> projects = new ArrayList<Project>();
		UserList ul = UserList.getInstance();

		try {
			FileReader reader = new FileReader(PROJECT_FILE_NAME);
			JSONParser parser = new JSONParser();
			JSONArray projectsJSON = (JSONArray) parser.parse(reader);

			for (int i = 0; i < projectsJSON.size(); i++) {
				JSONObject projectJSON = (JSONObject) projectsJSON.get(i);
				JSONArray jcolumns = (JSONArray) projectJSON.get(PROJECT_COLUMNS);
				ArrayList<Column> columns = toColumn(jcolumns);

				String name = (String) projectJSON.get(PROJECT_NAME);

				String sScrumMaster = (String) projectJSON.get(PROJECT_MASTER);
				User scrumMaster = ul.getUserByUUID(UUID.fromString(sScrumMaster));

				JSONArray jDevelopers = (JSONArray) projectJSON.get(PROJECT_DEVELOPERS);
				ArrayList<String> sDevelopers = new ArrayList<String>();
				for (Object j : jDevelopers) {
					sDevelopers.add((String) j);
				}

				ArrayList<User> developers = new ArrayList<User>();

				for (String sUser : sDevelopers) {
					developers.add(ul.getUserByUUID(UUID.fromString(sUser)));
				}

				projects.add(new Project(columns, name, scrumMaster, developers));
			}

			return projects;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * toColumn is a helper function for getProjects to convert
	 * Json array of columns to arraylist of columns
	 * 
	 * @param JSONArray of column json data
	 * @return arraylist of columns
	 */
	private static ArrayList<Column> toColumn(JSONArray array) {
		ArrayList<Column> returnArray = new ArrayList<Column>();
		JSONObject temp;
		String name;
		JSONArray jtasks;
		ArrayList<Task> tasks;
		for (int j = 0; j < array.size(); j++) {
			temp = (JSONObject) array.get(j);
			name = (String) temp.get(COLUMN_NAME);
			jtasks = (JSONArray) temp.get(COLUMN_TASKS);
			tasks = toTasks(jtasks);
			returnArray.add(new Column(name, tasks));
		}
		return returnArray;
	}

	/**
	 * helper function for toColumns to convert task
	 * json array to arraylist of tasks
	 * 
	 * @param JSONArray of task json data
	 * @return arraylist of tasks
	 */
	private static ArrayList<Task> toTasks(JSONArray array) {
		ArrayList<Task> returnArray = new ArrayList<Task>();
		JSONObject temp;
		JSONArray jusers, jcomments;
		ArrayList<User> users;
		ArrayList<Comment> comments;
		String title, tempDate;
		int priority, leaderboardPoints;
		Long tempint;
		Date date = null;
		for (int j = 0; j < array.size(); j++) {
			temp = (JSONObject) array.get(j);
			title = (String) temp.get(TASK_TITLE);
			jusers = (JSONArray) temp.get(TASK_USERS);
			users = toUsers(jusers);
			jcomments = (JSONArray) temp.get(TASK_COMMENTS);
			comments = toComments(jcomments);
			tempint = (Long) temp.get(TASK_PRIORITY);
			priority = tempint.intValue();
			tempint = (Long) temp.get(TASK_POINTS);
			leaderboardPoints = tempint.intValue();
			tempDate = (String) temp.get(TASK_DUE_DATE);
			try {
				date = FORMAT.parse(tempDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			returnArray.add(new Task(title, comments, users, priority, date, leaderboardPoints));
		}
		return returnArray;
	}

	/**
	 * helper function for toTasks to convert jsonarray
	 * of users to arraylist of users
	 * 
	 * @param JSONarray of users
	 * @return arraylist of users
	 */
	private static ArrayList<User> toUsers(JSONArray array) {
		ArrayList<User> returnArray = new ArrayList<User>();
		for (int i = 0; i < array.size(); i++) {
			returnArray.add(toUser((String) array.get(i)));
		}
		return returnArray;
	}

	/**
	 * helper function for toComments and toUser to convert
	 * user UUID to actual user from users.json
	 * 
	 * @param juser UUID in string form
	 * @return user matching UUID
	 */
	private static User toUser(String juser) {
		UserList ul = UserList.getInstance();
		User user = ul.getUserByUUID(UUID.fromString(juser));
		return user;
	}

	/**
	 * toComments recursive helper fucntion to convert comment json array
	 * to arraylist of comments
	 * 
	 * @param JSONArray of comments
	 * @return arraylist of comments
	 */
	private static ArrayList<Comment> toComments(JSONArray array) {
		ArrayList<Comment> returnArray = new ArrayList<Comment>();
		String body, tempDate;
		JSONArray jcomments;
		ArrayList<Comment> nestComments;
		User user;
		JSONObject temp;
		Date date = null;
		for (int i = 0; i < array.size(); i++) {
			temp = (JSONObject) array.get(i);
			body = (String) temp.get(COMMENT_BODY);
			user = toUser((String) temp.get(COMMENT_USER));
			jcomments = (JSONArray) temp.get(COMMENT_COMMENTS);
			nestComments = toComments(jcomments);
			tempDate = (String) temp.get(COMMENT_DATE);
			try {
				date = FORMAT.parse(tempDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			returnArray.add(new Comment(body, user, nestComments, date));
		}
		return returnArray;
	}

	/*
	 * public static void main(String[] args) {
	 * ArrayList<Project> projects = getProjects();
	 * for (Project p : projects) {
	 * ArrayList<Column> columns = p.getColumns();
	 * for (Column c : columns) {
	 * ArrayList<Task> tasks = c.getTasks();
	 * for (Task t : tasks) {
	 * System.out.println("Task date: " + FORMAT.format(t.getDate()));
	 * ArrayList<Comment> comments = t.getComments();
	 * for (Comment x : comments) {
	 * System.out.println();
	 * System.out.println("Comment date: " + FORMAT.format(x.getdateOfComment()));
	 * }
	 * }
	 * }
	 * }
	 * }
	 */
}
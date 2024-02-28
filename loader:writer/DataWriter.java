package javaFiles;

import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Datawriter uses DataConstants and saves projects and users
 * to Projects.json and Users.json
 * 
 * @author Ryan Caudill
 */

public class DataWriter extends DataConstants {

  /**
   * Saves all user data in UserList to Users.json
   * 
   * @return boolean showing it is complete
   */
  public static boolean saveUsers() {/////////////////////////////////////////////////////////
    UserList userList = UserList.getInstance();
    ArrayList<User> users = userList.getUsers();
    JSONArray jsonUsers = new JSONArray();
    ArrayList<User> compare = DataLoader.getUsers();

    for (int i = 0; i < users.size(); i++) {
      if (compare.contains(users.get(i))) {
        continue;
      }
      jsonUsers.add(getUserJSON(users.get(i)));
    }

    try (FileWriter file = new FileWriter(USER_FILE_NAME)) {
      file.write(jsonUsers.toJSONString());
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return true;
  }

  /**
   * Helper function for saveUsers to convert each user to JSON format
   * 
   * @param user to convert to JSON object
   * @return JSONObject of user
   */
  private static JSONObject getUserJSON(User user) {
    JSONObject userDetails = new JSONObject();
    userDetails.put(USER_PASSWORD, user.getPassword());
    userDetails.put(USER_EMAIL, user.getEmail());
    userDetails.put(USER_FIRST_NAME, user.getFirstName());
    userDetails.put(USER_LAST_NAME, user.getLastName());
    userDetails.put(USER_POINTS, user.getPoints());
    userDetails.put(USER_ISMASTER, user.getIsMaster());
    userDetails.put(USER_ID, user.getID().toString());
    return userDetails;
  }

  /**
   * Saves all project data to projects.json
   * 
   * @return boolean meaning this completed
   */
  public static boolean saveProjects() {///////////////////////////////////////////////////////////
    ProjectList projectList = ProjectList.getInstance();
    ArrayList<Project> projects = projectList.getProjects();

    // Convert arraylist to JSONArray
    JSONArray jsonProjects = new JSONArray();

    for (int i = 0; i < projects.size(); i++) {
      jsonProjects.add(getProjectJSON(projects.get(i)));
    }

    try (FileWriter file = new FileWriter(PROJECT_FILE_NAME)) {
      file.write(jsonProjects.toJSONString());
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * helper functions for saveProjects to convert a project
   * to JSONObject form
   * 
   * @param project to convert
   * @return JSON object of project
   */
  private static JSONObject getProjectJSON(Project project) {
    ArrayList<Column> columns = project.getColumns();
    // Make JSONArray of object arraylists
    JSONArray JSONcolumns = new JSONArray();
    JSONArray JSONdevelopers = new JSONArray();
    for (Column c : columns) {
      JSONcolumns.add(getColumnJSON(c));
    }
    ArrayList<User> users = project.getDevelopers();
    for (User u : users) {
      JSONdevelopers.add(u.getID().toString());
    }
    // Make object to return
    JSONObject projectDetails = new JSONObject();
    projectDetails.put(PROJECT_COLUMNS, JSONcolumns);
    projectDetails.put(PROJECT_NAME, project.getName());
    projectDetails.put(PROJECT_MASTER, project.getMaster().getID().toString());
    projectDetails.put(PROJECT_DEVELOPERS, JSONdevelopers);
    return projectDetails;
  }

  /**
   * Helper function for getPorjectJSON to convert each column to JSON
   * object form
   * 
   * @param column to convert
   * @return JSONObject of column
   */
  private static JSONObject getColumnJSON(Column column) {
    ArrayList<Task> tasks = column.getTasks();
    // Make JSONArray of object arraylist
    JSONArray JSONtasks = new JSONArray();
    for (Task t : tasks) {
      JSONtasks.add(getTaskJSON(t));
    }
    // Make object to return
    JSONObject returnJSON = new JSONObject();
    returnJSON.put(COLUMN_NAME, column.getName());
    returnJSON.put(COLUMN_TASKS, JSONtasks);
    return returnJSON;
  }

  /**
   * Helper function for getColumnJSON to convert each task to JSON
   * object form
   * 
   * @param task to convert
   * @return JSONobejct of the task
   */
  private static JSONObject getTaskJSON(Task task) {
    ArrayList<Comment> comments = task.getComments();
    ArrayList<User> users = task.getAssignedUsers();
    // Make JSONArrays of object arrayLists
    JSONArray JSONcomments = new JSONArray();
    if (comments != null) {
      for (Comment c : comments) {
        JSONcomments.add(getCommentJSON(c));
      }
    }

    JSONArray JSONusers = new JSONArray();
    if (users != null) {
      for (User u : users) {
        JSONusers.add(u.getID().toString());
      }
    }
    // Make object to return
    JSONObject returnJSON = new JSONObject();
    returnJSON.put(TASK_TITLE, task.getTitle());
    returnJSON.put(TASK_COMMENTS, JSONcomments);
    returnJSON.put(TASK_USERS, JSONusers);
    returnJSON.put(TASK_PRIORITY, task.getPriority());
    returnJSON.put(TASK_POINTS, task.getPoints());
    returnJSON.put(TASK_DUE_DATE, FORMAT.format(task.getDate()));
    Date temp = task.getDate();
    String date = FORMAT.format(temp);
    returnJSON.put(TASK_DUE_DATE, date);
    return returnJSON;
  }

  /**
   * Recursive helper function to convert a comment to JSON OBject form
   * 
   * @param comment to convert
   */
  private static JSONObject getCommentJSON(Comment comment) {
    ArrayList<Comment> comments = comment.getComments();
    // Make JSONArray of nested comments
    JSONArray JSONcomments = new JSONArray();
    if (comments != null) {
      for (Comment c : comments) {
        JSONcomments.add(getCommentJSON(c));
      }
    }

    // Make object to return
    JSONObject returnJSON = new JSONObject();
    returnJSON.put(COMMENT_BODY, comment.getBody());
    returnJSON.put(COMMENT_USER, comment.getUser().getID().toString());
    returnJSON.put(COMMENT_COMMENTS, JSONcomments);
    Date temp = comment.getdateOfComment();
    String date = FORMAT.format(temp);
    returnJSON.put(COMMENT_DATE, date);
    return returnJSON;
  }

}

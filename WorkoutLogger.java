package ArnoldTracker;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
public class WorkoutLogger {

    public static void main(String[] args)
    {
        String url = "jdbc:postgresql://localhost:5432/arnold_tracker";
        String user = "bradypierce";
        String password = "Casey6557!!";
        Scanner scan = new Scanner(System.in);
        try{
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database Connected Successfully!");
            System.out.println("What split are you running today: ");
            String splitInput = scan.nextLine();
            String createWorkoutSQL = "INSERT INTO workouts (split_day) VALUES (?)";
            PreparedStatement workoutStmt = conn.prepareStatement(createWorkoutSQL, Statement.RETURN_GENERATED_KEYS);
            workoutStmt.setString(1, splitInput);
            workoutStmt.executeUpdate();
            ResultSet rsKeys = workoutStmt.getGeneratedKeys();
            int currentWorkoutID = 0;
            if(rsKeys.next())
            {
                currentWorkoutID = rsKeys.getInt(1);
            }
            Statement s1 = conn.createStatement();
            String query = "SELECT * FROM exercises;";
            ResultSet rs = s1.executeQuery(query);
            System.out.println("\n---AVAILABLE EXERCISES---");
            while(rs.next())
            {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                System.out.println(id + " - " + name);
            }
            String insertSQL = "INSERT INTO workout_sets(workout_id, exercise_id, weight_lbs, reps) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            while(true)
            {
                System.out.println("\nEnter Exercise ID to log a set(or 0 to finish): ");
                int exerciseID = scan.nextInt();
                if(exerciseID == 0)
                {
                    break;
                }
                System.out.println("Weight(lbs): ");
                int weight = scan.nextInt();
                System.out.println("Reps: ");
                int reps = scan.nextInt();
                pstmt.setInt(1, currentWorkoutID);
                pstmt.setInt(2, exerciseID);
                pstmt.setInt(3, weight);
                pstmt.setInt(4, reps);
                pstmt.executeUpdate();
                System.out.println("Logged: Exercise " + exerciseID + " - " + weight + "lbs for " + reps + " reps.");
            }
            String summarySQL = "SELECT e.name, w.weight_lbs, w.reps FROM workout_sets w JOIN exercises e ON w.exercise_id = e.id WHERE w.workout_id = ?";
            PreparedStatement summaryStmt = conn.prepareStatement(summarySQL);
            summaryStmt.setInt(1, currentWorkoutID);
            ResultSet rsSummary = summaryStmt.executeQuery();
            System.out.println("\n----WORKOUT SUMMARY----");
            while(rsSummary.next() == true)
            {
                String name = rsSummary.getString("name");
                int weight = rsSummary.getInt("weight_lbs");
                int reps = rsSummary.getInt("reps");
                System.out.println(name + ": " + weight + " lbs x " + reps + " reps");
            }
        }catch(SQLException e)
        {
            System.out.println("Connection Failed. " + e.getMessage());
        }
        
    }
}

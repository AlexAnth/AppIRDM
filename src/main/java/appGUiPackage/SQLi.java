package appGUiPackage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLi {

    public static String url = null;

    public SQLi(){
        url = "jdbc:sqlite:" + this.getClass().getResource("/spam_db.db").getPath();
        SQLi.createNewDatabase();
    }

    public static void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createBooleanTable(){
        System.out.println("Boolean table created\n");
        String sql = "CREATE TABLE IF NOT EXISTS boolean (\n"
                + "	word text NOT NULL,\n"
                + "	mail text NOT NULL,\n"
                + "	frequency integer NOT NULL,\n"
                + " PRIMARY KEY(word, mail)\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createIndexTable() {
        System.out.println("Index table created\n");
        String sql = "CREATE TABLE IF NOT EXISTS tbl_index (\n"
                + "	word text PRIMARY KEY,\n"
                + "	mail text NOT NULL\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
             // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Insert a new row into the tbl_index table
     * @param word
     * @param mail
     */
    public void insertIndexTable(String word, String mail) {
        String sql = "INSERT OR IGNORE INTO tbl_index(word,mail) VALUES(?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, mail);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertBooleanTable(String word, String mail, int frequency) {
        String sql = "INSERT OR REPLACE INTO boolean(word,mail,frequency) VALUES(?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, mail);
            pstmt.setInt(3, frequency);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String queryBooleanTable(SQLi sqLi,String word) {
        String output = null;
        List<String> items = new ArrayList<String>(Arrays.asList(word.split(" ")));
        int counter=0;
        String sql=" ";
        boolean b= false;
        for(String s : items){
            if(b==true){
                if(counter!=0) {
                    sql=sql+" INTERSECT ";
                }
                sql=sql+"SELECT mail FROM boolean_tbl WHERE word='"+s+"' and frequency=0 ";
                b=false;
                counter++;
            }else {
                if (!s.equals("NOT") && !s.equals("not")){
                    if(counter!=0) {
                        sql=sql+" INTERSECT ";
                    }
                    sql=sql+"SELECT mail FROM boolean_tbl WHERE word='"+s+"' and frequency=1 ";
                    counter++;
                }else{
                    b=true;
                }
            }
        }
        System.out.println(sql.trim());

        try (Connection conn = sqLi.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)){
            boolean empty = true;
            while (rs.next()) {
                output = "Results : "+rs.getString("mail");
                empty=false;
            }
            if(empty){
                output = "No results found on database.\n" +
                        "Note this may be due to limited records.\n" +
                        "Please enter one of the suggested queries on our report.\n";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            output= "SQLITE_ERROR";
        }
        return output;
    }

}


import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class MyTube {
    static Connection con;
    static Statement stmt;
    static PreparedStatement pstmt;
    static Scanner scanner;
    static String underbars = "---------------------------------";
    static ResultSet rs;
    static String url = "jdbc:mysql://localhost:3306/MyTube";
    static String user = "root";
    static String psw = "duftlagltkfwk1";
    static String v_title;
    static String v_length;
    static String v_count;
    static String v_like;
    static Date v_uploadDate;
    static String v_genre;
    static String isAdver;
    static String u_channelName;
    static String v_inum;
    static String u_inum;
    static String search;
    static int menu;
    static String admin_num = "1234567890";
    static String[] genres = {"Game", "Movie", "Sports", "News", "Music", "Economics", "Education", "Etc"};

    public static void main(String[] args) {
        // executequery -> select

        // executeupdate -> insert, update.
        try {
            scanner = new Scanner(System.in);
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            con = DriverManager.getConnection(url, user, psw);

            main_Mode();
            con.close();
            scanner.close();
            System.out.println("Program end!");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void showdefaultMenu(String mode) {
        System.out.println(underbars);
        System.out.println("Welcome to MyTube! This is " + mode + " mode!");
        System.out.println(underbars);
    }

    public static void main_Mode() {
        while(true) {
            showdefaultMenu("Main");
            System.out.println("This is main menu. Select Menu.");
            System.out.println(underbars);


            System.out.println("0. Ending Program");
            System.out.println("1. Administrator_Mode");
            System.out.println("2. User_Mode");
            System.out.println(underbars);

            menu = scanner.nextInt();
            if(menu == 0) {
                System.out.println("Bye! See u later!");
                scanner.close();
                return;
            }

            switch (menu) {
                case 1:
                    administrator_Mode();
                    continue;

                case 2:
                    user_Mode();
            }
        }
    }

    public static void administrator_Mode() {
        while(true) {
            showdefaultMenu("Administrator");
            System.out.println("0. Previous Menu.");
            System.out.println("1. Show user's Upload list Or Watching list.");
            System.out.println(underbars);
            menu = scanner.nextInt();

            if(menu == 0) {
                System.out.println("Go back to previous menu..");
                return;
            }

            else {
                search_for_User();
            }
        }
    }

    public static void search_for_User() {
        int space;
        int rowcount;
        int index = 1;
        String[] users;
        String selected;

        search = "%" + u_channelName + "%";

        try {
            String search_for_user_query = "select * from myuser";
            String browse_for_watching_query = "select * from myuser, myvideo, mywatchinglist where myuser.u_inum = ? and myuser.u_inum = mywatchinglist.u_inum and mywatchinglist.v_inum = myvideo.v_inum and uadmin_inum = ?";
            String browse_for_upload_query = "select * from myuser, myvideo where u_inum = ? and u_inum = uploader_u_inum and uadmin_inum = ?";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet tmp = stmt.executeQuery(search_for_user_query);
            tmp.last();
            rowcount = tmp.getRow();
            tmp.beforeFirst();

            users = new String[rowcount + 1];
            System.out.println("This is list of users!");
            System.out.print("u_channelName           ");
            System.out.println("Subscribers");
            while(tmp.next()) {
                String channelName = tmp.getString("u_channelName");
                u_inum = tmp.getString("u_inum");
                int sub_count = tmp.getInt("sub_count");

                System.out.print(index + ". " + channelName);

                space = 22 - Integer.toString(index).length() - channelName.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.println(sub_count);
                users[index] = u_inum;
                index++;
            }

            System.out.println(" Which user do you want to browse? Select index!");
            menu = scanner.nextInt();

            selected = users[menu];

            System.out.println("Which list you wanna browse? User's upload list or watching list?");
            System.out.print("1 : Upload list 2 : Watching list. ");
            menu = scanner.nextInt();

            if(menu == 2) {
                pstmt = con.prepareStatement(browse_for_watching_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, selected);
                pstmt.setString(2, admin_num);
                rs = pstmt.executeQuery();
                rs.next();

                showvideoResults(rs, selected, 0);
            }

            else {
                pstmt = con.prepareStatement(browse_for_upload_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, selected);
                pstmt.setString(2, admin_num);
                rs = pstmt.executeQuery();
                rs.next();

                showvideoResults(rs, selected, 2);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public static void user_Mode() {
        while(true) {
            showdefaultMenu("User");
            System.out.println("Select menu.");
            System.out.println(underbars);
            System.out.println("0. Previous Menu");
            System.out.println("1. Create Account");
            System.out.println("2. User Login");
            System.out.println(underbars);

            menu = scanner.nextInt();
            if(menu == 0) {
                System.out.println("Go back to previous menu..");
                return;
            }

            switch (menu){
                case 1:
                    user_Create();
                    break;

                case 2:
                    user_Login();
            }
            // user mode로 영상 업로드 ~
        }
    }

    public static void user_Create() {
        String u_id;
        String u_password;
        while(true) {
            try {
                String user_insert_query = "insert into MyUser (u_inum, u_id, u_password, u_channelName) values (?, ?, ?, ?)";
                String user_inum_check_query = "select u_inum from MyUser where u_inum = ?";

                showdefaultMenu("User");
                showdefaultMenu("Create Account");


                while(true) {
                    u_inum = getrandNum();

                    pstmt = con.prepareStatement(user_inum_check_query);
                    pstmt.setString(1, u_inum);

                    rs = pstmt.executeQuery();
                    if(rs.next()) {
                        System.out.println("same inum : " + u_inum);
                        System.out.println("There already exists same inum! Please re-type!!");
                    }
                    else {
                        break;
                    }
                }

                while(true) {
                    System.out.println("Please type your ID.");
                    System.out.print("ID : ");
                    u_id = scanner.next();

                    if(u_id.length() > 40) {
                        System.out.println("ID's too long! Please re-type!!");
                    }
                    else {
                        break;
                    }
                }

                while(true) {
                    System.out.println("Please type your channel name.");
                    System.out.print("Password : ");
                    u_password = scanner.next();

                    if(u_password.length() > 40) {
                        System.out.println("Password's too long! Please re-type!!");
                    }
                    else {
                        break;
                    }
                }

                while(true) {
                    System.out.println("Please type your channel name.");
                    System.out.print("Channel name : ");
                    u_channelName = scanner.next();

                    if(u_channelName.length() > 40) {
                        System.out.println("ChannelName's too long! Please re-type!!");
                    }
                    else {
                        break;
                    }
                }

                System.out.println("Your id : " + u_id + " channelName : " + u_channelName + " u_id : " + u_id);
                System.out.println("Is it right? 1. YES 2. NO");
                menu = scanner.nextInt();

                if (menu == 1) {
                    try {
                        pstmt = con.prepareStatement(user_insert_query);
                        pstmt.setString(1, u_inum);
                        pstmt.setString(2, u_id);
                        pstmt.setString(3, u_password);
                        pstmt.setString(4, u_channelName);

                        pstmt.executeUpdate();
                        return;
                    } catch (SQLException s) {
                        s.printStackTrace();
                    }
                }

                else {
                    System.out.println("OK! Please re-type your info again!!");
                }
            } catch(SQLException s) {
                s.printStackTrace();
            }
        }
    }

    public static void user_Login() {
        String user_find_query = "select * from MyUser where u_id = ? and u_password = ?";
        try {
            while(true) {
                showdefaultMenu("Log_in");
                System.out.println("Please type your ID and Password.");
                System.out.print("ID : ");
                String u_id = scanner.next();

                System.out.print("Password : ");
                String u_password = scanner.next();

                pstmt = con.prepareStatement(user_find_query);
                pstmt.setString(1, u_id);
                pstmt.setString(2, u_password);

                rs = pstmt.executeQuery();

                if(rs.next()) {
                    System.out.println(rs.getString("u_channelName") + " log_in success!!");
                    user_Actions(rs.getString("u_inum"), rs.getString("u_channelName"));
                    return;
                }

                else {
                    System.out.println("you've got wrong ID or Password! re-type!!");
                }

            }
        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void user_Actions(String u_inum, String u_channelName) {
        try {
            while(true) {
                showdefaultMenu("User");
                System.out.println("Select menu.");
                System.out.println(underbars);
                System.out.println("0. Previous menu");
                System.out.println("1. Upload vidoes");
                System.out.println("2. Watch videos");
                System.out.println("3. About playlist");
                System.out.println("4. About subscribee");
                System.out.println("5. User info");
                System.out.println("6. Administrate videos");
                System.out.println(underbars);
                menu = scanner.nextInt();
                if(menu == 0) {
                    System.out.println("Go back to previous menu..");
                    return;
                }

                switch(menu)  {
                    case 1:
                        upload_Videos(u_inum, u_channelName);
                        break;
                    case 2:
                        watch_Videos(u_inum);
                        break;
                    case 3:
                        user_Playlists(u_inum, u_channelName);
                        break;
                    case 4:
                        show_Subscribees(u_inum, u_channelName);
                        break;
                    case 5:
                        browse_user_Info(u_inum, 1);
                        break;
                    case 6:
                        administrate_user_Videos(u_inum);
                }

            }
        } catch(Exception s) {
            s.printStackTrace();
        }
    }

    public static void administrate_user_Videos(String u_inum) {
        String get_uploaded_videos_query = "select * from myvideo where uploader_u_inum = ?";
        System.out.println("Which opertation do you wanna do? 1. Browse uploaded video 2. Delete Video ");
        try {
            menu = scanner.nextInt();
            pstmt = con.prepareStatement(get_uploaded_videos_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, u_inum);
            ResultSet tmp = pstmt.executeQuery();

            if(menu == 1) {
                showvideoResults(tmp, u_inum, 0);
            }

            else {
                showvideoResults(tmp, u_inum, 2);
            }

        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void browse_user_Info(String u_inum, int mode) {
        try {
            String get_user_info_query = "select * from myuser where u_inum = ?";
            pstmt = con.prepareStatement(get_user_info_query);
            pstmt.setString(1, u_inum);
            rs = pstmt.executeQuery();
            rs.next();

            System.out.println("Channel Name : " + rs.getString("u_channelName"));
            System.out.println("Sub_count : " + rs.getString("sub_count"));

            if(mode == 0) {
                return;
            }

            System.out.println("Which operation do you wanna do? ");
            System.out.println("0. Previous Menu");
            System.out.println("1. View watch_lists");
            System.out.println("2. Get payback for advertisement");
            menu = scanner.nextInt();
            if(menu == 0) {
                System.out.println("Go back to previous menu..");
            }
            else if(menu == 1) {
                String get_watchlist_query = "select * from mywatchinglist, myvideo where u_inum = ? and mywatchinglist.v_inum = myvideo.v_inum";
                pstmt = con.prepareStatement(get_watchlist_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, u_inum);

                ResultSet tmp = pstmt.executeQuery();

                showvideoResults(tmp, u_inum, 0);
            }
            else {
                get_Payback(u_inum);
            }

        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void get_Payback(String u_inum) {
        String get_adcounts_query = "select * from myvideo where uploader_u_inum = ?";
        String set_adcounts_zero_query = "update myvideo set v_adcount = 0 where uploader_u_inum = ?";
        int totaladcounts = 0;

        try {
            pstmt = con.prepareStatement(get_adcounts_query);
            pstmt.setString(1, u_inum);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                int adcount = rs.getInt("v_adcount");
                totaladcounts += adcount;
            }

            pstmt = con.prepareStatement(set_adcounts_zero_query);
            pstmt.setString(1, u_inum);
            pstmt.executeUpdate();

            if(totaladcounts == 0) {
                System.out.println("#FAIL : You don't have any adcounts for payback..");
                return;
            }

            System.out.println("#SUCCESS : Successfully got payback for advertisement!!");
            System.out.println("You've got " + totaladcounts * 1000 + " WON!!");
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
    public static void upload_Videos(String uploader_u_inum, String uploader_channelName) {
        try {
            String video_upload_query = "insert into MyVideo (v_title, v_inum, v_length, v_genre, isAdver, uploader_u_inum, v_uploadDate) values (?, ?, ?, ?, ?, ?, ?)";
            while(true) {
                showdefaultMenu("User");
                showdefaultMenu("upload_Video");
                System.out.println("IF You don't want to Upload, type #EXIT");

                System.out.println("Please type video's title, v_inum, length, genre and whether has advertisement or not.");
                System.out.print("Title : ");
                v_title = scanner.next();
                if(v_title.equals("#EXIT")) {
                    System.out.println("Go back to previous menu..");
                    return;
                }

                v_inum = getrandNum();

                System.out.print("Video Length : ex) xx:xx:xx ");
                v_length = scanner.next();

                System.out.println("Genre : 1. Game 2. Movie 3. Sports 4. News 5. Music 6. Economics 7. Education 8. Etc");
                int index = scanner.nextInt();
                while(index < 1 || index > 8) {
                    System.out.println("You've typed wrong genre! Please re-type! ");
                    System.out.println("Genre : 1. Game 2. Movie 3. Sports 4. News 5. Music 6. Economics 7. Education 8. Etc");
                    index = scanner.nextInt();
                }
                v_genre = genres[index - 1];

                System.out.println("Advertisement? : 1. Y 2. N");
                menu = scanner.nextInt();
                if(menu == 1) {
                    isAdver = "Y";
                }
                else {
                    isAdver = "N";
                }

                System.out.println("You've made choice v_title : " + v_title + " // v_length : " + v_length + " // v_genre : " + v_genre + " // isAdver : " + isAdver);
                System.out.println("Is this right? 1. Y 2. N");
                menu = scanner.nextInt();
                while(!(menu == 1 || menu == 2)) {
                    System.out.println("You've typed wrong menu! Please re-type!");
                    System.out.println("Is this right? 1. Y 2. N");
                }
                if(menu == 1) {
                    pstmt = con.prepareStatement(video_upload_query);
                    pstmt.setString(1, v_title);
                    pstmt.setString(2, v_inum);
                    pstmt.setString(3, v_length);
                    pstmt.setString(4, v_genre);
                    pstmt.setString(5, isAdver);
                    pstmt.setString(6, uploader_u_inum);
                    pstmt.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.now()));

                    pstmt.executeUpdate();
                    System.out.println(uploader_channelName + "'s Video Successfully Uploaded with title :" + v_title);

                    if(isAdver == "Y") {
                        String adv_inums[] = new String[6];
                        String adv_names[] = new String[6];
                        String update_advertisement_query = "update myvideo set adv_inum = ? where v_inum = ?";
                        String insert_inum;
                        String insert_name;
                        int adv_index = 1;
                        String get_advertisements_query = "select * from myadvertisement";

                        stmt = con.createStatement();
                        ResultSet tmp = stmt.executeQuery(get_advertisements_query);
                        System.out.println("This is the list of advertisements that you can advertise!");
                        System.out.println("adv_name  " + "adv_length  " + "adv_owner");

                        while(tmp.next()) {
                            String adv_name = tmp.getString("adv_name");
                            String adv_owner = tmp.getString("adv_owner");
                            int adv_length = tmp.getInt("adv_length");

                            adv_inums[adv_index] = tmp.getString("adv_inum");
                            adv_names[adv_index] = adv_name;

                            System.out.print(adv_name + "     ");
                            System.out.print(adv_length + "          ");
                            System.out.println(adv_owner);
                            adv_index++;
                        }

                        System.out.println("Which advertisement you wanna attach?? Select index!");
                        System.out.print("Index : ");
                        menu = scanner.nextInt();
                        insert_inum = adv_inums[menu];
                        insert_name = adv_names[menu];

                        pstmt = con.prepareStatement(update_advertisement_query);
                        pstmt.setString(1, insert_inum);
                        pstmt.setString(2, v_inum);
                        pstmt.executeUpdate();

                        System.out.println("#SUCCESS : Successfully inserted advertisement " + insert_name);
                    }
                    return;
                }
                else{
                    System.out.println("OK! Please re-type!!");
                }
            }
        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void watch_Videos(String u_inum) {
        while(true) {
            showdefaultMenu("watch_Videos");
            System.out.println("Select menu.");
            System.out.println(underbars);
            System.out.println("0. Previous Menu");
            System.out.println("1. Watch Subscribee's videos");
            System.out.println("2. Search Videos");
            System.out.println("3. Recommended Videos");
            System.out.println("4. Select Genre");
            System.out.println(underbars);

            menu = scanner.nextInt();

            if(menu == 0) {
                System.out.println("Go back to previous menu..");
                return;
            }

            switch (menu) {
                case 1 :
                    watch_Subscribee_Video(u_inum);
                    break;

                case 2 :
                    search_Videos(u_inum);
                    break;

                case 3 :
                    watch_by_Order(u_inum);
                    break;

                case 4 :
                    watch_by_Genre(u_inum);
            }
        }
    }

    public static void watch_by_Genre(String u_inum) {
        showdefaultMenu("watch_by_Genre");

        System.out.println("Which Genre do you want to watch??");
        System.out.println("Genre : 1. Game 2. Movie 3. Sports 4. News 5. Music 6. Economics 7. Education 8. Etc");
        menu = scanner.nextInt();
        String genre = genres[menu - 1];

        String get_by_genre_query = "select * from myvideo where v_genre = ? order by rand() LIMIT 10";

        try {
            pstmt = con.prepareStatement(get_by_genre_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, genre);
            rs = pstmt.executeQuery();

            System.out.println("This is videos for genre : " + genre);
            showvideoResults(rs, u_inum, 1);
        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void watch_by_Order(String u_inum) {
        try {
            showdefaultMenu("watch_by_Order");

            String get_by_likes_query = "select * from myvideo order by v_like desc LIMIT 10";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(get_by_likes_query);
            showvideoResults(rs, u_inum, 1);
        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void watch_Subscribee_Video(String subscriber_inum) {
        String get_subscribee_channelName_query = "select * from myuser, subscribe where subscriber_inum = ? and subscribee_inum = u_inum";
        String get_subscribee_videos_query = "select * from myvideo where uploader_u_inum = ?";
        int index = 1;
        String[] subscribee_inums;
        String[] subscribee_channelNames;
        String subscribee_inum;
        String subscribee_channelName;
        String selected;

        try {
            pstmt = con.prepareStatement(get_subscribee_channelName_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, subscriber_inum);
            rs = pstmt.executeQuery();
            rs.last();
            int rowcount = rs.getRow();
            rs.beforeFirst();

            subscribee_inums = new String[rowcount + 1];
            subscribee_channelNames = new String[rowcount + 1];

            System.out.println("This is the list of your subscribee!");
            while(rs.next()) {
                subscribee_channelName = rs.getString("u_channelName");
                subscribee_inum = rs.getString("subscribee_inum");

                subscribee_channelNames[index] = subscribee_channelName;
                subscribee_inums[index] = subscribee_inum;

                System.out.println(index + " . " + subscribee_channelName);
                index++;
            }

            System.out.print("Which channel do you want to browse? Type index! ");
            menu = scanner.nextInt();
            selected = subscribee_inums[menu];
            pstmt = con.prepareStatement(get_subscribee_videos_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, selected);
            ResultSet tmp = pstmt.executeQuery();
            showvideoResults(tmp, subscriber_inum, 1);
        } catch (SQLException s) {
            s.printStackTrace();
        }
    }
    public static void showvideoInfos() {
        System.out.println(underbars);
        System.out.print("v_title           "); // 18
        System.out.print("v_length   "); // 11
        System.out.print("v_count  "); // 9
        System.out.print("v_like   "); // 9
        System.out.print("v_uploadDate    "); // 16
        System.out.print("v_genre    "); // 11
        System.out.print("isAdver   "); // 7
        System.out.println("ChannelName"); // 11
        System.out.println(underbars);
    }

    public static void showvideoResults(ResultSet rs, String u_inum, int mode) {
        String getChannel_query = "select u_channelName from myuser, myvideo where u_inum = ?";

        int index = 1;
        int pageindex = 1;
        int space;
        int rowcount;
        int page;
        int row;
        String[][] v_inums;
        boolean pageflag = true;
        boolean videoflag = true;
        try {
            rs.last();
            rowcount = rs.getRow();
            rs.beforeFirst();

            if(rowcount == 0) {
                System.out.println("There is no video to show!!");
                videoflag = false;
            }
            v_inums = new String[(rowcount / 10) + 2][11];
            while(rs.next()) {
                if(pageflag) {
                    System.out.println(underbars);
                    if(mode == 3) {
                        System.out.println("Channel Name : " + rs.getString("u_channelName"));
                        System.out.println(underbars);
                    }

                    System.out.println("Current Page : " + pageindex);
                    showvideoInfos();
                    pageflag = false;
                }

                v_inums[pageindex][index] = rs.getString("v_inum");
                System.out.print(index + " . " + rs.getString("v_title"));
                v_title = rs.getString("v_title");
                space = 12 - v_title.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("v_length"));
                v_length = rs.getString("v_length");
                space = 9 - v_length.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getInt("v_count"));
                v_count = Integer.toString(rs.getInt("v_count"));
                space = 7 - v_count.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getInt("v_like"));
                v_like = Integer.toString(rs.getInt("v_like"));
                space = 7 - v_like.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getDate("v_uploadDate"));
                v_uploadDate = rs.getDate("v_uploadDate");
                for(int i = 0; i < 4; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("v_genre"));
                v_genre = rs.getString("v_genre");
                space = 9 - v_genre.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("isAdver"));
                System.out.print("    ");

                pstmt = con.prepareStatement(getChannel_query);
                pstmt.setString(1, rs.getString("uploader_u_inum"));

                ResultSet tmp = pstmt.executeQuery();
                tmp.next();
                System.out.println("     " + tmp.getString("u_channelName"));

                index++;
                if(index == 10) {
                    index = 1;
                    pageindex++;
                    pageflag = true;
                }
            }

            System.out.println(underbars);

            if(mode == 2) {
                System.out.print("Is there any video you want to DELETE?? 1. Y 2 .N ");
                menu = scanner.nextInt();
                if(menu == 2) {
                    return;
                }

                else {
                    String get_deleted_v_title_query = "select * from myvideo where v_inum = ?";
                    String delete_user_video_query = "delete from myvideo where v_inum = ?";

                    System.out.println("OK! Which video you want to DELETE? Type page and Row.");
                    System.out.print("Page : ");
                    page = scanner.nextInt();

                    System.out.print("Row : ");
                    row = scanner.nextInt();

                    String delete_v_inum = v_inums[page][row];

                    pstmt = con.prepareStatement(get_deleted_v_title_query);
                    pstmt.setString(1, delete_v_inum);
                    ResultSet tmp = pstmt.executeQuery();
                    tmp.next();
                    String deleted_v_title = tmp.getString("v_title");

                    pstmt = con.prepareStatement(delete_user_video_query);
                    pstmt.setString(1, delete_v_inum);
                    pstmt.executeUpdate();

                    System.out.println("#SUCCESS : Successfully deleted " + deleted_v_title);
                    return;
                }
            }

            if(mode == 0) {
                return;
            }

            if(videoflag) {
                System.out.println("Which video You want to watch?? Select Page and Row. Or if you want to exit, just type 0!");
                System.out.print("Page : ");
                page = scanner.nextInt();
                if(page == 0) {
                    return;
                }
                System.out.print("Row : ");
                row = scanner.nextInt();

                v_inum = v_inums[page][row];

                watch_Video(v_inum, u_inum);
            }

        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void watch_Video(String v_inum, String u_inum) {
        try {
            String count_plus_query = "update myvideo set v_count = v_count + 1 where v_inum = ?";
            String get_videoinfo_query = "select * from myvideo where v_inum = ?";
            String add_watchlist_query = "insert into Mywatchinglist(u_inum, v_inum, viewtime) values (?, ?, ?) on duplicate key update viewtime = ?";
            String choose_playlist_query = "select * from myuser, myplaylist where u_inum = ? and owner_u_inum = u_inum";
            String add_playlist_query = "insert ignore into AddtoPlaylist values (?, ?)";
            String tmphour;
            String tmpminute;
            String uploader_u_inum;
            int total = 0;

            pstmt = con.prepareStatement(get_videoinfo_query);
            pstmt.setString(1, v_inum);
            rs = pstmt.executeQuery();
            rs.next();

            isAdver = rs.getString("isAdver");
            if(isAdver.equals("Y")) {
                String adcount_plus_query = "update myvideo set v_adcount = v_adcount + 1 where v_inum = ?";
                String get_advertisement_query = "select * from myvideo, myadvertisement where v_inum = ? and myvideo.adv_inum = myadvertisement.adv_inum";

                pstmt = con.prepareStatement(get_advertisement_query);
                pstmt.setString(1, v_inum);
                ResultSet tmp = pstmt.executeQuery();
                tmp.next();
                String adv_name = tmp.getString("adv_name");
                int adv_length = tmp.getInt("adv_length");

                System.out.println("Watching : " + adv_name + "length : " + adv_length + " seconds.");
                System.out.print("[");
                for(int i = 0; i < adv_length; i++) {
                    System.out.print("@");
                }
                System.out.println("]");
                System.out.println("Watching advertisement complete!");

                pstmt = con.prepareStatement(adcount_plus_query);
                pstmt.setString(1, v_inum);
                pstmt.executeUpdate();
            }
            v_title = rs.getString("v_title");
            v_length = rs.getString("v_length");
            uploader_u_inum = rs.getString("uploader_u_inum");
            String[]tmp = v_length.split(":");
            tmphour = tmp[0];
            tmpminute = tmp[1];

            int hour = Integer.parseInt(tmphour);
            int minute = Integer.parseInt(tmpminute);

            total += hour * 60 + minute;
            System.out.println("Watching : " + v_title + "video length : " + v_length);
            System.out.print("[");
            for(int i = 0; i < total; i++) {
                System.out.print("#");
            }
            System.out.println("]");
            System.out.println("Watching video complete!!");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            pstmt = con.prepareStatement(add_watchlist_query);

            pstmt.setString(1, u_inum);
            pstmt.setString(2, v_inum);
            pstmt.setTimestamp(3, timestamp);
            pstmt.setTimestamp(4, timestamp);
            pstmt.executeUpdate();

            pstmt = con.prepareStatement(count_plus_query);
            pstmt.setString(1, v_inum);
            pstmt.executeUpdate();

            System.out.println("Have you enjoyed this video? Why don't you click like OR subscribe this channel OR add to your playlist?");
            System.out.print("Press Like? 1 : Y 2 : N ");
            int like = scanner.nextInt();

            if(like == 1) {
                String like_count_plus_query = "update myvideo set v_like = v_like + 1 where v_inum = ?";
                pstmt = con.prepareStatement(like_count_plus_query);
                pstmt.setString(1, v_inum);

                pstmt.executeUpdate();
                System.out.println("#SUCCESS : You Like this Video! : " + v_title);
            }

            System.out.println("Press subscribe? 1 : Y 2 : N");
            int subcribe = scanner.nextInt();

            if(subcribe == 1) {
                String check_for_duplicate_subscribe = "select * from subscribe where subscriber_inum = ? and subscribee_inum = ?";
                pstmt = con.prepareStatement(check_for_duplicate_subscribe);
                pstmt.setString(1, u_inum);
                pstmt.setString(2, uploader_u_inum);

                ResultSet tmp2 = pstmt.executeQuery();
                if(tmp2.next() == true) {
                    System.out.println("#FAIL : You already subscribed!");
                }

                else {
                    String subscribe_query = "insert into subscribe values (?, ?)";
                    String update_sub_count_query = "update myuser set sub_count = sub_count + 1 where u_inum = ?";
                    pstmt = con.prepareStatement(subscribe_query);
                    pstmt.setString(1, u_inum);
                    pstmt.setString(2, uploader_u_inum);

                    pstmt.executeUpdate();

                    pstmt = con.prepareStatement(update_sub_count_query);
                    pstmt.setString(1, uploader_u_inum);

                    pstmt.executeUpdate();
                    System.out.println("#SUCCESS : Subscribe complete!");
                }
            }

            System.out.println("Add to your playlist? 1 : Y 2 : N");
            int addtoplayist = scanner.nextInt();
            if(addtoplayist == 1) {
                pstmt = con.prepareStatement(choose_playlist_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, u_inum);

                rs = pstmt.executeQuery();
                System.out.println("OK! I'll show your playlists!");
                System.out.println("     Playlist Name");

                String[] p_inums;
                String[] p_names;
                rs.last();
                int rowcount = rs.getRow();
                rs.beforeFirst();

                p_inums = new String[rowcount + 1];
                p_names = new String[rowcount + 1];
                int index = 1;
                while(rs.next()) {
                    String p_name = rs.getString("p_name");
                    String p_inum = rs.getString("p_inum");
                    System.out.println(index + " . " + p_name);
                    p_inums[index] = p_inum;
                    p_names[index] = p_name;
                    index++;
                }

                System.out.println("Which playlist do you want to add??");
                System.out.print("Playlist index :");
                int playlist = scanner.nextInt();

                pstmt = con.prepareStatement(add_playlist_query);
                pstmt.setString(1, p_inums[playlist]);
                pstmt.setString(2, v_inum);

                pstmt.executeUpdate();
                System.out.println("#SUCCESS : You've added " + v_title + " to playlist : " + p_names[playlist]);
            }
        } catch(SQLException s) {
            s.printStackTrace();
        }

    }

    public static void search_Videos(String u_inum) {

        while(true) {
            showdefaultMenu("search_Videos");
            System.out.println("Select menu.");
            System.out.println(underbars);
            System.out.println("0. Previous Menu");
            System.out.println("1. Search by Video_Title");
            System.out.println("2. Search by Channel_Name");
            System.out.println(underbars);

            menu = scanner.nextInt();
            if(menu == 0) {
                System.out.println("Go back to previous menu..");
                return;
            }

            switch (menu) {
                case 1 :
                    try {
                        String search_with_title_query = "select * from myvideo where v_title like ?";

                        System.out.println("Please type title of the video.");
                        System.out.print("Title : ");
                        v_title = scanner.next();
                        search = "%" + v_title + "%";

                        pstmt = con.prepareStatement(search_with_title_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        pstmt.setString(1, search);

                        rs = pstmt.executeQuery();
                        if(rs.next() == false) {
                            System.out.println("#ERROR : There is no video named " + v_title);
                            break;
                        }
                        else {
                            System.out.println("#SUCCESS : This is the result from search by video title.");
                            rs.previous();
                            showvideoResults(rs, u_inum, 1);
                        }

                        break;
                    } catch(SQLException s) {
                        s.printStackTrace();
                    }
                    break;

                case 2 :
                    try {
                        String search_with_channelName_query = "select * from myvideo, myuser where u_channelName like ? and u_inum = uploader_u_inum";
                        pstmt = con.prepareStatement(search_with_channelName_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        System.out.println("Please type Name of the channel.");
                        u_channelName = scanner.next();

                        search = "%" + u_channelName + "%";
                        pstmt.setString(1, search);

                        rs = pstmt.executeQuery();
                        if(rs.next() == false) {
                            System.out.println(underbars);
                            System.out.println("#ERROR : There is no channel named " + u_channelName);
                            break;
                        }
                        else {
                            System.out.println(underbars);
                            System.out.println("#SUCCESS : This is the result from search by channel Name.");
                            rs.previous();
                            showvideoResults(rs, u_inum, 3);
                        }
                    } catch (SQLException s) {
                        s.printStackTrace();
                    }

            }
        }
    }

    public static void user_Playlists(String u_inum, String u_channelName) {
        System.out.println("user_Playlists");
        System.out.println("0. Previous Menu");
        System.out.println("1. Create my playlist");
        System.out.println("2. Browse my playlist");
        System.out.println(underbars);

        menu = scanner.nextInt();
        if(menu == 0) {
            System.out.println("Go back to previous menu..");
            return;
        }

        switch (menu) {
            case 1 :
                create_Playlist(u_inum, u_channelName);
                break;

            case 2 :
                browse_Playlist(u_inum, u_channelName);
        }
    }

    public static void create_Playlist(String owner_u_inum, String u_channelName) {
        String p_inum;
        String p_name;
        try {
            showdefaultMenu("create_Playlist");
            p_inum = getrandNum();

            System.out.println("Please type your Playlist Name!");
            System.out.print("PlayList name : ");
            p_name = scanner.next();

            String create_playlist_query = "insert into MyPlaylist values(?, ?, ?)";

            pstmt = con.prepareStatement(create_playlist_query);
            pstmt.setString(1, p_inum);
            pstmt.setString(2, owner_u_inum);
            pstmt.setString(3, p_name);
            pstmt.executeUpdate();

            System.out.println("Congratulations " + u_channelName + " !" + "You've made a playlist named " + p_name + " !!");
        } catch(SQLException s) {
            s.printStackTrace();
        }

    }

    public static void browse_Playlist(String owner_u_inum, String u_channelName) {
        try {
            int index = 1;
            int rowcount;
            String p_inums[];
            String p_names[];
            String p_inum;
            String p_selected_name;
            String get_u_playlist_query = "select * from myplaylist where owner_u_inum = ?";
            String get_playlist_videos_query = "select * from addtoplaylist, myvideo where p_inum = ? and addtoplaylist.v_inum = myvideo.v_inum";
            String get_videocount_query = "select count(*) from addtoplaylist where p_inum = ?"; // p_inum 가져와서 count.
            showdefaultMenu("browse_Playlist");
            System.out.println("This is channel " + u_channelName + "'s" + " Playlists!!");
            System.out.println(underbars);

            pstmt = con.prepareStatement(get_u_playlist_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, owner_u_inum);
            rs = pstmt.executeQuery();

            rs.last();
            rowcount = rs.getRow();
            rs.beforeFirst();

            p_inums = new String[rowcount + 1];
            p_names = new String[rowcount + 1];

            System.out.print("    Playlist Name");
            System.out.print("         ");
            System.out.println("Video Count");
            while(rs.next()) {
                String p_name = rs.getString("p_name");
                p_inums[index] = rs.getString("p_inum");
                p_names[index] = rs.getString("p_name");
                pstmt = con.prepareStatement(get_videocount_query);
                pstmt.setString(1, rs.getString("p_inum"));
                int videocount = 0;
                ResultSet tmp = pstmt.executeQuery();
                if(tmp.next()) {
                    videocount = tmp.getInt(1);
                }
                System.out.print(index + " . " + p_name);

                int space = 22 - p_name.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }
                System.out.println(videocount);
                index++;
            }

            System.out.print("Do you want to browse any of your playlist?? 1. Y 2. N ");
            menu = scanner.nextInt();

            if(menu == 1) {
                System.out.println("OK! Which playlist do you wanna browse?? Type the index!");
                System.out.print("Index : ");
                menu = scanner.nextInt();
                p_inum = p_inums[menu];

                pstmt = con.prepareStatement(get_playlist_videos_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setString(1, p_inum);
                rs = pstmt.executeQuery();

                showvideoResults(rs, owner_u_inum , 0);
            }
            System.out.print("Do you want to modify any of your playlist?? 1. Y 2. N ");
            menu = scanner.nextInt();

            if(menu == 2) {
                return;
            }

            System.out.println("OK! Which playlist do you wanna modify?? Type the index!");
            System.out.print("Index : ");
            menu = scanner.nextInt();
            p_inum = p_inums[menu];
            p_selected_name = p_names[menu];

            System.out.println("You've chose " + p_selected_name);
            System.out.println("Which operations do you want to do? 0. Previous Menu 1. Delete video from playlist 2. Delete playlist");
            System.out.print("Type menu : ");
            menu = scanner.nextInt();

            switch (menu) {
                case 0:
                    return;

                case 1:
                    delete_from_Playlist(p_inum, u_channelName);
                    break;

                case 2:
                    delete_Playlist(p_inum, p_selected_name);
            }


        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static void delete_Playlist(String p_inum, String p_selected_name) {
        try {
            String delete_playlist_query = "delete from myplaylist where p_inum = ?";
            pstmt = con.prepareStatement(delete_playlist_query);
            pstmt.setString(1, p_inum);
            pstmt.executeUpdate();

        } catch(SQLException s) {
            s.printStackTrace();
        }

        System.out.println("#SUCCESS : Successfully deleted " + p_selected_name);
    }

    public static void delete_from_Playlist(String p_inum, String u_channelName) {
        String show_videos_query = "select * from addtoplaylist, myvideo where p_inum = ? and myvideo.v_inum = addtoplaylist.v_inum";
        String delete_video_query = "delete from addtoplaylist where v_inum = ?";
        int rowcount;
        int pageindex = 1;
        int index = 1;
        int page;
        int orderindex;
        String[][] v_inums;
        String[][] v_titles;

        int space;
        boolean pageflag = true;

        try {
            showdefaultMenu("delete_from_Playlist");
            pstmt = con.prepareStatement(show_videos_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, p_inum);
            rs = pstmt.executeQuery();

            rs.last();
            rowcount = rs.getRow();
            rs.beforeFirst();

            v_inums = new String[(rowcount / 10) + 2][11];
            v_titles = new String[(rowcount / 10) + 2][11];

            while(rs.next()) {
                if(pageflag) {
                    System.out.println(underbars);
                    System.out.println("Current Page : " + pageindex);
                    showvideoInfos();
                    pageflag = false;
                }

                v_inums[pageindex][index] = rs.getString("v_inum");
                v_titles[pageindex][index] = rs.getString("v_title");

                v_inums[pageindex][index] = rs.getString("v_inum");
                System.out.print(index + " . " + rs.getString("v_title"));
                v_title = rs.getString("v_title");
                space = 12 - v_title.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("v_length"));
                v_length = rs.getString("v_length");
                space = 9 - v_length.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getInt("v_count"));
                v_count = Integer.toString(rs.getInt("v_count"));
                space = 7 - v_count.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getInt("v_like"));
                v_like = Integer.toString(rs.getInt("v_like"));
                space = 7 - v_like.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getDate("v_uploadDate"));
                v_uploadDate = rs.getDate("v_uploadDate");
                for(int i = 0; i < 4; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("v_genre"));
                v_genre = rs.getString("v_genre");
                space = 9 - v_genre.length();
                for(int i = 0; i < space; i++) {
                    System.out.print(" ");
                }

                System.out.print("  " + rs.getString("isAdver"));
                System.out.print("    ");

                System.out.println("     " + u_channelName);
                index++;

                if(index == 10) {
                    pageindex++;
                    index = 1;
                    pageflag = true;
                }
            }

            System.out.println("Which video do you want to delete? Choose page index and order index!");
            System.out.print("Page : ");
            page = scanner.nextInt();
            System.out.print("Orderindex : ");
            orderindex = scanner.nextInt();

            v_inum = v_inums[page][orderindex];
            pstmt = con.prepareStatement(delete_video_query);
            pstmt.setString(1, v_inum);
            pstmt.executeUpdate();

            System.out.println("#SUCCESS : " + v_titles[page][orderindex] + "is successfully deleted!!");
        } catch(SQLException s) {
            s.printStackTrace();
        }

    }

    public static void show_Subscribees(String u_inum, String u_channelName) {
        String show_subscribees_query = "select * from subscribe, myuser where subscriber_inum = ? and u_inum = subscribee_inum";
        String delete_subscribees_query = "delete from subscribe where subscriber_inum = ? and subscribee_inum = ?";
        String subscribee_channelName;
        String delete_subscribee;
        int index = 1;
        String[] subscribees;
        String[] channelNames;
        showdefaultMenu("show_Subscribees");
        try {
            pstmt = con.prepareStatement(show_subscribees_query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, u_inum);
            rs = pstmt.executeQuery();

            rs.last();
            int rowcount = rs.getRow();
            rs.beforeFirst();

            subscribees = new String[rowcount + 1];
            channelNames = new String[rowcount + 1];
            System.out.println("This is subscribees of " + u_channelName);

            while(rs.next()) {
                subscribee_channelName = rs.getString("u_channelName");
                System.out.println(index + " . " + subscribee_channelName);
                subscribees[index] = rs.getString("subscribee_inum");
                channelNames[index] = rs.getString("u_channelName");
                index++;
            }

            System.out.println("Any subscribee to de_subscribe? 1 : Y 2 : N");
            menu = scanner.nextInt();
            if(menu == 1) {
                System.out.print("OK! Type index to de_subscribe! ");
                menu = scanner.nextInt();
                delete_subscribee = subscribees[menu];
                subscribee_channelName = channelNames[menu];
                pstmt = con.prepareStatement(delete_subscribees_query);
                pstmt.setString(1, u_inum);
                pstmt.setString(2, delete_subscribee);
                pstmt.executeUpdate();

                System.out.println("#SUCCESS : Successfully de_subscribed " + subscribee_channelName);
            }
        } catch(SQLException s) {
            s.printStackTrace();
        }
    }

    public static String getrandNum() {
        Random random = new Random();
        int createNum = 0;
        String ranNum = "";
        int size = 10;
        String resultNum = "";

        for(int i = 0; i < size; i++) {
            createNum = random.nextInt(9);
            ranNum = Integer.toString(createNum);
            resultNum += ranNum;
        }

        return resultNum;
    }
}

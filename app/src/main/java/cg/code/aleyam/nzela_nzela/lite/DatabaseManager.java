package cg.code.aleyam.nzela_nzela.lite;

import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cg.code.aleyam.nzela_nzela.transaction.connexion.UserObject;


public class DatabaseManager extends SQLiteOpenHelper{
    private Context context = null;
    private static final String DATABASE_NAME = "Nzela_Nzela_base.db";
    private static final int VERSION = 1;
    private int user_id = 0;
    private static DatabaseManager db_manager = null;

    public static DatabaseManager getInstance(Context context) {
        if(db_manager == null) {
            db_manager = new DatabaseManager(context);
        }
        return db_manager;
    }



    private DatabaseManager(Context context) {

        super(context , DATABASE_NAME , null , VERSION);
        this.context = context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //on cree les tables qu'il faut pour le bon fonctionnement de l'application

        String create_user_table = "CREATE TABLE user_table(\n" +
                "        id_user           integer  primary key autoincrement ,\n" +
                "        nom_user          Text NOT NULL,\n" +
                "        prenom_user       Text NOT NULL,\n" +
                "        tel_user          Text NOT NULL,\n" +
                "        tel_proch_user    Text NOT NULL,\n" +
                "        sexe_user    Text NOT NULL,\n" +
                "        localisation_user Text NOT NULL\n)";
        db.execSQL(create_user_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //rien pour l'instant.


    }

    public void setName(String name) {
        String query = null;
        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set nom_user = '"
                        +name+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");
            this.getWritableDatabase().execSQL(query);
        }
    }

    public void setLastName(String Lname) {
        String query = null;
        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set prenom_user = '"
                        +Lname+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");
            this.getWritableDatabase().execSQL(query);
        }
    }

    public void setPhone(String phone) {
        String query = null;
        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set tel_user = '"
                        +phone+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");
            this.getWritableDatabase().execSQL(query);
        }
    }

    public void setTelProch(String telProch) {
        String query = null;
        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set tel_proch_user = '"
                        +telProch+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");
            this.getWritableDatabase().execSQL(query);
        }
    }

    public void setAdress(String adress) {
        String query = null;
        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set localisation_user = '"
                        +adress+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");
            this.getWritableDatabase().execSQL(query);
        }
    }

    public void setUserInfo(String[] userInfo) {
        String query = null;

        if(UserExist()) {
            if(user_id != 0) {
                query = "update user_table set nom_user = '"
                        +userInfo[0]+"' , prenom_user = '"
                        +userInfo[1]+"' , tel_user = '"
                        +userInfo[2]+"' , tel_proch_user = '"
                        +userInfo[3]+"' , sexe_user = '"
                        +userInfo[4]+"' , localisation_user = '"
                        +userInfo[5]+"'  where id_user = "+this.user_id+"" ;
            } else throw new NullPointerException("user_id est = 0 ");

        } else {
            query = "insert into user_table(nom_user , prenom_user , tel_user , tel_proch_user , sexe_user , localisation_user ) values('"
                    +userInfo[0]+"' , '"
                    +userInfo[1]+"' , '"
                    +userInfo[2]+"' , '"
                    +userInfo[3]+"' , '"
                    +userInfo[4]+"' , '"
                    +userInfo[5]+"')";

        }
        this.getWritableDatabase().execSQL(query);

    }
    private boolean UserExist() {
        String searchUser = "select id_user from user_table";
        Cursor mouse = this.getReadableDatabase().rawQuery(searchUser , null);
        if(mouse.moveToFirst()) {
            //donc si on a un resultat
            user_id = mouse.getInt(0);
            return true;
        }
        return false;
    }
    public String[] getCurrentUser() {
        String requete = "select * from user_table ";
        Cursor souris = this.getReadableDatabase().rawQuery(requete , null);
        if(souris.moveToFirst()) {
            return new String[]{souris.getString(1) ,
                    souris.getString(2) ,
                    souris.getString(3) ,
                    souris.getString(4) ,
                    souris.getString(5) ,
                    souris.getString(6)
            };
        } else {
            return null;
        }
    }
    public void closeDB() {
        if(db_manager != null) {
            SQLiteDatabase sqLiteDatabase = db_manager.getWritableDatabase();
            if(sqLiteDatabase.isOpen()) {
                sqLiteDatabase.close();
                db_manager = null;
            }
        }
    }
}







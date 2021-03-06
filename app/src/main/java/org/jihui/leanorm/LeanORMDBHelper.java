package org.jihui.leanorm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import org.jihui.leanorm.util.IOUtils;
import org.jihui.leanorm.util.Log;
import org.jihui.leanorm.util.NaturalOrderComparator;
import org.jihui.leanorm.util.SqlParser;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zjh on 15-4-25.
 */
public class LeanORMDBHelper extends SQLiteOpenHelper {

    private final String mSqlParser;
    public final static String MIGRATION_PATH = "migrations";

    public LeanORMDBHelper() {
        //version不需要从外部传入
        super(Configuration.context, (String) Configuration.getMetaData(Configuration.LEAN_DB_NAME), null, (Integer) Configuration.getMetaData(Configuration.LEAN_DB_VERSION));
        final String mode = Configuration.getMetaData(Configuration.LEAN_SQL_PARSER);
        if (mode == null) {
            mSqlParser = Configuration.DEFAULT_SQL_PARSER;
        } else {
            mSqlParser = mode;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTable(db);
        executeMigrations(db, oldVersion, newVersion);
    }

    public void dropTable(SQLiteDatabase db) {
        try {
            String[] classeNames = ((String) Configuration.getMetaData(Configuration.LEAN_MODELS)).split(",");
            for (String name : classeNames) {
                Class<? extends LeanORMModel> resource = null;
                resource = (Class<? extends LeanORMModel>) Class.forName(name);
                String dropsql = String.format("DROP TABLE IF EXISTS %s", resource.getSimpleName().toLowerCase());
                android.util.Log.d("LEAN_ORM", dropsql);
                db.execSQL(dropsql);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createTable(SQLiteDatabase db) {
        try {
            String[] classeNames = ((String) Configuration.getMetaData(Configuration.LEAN_MODELS)).split(",");
            for (String name : classeNames) {
                Class<? extends LeanORMModel> resource = (Class<? extends LeanORMModel>) Class.forName(name);
                List<String> statements = new ArrayList<String>();
                for (Field field : resource.getFields()) {
                    String fieldName = field.getName();
                    if (fieldName.toLowerCase().equals("_id")) {
                        continue;
                    }
                    android.util.Log.d("LEAN_ORM", "field name -> " + fieldName);
                    Class<?> fieldType = field.getType();
                    android.util.Log.d("LEAN_ORM", "field type name -> " + fieldType);
                    statements.add(String.format("%s %s", fieldName, TypeClassMapping.typeClassMapping.get(fieldType)));
                }
                //default use _id as the primary key for specific table
                String createsql = String.format("CREATE TABLE IF NOT EXISTS %s (_id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, %s)", resource.getSimpleName().toLowerCase(), StringUtils.join(statements, ", "));
                android.util.Log.d("LEAN_ORM", createsql);
                db.execSQL(createsql);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean executeMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.d("LEAN_ORM", "start migration");
        boolean migrationExecuted = false;
        try {
            final List<String> files = Arrays.asList(Configuration.context.getAssets().list(MIGRATION_PATH));
            Collections.sort(files, new NaturalOrderComparator());

            db.beginTransaction();
            try {
                for (String file : files) {
                    try {
                        final int version = Integer.valueOf(file.replace(".sql", ""));

                        if (version > oldVersion && version <= newVersion) {
                            executeSqlScript(db, file);
                            migrationExecuted = true;

                            Log.i(file + " executed succesfully.");
                        }
                    } catch (NumberFormatException e) {
                        Log.w("Skipping invalidly named file: " + file, e);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException e) {
            Log.e("Failed to execute migrations.", e);
        }

        return migrationExecuted;
    }

    private void executeSqlScript(SQLiteDatabase db, String file) {
        InputStream stream = null;
        try {
            stream = Configuration.context.getAssets().open(MIGRATION_PATH + "/" + file);

            if (Configuration.SQL_PARSER_DELIMITED.equalsIgnoreCase(mSqlParser)) {
                executeDelimitedSqlScript(db, stream);

            } else {
                executeLegacySqlScript(db, stream);
            }

        } catch (IOException e) {
            Log.e("Failed to execute " + file, e);

        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private void executeDelimitedSqlScript(SQLiteDatabase db, InputStream stream) throws IOException {
        List<String> commands = SqlParser.parse(stream);
        for (String command : commands) {
            db.execSQL(command);
        }
    }

    private void executeLegacySqlScript(SQLiteDatabase db, InputStream stream) throws IOException {
        InputStreamReader reader = null;
        BufferedReader buffer = null;

        try {
            reader = new InputStreamReader(stream);
            buffer = new BufferedReader(reader);
            String line = null;

            while ((line = buffer.readLine()) != null) {
                line = line.replace(";", "").trim();
                if (!TextUtils.isEmpty(line)) {
                    db.execSQL(line);
                }
            }
        } finally {
            IOUtils.closeQuietly(buffer);
            IOUtils.closeQuietly(reader);
        }
    }
}

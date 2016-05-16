package com.example.umyhpuscdi.snapthat;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by umyhpuscdi on 2016-05-10.
 */
public abstract class Serializer {
    public static byte[] serialize(Object obj) throws IOException {

        /*
            Gson alternative to serialization

            Gson gson = new Gson();
            String jsonString = gson.toJson(playerData);
            return jsonString.getBytes();
        */

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();

    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {

        /*
            Gson alternative

            Gson alternative to serialization
            Gson gson = new Gson();
            return gson.fromJson(new String(data), PlayerData.class);
        */

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();

    }
}

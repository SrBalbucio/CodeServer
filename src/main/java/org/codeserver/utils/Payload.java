package org.codeserver.utils;

import lombok.AllArgsConstructor;
import org.codeserver.model.User;
import org.json.JSONObject;

import java.util.Map;

@AllArgsConstructor
public class Payload implements Map.Entry<User, JSONObject>{

    private User user;
    private JSONObject jsonObject;

    @Override
    public User getKey() {
        return user;
    }

    @Override
    public JSONObject getValue() {
        return jsonObject;
    }

    @Override
    public JSONObject setValue(JSONObject value) {
        return null;
    }
}

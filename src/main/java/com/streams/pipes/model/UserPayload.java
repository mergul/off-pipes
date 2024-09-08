package com.streams.pipes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

@JsonDeserialize(builder = UserPayload.Builder.class)
public class UserPayload {
    public String id;
    public List<String> tags;
    public List<String> users;
    public Integer index;
    public String random;
    private Boolean isAdmin;
    private List<String> offers;

    public UserPayload(){}

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserPayload(String id, List<String> tags, List<String> users, Integer index, String random, Boolean isAdmin, List<String> offers) {
        this.id = id;
        this.tags = tags;
        this.users = users;
        this.index = index;
        this.random = random;
        this.isAdmin = isAdmin;
        this.offers = offers;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getUsers() {
        return users;
    }

    public Integer getIndex() {
        return index;
    }

    public String getRandom() {
        return random;
    }

    public String getId() {
        return id;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public List<String> getOffers() {
        return offers;
    }

    @Override
    public String toString() {
        return "UserPayload{" +
                "id='" + id + '\'' +
                ", tags=" + tags +
                ", users=" + users +
                ", index=" + index +
                ", random=" + random +
                ", isAdmin=" + isAdmin +
                ", offers=" + offers +
                '}';
    }

    public static Builder of() {
        return new Builder();
    }

    public static Builder of(String id) {
        return new Builder(id);
    }

    public static Builder from(UserPayload userPayload) {
        final Builder builder = new Builder();
        builder.id = userPayload.id;
        builder.index = userPayload.index;
        builder.tags = userPayload.tags;
        builder.users = userPayload.users;
        builder.random = userPayload.random;
        builder.isAdmin = userPayload.isAdmin;
        builder.offers = userPayload.offers;
        return builder;
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static final class Builder {

        public String id;
        public List<String> tags;
        public List<String> users;
        public Integer index;
        public String random;
        public Boolean isAdmin;
        public List<String> offers;

        public Builder() {
        }

        public Builder(String id) {
            this.id = id;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        public Builder withRandom(String random) {
            this.random = random;
            return this;
        }

        public Builder withIsAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withUsers(List<String> users) {
            this.users = users;
            return this;
        }

        public Builder withOffers(List<String> offers) {
            this.offers = offers;
            return this;
        }

        public UserPayload build() {
            return new UserPayload(id, tags, users, index, random, isAdmin, offers);
        }
    }
}

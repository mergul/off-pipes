package com.streams.pipes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;
import java.util.List;

@JsonDeserialize(builder = OfferPayload.Builder.class)
public class OfferPayload {
    @JsonSerialize(using = ToStringSerializer.class)
    private final ObjectId id;
    private final String ownerId;
    private final String newsId;
    private final String newsOwnerId;
    private final double price;
    private final List<String> tags;
    private final String topic;
    private final String thumb;
    private final Date startDate;
    private final Date endDate;
    private final Boolean active;
    private final Long count;
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OfferPayload(ObjectId id, String ownerId, String newsId, String newsOwnerId, double price, List<String> tags, String topic, String thumb, Date startDate, Date endDate, Boolean active, Long count){
        this.id=id;
        this.ownerId =ownerId;
        this.newsId = newsId;
        this.newsOwnerId = newsOwnerId;
        this.price=price;
        this.tags = tags;
        this.topic = topic;
        this.thumb = thumb;
        this.startDate=startDate;
        this.endDate=endDate;
        this.active=active;
        this.count = count;
    }

    public ObjectId getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }
    public String getNewsId() {
        return newsId;
    }
    public String getNewsOwnerId() {
        return newsOwnerId;
    }
    public double getPrice() {
        return price;
    }
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public List<String> getTags() {
        return tags;
    }
    public String getTopic() {
        return topic;
    }
    public String getThumb() {
        return thumb;
    }
    public Boolean getActive() {
        return active;
    }
    public Long getCount() {
        return count;
    }
    @Override
    public String toString() {
        return "OfferPayload{" +
                "id=" + id +
                ", ownerId='" + ownerId + '\'' +
                ", newsId='" + newsId + '\'' +
                ", newsOwnerId='" + newsOwnerId + '\'' +
                ", price='" + price + '\'' +
                ", tags=" + tags +
                ", topic='" + topic + '\'' +
                ", thumb='" + thumb + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", active='" + active + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
    public static Builder of() {
        return new Builder();
    }

    public static Builder of(ObjectId id) {
        return new Builder(id);
    }
    public static Builder from(OfferPayload offerPayload) {
        final Builder builder= new Builder();
        builder.id=offerPayload.id;
        builder.ownerId=offerPayload.ownerId;
        builder.newsId= offerPayload.newsId;
        builder.newsOwnerId= offerPayload.newsOwnerId;
        builder.thumb= offerPayload.thumb;
        builder.topic= offerPayload.topic;
        builder.price=offerPayload.price;
        builder.tags=offerPayload.tags;
        builder.startDate=offerPayload.startDate;
        builder.endDate=offerPayload.endDate;
        builder.active=offerPayload.active;
        builder.count=offerPayload.count;
        return builder;
    }
    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static final class Builder {
        private ObjectId id;
        private String ownerId;
        private String newsId;
        private String newsOwnerId;
        private double price;
        private List<String> tags;
        private String topic;
        private String thumb;
        private Date startDate;
        private Date endDate;
        private Boolean active;
        private Long count;
        public Builder() {
        }

        public Builder(ObjectId id) {
            this.id = id;
        }
        public Builder withId(ObjectId id) {
            this.id = id;
            return this;
        }
        public Builder withOwnerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }
        public Builder withNewsId(String newsId) {
            this.newsId = newsId;
            return this;
        }
        public Builder withNewsOwnerId(String newsOwnerId) {
            this.newsOwnerId = newsOwnerId;
            return this;
        }
        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }
        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }
        public Builder withTopic(String topic) {
            this.topic = topic;
            return this;
        }
        public Builder withThumb(String thumb) {
            this.thumb = thumb;
            return this;
        }
        public Builder withStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }
        public Builder withEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }
        public Builder withActive(Boolean active) {
            this.active = active;
            return this;
        }
        public Builder withCount(Long count) {
            this.count = count;
            return this;
        }
        public OfferPayload build(){
            return new OfferPayload(id, ownerId, newsId, newsOwnerId, price, tags, topic, thumb, startDate, endDate, active, count);
        }
    }
}

package de.gamekuchen.utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class RecUserRecord {
    public final int accountId;
    public final String profileImage;
    public final String displayName;
    public final String createdAt;
    public final String bannerImage;
    public final int identityFlags;
    public final Boolean isJunior;
    public final int personalPronouns;
    public final int platforms;
    public final String username;

    public RecUserRecord(@JsonProperty("accountID") int accountId,
                         @JsonProperty("profileImage") String profileImage,
                         @JsonProperty("displayName") String displayName,
                         @JsonProperty("createdAt") String createdAt,
                         @JsonProperty("bannerImage") String bannerImage,
                         @JsonProperty("identityFlags") int identityFlags,
                         @JsonProperty("isJunior") Boolean isJunior,
                         @JsonProperty("personalPronouns") int personalPronouns,
                         @JsonProperty("platforms") int platforms,
                         @JsonProperty("username") String username
    ) {
        this.accountId = accountId;
        this.profileImage = profileImage;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.bannerImage = bannerImage;
        this.identityFlags = identityFlags;
        this.isJunior = isJunior;
        this.personalPronouns = personalPronouns;
        this.platforms = platforms;
        this.username = username;
    }
}



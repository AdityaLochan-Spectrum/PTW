package com.example.PTW.Model;

import java.util.*;

public class RoleMappings {

    public static final Set<String> INITIATORS = Set.of(
        "8766425964", "9597161175", "9003757937", "8610559975",
        "9791518470", "8248101191", "9361565802", "6380379124",
        "9598290866", "8520857594", "8870577653", "9655011606"
    );

    public static final Map<String, String> ISSUER_MAP = Map.ofEntries(
        Map.entry("8766425964", "9908442275"),
        Map.entry("9597161175", "8766425964"),
        Map.entry("9003757937", "8766425964"),
        Map.entry("8610559975", "8766425964"),
        Map.entry("9791518470", "8766425964"),
        Map.entry("8248101191", "8766425964"),
        Map.entry("9361565802", "8766425964"),
        Map.entry("6380379124", "8766425964"),
        Map.entry("9598290866", "8766425964"),
        Map.entry("8520857594", "8766425964"),
        Map.entry("8870577653", "8766425964"),
        Map.entry("9655011606", "8766425964")
    );

    public static final String DEFAULT_REVIEWER = "7777880288";

    // Optional: Get Role
    public static String getRole(String mobileNumber) {
        if (INITIATORS.contains(mobileNumber)) return "Initiator";
        if (ISSUER_MAP.containsValue(mobileNumber)) return "Issuer";
        if (DEFAULT_REVIEWER.equals(mobileNumber)) return "Reviewer";
        return "Unknown";
    }

    // Get Issuer for a given Initiator
    public static String getIssuerForInitiator(String initiator) {
        return ISSUER_MAP.getOrDefault(initiator, "UNKNOWN_ISSUER");
    }

    // Get Reviewer for any Initiator (all share same reviewer)
    public static String getReviewerForInitiator(String initiator) {
        return DEFAULT_REVIEWER;
    }
}

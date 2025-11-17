package vn.iwork4se.service;

import java.util.List;

public interface AIDataRetrievalService {
    /**
     * Retrieve relevant job posts and employers based on user query
     * @param userMessage User's message/query
     * @return Formatted string containing relevant data to inject into AI prompt
     */
    String retrieveRelevantData(String userMessage);
}


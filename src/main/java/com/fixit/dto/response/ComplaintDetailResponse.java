package com.fixit.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ComplaintDetailResponse extends ComplaintResponse {

    private List<ComplaintUpdateResponse> updates = new ArrayList<>();
    private FeedbackResponse feedback;

    public ComplaintDetailResponse() {
    }

    public List<ComplaintUpdateResponse> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ComplaintUpdateResponse> updates) {
        this.updates = updates;
    }

    public FeedbackResponse getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackResponse feedback) {
        this.feedback = feedback;
    }
}
